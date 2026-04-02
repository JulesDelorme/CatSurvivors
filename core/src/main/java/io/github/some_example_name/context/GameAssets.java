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
    private final Texture magicOrb;
    private final Texture softGlow;
    private final TextureRegion[] prehistoricTiles;
    private final TextureRegion[] futureTiles;
    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> runAnimation;
    private final TextureRegion[] ravenIcons;
    private final TextureRegion[] mageFocusIcons;
    private final TextureRegion[] mageBookIcons;
    private final TextureRegion[] swordIcons;
    private final TextureRegion[] frostBombIcons;
    private final TextureRegion frostBombProjectile;
    private final TextureRegion frostBombBurst;
    private final TextureRegion frostBombSigil;
    private final TextureRegion frostBombSwirl;
    private final TextureRegion frostBombTrail;
    private final TextureRegion frostBombStreak;
    private final TextureRegion fireCometProjectile;
    private final TextureRegion fireCometBurst;
    private final TextureRegion fireCometSigil;
    private final TextureRegion fireCometStreak;
    private final TextureRegion[] prehistoryProps;
    private final TextureRegion[] futureProps;
    private final TextureRegion prehistoryGroundDecal;
    private final TextureRegion futureGroundDecal;
    private final ObjectMap<String, TextureRegion> enemySprites = new ObjectMap<String, TextureRegion>();

    public GameAssets() {
        whitePixel = createWhitePixel();
        magicOrb = createMagicOrb();
        softGlow = createSoftGlow();
        prehistoricTiles = loadTiles("tilesets/prehistoric_tileset_32.png");
        futureTiles = loadTiles("tilesets/future_tileset_32.png");
        idleAnimation = loadAnimation("characters/cat/cat_idle_sheet.png", 8, 0.15f);
        runAnimation = loadAnimation("characters/cat/cat_run_sheet.png", 10, 0.08f);
        ravenIcons = loadRavenIcons();
        mageFocusIcons = loadTextureRegions(
            "sprites/projectiles/mage_diamond_red.png",
            "sprites/projectiles/mage_diamond_violet.png",
            "sprites/projectiles/mage_diamond_blue.png",
            "sprites/projectiles/mage_diamond_green.png"
        );
        mageBookIcons = loadTextureRegions(
            "sprites/projectiles/mage_bolt_red.png",
            "sprites/projectiles/mage_bolt_violet.png",
            "sprites/projectiles/mage_bolt_blue.png",
            "sprites/projectiles/mage_bolt_green.png"
        );
        swordIcons = loadTextureRegions(
            "sprites/weapons/sword_red.png",
            "sprites/weapons/sword_blue.png",
            "sprites/weapons/sword_silver.png"
        );
        frostBombIcons = loadTextureRegions(
            "sprites/frost/frost_bomb.png",
            "sprites/frost/frost_vortex.png",
            "sprites/frost/frost_swirl.png",
            "sprites/frost/frost_sigil.png"
        );
        frostBombProjectile = new TextureRegion(loadTexture("sprites/frost/frost_bomb.png"));
        frostBombBurst = new TextureRegion(loadTexture("sprites/frost/frost_burst.png"));
        frostBombSigil = new TextureRegion(loadTexture("sprites/frost/frost_sigil.png"));
        frostBombSwirl = new TextureRegion(loadTexture("sprites/frost/frost_swirl.png"));
        frostBombTrail = new TextureRegion(loadTexture("sprites/frost/frost_trail.png"));
        frostBombStreak = new TextureRegion(loadTexture("sprites/frost/frost_streak.png"));
        fireCometProjectile = new TextureRegion(loadTexture("sprites/effects/fire_comet.png"));
        fireCometBurst = new TextureRegion(loadTexture("sprites/effects/fire_burst.png"));
        fireCometSigil = new TextureRegion(loadTexture("sprites/effects/fire_sigil.png"));
        fireCometStreak = new TextureRegion(loadTexture("sprites/effects/fire_streak.png"));
        prehistoryProps = loadTextureRegions(
            "sprites/props/prehistory/prehistory_firepit.png",
            "sprites/props/prehistory/prehistory_brazier.png",
            "sprites/props/prehistory/prehistory_relic.png",
            "sprites/props/prehistory/prehistory_stone.png"
        );
        futureProps = loadTextureRegions(
            "sprites/props/future/future_shield_cyan.png",
            "sprites/props/future/future_shield_violet.png",
            "sprites/props/future/future_core.png"
        );
        prehistoryGroundDecal = new TextureRegion(loadTexture("sprites/props/prehistory/prehistory_rune.png"));
        futureGroundDecal = new TextureRegion(loadTexture("sprites/props/future/future_rune.png"));
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

    private Texture createMagicOrb() {
        int size = 32;
        float radius = (size - 2) * 0.5f;
        float center = (size - 1) * 0.5f;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float dx = x - center;
                float dy = y - center;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                if (distance > radius) {
                    continue;
                }
                float normalized = distance / radius;
                float alpha = 1f - normalized;
                float brightness = 0.45f + (1f - normalized) * 0.55f;
                pixmap.setColor(brightness, brightness, brightness, Math.min(1f, alpha * 1.2f));
                pixmap.drawPixel(x, y);
            }
        }
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        pixmap.dispose();
        managedTextures.add(texture);
        return texture;
    }

    /**
     * Crée un halo procédural réutilisé pour l'ambiance, les lueurs d'UI et les ombres douces.
     */
    private Texture createSoftGlow() {
        int size = 96;
        float radius = (size - 2) * 0.5f;
        float center = (size - 1) * 0.5f;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float dx = x - center;
                float dy = y - center;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                if (distance > radius) {
                    continue;
                }
                float normalized = distance / radius;
                float alpha = 1f - normalized * normalized;
                alpha *= alpha;
                pixmap.setColor(1f, 1f, 1f, alpha * 0.9f);
                pixmap.drawPixel(x, y);
            }
        }
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
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

    private TextureRegion[] loadTextureRegions(String... paths) {
        TextureRegion[] regions = new TextureRegion[paths.length];
        for (int index = 0; index < paths.length; index++) {
            regions[index] = new TextureRegion(loadTexture(paths[index]));
        }
        return regions;
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

    public Texture getMagicOrb() {
        return magicOrb;
    }

    public Texture getSoftGlow() {
        return softGlow;
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

    public TextureRegion getWeaponIcon(WeaponType type, int level) {
        switch (type) {
            case HAIRBALL:
                return ravenIcons[0];
            case STONE_SPRAY:
                return mageBookIcons[getLevelVariantIndex(level, mageBookIcons.length)];
            case BONE_DART:
                return swordIcons[getLevelVariantIndex(level, swordIcons.length)];
            case FROST_BOMB:
                return frostBombIcons[getLevelVariantIndex(level, frostBombIcons.length)];
            case ORBIT_CLAWS:
                return ravenIcons[3];
            default:
                return ravenIcons[0];
        }
    }

    public TextureRegion getMageFocusIcon(int level) {
        return mageFocusIcons[getLevelVariantIndex(level, mageFocusIcons.length)];
    }

    public TextureRegion getSwordIcon(int level) {
        return swordIcons[getLevelVariantIndex(level, swordIcons.length)];
    }

    public TextureRegion getFrostBombProjectile() {
        return frostBombProjectile;
    }

    public TextureRegion getFrostBombBurst() {
        return frostBombBurst;
    }

    public TextureRegion getFrostBombSigil() {
        return frostBombSigil;
    }

    public TextureRegion getFrostBombSwirl() {
        return frostBombSwirl;
    }

    public TextureRegion getFrostBombTrail() {
        return frostBombTrail;
    }

    public TextureRegion getFrostBombStreak() {
        return frostBombStreak;
    }

    public TextureRegion getFireCometProjectile() {
        return fireCometProjectile;
    }

    public TextureRegion getFireCometBurst() {
        return fireCometBurst;
    }

    public TextureRegion getFireCometSigil() {
        return fireCometSigil;
    }

    public TextureRegion getFireCometStreak() {
        return fireCometStreak;
    }

    public TextureRegion[] getStageProps(TilesetType tilesetType) {
        return tilesetType == TilesetType.FUTURE ? futureProps : prehistoryProps;
    }

    public TextureRegion getStageGroundDecal(TilesetType tilesetType) {
        return tilesetType == TilesetType.FUTURE ? futureGroundDecal : prehistoryGroundDecal;
    }

    private int getLevelVariantIndex(int level, int variantCount) {
        int clampedLevel = Math.max(1, Math.min(5, level));
        return Math.min(variantCount - 1, (clampedLevel - 1) * variantCount / 5);
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
        return enemySprites.get(archetype.getSpriteKey());
    }

    public void dispose() {
        for (Texture texture : managedTextures) {
            texture.dispose();
        }
    }
}
