package io.github.some_example_name.game;

import com.badlogic.gdx.math.Vector2;

public class Projectile {
    public final Vector2 position = new Vector2();
    public final Vector2 velocity = new Vector2();
    public final WeaponType weaponType;
    public final int weaponLevel;
    public final float splashRadius;
    public final float frostPatchRadius;
    public final float frostPatchDuration;
    public final float frostSlowMultiplier;
    public final float radius;
    public final float damage;
    public final float maxDistance;
    public float traveledDistance;
    public int remainingHits;
    public boolean active = true;
    public boolean frostPatchSpawned;

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
        float combinedRadius = radius + enemy.archetype.radius;
        return position.dst2(enemy.position) < combinedRadius * combinedRadius;
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
}
