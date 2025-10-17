package dev.kgriffon.simplegame.network.packet.c2s;

public class PlayerMove {

    private float x;
    private float y;

    public PlayerMove() {
    }

    public PlayerMove(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
