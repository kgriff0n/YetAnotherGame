package dev.kgriffon.simplegame.entity;

import dev.kgriffon.simplegame.Shared;

import java.awt.*;

public class Player {

    private final int id;
    private final String username;
    private int health;
    private final Color color;
    private float x;
    private float y;

    public Player(int id, String username, Color color, float x, float y) {
        this.id = id;
        this.username = username;
        this.health = 3;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public Color getColor() {
        return color;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        if (x < 0) x = 0;
        else if (x > Shared.WIDTH) x = Shared.WIDTH;
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        if (y < 0) y = 0;
        else if (y > Shared.HEIGHT) y = Shared.HEIGHT;
        this.y = y;
    }
}
