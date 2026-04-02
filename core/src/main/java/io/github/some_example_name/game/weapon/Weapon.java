package io.github.some_example_name.game.weapon;

import io.github.some_example_name.game.WeaponType;
import io.github.some_example_name.game.session.GameSession;
import io.github.some_example_name.game.upgrade.UpgradeChoice;

/**
 * Contrat minimal commun à toutes les armes du jeu.
 */
public interface Weapon {
    /**
     * Retourne le type métier de l'arme.
     */
    WeaponType getType();

    /**
     * Retourne le libellé affiché dans le HUD et les cartes d'upgrade.
     */
    String getDisplayName();

    /**
     * Retourne le niveau actuel de l'arme.
     */
    int getLevel();

    /**
     * Indique si l'arme est déjà débloquée pendant ce run.
     */
    boolean isUnlocked();

    /**
     * Indique si une nouvelle amélioration peut encore être proposée.
     */
    boolean canOfferUpgrade();

    /**
     * Applique une montée de niveau à l'arme.
     */
    void applyUpgrade(GameSession session);

    /**
     * Fait avancer la logique runtime de l'arme sur une frame.
     */
    void update(GameSession session, float delta);

    /**
     * Construit la carte d'amélioration correspondant au prochain palier.
     */
    UpgradeChoice buildChoice(GameSession session);
}
