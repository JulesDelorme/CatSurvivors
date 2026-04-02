package io.github.some_example_name.game.session;

import com.badlogic.gdx.math.MathUtils;
import io.github.some_example_name.game.Enemy;
import io.github.some_example_name.game.EnemyArchetype;
import io.github.some_example_name.game.stage.StageDefinition;

/**
 * Gère la pression du stage : cadence de spawn, vague finale et condition de victoire.
 */
final class SessionSpawnController {
    private static final float ENEMY_SPAWN_RADIUS_MIN = StageDefinition.WORLD_HEIGHT * 0.62f;
    private static final float ENEMY_SPAWN_RADIUS_MAX = StageDefinition.WORLD_HEIGHT * 0.85f;

    void initialize(GameSession session) {
        session.setSpawnTimerInternal(session.getStage().getSpawnInterval(0f) * 0.8f);
    }

    void advance(GameSession session, float delta) {
        triggerFinalWaveIfNeeded(session);
        if (!session.isFinalWaveTriggeredInternal()) {
            spawnEnemies(session, delta);
        }

        if (session.getSurvivalTime() >= session.getStage().getDurationSeconds()
            && session.isFinalWaveTriggeredInternal()
            && !hasEliteAlive(session)) {
            session.setStateInternal(SessionState.WON);
        }
    }

    private void spawnEnemies(GameSession session, float delta) {
        float spawnTimer = session.getSpawnTimerInternal() - delta;
        float spawnInterval = session.getStage().getSpawnInterval(session.getSurvivalTime());
        while (spawnTimer <= 0f) {
            spawnTimer += spawnInterval;
            if (session.getEnemies().size < session.getStage().getMaxEnemies(session.getSurvivalTime())) {
                spawnEnemy(session, session.getStage().pickEnemyArchetype(session.getSurvivalTime()));
            }
        }
        session.setSpawnTimerInternal(spawnTimer);
    }

    private void spawnEnemy(GameSession session, EnemyArchetype archetype) {
        float angle = MathUtils.random(0f, 359f);
        float radius = MathUtils.random(ENEMY_SPAWN_RADIUS_MIN, ENEMY_SPAWN_RADIUS_MAX);
        float spawnX = session.getPlayer().getPosition().x + MathUtils.cosDeg(angle) * radius;
        float spawnY = session.getPlayer().getPosition().y + MathUtils.sinDeg(angle) * radius;
        session.getEnemies().add(new Enemy(archetype, spawnX, spawnY));
    }

    /**
     * Déclenche la vague finale signature du stage quand le timer de fin est atteint.
     */
    private void triggerFinalWaveIfNeeded(GameSession session) {
        if (session.isFinalWaveTriggeredInternal() || session.getSurvivalTime() < session.getStage().getFinalWaveStart()) {
            return;
        }

        session.setFinalWaveTriggeredInternal(true);
        spawnEnemy(session, session.getStage().getEliteArchetype());
        for (int index = 0; index < session.getStage().getFinalWaveSupportCount(); index++) {
            EnemyArchetype support = index % 2 == 0 ? session.getStage().getRunnerArchetype() : session.getStage().getTankArchetype();
            spawnEnemy(session, support);
        }
    }

    private boolean hasEliteAlive(GameSession session) {
        for (Enemy enemy : session.getEnemies()) {
            if (enemy.isAlive() && enemy.getArchetype().isElite()) {
                return true;
            }
        }
        return false;
    }
}
