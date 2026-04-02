package io.github.some_example_name.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import io.github.some_example_name.context.GameAssets;
import io.github.some_example_name.game.CatAnim;
import io.github.some_example_name.game.Enemy;
import io.github.some_example_name.game.ExperienceOrb;
import io.github.some_example_name.game.FrostPatch;
import io.github.some_example_name.game.OrbitBlade;
import io.github.some_example_name.game.Player;
import io.github.some_example_name.game.Projectile;
import io.github.some_example_name.game.WeaponType;
import io.github.some_example_name.game.session.GameSession;

public class EntityRenderer {
    private static final float PLAYER_DRAW_SIZE = 96f;
    private static final Color XP_OUTER = new Color(0.35f, 0.96f, 0.78f, 1f);
    private static final Color XP_INNER = new Color(0.88f, 1f, 0.96f, 1f);
    private static final Color BLADE_OUTER = new Color(1f, 0.84f, 0.48f, 1f);
    private static final Color BLADE_INNER = new Color(1f, 0.97f, 0.86f, 1f);
    private static final Color PROJECTILE_HAIRBALL = new Color(0.32f, 0.22f, 0.16f, 1f);
    private static final Color PROJECTILE_HAIRBALL_CORE = new Color(0.70f, 0.58f, 0.45f, 1f);
    private static final Color PROJECTILE_STONE = new Color(0.54f, 0.52f, 0.48f, 1f);
    private static final Color PROJECTILE_STONE_CORE = new Color(0.82f, 0.78f, 0.70f, 1f);
    private static final Color PROJECTILE_BONE = new Color(0.91f, 0.86f, 0.72f, 1f);
    private static final Color PROJECTILE_BONE_CORE = new Color(0.66f, 0.56f, 0.45f, 1f);
    private static final Color PROJECTILE_ORBIT = new Color(0.97f, 0.77f, 0.36f, 1f);
    private static final Color PROJECTILE_ORBIT_CORE = new Color(1f, 0.97f, 0.80f, 1f);
    private static final float ORBITING_SWORD_SIZE = 40f;
    private static final Color MAGIC_RED = new Color(0.98f, 0.43f, 0.49f, 1f);
    private static final Color MAGIC_VIOLET = new Color(0.76f, 0.46f, 0.96f, 1f);
    private static final Color MAGIC_BLUE = new Color(0.35f, 0.71f, 1f, 1f);
    private static final Color MAGIC_GREEN = new Color(0.34f, 0.96f, 0.76f, 1f);
    private static final Color FROST_TINT = new Color(0.58f, 0.90f, 1f, 1f);
    private static final Color HEALTH_BG = new Color(0.05f, 0.06f, 0.08f, 0.85f);
    private static final Color HEALTH_FILL = new Color(1f, 0.36f, 0.34f, 1f);
    private static final Color HEALTH_ELITE = new Color(1f, 0.84f, 0.48f, 1f);

    /**
     * Dessine les entités dans un ordre stable pour préserver la lisibilité du combat.
     */
    public void draw(SpriteBatch batch, GameAssets assets, GameSession session) {
        Texture whitePixel = assets.getWhitePixel();
        Texture softGlow = assets.getSoftGlow();
        drawExperience(batch, assets, session);
        drawFrostPatches(batch, whitePixel, assets, session);
        drawEnemies(batch, whitePixel, softGlow, assets, session);
        drawProjectiles(batch, whitePixel, softGlow, assets, session);
        drawOrbitBlades(batch, whitePixel, softGlow, assets, session);
        drawPlayer(batch, assets, softGlow, session);
        drawOrbitingSwords(batch, whitePixel, softGlow, assets, session);
        batch.setColor(Color.WHITE);
    }

