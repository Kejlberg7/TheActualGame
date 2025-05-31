package io.github.the_actual_game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.the_actual_game.constants.GameConstants;
import io.github.the_actual_game.entities.Enemy;

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

    public void update(float delta, Rectangle player) {
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;
            // Move enemy downward
            enemy.rect.y -= GameConstants.ENEMY_SPEED * delta;
            
            // If enemy reaches bottom, move it back to top
            if (enemy.rect.y + enemy.rect.height < 0) {
                enemy.rect.y = GameConstants.SCREEN_HEIGHT;
            }
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
        shapeRenderer.setColor(Color.RED);
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;
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