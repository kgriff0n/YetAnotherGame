package dev.kgriffon.simplegame.network.packet.c2s;

public class LoginRequest {
    private String username;

    public LoginRequest() {
    }

    public LoginRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
