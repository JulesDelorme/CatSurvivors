package io.github.some_example_name.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.some_example_name.context.GameAssets;
import io.github.some_example_name.game.stage.StageDefinition;
import io.github.some_example_name.game.stage.TerrainGenerator;

public class MapRenderer {
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
    }
}
