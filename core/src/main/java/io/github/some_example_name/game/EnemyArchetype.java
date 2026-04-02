package io.github.some_example_name.game;

import com.badlogic.gdx.graphics.Color;

public class EnemyArchetype {
    private final String displayName;
    private final String spriteKey;
    private final float radius;
    private final float baseSpeed;
    private final float maxHealth;
    private final int xpValue;
    private final float contactDamagePerSecond;
    private final float burstMultiplier;
    private final float burstEvery;
    private final float burstDuration;
    private final Color primaryColor;
    private final Color secondaryColor;
    private final Color accentColor;
    private final boolean robotic;
    private final boolean elite;

    public EnemyArchetype(String displayName, String spriteKey, float radius, float baseSpeed, float maxHealth, int xpValue,
                          float contactDamagePerSecond, float burstMultiplier, float burstEvery, float burstDuration,
                          Color primaryColor, Color secondaryColor, Color accentColor, boolean robotic, boolean elite) {
        this.displayName = displayName;
        this.spriteKey = spriteKey;
        this.radius = radius;
        this.baseSpeed = baseSpeed;
        this.maxHealth = maxHealth;
        this.xpValue = xpValue;
        this.contactDamagePerSecond = contactDamagePerSecond;
        this.burstMultiplier = burstMultiplier;
        this.burstEvery = burstEvery;
        this.burstDuration = burstDuration;
        this.primaryColor = new Color(primaryColor);
        this.secondaryColor = new Color(secondaryColor);
        this.accentColor = new Color(accentColor);
        this.robotic = robotic;
        this.elite = elite;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSpriteKey() {
        return spriteKey;
    }

    public float getRadius() {
        return radius;
    }

    public float getBaseSpeed() {
        return baseSpeed;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public int getXpValue() {
        return xpValue;
    }

    public float getContactDamagePerSecond() {
        return contactDamagePerSecond;
    }

    public float getBurstMultiplier() {
        return burstMultiplier;
    }

    public float getBurstEvery() {
        return burstEvery;
    }

    public float getBurstDuration() {
        return burstDuration;
    }

    public Color getPrimaryColor() {
        return primaryColor;
    }

    public Color getSecondaryColor() {
        return secondaryColor;
    }

    public Color getAccentColor() {
        return accentColor;
    }

    public boolean isRobotic() {
        return robotic;
    }

    public boolean isElite() {
        return elite;
    }
}
