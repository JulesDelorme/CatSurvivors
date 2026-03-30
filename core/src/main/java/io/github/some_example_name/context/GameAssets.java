package io.github.some_example_name.context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.game.CatAnim;
import io.github.some_example_name.game.EnemyArchetype;
import io.github.some_example_name.game.PassiveType;
import io.github.some_example_name.game.WeaponType;
import io.github.some_example_name.game.stage.TilesetType;

public class GameAssets {
    private static final int TILE_SIZE = 32;

    private final Array<Texture> managedTextures = new Array<Texture>();
    private final Texture whitePixel;
    private final TextureRegion[] prehistoricTiles;
    private final TextureRegion[] futureTiles;
    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> runAnimation;
    private final Animation<TextureRegion> jumpAnimation;
    private final Animation<TextureRegion> fallAnimation;
    private final TextureRegion[] ravenIcons;
    private final ObjectMap<String, TextureRegion> enemySprites = new ObjectMap<String, TextureRegion>();

    public GameAssets() {
        whitePixel = createWhitePixel();
        prehistoricTiles = loadTiles("tilesets/prehistoric_tileset_32.png");
        futureTiles = loadTiles("tilesets/future_tileset_32.png");
        idleAnimation = loadAnimation("characters/cat/cat_idle_sheet.png", 8, 0.15f);
        runAnimation = loadAnimation("characters/cat/cat_run_sheet.png", 10, 0.08f);
        jumpAnimation = loadAnimation("characters/cat/cat_jump_sheet.png", 4, 0.12f);
        fallAnimation = loadAnimation("characters/cat/cat_fall_sheet.png", 4, 0.12f);
        ravenIcons = loadRavenIcons();
        loadEnemySprites();
    }

    private Texture createWhitePixel() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        pixmap.dispose();
        managedTextures.add(texture);
        return texture;
    }

    private Texture loadTexture(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        managedTextures.add(texture);
        return texture;
    }

    private TextureRegion[] loadTiles(String path) {
        Texture texture = loadTexture(path);
        TextureRegion[][] split = TextureRegion.split(texture, TILE_SIZE, TILE_SIZE);
        TextureRegion[] flat = new TextureRegion[split.length * split[0].length];
        int index = 0;
        for (TextureRegion[] row : split) {
            for (TextureRegion region : row) {
                flat[index++] = region;
            }
        }
        return flat;
    }

    private Animation<TextureRegion> loadAnimation(String path, int frameCount, float frameDuration) {
        Texture texture = loadTexture(path);
        TextureRegion[][] split = TextureRegion.split(texture, TILE_SIZE, TILE_SIZE);
        Array<TextureRegion> frames = new Array<TextureRegion>(frameCount);
        for (int index = 0; index < frameCount; index++) {
            frames.add(split[0][index]);
        }
        return new Animation<TextureRegion>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    private TextureRegion[] loadRavenIcons() {
        Texture texture = loadTexture("icons/raven_fantasy_32.png");
        TextureRegion[][] split = TextureRegion.split(texture, TILE_SIZE, TILE_SIZE);
        TextureRegion[] selected = new TextureRegion[9];
        selected[0] = split[0][0];
        selected[1] = split[0][1];
        selected[2] = split[0][2];
        selected[3] = split[0][3];
        selected[4] = split[1][0];
        selected[5] = split[1][1];
        selected[6] = split[1][2];
        selected[7] = split[1][3];
        selected[8] = split[2][0];
        return selected;
    }

    private void loadEnemySprites() {
        Texture prehistoric = loadTexture("sprites/prehistory/kenney_desert_enemies_24.png");
        enemySprites.put("prehistory_grunt", new TextureRegion(prehistoric, 0, 25, 24, 24));
        enemySprites.put("prehistory_runner", new TextureRegion(prehistoric, 75, 0, 24, 24));
        enemySprites.put("prehistory_tank", new TextureRegion(prehistoric, 0, 50, 24, 24));
        enemySprites.put("prehistory_elite", new TextureRegion(prehistoric, 25, 75, 24, 24));

        enemySprites.put("future_grunt", new TextureRegion(loadTexture("sprites/future/robot_blue.png")));
        enemySprites.put("future_runner", new TextureRegion(loadTexture("sprites/future/robot_green.png")));
        enemySprites.put("future_tank", new TextureRegion(loadTexture("sprites/future/robot_red.png")));
        enemySprites.put("future_elite", new TextureRegion(loadTexture("sprites/future/robot_yellow.png")));
    }

    public Texture getWhitePixel() {
        return whitePixel;
    }

    public TextureRegion[] getTiles(TilesetType tilesetType) {
        return tilesetType == TilesetType.FUTURE ? futureTiles : prehistoricTiles;
    }

    public Animation<TextureRegion> getCatAnimation(CatAnim animation) {
        switch (animation) {
            case RUN:
                return runAnimation;
            case IDLE:
            default:
                return idleAnimation;
        }
    }

    public Animation<TextureRegion> getJumpAnimation() {
        return jumpAnimation;
    }

    public Animation<TextureRegion> getFallAnimation() {
        return fallAnimation;
    }

    public TextureRegion getWeaponIcon(WeaponType type) {
        switch (type) {
            case HAIRBALL:
                return ravenIcons[0];
            case STONE_SPRAY:
                return ravenIcons[1];
            case BONE_DART:
                return ravenIcons[2];
            case ORBIT_CLAWS:
            default:
                return ravenIcons[3];
        }
    }

    public TextureRegion getPassiveIcon(PassiveType type) {
        switch (type) {
            case SPEED:
                return ravenIcons[4];
            case DAMAGE:
                return ravenIcons[5];
            case ATTACK_SPEED:
                return ravenIcons[6];
            case MAGNET:
                return ravenIcons[7];
            case VITALITY:
            default:
                return ravenIcons[8];
        }
    }

    public TextureRegion getEnemySprite(EnemyArchetype archetype) {
        return enemySprites.get(archetype.spriteKey);
    }

    public void dispose() {
        for (Texture texture : managedTextures) {
            texture.dispose();
        }
    }
}
