package io.github.the_actual_game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class EnemyManager {
    private Array<Rectangle> enemies;
    private static final float ENEMY_SPEED = 150;
    private static final float ENEMY_WIDTH = 30;
    private static final float ENEMY_HEIGHT = 30;
    private final int SCREEN_WIDTH = 1500;
    private final int SCREEN_HEIGHT = 900;

    public EnemyManager() {
        enemies = new Array<Rectangle>();
        spawnInitialEnemies();
    }

    private void spawnInitialEnemies() {
        for (int i = 0; i < 5; i++) {
            Rectangle enemy = new Rectangle();
            enemy.width = ENEMY_WIDTH;
            enemy.height = ENEMY_HEIGHT;
            enemy.x = 100 + i * 300;
            enemy.y = 700;
            enemies.add(enemy);
        }
    }

    public void update(float delta, Rectangle player) {
        for (Rectangle enemy : enemies) {
            // Move enemy downward
            enemy.y -= ENEMY_SPEED * delta;
            
            // If enemy reaches bottom, move it back to top
            if (enemy.y + enemy.height < 0) {
                enemy.y = SCREEN_HEIGHT;
            }
        }
    }

    public boolean checkCollisions(Rectangle player) {
        for (Rectangle enemy : enemies) {
            if (enemy.overlaps(player)) {
                return true;
            }
        }
        return false;
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);
        for (Rectangle enemy : enemies) {
            shapeRenderer.rect(enemy.x, enemy.y, enemy.width, enemy.height);
        }
    }

    public void reset() {
        enemies.clear();
        spawnInitialEnemies();
    }
} 