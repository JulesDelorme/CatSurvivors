package io.github.some_example_name.game.stage;

import com.badlogic.gdx.graphics.Color;
import io.github.some_example_name.game.EnemyArchetype;

public final class StageLibrary {
    private static final int TILE_GRASS = 0;
    private static final int TILE_DIRT = 1;
    private static final int TILE_PATH = 2;
    private static final int TILE_STONE = 3;
    private static final int TILE_BONES = 5;
    private static final int TILE_FIREPIT = 6;
    private static final int TILE_CLIFF = 7;
    private static final int TILE_STUMP = 9;
    private static final int TILE_TOTEM = 12;
    private static final int TILE_GRASS_ALT = 14;

    private static final int TILE_METAL = 0;
    private static final int TILE_CIRCUIT = 1;
    private static final int TILE_NEON = 2;
    private static final int TILE_HAZARD = 3;
    private static final int TILE_RUST = 4;
    private static final int TILE_WRECK = 5;
    private static final int TILE_CORE = 6;
    private static final int TILE_CONVEYOR = 7;
    private static final int TILE_BLOCK = 8;
    private static final int TILE_GLASS = 9;
    private static final int TILE_VENT = 10;
    private static final int TILE_SERVER = 11;
    private static final int TILE_OIL = 12;
    private static final int TILE_EMITTER = 13;
    private static final int TILE_METAL_ALT = 15;

    private StageLibrary() {
    }

    public static StageDefinition create(StageId stageId) {
        switch (stageId) {
            case FUTURE:
                return createFuture();
            case PREHISTORY:
            default:
                return createPrehistory();
        }
    }

    private static StageDefinition createPrehistory() {
        EnemyArchetype grunt = new EnemyArchetype(
            "Brute",
            "prehistory_grunt",
            18f,
            72f,
            26f,
            1,
            16f,
            1f,
            0f,
            0f,
            new Color(0.46f, 0.29f, 0.16f, 1f),
            new Color(0.68f, 0.46f, 0.24f, 1f),
            new Color(0.93f, 0.85f, 0.68f, 1f),
            false,
            false
        );
        EnemyArchetype runner = new EnemyArchetype(
            "Raptor",
            "prehistory_runner",
            14f,
            102f,
            18f,
            1,
            11f,
            1.65f,
            2.0f,
            0.35f,
            new Color(0.34f, 0.47f, 0.20f, 1f),
            new Color(0.54f, 0.72f, 0.33f, 1f),
            new Color(0.96f, 0.90f, 0.71f, 1f),
            false,
            false
        );
        EnemyArchetype tank = new EnemyArchetype(
            "Mastodonte",
            "prehistory_tank",
            24f,
            58f,
            52f,
            3,
            24f,
            1f,
            0f,
            0f,
            new Color(0.32f, 0.24f, 0.14f, 1f),
            new Color(0.56f, 0.39f, 0.23f, 1f),
            new Color(0.82f, 0.74f, 0.59f, 1f),
            false,
            false
        );
        EnemyArchetype elite = new EnemyArchetype(
            "Alpha",
            "prehistory_elite",
            34f,
            76f,
            260f,
            8,
            38f,
            1.15f,
            2.8f,
            0.5f,
            new Color(0.47f, 0.20f, 0.12f, 1f),
            new Color(0.78f, 0.41f, 0.22f, 1f),
            new Color(0.98f, 0.90f, 0.68f, 1f),
            false,
            true
        );

        return new StageDefinition(
            StageId.PREHISTORY,
            "Préhistoire",
            "Première chasse dans la clairière sauvage",
            TilesetType.PREHISTORY,
            buildPrehistoryMap(),
            120f,
            12f,
            new float[] {0f, 20f, 40f, 58f},
            new float[] {1.20f, 0.95f, 0.76f, 0.64f},
            new int[] {22, 31, 40, 50},
            new float[] {0f, 14f, 27f, 40f},
            new float[][] {
                {0.72f, 0.22f, 0.06f},
                {0.54f, 0.28f, 0.18f},
                {0.42f, 0.33f, 0.25f},
                {0.30f, 0.38f, 0.32f}
            },
            grunt,
            runner,
            tank,
            elite,
            11,
            new Color(0.05f, 0.05f, 0.04f, 1f),
            new Color(0.93f, 0.76f, 0.48f, 1f),
            new Color(0.11f, 0.09f, 0.06f, 0.96f)
        );
    }

