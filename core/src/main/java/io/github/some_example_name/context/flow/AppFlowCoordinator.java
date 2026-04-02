package io.github.some_example_name.context.flow;

import io.github.some_example_name.game.stage.StageId;

/**
 * Contrat minimal du flux global de l'application.
 */
public interface AppFlowCoordinator {
    /**
     * Indique si un stage est accessible depuis la progression courante.
     */
    boolean isStageUnlocked(StageId stageId);

    /**
     * Retourne au menu principal.
     */
    void showMenu();

    /**
     * Lance un stage jouable.
     */
    void startStage(StageId stageId);

    /**
     * Termine un stage sur une victoire.
     */
    void completeStage(StageId stageId);

    /**
     * Termine un stage sur une défaite.
     */
    void failStage(StageId stageId);
}
