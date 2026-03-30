package io.github.some_example_name.game;

import com.badlogic.gdx.math.Vector2;

public class Enemy {
    public final Vector2 position = new Vector2();
    public final float radius;
    public final float baseSpeed;
    public final int xpValue;
    public float health;
    public boolean alive = true;

    public Enemy(float startX, float startY, float radius, float baseSpeed, float health, int xpValue) {
        position.set(startX, startY);
        this.radius = radius;
        this.baseSpeed = baseSpeed;
        this.health = health;
        this.xpValue = xpValue;
    }

    public void update(Vector2 target, float additionalSpeed, float delta) {
        float deltaX = target.x - position.x;
        float deltaY = target.y - position.y;
        float lengthSquared = deltaX * deltaX + deltaY * deltaY;
        if (lengthSquared == 0f) {
            return;
        }

        float length = (float) Math.sqrt(lengthSquared);
        float speed = baseSpeed + additionalSpeed;
        position.x += deltaX / length * speed * delta;
        position.y += deltaY / length * speed * delta;
    }

    public boolean overlaps(Vector2 target, float targetRadius) {
        float combinedRadius = radius + targetRadius;
        return position.dst2(target) < combinedRadius * combinedRadius;
    }

    public boolean applyDamage(float damage) {
        if (!alive) {
            return false;
        }

        health -= damage;
        if (health > 0f) {
            return false;
        }

        alive = false;
        return true;
    }
}
