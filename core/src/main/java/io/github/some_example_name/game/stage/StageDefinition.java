package io.github.some_example_name.game.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import io.github.some_example_name.game.EnemyArchetype;

public class StageDefinition {
    public static final int TILE_SIZE = 32;
    public static final int MAP_COLS = 40;
    public static final int MAP_ROWS = 22;
    public static final float WORLD_WIDTH = MAP_COLS * TILE_SIZE;
    public static final float WORLD_HEIGHT = MAP_ROWS * TILE_SIZE;

    public final StageId id;
    public final String displayName;
    public final String subtitle;
    public final TilesetType tilesetType;
    public final int[][] mapTiles;
    public final float durationSeconds;
    public final float finalWaveLeadSeconds;
    public final float[] phaseStartTimes;
    public final float[] spawnIntervals;
    public final int[] maxEnemies;
    public final float[] speedBonuses;
    public final float[][] spawnWeights;
    public final EnemyArchetype grunt;
    public final EnemyArchetype runner;
    public final EnemyArchetype tank;
    public final EnemyArchetype elite;
    public final int finalWaveSupportCount;
    public final Color backgroundColor;
    public final Color accentColor;
    public final Color panelColor;

    public StageDefinition(StageId id, String displayName, String subtitle, TilesetType tilesetType, int[][] mapTiles,
                           float durationSeconds, float finalWaveLeadSeconds, float[] phaseStartTimes,
                           float[] spawnIntervals, int[] maxEnemies, float[] speedBonuses, float[][] spawnWeights,
                           EnemyArchetype grunt, EnemyArchetype runner, EnemyArchetype tank, EnemyArchetype elite,
                           int finalWaveSupportCount, Color backgroundColor, Color accentColor, Color panelColor) {
        this.id = id;
        this.displayName = displayName;
        this.subtitle = subtitle;
        this.tilesetType = tilesetType;
        this.mapTiles = mapTiles;
        this.durationSeconds = durationSeconds;
        this.finalWaveLeadSeconds = finalWaveLeadSeconds;
        this.phaseStartTimes = phaseStartTimes;
        this.spawnIntervals = spawnIntervals;
        this.maxEnemies = maxEnemies;
        this.speedBonuses = speedBonuses;
        this.spawnWeights = spawnWeights;
        this.grunt = grunt;
        this.runner = runner;
        this.tank = tank;
        this.elite = elite;
        this.finalWaveSupportCount = finalWaveSupportCount;
        this.backgroundColor = new Color(backgroundColor);
        this.accentColor = new Color(accentColor);
        this.panelColor = new Color(panelColor);
    }

    public float getFinalWaveStart() {
        return durationSeconds - finalWaveLeadSeconds;
    }

    public int getPhase(float elapsedSeconds) {
        int phase = 0;
        for (int index = 0; index < phaseStartTimes.length; index++) {
            if (elapsedSeconds >= phaseStartTimes[index]) {
                phase = index;
            }
        }
        return Math.min(phase, spawnIntervals.length - 1);
    }

    public float getSpawnInterval(float elapsedSeconds) {
        return spawnIntervals[getPhase(elapsedSeconds)];
    }

    public int getMaxEnemies(float elapsedSeconds) {
        return maxEnemies[getPhase(elapsedSeconds)];
    }

    public float getSpeedBonus(float elapsedSeconds) {
        return speedBonuses[getPhase(elapsedSeconds)];
    }

    public EnemyArchetype pickEnemyArchetype(float elapsedSeconds) {
        float[] weights = spawnWeights[getPhase(elapsedSeconds)];
        float roll = MathUtils.random();
        float cumulative = 0f;

        cumulative += weights[0];
        if (roll <= cumulative) {
            return grunt;
        }

        cumulative += weights[1];
        if (roll <= cumulative) {
            return runner;
        }

        return tank;
    }
}
