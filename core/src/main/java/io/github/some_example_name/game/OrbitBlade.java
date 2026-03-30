package io.github.some_example_name.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class OrbitBlade {
    private final Vector2 offset = new Vector2();

    public final Vector2 position = new Vector2();
    public float angleDeg;
    public float orbitRadius;
    public float size;
    public float damage;

    public OrbitBlade(float angleDeg, float orbitRadius, float size, float damage) {
        this.angleDeg = angleDeg;
        this.orbitRadius = orbitRadius;
        this.size = size;
        this.damage = damage;
    }

    public void update(Vector2 anchor, float spinSpeed, float delta) {
        angleDeg = (angleDeg + spinSpeed * delta) % 360f;
        offset.set(MathUtils.cosDeg(angleDeg), MathUtils.sinDeg(angleDeg)).scl(orbitRadius);
        position.set(anchor).add(offset);
    }
}
