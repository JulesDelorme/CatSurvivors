package io.github.some_example_name.context;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.Main;
import io.github.some_example_name.context.flow.AppFlowCoordinator;
import io.github.some_example_name.context.flow.AppFlowRouter;
import io.github.some_example_name.context.flow.AppFlowState;
import io.github.some_example_name.context.flow.DefaultAppFlowCoordinator;
import io.github.some_example_name.context.flow.EndFlowState;
import io.github.some_example_name.context.flow.GameFlowState;
import io.github.some_example_name.context.flow.MenuFlowState;
import io.github.some_example_name.context.flow.UnlockFlowState;
import io.github.some_example_name.game.stage.StageDefinition;
import io.github.some_example_name.game.stage.StageId;
import io.github.some_example_name.game.stage.StageLibrary;
import io.github.some_example_name.screen.EndScreen;
import io.github.some_example_name.screen.GameScreen;
import io.github.some_example_name.screen.MenuScreen;
import io.github.some_example_name.screen.UnlockScreen;

/**
 * Point d'entrée partagé des écrans pour les services globaux, les assets et le routage.
 */
public class GameContext implements AppFlowRouter {
    private final Main game;
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private final GameAssets assets = new GameAssets();
    private final ProgressStore progressStore = new ProgressStore();
    private final AppFlowCoordinator flow = new DefaultAppFlowCoordinator(this, progressStore);

    public GameContext(Main game) {
        this.game = game;
        font.getData().setScale(1.1f);
    }

    /**
     * Retourne le coordinateur de flux utilisé par les écrans.
     */
    public AppFlowCoordinator flow() {
        return flow;
    }

    public Main getGame() {
        return game;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public BitmapFont getFont() {
        return font;
    }

    public GlyphLayout getGlyphLayout() {
        return glyphLayout;
    }

    public GameAssets getAssets() {
        return assets;
    }

    public StageDefinition getStage(StageId stageId) {
        return StageLibrary.create(stageId);
    }

    @Override
    public void show(AppFlowState state) {
        if (state instanceof MenuFlowState) {
            game.replaceScreen(new MenuScreen(this));
            return;
        }
        if (state instanceof GameFlowState) {
            GameFlowState gameState = (GameFlowState) state;
            game.replaceScreen(new GameScreen(this, getStage(gameState.getStageId())));
            return;
        }
        if (state instanceof UnlockFlowState) {
            UnlockFlowState unlockState = (UnlockFlowState) state;
            game.replaceScreen(new UnlockScreen(this, unlockState.getUnlockedStageId()));
            return;
        }
        if (state instanceof EndFlowState) {
            EndFlowState endState = (EndFlowState) state;
            game.replaceScreen(new EndScreen(this, endState.getStageId(), endState.isVictory()));
            return;
        }
        throw new IllegalArgumentException("Unknown flow state: " + state.getClass().getName());
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
        assets.dispose();
    }
}
