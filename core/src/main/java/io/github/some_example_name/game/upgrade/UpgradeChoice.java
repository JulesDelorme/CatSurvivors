package io.github.some_example_name.game.upgrade;

import io.github.some_example_name.game.PassiveType;
import io.github.some_example_name.game.WeaponType;

public class UpgradeChoice {
    private final UpgradeCategory category;
    private final WeaponType weaponType;
    private final PassiveType passiveType;
    private final String title;
    private final String description;
    private final int resultingLevel;

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

    public UpgradeCategory getCategory() {
        return category;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public PassiveType getPassiveType() {
        return passiveType;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getResultingLevel() {
        return resultingLevel;
    }
}
