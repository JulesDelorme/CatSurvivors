package io.github.some_example_name.game;

import com.badlogic.gdx.math.Vector2;

public class Projectile {
    private final Vector2 position = new Vector2();
    private final Vector2 velocity = new Vector2();
    private final WeaponType weaponType;
    private final int weaponLevel;
    private final float splashRadius;
    private final float frostPatchRadius;
    private final float frostPatchDuration;
    private final float frostSlowMultiplier;
    private final float radius;
    private final float damage;
    private final float maxDistance;
    private float traveledDistance;
    private int remainingHits;
    private boolean active = true;
    private boolean frostPatchSpawned;

    public Projectile(float startX, float startY, Vector2 direction, float speed, float radius, float damage,
                      int remainingHits, float maxDistance, WeaponType weaponType, int weaponLevel, float splashRadius,
                      float frostPatchRadius, float frostPatchDuration, float frostSlowMultiplier) {
        position.set(startX, startY);
        velocity.set(direction).nor().scl(speed);
        this.radius = radius;
        this.damage = damage;
        this.remainingHits = remainingHits;
        this.maxDistance = maxDistance;
        this.weaponType = weaponType;
        this.weaponLevel = weaponLevel;
        this.splashRadius = splashRadius;
        this.frostPatchRadius = frostPatchRadius;
        this.frostPatchDuration = frostPatchDuration;
        this.frostSlowMultiplier = frostSlowMultiplier;
    }

    public void update(float delta) {
        float stepDistance = velocity.len() * delta;
        position.mulAdd(velocity, delta);
        traveledDistance += stepDistance;
        if (traveledDistance >= maxDistance) {
            active = false;
        }
    }

    public boolean overlaps(Enemy enemy) {
        float combinedRadius = radius + enemy.getArchetype().getRadius();
        return position.dst2(enemy.getPosition()) < combinedRadius * combinedRadius;
    }

    public void registerHit() {
        remainingHits--;
        if (remainingHits <= 0) {
            active = false;
        }
    }

    public boolean hasFrostPatch() {
        return frostPatchRadius > 0f && frostPatchDuration > 0f;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public int getWeaponLevel() {
        return weaponLevel;
    }

    public float getSplashRadius() {
        return splashRadius;
    }

    public float getFrostPatchRadius() {
        return frostPatchRadius;
    }

    public float getFrostPatchDuration() {
        return frostPatchDuration;
    }

    public float getFrostSlowMultiplier() {
        return frostSlowMultiplier;
    }

    public float getRadius() {
        return radius;
    }

    public float getDamage() {
        return damage;
    }

    public float getMaxDistance() {
        return maxDistance;
    }

    public float getTraveledDistance() {
        return traveledDistance;
    }

    public int getRemainingHits() {
        return remainingHits;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public boolean hasFrostPatchSpawned() {
        return frostPatchSpawned;
    }

    public void markFrostPatchSpawned() {
        frostPatchSpawned = true;
    }
}
