package io.github.some_example_name.game.stage;

public final class TerrainGenerator {
    private static final int PRE_GRASS = 0;
    private static final int PRE_DIRT = 1;
    private static final int PRE_PATH = 2;
    private static final int PRE_STONE = 3;
    private static final int PRE_BONES = 5;
    private static final int PRE_FIREPIT = 6;
    private static final int PRE_STUMP = 9;
    private static final int PRE_TOTEM = 12;
    private static final int PRE_GRASS_ALT = 14;

    private static final int FUT_METAL = 0;
    private static final int FUT_CIRCUIT = 1;
    private static final int FUT_NEON = 2;
    private static final int FUT_HAZARD = 3;
    private static final int FUT_RUST = 4;
    private static final int FUT_WRECK = 5;
    private static final int FUT_CORE = 6;
    private static final int FUT_CONVEYOR = 7;
    private static final int FUT_BLOCK = 8;
    private static final int FUT_GLASS = 9;
    private static final int FUT_VENT = 10;
    private static final int FUT_SERVER = 11;
    private static final int FUT_OIL = 12;
    private static final int FUT_EMITTER = 13;
    private static final int FUT_METAL_ALT = 15;

    private TerrainGenerator() {
    }

    public static int getTileIndex(StageDefinition stage, int tileX, int tileY) {
        switch (stage.id) {
            case FUTURE:
                return getFutureTile(tileX, tileY);
            case PREHISTORY:
            default:
                return getPrehistoryTile(tileX, tileY);
        }
    }

    private static int getPrehistoryTile(int x, int y) {
        long noise = hash(x, y, 17);
        int pathA = mod(x - y, 19);
        int pathB = mod(x + y, 23);
        if (pathA <= 1 || pathA >= 18 || pathB == 0) {
            return PRE_PATH;
        }
        if ((noise & 255) < 18) {
            return PRE_DIRT;
        }
        if ((noise & 255) < 25) {
            return PRE_STONE;
        }
        if ((noise & 511) == 33) {
            return PRE_FIREPIT;
        }
        if ((noise & 255) == 44) {
            return PRE_TOTEM;
        }
        if ((noise & 255) == 88) {
            return PRE_BONES;
        }
        if ((noise & 255) == 120) {
            return PRE_STUMP;
        }
        return ((noise >>> 8) & 3) == 0 ? PRE_GRASS_ALT : PRE_GRASS;
    }

    private static int getFutureTile(int x, int y) {
        long noise = hash(x, y, 83);
        int neonA = mod(x, 12);
        int neonB = mod(y + 3, 14);
        if (neonA == 0 || neonA == 1) {
            return FUT_NEON;
        }
        if (neonB == 0 || neonB == 1) {
            return FUT_CIRCUIT;
        }
        if ((noise & 255) < 15) {
            return FUT_RUST;
        }
        if ((noise & 1023) == 65) {
            return FUT_SERVER;
        }
        if ((noise & 1023) == 139) {
            return FUT_WRECK;
        }
        if ((noise & 1023) == 312) {
            return FUT_EMITTER;
        }
        if ((noise & 1023) == 401) {
            return FUT_CORE;
        }
        if ((noise & 511) == 97) {
            return FUT_GLASS;
        }
        if ((noise & 511) == 188) {
            return FUT_OIL;
        }
        if ((noise & 511) == 252) {
            return FUT_VENT;
        }
        if ((noise & 511) == 315) {
            return FUT_HAZARD;
        }
        if ((noise & 255) == 211) {
            return FUT_CONVEYOR;
        }
        if ((noise >>> 8 & 3) == 0) {
            return FUT_METAL_ALT;
        }
        if ((noise >>> 10 & 7) == 0) {
            return FUT_BLOCK;
        }
        return FUT_METAL;
    }

    private static long hash(int x, int y, int salt) {
        long value = x * 73428767L ^ y * 912931L ^ salt * 18233L;
        value ^= (value << 13);
        value ^= (value >>> 7);
        value ^= (value << 17);
        return value & 0x7fffffffL;
    }

    private static int mod(int value, int modulo) {
        int result = value % modulo;
        return result < 0 ? result + modulo : result;
    }
}
