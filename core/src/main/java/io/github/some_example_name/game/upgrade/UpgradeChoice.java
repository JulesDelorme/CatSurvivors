package io.github.some_example_name.game.upgrade;

import io.github.some_example_name.game.PassiveType;
import io.github.some_example_name.game.WeaponType;

public class UpgradeChoice {
    public final UpgradeCategory category;
    public final WeaponType weaponType;
    public final PassiveType passiveType;
    public final String title;
    public final String description;
    public final int resultingLevel;

    public UpgradeChoice(WeaponType weaponType, String title, String description, int resultingLevel) {
        this.category = UpgradeCategory.WEAPON;
        this.weaponType = weaponType;
        this.passiveType = null;
        this.title = title;
        this.description = description;
        this.resultingLevel = resultingLevel;
    }

    public UpgradeChoice(PassiveType passiveType, String title, String description, int resultingLevel) {
        this.category = UpgradeCategory.PASSIVE;
        this.weaponType = null;
        this.passiveType = passiveType;
        this.title = title;
        this.description = description;
        this.resultingLevel = resultingLevel;
    }
}
