package io.github.some_example_name.game.weapon;

import io.github.some_example_name.game.WeaponType;
import io.github.some_example_name.game.session.GameSession;

// Base commune : niveau, cooldown et logique de montée en rang.
abstract class AbstractWeapon implements Weapon {
    private final WeaponType type;
    private final String displayName;

    protected int level;
    protected float cooldownTimer;

    protected AbstractWeapon(WeaponType type, String displayName, int startingLevel) {
        this.type = type;
        this.displayName = displayName;
        level = startingLevel;
    }

    @Override
    public WeaponType getType() {
        return type;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public boolean isUnlocked() {
        return level > 0;
    }

    @Override
    public boolean canOfferUpgrade() {
        return level < 5;
    }

    @Override
    public void applyUpgrade(GameSession session) {
        if (!canOfferUpgrade()) {
            return;
        }
        // Chaque niveau est appliqué ici, les sous-classes réagissent ensuite si besoin.
        level++;
        onLevelChanged(session);
    }

    protected void onLevelChanged(GameSession session) {
        cooldownTimer = Math.min(cooldownTimer, getCooldown(session));
    }

    protected abstract float getCooldown(GameSession session);
}
