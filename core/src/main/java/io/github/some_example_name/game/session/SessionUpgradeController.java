package io.github.some_example_name.game.session;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.game.PassiveType;
import io.github.some_example_name.game.Player;
import io.github.some_example_name.game.upgrade.UpgradeCategory;
import io.github.some_example_name.game.upgrade.UpgradeChoice;
import io.github.some_example_name.game.weapon.Weapon;

import java.util.EnumMap;

/**
 * Gère la progression du run : XP, niveaux, passifs et cartes d'amélioration.
 */
final class SessionUpgradeController {
    private static final float BASE_PLAYER_SPEED = 280f;
    private static final float BASE_PICKUP_MAGNET = 96f;
    private static final float BASE_PICKUP_TOUCH = 24f;

    void initialize(GameSession session) {
        EnumMap<PassiveType, Integer> passiveLevels = session.passiveLevelRegistry();
        passiveLevels.clear();
        for (PassiveType type : PassiveType.values()) {
            passiveLevels.put(type, 0);
        }
        session.setXpToNextLevelInternal(getXpThreshold(session.getLevel()));
        refreshDerivedStats(session);
    }

    float getDamageMultiplier(GameSession session) {
        return 1f + getPassiveLevel(session, PassiveType.DAMAGE) * 0.18f;
    }

    float getAttackSpeedFactor(GameSession session) {
        return Math.max(0.55f, 1f - getPassiveLevel(session, PassiveType.ATTACK_SPEED) * 0.08f);
    }

    float getPickupMagnetRadius(GameSession session) {
        return BASE_PICKUP_MAGNET + getPassiveLevel(session, PassiveType.MAGNET) * 30f;
    }

    float getPickupTouchRadius(GameSession session) {
        return BASE_PICKUP_TOUCH + getPassiveLevel(session, PassiveType.MAGNET) * 3f;
    }

    int getPassiveLevel(GameSession session, PassiveType passiveType) {
        Integer value = session.passiveLevelRegistry().get(passiveType);
        return value == null ? 0 : value;
    }

    /**
     * Applique le choix d'upgrade sélectionné et rouvre des cartes tant que des niveaux restent en attente.
     */
    void chooseUpgrade(GameSession session, int index) {
        if (session.getState() != SessionState.LEVEL_UP || index < 0 || index >= session.getLevelChoices().size) {
            return;
        }

        UpgradeChoice choice = session.getLevelChoices().get(index);
        if (choice.getCategory() == UpgradeCategory.WEAPON) {
            session.weaponRegistry().get(choice.getWeaponType()).applyUpgrade(session);
        } else {
            applyPassiveUpgrade(session, choice.getPassiveType());
        }

        session.decrementPendingLevelUpsInternal();
        if (session.getPendingLevelUpsInternal() > 0) {
            openLevelChoices(session);
        } else {
            session.clearLevelChoicesInternal();
            session.setStateInternal(SessionState.RUNNING);
        }
    }

    /**
     * Ajoute de l'expérience puis ouvre un choix d'upgrade dès qu'un niveau est gagné.
     */
    void gainExperience(GameSession session, int amount) {
        session.setCurrentXpInternal(session.getCurrentXp() + amount);
        while (session.getCurrentXp() >= session.getXpToNextLevel()) {
            session.setCurrentXpInternal(session.getCurrentXp() - session.getXpToNextLevel());
            session.incrementLevelInternal();
            session.incrementPendingLevelUpsInternal();
            session.setXpToNextLevelInternal(getXpThreshold(session.getLevel()));
        }

        if (session.getPendingLevelUpsInternal() > 0) {
            openLevelChoices(session);
        }
    }

    /**
     * Recalcule les statistiques dérivées du joueur à partir des passifs actifs.
     */
    private void refreshDerivedStats(GameSession session) {
        Player player = session.getPlayer();
        float previousMaxHealth = player.getMaxHealth();
        player.setMaxHealth(Player.BASE_MAX_HEALTH + getPassiveLevel(session, PassiveType.VITALITY) * 20f);
        player.setSpeed(BASE_PLAYER_SPEED + getPassiveLevel(session, PassiveType.SPEED) * 28f);
        if (previousMaxHealth == 0f) {
            player.setHealth(player.getMaxHealth());
        } else {
            player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
        }
    }

    private void applyPassiveUpgrade(GameSession session, PassiveType passiveType) {
        session.passiveLevelRegistry().put(passiveType, Math.min(5, getPassiveLevel(session, passiveType) + 1));
        float previousMaxHealth = session.getPlayer().getMaxHealth();
        refreshDerivedStats(session);
        if (passiveType == PassiveType.VITALITY) {
            session.getPlayer().setHealth(Math.min(session.getPlayer().getMaxHealth(),
                Math.max(session.getPlayer().getHealth(), previousMaxHealth) + 20f));
        }
    }

    private float getXpThreshold(int currentLevel) {
        return 8f + (currentLevel - 1) * 5f;
    }

    /**
     * Remplit l'overlay de niveau à partir de toutes les armes et passifs encore améliorable.
     */
    private void openLevelChoices(GameSession session) {
        session.clearLevelChoicesInternal();
        Array<UpgradeChoice> available = new Array<UpgradeChoice>();

        for (Weapon weapon : session.weaponRegistry().values()) {
            if (weapon.canOfferUpgrade()) {
                available.add(weapon.buildChoice(session));
            }
        }
        for (PassiveType passiveType : PassiveType.values()) {
            if (getPassiveLevel(session, passiveType) < 5) {
                available.add(buildPassiveChoice(session, passiveType));
            }
        }

        while (session.getLevelChoices().size < 3 && available.size > 0) {
            session.getLevelChoices().add(available.removeIndex(MathUtils.random(available.size - 1)));
        }

        session.setStateInternal(SessionState.LEVEL_UP);
    }

    private UpgradeChoice buildPassiveChoice(GameSession session, PassiveType passiveType) {
        int nextLevel = getPassiveLevel(session, passiveType) + 1;
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
