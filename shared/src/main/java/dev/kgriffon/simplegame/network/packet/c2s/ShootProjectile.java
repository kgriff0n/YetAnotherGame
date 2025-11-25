package dev.kgriffon.simplegame.network.packet.c2s;

public class ShootProjectile {

    private float dx;
    private float dy;

    public ShootProjectile() {
    }

    public ShootProjectile(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public float getDx() {
        return dx;
    }

    public float getDy() {
        return dy;
    }
}
