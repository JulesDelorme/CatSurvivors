package io.github.some_example_name.game.weapon;

import io.github.some_example_name.game.WeaponType;
import io.github.some_example_name.game.session.GameSession;
import io.github.some_example_name.game.upgrade.UpgradeChoice;

public class ProjectileWeapon extends AbstractWeapon {
    public ProjectileWeapon(WeaponType type, String displayName, int startingLevel) {
        super(type, displayName, startingLevel);
    }

    @Override
    public void update(GameSession session, float delta) {
        if (!isUnlocked()) {
            return;
        }
        cooldownTimer -= delta;
        while (cooldownTimer <= 0f) {
            cooldownTimer += getCooldown(session);
            fire(session);
        }
    }

    @Override
    public UpgradeChoice buildChoice(GameSession session) {
        int nextLevel = Math.min(5, getLevel() + 1);
        String title = isUnlocked() ? getDisplayName() + " niv. " + nextLevel : "Débloquer " + getDisplayName();
        return new UpgradeChoice(getType(), title, getDescription(nextLevel), nextLevel);
    }

    @Override
    protected float getCooldown(GameSession session) {
        float base;
        switch (getType()) {
            case STONE_SPRAY:
                base = getMageCooldown();
                break;
            case BONE_DART:
                base = getSwordCooldown();
                break;
            case FROST_BOMB:
                base = getFrostBombCooldown();
                break;
            case HAIRBALL:
            default:
                base = getHairballCooldown();
                break;
        }
        return base * session.getAttackSpeedFactor();
    }

    private void fire(GameSession session) {
        switch (getType()) {
            case STONE_SPRAY:
                session.fireAimedBurst(
                    getMageCount(),
                    getMageSpread(),
                    getMageSpeed(),
                    getMageProjectileRadius(),
                    getMageDamage(session),
                    1,
                    getMageRange(),
                    getType(),
                    getLevel(),
                    560f,
                    10f,
                    getMageSplashRadius(),
                    0f,
                    0f,
                    0f
                );
                break;
            case BONE_DART:
                session.fireAimedBurst(
                    getSwordCount(),
                    getSwordSpread(),
                    getSwordSpeed(),
                    getSwordSlashRadius(),
                    getSwordDamage(session),
                    getSwordPierce(),
                    getSwordRange(),
                    getType(),
                    getLevel(),
                    360f,
                    18f,
                    0f,
                    0f,
                    0f,
                    0f
                );
                break;
            case FROST_BOMB:
                session.fireAimedBurst(
                    getFrostBombCount(),
                    getFrostBombSpread(),
                    getFrostBombSpeed(),
                    getFrostBombProjectileRadius(),
                    getFrostBombDamage(session),
                    1,
                    getFrostBombRange(),
                    getType(),
                    getLevel(),
                    540f,
                    12f,
                    getFrostBombSplashRadius(),
                    getFrostPatchRadius(),
                    getFrostPatchDuration(),
                    getFrostSlowMultiplier()
                );
                break;
            case HAIRBALL:
            default:
                session.fireAimedBurst(
                    getHairballCount(),
                    getHairballSpread(),
                    520f,
                    6f,
                    getHairballDamage(session),
                    1,
                    getHairballRange(),
                    getType(),
                    getLevel(),
                    420f
                );
                break;
        }
    }

    private float getHairballCooldown() {
        switch (getLevel()) {
            case 1:
                return 0.48f;
            case 2:
                return 0.42f;
            case 3:
                return 0.36f;
            case 4:
                return 0.31f;
            default:
                return 0.27f;
        }
    }

    private float getHairballDamage(GameSession session) {
        float damage;
        switch (getLevel()) {
            case 1:
                damage = 14f;
                break;
            case 2:
                damage = 19f;
                break;
            case 3:
                damage = 22f;
                break;
            case 4:
                damage = 28f;
                break;
            default:
                damage = 34f;
                break;
        }
        return damage * session.getDamageMultiplier();
    }

    private int getHairballCount() {
        if (getLevel() >= 5) {
            return 3;
        }
        if (getLevel() >= 3) {
            return 2;
        }
        return 1;
    }

