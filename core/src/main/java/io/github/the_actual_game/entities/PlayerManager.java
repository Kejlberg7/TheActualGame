package io.github.the_actual_game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import io.github.the_actual_game.constants.GameConstants;
import io.github.the_actual_game.constants.LevelConfig;
import io.github.the_actual_game.utils.ModelManager;

public class PlayerManager {
    private Model3D player;
    private Array<Model3D> bullets;
    private int lives;
    private float invulnerabilityTimer;
    private static final float INVULNERABILITY_DURATION = 2.0f; // 2 seconds of invulnerability after being hit
    private LevelConfig currentLevelConfig;
    private float shootingTimer = 0;
    private float currentShootingInterval;
    private int currentShotCount;

    public PlayerManager() {
        // Create player model
        player = new Model3D(
            ModelManager.getInstance().getModel("models/player.g3db"),
            GameConstants.SCREEN_WIDTH/2,
            GameConstants.PLAYER_INITIAL_Y,
            0,
            1.0f
        );
        
        bullets = new Array<>();
        lives = GameConstants.PLAYER_DEFAULT_LIFE;
        invulnerabilityTimer = 0;
        currentShootingInterval = GameConstants.DEFAULT_SHOOTING_INTERVAL;
        currentShotCount = GameConstants.DEFAULT_SHOT_COUNT;
        setLevel(0); // Start at level 1 (index 0)
    }

    public void setLevel(int level) {
        // Ensure level is within bounds
        if (level < 0) level = 0;
        if (level >= GameConstants.MAX_LEVELS) level = GameConstants.MAX_LEVELS - 1;
        
        currentLevelConfig = GameConstants.LEVEL_CONFIGS[level];
    }

    public void update(float delta) {
        // Update invulnerability timer
        if (invulnerabilityTimer > 0) {
            invulnerabilityTimer -= delta;
        }

        // Handle player movement
        Vector3 movement = new Vector3();
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            movement.x = -GameConstants.PLAYER_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            movement.x = GameConstants.PLAYER_SPEED * delta;
        }
        
        // Apply movement with screen bounds check
        Vector3 newPos = player.getPosition().cpy().add(movement);
        float playerWidth = player.getDimensions().x;
        float margin = 5f; // Add a small margin to keep player fully visible
        
        // Keep player within screen bounds with margin
        if (newPos.x < margin) {
            newPos.x = margin;
        } else if (newPos.x > GameConstants.SCREEN_WIDTH - playerWidth - margin) {
            newPos.x = GameConstants.SCREEN_WIDTH - playerWidth - margin;
        }
        
        player.setPosition(newPos.x, newPos.y, newPos.z);

        // Update bullets
        for (int i = bullets.size - 1; i >= 0; i--) {
            Model3D bullet = bullets.get(i);
            bullet.translate(0, currentLevelConfig.getBulletSpeed() * delta, 0);
            if (bullet.getPosition().y > GameConstants.SCREEN_HEIGHT) {
                bullets.removeIndex(i);
            }
        }

        // Handle auto-shooting
        shootingTimer += delta;
        if (shootingTimer >= currentShootingInterval) {
            shoot();
            shootingTimer = 0;
        }
    }

    private void shoot() {
        float totalWidth = (currentShotCount - 1) * GameConstants.MULTI_SHOT_SPREAD;
        float startX = player.getPosition().x + player.getDimensions().x / 2 - totalWidth / 2;

        for (int i = 0; i < currentShotCount; i++) {
            Model3D bullet = new Model3D(
                ModelManager.getInstance().getModel("models/bullet.g3db"),
                startX + (i * GameConstants.MULTI_SHOT_SPREAD),
                player.getPosition().y + player.getDimensions().y,
                0,
                0.5f
            );
            bullets.add(bullet);
        }
        // Add debug info for shooting
        System.out.println("Shooting " + currentShotCount + " bullets with interval " + currentShootingInterval);
    }

    public void adjustShootingSpeed(int powerLevel) {
        float oldInterval = currentShootingInterval;
        float adjustment = 0.05f * Math.abs(powerLevel);
        if (powerLevel > 0) {
            // Faster shooting = lower interval
            currentShootingInterval = Math.max(GameConstants.MIN_SHOOTING_INTERVAL, 
                                           currentShootingInterval - adjustment);
        } else {
            // Slower shooting = higher interval
            currentShootingInterval = Math.min(GameConstants.MAX_SHOOTING_INTERVAL, 
                                           currentShootingInterval + adjustment);
        }
        System.out.println("Shooting interval changed from " + oldInterval + " to " + currentShootingInterval + 
                         " (powerLevel: " + powerLevel + ", adjustment: " + adjustment + ")");
    }

    public void adjustShotCount(int powerLevel) {
        if (powerLevel > 0) {
            // Add one shot
            currentShotCount = Math.min(currentShotCount + 1, GameConstants.MAX_SHOT_COUNT);
        } else {
            // Remove one shot
            currentShotCount = Math.max(currentShotCount - 1, GameConstants.MIN_SHOT_COUNT);
        }
    }

    public boolean handleShooting() {
        // This method is now only used to tell the game screen if a shot was fired this frame
        return shootingTimer == 0;
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        // Render player (with blinking effect when invulnerable)
        if (invulnerabilityTimer <= 0 || (int)(invulnerabilityTimer * 10) % 2 == 0) {
            modelBatch.render(player.getModelInstance(), environment);
        }

        // Render bullets
        for (Model3D bullet : bullets) {
            modelBatch.render(bullet.getModelInstance(), environment);
        }
    }

    public void checkCollisions(Array<Enemy> enemies, Array<Gate> gates) {
        // Check enemy collisions
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;
            
            if (enemy.getModel().collidesWith(player)) {
                if (!isInvulnerable()) {
                    hit();
                }
            }
            
            // Check if enemy has passed the player
            if (enemy.getModel().getPosition().y + enemy.getModel().getDimensions().y < player.getPosition().y) {
                hit();
            }
        }

        // Check gate collisions
        for (Gate gate : gates) {
            if (!gate.isUsed() && gate.getModel().collidesWith(player)) {
                int powerLevel = gate.getPowerLevel();
                if (gate.getType() == Gate.GateType.SPEED) {
                    adjustShootingSpeed(powerLevel);
                } else {
                    adjustShotCount(powerLevel);
                }
                gate.setUsed();
            }
        }
    }

    public Model3D getPlayer() {
        return player;
    }

    public Array<Model3D> getBullets() {
        return bullets;
    }

    public void reset() {
        player.setPosition(
            GameConstants.SCREEN_WIDTH/2 - player.getDimensions().x/2,
            GameConstants.PLAYER_INITIAL_Y,
            0
        );
        bullets.clear();
        lives = GameConstants.PLAYER_DEFAULT_LIFE;
        invulnerabilityTimer = 0;
        shootingTimer = 0;
        currentShootingInterval = GameConstants.DEFAULT_SHOOTING_INTERVAL;
        currentShotCount = GameConstants.DEFAULT_SHOT_COUNT;
        setLevel(0);
    }

    public boolean isInvulnerable() {
        return invulnerabilityTimer > 0;
    }

    public void hit() {
        if (!isInvulnerable()) {
            lives--;
            invulnerabilityTimer = INVULNERABILITY_DURATION;
        }
    }

    public boolean isAlive() {
        return lives > 0;
    }

    public int getLives() {
        return lives;
    }

    public void dispose() {
        if (player != null) {
            player.dispose();
        }
        for (Model3D bullet : bullets) {
            bullet.dispose();
        }
    }
}
