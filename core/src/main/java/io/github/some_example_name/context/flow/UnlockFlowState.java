package io.github.some_example_name.context.flow;

import io.github.some_example_name.game.stage.StageId;

import java.util.Objects;

public final class UnlockFlowState implements AppFlowState {
    private final StageId unlockedStageId;

    public UnlockFlowState(StageId unlockedStageId) {
        this.unlockedStageId = Objects.requireNonNull(unlockedStageId, "unlockedStageId");
    }

    public StageId getUnlockedStageId() {
        return unlockedStageId;
    }
}
