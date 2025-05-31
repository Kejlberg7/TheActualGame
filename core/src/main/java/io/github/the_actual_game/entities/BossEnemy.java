package io.github.the_actual_game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class BossEnemy extends Enemy {
    private static final float BOSS_SIZE_MULTIPLIER = 2.0f;
    private static final int BOSS_HEALTH_MULTIPLIER = 3;

    public BossEnemy(float x, float y, float baseWidth, float baseHeight, int baseLife) {
        super(x, y,
              baseWidth * BOSS_SIZE_MULTIPLIER,
              baseHeight * BOSS_SIZE_MULTIPLIER,
              baseLife * BOSS_HEALTH_MULTIPLIER);
        // Start with green color like regular enemies
        this.color = new Color(0, 1, 0, 1);
    }

    @Override
    public void hit(int damage) {
        super.hit(damage);
        if (life > 0) {
            // Calculate life percentage
            float lifePercentage = life / (float)initialLife;

            // Create color transition: green -> dark red
            if (lifePercentage > 0.5f) {
                // Transition from green to dark red (reduce green, increase red)
                float transition = (1 - lifePercentage) * 2; // 0 to 1 for upper half
                color.set(transition, 1 - transition, 0, 1);
            } else {
                // Transition to darker red (reduce green)
                float transition = lifePercentage * 2; // 0 to 1 for lower half
                color.set(1, transition * 0.5f, 0, 1); // Keep red at max, reduce green to half
            }
        }
    }
}
