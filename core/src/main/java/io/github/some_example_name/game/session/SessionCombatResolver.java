package io.github.some_example_name.game.session;

import com.badlogic.gdx.math.MathUtils;
import io.github.some_example_name.game.Enemy;
import io.github.some_example_name.game.ExperienceOrb;
import io.github.some_example_name.game.FrostPatch;
import io.github.some_example_name.game.OrbitBlade;
import io.github.some_example_name.game.Projectile;
import io.github.some_example_name.game.WeaponType;
import io.github.some_example_name.game.stage.StageDefinition;

// Regroupe les interactions de combat et de terrain pendant un run.
final class SessionCombatResolver {
    private static final float ORBIT_HIT_COOLDOWN = 0.20f;
    private static final float ENEMY_DESPAWN_RADIUS = StageDefinition.WORLD_WIDTH * 1.6f;

    void updateExperienceOrbs(GameSession session, float delta) {
        float magnetRadius = session.getPickupMagnetRadius();
        float pickupTouchRadius = session.getPickupTouchRadius();

        for (ExperienceOrb orb : session.getExperienceOrbs()) {
            if (!orb.active) {
                continue;
            }
            orb.update(session.getPlayer().position, magnetRadius, delta);
            if (orb.overlaps(session.getPlayer().position, pickupTouchRadius)) {
                orb.active = false;
                session.gainExperienceInternal(orb.value);
                if (session.getState() == SessionState.LEVEL_UP) {
                    break;
                }
            }
        }

        for (int index = session.getExperienceOrbs().size - 1; index >= 0; index--) {
            if (!session.getExperienceOrbs().get(index).active) {
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
            if (!enemy.alive) {
                continue;
            }

            float distanceSquared = enemy.position.dst2(session.getPlayer().position);
            if (distanceSquared > maxDistanceSquared || distanceSquared >= closestDistanceSquared) {
                continue;
            }

            closest = enemy;
            closestDistanceSquared = distanceSquared;
        }
        return closest;
    }

    void fireAimedBurst(GameSession session, int projectileCount, float spreadDegrees, float speed, float radius, float damage,
                        int remainingHits, float maxDistance, WeaponType weaponType, int weaponLevel, float aimRange,
                        float spawnDistance, float splashRadius, float frostPatchRadius, float frostPatchDuration,
                        float frostSlowMultiplier) {
        // Tous les tirs "classiques" passent ici : on vise un ennemi proche, sinon la dernière direction du joueur.
        Enemy target = findNearestEnemy(session, aimRange);
        if (target != null) {
            session.attackDirectionBufferInternal().set(target.position).sub(session.getPlayer().position).nor();
        } else {
            session.attackDirectionBufferInternal().set(session.getPlayer().lastAimDirection);
        }

        if (session.attackDirectionBufferInternal().isZero(0.001f)) {
            session.attackDirectionBufferInternal().set(1f, 0f);
        }

        for (int index = 0; index < projectileCount; index++) {
            float spreadOffset = (index - (projectileCount - 1) * 0.5f) * spreadDegrees;
            session.weaponDirectionBufferInternal().set(session.attackDirectionBufferInternal()).rotateDeg(spreadOffset);
            float startX = session.getPlayer().position.x + session.weaponDirectionBufferInternal().x * spawnDistance;
            float startY = session.getPlayer().position.y + 4f + session.weaponDirectionBufferInternal().y * spawnDistance;
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
                session.getPlayer().position.x,
                session.getPlayer().position.y,
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

    void ensureOrbitBladeCount(GameSession session, int count, float orbitRadius, float bladeSize, float damage) {
        // Les lames orbitales sont conservées d'une frame à l'autre pour éviter de les recréer en boucle.
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
            if (!projectile.active) {
                continue;
            }
            // Une bombe glacée qui expire ou sort de l'écran doit quand même laisser sa zone au sol.
            projectile.update(delta);
            if (!projectile.active && projectile.hasFrostPatch() && !projectile.frostPatchSpawned) {
                spawnFrostPatch(session, projectile);
            }
            if (projectile.position.dst2(session.getPlayer().position) > ENEMY_DESPAWN_RADIUS * ENEMY_DESPAWN_RADIUS) {
                projectile.active = false;
                if (projectile.hasFrostPatch() && !projectile.frostPatchSpawned) {
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
            if (enemy.alive) {
                enemy.update(session.getPlayer().position, speedBonus, getEnemySpeedMultiplier(session, enemy), delta);
            }
        }
    }

    private float getEnemySpeedMultiplier(GameSession session, Enemy enemy) {
        float speedMultiplier = 1f;
        for (FrostPatch patch : session.getFrostPatches()) {
            if (!patch.overlaps(enemy.position, enemy.archetype.radius)) {
                continue;
            }
            speedMultiplier = Math.min(speedMultiplier, patch.slowMultiplier);
            enemy.applyChill(0.35f);
        }
        return speedMultiplier;
    }

    private void resolveProjectileHits(GameSession session) {
        // Un projectile peut tuer, exploser ou laisser une zone de glace selon son profil.
        for (Projectile projectile : session.getProjectiles()) {
            if (!projectile.active) {
                continue;
            }
            for (Enemy enemy : session.getEnemies()) {
                if (!enemy.alive || !projectile.overlaps(enemy)) {
                    continue;
                }
                if (projectile.splashRadius > 0f) {
                    explodeProjectile(session, projectile);
                    break;
                }
                if (enemy.applyDamage(projectile.damage)) {
                    spawnExperienceOrb(session, enemy);
                }
                if (projectile.hasFrostPatch() && !projectile.frostPatchSpawned) {
                    spawnFrostPatch(session, projectile);
                }
                projectile.registerHit();
                if (!projectile.active) {
                    break;
                }
            }
        }
    }

    private void explodeProjectile(GameSession session, Projectile projectile) {
        float splashRadiusSquared = projectile.splashRadius * projectile.splashRadius;
        for (Enemy enemy : session.getEnemies()) {
            if (!enemy.alive) {
                continue;
            }
            float combinedRadius = projectile.splashRadius + enemy.archetype.radius;
            if (projectile.position.dst2(enemy.position) > Math.max(splashRadiusSquared, combinedRadius * combinedRadius)) {
                continue;
            }
            if (enemy.applyDamage(projectile.damage)) {
                spawnExperienceOrb(session, enemy);
            }
        }
        if (projectile.hasFrostPatch() && !projectile.frostPatchSpawned) {
            spawnFrostPatch(session, projectile);
        }
        projectile.active = false;
    }

    private void spawnFrostPatch(GameSession session, Projectile projectile) {
        session.getFrostPatches().add(new FrostPatch(
            projectile.position.x,
            projectile.position.y,
            projectile.frostPatchRadius,
            projectile.frostPatchDuration,
            projectile.frostSlowMultiplier
        ));
        projectile.frostPatchSpawned = true;
    }

    private void resolveOrbitHits(GameSession session) {
        if (session.getOrbitBlades().size == 0) {
            return;
        }

        for (OrbitBlade blade : session.getOrbitBlades()) {
            for (Enemy enemy : session.getEnemies()) {
                if (!enemy.alive || enemy.orbitDamageCooldown > 0f) {
                    continue;
                }
                float combined = blade.size + enemy.archetype.radius;
                if (blade.position.dst2(enemy.position) > combined * combined) {
                    continue;
                }
                enemy.orbitDamageCooldown = ORBIT_HIT_COOLDOWN;
                if (enemy.applyDamage(blade.damage)) {
                    spawnExperienceOrb(session, enemy);
                }
            }
        }
    }

    private void spawnExperienceOrb(GameSession session, Enemy enemy) {
        session.getExperienceOrbs().add(new ExperienceOrb(enemy.position.x, enemy.position.y, enemy.archetype.xpValue));
    }

    private void removeInactiveEntities(GameSession session) {
        // Les ennemis non élites trop loin sont retirés pour garder une simulation légère autour du joueur.
        for (int index = session.getProjectiles().size - 1; index >= 0; index--) {
            if (!session.getProjectiles().get(index).active) {
                session.getProjectiles().removeIndex(index);
            }
        }
        for (int index = session.getEnemies().size - 1; index >= 0; index--) {
            Enemy enemy = session.getEnemies().get(index);
            if (!enemy.alive
                || (!enemy.archetype.elite
                && enemy.position.dst2(session.getPlayer().position) > ENEMY_DESPAWN_RADIUS * ENEMY_DESPAWN_RADIUS)) {
                session.getEnemies().removeIndex(index);
            }
        }
    }

    private void applyContactDamage(GameSession session, float delta) {
        float damageThisFrame = 0f;
        for (Enemy enemy : session.getEnemies()) {
            if (enemy.alive && enemy.overlaps(session.getPlayer().position, session.getPlayer().radius)) {
                damageThisFrame += enemy.archetype.contactDamagePerSecond * delta;
            }
        }

        if (damageThisFrame <= 0f) {
            return;
        }

        session.getPlayer().health = Math.max(0f, session.getPlayer().health - damageThisFrame);
        session.getPlayer().hitFlashTime = 1f;
        if (session.getPlayer().health <= 0f) {
            session.setStateInternal(SessionState.LOST);
        }
    }
}