    private float getHairballSpread() {
        if (getLevel() >= 5) {
            return 18f;
        }
        if (getLevel() >= 3) {
            return 10f;
        }
        return 0f;
    }

    private float getHairballRange() {
        return getLevel() >= 4 ? 470f : 400f;
    }

    private float getMageCooldown() {
        switch (getLevel()) {
            case 1:
                return 1.38f;
            case 2:
                return 1.18f;
            case 3:
                return 1.02f;
            case 4:
                return 0.88f;
            default:
                return 0.74f;
        }
    }

    private float getMageDamage(GameSession session) {
        float damage;
        switch (getLevel()) {
            case 1:
                damage = 18f;
                break;
            case 2:
                damage = 24f;
                break;
            case 3:
                damage = 30f;
                break;
            case 4:
                damage = 37f;
                break;
            default:
                damage = 44f;
                break;
        }
        return damage * session.getDamageMultiplier();
    }

    private int getMageCount() {
        if (getLevel() >= 5) {
            return 3;
        }
        if (getLevel() >= 3) {
            return 2;
        }
        return 1;
    }

    private float getMageSpread() {
        if (getLevel() >= 5) {
            return 16f;
        }
        if (getLevel() >= 3) {
            return 10f;
        }
        return 0f;
    }

    private float getMageSpeed() {
        return getLevel() >= 4 ? 520f : 460f;
    }

    private float getMageProjectileRadius() {
        switch (getLevel()) {
            case 1:
                return 7f;
            case 2:
                return 8f;
            case 3:
                return 9f;
            case 4:
                return 10f;
            default:
                return 11f;
        }
    }

    private float getMageRange() {
        return getLevel() >= 4 ? 430f : 380f;
    }

    private float getMageSplashRadius() {
        switch (getLevel()) {
            case 1:
                return 42f;
            case 2:
                return 52f;
            case 3:
                return 60f;
            case 4:
                return 68f;
            default:
                return 78f;
        }
    }

    private float getSwordCooldown() {
        switch (getLevel()) {
            case 1:
                return 0.92f;
            case 2:
                return 0.80f;
            case 3:
                return 0.70f;
            case 4:
                return 0.61f;
            default:
                return 0.54f;
        }
    }

    private float getSwordDamage(GameSession session) {
        float damage;
        switch (getLevel()) {
            case 1:
                damage = 24f;
                break;
            case 2:
                damage = 30f;
                break;
            case 3:
                damage = 36f;
                break;
            case 4:
                damage = 42f;
                break;
            default:
                damage = 50f;
                break;
        }
        return damage * session.getDamageMultiplier();
    }

    private int getSwordCount() {
        if (getLevel() >= 4) {
            return 3;
        }
        if (getLevel() >= 2) {
            return 2;
        }
        return 1;
    }

    private float getSwordSpread() {
        switch (getLevel()) {
            case 1:
                return 0f;
            case 2:
                return 14f;
            case 3:
                return 20f;
            default:
                return 28f;
        }
    }

    private float getSwordSpeed() {
        return getLevel() >= 4 ? 780f : 700f;
    }

    private float getSwordRange() {
        switch (getLevel()) {
            case 1:
                return 96f;
            case 2:
                return 108f;
            case 3:
                return 118f;
            case 4:
                return 132f;
            default:
                return 146f;
        }
    }

    private float getSwordSlashRadius() {
        switch (getLevel()) {
            case 1:
                return 12f;
            case 2:
                return 13f;
            case 3:
                return 15f;
            case 4:
                return 16f;
            default:
                return 18f;
        }
    }