    private void drawExperience(SpriteBatch batch, GameAssets assets, GameSession session) {
        Texture orbTexture = assets.getMagicOrb();
        Texture glow = assets.getSoftGlow();
        for (ExperienceOrb orb : session.getExperienceOrbs()) {
            float pulse = 1.5f + MathUtils.sin(orb.getPulseTime() * 7f) * 1.2f;
            float size = 10f + orb.getValue() * 2f + pulse;
            float glowSize = size * 2.4f;
            float x = orb.getPosition().x - size * 0.5f;
            float y = orb.getPosition().y - size * 0.5f;

            drawGlow(batch, glow, orb.getPosition().x, orb.getPosition().y, glowSize, XP_OUTER, 0.22f);
            batch.setColor(XP_OUTER.r, XP_OUTER.g, XP_OUTER.b, 0.92f);
            batch.draw(orbTexture, x, y, size, size);
            batch.setColor(XP_INNER.r, XP_INNER.g, XP_INNER.b, 0.95f);
            batch.draw(orbTexture, x + 3f, y + 3f, size - 6f, size - 6f);
            batch.setColor(1f, 1f, 1f, 0.45f);
            batch.draw(orbTexture, x + size * 0.24f, y + size * 0.24f, size * 0.28f, size * 0.28f);
        }
    }

    /**
     * Dessine les ennemis avec leur animation de flottement, leurs effets de hit et leur barre de vie.
     */
    private void drawEnemies(SpriteBatch batch, Texture whitePixel, Texture softGlow, GameAssets assets, GameSession session) {
        for (Enemy enemy : session.getEnemies()) {
            TextureRegion sprite = assets.getEnemySprite(enemy.getArchetype());
            Color primary = new Color(enemy.getArchetype().getPrimaryColor());
            Color secondary = new Color(enemy.getArchetype().getSecondaryColor());
            float bob = MathUtils.sin(enemy.getAnimationTime() * (enemy.getArchetype().isRobotic() ? 6.5f : 4.5f)
                + enemy.getPosition().x * 0.02f) * (enemy.getArchetype().isRobotic() ? 1.6f : 0.9f);
            if (enemy.getHitFlashTime() > 0f) {
                float lerp = Math.min(1f, enemy.getHitFlashTime());
                primary.lerp(Color.WHITE, 0.5f * lerp);
                secondary.lerp(Color.WHITE, 0.65f * lerp);
            }
            if (enemy.getChilledTime() > 0f) {
                float chill = Math.min(1f, enemy.getChilledTime() * 1.8f);
                primary.lerp(FROST_TINT, 0.35f * chill);
                secondary.lerp(FROST_TINT, 0.50f * chill);
            }

            float shadowWidth = enemy.getArchetype().getRadius() * (enemy.getArchetype().isRobotic() ? 3.0f : 3.3f);
            float shadowHeight = enemy.getArchetype().isRobotic() ? 18f : 16f;
            drawSoftShadow(batch, softGlow, enemy.getPosition().x, enemy.getPosition().y - enemy.getArchetype().getRadius() * 0.9f,
                shadowWidth,
                shadowHeight, 0.24f);

            float auraSize = enemy.getArchetype().getRadius() * (enemy.getArchetype().isElite() ? 5.8f : 4.0f);
            if (enemy.getArchetype().isRobotic()) {
                drawGlow(batch, softGlow, enemy.getPosition().x, enemy.getPosition().y + bob + 4f, auraSize,
                    enemy.getArchetype().getAccentColor(), enemy.getArchetype().isElite() ? 0.18f : 0.08f);
            }
            if (enemy.getArchetype().isElite()) {
                drawGlow(batch, softGlow, enemy.getPosition().x, enemy.getPosition().y + bob + 4f, auraSize * 1.18f, HEALTH_ELITE, 0.11f);
            }
            if (enemy.getHitFlashTime() > 0f) {
                drawGlow(batch, softGlow, enemy.getPosition().x, enemy.getPosition().y + bob, auraSize * 0.9f, Color.WHITE,
                    0.12f * Math.min(1f, enemy.getHitFlashTime()));
            }
            if (enemy.getChilledTime() > 0f) {
                drawGlow(batch, softGlow, enemy.getPosition().x, enemy.getPosition().y + bob, auraSize * 1.1f, FROST_TINT,
                    0.10f * Math.min(1f, enemy.getChilledTime()));
            }

            if (sprite != null) {
                float width;
                float height;
                if (enemy.getArchetype().isRobotic()) {
                    float aspect = (float) sprite.getRegionWidth() / (float) sprite.getRegionHeight();
                    height = enemy.getArchetype().getRadius() * (enemy.getArchetype().isElite() ? 2.85f : 2.45f);
                    width = height * aspect;
                } else {
                    float drawScale = enemy.getArchetype().isElite() ? 3.2f : 2.5f;
                    width = sprite.getRegionWidth() * drawScale;
                    height = sprite.getRegionHeight() * drawScale;
                }
                float x = enemy.getPosition().x - width * 0.5f;
                float y = enemy.getPosition().y - height * 0.5f + bob;

                batch.setColor(0f, 0f, 0f, 0.10f);
                batch.draw(whitePixel, x + 4f, y + 4f, width, height);
                batch.setColor(primary);
                batch.draw(sprite, x, y, width, height);

                if (enemy.getArchetype().isElite()) {
                    batch.setColor(enemy.getArchetype().getAccentColor());
                    batch.draw(whitePixel, x - 6f, y - 6f, width + 12f, 2f);
                }
                drawEnemyHealthBar(batch, whitePixel, enemy, enemy.getPosition().x - width * 0.34f, y + height + 6f, width * 0.68f);
            } else {
                float width = enemy.getArchetype().getRadius() * (enemy.getArchetype().isRobotic() ? 2.15f : 2.25f);
                float height = enemy.getArchetype().getRadius() * (enemy.getArchetype().isRobotic() ? 1.65f : 1.45f);
                float x = enemy.getPosition().x - width * 0.5f;
                float y = enemy.getPosition().y - height * 0.55f + bob;

                batch.setColor(primary);
                batch.draw(whitePixel, x, y, width, height);

                batch.setColor(secondary);
                batch.draw(whitePixel, x + 4f, y + 4f, width - 8f, height - 8f);

                batch.setColor(enemy.getArchetype().getAccentColor());
                if (enemy.getArchetype().isRobotic()) {
                    batch.draw(whitePixel, x + 4f, y + height - 5f, width - 8f, 3f);
                    batch.draw(whitePixel, x + width * 0.25f, y + height * 0.48f, width * 0.5f, 3f);
                } else {
                    batch.draw(whitePixel, x + 4f, y + height - 2f, 4f, 8f);
                    batch.draw(whitePixel, x + width - 8f, y + height - 2f, 4f, 8f);
                }

                if (enemy.getArchetype().isElite()) {
                    batch.setColor(enemy.getArchetype().getAccentColor());
                    batch.draw(whitePixel, x - 4f, y - 4f, width + 8f, 2f);
                }
                drawEnemyHealthBar(batch, whitePixel, enemy, x, y + height + 6f, width);
            }
        }
    }

