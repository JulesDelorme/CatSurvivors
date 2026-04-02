package io.github.some_example_name.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class OrbitBlade {
    private final Vector2 offset = new Vector2();

    private final Vector2 position = new Vector2();
    private float angleDeg;
    private float orbitRadius;
    private float size;
    private float damage;

    public OrbitBlade(float angleDeg, float orbitRadius, float size, float damage) {
        this.angleDeg = angleDeg;
        this.orbitRadius = orbitRadius;
        this.size = size;
        this.damage = damage;
    }

    public void setStats(float orbitRadius, float size, float damage) {
        this.orbitRadius = orbitRadius;
        this.size = size;
        this.damage = damage;
    }

    public void update(Vector2 anchor, float spinSpeed, float delta) {
        angleDeg = (angleDeg + spinSpeed * delta) % 360f;
        offset.set(MathUtils.cosDeg(angleDeg), MathUtils.sinDeg(angleDeg)).scl(orbitRadius);
        position.set(anchor).add(offset);
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getAngleDeg() {
        return angleDeg;
    }

    public float getOrbitRadius() {
        return orbitRadius;
    }

    public float getSize() {
        return size;
    }

    public float getDamage() {
        return damage;
    }
}
