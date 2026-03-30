package io.github.some_example_name.game.session;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.game.Enemy;
import io.github.some_example_name.game.EnemyArchetype;
import io.github.some_example_name.game.ExperienceOrb;
import io.github.some_example_name.game.OrbitBlade;
import io.github.some_example_name.game.PassiveType;
import io.github.some_example_name.game.Player;
import io.github.some_example_name.game.Projectile;
import io.github.some_example_name.game.WeaponType;
import io.github.some_example_name.game.stage.StageDefinition;
import io.github.some_example_name.game.upgrade.UpgradeCategory;
import io.github.some_example_name.game.upgrade.UpgradeChoice;
import io.github.some_example_name.game.weapon.OrbitWeapon;
import io.github.some_example_name.game.weapon.ProjectileWeapon;
import io.github.some_example_name.game.weapon.Weapon;

import java.util.EnumMap;

public class GameSession {
    private static final float BASE_PLAYER_SPEED = 280f;
    private static final float BASE_PICKUP_MAGNET = 96f;
    private static final float BASE_PICKUP_TOUCH = 24f;
    private static final float ORBIT_HIT_COOLDOWN = 0.20f;
    private static final float ENEMY_SPAWN_RADIUS_MIN = StageDefinition.WORLD_HEIGHT * 0.62f;
    private static final float ENEMY_SPAWN_RADIUS_MAX = StageDefinition.WORLD_HEIGHT * 0.85f;
    private static final float ENEMY_DESPAWN_RADIUS = StageDefinition.WORLD_WIDTH * 1.6f;

    private final StageDefinition stage;
    private final Player player = new Player(0f, 0f);
    private final Array<Enemy> enemies = new Array<Enemy>();
    private final Array<Projectile> projectiles = new Array<Projectile>();
    private final Array<ExperienceOrb> experienceOrbs = new Array<ExperienceOrb>();
    private final Array<OrbitBlade> orbitBlades = new Array<OrbitBlade>();
    private final Array<UpgradeChoice> levelChoices = new Array<UpgradeChoice>();
    private final EnumMap<WeaponType, Weapon> weapons = new EnumMap<WeaponType, Weapon>(WeaponType.class);
    private final EnumMap<PassiveType, Integer> passiveLevels = new EnumMap<PassiveType, Integer>(PassiveType.class);
    private final Vector2 attackDirectionBuffer = new Vector2();
    private final Vector2 weaponDirectionBuffer = new Vector2();

    private SessionState state = SessionState.RUNNING;
    private float survivalTime;
    private float spawnTimer;
    private float currentXp;
    private float xpToNextLevel;
    private int level = 1;
    private int pendingLevelUps;
    private boolean finalWaveTriggered;

    public GameSession(StageDefinition stage) {
        this.stage = stage;
        for (PassiveType type : PassiveType.values()) {
            passiveLevels.put(type, 0);
        }

        weapons.put(WeaponType.HAIRBALL, new ProjectileWeapon(WeaponType.HAIRBALL, "Canon à poils", 1));
        weapons.put(WeaponType.STONE_SPRAY, new ProjectileWeapon(WeaponType.STONE_SPRAY, "Spray de pierres", 0));
        weapons.put(WeaponType.BONE_DART, new ProjectileWeapon(WeaponType.BONE_DART, "Dards d'os", 0));
        weapons.put(WeaponType.ORBIT_CLAWS, new OrbitWeapon(0));

        xpToNextLevel = getXpThreshold(level);
        refreshDerivedStats();
        spawnTimer = stage.getSpawnInterval(0f) * 0.8f;
    }

    public StageDefinition getStage() {
        return stage;
    }

