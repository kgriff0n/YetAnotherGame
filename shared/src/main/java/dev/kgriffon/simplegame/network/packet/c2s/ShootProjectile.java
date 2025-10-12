package dev.kgriffon.simplegame.network.packet.c2s;

public class ShootProjectile {

    private float x;
    private float y;
    private float dx;
    private float dy;

    public ShootProjectile() {
    }

    public ShootProjectile(float x, float y, float dx, float dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
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
}
