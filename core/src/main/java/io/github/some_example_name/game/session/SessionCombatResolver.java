package io.github.some_example_name.game.session;

import com.badlogic.gdx.math.MathUtils;
import io.github.some_example_name.game.Enemy;
import io.github.some_example_name.game.ExperienceOrb;
import io.github.some_example_name.game.FrostPatch;
import io.github.some_example_name.game.OrbitBlade;
import io.github.some_example_name.game.Projectile;
import io.github.some_example_name.game.WeaponType;
import io.github.some_example_name.game.stage.StageDefinition;

/**
 * Regroupe les interactions de combat et de terrain pendant un run.
 */
final class SessionCombatResolver {
    private static final float ORBIT_HIT_COOLDOWN = 0.20f;
    private static final float ENEMY_DESPAWN_RADIUS = StageDefinition.WORLD_WIDTH * 1.6f;

    void updateExperienceOrbs(GameSession session, float delta) {
        float magnetRadius = session.getPickupMagnetRadius();
        float pickupTouchRadius = session.getPickupTouchRadius();

        for (ExperienceOrb orb : session.getExperienceOrbs()) {
            if (!orb.isActive()) {
                continue;
            }
            orb.update(session.getPlayer().getPosition(), magnetRadius, delta);
            if (orb.overlaps(session.getPlayer().getPosition(), pickupTouchRadius)) {
                orb.deactivate();
                session.gainExperienceInternal(orb.getValue());
                if (session.getState() == SessionState.LEVEL_UP) {
                    break;
                }
            }
        }

        for (int index = session.getExperienceOrbs().size - 1; index >= 0; index--) {
            if (!session.getExperienceOrbs().get(index).isActive()) {
                session.getExperienceOrbs().removeIndex(index);
            }
        }
    }

    void advanceWorld(GameSession session, float delta) {
        updateProjectiles(session, delta);
        updateFrostPatches(session, delta);
        updateEnemies(session, delta);
        resolveProjectileHits(session);
        resolveOrbitHits(session);
        removeInactiveEntities(session);
        applyContactDamage(session, delta);
    }

    Enemy findNearestEnemy(GameSession session, float radius) {
        float maxDistanceSquared = radius * radius;
        Enemy closest = null;
        float closestDistanceSquared = Float.MAX_VALUE;

        for (Enemy enemy : session.getEnemies()) {
            if (!enemy.isAlive()) {
                continue;
            }

            float distanceSquared = enemy.getPosition().dst2(session.getPlayer().getPosition());
            if (distanceSquared > maxDistanceSquared || distanceSquared >= closestDistanceSquared) {
                continue;
            }

            closest = enemy;
            closestDistanceSquared = distanceSquared;
        }
        return closest;
    }

    /**
     * Crée une salve dirigée vers l'ennemi le plus proche ou, à défaut, vers la dernière direction du joueur.
     */
    void fireAimedBurst(GameSession session, int projectileCount, float spreadDegrees, float speed, float radius, float damage,
                        int remainingHits, float maxDistance, WeaponType weaponType, int weaponLevel, float aimRange,
                        float spawnDistance, float splashRadius, float frostPatchRadius, float frostPatchDuration,
                        float frostSlowMultiplier) {
        Enemy target = findNearestEnemy(session, aimRange);
        if (target != null) {
            session.attackDirectionBufferInternal().set(target.getPosition()).sub(session.getPlayer().getPosition()).nor();
        } else {
            session.attackDirectionBufferInternal().set(session.getPlayer().getLastAimDirection());
        }

        if (session.attackDirectionBufferInternal().isZero(0.001f)) {
            session.attackDirectionBufferInternal().set(1f, 0f);
        }

        for (int index = 0; index < projectileCount; index++) {
            float spreadOffset = (index - (projectileCount - 1) * 0.5f) * spreadDegrees;
            session.weaponDirectionBufferInternal().set(session.attackDirectionBufferInternal()).rotateDeg(spreadOffset);
            float startX = session.getPlayer().getPosition().x + session.weaponDirectionBufferInternal().x * spawnDistance;
            float startY = session.getPlayer().getPosition().y + 4f + session.weaponDirectionBufferInternal().y * spawnDistance;
            session.getProjectiles().add(new Projectile(
                startX,
                startY,
                session.weaponDirectionBufferInternal(),
                speed,
                radius,
                damage,
                remainingHits,
                maxDistance,
                weaponType,
                weaponLevel,
                splashRadius,
                frostPatchRadius,
                frostPatchDuration,
                frostSlowMultiplier
            ));
        }
    }

