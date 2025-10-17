package dev.kgriffon.simplegame.entity;

import com.esotericsoftware.minlog.Log;
import dev.kgriffon.simplegame.Shared;

import java.awt.Color;
import java.util.Collection;

public class Projectile {

    private static final float SPEED = 600;

    private final int playerId;
    private float x;
    private float y;
    private float dx;
    private float dy;
    private final Color color;
    private boolean loaded;

    public Projectile(int playerId, float x, float y, float dx, float dy, Color color) {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.color = color;
        this.loaded = true;
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
        if (x < 0 || x > Shared.WIDTH || y < 0 || y > Shared.HEIGHT) loaded = false;
//        if (x < 0 || x > Shared.WIDTH) {
//            dx = -dx;
//        }
//        if (y < 0 || y > Shared.HEIGHT) {
//            dy = -dy;
//        }
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
}
