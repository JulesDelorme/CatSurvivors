package io.github.some_example_name.game;

import com.badlogic.gdx.math.Vector2;

public class Player {
    public static final float BASE_MAX_HEALTH = 100f;
    private static final float BASE_SPEED = 280f;

    private final Vector2 position = new Vector2();
    private final Vector2 movement = new Vector2();
    private final Vector2 lastAimDirection = new Vector2(1f, 0f);
    private final float radius = 18f;
    private float maxHealth = BASE_MAX_HEALTH;
    private float health = BASE_MAX_HEALTH;
    private float speed = BASE_SPEED;
    private float animationTime;
    private float hitFlashTime;
    private boolean moving;
    private boolean facingLeft;
    private CatAnim anim = CatAnim.IDLE;

    public Player(float startX, float startY) {
        position.set(startX, startY);
        movement.setZero();
        lastAimDirection.set(1f, 0f);
        maxHealth = BASE_MAX_HEALTH;
        health = BASE_MAX_HEALTH;
        speed = BASE_SPEED;
        animationTime = 0f;
        hitFlashTime = 0f;
        moving = false;
        facingLeft = false;
        anim = CatAnim.IDLE;
    }

    public void setMovement(float horizontal, float vertical) {
        movement.set(horizontal, vertical);
        moving = movement.len2() > 0f;

        if (!moving) {
            movement.setZero();
            anim = CatAnim.IDLE;
            return;
        }

        movement.nor();
        lastAimDirection.set(movement);
        anim = CatAnim.RUN;

        if (movement.x < -0.01f) {
            facingLeft = true;
        } else if (movement.x > 0.01f) {
            facingLeft = false;
        }
    }

    public void update(float delta) {
        if (moving) {
            position.mulAdd(movement, speed * delta);
        }
        animationTime += delta;
        hitFlashTime = Math.max(0f, hitFlashTime - delta * 3.5f);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getMovement() {
        return movement;
    }

    public Vector2 getLastAimDirection() {
        return lastAimDirection;
    }

    public float getRadius() {
        return radius;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = Math.max(0f, maxHealth);
        health = Math.min(health, this.maxHealth);
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = Math.max(0f, Math.min(health, maxHealth));
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = Math.max(0f, speed);
    }

    public float getAnimationTime() {
        return animationTime;
    }

    public float getHitFlashTime() {
        return hitFlashTime;
    }

    public boolean isMoving() {
        return moving;
    }

    public boolean isFacingLeft() {
        return facingLeft;
    }

    public CatAnim getAnim() {
        return anim;
    }

    public boolean applyDamage(float damage) {
        if (damage <= 0f) {
            return false;
        }
        health = Math.max(0f, health - damage);
        hitFlashTime = 1f;
        return health <= 0f;
    }
}
