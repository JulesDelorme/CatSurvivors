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
    private static final Color SHADOW = new Color(0f, 0f, 0f, 0.20f);
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

    public void draw(SpriteBatch batch, GameAssets assets, GameSession session) {
        Texture whitePixel = assets.getWhitePixel();
        drawExperience(batch, whitePixel, session);
        drawFrostPatches(batch, whitePixel, assets, session);
        drawEnemies(batch, whitePixel, assets, session);
        drawProjectiles(batch, whitePixel, assets, session);
        drawOrbitBlades(batch, whitePixel, session);
        drawPlayer(batch, assets, whitePixel, session.getPlayer());
        drawOrbitingSwords(batch, whitePixel, assets, session);
        batch.setColor(Color.WHITE);
    }

    private void drawExperience(SpriteBatch batch, Texture whitePixel, GameSession session) {
        for (ExperienceOrb orb : session.getExperienceOrbs()) {
            float pulse = 1.5f + MathUtils.sin(orb.pulseTime * 7f) * 1.2f;
            float size = 10f + orb.value * 2f + pulse;
            float x = orb.position.x - size * 0.5f;
            float y = orb.position.y - size * 0.5f;

            batch.setColor(XP_OUTER);
            batch.draw(whitePixel, x, y, size, size);
            batch.setColor(XP_INNER);
            batch.draw(whitePixel, x + 2f, y + 2f, size - 4f, size - 4f);
        }
    }

    private void drawEnemies(SpriteBatch batch, Texture whitePixel, GameAssets assets, GameSession session) {
        for (Enemy enemy : session.getEnemies()) {
            TextureRegion sprite = assets.getEnemySprite(enemy.archetype);
            Color primary = new Color(enemy.archetype.primaryColor);
            Color secondary = new Color(enemy.archetype.secondaryColor);
            if (enemy.hitFlashTime > 0f) {
                float lerp = Math.min(1f, enemy.hitFlashTime);
                primary.lerp(Color.WHITE, 0.5f * lerp);
                secondary.lerp(Color.WHITE, 0.65f * lerp);
            }
            if (enemy.chilledTime > 0f) {
                float chill = Math.min(1f, enemy.chilledTime * 1.8f);
                primary.lerp(FROST_TINT, 0.35f * chill);
                secondary.lerp(FROST_TINT, 0.50f * chill);
            }

            if (sprite != null) {
                float width;
                float height;
                if (enemy.archetype.robotic) {
                    float aspect = (float) sprite.getRegionWidth() / (float) sprite.getRegionHeight();
                    height = enemy.archetype.radius * (enemy.archetype.elite ? 2.85f : 2.45f);
                    width = height * aspect;
                } else {
                    float drawScale = enemy.archetype.elite ? 3.2f : 2.5f;
                    width = sprite.getRegionWidth() * drawScale;
                    height = sprite.getRegionHeight() * drawScale;
                }
                float x = enemy.position.x - width * 0.5f;
                float y = enemy.position.y - height * 0.5f;

                batch.setColor(SHADOW);
                batch.draw(whitePixel, enemy.position.x - width * 0.25f, enemy.position.y - height * 0.32f, width * 0.5f, 8f);
                batch.setColor(primary);
                batch.draw(sprite, x, y, width, height);

                if (enemy.archetype.elite) {
                    batch.setColor(enemy.archetype.accentColor);
                    batch.draw(whitePixel, x - 4f, y - 4f, width + 8f, 2f);
                }
            } else {
                float width = enemy.archetype.radius * (enemy.archetype.robotic ? 2.15f : 2.25f);
                float height = enemy.archetype.radius * (enemy.archetype.robotic ? 1.65f : 1.45f);
                float x = enemy.position.x - width * 0.5f;
                float y = enemy.position.y - height * 0.55f;

                batch.setColor(SHADOW);
                batch.draw(whitePixel, x + 4f, y - 6f, width - 8f, 8f);

                batch.setColor(primary);
                batch.draw(whitePixel, x, y, width, height);

                batch.setColor(secondary);
                batch.draw(whitePixel, x + 4f, y + 4f, width - 8f, height - 8f);

                batch.setColor(enemy.archetype.accentColor);
                if (enemy.archetype.robotic) {
                    batch.draw(whitePixel, x + 4f, y + height - 5f, width - 8f, 3f);
                    batch.draw(whitePixel, x + width * 0.25f, y + height * 0.48f, width * 0.5f, 3f);
                } else {
                    batch.draw(whitePixel, x + 4f, y + height - 2f, 4f, 8f);
                    batch.draw(whitePixel, x + width - 8f, y + height - 2f, 4f, 8f);
                }

                if (enemy.archetype.elite) {
                    batch.setColor(enemy.archetype.accentColor);
                    batch.draw(whitePixel, x - 4f, y - 4f, width + 8f, 2f);
                }
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
            float alpha = Math.min(1f, patch.remainingDuration / patch.duration);
            float size = patch.radius * 2f;
            float x = patch.position.x - patch.radius;
            float y = patch.position.y - patch.radius;

            batch.setColor(0.45f, 0.86f, 1f, 0.10f * alpha);
            batch.draw(whitePixel, x - 4f, y - 4f, size + 8f, size + 8f);

            batch.setColor(0.58f, 0.92f, 1f, 0.30f * alpha);
            batch.draw(sigil, x, y, size * 0.5f, size * 0.5f, size, size, 1f, 1f, -patch.animationTime * 22f);

            batch.setColor(0.75f, 0.96f, 1f, 0.32f * alpha);
            batch.draw(swirl, x + 6f, y + 6f, (size - 12f) * 0.5f, (size - 12f) * 0.5f, size - 12f, size - 12f, 1f, 1f,
                patch.animationTime * 36f);

            batch.setColor(0.84f, 0.98f, 1f, 0.22f * alpha);
            batch.draw(trail, x - 8f, patch.position.y - 10f, size + 16f, 20f);
            batch.draw(streak, patch.position.x - 10f, y - 8f, 20f, size + 16f);

            float burstSize = size * 0.42f;
            batch.setColor(1f, 1f, 1f, 0.35f * alpha);
            batch.draw(burst, patch.position.x - burstSize * 0.5f, patch.position.y - burstSize * 0.5f, burstSize * 0.5f,
                burstSize * 0.5f, burstSize, burstSize, 1f, 1f, patch.animationTime * 48f);
        }
    }

    private void drawProjectiles(SpriteBatch batch, Texture whitePixel, GameAssets assets, GameSession session) {
        for (Projectile projectile : session.getProjectiles()) {
            if (projectile.weaponType == WeaponType.STONE_SPRAY) {
                drawMageProjectile(batch, assets, projectile);
                continue;
            }
            if (projectile.weaponType == WeaponType.BONE_DART) {
                drawSwordSlash(batch, whitePixel, assets, projectile);
                continue;
            }
            if (projectile.weaponType == WeaponType.FROST_BOMB) {
                drawFrostBomb(batch, whitePixel, assets, projectile);
                continue;
            }

            float size = projectile.radius * 2f;
            float x = projectile.position.x - projectile.radius;
            float y = projectile.position.y - projectile.radius;
            Color outer = PROJECTILE_HAIRBALL;
            Color inner = PROJECTILE_HAIRBALL_CORE;
            if (projectile.weaponType == WeaponType.STONE_SPRAY) {
                outer = PROJECTILE_STONE;
                inner = PROJECTILE_STONE_CORE;
            } else if (projectile.weaponType == WeaponType.BONE_DART) {
                outer = PROJECTILE_BONE;
                inner = PROJECTILE_BONE_CORE;
            } else if (projectile.weaponType == WeaponType.ORBIT_CLAWS) {
                outer = PROJECTILE_ORBIT;
                inner = PROJECTILE_ORBIT_CORE;
            }

            batch.setColor(outer);
            batch.draw(whitePixel, x, y, size, size);
            batch.setColor(inner);
            batch.draw(whitePixel, x + 2f, y + 2f, size - 4f, size - 4f);
        }
    }

    private void drawMageProjectile(SpriteBatch batch, GameAssets assets, Projectile projectile) {
        Texture orb = assets.getMagicOrb();
        TextureRegion focus = assets.getMageFocusIcon(projectile.weaponLevel);
        TextureRegion comet = assets.getFireCometProjectile();
        TextureRegion burst = assets.getFireCometBurst();
        TextureRegion sigil = assets.getFireCometSigil();
        TextureRegion streak = assets.getFireCometStreak();
        Color magicColor = getMagicColor(projectile.weaponLevel);
        float angle = projectile.velocity.angleDeg() - 45f;
        float size = Math.max(22f, projectile.radius * 4.2f);
        float auraSize = size + projectile.splashRadius * 0.35f;
        float x = projectile.position.x - size * 0.5f;
        float y = projectile.position.y - size * 0.5f;
        float auraX = projectile.position.x - auraSize * 0.5f;
        float auraY = projectile.position.y - auraSize * 0.5f;
        float sigilSize = auraSize + 10f;
        float trailLength = size + 20f;

        batch.setColor(magicColor.r, magicColor.g, magicColor.b, 0.20f);
        batch.draw(sigil, projectile.position.x - sigilSize * 0.5f, projectile.position.y - sigilSize * 0.5f,
            sigilSize * 0.5f, sigilSize * 0.5f, sigilSize, sigilSize, 1f, 1f, -projectile.traveledDistance * 0.45f);

        batch.setColor(magicColor.r, magicColor.g, magicColor.b, 0.34f);
        batch.draw(streak, projectile.position.x - trailLength * 0.64f, projectile.position.y - size * 0.40f,
            trailLength * 0.16f, size * 0.40f, trailLength, size * 0.80f, 1f, 1f, angle);

        batch.setColor(magicColor.r, magicColor.g, magicColor.b, 0.24f);
        batch.draw(orb, auraX, auraY, auraSize, auraSize);

        batch.setColor(magicColor.r, magicColor.g, magicColor.b, 0.92f);
        batch.draw(comet, x - 2f, y - 2f, (size + 4f) * 0.5f, (size + 4f) * 0.5f, size + 4f, size + 4f, 1f, 1f,
            angle + projectile.traveledDistance * 0.10f);

        batch.setColor(1f, 1f, 1f, 0.72f);
        batch.draw(orb, x + 4f, y + 4f, size - 8f, size - 8f);

        float burstSize = size * 0.62f;
        batch.setColor(1f, 1f, 1f, 0.76f);
        batch.draw(burst, projectile.position.x - burstSize * 0.5f, projectile.position.y - burstSize * 0.5f,
            burstSize * 0.5f, burstSize * 0.5f, burstSize, burstSize, 1f, 1f, projectile.traveledDistance * 0.22f);

        float focusSize = size * 0.55f;
        batch.setColor(1f, 1f, 1f, 0.86f);
        batch.draw(focus, projectile.position.x - focusSize * 0.5f, projectile.position.y - focusSize * 0.5f,
            focusSize * 0.5f, focusSize * 0.5f, focusSize, focusSize, 1f, 1f, projectile.traveledDistance * 1.8f);
    }

    private void drawSwordSlash(SpriteBatch batch, Texture whitePixel, GameAssets assets, Projectile projectile) {
        TextureRegion sprite = assets.getSwordIcon(projectile.weaponLevel);
        float angle = projectile.velocity.angleDeg() - 45f;
        float size = Math.max(30f, projectile.radius * 5f);
        float x = projectile.position.x - size * 0.5f;
        float y = projectile.position.y - size * 0.5f;
        float directionX = MathUtils.cosDeg(projectile.velocity.angleDeg());
        float directionY = MathUtils.sinDeg(projectile.velocity.angleDeg());

        batch.setColor(1f, 1f, 1f, 0.15f);
        batch.draw(sprite, x - directionX * 10f, y - directionY * 10f, size * 0.30f, size * 0.22f, size, size, 1f, 1f, angle);
        batch.setColor(1f, 1f, 1f, 0.30f);
        batch.draw(sprite, x - directionX * 5f, y - directionY * 5f, size * 0.30f, size * 0.22f, size, size, 1f, 1f, angle);

        batch.setColor(1f, 1f, 1f, 0.18f);
        batch.draw(whitePixel, projectile.position.x - directionX * 14f, projectile.position.y - directionY * 14f, 28f, 4f);

        batch.setColor(Color.WHITE);
        batch.draw(sprite, x, y, size * 0.30f, size * 0.22f, size, size, 1f, 1f, angle);
    }

    private void drawFrostBomb(SpriteBatch batch, Texture whitePixel, GameAssets assets, Projectile projectile) {
        TextureRegion bomb = assets.getFrostBombProjectile();
        TextureRegion burst = assets.getFrostBombBurst();
        TextureRegion streak = assets.getFrostBombStreak();
        float angle = projectile.velocity.angleDeg() - 45f;
        float size = Math.max(26f, projectile.radius * 4.6f);
        float x = projectile.position.x - size * 0.5f;
        float y = projectile.position.y - size * 0.5f;
        float trailLength = Math.max(24f, projectile.splashRadius * 0.55f);
        float directionX = MathUtils.cosDeg(projectile.velocity.angleDeg());
        float directionY = MathUtils.sinDeg(projectile.velocity.angleDeg());

        batch.setColor(0.70f, 0.94f, 1f, 0.24f);
        batch.draw(streak, x - directionX * 16f, y - directionY * 16f, size * 0.5f, size * 0.5f, trailLength, size * 0.75f,
            1f, 1f, angle);

        batch.setColor(0.74f, 0.97f, 1f, 0.82f);
        batch.draw(bomb, x, y, size * 0.5f, size * 0.5f, size, size, 1f, 1f, angle + projectile.traveledDistance * 0.18f);

        float burstSize = size * 0.52f;
        batch.setColor(1f, 1f, 1f, 0.66f);
        batch.draw(burst, projectile.position.x - burstSize * 0.5f, projectile.position.y - burstSize * 0.5f, burstSize * 0.5f,
            burstSize * 0.5f, burstSize, burstSize, 1f, 1f, -projectile.traveledDistance * 0.25f);

        batch.setColor(0.56f, 0.90f, 1f, 0.18f);
        batch.draw(whitePixel, x - 6f, y - 6f, size + 12f, size + 12f);
    }

    private void drawOrbitBlades(SpriteBatch batch, Texture whitePixel, GameSession session) {
        for (OrbitBlade blade : session.getOrbitBlades()) {
            float size = blade.size * 2f;
            float x = blade.position.x - blade.size;
            float y = blade.position.y - blade.size;
            batch.setColor(BLADE_OUTER);
            batch.draw(whitePixel, x, y, size, size);
            batch.setColor(BLADE_INNER);
            batch.draw(whitePixel, x + 3f, y + 3f, size - 6f, size - 6f);
        }
    }

    private void drawOrbitingSwords(SpriteBatch batch, Texture whitePixel, GameAssets assets, GameSession session) {
        int weaponLevel = session.getWeaponLevel(WeaponType.BONE_DART);
        if (weaponLevel <= 0) {
            return;
        }

        Player player = session.getPlayer();
        TextureRegion sprite = assets.getSwordIcon(weaponLevel);
        float size = ORBITING_SWORD_SIZE + Math.min(12f, weaponLevel * 2f);
        float orbitRadius = 34f + weaponLevel * 6f;
        float baseAngle = player.animationTime * (120f + weaponLevel * 18f);
        int swordCount = getOrbitingSwordCount(weaponLevel);

        for (int index = 0; index < swordCount; index++) {
            float angle = baseAngle + 360f * index / swordCount;
            float centerX = player.position.x + MathUtils.cosDeg(angle) * orbitRadius;
            float centerY = player.position.y + 6f + MathUtils.sinDeg(angle) * orbitRadius;
            drawOrbitingSword(batch, whitePixel, sprite, centerX, centerY, size, angle - 45f);
        }
    }

    private void drawOrbitingSword(SpriteBatch batch, Texture whitePixel, TextureRegion sprite, float centerX, float centerY,
                                   float size, float rotation) {
        float x = centerX - size * 0.5f;
        float y = centerY - size * 0.5f;
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

    private void drawPlayer(SpriteBatch batch, GameAssets assets, Texture whitePixel, Player player) {
        Animation<TextureRegion> animation = assets.getCatAnimation(player.anim == CatAnim.RUN ? CatAnim.RUN : CatAnim.IDLE);
        TextureRegion frame = animation.getKeyFrame(player.animationTime);
        float drawX = player.position.x - PLAYER_DRAW_SIZE * 0.5f;
        float drawY = player.position.y - PLAYER_DRAW_SIZE * 0.42f;

        batch.setColor(SHADOW);
        batch.draw(whitePixel, player.position.x - 18f, player.position.y - 24f, 36f, 10f);

        float flash = Math.min(1f, player.hitFlashTime);
        batch.setColor(1f, 1f - 0.18f * flash, 1f - 0.18f * flash, 1f);
        if (player.facingLeft) {
            batch.draw(frame, drawX + PLAYER_DRAW_SIZE, drawY, -PLAYER_DRAW_SIZE, PLAYER_DRAW_SIZE);
        } else {
            batch.draw(frame, drawX, drawY, PLAYER_DRAW_SIZE, PLAYER_DRAW_SIZE);
        }
    }
}
