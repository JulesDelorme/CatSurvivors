package io.github.some_example_name.game.session;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.game.Enemy;
import io.github.some_example_name.game.ExperienceOrb;
import io.github.some_example_name.game.FrostPatch;
import io.github.some_example_name.game.OrbitBlade;
import io.github.some_example_name.game.PassiveType;
import io.github.some_example_name.game.Player;
import io.github.some_example_name.game.Projectile;
import io.github.some_example_name.game.WeaponType;
import io.github.some_example_name.game.stage.StageDefinition;
import io.github.some_example_name.game.upgrade.UpgradeChoice;
import io.github.some_example_name.game.weapon.OrbitWeapon;
import io.github.some_example_name.game.weapon.ProjectileWeapon;
import io.github.some_example_name.game.weapon.Weapon;

import java.util.EnumMap;

/**
 * Porte tout l'état runtime d'une partie et délègue les gros blocs métier à des composants dédiés.
 */
public class GameSession {
    private final StageDefinition stage;
    private final Player player = new Player(0f, 0f);
    private final Array<Enemy> enemies = new Array<Enemy>();
    private final Array<Projectile> projectiles = new Array<Projectile>();
    private final Array<ExperienceOrb> experienceOrbs = new Array<ExperienceOrb>();
    private final Array<FrostPatch> frostPatches = new Array<FrostPatch>();
    private final Array<OrbitBlade> orbitBlades = new Array<OrbitBlade>();
    private final Array<UpgradeChoice> levelChoices = new Array<UpgradeChoice>();
    private final EnumMap<WeaponType, Weapon> weapons = new EnumMap<WeaponType, Weapon>(WeaponType.class);
    private final EnumMap<PassiveType, Integer> passiveLevels = new EnumMap<PassiveType, Integer>(PassiveType.class);
    private final Vector2 attackDirectionBuffer = new Vector2();
    private final Vector2 weaponDirectionBuffer = new Vector2();
    private final SessionUpgradeController upgradeController = new SessionUpgradeController();
    private final SessionCombatResolver combatResolver = new SessionCombatResolver();
    private final SessionSpawnController spawnController = new SessionSpawnController();

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

        weapons.put(WeaponType.HAIRBALL, new ProjectileWeapon(WeaponType.HAIRBALL, "Canon à poils", 1));
        weapons.put(WeaponType.STONE_SPRAY, new ProjectileWeapon(WeaponType.STONE_SPRAY, "Livre de mage", 0));
        weapons.put(WeaponType.BONE_DART, new ProjectileWeapon(WeaponType.BONE_DART, "Épée runique", 0));
        weapons.put(WeaponType.FROST_BOMB, new ProjectileWeapon(WeaponType.FROST_BOMB, "Bombe givrante", 0));
        weapons.put(WeaponType.ORBIT_CLAWS, new OrbitWeapon(0));

        upgradeController.initialize(this);
        spawnController.initialize(this);
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