    void fireRadialBurst(GameSession session, int projectileCount, float speed, float radius, float damage, int remainingHits,
                         float maxDistance, WeaponType weaponType, int weaponLevel) {
        float startAngle = MathUtils.random(0f, 359f);
        for (int index = 0; index < projectileCount; index++) {
            float angle = startAngle + 360f * index / projectileCount;
            session.weaponDirectionBufferInternal().set(1f, 0f).setAngleDeg(angle);
            session.getProjectiles().add(new Projectile(
                session.getPlayer().getPosition().x,
                session.getPlayer().getPosition().y,
                session.weaponDirectionBufferInternal(),
                speed,
                radius,
                damage,
                remainingHits,
                maxDistance,
                weaponType,
                weaponLevel,
                0f,
                0f,
                0f,
                0f
            ));
        }
    }

    /**
     * Ajuste le nombre de lames orbitales sans les recréer inutilement à chaque frame.
     */
    void ensureOrbitBladeCount(GameSession session, int count, float orbitRadius, float bladeSize, float damage) {
        while (session.getOrbitBlades().size < count) {
            float angle = 360f * session.getOrbitBlades().size / Math.max(1, count);
            session.getOrbitBlades().add(new OrbitBlade(angle, orbitRadius, bladeSize, damage));
        }
        while (session.getOrbitBlades().size > count) {
            session.getOrbitBlades().removeIndex(session.getOrbitBlades().size - 1);
        }
    }

    private void updateProjectiles(GameSession session, float delta) {
        for (Projectile projectile : session.getProjectiles()) {
            if (!projectile.isActive()) {
                continue;
            }
            projectile.update(delta);
            if (!projectile.isActive() && projectile.hasFrostPatch() && !projectile.hasFrostPatchSpawned()) {
                spawnFrostPatch(session, projectile);
            }
            if (projectile.getPosition().dst2(session.getPlayer().getPosition()) > ENEMY_DESPAWN_RADIUS * ENEMY_DESPAWN_RADIUS) {
                projectile.deactivate();
                if (projectile.hasFrostPatch() && !projectile.hasFrostPatchSpawned()) {
                    spawnFrostPatch(session, projectile);
                }
            }
        }
    }

    private void updateFrostPatches(GameSession session, float delta) {
        for (FrostPatch patch : session.getFrostPatches()) {
            patch.update(delta);
        }
        for (int index = session.getFrostPatches().size - 1; index >= 0; index--) {
            if (session.getFrostPatches().get(index).isExpired()) {
                session.getFrostPatches().removeIndex(index);
            }
        }
    }

    private void updateEnemies(GameSession session, float delta) {
        float speedBonus = session.getStage().getSpeedBonus(session.getSurvivalTime());
        for (Enemy enemy : session.getEnemies()) {
            if (enemy.isAlive()) {
                enemy.update(session.getPlayer().getPosition(), speedBonus, getEnemySpeedMultiplier(session, enemy), delta);
            }
        }
    }

    private float getEnemySpeedMultiplier(GameSession session, Enemy enemy) {
        float speedMultiplier = 1f;
        for (FrostPatch patch : session.getFrostPatches()) {
            if (!patch.overlaps(enemy.getPosition(), enemy.getArchetype().getRadius())) {
                continue;
            }
            speedMultiplier = Math.min(speedMultiplier, patch.getSlowMultiplier());
            enemy.applyChill(0.35f);
        }
        return speedMultiplier;
    }

