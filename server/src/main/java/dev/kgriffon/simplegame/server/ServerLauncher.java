package dev.kgriffon.simplegame.server;

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
import dev.kgriffon.simplegame.network.packet.c2s.SuicidePacket;
import dev.kgriffon.simplegame.network.packet.s2c.*;
import dev.kgriffon.simplegame.score.ScoreManager;
import dev.kgriffon.simplegame.util.ColorUtil;

import java.awt.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.*;

/** Launches the server application. */
public class ServerLauncher {
    private final Server server;
    private final ConcurrentHashMap<Integer, Player> players = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<Projectile> projectiles = new ConcurrentLinkedQueue<>();
    private final ScoreManager scoreManager;

//    private long bytesSent = 0;
//    private long bytesReceived = 0;

    public ServerLauncher() throws IOException {
        server = new Server();
        server.start();
        server.bind(Network.TCP_PORT, Network.UDP_PORT);

        Network.register(server);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::tick, 0, 50, TimeUnit.MILLISECONDS);

        // Init
        scoreManager = new ScoreManager();

        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {

            }

            @Override
            public void received(Connection connection, Object packet) {
//                bytesReceived += estimatePacketSize(packet);
                if (packet instanceof LoginRequest pkt) {
                    // Player initialization
                    int id = connection.getID();
                    Color color = ColorUtil.randomColor();
                    float x = (float) (Math.random() * Shared.WIDTH);
                    float y = (float) (Math.random() * Shared.HEIGHT);
                    Player currentPlayer = new Player(id, pkt.getUsername(), pkt.getFace(), color, x, y);
                    server.sendToTCP(id, new LoginResponse(id, color.getRGB(), x, y));
//                    bytesSent += estimatePacketSize(new LoginResponse(id, color.getRGB(), x, y));

                    Log.info("[SERVER] Send LoginResponse to " + connection.getID());

                    for (Player player : players.values()) {
                        connection.sendTCP(new NewPlayer(player));
//                        bytesSent += estimatePacketSize(new NewPlayer(player));
                    }

                    int[] ids = new int[projectiles.size()];
                    int[] playerId = new int[projectiles.size()];
                    float[] pX = new float[projectiles.size()];
                    float[] pY = new float[projectiles.size()];
                    float[] dx = new float[projectiles.size()];
                    float[] dy = new float[projectiles.size()];
                    int[] rgb = new int[projectiles.size()];

                    int i = 0;
                    for (Projectile projectile : projectiles) {
                        ids[i] = projectile.getId();
                        playerId[i] = projectile.getPlayerId();
                        pX[i] = projectile.getX();
                        pY[i] = projectile.getY();
                        dx[i] = projectile.getDx();
                        dy[i] = projectile.getDy();
                        rgb[i] = projectile.getColor().getRGB();
                        i++;
                    }
                    connection.sendTCP(new ProjectilesBatch(ids, playerId, pX, pY, dx, dy, rgb));
//                    bytesSent += estimatePacketSize(new ProjectilesBatch(playerId, pX, pY, dx, dy, rgb));

                    server.sendToAllExceptTCP(connection.getID(), new NewPlayer(currentPlayer));
//                    bytesSent += (long) estimatePacketSize(new NewPlayer(currentPlayer)) * (server.getConnections().size() - 1);
                    players.put(id, currentPlayer);
                    scoreManager.add(id, pkt.getUsername());
                    server.sendToAllTCP(scoreManager.createPacket());

                } else if (packet instanceof PlayerMove pkt) {
                    Player player = players.get(connection.getID());
                    if (player != null) {
                        //TODO check for move validity
                        player.setX(pkt.getX()); // update context
                        player.setY(pkt.getY());
                        server.sendToAllUDP(new PlayerPosition(connection.getID(), pkt.getX(), pkt.getY()));
//                        bytesSent += (long) estimatePacketSize(new PlayerPosition(connection.getID(), pkt.getX(), pkt.getY())) * server.getConnections().size();
                    }
                } else if (packet instanceof ShootProjectile pkt) {
                    Player player = players.get(connection.getID());
                    if (player != null) {
                        //TODO check for projectile validity
                        Projectile projectile = new Projectile(connection.getID(), player.getX(), player.getY(), pkt.getDx(), pkt.getDy(), player.getColor());
                        projectiles.add(projectile);
                        server.sendToAllTCP(new NewProjectile(projectile.getId(), player.getId(), player.getX(), player.getY(), pkt.getDx(), pkt.getDy(), player.getColor().getRGB()));
//                        bytesSent += (long) estimatePacketSize(new NewProjectile(pkt.getX(), pkt.getY(), pkt.getDx(), pkt.getDy(), player.getColor().getRGB())) * (server.getConnections().size() - 1);
                    }
                } else if (packet instanceof SuicidePacket) {
                    Player player = players.get(connection.getID());
                    float x = (float) (Math.random() * Shared.WIDTH);
                    float y = (float) (Math.random() * Shared.HEIGHT);
                    player.setX(x);
                    player.setY(y);
                    connection.sendTCP(new PlayerPosition(connection.getID(), x, y));
                    server.sendToAllExceptUDP(connection.getID(), new PlayerPosition(connection.getID(), x, y));
                }
            }

            @Override
            public void disconnected(Connection connection) {
//                System.out.println("[SERVER] Sent: " + bytesSent + " bytes | Received: " + bytesReceived + " bytes");
                int playerId = connection.getID();
                players.remove(playerId);
                scoreManager.remove(playerId);
                server.sendToAllExceptTCP(playerId, new PlayerRemove(playerId));
                server.sendToAllExceptTCP(playerId, scoreManager.createPacket());
            }
        });

        Log.info("[SERVER] Running on " + Network.TCP_PORT);
    }

    private void tick() {
        float delta = 0.05f; // 50ms
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.update(delta);
            Player player = projectile.collide(players.values());
            if (player != null) {
                projectile.setLoaded(false);
                server.sendToAllTCP(new RemoveProjectile(projectile.getId())); // remove projectile
                player.setHealth(player.getHealth() - 1); // update context
                if (player.getHealth() == 0) { // respawn
                    player.setHealth(3);
                    player.setX((float) (Math.random() * Shared.WIDTH));
                    player.setY((float) (Math.random() * Shared.HEIGHT));
                    server.sendToAllTCP(new PlayerPosition(player.getId(), player.getX(), player.getY()));

                    // Update scoreboard
                    scoreManager.update(projectile.getPlayerId(), scoreManager.getScore(projectile.getPlayerId()) + 1);
                    server.sendToAllTCP(scoreManager.createPacket());
                }
                server.sendToAllTCP(new PlayerHealthUpdate(player.getId(), player.getHealth()));
            }
            if (!projectile.isLoaded()) iterator.remove();
        }

        //TODO maybe re-send projectiles positions sometimes to resync everything
    }

//    private int estimatePacketSize(Object obj) {
//        Output output = new Output(1024, -1);
//        Network.KRYO.writeClassAndObject(output, obj);
//        return output.position();
//    }

    public static void main(String[] args) throws IOException {
        new ServerLauncher();
    }
}
