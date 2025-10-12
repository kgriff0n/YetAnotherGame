package dev.kgriffon.simplegame.network.packet.s2c;


public class LoginResponse {
    private int id;
    private int rgb;
    private float x;
    private float y;

    public LoginResponse() {
    }

    public LoginResponse(int id, int rgb, float x, float y) {
        this.id = id;
        this.rgb = rgb;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
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