    private void drawFrostPatches(SpriteBatch batch, Texture whitePixel, GameAssets assets, GameSession session) {
        TextureRegion sigil = assets.getFrostBombSigil();
        TextureRegion swirl = assets.getFrostBombSwirl();
        TextureRegion trail = assets.getFrostBombTrail();
        TextureRegion streak = assets.getFrostBombStreak();
        TextureRegion burst = assets.getFrostBombBurst();

        for (FrostPatch patch : session.getFrostPatches()) {
            float alpha = Math.min(1f, patch.getRemainingDuration() / patch.getDuration());
            float size = patch.getRadius() * 2f;
            float x = patch.getPosition().x - patch.getRadius();
            float y = patch.getPosition().y - patch.getRadius();

            batch.setColor(0.45f, 0.86f, 1f, 0.10f * alpha);
            batch.draw(whitePixel, x - 4f, y - 4f, size + 8f, size + 8f);

            batch.setColor(0.58f, 0.92f, 1f, 0.30f * alpha);
            batch.draw(sigil, x, y, size * 0.5f, size * 0.5f, size, size, 1f, 1f, -patch.getAnimationTime() * 22f);

            batch.setColor(0.75f, 0.96f, 1f, 0.32f * alpha);
            batch.draw(swirl, x + 6f, y + 6f, (size - 12f) * 0.5f, (size - 12f) * 0.5f, size - 12f, size - 12f, 1f, 1f,
                patch.getAnimationTime() * 36f);

            batch.setColor(0.84f, 0.98f, 1f, 0.22f * alpha);
            batch.draw(trail, x - 8f, patch.getPosition().y - 10f, size + 16f, 20f);
            batch.draw(streak, patch.getPosition().x - 10f, y - 8f, 20f, size + 16f);

            float burstSize = size * 0.42f;
            batch.setColor(1f, 1f, 1f, 0.35f * alpha);
            batch.draw(burst, patch.getPosition().x - burstSize * 0.5f, patch.getPosition().y - burstSize * 0.5f,
                burstSize * 0.5f, burstSize * 0.5f, burstSize, burstSize, 1f, 1f, patch.getAnimationTime() * 48f);
        }
    }

