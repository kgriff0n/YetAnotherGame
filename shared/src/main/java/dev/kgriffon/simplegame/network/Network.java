package dev.kgriffon.simplegame.network;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import dev.kgriffon.simplegame.network.packet.c2s.LoginRequest;
import dev.kgriffon.simplegame.network.packet.c2s.PlayerMove;
import dev.kgriffon.simplegame.network.packet.c2s.ShootProjectile;
import dev.kgriffon.simplegame.network.packet.s2c.*;

public class Network {
    public static Kryo KRYO;
    public static final int TCP_PORT = 54555;
    public static final int UDP_PORT = 54777;

    public static void register(EndPoint endpoint) {
        KRYO = endpoint.getKryo();

        // Client → Server
        KRYO.register(LoginRequest.class);
        KRYO.register(PlayerMove.class);
        KRYO.register(ShootProjectile.class);

        // Server → Client
        KRYO.register(LoginResponse.class);
        KRYO.register(NewPlayer.class);
        KRYO.register(NewProjectile.class);
        KRYO.register(PlayerHealthUpdate.class);
        KRYO.register(PlayerPosition.class);
        KRYO.register(PlayerRemove.class);
        KRYO.register(ProjectilesBatch.class);

        // Java classes
        KRYO.register(int[].class);
        KRYO.register(float[].class);
    }
}
