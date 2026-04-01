package io.github.some_example_name.context.flow;

import io.github.some_example_name.game.stage.StageId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultAppFlowCoordinatorTest {
    @Test
    void showMenuRoutesToMenuState() {
        RecordingRouter router = new RecordingRouter();
        DefaultAppFlowCoordinator coordinator = new DefaultAppFlowCoordinator(router, new InMemoryStageProgression());

        coordinator.showMenu();

        assertInstanceOf(MenuFlowState.class, router.lastState);
    }

    @Test
    void startStageRoutesToGameState() {
        RecordingRouter router = new RecordingRouter();
        DefaultAppFlowCoordinator coordinator = new DefaultAppFlowCoordinator(router, new InMemoryStageProgression());

        coordinator.startStage(StageId.FUTURE);

        GameFlowState state = assertInstanceOf(GameFlowState.class, router.lastState);
        assertSame(StageId.FUTURE, state.getStageId());
    }

    @Test
    void clearingPrehistoryUnlocksFutureAndShowsUnlockScreenOnce() {
        RecordingRouter router = new RecordingRouter();
        InMemoryStageProgression progression = new InMemoryStageProgression();
        DefaultAppFlowCoordinator coordinator = new DefaultAppFlowCoordinator(router, progression);

        coordinator.completeStage(StageId.PREHISTORY);

        assertTrue(progression.isUnlocked(StageId.FUTURE));
        UnlockFlowState state = assertInstanceOf(UnlockFlowState.class, router.lastState);
        assertSame(StageId.FUTURE, state.getUnlockedStageId());
    }

    @Test
    void clearingAlreadyUnlockedPrehistoryRoutesToVictoryEnd() {
        RecordingRouter router = new RecordingRouter();
        InMemoryStageProgression progression = new InMemoryStageProgression();
        progression.unlock(StageId.FUTURE);
        DefaultAppFlowCoordinator coordinator = new DefaultAppFlowCoordinator(router, progression);

        coordinator.completeStage(StageId.PREHISTORY);

        EndFlowState state = assertInstanceOf(EndFlowState.class, router.lastState);
        assertSame(StageId.PREHISTORY, state.getStageId());
        assertTrue(state.isVictory());
    }

    @Test
    void failingStageRoutesToDefeatEnd() {
        RecordingRouter router = new RecordingRouter();
        DefaultAppFlowCoordinator coordinator = new DefaultAppFlowCoordinator(router, new InMemoryStageProgression());

        coordinator.failStage(StageId.FUTURE);

        EndFlowState state = assertInstanceOf(EndFlowState.class, router.lastState);
        assertSame(StageId.FUTURE, state.getStageId());
        assertFalse(state.isVictory());
    }

    @Test
    void unlockedStateDelegatesToProgression() {
        RecordingRouter router = new RecordingRouter();
        InMemoryStageProgression progression = new InMemoryStageProgression();
        DefaultAppFlowCoordinator coordinator = new DefaultAppFlowCoordinator(router, progression);

        assertFalse(coordinator.isStageUnlocked(StageId.FUTURE));
        progression.unlock(StageId.FUTURE);
        assertTrue(coordinator.isStageUnlocked(StageId.FUTURE));
    }

    private static final class RecordingRouter implements AppFlowRouter {
        private AppFlowState lastState;

        @Override
        public void show(AppFlowState state) {
            lastState = state;
        }
    }

    private static final class InMemoryStageProgression implements StageProgression {
        private StageId highestUnlockedStage = StageId.PREHISTORY;

        @Override
        public boolean isUnlocked(StageId stageId) {
            return stageId.ordinal() <= highestUnlockedStage.ordinal();
        }

        @Override
        public void unlock(StageId stageId) {
            if (!isUnlocked(stageId)) {
                highestUnlockedStage = stageId;
            }
        }
    }
}