    private void drawProjectiles(SpriteBatch batch, Texture whitePixel, Texture softGlow, GameAssets assets, GameSession session) {
        Texture orbTexture = assets.getMagicOrb();
        for (Projectile projectile : session.getProjectiles()) {
            if (projectile.getWeaponType() == WeaponType.STONE_SPRAY) {
                drawMageProjectile(batch, assets, projectile);
                continue;
            }
            if (projectile.getWeaponType() == WeaponType.BONE_DART) {
                drawSwordSlash(batch, whitePixel, assets, projectile);
                continue;
            }
            if (projectile.getWeaponType() == WeaponType.FROST_BOMB) {
                drawFrostBomb(batch, whitePixel, assets, projectile);
                continue;
            }

            float size = projectile.getRadius() * 2f;
            float x = projectile.getPosition().x - projectile.getRadius();
            float y = projectile.getPosition().y - projectile.getRadius();
            Color outer = PROJECTILE_HAIRBALL;
            Color inner = PROJECTILE_HAIRBALL_CORE;
            if (projectile.getWeaponType() == WeaponType.STONE_SPRAY) {
                outer = PROJECTILE_STONE;
                inner = PROJECTILE_STONE_CORE;
            } else if (projectile.getWeaponType() == WeaponType.BONE_DART) {
                outer = PROJECTILE_BONE;
                inner = PROJECTILE_BONE_CORE;
            } else if (projectile.getWeaponType() == WeaponType.ORBIT_CLAWS) {
                outer = PROJECTILE_ORBIT;
                inner = PROJECTILE_ORBIT_CORE;
            }

            drawGlow(batch, softGlow, projectile.getPosition().x, projectile.getPosition().y, size * 3.2f, outer, 0.20f);
            batch.setColor(outer.r, outer.g, outer.b, 0.92f);
            batch.draw(orbTexture, x - 1f, y - 1f, size + 2f, size + 2f);
            batch.setColor(inner.r, inner.g, inner.b, 0.96f);
            batch.draw(orbTexture, x + 3f, y + 3f, size - 6f, size - 6f);
            batch.setColor(1f, 1f, 1f, 0.24f);
            batch.draw(whitePixel, x + size * 0.30f, y + size * 0.62f, size * 0.22f, size * 0.12f);
        }
    }

    private void drawMageProjectile(SpriteBatch batch, GameAssets assets, Projectile projectile) {
        Texture orb = assets.getMagicOrb();
        TextureRegion focus = assets.getMageFocusIcon(projectile.getWeaponLevel());
        TextureRegion comet = assets.getFireCometProjectile();
        TextureRegion burst = assets.getFireCometBurst();
        TextureRegion sigil = assets.getFireCometSigil();
        TextureRegion streak = assets.getFireCometStreak();
        Color magicColor = getMagicColor(projectile.getWeaponLevel());
        float angle = projectile.getVelocity().angleDeg() - 45f;
        float size = Math.max(22f, projectile.getRadius() * 4.2f);
        float auraSize = size + projectile.getSplashRadius() * 0.35f;
        float x = projectile.getPosition().x - size * 0.5f;
        float y = projectile.getPosition().y - size * 0.5f;
        float auraX = projectile.getPosition().x - auraSize * 0.5f;
        float auraY = projectile.getPosition().y - auraSize * 0.5f;
        float sigilSize = auraSize + 10f;
        float trailLength = size + 20f;

        batch.setColor(magicColor.r, magicColor.g, magicColor.b, 0.20f);
        batch.draw(sigil, projectile.getPosition().x - sigilSize * 0.5f, projectile.getPosition().y - sigilSize * 0.5f,
            sigilSize * 0.5f, sigilSize * 0.5f, sigilSize, sigilSize, 1f, 1f, -projectile.getTraveledDistance() * 0.45f);

        batch.setColor(magicColor.r, magicColor.g, magicColor.b, 0.34f);
        batch.draw(streak, projectile.getPosition().x - trailLength * 0.64f, projectile.getPosition().y - size * 0.40f,
            trailLength * 0.16f, size * 0.40f, trailLength, size * 0.80f, 1f, 1f, angle);

        batch.setColor(magicColor.r, magicColor.g, magicColor.b, 0.24f);
        batch.draw(orb, auraX, auraY, auraSize, auraSize);

        batch.setColor(magicColor.r, magicColor.g, magicColor.b, 0.92f);
        batch.draw(comet, x - 2f, y - 2f, (size + 4f) * 0.5f, (size + 4f) * 0.5f, size + 4f, size + 4f, 1f, 1f,
            angle + projectile.getTraveledDistance() * 0.10f);

        batch.setColor(1f, 1f, 1f, 0.72f);
        batch.draw(orb, x + 4f, y + 4f, size - 8f, size - 8f);

        float burstSize = size * 0.62f;
        batch.setColor(1f, 1f, 1f, 0.76f);
        batch.draw(burst, projectile.getPosition().x - burstSize * 0.5f, projectile.getPosition().y - burstSize * 0.5f,
            burstSize * 0.5f, burstSize * 0.5f, burstSize, burstSize, 1f, 1f, projectile.getTraveledDistance() * 0.22f);

        float focusSize = size * 0.55f;
        batch.setColor(1f, 1f, 1f, 0.86f);
        batch.draw(focus, projectile.getPosition().x - focusSize * 0.5f, projectile.getPosition().y - focusSize * 0.5f,
            focusSize * 0.5f, focusSize * 0.5f, focusSize, focusSize, 1f, 1f, projectile.getTraveledDistance() * 1.8f);
    }