    public Player getPlayer() {
        return player;
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public Array<Projectile> getProjectiles() {
        return projectiles;
    }

    public Array<ExperienceOrb> getExperienceOrbs() {
        return experienceOrbs;
    }

    public Array<OrbitBlade> getOrbitBlades() {
        return orbitBlades;
    }

    public Array<UpgradeChoice> getLevelChoices() {
        return levelChoices;
    }

    public SessionState getState() {
        return state;
    }

    public float getSurvivalTime() {
        return survivalTime;
    }

    public float getCurrentXp() {
        return currentXp;
    }

    public float getXpToNextLevel() {
        return xpToNextLevel;
    }

    public int getLevel() {
        return level;
    }

    public boolean isStageCleared() {
        return state == SessionState.WON;
    }

    public boolean isLost() {
        return state == SessionState.LOST;
    }

    public void togglePause() {
        if (state == SessionState.RUNNING) {
            state = SessionState.PAUSED;
        } else if (state == SessionState.PAUSED) {
            state = SessionState.RUNNING;
        }
    }

    public void setMovement(float horizontal, float vertical) {
        player.setMovement(horizontal, vertical);
    }

    public void update(float delta) {
        if (state != SessionState.RUNNING) {
            return;
        }

        float frameDelta = Math.min(delta, 1f / 20f);
        survivalTime += frameDelta;

        player.update(frameDelta);

        updateExperienceOrbs(frameDelta);
        if (state != SessionState.RUNNING) {
            return;
        }

        for (Weapon weapon : weapons.values()) {
            weapon.update(this, frameDelta);
        }

        updateProjectiles(frameDelta);
        updateEnemies(frameDelta);
        resolveProjectileHits();
        resolveOrbitHits();
        removeInactiveEntities();
        applyContactDamage(frameDelta);
        if (state != SessionState.RUNNING) {
            return;
        }

        triggerFinalWaveIfNeeded();
        if (!finalWaveTriggered) {
            spawnEnemies(frameDelta);
        }

        if (survivalTime >= stage.durationSeconds && finalWaveTriggered && !hasEliteAlive()) {
            state = SessionState.WON;
        }
    }

    public float getDamageMultiplier() {
        return 1f + getPassiveLevel(PassiveType.DAMAGE) * 0.18f;
    }

    public float getAttackSpeedFactor() {
        return Math.max(0.55f, 1f - getPassiveLevel(PassiveType.ATTACK_SPEED) * 0.08f);
    }

    public float getPickupMagnetRadius() {
        return BASE_PICKUP_MAGNET + getPassiveLevel(PassiveType.MAGNET) * 30f;
    }

    public float getPickupTouchRadius() {
        return BASE_PICKUP_TOUCH + getPassiveLevel(PassiveType.MAGNET) * 3f;
    }

    public Array<Weapon> getOwnedWeapons() {
        Array<Weapon> ownedWeapons = new Array<Weapon>();
        for (Weapon weapon : weapons.values()) {
            if (weapon.isUnlocked()) {
                ownedWeapons.add(weapon);
            }
        }
        return ownedWeapons;
    }

    public int getPassiveLevel(PassiveType passiveType) {
        Integer value = passiveLevels.get(passiveType);
        return value == null ? 0 : value;
    }

    public void chooseUpgrade(int index) {
        if (state != SessionState.LEVEL_UP || index < 0 || index >= levelChoices.size) {
            return;
        }

        UpgradeChoice choice = levelChoices.get(index);
        if (choice.category == UpgradeCategory.WEAPON) {
            weapons.get(choice.weaponType).applyUpgrade(this);
        } else {
            applyPassiveUpgrade(choice.passiveType);
        }

        pendingLevelUps--;
        if (pendingLevelUps > 0) {
            openLevelChoices();
        } else {
            levelChoices.clear();
            state = SessionState.RUNNING;
        }
    }

    public Enemy findNearestEnemy(float radius) {
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

    public void fireAimedBurst(int projectileCount, float spreadDegrees, float speed, float radius, float damage,
                               int remainingHits, float maxDistance, WeaponType weaponType, float aimRange) {
        Enemy target = findNearestEnemy(aimRange);
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

    public void fireRadialBurst(int projectileCount, float speed, float radius, float damage, int remainingHits,
                                float maxDistance, WeaponType weaponType) {
        float startAngle = MathUtils.random(0f, 359f);
        for (int index = 0; index < projectileCount; index++) {
            float angle = startAngle + 360f * index / projectileCount;
            weaponDirectionBuffer.set(1f, 0f).setAngleDeg(angle);
            projectiles.add(new Projectile(
                player.position.x,
                player.position.y,
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

    public void ensureOrbitBladeCount(int count, float orbitRadius, float bladeSize, float damage) {
        while (orbitBlades.size < count) {
            float angle = 360f * orbitBlades.size / Math.max(1, count);
            orbitBlades.add(new OrbitBlade(angle, orbitRadius, bladeSize, damage));
        }
        while (orbitBlades.size > count) {
            orbitBlades.removeIndex(orbitBlades.size - 1);
        }
    }

    private void refreshDerivedStats() {
        float previousMaxHealth = player.maxHealth;
        player.maxHealth = Player.BASE_MAX_HEALTH + getPassiveLevel(PassiveType.VITALITY) * 20f;
        player.speed = BASE_PLAYER_SPEED + getPassiveLevel(PassiveType.SPEED) * 28f;
        if (previousMaxHealth == 0f) {
            player.health = player.maxHealth;
        } else {
            player.health = Math.min(player.health, player.maxHealth);
        }
    }

    private void applyPassiveUpgrade(PassiveType passiveType) {
        passiveLevels.put(passiveType, Math.min(5, getPassiveLevel(passiveType) + 1));
        float previousMaxHealth = player.maxHealth;
        refreshDerivedStats();
        if (passiveType == PassiveType.VITALITY) {
            player.health = Math.min(player.maxHealth, Math.max(player.health, previousMaxHealth) + 20f);
        }
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
                if (state == SessionState.LEVEL_UP) {
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
        return 8f + (currentLevel - 1) * 5f;
    }

    private void updateProjectiles(float delta) {
        for (Projectile projectile : projectiles) {
            if (!projectile.active) {
                continue;
            }
            projectile.update(delta);
            if (projectile.position.dst2(player.position) > ENEMY_DESPAWN_RADIUS * ENEMY_DESPAWN_RADIUS) {
                projectile.active = false;
            }
        }
    }

    private void updateEnemies(float delta) {
        float speedBonus = stage.getSpeedBonus(survivalTime);
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
                if (enemy.applyDamage(projectile.damage)) {
                    spawnExperienceOrb(enemy);
                }
                projectile.registerHit();
                if (!projectile.active) {
                    break;
                }
            }
        }
    }

    private void resolveOrbitHits() {
        if (orbitBlades.size == 0) {
            return;
        }

        for (OrbitBlade blade : orbitBlades) {
            for (Enemy enemy : enemies) {
                if (!enemy.alive || enemy.orbitDamageCooldown > 0f) {
                    continue;
                }
                float combined = blade.size + enemy.archetype.radius;
                if (blade.position.dst2(enemy.position) > combined * combined) {
                    continue;
                }
                enemy.orbitDamageCooldown = ORBIT_HIT_COOLDOWN;
                if (enemy.applyDamage(blade.damage)) {
                    spawnExperienceOrb(enemy);
                }
            }
        }
    }

    private void spawnExperienceOrb(Enemy enemy) {
        experienceOrbs.add(new ExperienceOrb(enemy.position.x, enemy.position.y, enemy.archetype.xpValue));
    }

    private void removeInactiveEntities() {
        for (int index = projectiles.size - 1; index >= 0; index--) {
            if (!projectiles.get(index).active) {
                projectiles.removeIndex(index);
            }
        }
        for (int index = enemies.size - 1; index >= 0; index--) {
            Enemy enemy = enemies.get(index);
            if (!enemy.alive || (!enemy.archetype.elite && enemy.position.dst2(player.position) > ENEMY_DESPAWN_RADIUS * ENEMY_DESPAWN_RADIUS)) {
                enemies.removeIndex(index);
            }
        }
    }

    private void applyContactDamage(float delta) {
        float damageThisFrame = 0f;
        for (Enemy enemy : enemies) {
            if (enemy.alive && enemy.overlaps(player.position, player.radius)) {
                damageThisFrame += enemy.archetype.contactDamagePerSecond * delta;
            }
        }

        if (damageThisFrame <= 0f) {
            return;
        }

        player.health = Math.max(0f, player.health - damageThisFrame);
        player.hitFlashTime = 1f;
        if (player.health <= 0f) {
            state = SessionState.LOST;
        }
    }

    private void spawnEnemies(float delta) {
        spawnTimer -= delta;
        float spawnInterval = stage.getSpawnInterval(survivalTime);
        while (spawnTimer <= 0f) {
            spawnTimer += spawnInterval;
            if (enemies.size < stage.getMaxEnemies(survivalTime)) {
                spawnEnemy(stage.pickEnemyArchetype(survivalTime));
            }
        }
    }

    private void spawnEnemy(EnemyArchetype archetype) {
        float angle = MathUtils.random(0f, 359f);
        float radius = MathUtils.random(ENEMY_SPAWN_RADIUS_MIN, ENEMY_SPAWN_RADIUS_MAX);
        float spawnX = player.position.x + MathUtils.cosDeg(angle) * radius;
        float spawnY = player.position.y + MathUtils.sinDeg(angle) * radius;
        enemies.add(new Enemy(archetype, spawnX, spawnY));
    }

    private void triggerFinalWaveIfNeeded() {
        if (finalWaveTriggered || survivalTime < stage.getFinalWaveStart()) {
            return;
        }
        finalWaveTriggered = true;
        spawnEnemy(stage.elite);
        for (int index = 0; index < stage.finalWaveSupportCount; index++) {
            EnemyArchetype support = index % 2 == 0 ? stage.runner : stage.tank;
            spawnEnemy(support);
        }
    }

    private boolean hasEliteAlive() {
        for (Enemy enemy : enemies) {
            if (enemy.alive && enemy.archetype.elite) {
                return true;
            }
        }
        return false;
    }

    private void openLevelChoices() {
        levelChoices.clear();
        Array<UpgradeChoice> available = new Array<UpgradeChoice>();
        for (Weapon weapon : weapons.values()) {
            if (weapon.canOfferUpgrade()) {
                available.add(weapon.buildChoice(this));
            }
        }
        for (PassiveType passiveType : PassiveType.values()) {
            if (getPassiveLevel(passiveType) < 5) {
                available.add(buildPassiveChoice(passiveType));
            }
        }

        while (levelChoices.size < 3 && available.size > 0) {
            levelChoices.add(available.removeIndex(MathUtils.random(available.size - 1)));
        }

        state = SessionState.LEVEL_UP;
    }

    private UpgradeChoice buildPassiveChoice(PassiveType passiveType) {
        int nextLevel = getPassiveLevel(passiveType) + 1;
        switch (passiveType) {
            case SPEED:
                return new UpgradeChoice(passiveType, "Patounes rapides niv. " + nextLevel, "+28 vitesse de déplacement.", nextLevel);
            case DAMAGE:
                return new UpgradeChoice(passiveType, "Griffes affûtées niv. " + nextLevel, "+18% dégâts sur toutes les armes.", nextLevel);
            case ATTACK_SPEED:
                return new UpgradeChoice(passiveType, "Instinct nerveux niv. " + nextLevel, "Réduit le délai entre les attaques.", nextLevel);
            case MAGNET:
                return new UpgradeChoice(passiveType, "Moustaches aimantées niv. " + nextLevel, "+30 rayon d'aspiration d'XP.", nextLevel);
            case VITALITY:
            default:
                return new UpgradeChoice(passiveType, "Vitalité niv. " + nextLevel, "+20 PV max et soin immédiat.", nextLevel);
        }
    }
}
