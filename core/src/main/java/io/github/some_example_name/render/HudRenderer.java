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
    private static final Color PANEL_SHADOW = new Color(0f, 0f, 0f, 0.20f);
    private static final Color BAR_BG = new Color(0.10f, 0.10f, 0.11f, 0.96f);
    private static final Color HP_BAR = new Color(0.92f, 0.38f, 0.34f, 1f);
    private static final Color XP_BAR = new Color(0.23f, 0.84f, 0.72f, 1f);
    private static final Color TEXT_MUTED = new Color(0.82f, 0.86f, 0.90f, 0.92f);
    private static final Color ROW_TINT = new Color(1f, 1f, 1f, 0.06f);

    public void draw(SpriteBatch batch, BitmapFont font, GlyphLayout glyphLayout, Texture whitePixel, GameAssets assets,
                     GameSession session) {
        // HUD découpé en trois blocs : état joueur, progression du run, inventaire/passifs.
        Color panelColor = session.getStage().panelColor;
        Color accentColor = session.getStage().accentColor;

        float leftX = 18f;
        float leftY = StageDefinition.WORLD_HEIGHT - 158f;
        float leftWidth = 344f;
        float centerX = 378f;
        float centerY = StageDefinition.WORLD_HEIGHT - 108f;
        float centerWidth = 540f;
        float rightX = StageDefinition.WORLD_WIDTH - 338f;
        float rightY = StageDefinition.WORLD_HEIGHT - 214f;
        float rightWidth = 320f;

        drawPanel(batch, whitePixel, leftX, leftY, leftWidth, 142f, panelColor, accentColor);
        drawPanel(batch, whitePixel, centerX, centerY, centerWidth, 92f, panelColor, accentColor);
        drawPanel(batch, whitePixel, rightX, rightY, rightWidth, 198f, panelColor, accentColor);

        font.setColor(accentColor);
        font.draw(batch, session.getStage().displayName, leftX + 16f, StageDefinition.WORLD_HEIGHT - 30f);
        font.setColor(TEXT_MUTED);
        font.draw(batch, session.getStage().subtitle, leftX + 16f, StageDefinition.WORLD_HEIGHT - 54f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Niveau " + session.getLevel(), leftX + 16f, StageDefinition.WORLD_HEIGHT - 82f);

        drawFramedBar(batch, whitePixel, leftX + 16f, StageDefinition.WORLD_HEIGHT - 116f, 220f, 16f,
            session.getPlayer().health / session.getPlayer().maxHealth, HP_BAR, accentColor);
        drawFramedBar(batch, whitePixel, leftX + 16f, StageDefinition.WORLD_HEIGHT - 142f, 220f, 14f,
            session.getCurrentXp() / session.getXpToNextLevel(), XP_BAR, accentColor);
        font.setColor(Color.WHITE);
        font.draw(batch, "PV " + Math.round(session.getPlayer().health) + " / " + Math.round(session.getPlayer().maxHealth),
            leftX + 246f, StageDefinition.WORLD_HEIGHT - 103f);
        font.setColor(TEXT_MUTED);
        font.draw(batch, "XP suivante", leftX + 246f, StageDefinition.WORLD_HEIGHT - 128f);

        float timeLeft = Math.max(0f, session.getStage().durationSeconds - session.getSurvivalTime());
        float progress = session.getSurvivalTime() / session.getStage().durationSeconds;
        font.setColor(Color.WHITE);
        font.draw(batch, "Run", centerX + 16f, StageDefinition.WORLD_HEIGHT - 32f);
        font.setColor(TEXT_MUTED);
        font.draw(batch, "Temps restant " + String.format("%.1f", timeLeft) + "s", centerX + 16f, StageDefinition.WORLD_HEIGHT - 58f);
        drawFramedBar(batch, whitePixel, centerX + 16f, StageDefinition.WORLD_HEIGHT - 92f, centerWidth - 32f, 18f,
            progress, accentColor, accentColor);
        font.setColor(Color.WHITE);
        font.draw(batch, String.format("%.0f%%", Math.max(0f, Math.min(100f, progress * 100f))), centerX + centerWidth - 72f,
            StageDefinition.WORLD_HEIGHT - 58f);

        float rowY = StageDefinition.WORLD_HEIGHT - 42f;
        font.setColor(accentColor);
        font.draw(batch, "Armes", rightX + 18f, rowY);
        rowY -= 28f;
        for (Weapon weapon : session.getOwnedWeapons()) {
            drawRect(batch, whitePixel, rightX + 14f, rowY - 18f, rightWidth - 28f, 22f, ROW_TINT);
            batch.setColor(Color.WHITE);
            batch.draw(assets.getWeaponIcon(weapon.getType(), weapon.getLevel()), rightX + 18f, rowY - 18f, 18f, 18f);
            font.setColor(Color.WHITE);
            font.draw(batch, weapon.getDisplayName() + " " + weapon.getLevel(), rightX + 44f, rowY);
            rowY -= 24f;
        }

        rowY -= 8f;
        font.setColor(accentColor);
        font.draw(batch, "Passifs", rightX + 18f, rowY);
        rowY -= 24f;
        rowY = drawPassive(batch, font, whitePixel, assets, rightX + 14f, rightWidth - 28f, rowY, "Vitesse",
            session.getPassiveLevel(PassiveType.SPEED), PassiveType.SPEED);
        rowY = drawPassive(batch, font, whitePixel, assets, rightX + 14f, rightWidth - 28f, rowY, "Dégâts",
            session.getPassiveLevel(PassiveType.DAMAGE), PassiveType.DAMAGE);
        rowY = drawPassive(batch, font, whitePixel, assets, rightX + 14f, rightWidth - 28f, rowY, "Cadence",
            session.getPassiveLevel(PassiveType.ATTACK_SPEED), PassiveType.ATTACK_SPEED);
        rowY = drawPassive(batch, font, whitePixel, assets, rightX + 14f, rightWidth - 28f, rowY, "Aimant",
            session.getPassiveLevel(PassiveType.MAGNET), PassiveType.MAGNET);
        drawPassive(batch, font, whitePixel, assets, rightX + 14f, rightWidth - 28f, rowY, "Vitalité",
            session.getPassiveLevel(PassiveType.VITALITY), PassiveType.VITALITY);

        drawRect(batch, whitePixel, StageDefinition.WORLD_WIDTH - 278f, 12f, 260f, 22f, panelColor);
        batch.setColor(accentColor);
        batch.draw(whitePixel, StageDefinition.WORLD_WIDTH - 278f, 32f, 260f, 2f);
        glyphLayout.setText(font, "Échap pause  |  1-2-3 upgrades");
        font.setColor(Color.WHITE);
        font.draw(batch, glyphLayout, StageDefinition.WORLD_WIDTH - glyphLayout.width - 26f, 28f);
        batch.setColor(Color.WHITE);
    }

    private float drawPassive(SpriteBatch batch, BitmapFont font, Texture whitePixel, GameAssets assets, float x,
                              float width, float y, String label, int level, PassiveType type) {
        drawRect(batch, whitePixel, x, y - 18f, width, 22f, ROW_TINT);
        batch.setColor(Color.WHITE);
        batch.draw(assets.getPassiveIcon(type), x + 4f, y - 18f, 18f, 18f);
        font.setColor(Color.WHITE);
        font.draw(batch, label + " " + level, x + 30f, y);
        return y - 22f;
    }

    private void drawFramedBar(SpriteBatch batch, Texture whitePixel, float x, float y, float width, float height,
                               float ratio, Color fillColor, Color accentColor) {
        // Les barres ont un léger cadre pour mieux ressortir sur les maps chargées.
        drawRect(batch, whitePixel, x - 2f, y - 2f, width + 4f, height + 4f, PANEL_SHADOW);
        drawRect(batch, whitePixel, x, y, width, height, BAR_BG);
        drawRect(batch, whitePixel, x, y + height - 2f, width, 2f, new Color(accentColor.r, accentColor.g, accentColor.b, 0.25f));
        drawRect(batch, whitePixel, x + 2f, y + 2f, (width - 4f) * Math.max(0f, Math.min(1f, ratio)), height - 4f, fillColor);
    }

    private void drawPanel(SpriteBatch batch, Texture whitePixel, float x, float y, float width, float height, Color panelColor,
                           Color accentColor) {
        drawRect(batch, whitePixel, x + 4f, y - 4f, width, height, PANEL_SHADOW);
        drawRect(batch, whitePixel, x, y, width, height, panelColor);
        drawRect(batch, whitePixel, x, y + height - 3f, width, 3f, accentColor);
        drawRect(batch, whitePixel, x + 8f, y + 8f, width - 16f, height - 20f, new Color(1f, 1f, 1f, 0.03f));
    }

    private void drawRect(SpriteBatch batch, Texture whitePixel, float x, float y, float width, float height, Color color) {
        batch.setColor(color);
        batch.draw(whitePixel, x, y, width, height);
        batch.setColor(Color.WHITE);
    }
}
