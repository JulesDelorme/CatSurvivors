package io.github.some_example_name.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.context.GameContext;
import io.github.some_example_name.game.stage.StageId;
import io.github.some_example_name.ui.UiButton;

public class EndScreen extends ScreenAdapter {
    private static final float UI_WIDTH = 1280f;
    private static final float UI_HEIGHT = 720f;

    private final GameContext context;
    private final StageId stageId;
    private final boolean victory;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final Viewport viewport = new FitViewport(UI_WIDTH, UI_HEIGHT, camera);
    private final Vector3 pointer = new Vector3();
    private final UiButton retryButton = new UiButton(350f, 180f, 260f, 82f, "Rejouer", "R ou clic", true);
    private final UiButton menuButton = new UiButton(670f, 180f, 260f, 82f, "Retour menu", "M ou clic", true);

    public EndScreen(GameContext context, StageId stageId, boolean victory) {
        this.context = context;
        this.stageId = stageId;
        this.victory = victory;
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(victory ? 0.04f : 0.06f, 0.05f, victory ? 0.08f : 0.06f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        camera.update();

        context.getBatch().setProjectionMatrix(camera.combined);
        context.getBatch().begin();
        drawRect(0f, 0f, UI_WIDTH, UI_HEIGHT, new Color(victory ? 0.04f : 0.06f, 0.05f, victory ? 0.08f : 0.06f, 1f));
        drawGlow(640f, 386f, 420f, victory ? context.getStage(stageId).getAccentColor() : new Color(0.96f, 0.42f, 0.38f, 1f),
            victory ? 0.16f : 0.12f);
        drawRect(226f, 90f, 840f, 520f, new Color(0f, 0f, 0f, 0.24f));
        drawRect(220f, 96f, 840f, 520f, new Color(0.08f, 0.09f, 0.12f, 0.96f));
        drawRect(220f, 612f, 840f, 4f, victory ? context.getStage(stageId).getAccentColor() : new Color(0.95f, 0.52f, 0.48f, 1f));

        if (victory) {
            context.getFont().setColor(new Color(0.95f, 0.88f, 0.62f, 1f));
            context.getFont().draw(context.getBatch(), "Victoire finale", 500f, 560f);
            context.getFont().setColor(Color.WHITE);
            context.getFont().draw(context.getBatch(), "Le chat a traversé les deux premières époques.", 356f, 518f);
            context.getFont().draw(context.getBatch(), "Le projet est prêt à accueillir Égypte, Moyen Âge, moderne ou cyberpunk.", 262f, 490f);
        } else {
            context.getFont().setColor(new Color(0.95f, 0.52f, 0.48f, 1f));
            context.getFont().draw(context.getBatch(), "Défaite", 572f, 560f);
            context.getFont().setColor(Color.WHITE);
            context.getFont().draw(context.getBatch(), "Le run sur " + context.getStage(stageId).getDisplayName() + " s'est arrêté ici.",
                364f, 518f);
            context.getFont().draw(context.getBatch(), "Relance une partie ou retourne au menu pour choisir ta map.", 330f, 490f);
        }

        drawButton(retryButton, new Color(0.22f, 0.64f, 0.56f, 1f));
        drawButton(menuButton, new Color(0.28f, 0.31f, 0.36f, 1f));
        context.getBatch().end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) || (!victory && Gdx.input.isKeyJustPressed(Input.Keys.ENTER))) {
            context.flow().startStage(stageId);
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
            || (victory && Gdx.input.isKeyJustPressed(Input.Keys.ENTER))) {
            context.flow().showMenu();
            return;
        }
        if (!Gdx.input.justTouched()) {
            return;
        }

        pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        viewport.unproject(pointer);
        if (retryButton.contains(pointer.x, pointer.y)) {
            context.flow().startStage(stageId);
        } else if (menuButton.contains(pointer.x, pointer.y)) {
            context.flow().showMenu();
        }
    }

    private void drawButton(UiButton button, Color color) {
        drawRect(button.getBounds().x, button.getBounds().y, button.getBounds().width, button.getBounds().height, color);
        context.getFont().setColor(Color.WHITE);
        context.getFont().draw(context.getBatch(), button.getLabel(), button.getBounds().x + 28f, button.getBounds().y + 52f);
        context.getFont().draw(context.getBatch(), button.getSubLabel(), button.getBounds().x + 28f, button.getBounds().y + 26f);
    }

    private void drawRect(float x, float y, float width, float height, Color color) {
        context.getBatch().setColor(color);
        context.getBatch().draw(context.getAssets().getWhitePixel(), x, y, width, height);
        context.getBatch().setColor(Color.WHITE);
    }

    private void drawGlow(float centerX, float centerY, float size, Color color, float alpha) {
        context.getBatch().setColor(color.r, color.g, color.b, alpha);
        context.getBatch().draw(context.getAssets().getSoftGlow(), centerX - size * 0.5f, centerY - size * 0.5f, size, size);
        context.getBatch().setColor(Color.WHITE);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
