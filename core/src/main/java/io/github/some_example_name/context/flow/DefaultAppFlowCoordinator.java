package io.github.some_example_name.context.flow;

import io.github.some_example_name.game.stage.StageId;

import java.util.Objects;

/**
 * Centralise le flux Menu -> Game -> Unlock/End pour éviter de disperser cette logique dans les écrans.
 */
public class DefaultAppFlowCoordinator implements AppFlowCoordinator {
    private final AppFlowRouter router;
    private final StageProgression progression;

    public DefaultAppFlowCoordinator(AppFlowRouter router, StageProgression progression) {
        this.router = Objects.requireNonNull(router, "router");
        this.progression = Objects.requireNonNull(progression, "progression");
    }

    @Override
    public boolean isStageUnlocked(StageId stageId) {
        return progression.isUnlocked(stageId);
    }

    @Override
    public void showMenu() {
        router.show(new MenuFlowState());
    }

    @Override
    public void startStage(StageId stageId) {
        router.show(new GameFlowState(stageId));
    }

    /**
     * Termine le stage et déclenche l'écran de déblocage lors du premier clear de la préhistoire.
     */
    @Override
    public void completeStage(StageId stageId) {
        if (stageId == StageId.PREHISTORY && !progression.isUnlocked(StageId.FUTURE)) {
            progression.unlock(StageId.FUTURE);
            router.show(new UnlockFlowState(StageId.FUTURE));
            return;
        }

        router.show(new EndFlowState(stageId, true));
    }

    @Override
    public void failStage(StageId stageId) {
        router.show(new EndFlowState(stageId, false));
    }
}