    public Array<FrostPatch> getFrostPatches() {
        return frostPatches;
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

    /**
     * Fait avancer une frame de simulation en bornant le delta pour préserver la stabilité du run.
     */
    public void update(float delta) {
        if (state != SessionState.RUNNING) {
            return;
        }

        float frameDelta = Math.min(delta, 1f / 20f);
        addSurvivalTimeInternal(frameDelta);
        player.update(frameDelta);

        combatResolver.updateExperienceOrbs(this, frameDelta);
        if (state != SessionState.RUNNING) {
            return;
        }

        for (Weapon weapon : weapons.values()) {
            weapon.update(this, frameDelta);
        }

        combatResolver.advanceWorld(this, frameDelta);
        if (state != SessionState.RUNNING) {
            return;
        }

        spawnController.advance(this, frameDelta);
    }

    public float getDamageMultiplier() {
        return upgradeController.getDamageMultiplier(this);
    }

    public float getAttackSpeedFactor() {
        return upgradeController.getAttackSpeedFactor(this);
    }

    public float getPickupMagnetRadius() {
        return upgradeController.getPickupMagnetRadius(this);
    }

    public float getPickupTouchRadius() {
        return upgradeController.getPickupTouchRadius(this);
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

    public int getWeaponLevel(WeaponType weaponType) {
        Weapon weapon = weapons.get(weaponType);
        return weapon == null ? 0 : weapon.getLevel();
    }

    public int getPassiveLevel(PassiveType passiveType) {
        return upgradeController.getPassiveLevel(this, passiveType);
    }

    public void chooseUpgrade(int index) {
        upgradeController.chooseUpgrade(this, index);
    }

    public Enemy findNearestEnemy(float radius) {
        return combatResolver.findNearestEnemy(this, radius);
    }

    public void fireAimedBurst(int projectileCount, float spreadDegrees, float speed, float radius, float damage,
                               int remainingHits, float maxDistance, WeaponType weaponType, int weaponLevel,
                               float aimRange) {
        fireAimedBurst(projectileCount, spreadDegrees, speed, radius, damage, remainingHits, maxDistance, weaponType,
            weaponLevel, aimRange, 0f, 0f, 0f, 0f, 0f);
    }

    public void fireAimedBurst(int projectileCount, float spreadDegrees, float speed, float radius, float damage,
                               int remainingHits, float maxDistance, WeaponType weaponType, int weaponLevel,
                               float aimRange, float spawnDistance, float splashRadius, float frostPatchRadius,
                               float frostPatchDuration, float frostSlowMultiplier) {
        combatResolver.fireAimedBurst(this, projectileCount, spreadDegrees, speed, radius, damage, remainingHits,
            maxDistance, weaponType, weaponLevel, aimRange, spawnDistance, splashRadius, frostPatchRadius,
            frostPatchDuration, frostSlowMultiplier);
    }

    public void fireRadialBurst(int projectileCount, float speed, float radius, float damage, int remainingHits,
                                float maxDistance, WeaponType weaponType, int weaponLevel) {
        combatResolver.fireRadialBurst(this, projectileCount, speed, radius, damage, remainingHits, maxDistance,
            weaponType, weaponLevel);
    }

    public void ensureOrbitBladeCount(int count, float orbitRadius, float bladeSize, float damage) {
        combatResolver.ensureOrbitBladeCount(this, count, orbitRadius, bladeSize, damage);
    }

    /**
     * Retourne le registre mutable des armes possédées pendant la session.
     */
    EnumMap<WeaponType, Weapon> weaponRegistry() {
        return weapons;
    }

    /**
     * Retourne le registre mutable des niveaux de passifs.
     */
    EnumMap<PassiveType, Integer> passiveLevelRegistry() {
        return passiveLevels;
    }

    /**
     * Expose un buffer réutilisable pour calculer une direction d'attaque.
     */
    Vector2 attackDirectionBufferInternal() {
        return attackDirectionBuffer;
    }

    /**
     * Expose un buffer réutilisable pour calculer une direction propre à une arme.
     */
    Vector2 weaponDirectionBufferInternal() {
        return weaponDirectionBuffer;
    }

    void setStateInternal(SessionState state) {
        this.state = state;
    }

    void addSurvivalTimeInternal(float delta) {
        survivalTime += delta;
    }

    float getSpawnTimerInternal() {
        return spawnTimer;
    }

    void setSpawnTimerInternal(float spawnTimer) {
        this.spawnTimer = spawnTimer;
    }

    void setCurrentXpInternal(float currentXp) {
        this.currentXp = currentXp;
    }

    void setXpToNextLevelInternal(float xpToNextLevel) {
        this.xpToNextLevel = xpToNextLevel;
    }

    void incrementLevelInternal() {
        level++;
    }

    int getPendingLevelUpsInternal() {
        return pendingLevelUps;
    }

    void incrementPendingLevelUpsInternal() {
        pendingLevelUps++;
    }

    void decrementPendingLevelUpsInternal() {
        pendingLevelUps--;
    }

    boolean isFinalWaveTriggeredInternal() {
        return finalWaveTriggered;
    }

    void setFinalWaveTriggeredInternal(boolean finalWaveTriggered) {
        this.finalWaveTriggered = finalWaveTriggered;
    }

    void clearLevelChoicesInternal() {
        levelChoices.clear();
    }

    void gainExperienceInternal(int amount) {
        upgradeController.gainExperience(this, amount);
    }
}
