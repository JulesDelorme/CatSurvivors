package io.github.some_example_name.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.context.GameContext;
import io.github.some_example_name.game.session.GameSession;
import io.github.some_example_name.game.session.SessionState;
import io.github.some_example_name.game.stage.StageDefinition;
import io.github.some_example_name.render.EntityRenderer;
import io.github.some_example_name.render.HudRenderer;
import io.github.some_example_name.render.MapRenderer;
import io.github.some_example_name.render.OverlayRenderer;

public class GameScreen extends ScreenAdapter {
    private final GameContext context;
    private final StageDefinition stage;
    private final GameSession session;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final Viewport viewport = new FitViewport(StageDefinition.WORLD_WIDTH, StageDefinition.WORLD_HEIGHT, camera);
    private final Matrix4 hudMatrix = new Matrix4();
    private final Vector3 pointer = new Vector3();
    private final Array<Rectangle> choiceBounds = new Array<Rectangle>();
    private final MapRenderer mapRenderer = new MapRenderer();
    private final EntityRenderer entityRenderer = new EntityRenderer();
    private final HudRenderer hudRenderer = new HudRenderer();
    private final OverlayRenderer overlayRenderer = new OverlayRenderer();

    private boolean routed;

    public GameScreen(GameContext context, StageDefinition stage) {
        this.context = context;
        this.stage = stage;
        session = new GameSession(stage);
        overlayRenderer.layoutChoiceBounds(choiceBounds);
        camera.position.set(session.getPlayer().getPosition().x, session.getPlayer().getPosition().y, 0f);
    }

    @Override
    public void render(float delta) {
        handleInput();
        session.update(delta);

        Gdx.gl.glClearColor(stage.getBackgroundColor().r, stage.getBackgroundColor().g, stage.getBackgroundColor().b,
            stage.getBackgroundColor().a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.position.set(session.getPlayer().getPosition().x, session.getPlayer().getPosition().y, 0f);
        viewport.apply();
        camera.update();

        context.getBatch().setProjectionMatrix(camera.combined);
        context.getBatch().begin();
        mapRenderer.draw(context.getBatch(), context.getAssets(), stage, camera);
        entityRenderer.draw(context.getBatch(), context.getAssets(), session);
        context.getBatch().end();

        hudMatrix.setToOrtho2D(0f, 0f, StageDefinition.WORLD_WIDTH, StageDefinition.WORLD_HEIGHT);
        context.getBatch().setProjectionMatrix(hudMatrix);
        context.getBatch().begin();
        hudRenderer.draw(context.getBatch(), context.getFont(), context.getGlyphLayout(), context.getAssets().getWhitePixel(),
            context.getAssets(), session);
        if (session.getState() == SessionState.PAUSED) {
            overlayRenderer.drawPause(context.getBatch(), context.getFont(), context.getGlyphLayout(),
                context.getAssets().getWhitePixel(), context.getAssets());
        } else if (session.getState() == SessionState.LEVEL_UP) {
            overlayRenderer.drawLevelUp(context.getBatch(), context.getFont(), context.getGlyphLayout(),
                context.getAssets().getWhitePixel(), context.getAssets(), session, choiceBounds, hoveredChoiceIndex());
        }
        context.getBatch().end();

        routeIfFinished();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && session.getState() != SessionState.LEVEL_UP) {
            session.togglePause();
        }

        if (session.getState() == SessionState.LEVEL_UP) {
            session.setMovement(0f, 0f);
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) {
                session.chooseUpgrade(0);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
                session.chooseUpgrade(1);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)) {
                session.chooseUpgrade(2);
            } else if (Gdx.input.justTouched()) {
                int hovered = hoveredChoiceIndex();
                if (hovered >= 0) {
                    session.chooseUpgrade(hovered);
                }
            }
            return;
        }

        if (session.getState() != SessionState.RUNNING) {
            session.setMovement(0f, 0f);
            return;
        }

        float horizontal = 0f;
        float vertical = 0f;

        if (isLeftPressed()) {
            horizontal -= 1f;
        }
        if (isRightPressed()) {
            horizontal += 1f;
        }
        if (isDownPressed()) {
            vertical -= 1f;
        }
        if (isUpPressed()) {
            vertical += 1f;
        }
        session.setMovement(horizontal, vertical);
    }

    private boolean isLeftPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.Q) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
    }

    private boolean isRightPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    }

    private boolean isUpPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.Z) || Gdx.input.isKeyPressed(Input.Keys.UP);
    }

    private boolean isDownPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
    }

    private int hoveredChoiceIndex() {
        pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        viewport.unproject(pointer);
        float hudX = pointer.x - camera.position.x + StageDefinition.WORLD_WIDTH * 0.5f;
        float hudY = pointer.y - camera.position.y + StageDefinition.WORLD_HEIGHT * 0.5f;
        for (int index = 0; index < choiceBounds.size; index++) {
            if (choiceBounds.get(index).contains(hudX, hudY)) {
                return index;
            }
        }
        return -1;
    }

    private void routeIfFinished() {
        if (routed) {
            return;
        }

        if (session.isLost()) {
            routed = true;
            context.flow().failStage(stage.getId());
            return;
        }

        if (!session.isStageCleared()) {
            return;
        }

        routed = true;
        context.flow().completeStage(stage.getId());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
