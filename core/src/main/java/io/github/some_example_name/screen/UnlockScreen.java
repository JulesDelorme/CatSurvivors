package io.github.some_example_name.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.context.GameContext;
import io.github.some_example_name.game.stage.StageId;
import io.github.some_example_name.ui.UiButton;

public class UnlockScreen extends ScreenAdapter {
    private static final float UI_WIDTH = 1280f;
    private static final float UI_HEIGHT = 720f;

    private final GameContext context;
    private final StageId unlockedStageId;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final Viewport viewport = new FitViewport(UI_WIDTH, UI_HEIGHT, camera);
    private final Vector3 pointer = new Vector3();
    private final UiButton playButton = new UiButton(350f, 180f, 260f, 82f, "Lancer le Futur", "Entrée ou clic", true);
    private final UiButton menuButton = new UiButton(670f, 180f, 260f, 82f, "Retour menu", "M ou clic", true);

    public UnlockScreen(GameContext context, StageId unlockedStageId) {
        this.context = context;
        this.unlockedStageId = unlockedStageId;
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0.03f, 0.05f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        camera.update();

        context.getBatch().setProjectionMatrix(camera.combined);
        context.getBatch().begin();
        drawRect(0f, 0f, UI_WIDTH, UI_HEIGHT, new Color(0.03f, 0.05f, 0.08f, 1f));
        drawGlow(640f, 392f, 420f, context.getStage(unlockedStageId).getAccentColor(), 0.16f);
        drawRect(226f, 90f, 840f, 520f, new Color(0f, 0f, 0f, 0.24f));
        drawRect(220f, 96f, 840f, 520f, new Color(0.07f, 0.09f, 0.12f, 0.96f));
        drawRect(220f, 612f, 840f, 4f, context.getStage(unlockedStageId).getAccentColor());

        context.getFont().setColor(new Color(0.42f, 0.98f, 0.94f, 1f));
        context.getFont().draw(context.getBatch(), "Époque suivante débloquée", 408f, 566f);
        context.getFont().setColor(Color.WHITE);
        context.getFont().draw(context.getBatch(), "Le chat active un portail et peut maintenant rejoindre le Futur / Robots.", 282f, 522f);
        context.getFont().draw(context.getBatch(), "La nouvelle map est aussi disponible depuis le menu principal.", 330f, 492f);

        TextureRegion[] futureTiles = context.getAssets().getTiles(context.getStage(unlockedStageId).getTilesetType());
        int[] preview = {0, 1, 2, 3, 6, 11, 13};
        for (int index = 0; index < preview.length; index++) {
            context.getBatch().draw(futureTiles[preview[index]], 318f + index * 78f, 330f, 64f, 64f);
        }

        drawButton(playButton, new Color(0.20f, 0.66f, 0.60f, 1f));
        drawButton(menuButton, new Color(0.26f, 0.30f, 0.36f, 1f));
        context.getBatch().end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            context.flow().startStage(unlockedStageId);
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            context.flow().showMenu();
            return;
        }
        if (!Gdx.input.justTouched()) {
            return;
        }

        pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        viewport.unproject(pointer);
        if (playButton.contains(pointer.x, pointer.y)) {
            context.flow().startStage(unlockedStageId);
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
