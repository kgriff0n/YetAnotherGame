package dev.kgriffon.simplegame.entity;

public abstract class Entity {

    private final float hitboxWidth;
    private final float hitboxHeight;

    public Entity(float hitboxWidth, float hitboxHeight) {
        this.hitboxWidth = hitboxWidth;
        this.hitboxHeight = hitboxHeight;
    }

    public float getHitboxWidth() {
        return hitboxWidth;
    }

    public float getHitboxHeight() {
        return hitboxHeight;
    }
}
