package dev.kgriffon.simplegame.network.packet.s2c;

import dev.kgriffon.simplegame.entity.Player;

import java.awt.*;

public class NewPlayer {
    private int id;
    private String username;
    private int health;
    private int rgb;
    private float x;
    private float y;

    public NewPlayer() {
    }

    public NewPlayer(Player player) {
        this.id = player.getId();
        this.username = player.getUsername();
        this.health = player.getHealth();
        this.rgb = player.getColor().getRGB();
        this.x = player.getX();
        this.y = player.getY();
    }

    public NewPlayer(int id, String username, int health, Color color, float x, float y) {
        this.id = id;
        this.username = username;
        this.health = health;
        this.rgb = color.getRGB();
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getRGB() {
        return rgb;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
