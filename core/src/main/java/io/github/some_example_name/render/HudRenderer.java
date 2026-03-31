package io.github.some_example_name.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.context.GameAssets;
import io.github.some_example_name.game.PassiveType;
import io.github.some_example_name.game.session.GameSession;
import io.github.some_example_name.game.stage.StageDefinition;
import io.github.some_example_name.game.weapon.Weapon;

public class HudRenderer {
    private static final Color PANEL = new Color(0f, 0f, 0f, 0.42f);
    private static final Color BAR_BG = new Color(0.10f, 0.10f, 0.11f, 1f);
    private static final Color HP_BAR = new Color(0.92f, 0.38f, 0.34f, 1f);
    private static final Color XP_BAR = new Color(0.23f, 0.84f, 0.72f, 1f);

    public void draw(SpriteBatch batch, BitmapFont font, GlyphLayout glyphLayout, Texture whitePixel, GameAssets assets,
                     GameSession session) {
        drawRect(batch, whitePixel, 16f, StageDefinition.WORLD_HEIGHT - 148f, 320f, 132f, PANEL);
        drawRect(batch, whitePixel, 352f, StageDefinition.WORLD_HEIGHT - 84f, 576f, 30f, PANEL);
        drawRect(batch, whitePixel, StageDefinition.WORLD_WIDTH - 332f, StageDefinition.WORLD_HEIGHT - 180f, 316f, 164f, PANEL);

        drawBar(batch, whitePixel, 30f, StageDefinition.WORLD_HEIGHT - 79f, 240f, 16f,
            session.getPlayer().health / session.getPlayer().maxHealth, HP_BAR);
        drawBar(batch, whitePixel, 30f, StageDefinition.WORLD_HEIGHT - 109f, 240f, 16f,
            session.getCurrentXp() / session.getXpToNextLevel(), XP_BAR);

        font.setColor(Color.WHITE);
        font.draw(batch, session.getStage().displayName, 30f, StageDefinition.WORLD_HEIGHT - 28f);
        font.draw(batch, "Niveau " + session.getLevel(), 30f, StageDefinition.WORLD_HEIGHT - 52f);
        font.draw(batch, "PV " + Math.round(session.getPlayer().health) + " / " + Math.round(session.getPlayer().maxHealth),
            278f, StageDefinition.WORLD_HEIGHT - 67f);
        font.draw(batch, "XP", 278f, StageDefinition.WORLD_HEIGHT - 97f);

        float timeLeft = Math.max(0f, session.getStage().durationSeconds - session.getSurvivalTime());
        font.draw(batch, "Temps restant " + String.format("%.1f", timeLeft) + "s", 366f, StageDefinition.WORLD_HEIGHT - 63f);
        font.draw(batch, "Époque " + session.getStage().subtitle, 366f, StageDefinition.WORLD_HEIGHT - 35f);

        float rowY = StageDefinition.WORLD_HEIGHT - 42f;
        font.draw(batch, "Armes", StageDefinition.WORLD_WIDTH - 314f, rowY);
        rowY -= 26f;
        for (Weapon weapon : session.getOwnedWeapons()) {
            batch.setColor(Color.WHITE);
            batch.draw(assets.getWeaponIcon(weapon.getType(), weapon.getLevel()), StageDefinition.WORLD_WIDTH - 314f, rowY - 18f, 18f, 18f);
            font.draw(batch, weapon.getDisplayName() + " " + weapon.getLevel(), StageDefinition.WORLD_WIDTH - 288f, rowY);
            rowY -= 24f;
        }

        rowY -= 8f;
        font.draw(batch, "Passifs", StageDefinition.WORLD_WIDTH - 314f, rowY);
        rowY -= 24f;
        rowY = drawPassive(batch, font, assets, rowY, "Vitesse", session.getPassiveLevel(PassiveType.SPEED), PassiveType.SPEED);
        rowY = drawPassive(batch, font, assets, rowY, "Dégâts", session.getPassiveLevel(PassiveType.DAMAGE), PassiveType.DAMAGE);
        rowY = drawPassive(batch, font, assets, rowY, "Cadence", session.getPassiveLevel(PassiveType.ATTACK_SPEED), PassiveType.ATTACK_SPEED);
        rowY = drawPassive(batch, font, assets, rowY, "Aimant", session.getPassiveLevel(PassiveType.MAGNET), PassiveType.MAGNET);
        drawPassive(batch, font, assets, rowY, "Vitalité", session.getPassiveLevel(PassiveType.VITALITY), PassiveType.VITALITY);

        glyphLayout.setText(font, "Échap pause  |  1-2-3 upgrades");
        font.draw(batch, glyphLayout, StageDefinition.WORLD_WIDTH - glyphLayout.width - 18f, 24f);
        batch.setColor(Color.WHITE);
    }

    private float drawPassive(SpriteBatch batch, BitmapFont font, GameAssets assets, float y, String label, int level,
                              PassiveType type) {
        batch.setColor(Color.WHITE);
        batch.draw(assets.getPassiveIcon(type), StageDefinition.WORLD_WIDTH - 314f, y - 18f, 18f, 18f);
        font.draw(batch, label + " " + level, StageDefinition.WORLD_WIDTH - 288f, y);
        return y - 22f;
    }

    private void drawBar(SpriteBatch batch, Texture whitePixel, float x, float y, float width, float height,
                         float ratio, Color fillColor) {
        drawRect(batch, whitePixel, x, y, width, height, BAR_BG);
        drawRect(batch, whitePixel, x + 2f, y + 2f, (width - 4f) * Math.max(0f, Math.min(1f, ratio)), height - 4f, fillColor);
    }

    private void drawRect(SpriteBatch batch, Texture whitePixel, float x, float y, float width, float height, Color color) {
        batch.setColor(color);
        batch.draw(whitePixel, x, y, width, height);
        batch.setColor(Color.WHITE);
    }
}