    private void resolveProjectileHits(GameSession session) {
        for (Projectile projectile : session.getProjectiles()) {
            if (!projectile.isActive()) {
                continue;
            }
            for (Enemy enemy : session.getEnemies()) {
                if (!enemy.isAlive() || !projectile.overlaps(enemy)) {
                    continue;
                }
                if (projectile.getSplashRadius() > 0f) {
                    explodeProjectile(session, projectile);
                    break;
                }
                if (enemy.applyDamage(projectile.getDamage())) {
                    spawnExperienceOrb(session, enemy);
                }
                if (projectile.hasFrostPatch() && !projectile.hasFrostPatchSpawned()) {
                    spawnFrostPatch(session, projectile);
                }
                projectile.registerHit();
                if (!projectile.isActive()) {
                    break;
                }
            }
        }
    }

    private void explodeProjectile(GameSession session, Projectile projectile) {
        float splashRadiusSquared = projectile.getSplashRadius() * projectile.getSplashRadius();
        for (Enemy enemy : session.getEnemies()) {
            if (!enemy.isAlive()) {
                continue;
            }
            float combinedRadius = projectile.getSplashRadius() + enemy.getArchetype().getRadius();
            if (projectile.getPosition().dst2(enemy.getPosition()) > Math.max(splashRadiusSquared, combinedRadius * combinedRadius)) {
                continue;
            }
            if (enemy.applyDamage(projectile.getDamage())) {
                spawnExperienceOrb(session, enemy);
            }
        }
        if (projectile.hasFrostPatch() && !projectile.hasFrostPatchSpawned()) {
            spawnFrostPatch(session, projectile);
        }
        projectile.deactivate();
    }

    private void spawnFrostPatch(GameSession session, Projectile projectile) {
        session.getFrostPatches().add(new FrostPatch(
            projectile.getPosition().x,
            projectile.getPosition().y,
            projectile.getFrostPatchRadius(),
            projectile.getFrostPatchDuration(),
            projectile.getFrostSlowMultiplier()
        ));
        projectile.markFrostPatchSpawned();
    }

    private void resolveOrbitHits(GameSession session) {
        if (session.getOrbitBlades().size == 0) {
            return;
        }

        for (OrbitBlade blade : session.getOrbitBlades()) {
            for (Enemy enemy : session.getEnemies()) {
                if (!enemy.isAlive() || enemy.getOrbitDamageCooldown() > 0f) {
                    continue;
                }
                float combined = blade.getSize() + enemy.getArchetype().getRadius();
                if (blade.getPosition().dst2(enemy.getPosition()) > combined * combined) {
                    continue;
                }
                enemy.startOrbitDamageCooldown(ORBIT_HIT_COOLDOWN);
                if (enemy.applyDamage(blade.getDamage())) {
                    spawnExperienceOrb(session, enemy);
                }
            }
        }
    }

    private void spawnExperienceOrb(GameSession session, Enemy enemy) {
        session.getExperienceOrbs().add(new ExperienceOrb(
            enemy.getPosition().x,
            enemy.getPosition().y,
            enemy.getArchetype().getXpValue()
        ));
    }

    /**
     * Retire les projectiles inactifs et les ennemis morts ou trop éloignés du joueur.
     */
    private void removeInactiveEntities(GameSession session) {
        for (int index = session.getProjectiles().size - 1; index >= 0; index--) {
            if (!session.getProjectiles().get(index).isActive()) {
                session.getProjectiles().removeIndex(index);
            }
        }
        for (int index = session.getEnemies().size - 1; index >= 0; index--) {
            Enemy enemy = session.getEnemies().get(index);
            if (!enemy.isAlive()
                || (!enemy.getArchetype().isElite()
                && enemy.getPosition().dst2(session.getPlayer().getPosition()) > ENEMY_DESPAWN_RADIUS * ENEMY_DESPAWN_RADIUS)) {
                session.getEnemies().removeIndex(index);
            }
        }
    }

    private void applyContactDamage(GameSession session, float delta) {
        float damageThisFrame = 0f;
        for (Enemy enemy : session.getEnemies()) {
            if (enemy.isAlive() && enemy.overlaps(session.getPlayer().getPosition(), session.getPlayer().getRadius())) {
                damageThisFrame += enemy.getArchetype().getContactDamagePerSecond() * delta;
            }
        }

        if (damageThisFrame <= 0f) {
            return;
        }

        if (session.getPlayer().applyDamage(damageThisFrame)) {
            session.setStateInternal(SessionState.LOST);
        }
    }
}
