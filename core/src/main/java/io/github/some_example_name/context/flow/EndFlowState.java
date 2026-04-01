package io.github.some_example_name.context.flow;

import io.github.some_example_name.game.stage.StageId;

import java.util.Objects;

public final class EndFlowState implements AppFlowState {
    private final StageId stageId;
    private final boolean victory;

    public EndFlowState(StageId stageId, boolean victory) {
        this.stageId = Objects.requireNonNull(stageId, "stageId");
        this.victory = victory;
    }

    public StageId getStageId() {
        return stageId;
    }

    public boolean isVictory() {
        return victory;
    }
}
