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
        // Start with a purple color to distinguish it as a boss
        this.color = new Color(0.5f, 0, 0.5f, 1);
    }

    @Override
    public void hit(int damage) {
        super.hit(damage);
        // Keep the purple tint while transitioning to red
        if (life > 0) {
            float lifePercentage = life / (float)initialLife;
            color.set(0.5f + (0.5f * (1 - lifePercentage)), 0, 0.5f + (0.5f * (1 - lifePercentage)), 1);
        }
    }
} 