    private void drawSwordSlash(SpriteBatch batch, Texture whitePixel, GameAssets assets, Projectile projectile) {
        TextureRegion sprite = assets.getSwordIcon(projectile.getWeaponLevel());
        float angle = projectile.getVelocity().angleDeg() - 45f;
        float size = Math.max(30f, projectile.getRadius() * 5f);
        float x = projectile.getPosition().x - size * 0.5f;
        float y = projectile.getPosition().y - size * 0.5f;
        float directionX = MathUtils.cosDeg(projectile.getVelocity().angleDeg());
        float directionY = MathUtils.sinDeg(projectile.getVelocity().angleDeg());

        batch.setColor(1f, 1f, 1f, 0.15f);
        batch.draw(sprite, x - directionX * 10f, y - directionY * 10f, size * 0.30f, size * 0.22f, size, size, 1f, 1f, angle);
        batch.setColor(1f, 1f, 1f, 0.30f);
        batch.draw(sprite, x - directionX * 5f, y - directionY * 5f, size * 0.30f, size * 0.22f, size, size, 1f, 1f, angle);

        batch.setColor(1f, 1f, 1f, 0.18f);
        batch.draw(whitePixel, projectile.getPosition().x - directionX * 14f, projectile.getPosition().y - directionY * 14f, 28f, 4f);

        batch.setColor(Color.WHITE);
        batch.draw(sprite, x, y, size * 0.30f, size * 0.22f, size, size, 1f, 1f, angle);
    }

    private void drawFrostBomb(SpriteBatch batch, Texture whitePixel, GameAssets assets, Projectile projectile) {
        TextureRegion bomb = assets.getFrostBombProjectile();
        TextureRegion burst = assets.getFrostBombBurst();
        TextureRegion streak = assets.getFrostBombStreak();
        float angle = projectile.getVelocity().angleDeg() - 45f;
        float size = Math.max(26f, projectile.getRadius() * 4.6f);
        float x = projectile.getPosition().x - size * 0.5f;
        float y = projectile.getPosition().y - size * 0.5f;
        float trailLength = Math.max(24f, projectile.getSplashRadius() * 0.55f);
        float directionX = MathUtils.cosDeg(projectile.getVelocity().angleDeg());
        float directionY = MathUtils.sinDeg(projectile.getVelocity().angleDeg());

        batch.setColor(0.70f, 0.94f, 1f, 0.24f);
        batch.draw(streak, x - directionX * 16f, y - directionY * 16f, size * 0.5f, size * 0.5f, trailLength, size * 0.75f,
            1f, 1f, angle);

        batch.setColor(0.74f, 0.97f, 1f, 0.82f);
        batch.draw(bomb, x, y, size * 0.5f, size * 0.5f, size, size, 1f, 1f, angle + projectile.getTraveledDistance() * 0.18f);

        float burstSize = size * 0.52f;
        batch.setColor(1f, 1f, 1f, 0.66f);
        batch.draw(burst, projectile.getPosition().x - burstSize * 0.5f, projectile.getPosition().y - burstSize * 0.5f,
            burstSize * 0.5f, burstSize * 0.5f, burstSize, burstSize, 1f, 1f, -projectile.getTraveledDistance() * 0.25f);

        batch.setColor(0.56f, 0.90f, 1f, 0.18f);
        batch.draw(whitePixel, x - 6f, y - 6f, size + 12f, size + 12f);
    }

