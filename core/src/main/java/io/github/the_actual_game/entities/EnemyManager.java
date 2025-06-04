package io.github.the_actual_game.entities;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import io.github.the_actual_game.constants.GameConstants;
import io.github.the_actual_game.constants.LevelConfig;

public class EnemyManager {
    private Array<Enemy> enemies;
    private LevelConfig currentLevelConfig;
    private int remainingEnemies;
    private int currentLevel;
    private float spawnTimer;
    private boolean initialized = false;
    private static final float SPAWN_INTERVAL = 4.0f; // Increased from 3.0f to 4.0f seconds

    public EnemyManager() {
        System.out.println("EnemyManager constructor called");
        enemies = new Array<>();
        spawnTimer = SPAWN_INTERVAL;
        if (!initialized) {
            setLevel(0);
            initialized = true;
        }
    }

    public void reset() {
        System.out.println("EnemyManager reset called");
        // Clean up existing enemies
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
        enemies.clear();
        
        // Reset state
        spawnTimer = SPAWN_INTERVAL;
        setLevel(0);
    }

    public void setLevel(int level) {
        System.out.println("EnemyManager setLevel called with level: " + level);
        
        // Clean up any existing enemies first
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
        enemies.clear();
        
        if (level < 0) level = 0;
        if (level >= GameConstants.MAX_LEVELS) level = GameConstants.MAX_LEVELS - 1;
        
        currentLevel = level;
        currentLevelConfig = GameConstants.LEVEL_CONFIGS[level];
        remainingEnemies = currentLevelConfig.getEnemyCount() - 1; // Subtract 1 for initial spawn
        
        // Spawn exactly one enemy
        System.out.println("Spawning initial enemy");
        Enemy enemy = new Enemy(
            GameConstants.SCREEN_WIDTH/2,
            GameConstants.ENEMY_INITIAL_Y,
            GameConstants.ENEMY_WIDTH,
            GameConstants.ENEMY_HEIGHT,
            currentLevelConfig.getEnemyLife()
        );
        enemies.add(enemy);
    }

    private void spawnEnemy() {
        if (remainingEnemies < 0) return;
        
        // Randomly choose which pane to spawn in
        int pane = (int)(Math.random() * GameConstants.NUMBER_OF_PANES);
        float paneOffset = pane * GameConstants.PANE_WIDTH;
        
        // Random position within the pane, accounting for enemy width
        float enemyWidth = GameConstants.ENEMY_WIDTH;
        float minX = paneOffset + enemyWidth/2;
        float maxX = paneOffset + GameConstants.PANE_WIDTH - enemyWidth/2;
        float x = minX + (float)(Math.random() * (maxX - minX));
        
        // Ensure x is within screen bounds
        x = Math.max(enemyWidth/2, Math.min(x, GameConstants.SCREEN_WIDTH - enemyWidth/2));
        
        Enemy enemy = new Enemy(
            x,
            GameConstants.ENEMY_INITIAL_Y,
            GameConstants.ENEMY_WIDTH,
            GameConstants.ENEMY_HEIGHT,
            currentLevelConfig.getEnemyLife()
        );
        enemies.add(enemy);
    }

    public void update(float delta) {
        // Handle enemy spawning
        spawnTimer += delta;
        if (spawnTimer >= SPAWN_INTERVAL && remainingEnemies >= 0) {
            spawnEnemy();
            spawnTimer = 0;
            remainingEnemies--; // Only decrement after spawning
        }

        // Update enemy positions
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;
            
            // Get current position and calculate new position
            Vector3 pos = enemy.getModel().getPosition();
            float newY = pos.y - currentLevelConfig.getEnemySpeed() * delta;
            
            // Keep enemy within screen bounds
            float enemyWidth = enemy.getModel().getDimensions().x;
            float x = Math.max(enemyWidth/2, Math.min(pos.x, GameConstants.SCREEN_WIDTH - enemyWidth/2));
            
            enemy.getModel().setPosition(x, newY, pos.z);
        }

        // Remove enemies that are off screen
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            if (!enemy.isAlive() || enemy.getModel().getPosition().y < 0) {
                enemy.dispose();
                enemies.removeIndex(i);
            }
        }
    }

    public int checkBulletCollisions(Array<Model3D> bullets) {
        int scoreEarned = 0;
        for (int i = bullets.size - 1; i >= 0; i--) {
            Model3D bullet = bullets.get(i);
            for (Enemy enemy : enemies) {
                if (!enemy.isAlive()) continue;
                if (enemy.getModel().collidesWith(bullet)) {
                    enemy.hit(1);
                    if (!enemy.isAlive()) {
                        scoreEarned += 10;
                    }
                    bullets.removeIndex(i);
                    break;
                }
            }
        }
        return scoreEarned;
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        for (Enemy enemy : enemies) {
            enemy.render(modelBatch, environment);
        }
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public boolean isLevelComplete() {
        return remainingEnemies <= 0 && enemies.size == 0;
    }

    public int getCurrentLevel() {
        return currentLevel + 1;
    }

    public void dispose() {
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
    }
}
