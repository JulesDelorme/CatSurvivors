package io.github.some_example_name.game;

import com.badlogic.gdx.graphics.Color;

public class EnemyArchetype {
    public final String displayName;
    public final String spriteKey;
    public final float radius;
    public final float baseSpeed;
    public final float maxHealth;
    public final int xpValue;
    public final float contactDamagePerSecond;
    public final float burstMultiplier;
    public final float burstEvery;
    public final float burstDuration;
    public final Color primaryColor;
    public final Color secondaryColor;
    public final Color accentColor;
    public final boolean robotic;
    public final boolean elite;

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
}
