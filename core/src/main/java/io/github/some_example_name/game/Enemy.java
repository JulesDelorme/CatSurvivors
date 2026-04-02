package io.github.some_example_name.game;

import com.badlogic.gdx.math.Vector2;

public class Enemy {
    private final Vector2 position = new Vector2();
    private final EnemyArchetype archetype;
    private float health;
    private boolean alive = true;
    private float animationTime;
    private float hitFlashTime;
    private float orbitDamageCooldown;
    private float chilledTime;

    public Enemy(EnemyArchetype archetype, float startX, float startY) {
        this.archetype = archetype;
        position.set(startX, startY);
        health = archetype.getMaxHealth();
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
        float speed = (archetype.getBaseSpeed() + additionalSpeed) * speedMultiplier;
        if (archetype.getBurstEvery() > 0f) {
            float cycle = animationTime % archetype.getBurstEvery();
            if (cycle <= archetype.getBurstDuration()) {
                speed *= archetype.getBurstMultiplier();
            }
        }
        position.x += deltaX / length * speed * delta;
        position.y += deltaY / length * speed * delta;
    }

    public boolean overlaps(Vector2 target, float targetRadius) {
        float combinedRadius = archetype.getRadius() + targetRadius;
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

    public Vector2 getPosition() {
        return position;
    }

    public EnemyArchetype getArchetype() {
        return archetype;
    }

    public float getHealth() {
        return health;
    }

    public boolean isAlive() {
        return alive;
    }

    public float getAnimationTime() {
        return animationTime;
    }

    public float getHitFlashTime() {
        return hitFlashTime;
    }

    public float getOrbitDamageCooldown() {
        return orbitDamageCooldown;
    }

    public void startOrbitDamageCooldown(float cooldown) {
        orbitDamageCooldown = Math.max(0f, cooldown);
    }

    public float getChilledTime() {
        return chilledTime;
    }
}
