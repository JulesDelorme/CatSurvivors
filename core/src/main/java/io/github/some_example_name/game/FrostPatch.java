package io.github.some_example_name.game;

import com.badlogic.gdx.math.Vector2;

public class FrostPatch {
    public final Vector2 position = new Vector2();
    public final float radius;
    public final float duration;
    public final float slowMultiplier;
    public float remainingDuration;
    public float animationTime;

    public FrostPatch(float x, float y, float radius, float duration, float slowMultiplier) {
        position.set(x, y);
        this.radius = radius;
        this.duration = duration;
        this.remainingDuration = duration;
        this.slowMultiplier = slowMultiplier;
    }

    public void update(float delta) {
        animationTime += delta;
        remainingDuration = Math.max(0f, remainingDuration - delta);
    }

    public boolean overlaps(Vector2 target, float targetRadius) {
        float combinedRadius = radius + targetRadius;
        return position.dst2(target) < combinedRadius * combinedRadius;
    }

    public boolean isExpired() {
        return remainingDuration <= 0f;
    }
}
