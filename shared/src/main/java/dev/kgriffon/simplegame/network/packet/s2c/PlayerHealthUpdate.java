package dev.kgriffon.simplegame.network.packet.s2c;

public class PlayerHealthUpdate {

    private int id;
    private int health;

    public PlayerHealthUpdate() {
    }

    public PlayerHealthUpdate(int id, int health) {
        this.id = id;
        this.health = health;
    }

    public int getId() {
        return id;
    }

    public int getHealth() {
        return health;
    }
}
