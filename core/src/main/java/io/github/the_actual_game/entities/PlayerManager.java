package io.github.the_actual_game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.the_actual_game.constants.GameConstants;

public class PlayerManager {
    private Rectangle player;
    private Array<Rectangle> bullets;
    private boolean spacePressedLastFrame = false;

    public PlayerManager() {
        player = new Rectangle();
        player.width = GameConstants.PLAYER_WIDTH;
        player.height = GameConstants.PLAYER_HEIGHT;
        player.x = GameConstants.PLAYER_INITIAL_X;
        player.y = GameConstants.PLAYER_INITIAL_Y;
        bullets = new Array<Rectangle>();
    }

    public void update(float delta) {
        // Handle player movement
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.x -= GameConstants.PLAYER_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.x += GameConstants.PLAYER_SPEED * delta;
        }

        // Keep player within screen bounds
        if (player.x < 0) player.x = 0;
        if (player.x + player.width > GameConstants.SCREEN_WIDTH) {
            player.x = GameConstants.SCREEN_WIDTH - player.width;
        }

        // Update bullets
        for (int i = bullets.size - 1; i >= 0; i--) {
            Rectangle bullet = bullets.get(i);
            bullet.y += GameConstants.BULLET_SPEED * delta;
            if (bullet.y > GameConstants.SCREEN_HEIGHT) {
                bullets.removeIndex(i);
            }
        }
    }

    public boolean handleShooting() {
        boolean spacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        boolean shotFired = false;
        
        if (spacePressed && !spacePressedLastFrame) {
            Rectangle bullet = new Rectangle();
            bullet.width = GameConstants.BULLET_WIDTH;
            bullet.height = GameConstants.BULLET_HEIGHT;
            bullet.x = player.x + player.width / 2 - GameConstants.BULLET_WIDTH / 2;
            bullet.y = player.y + player.height;
            bullets.add(bullet);
            shotFired = true;
        }
        spacePressedLastFrame = spacePressed;
        return shotFired;
    }

    public void render(ShapeRenderer shapeRenderer) {
        // Draw player
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(player.x, player.y, player.width, player.height);

        // Draw bullets
        shapeRenderer.setColor(Color.YELLOW);
        for (Rectangle bullet : bullets) {
            shapeRenderer.rect(bullet.x, bullet.y, bullet.width, bullet.height);
        }
    }

    public Rectangle getPlayer() {
        return player;
    }

    public void reset() {
        player.x = GameConstants.PLAYER_INITIAL_X;
        player.y = GameConstants.PLAYER_INITIAL_Y;
        bullets.clear();
    }
}
