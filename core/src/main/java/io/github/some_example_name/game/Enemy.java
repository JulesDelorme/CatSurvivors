package io.github.some_example_name.game;

import com.badlogic.gdx.math.Vector2;

public class Enemy {
    public final Vector2 position = new Vector2();
    public final EnemyArchetype archetype;
    public float health;
    public boolean alive = true;
    public float animationTime;
    public float hitFlashTime;
    public float orbitDamageCooldown;
    public float chilledTime;

    public Enemy(EnemyArchetype archetype, float startX, float startY) {
        this.archetype = archetype;
        position.set(startX, startY);
        health = archetype.maxHealth;
    }

    public void update(Vector2 target, float additionalSpeed, float speedMultiplier, float delta) {
        animationTime += delta;
        hitFlashTime = Math.max(0f, hitFlashTime - delta * 3.5f);
        orbitDamageCooldown = Math.max(0f, orbitDamageCooldown - delta);
        chilledTime = Math.max(0f, chilledTime - delta * 3f);

        float deltaX = target.x - position.x;
        float deltaY = target.y - position.y;
        float lengthSquared = deltaX * deltaX + deltaY * deltaY;
        if (lengthSquared == 0f) {
            return;
        }

        float length = (float) Math.sqrt(lengthSquared);
        float speed = (archetype.baseSpeed + additionalSpeed) * speedMultiplier;
        if (archetype.burstEvery > 0f) {
            float cycle = animationTime % archetype.burstEvery;
            if (cycle <= archetype.burstDuration) {
                speed *= archetype.burstMultiplier;
            }
        }
        position.x += deltaX / length * speed * delta;
        position.y += deltaY / length * speed * delta;
    }

    public boolean overlaps(Vector2 target, float targetRadius) {
        float combinedRadius = archetype.radius + targetRadius;
        return position.dst2(target) < combinedRadius * combinedRadius;
    }

    public boolean applyDamage(float damage) {
        if (!alive) {
            return false;
        }

        health -= damage;
        hitFlashTime = 1f;
        if (health > 0f) {
            return false;
        }

        alive = false;
        return true;
    }

    public void applyChill(float duration) {
        chilledTime = Math.max(chilledTime, duration);
    }
}
