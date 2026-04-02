package io.github.some_example_name.game;

import com.badlogic.gdx.math.Vector2;

public class ExperienceOrb {
    private final Vector2 position = new Vector2();
    private final int value;
    private boolean active = true;
    private float pulseTime;

    public ExperienceOrb(float startX, float startY, int value) {
        position.set(startX, startY);
        this.value = value;
    }

    public void update(Vector2 playerPosition, float magnetRadius, float delta) {
        pulseTime += delta;
        float deltaX = playerPosition.x - position.x;
        float deltaY = playerPosition.y - position.y;
        float distanceSquared = deltaX * deltaX + deltaY * deltaY;
        if (distanceSquared > magnetRadius * magnetRadius || distanceSquared == 0f) {
            return;
        }

        float distance = (float) Math.sqrt(distanceSquared);
        float speed = 130f + distance * 2.4f;
        position.x += deltaX / distance * speed * delta;
        position.y += deltaY / distance * speed * delta;
    }

    public boolean overlaps(Vector2 playerPosition, float pickupRadius) {
        return position.dst2(playerPosition) < pickupRadius * pickupRadius;
    }

    public Vector2 getPosition() {
        return position;
    }

    public int getValue() {
        return value;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public float getPulseTime() {
        return pulseTime;
    }
}
