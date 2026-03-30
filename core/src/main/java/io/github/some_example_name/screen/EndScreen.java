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

        context.batch.setProjectionMatrix(camera.combined);
        context.batch.begin();
        drawRect(0f, 0f, UI_WIDTH, UI_HEIGHT, new Color(victory ? 0.04f : 0.06f, 0.05f, victory ? 0.08f : 0.06f, 1f));
        drawRect(220f, 96f, 840f, 520f, new Color(0.08f, 0.09f, 0.12f, 0.96f));

        if (victory) {
            context.font.setColor(new Color(0.95f, 0.88f, 0.62f, 1f));
            context.font.draw(context.batch, "Victoire finale", 500f, 560f);
            context.font.setColor(Color.WHITE);
            context.font.draw(context.batch, "Le chat a traversé les deux premières époques.", 356f, 518f);
            context.font.draw(context.batch, "Le projet est prêt à accueillir Égypte, Moyen Âge, moderne ou cyberpunk.", 262f, 490f);
        } else {
            context.font.setColor(new Color(0.95f, 0.52f, 0.48f, 1f));
            context.font.draw(context.batch, "Défaite", 572f, 560f);
            context.font.setColor(Color.WHITE);
            context.font.draw(context.batch, "Le run sur " + context.getStage(stageId).displayName + " s'est arrêté ici.", 364f, 518f);
            context.font.draw(context.batch, "Relance une partie ou retourne au menu pour choisir ta map.", 330f, 490f);
        }

        drawButton(retryButton, new Color(0.22f, 0.64f, 0.56f, 1f));
        drawButton(menuButton, new Color(0.28f, 0.31f, 0.36f, 1f));
        context.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) || (!victory && Gdx.input.isKeyJustPressed(Input.Keys.ENTER))) {
            context.startStage(stageId);
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
            || (victory && Gdx.input.isKeyJustPressed(Input.Keys.ENTER))) {
            context.showMenu();
            return;
        }
        if (!Gdx.input.justTouched()) {
            return;
        }

        pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        viewport.unproject(pointer);
        if (retryButton.contains(pointer.x, pointer.y)) {
            context.startStage(stageId);
        } else if (menuButton.contains(pointer.x, pointer.y)) {
            context.showMenu();
        }
    }

    private void drawButton(UiButton button, Color color) {
        drawRect(button.bounds.x, button.bounds.y, button.bounds.width, button.bounds.height, color);
        context.font.setColor(Color.WHITE);
        context.font.draw(context.batch, button.label, button.bounds.x + 28f, button.bounds.y + 52f);
        context.font.draw(context.batch, button.subLabel, button.bounds.x + 28f, button.bounds.y + 26f);
    }

    private void drawRect(float x, float y, float width, float height, Color color) {
        context.batch.setColor(color);
        context.batch.draw(context.assets.getWhitePixel(), x, y, width, height);
        context.batch.setColor(Color.WHITE);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
