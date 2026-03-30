package io.github.some_example_name.game;

import com.badlogic.gdx.math.Vector2;

public class Player {
    public static final float BASE_MAX_HEALTH = 100f;

    public final Vector2 position = new Vector2();
    public final Vector2 movement = new Vector2();
    public final Vector2 lastAimDirection = new Vector2(1f, 0f);
    public final float radius = 18f;
    public float maxHealth = BASE_MAX_HEALTH;
    public float health = BASE_MAX_HEALTH;
    public float speed = 280f;
    public float animationTime;
    public float hitFlashTime;
    public boolean moving;
    public boolean facingLeft;
    public CatAnim anim = CatAnim.IDLE;

    public Player(float startX, float startY) {
        reset(startX, startY);
    }

    public void reset(float startX, float startY) {
        position.set(startX, startY);
        movement.setZero();
        lastAimDirection.set(1f, 0f);
        maxHealth = BASE_MAX_HEALTH;
        health = BASE_MAX_HEALTH;
        speed = 280f;
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
}
