package io.github.some_example_name.game.weapon;

import io.github.some_example_name.game.WeaponType;
import io.github.some_example_name.game.session.GameSession;
import io.github.some_example_name.game.upgrade.UpgradeChoice;

// Contrat minimal commun à toutes les armes du jeu.
public interface Weapon {
    WeaponType getType();

    String getDisplayName();

    int getLevel();

    boolean isUnlocked();

    boolean canOfferUpgrade();

    void applyUpgrade(GameSession session);

    void update(GameSession session, float delta);

    UpgradeChoice buildChoice(GameSession session);
}
