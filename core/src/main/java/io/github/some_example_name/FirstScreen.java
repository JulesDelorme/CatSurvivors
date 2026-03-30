package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.game.CatAnim;
import io.github.some_example_name.game.Enemy;
import io.github.some_example_name.game.ExperienceOrb;
import io.github.some_example_name.game.LevelChoice;
import io.github.some_example_name.game.Player;
import io.github.some_example_name.game.Projectile;
import io.github.some_example_name.game.UpgradeType;
import io.github.some_example_name.game.WeaponType;

/** Prehistoric playable slice for CatSurvivors with survivor-style progression. */
public class FirstScreen extends ScreenAdapter {
    private static final int TILE_SIZE = 32;
    private static final int MAP_COLS = 40;
    private static final int MAP_ROWS = 22;
    private static final float WORLD_WIDTH = MAP_COLS * TILE_SIZE;
    private static final float WORLD_HEIGHT = MAP_ROWS * TILE_SIZE;
    private static final float PLAYER_DRAW_SIZE = 96f;
    private static final float SURVIVAL_GOAL = 60f;
    private static final float CONTACT_DAMAGE_PER_SECOND = 22f;
    private static final int MAX_ENEMIES = 60;
    private static final float BASE_PLAYER_SPEED = 280f;
    private static final float BASE_PICKUP_MAGNET = 96f;
    private static final float BASE_PICKUP_TOUCH = 24f;

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

    private static final Color COLOR_PANEL = new Color(0.09f, 0.07f, 0.05f, 0.94f);
    private static final Color COLOR_PANEL_ALT = new Color(0.12f, 0.10f, 0.06f, 0.98f);
    private static final Color COLOR_OVERLAY = new Color(0f, 0f, 0f, 0.72f);
    private static final Color COLOR_ENEMY_BODY = new Color(0.45f, 0.28f, 0.16f, 1f);
    private static final Color COLOR_ENEMY_HIGHLIGHT = new Color(0.63f, 0.44f, 0.25f, 1f);
    private static final Color COLOR_ENEMY_HORN = new Color(0.92f, 0.84f, 0.63f, 1f);
    private static final Color COLOR_PROJECTILE_HAIRBALL = new Color(0.32f, 0.22f, 0.16f, 1f);
    private static final Color COLOR_PROJECTILE_HAIRBALL_CORE = new Color(0.70f, 0.58f, 0.45f, 1f);
    private static final Color COLOR_PROJECTILE_STONE = new Color(0.54f, 0.52f, 0.48f, 1f);
    private static final Color COLOR_PROJECTILE_STONE_CORE = new Color(0.82f, 0.78f, 0.70f, 1f);
    private static final Color COLOR_PROJECTILE_BONE = new Color(0.91f, 0.86f, 0.72f, 1f);
    private static final Color COLOR_PROJECTILE_BONE_CORE = new Color(0.66f, 0.56f, 0.45f, 1f);
    private static final Color COLOR_XP = new Color(0.35f, 0.96f, 0.78f, 1f);
    private static final Color COLOR_XP_CORE = new Color(0.82f, 1f, 0.94f, 1f);
    private static final Color COLOR_SHADOW = new Color(0f, 0f, 0f, 0.18f);
    private static final Color COLOR_BAR_BG = new Color(0f, 0f, 0f, 0.45f);
    private static final Color COLOR_BAR_FILL = new Color(0.23f, 0.84f, 0.72f, 1f);

