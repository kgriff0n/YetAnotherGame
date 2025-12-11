package dev.kgriffon.simplegame.entity;

import java.awt.Color;
import java.util.Collection;

public class Projectile extends Entity {

    private static final float SPEED = 600;
    private static int ID = 0;

    private final int id;
    private final int playerId;
    private float x;
    private float y;
    private float dx;
    private float dy;
    private final Color color;
    private boolean loaded;
    private float time;

    /** Should only be used by server (to generate new ID) */
    public Projectile(int playerId, float x, float y, float dx, float dy, Color color) {
        this(ID++, playerId, x, y, dx, dy, color);
    }

    /** Should only be used by client */
    public Projectile(int id, int playerId, float x, float y, float dx, float dy, Color color) {
        super(16, 16);
        this.id = id;
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.color = color;
        this.loaded = true;
        this.time = 5f;
    }

    public int getId() {
        return id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getDx() {
        return dx;
    }

    public float getDy() {
        return dy;
    }

    public Color getColor() {
        return this.color;
    }

    public void update(float delta) {
        x += SPEED * dx * delta;
        y += SPEED * dy * delta;
        time -= delta;
        if (time <= 0) loaded = false;
//        if (x < 0 || x > Shared.WIDTH || y < 0 || y > Shared.HEIGHT) loaded = false;
//        if (x < 0 || x > Shared.WIDTH) {
//            dx = -dx;
//        }
//        if (y < 0 || y > Shared.HEIGHT) {
//            dy = -dy;
//        }
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public Player collide(Collection<Player> players) {
        for (Player player : players) {
            if (Math.abs(player.getX() - getX()) < 20 && Math.abs(player.getY() - getY()) < 20 && player.getId() != getPlayerId()) {
//                Log.info(String.format("%s %d / %d", player.getUsername(), player.getId(), getPlayerId()));
                return player;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Projectile[id=%d,playerId=%d,dx=%f,dy=%f]".formatted(id, playerId, dx, dy);
    }
}
