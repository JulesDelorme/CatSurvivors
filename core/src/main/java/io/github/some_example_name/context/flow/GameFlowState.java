package io.github.some_example_name.context.flow;

import io.github.some_example_name.game.stage.StageId;

import java.util.Objects;

public final class GameFlowState implements AppFlowState {
    private final StageId stageId;

    public GameFlowState(StageId stageId) {
        this.stageId = Objects.requireNonNull(stageId, "stageId");
    }

    public StageId getStageId() {
        return stageId;
    }
}
