package io.github.some_example_name.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.some_example_name.context.GameAssets;
import io.github.some_example_name.game.stage.StageDefinition;
import io.github.some_example_name.game.stage.TerrainGenerator;
import io.github.some_example_name.game.stage.TilesetType;

public class MapRenderer {
    private static final Color DECAL_TINT = new Color(1f, 1f, 1f, 0.24f);
    private static final Color PROP_TINT = new Color(1f, 1f, 1f, 0.96f);
    private static final Color PREHISTORY_WARM = new Color(0.98f, 0.79f, 0.47f, 1f);
    private static final Color PREHISTORY_COOL = new Color(0.22f, 0.32f, 0.24f, 1f);
    private static final Color FUTURE_CYAN = new Color(0.33f, 0.94f, 0.98f, 1f);
    private static final Color FUTURE_VIOLET = new Color(0.54f, 0.48f, 0.92f, 1f);

    /**
     * Dessine la map en couches : ambiance de fond, tuiles visibles, décor secondaire et voile avant-plan.
     */
    public void draw(SpriteBatch batch, GameAssets assets, StageDefinition stage, OrthographicCamera camera) {
        Texture whitePixel = assets.getWhitePixel();
        Texture softGlow = assets.getSoftGlow();
        TextureRegion[] tiles = assets.getTiles(stage.getTilesetType());
        batch.setColor(Color.WHITE);
        int startCol = (int) Math.floor((camera.position.x - StageDefinition.WORLD_WIDTH * 0.5f) / StageDefinition.TILE_SIZE) - 2;
        int endCol = (int) Math.ceil((camera.position.x + StageDefinition.WORLD_WIDTH * 0.5f) / StageDefinition.TILE_SIZE) + 2;
        int startRow = (int) Math.floor((camera.position.y - StageDefinition.WORLD_HEIGHT * 0.5f) / StageDefinition.TILE_SIZE) - 2;
        int endRow = (int) Math.ceil((camera.position.y + StageDefinition.WORLD_HEIGHT * 0.5f) / StageDefinition.TILE_SIZE) + 2;

        drawBackdrop(batch, whitePixel, softGlow, stage, camera);
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                int tileIndex = TerrainGenerator.getTileIndex(stage, col, row);
                TextureRegion region = tiles[tileIndex];
                float tileX = col * StageDefinition.TILE_SIZE;
                float tileY = row * StageDefinition.TILE_SIZE;
                batch.draw(region, col * StageDefinition.TILE_SIZE, row * StageDefinition.TILE_SIZE,
                    StageDefinition.TILE_SIZE, StageDefinition.TILE_SIZE);
                drawTileMood(batch, whitePixel, softGlow, stage, tileIndex, tileX, tileY, hash(col, row, 19));
            }
        }

        drawStageDecor(batch, assets, stage, whitePixel, softGlow, startCol, endCol, startRow, endRow);
        drawForegroundVeil(batch, whitePixel, softGlow, stage, camera);
        batch.setColor(Color.WHITE);
    }

    /**
     * Dessine un fond large et flottant pour éviter un rendu trop plat derrière la carte.
     */
    private void drawBackdrop(SpriteBatch batch, Texture whitePixel, Texture softGlow, StageDefinition stage,
                              OrthographicCamera camera) {
        float left = camera.position.x - StageDefinition.WORLD_WIDTH * 0.68f;
        float bottom = camera.position.y - StageDefinition.WORLD_HEIGHT * 0.68f;
        float width = StageDefinition.WORLD_WIDTH * 1.36f;
        float height = StageDefinition.WORLD_HEIGHT * 1.36f;

        batch.setColor(stage.getBackgroundColor().r * 0.85f, stage.getBackgroundColor().g * 0.85f,
            stage.getBackgroundColor().b * 0.9f, 0.55f);
        batch.draw(whitePixel, left, bottom, width, height);

        if (stage.getTilesetType() == TilesetType.FUTURE) {
            drawGlow(batch, softGlow, camera.position.x - 260f, camera.position.y + 180f, 340f, FUTURE_CYAN, 0.14f);
            drawGlow(batch, softGlow, camera.position.x + 280f, camera.position.y - 120f, 300f, FUTURE_VIOLET, 0.11f);
            drawGlow(batch, softGlow, camera.position.x, camera.position.y + 12f, 480f, stage.getAccentColor(), 0.07f);
            for (int index = -2; index <= 2; index++) {
                float stripeY = camera.position.y - 220f + index * 104f;
                batch.setColor(FUTURE_CYAN.r, FUTURE_CYAN.g, FUTURE_CYAN.b, 0.035f);
                batch.draw(whitePixel, left, stripeY, width, 4f);
            }
        } else {
            drawGlow(batch, softGlow, camera.position.x - 220f, camera.position.y + 190f, 420f, PREHISTORY_WARM, 0.14f);
            drawGlow(batch, softGlow, camera.position.x + 320f, camera.position.y - 80f, 300f, PREHISTORY_COOL, 0.08f);
            drawGlow(batch, softGlow, camera.position.x - 20f, camera.position.y - 120f, 360f, stage.getAccentColor(), 0.05f);
            batch.setColor(0f, 0f, 0f, 0.05f);
            batch.draw(whitePixel, left, camera.position.y + 180f, width, 120f);
        }
    }

    /**
     * Ajoute des variations visuelles locales pour casser l'aspect quadrillé de la map.
     */
    private void drawTileMood(SpriteBatch batch, Texture whitePixel, Texture softGlow, StageDefinition stage, int tileIndex,
                              float tileX, float tileY, long noise) {
        float darkness = 0.022f + (noise & 3L) * 0.008f;
        batch.setColor(0f, 0f, 0f, darkness);
        batch.draw(whitePixel, tileX, tileY, StageDefinition.TILE_SIZE, StageDefinition.TILE_SIZE);

        if ((noise & 31L) == 0L) {
            Color sparkleColor = stage.getTilesetType() == TilesetType.FUTURE ? FUTURE_CYAN : PREHISTORY_WARM;
            drawGlow(batch, softGlow, tileX + 16f, tileY + 16f, StageDefinition.TILE_SIZE * 2.8f, sparkleColor, 0.05f);
        }

        if (!isAccentTile(stage.getTilesetType(), tileIndex)) {
            return;
        }

        Color accent = stage.getTilesetType() == TilesetType.FUTURE ? stage.getAccentColor() : PREHISTORY_WARM;
        float alpha = stage.getTilesetType() == TilesetType.FUTURE ? 0.10f : 0.08f;
        drawGlow(batch, softGlow, tileX + 16f, tileY + 16f, StageDefinition.TILE_SIZE * 2.5f, accent, alpha);
        batch.setColor(accent.r, accent.g, accent.b, alpha * 0.22f);
        batch.draw(whitePixel, tileX + 3f, tileY + 3f, StageDefinition.TILE_SIZE - 6f, StageDefinition.TILE_SIZE - 6f);
    }

    /**
     * Dessine un décor déterministe pour enrichir le terrain sans stocker une grande carte.
     */
    private void drawStageDecor(SpriteBatch batch, GameAssets assets, StageDefinition stage, Texture whitePixel,
                                Texture softGlow, int startCol, int endCol, int startRow, int endRow) {
        TextureRegion[] props = assets.getStageProps(stage.getTilesetType());
        TextureRegion decal = assets.getStageGroundDecal(stage.getTilesetType());
        if (props.length == 0 || decal == null) {
            return;
        }

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                int tileIndex = TerrainGenerator.getTileIndex(stage, col, row);
                if (!isDecorBaseTile(stage.getTilesetType(), tileIndex)) {
                    continue;
                }

                long noise = hash(col, row, stage.getTilesetType() == TilesetType.FUTURE ? 91 : 37);
                float tileX = col * StageDefinition.TILE_SIZE;
                float tileY = row * StageDefinition.TILE_SIZE;

                if ((noise & 63L) == 0L) {
                    float decalSize = StageDefinition.TILE_SIZE * 0.86f;
                    float offsetX = (float) (((noise >>> 8) & 3L) - 1.5f);
                    float offsetY = (float) (((noise >>> 10) & 3L) - 1.5f);
                    Color glowColor = stage.getTilesetType() == TilesetType.FUTURE ? stage.getAccentColor() : PREHISTORY_WARM;
                    drawGlow(batch, softGlow, tileX + 16f, tileY + 16f, StageDefinition.TILE_SIZE * 2.4f, glowColor, 0.05f);
                    batch.setColor(DECAL_TINT);
                    batch.draw(decal, tileX + 2f + offsetX, tileY + 2f + offsetY, decalSize, decalSize);
                }

                if (((noise >>> 7) & 95L) != 0L) {
                    continue;
                }

                TextureRegion prop = props[(int) ((noise >>> 14) % props.length)];
                float size = StageDefinition.TILE_SIZE * (0.92f + ((noise >>> 18) & 3L) * 0.05f);
                float offsetX = (float) (((noise >>> 20) & 7L) - 3f);
                float offsetY = (float) (((noise >>> 23) & 3L) - 1f);
                float drawX = tileX + (StageDefinition.TILE_SIZE - size) * 0.5f + offsetX;
                float drawY = tileY + (StageDefinition.TILE_SIZE - size) * 0.5f + offsetY;
                Color glowColor = stage.getTilesetType() == TilesetType.FUTURE ? stage.getAccentColor() : PREHISTORY_WARM;

                batch.setColor(0f, 0f, 0f, 0.18f);
                batch.draw(whitePixel, drawX + size * 0.18f, drawY - 3f, size * 0.64f, 7f);
                drawGlow(batch, softGlow, drawX + size * 0.5f, drawY + size * 0.45f, size * 2.2f, glowColor,
                    stage.getTilesetType() == TilesetType.FUTURE ? 0.09f : 0.06f);
                batch.setColor(PROP_TINT);
                batch.draw(prop, drawX, drawY, size, size);
            }
        }
    }

    private void drawForegroundVeil(SpriteBatch batch, Texture whitePixel, Texture softGlow, StageDefinition stage,
                                    OrthographicCamera camera) {
        float left = camera.position.x - StageDefinition.WORLD_WIDTH * 0.5f;
        float bottom = camera.position.y - StageDefinition.WORLD_HEIGHT * 0.5f;
        if (stage.getTilesetType() == TilesetType.FUTURE) {
            batch.setColor(0.03f, 0.08f, 0.10f, 0.10f);
            batch.draw(whitePixel, left, bottom, StageDefinition.WORLD_WIDTH, 48f);
            drawGlow(batch, softGlow, camera.position.x + 340f, camera.position.y + 180f, 220f, FUTURE_CYAN, 0.06f);
        } else {
            batch.setColor(0f, 0f, 0f, 0.08f);
            batch.draw(whitePixel, left, bottom + StageDefinition.WORLD_HEIGHT - 72f, StageDefinition.WORLD_WIDTH, 72f);
            drawGlow(batch, softGlow, camera.position.x - 360f, camera.position.y + 150f, 260f, PREHISTORY_WARM, 0.05f);
        }
    }

    private boolean isDecorBaseTile(TilesetType tilesetType, int tileIndex) {
        if (tilesetType == TilesetType.FUTURE) {
            return tileIndex == 0 || tileIndex == 1 || tileIndex == 4 || tileIndex == 15;
        }
        return tileIndex == 0 || tileIndex == 1 || tileIndex == 14;
    }

    private boolean isAccentTile(TilesetType tilesetType, int tileIndex) {
        if (tilesetType == TilesetType.FUTURE) {
            return tileIndex == 1 || tileIndex == 2 || tileIndex == 6 || tileIndex == 11 || tileIndex == 13;
        }
        return tileIndex == 6 || tileIndex == 12;
    }

    private void drawGlow(SpriteBatch batch, Texture glow, float centerX, float centerY, float size, Color color, float alpha) {
        batch.setColor(color.r, color.g, color.b, alpha);
        batch.draw(glow, centerX - size * 0.5f, centerY - size * 0.5f, size, size);
    }

    private long hash(int x, int y, int salt) {
        long value = x * 73428767L ^ y * 912931L ^ salt * 18233L;
        value ^= (value << 13);
        value ^= (value >>> 7);
        value ^= (value << 17);
        return value & 0x7fffffffL;
    }
}
