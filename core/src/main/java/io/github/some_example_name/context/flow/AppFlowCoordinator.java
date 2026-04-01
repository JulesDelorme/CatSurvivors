package io.github.some_example_name.context.flow;

import io.github.some_example_name.game.stage.StageId;

// Contrat minimal du flux global de l'application.
public interface AppFlowCoordinator {
    boolean isStageUnlocked(StageId stageId);

    void showMenu();

    void startStage(StageId stageId);

    void completeStage(StageId stageId);

    void failStage(StageId stageId);
}
