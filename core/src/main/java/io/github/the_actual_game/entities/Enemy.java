package io.github.the_actual_game.entities;

import com.badlogic.gdx.math.Rectangle;

public class Enemy {
    public Rectangle rect;
    int life;

    public Enemy(float x, float y, float width, float height, int life) {
        this.rect = new Rectangle(x, y, width, height);
        this.life = life;
    }

    public boolean isAlive() {
        return life > 0;
    }

    public void hit(int damage) {
        life -= damage;
    }
} 