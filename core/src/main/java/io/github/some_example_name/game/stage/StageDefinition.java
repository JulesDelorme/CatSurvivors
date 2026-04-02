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

    private final StageId id;
    private final String displayName;
    private final String subtitle;
    private final TilesetType tilesetType;
    private final int[][] mapTiles;
    private final float durationSeconds;
    private final float finalWaveLeadSeconds;
    private final float[] phaseStartTimes;
    private final float[] spawnIntervals;
    private final int[] maxEnemies;
    private final float[] speedBonuses;
    private final float[][] spawnWeights;
    private final EnemyArchetype grunt;
    private final EnemyArchetype runner;
    private final EnemyArchetype tank;
    private final EnemyArchetype elite;
    private final int finalWaveSupportCount;
    private final Color backgroundColor;
    private final Color accentColor;
    private final Color panelColor;

    public StageDefinition(StageId id, String displayName, String subtitle, TilesetType tilesetType, int[][] mapTiles,
                           float durationSeconds, float finalWaveLeadSeconds, float[] phaseStartTimes,
                           float[] spawnIntervals, int[] maxEnemies, float[] speedBonuses, float[][] spawnWeights,
                           EnemyArchetype grunt, EnemyArchetype runner, EnemyArchetype tank, EnemyArchetype elite,
                           int finalWaveSupportCount, Color backgroundColor, Color accentColor, Color panelColor) {
        this.id = id;
        this.displayName = displayName;
        this.subtitle = subtitle;
        this.tilesetType = tilesetType;
        this.mapTiles = copyMapTiles(mapTiles);
        this.durationSeconds = durationSeconds;
        this.finalWaveLeadSeconds = finalWaveLeadSeconds;
        this.phaseStartTimes = phaseStartTimes.clone();
        this.spawnIntervals = spawnIntervals.clone();
        this.maxEnemies = maxEnemies.clone();
        this.speedBonuses = speedBonuses.clone();
        this.spawnWeights = copySpawnWeights(spawnWeights);
        this.grunt = grunt;
        this.runner = runner;
        this.tank = tank;
        this.elite = elite;
        this.finalWaveSupportCount = finalWaveSupportCount;
        this.backgroundColor = new Color(backgroundColor);
        this.accentColor = new Color(accentColor);
        this.panelColor = new Color(panelColor);
    }

    public StageId getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public TilesetType getTilesetType() {
        return tilesetType;
    }

    public float getDurationSeconds() {
        return durationSeconds;
    }

    public EnemyArchetype getRunnerArchetype() {
        return runner;
    }

    public EnemyArchetype getTankArchetype() {
        return tank;
    }

    public EnemyArchetype getEliteArchetype() {
        return elite;
    }

    public int getFinalWaveSupportCount() {
        return finalWaveSupportCount;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getAccentColor() {
        return accentColor;
    }

    public Color getPanelColor() {
        return panelColor;
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

    private static int[][] copyMapTiles(int[][] source) {
        int[][] copy = new int[source.length][];
        for (int index = 0; index < source.length; index++) {
            copy[index] = source[index].clone();
        }
        return copy;
    }

    private static float[][] copySpawnWeights(float[][] source) {
        float[][] copy = new float[source.length][];
        for (int index = 0; index < source.length; index++) {
            copy[index] = source[index].clone();
        }
        return copy;
    }
}
