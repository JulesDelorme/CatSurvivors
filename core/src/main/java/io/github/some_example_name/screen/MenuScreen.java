package io.github.some_example_name.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.context.GameContext;
import io.github.some_example_name.game.stage.StageDefinition;
import io.github.some_example_name.game.stage.StageId;
import io.github.some_example_name.ui.UiButton;

public class MenuScreen extends ScreenAdapter {
    private static final float UI_WIDTH = 1280f;
    private static final float UI_HEIGHT = 720f;
    private static final Color PANEL = new Color(0.07f, 0.08f, 0.10f, 0.96f);
    private static final Color CARD = new Color(0.12f, 0.14f, 0.18f, 0.98f);
    private static final Color CARD_LOCKED = new Color(0.08f, 0.09f, 0.12f, 0.94f);
    private static final Color BUTTON = new Color(0.19f, 0.54f, 0.48f, 1f);
    private static final Color BUTTON_DISABLED = new Color(0.28f, 0.31f, 0.36f, 1f);

    private final GameContext context;
    private final SpriteBatch batch;
    private final Texture whitePixel;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final Viewport viewport = new FitViewport(UI_WIDTH, UI_HEIGHT, camera);
    private final Vector3 pointer = new Vector3();
    private final UiButton prehistoryButton = new UiButton(150f, 160f, 420f, 280f, "Préhistoire", "Partie d'introduction jouable", true);
    private final UiButton futureButton;

    public MenuScreen(GameContext context) {
        this.context = context;
        batch = context.batch;
        whitePixel = context.assets.getWhitePixel();
        futureButton = new UiButton(710f, 160f, 420f, 280f, "Futur / Robots",
            context.flow().isStageUnlocked(StageId.FUTURE) ? "Débloqué" : "Verrouillé: termine la Préhistoire",
            context.flow().isStageUnlocked(StageId.FUTURE));
    }

    @Override
    public void render(float delta) {
        handleInput();
        StageDefinition prehistory = context.getStage(StageId.PREHISTORY);
        StageDefinition future = context.getStage(StageId.FUTURE);

        Gdx.gl.glClearColor(0.03f, 0.04f, 0.06f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        drawRect(0f, 0f, UI_WIDTH, UI_HEIGHT, new Color(0.03f, 0.04f, 0.06f, 1f));
        drawGlow(194f, 590f, 340f, prehistory.accentColor, 0.16f);
        drawGlow(1080f, 168f, 320f, future.accentColor, 0.14f);
        drawRect(58f, 66f, 1172f, 580f, new Color(0f, 0f, 0f, 0.22f));
        drawRect(54f, 70f, 1172f, 580f, PANEL);
        drawRect(80f, 520f, 1120f, 96f, new Color(0.12f, 0.17f, 0.19f, 1f));
        drawRect(80f, 613f, 1120f, 3f, new Color(0.45f, 0.88f, 0.86f, 1f));

        context.font.setColor(new Color(0.96f, 0.95f, 0.82f, 1f));
        context.font.draw(batch, "Cat Survivors", 96f, 586f);
        context.font.setColor(Color.WHITE);
        context.font.draw(batch, "Survis à chaque époque pour débloquer la suivante.", 96f, 552f);
        context.font.draw(batch, "Déplacement: WASD / ZQSD / flèches  |  Pause: Échap  |  Upgrades: 1-2-3 ou clic",
            96f, 528f);

        drawStageCard(prehistoryButton, StageId.PREHISTORY, true);
        drawStageCard(futureButton, StageId.FUTURE, context.flow().isStageUnlocked(StageId.FUTURE));

        context.font.setColor(Color.WHITE);
        context.font.draw(batch, "Raccourcis: [1] Préhistoire  [2] Futur si débloqué", 96f, 112f);

        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            context.flow().startStage(StageId.PREHISTORY);
            return;
        }

        if (context.flow().isStageUnlocked(StageId.FUTURE)
            && Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            context.flow().startStage(StageId.FUTURE);
            return;
        }

        if (!Gdx.input.justTouched()) {
            return;
        }

        pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        viewport.unproject(pointer);
        if (prehistoryButton.contains(pointer.x, pointer.y)) {
            context.flow().startStage(StageId.PREHISTORY);
        } else if (futureButton.contains(pointer.x, pointer.y)) {
            context.flow().startStage(StageId.FUTURE);
        }
    }

