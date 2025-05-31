package io.github.the_actual_game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {
    public Rectangle rect;
    private int life;
    private int initialLife;
    private Color color;

    public Enemy(float x, float y, float width, float height, int life) {
        this.rect = new Rectangle(x, y, width, height);
        this.life = life;
        this.initialLife = life;
        this.color = new Color(0, 1, 0, 1); // Start with full green
    }

    public boolean isAlive() {
        return life > 0;
    }

    public void hit(int damage) {
        life -= damage;
        if (life > 0) {
            // Calculate life percentage
            float lifePercentage = life / (float)initialLife;
            
            // Create color transition: green -> yellow -> red
            if (lifePercentage > 0.5f) {
                // Transition from green to yellow (reduce green, increase red)
                float transition = (1 - lifePercentage) * 2; // 0 to 1 for upper half
                color.set(transition, 1, 0, 1);
            } else {
                // Transition from yellow to red (reduce green)
                float transition = lifePercentage * 2; // 0 to 1 for lower half
                color.set(1, transition, 0, 1);
            }
        }
    }

    public Color getColor() {
        return color;
    }
} 