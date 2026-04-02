package io.github.some_example_name.game;

import com.badlogic.gdx.math.Vector2;

public class FrostPatch {
    private final Vector2 position = new Vector2();
    private final float radius;
    private final float duration;
    private final float slowMultiplier;
    private float remainingDuration;
    private float animationTime;

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

    public Vector2 getPosition() {
        return position;
    }

    public float getRadius() {
        return radius;
    }

    public float getDuration() {
        return duration;
    }

    public float getSlowMultiplier() {
        return slowMultiplier;
    }

    public float getRemainingDuration() {
        return remainingDuration;
    }

    public float getAnimationTime() {
        return animationTime;
    }
}
