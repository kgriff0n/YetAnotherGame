package dev.kgriffon.simplegame.network.packet.s2c;

public class ProjectilesBatch { //FIXME crash if too many projectiles

    private int[] id;
    private int[] playerId;
    private float[] x;
    private float[] y;
    private float[] dx;
    private float[] dy;
    private int[] rgb;

    public ProjectilesBatch() {
    }

    public ProjectilesBatch(int[] id, int[] playerId, float[] x, float[] y, float[] dx, float[] dy, int[] rgb) {
        this.id = id;
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.rgb = rgb;
    }

    public int[] getId() {
        return id;
    }

    public int[] getPlayerId() {
        return playerId;
    }

    public float[] getX() {
        return x;
    }

    public float[] getY() {
        return y;
    }

    public float[] getDx() {
        return dx;
    }

    public float[] getDy() {
        return dy;
    }

    public int[] getRgb() {
        return rgb;
    }
}
