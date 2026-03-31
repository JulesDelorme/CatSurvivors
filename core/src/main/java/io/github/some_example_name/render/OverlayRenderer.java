package io.github.some_example_name.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.context.GameAssets;
import io.github.some_example_name.game.session.GameSession;
import io.github.some_example_name.game.stage.StageDefinition;
import io.github.some_example_name.game.upgrade.UpgradeCategory;
import io.github.some_example_name.game.upgrade.UpgradeChoice;

public class OverlayRenderer {
    private static final Color OVERLAY = new Color(0f, 0f, 0f, 0.72f);
    private static final Color PANEL = new Color(0.08f, 0.09f, 0.12f, 0.96f);
    private static final Color CARD = new Color(0.13f, 0.15f, 0.18f, 0.98f);
    private static final Color CARD_HOVER = new Color(0.19f, 0.22f, 0.28f, 0.99f);

    public void layoutChoiceBounds(Array<Rectangle> bounds) {
        bounds.clear();
        float panelWidth = 960f;
        float cardWidth = 280f;
        float gap = 30f;
        float panelX = (StageDefinition.WORLD_WIDTH - panelWidth) * 0.5f;
        float cardY = (StageDefinition.WORLD_HEIGHT - 300f) * 0.5f + 44f;
        for (int index = 0; index < 3; index++) {
            bounds.add(new Rectangle(panelX + 30f + index * (cardWidth + gap), cardY, cardWidth, 172f));
        }
    }

    public void drawPause(SpriteBatch batch, BitmapFont font, GlyphLayout glyphLayout, Texture whitePixel) {
        drawRect(batch, whitePixel, 0f, 0f, StageDefinition.WORLD_WIDTH, StageDefinition.WORLD_HEIGHT, OVERLAY);
        drawRect(batch, whitePixel, 350f, 245f, 580f, 170f, PANEL);
        drawCentered(font, glyphLayout, batch, "Pause", StageDefinition.WORLD_HEIGHT * 0.5f + 30f, new Color(0.95f, 0.95f, 1f, 1f));
        drawCentered(font, glyphLayout, batch, "Échap pour reprendre", StageDefinition.WORLD_HEIGHT * 0.5f - 6f, Color.WHITE);
        drawCentered(font, glyphLayout, batch, "WASD / ZQSD / Flèches pour bouger", StageDefinition.WORLD_HEIGHT * 0.5f - 38f, Color.WHITE);
    }

    public void drawLevelUp(SpriteBatch batch, BitmapFont font, GlyphLayout glyphLayout, Texture whitePixel, GameAssets assets,
                            GameSession session, Array<Rectangle> bounds, int hoveredIndex) {
        drawRect(batch, whitePixel, 0f, 0f, StageDefinition.WORLD_WIDTH, StageDefinition.WORLD_HEIGHT, OVERLAY);

        float panelWidth = 960f;
        float panelHeight = 300f;
        float panelX = (StageDefinition.WORLD_WIDTH - panelWidth) * 0.5f;
        float panelY = (StageDefinition.WORLD_HEIGHT - panelHeight) * 0.5f;
        drawRect(batch, whitePixel, panelX, panelY, panelWidth, panelHeight, PANEL);
        drawCentered(font, glyphLayout, batch, "Montée de niveau", panelY + panelHeight - 30f, new Color(0.95f, 0.92f, 0.75f, 1f));
        drawCentered(font, glyphLayout, batch, "Clique ou appuie sur 1, 2 ou 3", panelY + panelHeight - 58f, Color.WHITE);

        for (int index = 0; index < session.getLevelChoices().size; index++) {
            Rectangle card = bounds.get(index);
            drawRect(batch, whitePixel, card.x, card.y, card.width, card.height, hoveredIndex == index ? CARD_HOVER : CARD);

            UpgradeChoice choice = session.getLevelChoices().get(index);
            drawRect(batch, whitePixel, card.x + 10f, card.y + card.height - 34f, 32f, 22f, session.getStage().accentColor);
            font.draw(batch, Integer.toString(index + 1), card.x + 21f, card.y + card.height - 18f);

            if (choice.category == UpgradeCategory.WEAPON) {
                batch.setColor(Color.WHITE);
                batch.draw(assets.getWeaponIcon(choice.weaponType, choice.resultingLevel), card.x + 16f, card.y + 112f, 28f, 28f);
            } else {
                batch.setColor(Color.WHITE);
                batch.draw(assets.getPassiveIcon(choice.passiveType), card.x + 16f, card.y + 112f, 28f, 28f);
            }

            glyphLayout.setText(font, choice.title, new Color(1f, 0.95f, 0.80f, 1f), card.width - 58f, 1, true);
            font.setColor(new Color(1f, 0.95f, 0.80f, 1f));
            font.draw(batch, glyphLayout, card.x + 52f, card.y + 145f);

            glyphLayout.setText(font, choice.description, Color.WHITE, card.width - 26f, 1, true);
            font.setColor(Color.WHITE);
            font.draw(batch, glyphLayout, card.x + 13f, card.y + 86f);
        }
        font.setColor(Color.WHITE);
    }

    private void drawRect(SpriteBatch batch, Texture whitePixel, float x, float y, float width, float height, Color color) {
        batch.setColor(color);
        batch.draw(whitePixel, x, y, width, height);
        batch.setColor(Color.WHITE);
    }

    private void drawCentered(BitmapFont font, GlyphLayout glyphLayout, SpriteBatch batch, String text, float y, Color color) {
        glyphLayout.setText(font, text);
        font.setColor(color);
        font.draw(batch, glyphLayout, (StageDefinition.WORLD_WIDTH - glyphLayout.width) * 0.5f, y);
        font.setColor(Color.WHITE);
    }
}
