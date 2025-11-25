package dev.kgriffon.simplegame.network.packet.c2s;

public class LoginRequest {
    private String username;
    private String face;

    public LoginRequest() {
    }

    public LoginRequest(String username, String face) {
        this.username = username;
        this.face = face;
    }

    public String getUsername() {
        return username;
    }

    public String getFace() {
        return face;
    }
}