    private static StageDefinition createFuture() {
        EnemyArchetype grunt = new EnemyArchetype(
            "Drone",
            "future_grunt",
            18f,
            94f,
            32f,
            1,
            18f,
            1f,
            0f,
            0f,
            new Color(0.20f, 0.28f, 0.38f, 1f),
            new Color(0.36f, 0.58f, 0.76f, 1f),
            new Color(0.52f, 0.98f, 0.94f, 1f),
            true,
            false
        );
        EnemyArchetype runner = new EnemyArchetype(
            "Spark Runner",
            "future_runner",
            14f,
            126f,
            20f,
            1,
            12f,
            1.8f,
            1.7f,
            0.3f,
            new Color(0.26f, 0.24f, 0.46f, 1f),
            new Color(0.56f, 0.48f, 0.88f, 1f),
            new Color(0.86f, 0.90f, 1f, 1f),
            true,
            false
        );
        EnemyArchetype tank = new EnemyArchetype(
            "Bulwark",
            "future_tank",
            26f,
            70f,
            70f,
            3,
            28f,
            1f,
            0f,
            0f,
            new Color(0.23f, 0.22f, 0.24f, 1f),
            new Color(0.43f, 0.44f, 0.48f, 1f),
            new Color(0.95f, 0.35f, 0.28f, 1f),
            true,
            false
        );
        EnemyArchetype elite = new EnemyArchetype(
            "Mech Alpha",
            "future_elite",
            36f,
            94f,
            360f,
            10,
            42f,
            1.2f,
            2.4f,
            0.45f,
            new Color(0.16f, 0.16f, 0.22f, 1f),
            new Color(0.31f, 0.74f, 0.86f, 1f),
            new Color(1f, 0.40f, 0.34f, 1f),
            true,
            true
        );

        return new StageDefinition(
            StageId.FUTURE,
            "Futur / Robots",
            "Le laboratoire sature sous la pression mécanique",
            TilesetType.FUTURE,
            buildFutureMap(),
            135f,
            14f,
            new float[] {0f, 24f, 48f, 70f},
            new float[] {1.00f, 0.81f, 0.62f, 0.47f},
            new int[] {26, 37, 50, 62},
            new float[] {10f, 24f, 38f, 54f},
            new float[][] {
                {0.66f, 0.24f, 0.10f},
                {0.48f, 0.32f, 0.20f},
                {0.34f, 0.38f, 0.28f},
                {0.22f, 0.42f, 0.36f}
            },
            grunt,
            runner,
            tank,
            elite,
            13,
            new Color(0.03f, 0.04f, 0.08f, 1f),
            new Color(0.39f, 0.95f, 0.92f, 1f),
            new Color(0.06f, 0.08f, 0.12f, 0.96f)
        );
    }

