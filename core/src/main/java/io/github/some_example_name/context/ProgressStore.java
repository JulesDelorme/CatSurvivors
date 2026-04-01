package io.github.some_example_name.context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import io.github.some_example_name.context.flow.StageProgression;
import io.github.some_example_name.game.stage.StageId;

public class ProgressStore implements StageProgression {
    private static final String PREF_NAME = "cat-survivors-progress";
    private static final String KEY_HIGHEST_UNLOCKED_STAGE = "highestUnlockedStage";

    private final Preferences preferences = Gdx.app.getPreferences(PREF_NAME);

    public StageId getHighestUnlockedStage() {
        int ordinal = preferences.getInteger(KEY_HIGHEST_UNLOCKED_STAGE, StageId.PREHISTORY.ordinal());
        StageId[] values = StageId.values();
        if (ordinal < 0 || ordinal >= values.length) {
            return StageId.PREHISTORY;
        }
        return values[ordinal];
    }

    @Override
    public boolean isUnlocked(StageId stageId) {
        return stageId.ordinal() <= getHighestUnlockedStage().ordinal();
    }

    @Override
    public void unlock(StageId stageId) {
        if (isUnlocked(stageId)) {
            return;
        }
        preferences.putInteger(KEY_HIGHEST_UNLOCKED_STAGE, stageId.ordinal());
        preferences.flush();
    }
}
