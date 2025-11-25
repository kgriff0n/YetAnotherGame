package dev.kgriffon.simplegame.network.packet.s2c;

public class RemoveProjectile {

    private int id;

    public RemoveProjectile() {}

    public RemoveProjectile(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