    private void drawOrbitBlades(SpriteBatch batch, Texture whitePixel, Texture softGlow, GameAssets assets, GameSession session) {
        TextureRegion clawIcon = assets.getWeaponIcon(WeaponType.ORBIT_CLAWS, 5);
        for (OrbitBlade blade : session.getOrbitBlades()) {
            float size = blade.getSize() * 3f;
            float x = blade.getPosition().x - size * 0.5f;
            float y = blade.getPosition().y - size * 0.5f;
            drawGlow(batch, softGlow, blade.getPosition().x, blade.getPosition().y, size * 1.9f, BLADE_OUTER, 0.22f);
            batch.setColor(0f, 0f, 0f, 0.22f);
            batch.draw(whitePixel, x + 5f, y - 2f, size - 10f, 8f);
            batch.setColor(1f, 0.96f, 0.82f, 0.94f);
            batch.draw(clawIcon, x, y, size * 0.5f, size * 0.5f, size, size, 1f, 1f, blade.getAngleDeg() - 42f);
            batch.setColor(BLADE_INNER.r, BLADE_INNER.g, BLADE_INNER.b, 0.30f);
            batch.draw(softGlow, x + size * 0.16f, y + size * 0.16f, size * 0.68f, size * 0.68f);
        }
    }

    private void drawOrbitingSwords(SpriteBatch batch, Texture whitePixel, Texture softGlow, GameAssets assets, GameSession session) {
        int weaponLevel = session.getWeaponLevel(WeaponType.BONE_DART);
        if (weaponLevel <= 0) {
            return;
        }

        Player player = session.getPlayer();
        TextureRegion sprite = assets.getSwordIcon(weaponLevel);
        float size = ORBITING_SWORD_SIZE + Math.min(12f, weaponLevel * 2f);
        float orbitRadius = 34f + weaponLevel * 6f;
        float baseAngle = player.getAnimationTime() * (120f + weaponLevel * 18f);
        int swordCount = getOrbitingSwordCount(weaponLevel);

        for (int index = 0; index < swordCount; index++) {
            float angle = baseAngle + 360f * index / swordCount;
            float centerX = player.getPosition().x + MathUtils.cosDeg(angle) * orbitRadius;
            float centerY = player.getPosition().y + 6f + MathUtils.sinDeg(angle) * orbitRadius;
            drawOrbitingSword(batch, whitePixel, softGlow, sprite, centerX, centerY, size, angle - 45f);
        }
    }

    private void drawOrbitingSword(SpriteBatch batch, Texture whitePixel, Texture softGlow, TextureRegion sprite, float centerX,
                                   float centerY, float size, float rotation) {
        float x = centerX - size * 0.5f;
        float y = centerY - size * 0.5f;
        drawGlow(batch, softGlow, centerX, centerY, size * 1.9f, PROJECTILE_BONE, 0.16f);
        batch.setColor(0f, 0f, 0f, 0.22f);
        batch.draw(whitePixel, x + 3f, y - 2f, size - 6f, size - 6f);
        batch.setColor(Color.WHITE);
        batch.draw(sprite, x, y, size * 0.28f, size * 0.22f, size, size, 1f, 1f, rotation);
    }

    private int getOrbitingSwordCount(int weaponLevel) {
        if (weaponLevel >= 4) {
            return 3;
        }
        if (weaponLevel >= 2) {
            return 2;
        }
        return 1;
    }

    private Color getMagicColor(int weaponLevel) {
        switch (weaponLevel) {
            case 1:
                return MAGIC_RED;
            case 2:
                return MAGIC_VIOLET;
            case 3:
                return MAGIC_BLUE;
            default:
                return MAGIC_GREEN;
        }
    }

