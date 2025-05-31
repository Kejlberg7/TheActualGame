package io.github.the_actual_game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.the_actual_game.constants.GameConstants;
import io.github.the_actual_game.entities.Enemy;
import com.badlogic.gdx.math.MathUtils;

public class EnemyManager {
    private Array<Enemy> enemies;

    public EnemyManager() {
        enemies = new Array<Enemy>();
        spawnInitialEnemies();
    }

    private void spawnInitialEnemies() {
        for (int i = 0; i < GameConstants.ENEMY_COUNT; i++) {
            float x = GameConstants.ENEMY_INITIAL_X + i * GameConstants.ENEMY_SPACING;
            float y = GameConstants.ENEMY_INITIAL_Y;
            enemies.add(new Enemy(x, y, GameConstants.ENEMY_WIDTH, GameConstants.ENEMY_HEIGHT, GameConstants.ENEMY_DEFAULT_LIFE));
        }
    }

    private void spawnNewEnemy() {
        // Generate a random x position within screen bounds
        float minX = GameConstants.ENEMY_WIDTH;
        float maxX = GameConstants.SCREEN_WIDTH - GameConstants.ENEMY_WIDTH;
        float x = MathUtils.random(minX, maxX);
        float y = GameConstants.SCREEN_HEIGHT; // Start at the top of the screen
        
        enemies.add(new Enemy(x, y, GameConstants.ENEMY_WIDTH, GameConstants.ENEMY_HEIGHT, GameConstants.ENEMY_DEFAULT_LIFE));
    }

    public void update(float delta, Rectangle player) {
        // Remove enemies that are no longer needed
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            if (!enemy.isAlive() || enemy.rect.y + enemy.rect.height < 0) {
                enemies.removeIndex(i);
                // Spawn a new enemy to replace the one that was removed
                spawnNewEnemy();
            }
        }

        // Update remaining enemies
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;
            // Move enemy downward
            enemy.rect.y -= GameConstants.ENEMY_SPEED * delta;
        }
    }

    public boolean checkCollisions(Rectangle player) {
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;
            if (enemy.rect.overlaps(player)) {
                return true;
            }
        }
        return false;
    }

    public void render(ShapeRenderer shapeRenderer) {
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;
            shapeRenderer.setColor(enemy.getColor());
            shapeRenderer.rect(enemy.rect.x, enemy.rect.y, enemy.rect.width, enemy.rect.height);
        }
    }

    public void reset() {
        enemies.clear();
        spawnInitialEnemies();
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }
}