    private void drawStageCard(UiButton button, StageId stageId, boolean enabled) {
        StageDefinition stage = context.getStage(stageId);
        Color cardColor = enabled ? CARD : CARD_LOCKED;
        drawRect(button.bounds.x + 4f, button.bounds.y - 4f, button.bounds.width, button.bounds.height, new Color(0f, 0f, 0f, 0.22f));
        drawGlow(button.bounds.x + button.bounds.width * 0.5f, button.bounds.y + button.bounds.height * 0.5f, 240f, stage.accentColor,
            enabled ? 0.12f : 0.05f);
        drawRect(button.bounds.x, button.bounds.y, button.bounds.width, button.bounds.height, cardColor);
        drawRect(button.bounds.x, button.bounds.y + button.bounds.height - 4f, button.bounds.width, 4f, stage.accentColor);
        drawTilesPreview(button.bounds, stageId);
        drawRect(button.bounds.x + 18f, button.bounds.y + 18f, button.bounds.width - 36f, 70f,
            enabled ? BUTTON : BUTTON_DISABLED);

        context.font.setColor(Color.WHITE);
        context.font.draw(batch, button.label, button.bounds.x + 28f, button.bounds.y + 62f);
        context.font.draw(batch, button.subLabel, button.bounds.x + 28f, button.bounds.y + 38f);

        float bodyY = button.bounds.y + button.bounds.height - 126f;
        if (stageId == StageId.PREHISTORY) {
            context.font.draw(batch, "Map 1", button.bounds.x + 24f, bodyY);
            context.font.draw(batch, "Pression douce, ennemis lents, vague finale primitive.", button.bounds.x + 24f, bodyY - 28f);
            context.font.draw(batch, "Parfaite pour découvrir les armes et l'XP.", button.bounds.x + 24f, bodyY - 54f);
        } else {
            context.font.draw(batch, "Map 2", button.bounds.x + 24f, bodyY);
            context.font.draw(batch, "Arène techno, robots agressifs, pression accrue.", button.bounds.x + 24f, bodyY - 28f);
            context.font.draw(batch, enabled ? "Déjà disponible dans le menu." : "Se débloque après une victoire en Préhistoire.",
                button.bounds.x + 24f, bodyY - 54f);
        }

        if (!enabled) {
            drawRect(button.bounds.x + button.bounds.width - 82f, button.bounds.y + button.bounds.height - 72f, 48f, 48f,
                new Color(0f, 0f, 0f, 0.35f));
            context.font.draw(batch, "X", button.bounds.x + button.bounds.width - 64f, button.bounds.y + button.bounds.height - 40f);
        }
    }

    private void drawTilesPreview(Rectangle bounds, StageId stageId) {
        TextureRegion[] tiles = context.assets.getTiles(context.getStage(stageId).tilesetType);
        int[] indices = stageId == StageId.PREHISTORY
            ? new int[] {0, 1, 2, 6, 12, 14}
            : new int[] {0, 1, 2, 6, 11, 13};
        for (int index = 0; index < indices.length; index++) {
            batch.setColor(Color.WHITE);
            batch.draw(tiles[indices[index]], bounds.x + 22f + index * 62f, bounds.y + bounds.height - 70f, 52f, 52f);
        }
    }

    private void drawRect(float x, float y, float width, float height, Color color) {
        batch.setColor(color);
        batch.draw(whitePixel, x, y, width, height);
        batch.setColor(Color.WHITE);
    }

    private void drawGlow(float centerX, float centerY, float size, Color color, float alpha) {
        batch.setColor(color.r, color.g, color.b, alpha);
        batch.draw(context.assets.getSoftGlow(), centerX - size * 0.5f, centerY - size * 0.5f, size, size);
        batch.setColor(Color.WHITE);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
