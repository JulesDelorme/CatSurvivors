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
        batch = context.getBatch();
        whitePixel = context.getAssets().getWhitePixel();
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
        drawGlow(194f, 590f, 340f, prehistory.getAccentColor(), 0.16f);
        drawGlow(1080f, 168f, 320f, future.getAccentColor(), 0.14f);
        drawRect(58f, 66f, 1172f, 580f, new Color(0f, 0f, 0f, 0.22f));
        drawRect(54f, 70f, 1172f, 580f, PANEL);
        drawRect(80f, 520f, 1120f, 96f, new Color(0.12f, 0.17f, 0.19f, 1f));
        drawRect(80f, 613f, 1120f, 3f, new Color(0.45f, 0.88f, 0.86f, 1f));

        context.getFont().setColor(new Color(0.96f, 0.95f, 0.82f, 1f));
        context.getFont().draw(batch, "Cat Survivors", 96f, 586f);
        context.getFont().setColor(Color.WHITE);
        context.getFont().draw(batch, "Survis à chaque époque pour débloquer la suivante.", 96f, 552f);
        context.getFont().draw(batch, "Déplacement: WASD / ZQSD / flèches  |  Pause: Échap  |  Upgrades: 1-2-3 ou clic",
            96f, 528f);

        drawStageCard(prehistoryButton, StageId.PREHISTORY, true);
        drawStageCard(futureButton, StageId.FUTURE, context.flow().isStageUnlocked(StageId.FUTURE));

        context.getFont().setColor(Color.WHITE);
        context.getFont().draw(batch, "Raccourcis: [1] Préhistoire  [2] Futur si débloqué", 96f, 112f);

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
        drawRect(button.getBounds().x + 4f, button.getBounds().y - 4f, button.getBounds().width, button.getBounds().height,
            new Color(0f, 0f, 0f, 0.22f));
        drawGlow(button.getBounds().x + button.getBounds().width * 0.5f, button.getBounds().y + button.getBounds().height * 0.5f, 240f,
            stage.getAccentColor(),
            enabled ? 0.12f : 0.05f);
        drawRect(button.getBounds().x, button.getBounds().y, button.getBounds().width, button.getBounds().height, cardColor);
        drawRect(button.getBounds().x, button.getBounds().y + button.getBounds().height - 4f, button.getBounds().width, 4f,
            stage.getAccentColor());
        drawTilesPreview(button.getBounds(), stageId);
        drawRect(button.getBounds().x + 18f, button.getBounds().y + 18f, button.getBounds().width - 36f, 70f,
            enabled ? BUTTON : BUTTON_DISABLED);

        context.getFont().setColor(Color.WHITE);
        context.getFont().draw(batch, button.getLabel(), button.getBounds().x + 28f, button.getBounds().y + 62f);
        context.getFont().draw(batch, button.getSubLabel(), button.getBounds().x + 28f, button.getBounds().y + 38f);

        float bodyY = button.getBounds().y + button.getBounds().height - 126f;
        if (stageId == StageId.PREHISTORY) {
            context.getFont().draw(batch, "Map 1", button.getBounds().x + 24f, bodyY);
            context.getFont().draw(batch, "Pression douce, ennemis lents, vague finale primitive.", button.getBounds().x + 24f, bodyY - 28f);
            context.getFont().draw(batch, "Parfaite pour découvrir les armes et l'XP.", button.getBounds().x + 24f, bodyY - 54f);
        } else {
            context.getFont().draw(batch, "Map 2", button.getBounds().x + 24f, bodyY);
            context.getFont().draw(batch, "Arène techno, robots agressifs, pression accrue.", button.getBounds().x + 24f, bodyY - 28f);
            context.getFont().draw(batch, enabled ? "Déjà disponible dans le menu." : "Se débloque après une victoire en Préhistoire.",
                button.getBounds().x + 24f, bodyY - 54f);
        }

        if (!enabled) {
            drawRect(button.getBounds().x + button.getBounds().width - 82f, button.getBounds().y + button.getBounds().height - 72f, 48f,
                48f,
                new Color(0f, 0f, 0f, 0.35f));
            context.getFont().draw(batch, "X", button.getBounds().x + button.getBounds().width - 64f,
                button.getBounds().y + button.getBounds().height - 40f);
        }
    }

    private void drawTilesPreview(Rectangle bounds, StageId stageId) {
        TextureRegion[] tiles = context.getAssets().getTiles(context.getStage(stageId).getTilesetType());
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
        batch.draw(context.getAssets().getSoftGlow(), centerX - size * 0.5f, centerY - size * 0.5f, size, size);
        batch.setColor(Color.WHITE);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
