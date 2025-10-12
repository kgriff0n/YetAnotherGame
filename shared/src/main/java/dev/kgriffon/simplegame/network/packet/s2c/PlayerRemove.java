package dev.kgriffon.simplegame.network.packet.s2c;

public class PlayerRemove {

    private int playerId;

    public PlayerRemove() {
    }

    public PlayerRemove(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }
}
