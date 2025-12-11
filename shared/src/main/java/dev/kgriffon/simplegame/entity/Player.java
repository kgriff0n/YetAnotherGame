package dev.kgriffon.simplegame.entity;

import dev.kgriffon.simplegame.Shared;

import java.awt.*;

public class Player extends Entity {

    private final int id;
    private final String username;
    private final String face;
    private int health;
    private final Color color;
    private float x;
    private float y;

    public Player(int id, String username, String face, Color color, float x, float y) {
        super(32, 32);
        this.id = id;
        this.username = username;
        this.face = face;
        this.health = 3;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public Player(int id, String username, String face, Color color, float x, float y, int health) {
        super(32, 32);
        this.id = id;
        this.username = username;
        this.face = face;
        this.health = health;
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

    public String getFace() {
        return face;
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
//        if (x < 0) x = 0;
//        else if (x > Shared.WIDTH) x = Shared.WIDTH;
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
//        if (y < 0) y = 0;
//        else if (y > Shared.HEIGHT) y = Shared.HEIGHT;
        this.y = y;
    }
}