    private final OrthographicCamera worldCamera = new OrthographicCamera();
    private final Viewport viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, worldCamera);
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private final Matrix4 hudMatrix = new Matrix4();
    private final Array<Texture> managedTextures = new Array<>();
    private final Array<Enemy> enemies = new Array<>();
    private final Array<Projectile> projectiles = new Array<>();
    private final Array<ExperienceOrb> experienceOrbs = new Array<>();
    private final Array<LevelChoice> levelChoices = new Array<>();
    private final Player player = new Player(WORLD_WIDTH * 0.5f, WORLD_HEIGHT * 0.5f);
    private final int[][] prehistoricMap = new int[MAP_ROWS][MAP_COLS];
    private final Vector2 attackDirectionBuffer = new Vector2();
    private final Vector2 weaponDirectionBuffer = new Vector2();
    private final StringBuilder hudBuilder = new StringBuilder();

    private Texture whitePixel;
    private TextureRegion[] prehistoricTiles;
    private TextureRegion[] futureTiles;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> runAnimation;

    private float survivalTime;
    private float spawnTimer;
    private float hairballTimer;
    private float stoneSprayTimer;
    private float boneDartTimer;
    private float currentXp;
    private float xpToNextLevel;
    private int level;
    private int pendingLevelUps;

    private int hairballLevel;
    private int stoneSprayLevel;
    private int boneDartLevel;
    private int swiftPawsLevel;
    private int magnetWhiskersLevel;
    private int vitalityLevel;

    private DemoState state = DemoState.RUNNING;

    @Override
    public void show() {
        if (whitePixel == null) {
            loadAssets();
        }
        font.getData().setScale(1.15f);
        buildPrehistoricMap();
        resetGame();
    }

    @Override
    public void render(float delta) {
        float frameDelta = Math.min(delta, 1f / 20f);
        update(frameDelta);

        Gdx.gl.glClearColor(0.03f, 0.03f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        worldCamera.update();

        drawWorld();
        drawHud();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        for (Texture texture : managedTextures) {
            texture.dispose();
        }
    }

    private void loadAssets() {
        whitePixel = createWhitePixel();
        prehistoricTiles = loadTiles("tilesets/prehistoric_tileset_32.png");
        futureTiles = loadTiles("tilesets/future_tileset_32.png");
        idleAnimation = loadAnimation("characters/cat/cat_idle_sheet.png", 8, 0.15f);
        runAnimation = loadAnimation("characters/cat/cat_run_sheet.png", 10, 0.08f);

        worldCamera.position.set(WORLD_WIDTH * 0.5f, WORLD_HEIGHT * 0.5f, 0f);
        worldCamera.update();
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

    private TextureRegion[] loadTiles(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        managedTextures.add(texture);

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
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        managedTextures.add(texture);

        TextureRegion[][] split = TextureRegion.split(texture, TILE_SIZE, TILE_SIZE);
        Array<TextureRegion> frames = new Array<>(frameCount);
        for (int index = 0; index < frameCount; index++) {
            frames.add(split[0][index]);
        }
        return new Animation<TextureRegion>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    private void buildPrehistoricMap() {
        for (int row = 0; row < MAP_ROWS; row++) {
            for (int col = 0; col < MAP_COLS; col++) {
                prehistoricMap[row][col] = ((row + col) % 5 == 0) ? TILE_GRASS_ALT : TILE_GRASS;
            }
        }

        for (int row = 0; row < MAP_ROWS; row++) {
            prehistoricMap[row][0] = TILE_CLIFF;
            prehistoricMap[row][MAP_COLS - 1] = TILE_CLIFF;
        }
        for (int col = 0; col < MAP_COLS; col++) {
            prehistoricMap[0][col] = TILE_CLIFF;
            prehistoricMap[MAP_ROWS - 1][col] = TILE_CLIFF;
        }

        for (int row = 6; row <= 15; row++) {
            for (int col = 6; col <= 33; col++) {
                prehistoricMap[row][col] = ((row + col) % 4 == 0) ? TILE_DIRT : TILE_GRASS;
            }
        }

        for (int col = 9; col <= 30; col++) {
            prehistoricMap[10][col] = TILE_PATH;
            prehistoricMap[11][col] = TILE_PATH;
        }
        for (int row = 7; row <= 14; row++) {
            prehistoricMap[row][19] = TILE_PATH;
            prehistoricMap[row][20] = TILE_PATH;
        }

        placeTile(3, 5, TILE_TOTEM);
        placeTile(6, 31, TILE_BONES);
        placeTile(16, 6, TILE_STUMP);
        placeTile(17, 30, TILE_STONE);
        placeTile(4, 18, TILE_FIREPIT);
        placeTile(16, 20, TILE_FIREPIT);
        placeTile(8, 9, TILE_BONES);
        placeTile(12, 28, TILE_STUMP);
        placeTile(14, 12, TILE_TOTEM);
        placeTile(18, 17, TILE_STONE);
    }

    private void placeTile(int row, int col, int tileIndex) {
        prehistoricMap[row][col] = tileIndex;
    }

    private void resetGame() {
        enemies.clear();
        projectiles.clear();
        experienceOrbs.clear();
        levelChoices.clear();

        player.reset(WORLD_WIDTH * 0.5f, WORLD_HEIGHT * 0.5f);
        survivalTime = 0f;
        spawnTimer = 0.7f;
        level = 1;
        currentXp = 0f;
        xpToNextLevel = getXpThreshold(level);
        pendingLevelUps = 0;

        hairballLevel = 1;
        stoneSprayLevel = 0;
        boneDartLevel = 0;
        swiftPawsLevel = 0;
        magnetWhiskersLevel = 0;
        vitalityLevel = 0;

        refreshDerivedStats();
        resetWeaponTimers();
        state = DemoState.RUNNING;
    }

    private void refreshDerivedStats() {
        player.maxHealth = Player.BASE_MAX_HEALTH + vitalityLevel * 20f;
        player.health = Math.min(player.health, player.maxHealth);
        player.speed = BASE_PLAYER_SPEED + swiftPawsLevel * 28f;
    }

    private void resetWeaponTimers() {
        hairballTimer = 0.12f;
        stoneSprayTimer = 0.6f;
        boneDartTimer = 0.4f;
    }

    private void update(float delta) {
        if (state == DemoState.LEVEL_UP) {
            handleLevelChoiceInput();
            return;
        }

        if (state == DemoState.WON || state == DemoState.LOST) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                resetGame();
            }
            return;
        }

        survivalTime += delta;
        if (survivalTime >= SURVIVAL_GOAL) {
            survivalTime = SURVIVAL_GOAL;
            state = DemoState.WON;
            return;
        }

        handlePlayerInput();
        player.update(delta);
        clampPlayerInsideArena();
        updateExperienceOrbs(delta);
        if (state != DemoState.RUNNING) {
            return;
        }

        updateWeapons(delta);
        updateProjectiles(delta);
        updateEnemies(delta);
        resolveProjectileHits();
        removeInactiveEntities();
        applyContactDamage(delta);
        if (state != DemoState.RUNNING) {
            return;
        }

        spawnEnemies(delta);
    }

    private void handlePlayerInput() {
        float horizontal = 0f;
        float vertical = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            horizontal -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            horizontal += 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            vertical -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            vertical += 1f;
        }

        player.setMovement(horizontal, vertical);
    }

    private void handleLevelChoiceInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) {
            applyLevelChoice(0);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
            applyLevelChoice(1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)) {
            applyLevelChoice(2);
        }
    }

    private void clampPlayerInsideArena() {
        float margin = TILE_SIZE + player.radius;
        player.position.x = MathUtils.clamp(player.position.x, margin, WORLD_WIDTH - margin);
        player.position.y = MathUtils.clamp(player.position.y, margin, WORLD_HEIGHT - margin);
    }

    private void updateExperienceOrbs(float delta) {
        float magnetRadius = getPickupMagnetRadius();
        float pickupTouchRadius = getPickupTouchRadius();

        for (ExperienceOrb orb : experienceOrbs) {
            if (!orb.active) {
                continue;
            }

            orb.update(player.position, magnetRadius, delta);
            if (orb.overlaps(player.position, pickupTouchRadius)) {
                orb.active = false;
                addExperience(orb.value);
                if (state == DemoState.LEVEL_UP) {
                    break;
                }
            }
        }

        for (int index = experienceOrbs.size - 1; index >= 0; index--) {
            if (!experienceOrbs.get(index).active) {
                experienceOrbs.removeIndex(index);
            }
        }
    }

    private void addExperience(int amount) {
        currentXp += amount;
        while (currentXp >= xpToNextLevel) {
            currentXp -= xpToNextLevel;
            level++;
            pendingLevelUps++;
            xpToNextLevel = getXpThreshold(level);
        }

        if (pendingLevelUps > 0) {
            openLevelChoices();
        }
    }

    private float getXpThreshold(int currentLevel) {
        return 6f + (currentLevel - 1) * 4f;
    }

    private void updateWeapons(float delta) {
        hairballTimer -= delta;
        while (hairballTimer <= 0f) {
            hairballTimer += getHairballCooldown();
            fireHairballs();
        }

        if (stoneSprayLevel > 0) {
            stoneSprayTimer -= delta;
            while (stoneSprayTimer <= 0f) {
                stoneSprayTimer += getStoneSprayCooldown();
                fireStoneSpray();
            }
        }

        if (boneDartLevel > 0) {
            boneDartTimer -= delta;
            while (boneDartTimer <= 0f) {
                boneDartTimer += getBoneDartCooldown();
                fireBoneDarts();
            }
        }
    }

    private void fireHairballs() {
        fireAimedProjectileBurst(
            getHairballCount(),
            getHairballSpread(),
            520f,
            6f,
            getHairballDamage(),
            1,
            getHairballRange(),
            WeaponType.HAIRBALL
        );
    }

    private void fireStoneSpray() {
        int count = getStoneSprayCount();
        float angleOffset = MathUtils.random(0f, 359f);
        for (int index = 0; index < count; index++) {
            float angle = angleOffset + 360f * index / count;
            weaponDirectionBuffer.set(1f, 0f).setAngleDeg(angle);
            projectiles.add(new Projectile(
                player.position.x,
                player.position.y,
                weaponDirectionBuffer,
                340f,
                5f,
                getStoneSprayDamage(),
                1,
                220f,
                WeaponType.STONE_SPRAY
            ));
        }
    }

    private void fireBoneDarts() {
        fireAimedProjectileBurst(
            getBoneDartCount(),
            getBoneDartSpread(),
            620f,
            7f,
            getBoneDartDamage(),
            getBoneDartHits(),
            520f,
            WeaponType.BONE_DART
        );
    }

    private void fireAimedProjectileBurst(int projectileCount, float spreadDegrees, float speed, float radius,
                                          float damage, int remainingHits, float maxDistance, WeaponType weaponType) {
        Enemy target = findNearestEnemy(getAimRange());
        if (target != null) {
            attackDirectionBuffer.set(target.position).sub(player.position).nor();
        } else {
            attackDirectionBuffer.set(player.lastAimDirection);
        }

        if (attackDirectionBuffer.isZero(0.001f)) {
            attackDirectionBuffer.set(1f, 0f);
        }

        for (int index = 0; index < projectileCount; index++) {
            float spreadOffset = (index - (projectileCount - 1) * 0.5f) * spreadDegrees;
            weaponDirectionBuffer.set(attackDirectionBuffer).rotateDeg(spreadOffset);
            projectiles.add(new Projectile(
                player.position.x,
                player.position.y + 4f,
                weaponDirectionBuffer,
                speed,
                radius,
                damage,
                remainingHits,
                maxDistance,
                weaponType
            ));
        }
    }

    private Enemy findNearestEnemy(float radius) {
        float maxDistanceSquared = radius * radius;
        Enemy closest = null;
        float closestDistanceSquared = Float.MAX_VALUE;

        for (Enemy enemy : enemies) {
            if (!enemy.alive) {
                continue;
            }

            float distanceSquared = enemy.position.dst2(player.position);
            if (distanceSquared > maxDistanceSquared || distanceSquared >= closestDistanceSquared) {
                continue;
            }

            closest = enemy;
            closestDistanceSquared = distanceSquared;
        }
        return closest;
    }

    private void updateProjectiles(float delta) {
        for (Projectile projectile : projectiles) {
            if (!projectile.active) {
                continue;
            }

            projectile.update(delta);
            if (projectile.position.x < -64f || projectile.position.x > WORLD_WIDTH + 64f
                || projectile.position.y < -64f || projectile.position.y > WORLD_HEIGHT + 64f) {
                projectile.active = false;
            }
        }
    }

    private void updateEnemies(float delta) {
        float speedBonus = getEnemySpeedBonus();
        for (Enemy enemy : enemies) {
            if (enemy.alive) {
                enemy.update(player.position, speedBonus, delta);
            }
        }
    }

    private void resolveProjectileHits() {
        for (Projectile projectile : projectiles) {
            if (!projectile.active) {
                continue;
            }

            for (Enemy enemy : enemies) {
                if (!enemy.alive || !projectile.overlaps(enemy)) {
                    continue;
                }

                boolean killed = enemy.applyDamage(projectile.damage);
                projectile.registerHit();
                if (killed) {
                    spawnExperienceOrb(enemy);
                }
                if (!projectile.active) {
                    break;
                }
            }
        }
    }

    private void spawnExperienceOrb(Enemy enemy) {
        experienceOrbs.add(new ExperienceOrb(enemy.position.x, enemy.position.y, enemy.xpValue));
    }

    private void applyContactDamage(float delta) {
        int touchingEnemies = 0;

        for (Enemy enemy : enemies) {
            if (enemy.alive && enemy.overlaps(player.position, player.radius)) {
                touchingEnemies++;
            }
        }

        if (touchingEnemies == 0) {
            return;
        }

        player.health = Math.max(0f, player.health - touchingEnemies * CONTACT_DAMAGE_PER_SECOND * delta);
        if (player.health <= 0f) {
            state = DemoState.LOST;
        }
    }

    private void removeInactiveEntities() {
        for (int index = projectiles.size - 1; index >= 0; index--) {
            if (!projectiles.get(index).active) {
                projectiles.removeIndex(index);
            }
        }

        for (int index = enemies.size - 1; index >= 0; index--) {
            if (!enemies.get(index).alive) {
                enemies.removeIndex(index);
            }
        }
    }

    private void spawnEnemies(float delta) {
        spawnTimer -= delta;
        float spawnInterval = getSpawnInterval();

        while (spawnTimer <= 0f) {
            spawnTimer += spawnInterval;
            if (enemies.size < MAX_ENEMIES) {
                enemies.add(createEnemy());
            }
        }
    }

    private Enemy createEnemy() {
        int phase = getDifficultyPhase();
        float radius = MathUtils.random(16f, 22f);
        float baseSpeed = 75f + MathUtils.random(-6f, 8f);
        float health = 14f + phase * 7f + MathUtils.random(0f, 4f);
        int xpValue = phase >= 3 ? 3 : (phase >= 2 ? 2 : 1);
        float padding = TILE_SIZE + radius;

        switch (MathUtils.random(3)) {
            case 0:
                return new Enemy(-padding, MathUtils.random(TILE_SIZE, WORLD_HEIGHT - TILE_SIZE), radius, baseSpeed, health, xpValue);
            case 1:
                return new Enemy(WORLD_WIDTH + padding, MathUtils.random(TILE_SIZE, WORLD_HEIGHT - TILE_SIZE), radius, baseSpeed, health, xpValue);
            case 2:
                return new Enemy(MathUtils.random(TILE_SIZE, WORLD_WIDTH - TILE_SIZE), -padding, radius, baseSpeed, health, xpValue);
            default:
                return new Enemy(MathUtils.random(TILE_SIZE, WORLD_WIDTH - TILE_SIZE), WORLD_HEIGHT + padding, radius, baseSpeed, health, xpValue);
        }
    }

    private float getSpawnInterval() {
        switch (getDifficultyPhase()) {
            case 3:
                return 0.62f;
            case 2:
                return 0.82f;
            case 1:
                return 1.04f;
            default:
                return 1.32f;
        }
    }

    private float getEnemySpeedBonus() {
        switch (getDifficultyPhase()) {
            case 3:
                return 55f;
            case 2:
                return 34f;
            case 1:
                return 18f;
            default:
                return 0f;
        }
    }

    private int getDifficultyPhase() {
        if (survivalTime >= 45f) {
            return 3;
        }
        if (survivalTime >= 30f) {
            return 2;
        }
        if (survivalTime >= 15f) {
            return 1;
        }
        return 0;
    }

    private float getPickupMagnetRadius() {
        return BASE_PICKUP_MAGNET + magnetWhiskersLevel * 28f;
    }

    private float getPickupTouchRadius() {
        return BASE_PICKUP_TOUCH + magnetWhiskersLevel * 3f;
    }

    private float getAimRange() {
        return hairballLevel >= 4 ? 440f : 360f;
    }

    private float getHairballCooldown() {
        switch (hairballLevel) {
            case 1:
                return 0.45f;
            case 2:
            case 3:
                return 0.38f;
            case 4:
                return 0.34f;
            default:
                return 0.30f;
        }
    }

    private float getHairballDamage() {
        switch (hairballLevel) {
            case 1:
                return 14f;
            case 2:
            case 3:
                return 18f;
            case 4:
                return 26f;
            default:
                return 34f;
        }
    }

    private int getHairballCount() {
        if (hairballLevel >= 5) {
            return 3;
        }
        if (hairballLevel >= 3) {
            return 2;
        }
        return 1;
    }

    private float getHairballSpread() {
        if (hairballLevel >= 5) {
            return 18f;
        }
        if (hairballLevel >= 3) {
            return 9f;
        }
        return 0f;
    }

    private float getHairballRange() {
        return hairballLevel >= 4 ? 460f : 380f;
    }

    private float getStoneSprayCooldown() {
        switch (stoneSprayLevel) {
            case 1:
            case 2:
                return 1.80f;
            case 3:
                return 1.55f;
            case 4:
                return 1.35f;
            default:
                return 1.18f;
        }
    }

    private float getStoneSprayDamage() {
        switch (stoneSprayLevel) {
            case 1:
                return 10f;
            case 2:
                return 12f;
            case 3:
                return 16f;
            case 4:
                return 18f;
            default:
                return 22f;
        }
    }

    private int getStoneSprayCount() {
        switch (stoneSprayLevel) {
            case 1:
                return 6;
            case 2:
            case 3:
                return 8;
            case 4:
                return 10;
            default:
                return 12;
        }
    }

    private float getBoneDartCooldown() {
        switch (boneDartLevel) {
            case 1:
            case 2:
                return 1.25f;
            case 3:
                return 1.10f;
            case 4:
                return 0.95f;
            default:
                return 0.85f;
        }
    }

    private float getBoneDartDamage() {
        switch (boneDartLevel) {
            case 1:
            case 2:
                return 22f;
            case 3:
            case 4:
                return 32f;
            default:
                return 44f;
        }
    }

    private int getBoneDartCount() {
        switch (boneDartLevel) {
            case 1:
                return 1;
            case 2:
            case 3:
                return 2;
            default:
                return 3;
        }
    }

    private float getBoneDartSpread() {
        switch (boneDartLevel) {
            case 1:
                return 0f;
            case 2:
            case 3:
                return 12f;
            default:
                return 18f;
        }
    }

    private int getBoneDartHits() {
        switch (boneDartLevel) {
            case 1:
            case 2:
                return 2;
            case 3:
            case 4:
                return 3;
            default:
                return 4;
        }
    }

    private void openLevelChoices() {
        state = DemoState.LEVEL_UP;
        levelChoices.clear();

        Array<UpgradeType> available = new Array<UpgradeType>();
        if (hairballLevel < 5) {
            available.add(UpgradeType.HAIRBALL);
        }
        if (stoneSprayLevel < 5) {
            available.add(UpgradeType.STONE_SPRAY);
        }
        if (boneDartLevel < 5) {
            available.add(UpgradeType.BONE_DART);
        }
        available.add(UpgradeType.SWIFT_PAWS);
        available.add(UpgradeType.MAGNET_WHISKERS);
        available.add(UpgradeType.VITALITY);

        while (levelChoices.size < 3 && available.size > 0) {
            int randomIndex = MathUtils.random(available.size - 1);
            levelChoices.add(buildChoice(available.removeIndex(randomIndex)));
        }
    }

    private LevelChoice buildChoice(UpgradeType type) {
        switch (type) {
            case HAIRBALL:
                return new LevelChoice(type, "Hairball Lv." + (hairballLevel + 1), getHairballChoiceDescription());
            case STONE_SPRAY:
                return new LevelChoice(type, stoneSprayLevel == 0 ? "Unlock Stone Spray" : "Stone Spray Lv." + (stoneSprayLevel + 1),
                    getStoneSprayChoiceDescription());
            case BONE_DART:
                return new LevelChoice(type, boneDartLevel == 0 ? "Unlock Bone Dart" : "Bone Dart Lv." + (boneDartLevel + 1),
                    getBoneDartChoiceDescription());
            case SWIFT_PAWS:
                return new LevelChoice(type, "Swift Paws Lv." + (swiftPawsLevel + 1), "+28 move speed.");
            case MAGNET_WHISKERS:
                return new LevelChoice(type, "Magnet Whiskers Lv." + (magnetWhiskersLevel + 1), "+28 pickup radius.");
            case VITALITY:
            default:
                return new LevelChoice(type, "Vitality Lv." + (vitalityLevel + 1), "+20 max HP and heal 20.");
        }
    }

    private String getHairballChoiceDescription() {
        switch (hairballLevel + 1) {
            case 2:
                return "Faster reload.";
            case 3:
                return "+1 hairball.";
            case 4:
                return "More damage and range.";
            default:
                return "+1 hairball and faster reload.";
        }
    }

    private String getStoneSprayChoiceDescription() {
        switch (stoneSprayLevel + 1) {
            case 1:
                return "Unlock a radial shard burst.";
            case 2:
                return "More rocks per burst.";
            case 3:
                return "Shard damage up.";
            case 4:
                return "Faster bursts.";
            default:
                return "Even more rocks and damage.";
        }
    }

    private String getBoneDartChoiceDescription() {
        switch (boneDartLevel + 1) {
            case 1:
                return "Unlock piercing bone darts.";
            case 2:
                return "+1 dart per volley.";
            case 3:
                return "Dart damage up.";
            case 4:
                return "Faster volleys.";
            default:
                return "+1 dart and more pierce.";
        }
    }

    private void applyLevelChoice(int choiceIndex) {
        if (choiceIndex < 0 || choiceIndex >= levelChoices.size) {
            return;
        }

        LevelChoice choice = levelChoices.get(choiceIndex);
        switch (choice.type) {
            case HAIRBALL:
                hairballLevel = Math.min(5, hairballLevel + 1);
                break;
            case STONE_SPRAY:
                stoneSprayLevel = Math.min(5, stoneSprayLevel + 1);
                break;
            case BONE_DART:
                boneDartLevel = Math.min(5, boneDartLevel + 1);
                break;
            case SWIFT_PAWS:
                swiftPawsLevel++;
                break;
            case MAGNET_WHISKERS:
                magnetWhiskersLevel++;
                break;
            case VITALITY:
                vitalityLevel++;
                break;
            default:
                break;
        }

        float previousMaxHealth = player.maxHealth;
        refreshDerivedStats();
        if (choice.type == UpgradeType.VITALITY) {
            player.health = Math.min(player.maxHealth, Math.max(player.health, previousMaxHealth) + 20f);
        }

        pendingLevelUps--;
        if (pendingLevelUps > 0) {
            openLevelChoices();
        } else {
            levelChoices.clear();
            state = DemoState.RUNNING;
        }
    }

    private void drawWorld() {
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();

        drawMap();
        drawExperienceOrbs();
        drawEnemies();
        drawProjectiles();
        drawPlayer();

        batch.end();
    }

    private void drawMap() {
        for (int row = 0; row < MAP_ROWS; row++) {
            for (int col = 0; col < MAP_COLS; col++) {
                TextureRegion region = prehistoricTiles[prehistoricMap[row][col]];
                batch.setColor(Color.WHITE);
                batch.draw(region, col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void drawExperienceOrbs() {
        for (ExperienceOrb orb : experienceOrbs) {
            float size = 10f + orb.value * 2f;
            float x = orb.position.x - size * 0.5f;
            float y = orb.position.y - size * 0.5f;

            batch.setColor(COLOR_XP);
            batch.draw(whitePixel, x, y, size, size);
            batch.setColor(COLOR_XP_CORE);
            batch.draw(whitePixel, x + 2f, y + 2f, size - 4f, size - 4f);
        }
        batch.setColor(Color.WHITE);
    }

    private void drawEnemies() {
        for (Enemy enemy : enemies) {
            float width = enemy.radius * 2.2f;
            float height = enemy.radius * 1.45f;
            float x = enemy.position.x - width * 0.5f;
            float y = enemy.position.y - height * 0.55f;

            batch.setColor(COLOR_ENEMY_BODY);
            batch.draw(whitePixel, x, y, width, height);

            batch.setColor(COLOR_ENEMY_HIGHLIGHT);
            batch.draw(whitePixel, x + 4f, y + 5f, width - 8f, height - 10f);

            batch.setColor(COLOR_ENEMY_HORN);
            batch.draw(whitePixel, x + 4f, y + height - 2f, 4f, 8f);
            batch.draw(whitePixel, x + width - 8f, y + height - 2f, 4f, 8f);

            batch.setColor(new Color(0.15f, 0.05f, 0.04f, 1f));
            batch.draw(whitePixel, x + width * 0.28f, y + height * 0.45f, 3f, 3f);
            batch.draw(whitePixel, x + width * 0.64f, y + height * 0.45f, 3f, 3f);
        }
        batch.setColor(Color.WHITE);
    }

    private void drawProjectiles() {
        for (Projectile projectile : projectiles) {
            float size = projectile.radius * 2f;
            float x = projectile.position.x - projectile.radius;
            float y = projectile.position.y - projectile.radius;
            Color baseColor = COLOR_PROJECTILE_HAIRBALL;
            Color coreColor = COLOR_PROJECTILE_HAIRBALL_CORE;

            if (projectile.weaponType == WeaponType.STONE_SPRAY) {
                baseColor = COLOR_PROJECTILE_STONE;
                coreColor = COLOR_PROJECTILE_STONE_CORE;
            } else if (projectile.weaponType == WeaponType.BONE_DART) {
                baseColor = COLOR_PROJECTILE_BONE;
                coreColor = COLOR_PROJECTILE_BONE_CORE;
            }

            batch.setColor(baseColor);
            batch.draw(whitePixel, x, y, size, size);
            batch.setColor(coreColor);
            batch.draw(whitePixel, x + 2f, y + 2f, size - 4f, size - 4f);
        }
        batch.setColor(Color.WHITE);
    }

    private void drawPlayer() {
        TextureRegion frame = getCurrentPlayerFrame();
        float drawX = player.position.x - PLAYER_DRAW_SIZE * 0.5f;
        float drawY = player.position.y - PLAYER_DRAW_SIZE * 0.42f;

        batch.setColor(COLOR_SHADOW);
        batch.draw(whitePixel, player.position.x - 18f, player.position.y - 24f, 36f, 10f);

        batch.setColor(Color.WHITE);
        if (player.facingLeft) {
            batch.draw(frame, drawX + PLAYER_DRAW_SIZE, drawY, -PLAYER_DRAW_SIZE, PLAYER_DRAW_SIZE);
        } else {
            batch.draw(frame, drawX, drawY, PLAYER_DRAW_SIZE, PLAYER_DRAW_SIZE);
        }
    }

    private TextureRegion getCurrentPlayerFrame() {
        Animation<TextureRegion> animation = player.anim == CatAnim.RUN ? runAnimation : idleAnimation;
        return animation.getKeyFrame(player.animationTime);
    }

    private void drawHud() {
        hudMatrix.setToOrtho2D(0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);
        batch.setProjectionMatrix(hudMatrix);
        batch.begin();

        drawRect(16f, WORLD_HEIGHT - 154f, 315f, 134f, COLOR_BAR_BG);
        drawRect(30f, WORLD_HEIGHT - 101f, 240f, 18f, new Color(0.16f, 0.14f, 0.12f, 1f));
        drawRect(32f, WORLD_HEIGHT - 99f, 236f * MathUtils.clamp(currentXp / xpToNextLevel, 0f, 1f), 14f, COLOR_BAR_FILL);

        font.setColor(Color.WHITE);
        font.draw(batch, "Age: Prehistory", 30f, WORLD_HEIGHT - 30f);
        font.draw(batch, "Lvl " + level, 30f, WORLD_HEIGHT - 58f);
        font.draw(batch, String.format("Time Left: %.1fs", SURVIVAL_GOAL - survivalTime), 30f, WORLD_HEIGHT - 82f);
        font.draw(batch, String.format("HP: %d / %d", MathUtils.ceil(player.health), MathUtils.ceil(player.maxHealth)), 30f, WORLD_HEIGHT - 126f);
        font.draw(batch, String.format("Enemies: %d", enemies.size), 205f, WORLD_HEIGHT - 126f);

        hudBuilder.setLength(0);
        hudBuilder.append("Hairball ").append(hairballLevel);
        if (stoneSprayLevel > 0) {
            hudBuilder.append(" | Spray ").append(stoneSprayLevel);
        }
        if (boneDartLevel > 0) {
            hudBuilder.append(" | Bone ").append(boneDartLevel);
        }
        font.draw(batch, hudBuilder, WORLD_WIDTH - 340f, WORLD_HEIGHT - 30f);
        font.draw(batch, "Move: WASD / Arrows", WORLD_WIDTH - 340f, WORLD_HEIGHT - 58f);
        font.draw(batch, "Collect XP and pick upgrades", WORLD_WIDTH - 340f, WORLD_HEIGHT - 86f);

        if (state == DemoState.LEVEL_UP) {
            drawLevelUpOverlay();
        } else if (state == DemoState.WON) {
            drawVictoryOverlay();
        } else if (state == DemoState.LOST) {
            drawLossOverlay();
        }

        batch.setColor(Color.WHITE);
        batch.end();
    }

    private void drawLevelUpOverlay() {
        drawRect(0f, 0f, WORLD_WIDTH, WORLD_HEIGHT, COLOR_OVERLAY);

        float panelWidth = 960f;
        float panelHeight = 290f;
        float panelX = (WORLD_WIDTH - panelWidth) * 0.5f;
        float panelY = (WORLD_HEIGHT - panelHeight) * 0.5f;
        drawRect(panelX, panelY, panelWidth, panelHeight, COLOR_PANEL);

        drawCenteredText("Level Up - Choose Your Upgrade", panelY + panelHeight - 34f, new Color(1f, 0.95f, 0.75f, 1f));
        drawCenteredText("Press 1, 2, or 3", panelY + panelHeight - 66f, Color.WHITE);

        float cardWidth = 280f;
        float cardHeight = 170f;
        float gap = 30f;
        float cardsX = panelX + 30f;
        float cardY = panelY + 42f;

        for (int index = 0; index < levelChoices.size; index++) {
            float cardX = cardsX + index * (cardWidth + gap);
            drawRect(cardX, cardY, cardWidth, cardHeight, COLOR_PANEL_ALT);
            drawRect(cardX + 10f, cardY + cardHeight - 38f, 32f, 24f, COLOR_BAR_FILL);
            font.draw(batch, Integer.toString(index + 1), cardX + 21f, cardY + cardHeight - 20f);

            LevelChoice choice = levelChoices.get(index);
            glyphLayout.setText(font, choice.title, new Color(1f, 0.94f, 0.78f, 1f), cardWidth - 26f, Align.center, true);
            font.setColor(new Color(1f, 0.94f, 0.78f, 1f));
            font.draw(batch, glyphLayout, cardX + 13f, cardY + cardHeight - 54f);

            glyphLayout.setText(font, choice.description, Color.WHITE, cardWidth - 26f, Align.center, true);
            font.setColor(Color.WHITE);
            font.draw(batch, glyphLayout, cardX + 13f, cardY + 70f);
        }
        font.setColor(Color.WHITE);
    }

    private void drawVictoryOverlay() {
        drawRect(0f, 0f, WORLD_WIDTH, WORLD_HEIGHT, COLOR_OVERLAY);

        float panelWidth = 700f;
        float panelHeight = 310f;
        float panelX = (WORLD_WIDTH - panelWidth) * 0.5f;
        float panelY = (WORLD_HEIGHT - panelHeight) * 0.5f;
        drawRect(panelX, panelY, panelWidth, panelHeight, COLOR_PANEL);

        drawCenteredText("Prehistory Cleared", WORLD_HEIGHT * 0.5f + 118f, new Color(1f, 0.92f, 0.72f, 1f));
        drawCenteredText("The cat uncovered a strange machine...", WORLD_HEIGHT * 0.5f + 82f, Color.WHITE);
        drawCenteredText("Next Epoch: Robots", WORLD_HEIGHT * 0.5f - 6f, new Color(0.55f, 1f, 0.95f, 1f));

        float previewX = panelX + 158f;
        float previewY = panelY + 86f;
        int[] previewTiles = {0, 1, 2, 3, 11, 13};
        for (int index = 0; index < previewTiles.length; index++) {
            batch.draw(futureTiles[previewTiles[index]], previewX + index * 64f, previewY, 64f, 64f);
        }

        drawCenteredText("Press R to restart the prehistoric demo", panelY + 34f, Color.WHITE);
    }

    private void drawLossOverlay() {
        drawRect(0f, 0f, WORLD_WIDTH, WORLD_HEIGHT, COLOR_OVERLAY);

        float panelWidth = 580f;
        float panelHeight = 186f;
        float panelX = (WORLD_WIDTH - panelWidth) * 0.5f;
        float panelY = (WORLD_HEIGHT - panelHeight) * 0.5f;
        drawRect(panelX, panelY, panelWidth, panelHeight, COLOR_PANEL);

        drawCenteredText("Game Over", WORLD_HEIGHT * 0.5f + 34f, new Color(1f, 0.80f, 0.80f, 1f));
        drawCenteredText("The prehistoric swarm caught the cat.", WORLD_HEIGHT * 0.5f, Color.WHITE);
        drawCenteredText("Press R to try again", WORLD_HEIGHT * 0.5f - 34f, Color.WHITE);
    }

    private void drawRect(float x, float y, float width, float height, Color color) {
        batch.setColor(color);
        batch.draw(whitePixel, x, y, width, height);
        batch.setColor(Color.WHITE);
    }

    private void drawCenteredText(String text, float y, Color color) {
        glyphLayout.setText(font, text);
        font.setColor(color);
        font.draw(batch, glyphLayout, (WORLD_WIDTH - glyphLayout.width) * 0.5f, y);
        font.setColor(Color.WHITE);
    }

    private enum DemoState {
        RUNNING,
        LEVEL_UP,
        WON,
        LOST
    }
}