    /**
     * Dessine le chat et ses traînées de déplacement pour mieux lire sa position pendant l'action.
     */
    private void drawPlayer(SpriteBatch batch, GameAssets assets, Texture softGlow, GameSession session) {
        Player player = session.getPlayer();
        Animation<TextureRegion> animation = assets.getCatAnimation(player.getAnim() == CatAnim.RUN ? CatAnim.RUN : CatAnim.IDLE);
        TextureRegion frame = animation.getKeyFrame(player.getAnimationTime());
        float drawX = player.getPosition().x - PLAYER_DRAW_SIZE * 0.5f;
        float bob = player.isMoving() ? MathUtils.sin(player.getAnimationTime() * 9f) * 1.8f : 0f;
        float drawY = player.getPosition().y - PLAYER_DRAW_SIZE * 0.42f + bob;
        float flash = Math.min(1f, player.getHitFlashTime());

        drawSoftShadow(batch, softGlow, player.getPosition().x, player.getPosition().y - 20f, 72f, 20f, 0.26f);
        drawGlow(batch, softGlow, player.getPosition().x, player.getPosition().y + 8f, 160f, session.getStage().getAccentColor(), 0.10f);
        if (player.getHitFlashTime() > 0f) {
            drawGlow(batch, softGlow, player.getPosition().x, player.getPosition().y + 8f, 180f, Color.WHITE, 0.16f * flash);
        }

        if (player.isMoving()) {
            for (int afterIndex = 2; afterIndex >= 1; afterIndex--) {
                float alpha = 0.10f * afterIndex;
                float offsetX = -player.getMovement().x * afterIndex * 8f;
                float offsetY = -player.getMovement().y * afterIndex * 5f;
                batch.setColor(session.getStage().getAccentColor().r, session.getStage().getAccentColor().g,
                    session.getStage().getAccentColor().b, alpha);
                drawCatFrame(batch, frame, drawX + offsetX, drawY + offsetY, player.isFacingLeft());
            }
        }

        batch.setColor(1f, 1f - 0.18f * flash, 1f - 0.18f * flash, 1f);
        drawCatFrame(batch, frame, drawX, drawY, player.isFacingLeft());
    }

    private void drawCatFrame(SpriteBatch batch, TextureRegion frame, float drawX, float drawY, boolean facingLeft) {
        if (facingLeft) {
            batch.draw(frame, drawX + PLAYER_DRAW_SIZE, drawY, -PLAYER_DRAW_SIZE, PLAYER_DRAW_SIZE);
            return;
        }
        batch.draw(frame, drawX, drawY, PLAYER_DRAW_SIZE, PLAYER_DRAW_SIZE);
    }

    /**
     * Dessine la barre de vie d'un ennemi en gardant celles des élites toujours visibles.
     */
    private void drawEnemyHealthBar(SpriteBatch batch, Texture whitePixel, Enemy enemy, float x, float y, float width) {
        if (!enemy.isAlive() || (enemy.getHealth() >= enemy.getArchetype().getMaxHealth() && !enemy.getArchetype().isElite())) {
            return;
        }

        float ratio = Math.max(0f, Math.min(1f, enemy.getHealth() / enemy.getArchetype().getMaxHealth()));
        batch.setColor(HEALTH_BG);
        batch.draw(whitePixel, x, y, width, 6f);
        Color fill = enemy.getArchetype().isElite() ? HEALTH_ELITE : HEALTH_FILL;
        batch.setColor(fill);
        batch.draw(whitePixel, x + 1f, y + 1f, (width - 2f) * ratio, 4f);
    }

    private void drawSoftShadow(SpriteBatch batch, Texture softGlow, float centerX, float centerY, float width, float height,
                                float alpha) {
        batch.setColor(0f, 0f, 0f, alpha);
        batch.draw(softGlow, centerX - width * 0.5f, centerY - height * 0.5f, width, height);
    }

    private void drawGlow(SpriteBatch batch, Texture softGlow, float centerX, float centerY, float size, Color color, float alpha) {
        batch.setColor(color.r, color.g, color.b, alpha);
        batch.draw(softGlow, centerX - size * 0.5f, centerY - size * 0.5f, size, size);
    }
}
