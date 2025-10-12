package dev.kgriffon.simplegame.network.packet.c2s;

public class PlayerMove {

    private int id;
    private float x;
    private float y;

    public PlayerMove() {
    }

    public PlayerMove(int id, float x, float y) {
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
