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
    private boolean spacePressedLastFrame = false;
    private int lives;
    private float invulnerabilityTimer;
    private static final float INVULNERABILITY_DURATION = 2.0f; // 2 seconds of invulnerability after being hit
    private int numShots = 1;
    private LevelConfig currentLevelConfig;

    public PlayerManager() {
        player = new Rectangle();
        player.width = GameConstants.PLAYER_WIDTH;
        player.height = GameConstants.PLAYER_HEIGHT;
        player.x = GameConstants.SCREEN_WIDTH/2 - player.width/2;
        player.y = GameConstants.PLAYER_INITIAL_Y;
        bullets = new Array<Rectangle>();
        lives = GameConstants.PLAYER_DEFAULT_LIFE;
        invulnerabilityTimer = 0;
        setLevel(0); // Start at level 1 (index 0)
    }

    public void setLevel(int level) {
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
    }

    public boolean handleShooting() {
        boolean spacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        boolean shotFired = false;
        
        if (spacePressed && !spacePressedLastFrame) {
            // Fire multiple bullets based on numShots
            float spacing = 8f; // Space between multiple shots
            float startX = player.x + player.width / 2 - (numShots * spacing) / 2;
            
            for (int i = 0; i < numShots; i++) {
                Rectangle bullet = new Rectangle();
                bullet.width = GameConstants.BULLET_WIDTH;
                bullet.height = GameConstants.BULLET_HEIGHT;
                bullet.x = startX + (i * spacing);
                bullet.y = player.y + player.height;
                bullets.add(bullet);
            }
            shotFired = true;
        }
        spacePressedLastFrame = spacePressed;
        return shotFired;
    }

    public void adjustShots(boolean increase) {
        if (increase) {
            numShots = Math.min(numShots + 1, GameConstants.MAX_SHOTS);
        } else {
            numShots = Math.max(numShots - 1, GameConstants.MIN_SHOTS);
        }
    }

    public void render(ShapeRenderer shapeRenderer) {
        // Draw player with blinking effect when invulnerable
        if (invulnerabilityTimer <= 0 || (int)(invulnerabilityTimer * 10) % 2 == 0) {
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.rect(player.x, player.y, player.width, player.height);
        }

        // Draw bullets
        shapeRenderer.setColor(Color.YELLOW);
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
        numShots = 1;
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
