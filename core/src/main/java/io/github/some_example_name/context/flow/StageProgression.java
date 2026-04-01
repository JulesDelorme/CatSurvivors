package io.github.some_example_name.context.flow;

import io.github.some_example_name.game.stage.StageId;

// Abstraction de la progression persistante pour pouvoir la remplacer facilement en test.
public interface StageProgression {
    boolean isUnlocked(StageId stageId);

    void unlock(StageId stageId);
}
