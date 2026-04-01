package io.github.some_example_name.context.flow;

import io.github.some_example_name.game.stage.StageId;

public interface StageProgression {
    boolean isUnlocked(StageId stageId);

    void unlock(StageId stageId);
}
