package dev.kgriffon.simplegame.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import dev.kgriffon.simplegame.MainGame;
import dev.kgriffon.simplegame.Shared;
import dev.kgriffon.simplegame.entity.Player;
import dev.kgriffon.simplegame.entity.Projectile;
import dev.kgriffon.simplegame.network.Network;
import dev.kgriffon.simplegame.network.packet.c2s.LoginRequest;
import dev.kgriffon.simplegame.network.packet.c2s.PlayerMove;
import dev.kgriffon.simplegame.network.packet.c2s.ShootProjectile;
import dev.kgriffon.simplegame.network.packet.s2c.*;
import dev.kgriffon.simplegame.score.ScoreEntry;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameScreen implements Screen {
    private final String username;
    private final String ip;
    private final String face;

    private BitmapFont font;
    private SpriteBatch batch;
    private Texture projectileTexture;
    private Texture playerTexture;
    private Map<String, Texture> playerFaces;

    private Client client;
    private Player player;
    private float speed = 200;

    private final ConcurrentHashMap<Integer, Player> players = new ConcurrentHashMap<>();
    private final ArrayList<Projectile> projectiles = new ArrayList<>();
    private final ArrayList<ScoreEntry> scoreboard = new ArrayList<>();

    public GameScreen(String username, String ip, String face) {
        this.username = username;
        this.ip = ip;
        this.face = face;
        playerFaces = new HashMap<>();
    }

    @Override
    public void show() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/DejaVuSans.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters =  FreeTypeFontGenerator.DEFAULT_CHARS + "♥★♠♦♣éèàùç";
        parameter.size = 16;
        font = generator.generateFont(parameter);
        generator.dispose();
        batch = new SpriteBatch();
        projectileTexture = new Texture("texture/entity/bullet.png");
        playerTexture = new Texture("texture/entity/player.png");
        connectToServer();
    }

    private void connectToServer() {
        client = new Client();
        client.start();
        Network.register(client);

        client.addListener(new Listener() {

            @Override
            public void connected(Connection connection) {
                client.sendTCP(new LoginRequest(username, face));
            }

            @Override
            public void received(Connection connection, Object packet) {
                if (packet instanceof LoginResponse pkt) {
                    player = new Player(pkt.getId(), username, face, new Color(pkt.getRGB()), pkt.getX(), pkt.getY());
                    players.put(pkt.getId(), player);
                    Log.info("[SERVER] Connected");
                } else if (packet instanceof NewPlayer pkt) {
                    Log.info("[SERVER] New player: " + pkt.getUsername());
                    players.put(pkt.getId(), new Player(pkt.getId(), pkt.getUsername(), pkt.getFace(), new Color(pkt.getRGB()), pkt.getX(), pkt.getY(), pkt.getHealth()));
                } else if (packet instanceof PlayerPosition pkt) {
                    Player other = players.get(pkt.getId());
                    if (other != null) {
                        other.setX(pkt.getX());
                        other.setY(pkt.getY());
                    }
                } else if (packet instanceof NewProjectile pkt) {
                    projectiles.add(new Projectile(pkt.getId(), pkt.getPlayerId(), pkt.getX(), pkt.getY(), pkt.getDx(), pkt.getDy(), new Color(pkt.getRGB())));
                } else if (packet instanceof PlayerHealthUpdate pkt) {
                    Player other = players.get(pkt.getId());
                    if (other != null) {
                        other.setHealth(pkt.getHealth());
                    }
                } else if (packet instanceof ProjectilesBatch pkt) {
                    for (int i = 0; i < pkt.getPlayerId().length; i++) {
                        projectiles.add(new Projectile(
                            pkt.getId()[i],
                            pkt.getPlayerId()[i],
                            pkt.getX()[i],
                            pkt.getY()[i],
                            pkt.getDx()[i],
                            pkt.getDy()[i],
                            new Color(pkt.getRgb()[i])
                        ));
                    }
                } else if (packet instanceof PlayerRemove pkt) {
                    players.remove(pkt.getPlayerId());
                } else if (packet instanceof Scoreboard pkt) {
                    scoreboard.clear();

                    int[] ids = pkt.getIds();
                    String[] usernames = pkt.getUsernames();
                    int[] scores = pkt.getScores();

                    for (int i = 0; i < ids.length; i++) {
                        scoreboard.add(new ScoreEntry(ids[i], usernames[i], scores[i]));
                    }
                } else if (packet instanceof RemoveProjectile pkt) {
                    for (Projectile p : projectiles) {
                        if (p.getId() == pkt.getId()) {
                            p.setLoaded(false);
                        }
                    }
                }
            }
        });

        try {
            client.connect(5000, ip, Network.TCP_PORT, Network.UDP_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(float delta) {
        // Inputs
        input(delta);

        // Update
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile p = iterator.next();
            p.update(delta);
            if (!p.isLoaded()) iterator.remove();
        }

        // Clear screen
        ScreenUtils.clear(1, 1, 1, 1);

        // Render
        batch.begin();
        font.setColor(0,0,0,1);
        GlyphLayout layout = new GlyphLayout();
        for (Player player : players.values()) {
            // Player
            batch.setColor(player.getColor().getRed() / 255f, player.getColor().getGreen() / 255f, player.getColor().getBlue() / 255f, 1);
            batch.draw(playerTexture,
                player.getX() - player.getHitboxWidth() / 2,
                player.getY() - player.getHitboxHeight() / 2,
                player.getHitboxWidth(),
                player.getHitboxHeight());
            batch.setColor(1,1,1, 1);

            // Face
            Texture face = playerFaces.get(player.getFace());
            if (face == null) {
                face = new Texture("texture/entity/face/%s.png".formatted(player.getFace()));
                playerFaces.put(player.getFace(), face);
            }
            batch.draw(face,
                player.getX() - player.getHitboxWidth() / 2,
                player.getY() - player.getHitboxHeight() / 2,
                player.getHitboxWidth(),
                player.getHitboxHeight());
            // Username
            layout.setText(font, player.getUsername());
            font.draw(batch, player.getUsername(), player.getX() - layout.width / 2, player.getY() + 45);
            // Health
            String health = "♥".repeat(Math.max(0, player.getHealth()));
            layout.setText(font, health);
            font.draw(batch, health, player.getX() - layout.width / 2, player.getY() + 30);
        }

        // Bullets
        for (Projectile projectile : projectiles) {
            Color color = projectile.getColor();
            batch.setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1);
            batch.draw(projectileTexture,
                projectile.getX() - projectile.getHitboxWidth() / 2,
                projectile.getY() - projectile.getHitboxHeight() / 2,
                projectile.getHitboxWidth(),
                projectile.getHitboxHeight());
        }

        // Scoreboard
        int lineY = 10;
        for (ScoreEntry score : scoreboard) {
            Color color = players.get(score.getId()).getColor();
            font.setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
            String line = "%s - %d".formatted(score.getUsername(), score.getScore());
            layout.setText(font, line);
            font.draw(batch, line, Shared.WIDTH - layout.width - 10, Shared.HEIGHT - lineY);
            lineY += (int) (layout.height + 10);
        }

        // Cursor
//        float mouseX = Gdx.input.getX();
//        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
//        batch.draw(projectileTexture, mouseX, mouseY);

        batch.end();
    }

    private void input(float delta) {
        if (player != null) {
            boolean move = false;
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                player.setX(player.getX() - speed * delta);
                move = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                player.setX(player.getX() + speed * delta);
                move = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                player.setY(player.getY() + speed * delta);
                move = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                player.setY(player.getY() - speed * delta);
                move = true;
            }
            if (move) {
                PlayerMove pkt = new PlayerMove(player.getX(), player.getY());
                client.sendUDP(pkt);
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                float dx = Gdx.input.getX() - player.getX();
                float dy = Gdx.graphics.getHeight() - Gdx.input.getY() - player.getY();
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
//                projectiles.add(new Projectile(
//                    player.getId(),
//                    player.getX(),
//                    player.getY(),
//                    dx / distance,
//                    dy / distance,
//                    player.getColor()
//                ));
                ShootProjectile pkt = new ShootProjectile(dx / distance, dy / distance);
                client.sendTCP(pkt);
            }
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        Log.info("Closing game...");

        font.dispose();
        batch.dispose();
        projectileTexture.dispose();
        playerTexture.dispose();
        playerFaces.values().forEach(Texture::dispose);

        if (client != null) {
            client.close();
            client.stop();
        }
    }
}

