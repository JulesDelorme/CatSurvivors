package io.github.some_example_name.game.weapon;

import io.github.some_example_name.game.OrbitBlade;
import io.github.some_example_name.game.WeaponType;
import io.github.some_example_name.game.session.GameSession;
import io.github.some_example_name.game.upgrade.UpgradeChoice;

public class OrbitWeapon extends AbstractWeapon {
    public OrbitWeapon(int startingLevel) {
        super(WeaponType.ORBIT_CLAWS, "Griffes orbitales", startingLevel);
    }

    @Override
    public void update(GameSession session, float delta) {
        if (!isUnlocked()) {
            session.getOrbitBlades().clear();
            return;
        }

        int bladeCount = getBladeCount();
        float orbitRadius = getOrbitRadius();
        float bladeSize = getBladeSize();
        float bladeDamage = getBladeDamage(session);
        float spinSpeed = getSpinSpeed();

        session.ensureOrbitBladeCount(bladeCount, orbitRadius, bladeSize, bladeDamage);
        for (OrbitBlade blade : session.getOrbitBlades()) {
            blade.orbitRadius = orbitRadius;
            blade.size = bladeSize;
            blade.damage = bladeDamage;
            blade.update(session.getPlayer().position, spinSpeed, delta);
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
        return 0f;
    }

    private int getBladeCount() {
        switch (getLevel()) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 2;
            default:
                return 3;
        }
    }

    private float getOrbitRadius() {
        switch (getLevel()) {
            case 1:
                return 52f;
            case 2:
                return 58f;
            case 3:
                return 64f;
            case 4:
                return 70f;
            default:
                return 76f;
        }
    }

    private float getBladeSize() {
        switch (getLevel()) {
            case 1:
                return 12f;
            case 2:
                return 13f;
            case 3:
                return 14f;
            case 4:
                return 15f;
            default:
                return 16f;
        }
    }

    private float getBladeDamage(GameSession session) {
        float damage;
        switch (getLevel()) {
            case 1:
                damage = 11f;
                break;
            case 2:
                damage = 15f;
                break;
            case 3:
                damage = 18f;
                break;
            case 4:
                damage = 22f;
                break;
            default:
                damage = 26f;
                break;
        }
        return damage * session.getDamageMultiplier();
    }

    private float getSpinSpeed() {
        switch (getLevel()) {
            case 1:
                return 175f;
            case 2:
                return 205f;
            case 3:
                return 235f;
            case 4:
                return 255f;
            default:
                return 280f;
        }
    }

    private String getDescription(int nextLevel) {
        switch (nextLevel) {
            case 1:
                return "Fait tourner une griffe défensive autour du chat.";
            case 2:
                return "Ajoute une seconde lame orbitale.";
            case 3:
                return "Augmente les dégâts et le rayon.";
            case 4:
                return "Rotation plus rapide.";
            default:
                return "Trio de griffes, plus larges et plus mordantes.";
        }
    }
}
