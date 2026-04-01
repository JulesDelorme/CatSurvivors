package io.github.some_example_name.game.session;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.game.PassiveType;
import io.github.some_example_name.game.Player;
import io.github.some_example_name.game.upgrade.UpgradeCategory;
import io.github.some_example_name.game.upgrade.UpgradeChoice;
import io.github.some_example_name.game.weapon.Weapon;

import java.util.EnumMap;

// Gère la progression du run : XP, niveaux, passifs et cartes d'amélioration.
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

    void chooseUpgrade(GameSession session, int index) {
        if (session.getState() != SessionState.LEVEL_UP || index < 0 || index >= session.getLevelChoices().size) {
            return;
        }

        // Une carte d'upgrade applique soit une arme, soit un passif, puis relance la partie.
        UpgradeChoice choice = session.getLevelChoices().get(index);
        if (choice.category == UpgradeCategory.WEAPON) {
            session.weaponRegistry().get(choice.weaponType).applyUpgrade(session);
        } else {
            applyPassiveUpgrade(session, choice.passiveType);
        }

        session.decrementPendingLevelUpsInternal();
        if (session.getPendingLevelUpsInternal() > 0) {
            openLevelChoices(session);
        } else {
            session.clearLevelChoicesInternal();
            session.setStateInternal(SessionState.RUNNING);
        }
    }

    void gainExperience(GameSession session, int amount) {
        session.setCurrentXpInternal(session.getCurrentXp() + amount);
        while (session.getCurrentXp() >= session.getXpToNextLevel()) {
            session.setCurrentXpInternal(session.getCurrentXp() - session.getXpToNextLevel());
            session.incrementLevelInternal();
            session.incrementPendingLevelUpsInternal();
            session.setXpToNextLevelInternal(getXpThreshold(session.getLevel()));
        }

        // Les niveaux en attente ouvrent un choix d'upgrade dès que la boucle peut être interrompue.
        if (session.getPendingLevelUpsInternal() > 0) {
            openLevelChoices(session);
        }
    }

    private void refreshDerivedStats(GameSession session) {
        // Les passifs modifient des stats dérivées ; on recentralise ces calculs ici.
        Player player = session.getPlayer();
        float previousMaxHealth = player.maxHealth;
        player.maxHealth = Player.BASE_MAX_HEALTH + getPassiveLevel(session, PassiveType.VITALITY) * 20f;
        player.speed = BASE_PLAYER_SPEED + getPassiveLevel(session, PassiveType.SPEED) * 28f;
        if (previousMaxHealth == 0f) {
            player.health = player.maxHealth;
        } else {
            player.health = Math.min(player.health, player.maxHealth);
        }
    }

    private void applyPassiveUpgrade(GameSession session, PassiveType passiveType) {
        session.passiveLevelRegistry().put(passiveType, Math.min(5, getPassiveLevel(session, passiveType) + 1));
        float previousMaxHealth = session.getPlayer().maxHealth;
        refreshDerivedStats(session);
        if (passiveType == PassiveType.VITALITY) {
            session.getPlayer().health = Math.min(session.getPlayer().maxHealth,
                Math.max(session.getPlayer().health, previousMaxHealth) + 20f);
        }
    }

    private float getXpThreshold(int currentLevel) {
        return 8f + (currentLevel - 1) * 5f;
    }

    private void openLevelChoices(GameSession session) {
        session.clearLevelChoicesInternal();
        Array<UpgradeChoice> available = new Array<UpgradeChoice>();

        // Les cartes sont tirées depuis tout ce qui reste améliorable : armes et passifs.
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
