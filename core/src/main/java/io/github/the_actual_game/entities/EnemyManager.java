package io.github.the_actual_game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.the_actual_game.constants.GameConstants;
import io.github.the_actual_game.constants.LevelConfig;
import io.github.the_actual_game.entities.Enemy;
import com.badlogic.gdx.math.MathUtils;

public class EnemyManager {
    private Array<Enemy> enemies;
    private LevelConfig currentLevelConfig;
    private int remainingEnemies;
    private int currentLevel;
    private boolean bossSpawned;
    private float spawnTimer;
    private static final float MIN_SPAWN_INTERVAL = 2.5f;
    private static final float MAX_SPAWN_INTERVAL = 6.0f;

    public EnemyManager() {
        enemies = new Array<Enemy>();
        setLevel(0); // Start at level 1 (index 0)
    }

    public void setLevel(int level) {
        // Ensure level is within bounds
        if (level < 0) level = 0;
        if (level >= GameConstants.MAX_LEVELS) level = GameConstants.MAX_LEVELS - 1;
        
        currentLevel = level;
        currentLevelConfig = GameConstants.LEVEL_CONFIGS[level];
        remainingEnemies = currentLevelConfig.getEnemyCount();
        enemies.clear();
        bossSpawned = false;
        spawnTimer = 0;
        // Spawn first enemy immediately
        spawnNewEnemy();
    }

    private void spawnInitialEnemies() {
        // No longer needed as we spawn enemies gradually
    }

    private void spawnNewEnemy() {
        if (remainingEnemies <= 0) return;
        
        // Generate a random x position within screen bounds
        float minX = GameConstants.ENEMY_WIDTH;
        float maxX = GameConstants.SCREEN_WIDTH - GameConstants.ENEMY_WIDTH;
        float x = MathUtils.random(minX, maxX);
        float y = GameConstants.SCREEN_HEIGHT; // Start at the top of the screen
        
        if (!bossSpawned && remainingEnemies == 1) {
            // Spawn boss as the last enemy
            x = GameConstants.SCREEN_WIDTH / 2 - (GameConstants.ENEMY_WIDTH * 2); // Center the boss
            enemies.add(new BossEnemy(x, y, GameConstants.ENEMY_WIDTH, GameConstants.ENEMY_HEIGHT, 
                                    currentLevelConfig.getEnemyLife()));
            bossSpawned = true;
        } else {
            enemies.add(new Enemy(x, y, GameConstants.ENEMY_WIDTH, GameConstants.ENEMY_HEIGHT, 
                                currentLevelConfig.getEnemyLife()));
        }
        remainingEnemies--;
    }

    public void update(float delta, Rectangle player) {
        // Update spawn timer
        spawnTimer += delta;
        
        // Randomly spawn new enemies
        if (remainingEnemies > 0 && spawnTimer >= MathUtils.random(MIN_SPAWN_INTERVAL, MAX_SPAWN_INTERVAL)) {
            spawnNewEnemy();
            spawnTimer = 0;
        }

        // Remove enemies that are no longer needed
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            if (!enemy.isAlive() || enemy.rect.y + enemy.rect.height < 0) {
                enemies.removeIndex(i);
            }
        }

        // Update remaining enemies
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;
            // Move enemy downward using level-specific speed
            enemy.rect.y -= currentLevelConfig.getEnemySpeed() * delta;
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
        setLevel(0);
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public boolean isLevelComplete() {
        return remainingEnemies <= 0 && enemies.size == 0;
    }

    public int getCurrentLevel() {
        return currentLevel + 1; // Convert from 0-based to 1-based for display
    }
}
