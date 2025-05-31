package io.github.the_actual_game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import io.github.the_actual_game.entities.EnemyManager;
import io.github.the_actual_game.entities.PlayerManager;
import io.github.the_actual_game.constants.GameConstants;
import com.badlogic.gdx.audio.Sound;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private boolean gameOver;
    private BitmapFont font;
    private SpriteBatch batch;
    private EnemyManager enemyManager;
    private PlayerManager playerManager;
    private Sound laserSound;

    public GameScreen() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);
        
        enemyManager = new EnemyManager();
        playerManager = new PlayerManager();
        
        // Load laser sound
        laserSound = Gdx.audio.newSound(Gdx.files.internal("laser-gun-81720.mp3"));
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        if (!gameOver) {
            // Update player and handle shooting
            playerManager.update(delta);
            if (playerManager.handleShooting() && laserSound != null) {
                laserSound.play();
            }

            // Update enemies and check collisions
            enemyManager.update(delta, playerManager.getPlayer());
            if (enemyManager.checkCollisions(playerManager.getPlayer())) {
                gameOver = true;
            }
        }

        // Draw shapes
        shapeRenderer.begin(ShapeType.Filled);
        
        // Draw player and bullets
        playerManager.render(shapeRenderer);
        
        // Draw enemies
        enemyManager.render(shapeRenderer);
        
        shapeRenderer.end();

        // Draw game over text if needed
        if (gameOver) {
            batch.begin();
            font.setColor(Color.RED);
            font.draw(batch, "GAME OVER - Press SPACE to restart", 550, 450);
            batch.end();

            // Restart game if space is pressed
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
                restartGame();
            }
        }
    }

    private void restartGame() {
        gameOver = false;
        playerManager.reset();
        enemyManager.reset();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        if (laserSound != null) {
            laserSound.dispose();
        }
        batch.dispose();
        font.dispose();
    }
}
