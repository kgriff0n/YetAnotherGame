package dev.kgriffon.simplegame.server;

import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import dev.kgriffon.simplegame.Shared;
import dev.kgriffon.simplegame.entity.Player;
import dev.kgriffon.simplegame.entity.Projectile;
import dev.kgriffon.simplegame.network.Network;
import dev.kgriffon.simplegame.network.packet.c2s.LoginRequest;
import dev.kgriffon.simplegame.network.packet.c2s.PlayerMove;
import dev.kgriffon.simplegame.network.packet.c2s.ShootProjectile;
import dev.kgriffon.simplegame.network.packet.s2c.*;
import dev.kgriffon.simplegame.util.ColorUtil;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** Launches the server application. */
public class ServerLauncher {
    private final Server server;
    private final ConcurrentHashMap<Integer, Player> players = new ConcurrentHashMap<>();
    private final ArrayList<Projectile> projectiles = new ArrayList<>(); //TODO check for ConcurrentModificationException

    private long bytesSent = 0;
    private long bytesReceived = 0;

    public ServerLauncher() throws IOException {
        server = new Server();
        server.start();
        server.bind(Network.TCP_PORT, Network.UDP_PORT);

        Network.register(server);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::tick, 0, 50, TimeUnit.MILLISECONDS);


        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {

            }

            @Override
            public void received(Connection connection, Object packet) {
                bytesReceived += estimatePacketSize(packet);
                if (packet instanceof LoginRequest pkt) {
                    int id = connection.getID();
                    Color color = ColorUtil.randomColor();
                    float x = (float) (Math.random() * 100);
                    float y = (float) (Math.random() * 100);
                    Player currentPlayer = new Player(id, pkt.getUsername(), color, x, y);
                    server.sendToTCP(id, new LoginResponse(id, color.getRGB(), x, y));
                    bytesSent += estimatePacketSize(new LoginResponse(id, color.getRGB(), x, y));

                    Log.info("[SERVER] Send LoginResponse to " + connection.getID());

                    for (Player player : players.values()) {
                        connection.sendTCP(new NewPlayer(player));
                        bytesSent += estimatePacketSize(new NewPlayer(player));
                    }

                    int[] playerId = new int[projectiles.size()];
                    float[] pX = new float[projectiles.size()];
                    float[] pY = new float[projectiles.size()];
                    float[] dx = new float[projectiles.size()];
                    float[] dy = new float[projectiles.size()];
                    int[] rgb = new int[projectiles.size()];
                    for (int i = 0; i < projectiles.size(); i++) {
                        Projectile projectile = projectiles.get(i);
                        playerId[i] = projectile.getPlayerId();
                        pX[i] = projectile.getX();
                        pY[i] = projectile.getY();
                        dx[i] = projectile.getDx();
                        dy[i] = projectile.getDy();
                        rgb[i] = projectile.getColor().getRGB();
                    }
                    connection.sendTCP(new ProjectilesBatch(playerId, pX, pY, dx, dy, rgb));
                    bytesSent += estimatePacketSize(new ProjectilesBatch(playerId, pX, pY, dx, dy, rgb));

                    server.sendToAllExceptTCP(connection.getID(), new NewPlayer(currentPlayer));
                    bytesSent += (long) estimatePacketSize(new NewPlayer(currentPlayer)) * (server.getConnections().size() - 1);
                    players.put(id, currentPlayer);

                } else if (packet instanceof PlayerMove pkt) {
                    Player player = players.get(pkt.getId()); //FIXME id is useless in pkt, should use connection.getId() instead
                    if (player != null) {
                        //TODO check for move validity
                        player.setX(pkt.getX()); // update context
                        player.setY(pkt.getY());
                        server.sendToAllUDP(new PlayerPosition(pkt.getId(), pkt.getX(), pkt.getY()));
                        bytesSent += (long) estimatePacketSize(new PlayerPosition(pkt.getId(), pkt.getX(), pkt.getY())) * server.getConnections().size();
                    }
                } else if (packet instanceof ShootProjectile pkt) {
                    Player player = players.get(connection.getID());
                    if (player != null) {
                        //TODO check for projectile validity
                        projectiles.add(new Projectile(connection.getID(), pkt.getX(), pkt.getY(), pkt.getDx(), pkt.getDy(), player.getColor()));
                        server.sendToAllExceptTCP(connection.getID(), new NewProjectile(pkt.getX(), pkt.getY(), pkt.getDx(), pkt.getDy(), player.getColor().getRGB()));
                        bytesSent += (long) estimatePacketSize(new NewProjectile(pkt.getX(), pkt.getY(), pkt.getDx(), pkt.getDy(), player.getColor().getRGB())) * (server.getConnections().size() - 1);
                    }
                }
            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("[SERVER] Sent: " + bytesSent + " bytes | Received: " + bytesReceived + " bytes");
                int playerId = connection.getID();
                players.remove(playerId);
                server.sendToAllExceptTCP(playerId, new PlayerRemove(playerId));
            }
        });

        Log.info("[SERVER] Running on " + Network.TCP_PORT);
    }

    private void tick() {
        float delta = 0.05f; // 50ms
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile p = iterator.next();
            p.update(delta);
            Player player = p.collide(players.values());
            if (player != null) {
                player.setHealth(player.getHealth() - 1); // update context
                if (player.getHealth() == 0) { // respawn
                    player.setHealth(3);
                    player.setX((float) (Math.random() * Shared.WIDTH));
                    player.setY((float) (Math.random() * Shared.HEIGHT));
                    server.sendToAllTCP(new PlayerPosition(player.getId(), player.getX(), player.getY()));
                }
                server.sendToAllTCP(new PlayerHealthUpdate(player.getId(), player.getHealth()));
            }
            if (!p.isLoaded()) iterator.remove();
        }

        //TODO maybe re-send projectiles positions sometimes to resync everything
    }

    private int estimatePacketSize(Object obj) {
        Output output = new Output(1024, -1);
        Network.KRYO.writeClassAndObject(output, obj);
        return output.position();
    }

    public static void main(String[] args) throws IOException {
        new ServerLauncher();
    }
}