    private int getSwordPierce() {
        switch (getLevel()) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 2;
            case 4:
                return 3;
            default:
                return 4;
        }
    }

    private float getFrostBombCooldown() {
        switch (getLevel()) {
            case 1:
                return 2.65f;
            case 2:
                return 2.32f;
            case 3:
                return 2.02f;
            case 4:
                return 1.76f;
            default:
                return 1.52f;
        }
    }

    private float getFrostBombDamage(GameSession session) {
        float damage;
        switch (getLevel()) {
            case 1:
                damage = 18f;
                break;
            case 2:
                damage = 24f;
                break;
            case 3:
                damage = 30f;
                break;
            case 4:
                damage = 36f;
                break;
            default:
                damage = 42f;
                break;
        }
        return damage * session.getDamageMultiplier();
    }

    private int getFrostBombCount() {
        if (getLevel() >= 5) {
            return 3;
        }
        if (getLevel() >= 3) {
            return 2;
        }
        return 1;
    }

    private float getFrostBombSpread() {
        if (getLevel() >= 5) {
            return 18f;
        }
        if (getLevel() >= 3) {
            return 12f;
        }
        return 0f;
    }

    private float getFrostBombSpeed() {
        return getLevel() >= 4 ? 410f : 360f;
    }

    private float getFrostBombProjectileRadius() {
        switch (getLevel()) {
            case 1:
                return 8f;
            case 2:
                return 9f;
            case 3:
                return 10f;
            case 4:
                return 11f;
            default:
                return 12f;
        }
    }

    private float getFrostBombRange() {
        return getLevel() >= 4 ? 470f : 420f;
    }

    private float getFrostBombSplashRadius() {
        switch (getLevel()) {
            case 1:
                return 50f;
            case 2:
                return 58f;
            case 3:
                return 66f;
            case 4:
                return 74f;
            default:
                return 84f;
        }
    }

    private float getFrostPatchRadius() {
        switch (getLevel()) {
            case 1:
                return 56f;
            case 2:
                return 64f;
            case 3:
                return 72f;
            case 4:
                return 82f;
            default:
                return 92f;
        }
    }

    private float getFrostPatchDuration() {
        switch (getLevel()) {
            case 1:
                return 2.4f;
            case 2:
                return 3.0f;
            case 3:
                return 3.6f;
            case 4:
                return 4.1f;
            default:
                return 4.8f;
        }
    }

    private float getFrostSlowMultiplier() {
        switch (getLevel()) {
            case 1:
                return 0.78f;
            case 2:
                return 0.70f;
            case 3:
                return 0.62f;
            case 4:
                return 0.56f;
            default:
                return 0.48f;
        }
    }

    private String getDescription(int nextLevel) {
        switch (getType()) {
            case STONE_SPRAY:
                switch (nextLevel) {
                    case 1:
                        return "Débloque un livre de mage qui tire des boules d'énergie explosives.";
                    case 2:
                        return "Explosion plus large et projectile plus dense.";
                    case 3:
                        return "Ajoute une seconde boule d'énergie.";
                    case 4:
                        return "Cadence et vitesse de tir en hausse.";
                    default:
                        return "Salve triple avec grosses explosions.";
                }
            case BONE_DART:
                switch (nextLevel) {
                    case 1:
                        return "Débloque une épée qui projette des slashs tranchants.";
                    case 2:
                        return "Ajoute un second slash à chaque attaque.";
                    case 3:
                        return "Slashs plus larges et plus puissants.";
                    case 4:
                        return "Ajoute un troisième slash et plus de portée.";
                    default:
                        return "Pression maximale avec cadence et perforation accrues.";
                }
            case FROST_BOMB:
                switch (nextLevel) {
                    case 1:
                        return "Lance une bombe glaciale qui explose et laisse une zone gelée.";
                    case 2:
                        return "La glace dure plus longtemps et ralentit davantage.";
                    case 3:
                        return "Ajoute une seconde bombe dans la salve.";
                    case 4:
                        return "Explosion et trace gelée plus larges.";
                    default:
                        return "Triple salve de givre avec slow très violent.";
                }
            case HAIRBALL:
            default:
                switch (nextLevel) {
                    case 2:
                        return "Recharge plus rapide.";
                    case 3:
                        return "Tire une boule de poils supplémentaire.";
                    case 4:
                        return "Portée et dégâts en hausse.";
                    case 5:
                        return "Volée triple, cadence supérieure.";
                    default:
                        return "Arme de départ auto-ciblée.";
                }
        }
    }
}
