package dev.kgriffon.simplegame.network.packet.s2c;

public class NewProjectile {

    private float x;
    private float y;
    private float dx;
    private float dy;
    private int rgb;

    public NewProjectile() {
    }

    public NewProjectile(float x, float y, float dx, float dy, int rgb) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.rgb = rgb;
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

    public int getRGB() {
        return rgb;
    }
}
