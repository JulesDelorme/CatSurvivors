package io.github.some_example_name.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.some_example_name.context.GameAssets;
import io.github.some_example_name.game.stage.StageDefinition;
import io.github.some_example_name.game.stage.TerrainGenerator;
import io.github.some_example_name.game.stage.TilesetType;

public class MapRenderer {
    private static final Color DECAL_TINT = new Color(1f, 1f, 1f, 0.26f);
    private static final Color PROP_TINT = new Color(1f, 1f, 1f, 0.94f);

    public void draw(SpriteBatch batch, GameAssets assets, StageDefinition stage, OrthographicCamera camera) {
        TextureRegion[] tiles = assets.getTiles(stage.tilesetType);
        batch.setColor(Color.WHITE);
        int startCol = (int) Math.floor((camera.position.x - StageDefinition.WORLD_WIDTH * 0.5f) / StageDefinition.TILE_SIZE) - 2;
        int endCol = (int) Math.ceil((camera.position.x + StageDefinition.WORLD_WIDTH * 0.5f) / StageDefinition.TILE_SIZE) + 2;
        int startRow = (int) Math.floor((camera.position.y - StageDefinition.WORLD_HEIGHT * 0.5f) / StageDefinition.TILE_SIZE) - 2;
        int endRow = (int) Math.ceil((camera.position.y + StageDefinition.WORLD_HEIGHT * 0.5f) / StageDefinition.TILE_SIZE) + 2;

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                TextureRegion region = tiles[TerrainGenerator.getTileIndex(stage, col, row)];
                batch.draw(region, col * StageDefinition.TILE_SIZE, row * StageDefinition.TILE_SIZE,
                    StageDefinition.TILE_SIZE, StageDefinition.TILE_SIZE);
            }
        }

        drawStageDecor(batch, assets, stage, startCol, endCol, startRow, endRow);
        batch.setColor(Color.WHITE);
    }

    private void drawStageDecor(SpriteBatch batch, GameAssets assets, StageDefinition stage, int startCol, int endCol,
                                int startRow, int endRow) {
        TextureRegion[] props = assets.getStageProps(stage.tilesetType);
        TextureRegion decal = assets.getStageGroundDecal(stage.tilesetType);
        if (props.length == 0 || decal == null) {
            return;
        }

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                int tileIndex = TerrainGenerator.getTileIndex(stage, col, row);
                if (!isDecorBaseTile(stage.tilesetType, tileIndex)) {
                    continue;
                }

                long noise = hash(col, row, stage.tilesetType == TilesetType.FUTURE ? 91 : 37);
                float tileX = col * StageDefinition.TILE_SIZE;
                float tileY = row * StageDefinition.TILE_SIZE;

                if ((noise & 127L) == 0L) {
                    float decalSize = StageDefinition.TILE_SIZE * 0.86f;
                    float offsetX = (float) (((noise >>> 8) & 3L) - 1.5f);
                    float offsetY = (float) (((noise >>> 10) & 3L) - 1.5f);
                    batch.setColor(DECAL_TINT);
                    batch.draw(decal, tileX + 2f + offsetX, tileY + 2f + offsetY, decalSize, decalSize);
                }

                if (((noise >>> 7) & 127L) != 0L) {
                    continue;
                }

                TextureRegion prop = props[(int) ((noise >>> 14) % props.length)];
                float size = StageDefinition.TILE_SIZE * (0.92f + ((noise >>> 18) & 3L) * 0.05f);
                float offsetX = (float) (((noise >>> 20) & 7L) - 3f);
                float offsetY = (float) (((noise >>> 23) & 3L) - 1f);
                batch.setColor(PROP_TINT);
                batch.draw(prop, tileX + (StageDefinition.TILE_SIZE - size) * 0.5f + offsetX,
                    tileY + (StageDefinition.TILE_SIZE - size) * 0.5f + offsetY, size, size);
            }
        }
    }

    private boolean isDecorBaseTile(TilesetType tilesetType, int tileIndex) {
        if (tilesetType == TilesetType.FUTURE) {
            return tileIndex == 0 || tileIndex == 1 || tileIndex == 4 || tileIndex == 15;
        }
        return tileIndex == 0 || tileIndex == 1 || tileIndex == 14;
    }

    private long hash(int x, int y, int salt) {
        long value = x * 73428767L ^ y * 912931L ^ salt * 18233L;
        value ^= (value << 13);
        value ^= (value >>> 7);
        value ^= (value << 17);
        return value & 0x7fffffffL;
    }
}
