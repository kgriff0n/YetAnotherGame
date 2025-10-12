package dev.kgriffon.simplegame.network.packet.s2c;

public class PlayerPosition {

    private int id;
    private float x;
    private float y;

    public PlayerPosition() {
    }

    public PlayerPosition(int id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
