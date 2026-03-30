package io.github.some_example_name.context;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.Main;
import io.github.some_example_name.game.stage.StageDefinition;
import io.github.some_example_name.game.stage.StageId;
import io.github.some_example_name.game.stage.StageLibrary;
import io.github.some_example_name.screen.EndScreen;
import io.github.some_example_name.screen.GameScreen;
import io.github.some_example_name.screen.MenuScreen;
import io.github.some_example_name.screen.UnlockScreen;

public class GameContext {
    public final Main game;
    public final SpriteBatch batch = new SpriteBatch();
    public final BitmapFont font = new BitmapFont();
    public final GlyphLayout glyphLayout = new GlyphLayout();
    public final GameAssets assets = new GameAssets();
    public final ProgressStore progressStore = new ProgressStore();

    public GameContext(Main game) {
        this.game = game;
        font.getData().setScale(1.1f);
    }

    public StageDefinition getStage(StageId stageId) {
        return StageLibrary.create(stageId);
    }

    public void showMenu() {
        game.replaceScreen(new MenuScreen(this));
    }

    public void startStage(StageId stageId) {
        game.replaceScreen(new GameScreen(this, getStage(stageId)));
    }

    public void showUnlock(StageId unlockedStageId) {
        game.replaceScreen(new UnlockScreen(this, unlockedStageId));
    }

    public void showEnd(StageId stageId, boolean victory) {
        game.replaceScreen(new EndScreen(this, stageId, victory));
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
        assets.dispose();
    }
}
