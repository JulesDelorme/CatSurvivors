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
                base = getStoneCooldown();
                break;
            case BONE_DART:
                base = getBoneCooldown();
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
                session.fireRadialBurst(
                    getStoneCount(),
                    340f,
                    5f,
                    getStoneDamage(session),
                    1,
                    230f,
                    getType()
                );
                break;
            case BONE_DART:
                session.fireAimedBurst(
                    getBoneCount(),
                    getBoneSpread(),
                    620f,
                    7f,
                    getBoneDamage(session),
                    getBonePierce(),
                    520f,
                    getType(),
                    520f
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

    private float getStoneCooldown() {
        switch (getLevel()) {
            case 1:
                return 2.25f;
            case 2:
                return 1.95f;
            case 3:
                return 1.65f;
            case 4:
                return 1.42f;
            default:
                return 1.18f;
        }
    }

    private float getStoneDamage(GameSession session) {
        float damage;
        switch (getLevel()) {
            case 1:
                damage = 11f;
                break;
            case 2:
                damage = 14f;
                break;
            case 3:
                damage = 17f;
                break;
            case 4:
                damage = 20f;
                break;
            default:
                damage = 24f;
                break;
        }
        return damage * session.getDamageMultiplier();
    }

    private int getStoneCount() {
        switch (getLevel()) {
            case 1:
                return 6;
            case 2:
                return 8;
            case 3:
                return 9;
            case 4:
                return 11;
            default:
                return 12;
        }
    }

    private float getBoneCooldown() {
        switch (getLevel()) {
            case 1:
                return 1.45f;
            case 2:
                return 1.24f;
            case 3:
                return 1.05f;
            case 4:
                return 0.92f;
            default:
                return 0.82f;
        }
    }

    private float getBoneDamage(GameSession session) {
        float damage;
        switch (getLevel()) {
            case 1:
                damage = 23f;
                break;
            case 2:
                damage = 29f;
                break;
            case 3:
                damage = 35f;
                break;
            case 4:
                damage = 40f;
                break;
            default:
                damage = 46f;
                break;
        }
        return damage * session.getDamageMultiplier();
    }

    private int getBoneCount() {
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
                return 3;
        }
    }

    private float getBoneSpread() {
        switch (getLevel()) {
            case 1:
                return 0f;
            case 2:
                return 10f;
            case 3:
                return 14f;
            default:
                return 18f;
        }
    }

    private int getBonePierce() {
        switch (getLevel()) {
            case 1:
                return 2;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 3;
            default:
                return 4;
        }
    }

    private String getDescription(int nextLevel) {
        switch (getType()) {
            case STONE_SPRAY:
                switch (nextLevel) {
                    case 1:
                        return "Déchaîne un cercle de pierres autour du chat.";
                    case 2:
                        return "Plus de projectiles par salve.";
                    case 3:
                        return "Les éclats frappent plus fort.";
                    case 4:
                        return "Le délai entre deux salves baisse.";
                    default:
                        return "Salves plus denses et plus brutales.";
                }
            case BONE_DART:
                switch (nextLevel) {
                    case 1:
                        return "Débloque des fléchettes osseuses perforantes.";
                    case 2:
                        return "Ajoute une fléchette à la volée.";
                    case 3:
                        return "Augmente les dégâts et la précision.";
                    case 4:
                        return "Les volées partent plus vite.";
                    default:
                        return "Encore plus de perforation et de pression.";
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
