package io.github.the_actual_game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.the_actual_game.constants.GameConstants;

public class EnemyManager {
    private Array<Rectangle> enemies;

    public EnemyManager() {
        enemies = new Array<Rectangle>();
        spawnInitialEnemies();
    }

    private void spawnInitialEnemies() {
        // Spawn enemies for each pane
        for (int pane = 0; pane < GameConstants.NUMBER_OF_PANES; pane++) {
            float paneOffset = pane * GameConstants.PANE_WIDTH;
            
            // Spawn enemies within this pane
            for (int i = 0; i < GameConstants.ENEMY_COUNT_PER_PANE; i++) {
                Rectangle enemy = new Rectangle();
                enemy.width = GameConstants.ENEMY_WIDTH;
                enemy.height = GameConstants.ENEMY_HEIGHT;
                enemy.x = paneOffset + GameConstants.ENEMY_INITIAL_X + i * GameConstants.ENEMY_SPACING;
                enemy.y = GameConstants.ENEMY_INITIAL_Y;
                enemies.add(enemy);
            }
        }
    }

    public void update(float delta, Rectangle player) {
        for (Rectangle enemy : enemies) {
            // Move enemy downward
            enemy.y -= GameConstants.ENEMY_SPEED * delta;
            
            // If enemy reaches bottom, move it back to top
            if (enemy.y + enemy.height < 0) {
                enemy.y = GameConstants.SCREEN_HEIGHT;
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
        // Draw pane separator
        shapeRenderer.setColor(Color.GRAY);
        float separatorX = GameConstants.PANE_WIDTH - GameConstants.PANE_SEPARATOR_WIDTH / 2;
        shapeRenderer.rect(separatorX, 0, GameConstants.PANE_SEPARATOR_WIDTH, GameConstants.SCREEN_HEIGHT);

        // Draw enemies
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