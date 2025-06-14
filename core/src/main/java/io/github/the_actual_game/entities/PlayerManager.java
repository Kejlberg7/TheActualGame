package io.github.the_actual_game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.the_actual_game.constants.GameConstants;
import io.github.the_actual_game.constants.LevelConfig;

public class PlayerManager {
    private Rectangle player;
    private Array<Rectangle> bullets;
    private int lives;
    private float invulnerabilityTimer;
    private static final float INVULNERABILITY_DURATION = 2.0f; // 2 seconds of invulnerability after being hit
    private LevelConfig currentLevelConfig;
    private float shootingTimer = 0;
    private float currentShootingInterval;
    private int currentShotCount;

    public PlayerManager() {
        player = new Rectangle();
        player.width = GameConstants.PLAYER_WIDTH;
        player.height = GameConstants.PLAYER_HEIGHT;
        player.x = GameConstants.SCREEN_WIDTH/2 - player.width/2;
        player.y = GameConstants.PLAYER_INITIAL_Y;
        bullets = new Array<Rectangle>();
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
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.x -= GameConstants.PLAYER_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.x += GameConstants.PLAYER_SPEED * delta;
        }

        // Keep player within screen bounds
        if (player.x < 0) player.x = 0;
        if (player.x > GameConstants.SCREEN_WIDTH - player.width) {
            player.x = GameConstants.SCREEN_WIDTH - player.width;
        }

        // Update bullets
        for (int i = bullets.size - 1; i >= 0; i--) {
            Rectangle bullet = bullets.get(i);
            bullet.y += currentLevelConfig.getBulletSpeed() * delta;
            if (bullet.y > GameConstants.SCREEN_HEIGHT) {
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
        float startX = player.x + player.width / 2 - totalWidth / 2;

        for (int i = 0; i < currentShotCount; i++) {
            Rectangle bullet = new Rectangle();
            bullet.width = GameConstants.BULLET_WIDTH;
            bullet.height = GameConstants.BULLET_HEIGHT;
            bullet.x = startX + (i * GameConstants.MULTI_SHOT_SPREAD);
            bullet.y = player.y + player.height;
            bullets.add(bullet);
        }
    }

    public void adjustShootingSpeed(int powerLevel) {
        float adjustment = 0.02f * Math.abs(powerLevel); // Reduced from 0.05f to 0.02f
        if (powerLevel > 0) {
            // Faster shooting = lower interval
            currentShootingInterval = Math.max(GameConstants.MIN_SHOOTING_INTERVAL, 
                                           currentShootingInterval - adjustment);
        } else {
            // Slower shooting = higher interval
            currentShootingInterval = Math.min(GameConstants.MAX_SHOOTING_INTERVAL, 
                                           currentShootingInterval + adjustment);
        }
    }

    public void adjustShotCount(int powerLevel) {
        // Make shot count changes more gradual
        if (powerLevel > 0) {
            // Only add a shot if we accumulate enough positive power
            if (powerLevel >= 3) {
                currentShotCount = Math.min(currentShotCount + 1, GameConstants.MAX_SHOT_COUNT);
            }
        } else {
            // Remove shots more aggressively for negative gates
            currentShotCount = Math.max(currentShotCount + (powerLevel / 2), GameConstants.MIN_SHOT_COUNT);
        }
    }

    public boolean handleShooting() {
        // This method is now only used to tell the game screen if a shot was fired this frame
        return shootingTimer == 0;
    }

    public void render(ShapeRenderer shapeRenderer) {
        // Draw player with blinking effect when invulnerable
        if (invulnerabilityTimer <= 0 || (int)(invulnerabilityTimer * 10) % 2 == 0) {
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.rect(player.x, player.y, player.width, player.height);
        }

        // Draw bullets in purple
        shapeRenderer.setColor(new Color(0.8f, 0f, 1f, 1f)); // Bright purple color
        for (Rectangle bullet : bullets) {
            shapeRenderer.rect(bullet.x, bullet.y, bullet.width, bullet.height);
        }

        // Draw life indicators in the top-left corner
        shapeRenderer.setColor(Color.RED);
        for (int i = 0; i < lives; i++) {
            float x = 10 + i * (GameConstants.PLAYER_WIDTH * 0.5f + 5);
            float y = GameConstants.SCREEN_HEIGHT - 30;
            shapeRenderer.rect(x, y, GameConstants.PLAYER_WIDTH * 0.5f, GameConstants.PLAYER_HEIGHT * 0.5f);
        }

        // Draw shot count indicator in the top-right corner
        shapeRenderer.setColor(new Color(0.8f, 0f, 1f, 1f)); // Match bullet color
        for (int i = 0; i < currentShotCount; i++) {
            float x = GameConstants.SCREEN_WIDTH - 30 - i * (GameConstants.BULLET_WIDTH + 5);
            float y = GameConstants.SCREEN_HEIGHT - 30;
            shapeRenderer.rect(x, y, GameConstants.BULLET_WIDTH, GameConstants.BULLET_HEIGHT);
        }
    }

    public Rectangle getPlayer() {
        return player;
    }

    public Array<Rectangle> getBullets() {
        return bullets;
    }

    public void reset() {
        player.x = GameConstants.SCREEN_WIDTH/2 - player.width/2;
        player.y = GameConstants.PLAYER_INITIAL_Y;
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
}
