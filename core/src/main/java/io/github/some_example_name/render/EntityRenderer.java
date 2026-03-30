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

    public void draw(SpriteBatch batch, GameAssets assets, GameSession session) {
        Texture whitePixel = assets.getWhitePixel();
        drawExperience(batch, whitePixel, session);
        drawEnemies(batch, whitePixel, assets, session);
        drawProjectiles(batch, whitePixel, session);
        drawOrbitBlades(batch, whitePixel, session);
        drawPlayer(batch, assets, whitePixel, session.getPlayer());
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

            if (sprite != null) {
                float drawScale = enemy.archetype.elite ? 3.2f : (enemy.archetype.robotic ? 2.7f : 2.5f);
                float width = sprite.getRegionWidth() * drawScale;
                float height = sprite.getRegionHeight() * drawScale;
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

    private void drawProjectiles(SpriteBatch batch, Texture whitePixel, GameSession session) {
        for (Projectile projectile : session.getProjectiles()) {
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