    private static int[][] buildPrehistoryMap() {
        int[][] map = new int[StageDefinition.MAP_ROWS][StageDefinition.MAP_COLS];
        for (int row = 0; row < StageDefinition.MAP_ROWS; row++) {
            for (int col = 0; col < StageDefinition.MAP_COLS; col++) {
                map[row][col] = ((row + col) % 5 == 0) ? TILE_GRASS_ALT : TILE_GRASS;
            }
        }

        for (int row = 0; row < StageDefinition.MAP_ROWS; row++) {
            map[row][0] = TILE_CLIFF;
            map[row][StageDefinition.MAP_COLS - 1] = TILE_CLIFF;
        }
        for (int col = 0; col < StageDefinition.MAP_COLS; col++) {
            map[0][col] = TILE_CLIFF;
            map[StageDefinition.MAP_ROWS - 1][col] = TILE_CLIFF;
        }

        for (int row = 6; row <= 15; row++) {
            for (int col = 6; col <= 33; col++) {
                map[row][col] = ((row + col) % 4 == 0) ? TILE_DIRT : TILE_GRASS;
            }
        }

        for (int col = 9; col <= 30; col++) {
            map[10][col] = TILE_PATH;
            map[11][col] = TILE_PATH;
        }
        for (int row = 7; row <= 14; row++) {
            map[row][19] = TILE_PATH;
            map[row][20] = TILE_PATH;
        }

        place(map, 3, 5, TILE_TOTEM);
        place(map, 6, 31, TILE_BONES);
        place(map, 16, 6, TILE_STUMP);
        place(map, 17, 30, TILE_STONE);
        place(map, 4, 18, TILE_FIREPIT);
        place(map, 16, 20, TILE_FIREPIT);
        place(map, 8, 9, TILE_BONES);
        place(map, 12, 28, TILE_STUMP);
        place(map, 14, 12, TILE_TOTEM);
        place(map, 18, 17, TILE_STONE);
        return map;
    }

    private static int[][] buildFutureMap() {
        int[][] map = new int[StageDefinition.MAP_ROWS][StageDefinition.MAP_COLS];
        for (int row = 0; row < StageDefinition.MAP_ROWS; row++) {
            for (int col = 0; col < StageDefinition.MAP_COLS; col++) {
                map[row][col] = ((row + col) % 4 == 0) ? TILE_METAL_ALT : TILE_METAL;
            }
        }

        for (int row = 0; row < StageDefinition.MAP_ROWS; row++) {
            map[row][0] = TILE_BLOCK;
            map[row][StageDefinition.MAP_COLS - 1] = TILE_BLOCK;
        }
        for (int col = 0; col < StageDefinition.MAP_COLS; col++) {
            map[0][col] = TILE_BLOCK;
            map[StageDefinition.MAP_ROWS - 1][col] = TILE_BLOCK;
        }

        for (int col = 6; col <= 33; col++) {
            map[10][col] = TILE_NEON;
            map[11][col] = TILE_NEON;
        }
        for (int row = 5; row <= 16; row++) {
            map[row][19] = TILE_CIRCUIT;
            map[row][20] = TILE_CIRCUIT;
        }

        for (int row = 4; row <= 17; row += 4) {
            for (int col = 4; col <= 34; col += 6) {
                map[row][col] = TILE_SERVER;
                map[row + 1][col] = TILE_GLASS;
            }
        }

        place(map, 3, 3, TILE_CORE);
        place(map, 3, 36, TILE_CORE);
        place(map, 18, 3, TILE_CORE);
        place(map, 18, 36, TILE_CORE);
        place(map, 6, 9, TILE_WRECK);
        place(map, 15, 29, TILE_WRECK);
        place(map, 7, 27, TILE_HAZARD);
        place(map, 14, 11, TILE_HAZARD);
        place(map, 8, 15, TILE_CONVEYOR);
        place(map, 13, 24, TILE_CONVEYOR);
        place(map, 5, 20, TILE_EMITTER);
        place(map, 16, 20, TILE_EMITTER);
        place(map, 9, 6, TILE_OIL);
        place(map, 12, 33, TILE_OIL);
        place(map, 10, 19, TILE_VENT);
        place(map, 11, 20, TILE_VENT);
        place(map, 10, 18, TILE_RUST);
        place(map, 11, 21, TILE_RUST);
        return map;
    }

    private static void place(int[][] map, int row, int col, int tile) {
        map[row][col] = tile;
    }
}
