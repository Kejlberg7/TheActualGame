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
import com.badlogic.gdx.math.Rectangle;

import io.github.the_actual_game.entities.EnemyManager;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private Rectangle player;
    private static final float PLAYER_SPEED = 300;
    private static final float PLAYER_WIDTH = 40;
    private static final float PLAYER_HEIGHT = 40;
    private boolean gameOver;
    private BitmapFont font;
    private SpriteBatch batch;
    private EnemyManager enemyManager;

    public GameScreen() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1500, 900);
        
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);
        
        // Create player at the bottom center of the screen
        player = new Rectangle();
        player.width = PLAYER_WIDTH;
        player.height = PLAYER_HEIGHT;
        player.x = 1500/2 - player.width/2;
        player.y = 50;
        
        // Initialize enemy manager
        enemyManager = new EnemyManager();
        gameOver = false;
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
            // Handle player movement
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
                player.x -= PLAYER_SPEED * delta;
            }
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
                player.x += PLAYER_SPEED * delta;
            }

            // Keep player within screen bounds
            if (player.x < 0) player.x = 0;
            if (player.x > 1500 - player.width) player.x = 1500 - player.width;

            // Update enemies and check collisions
            enemyManager.update(delta, player);
            if (enemyManager.checkCollisions(player)) {
                gameOver = true;
            }
        }

        // Draw shapes
        shapeRenderer.begin(ShapeType.Filled);
        
        // Draw player (blue rectangle)
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(player.x, player.y, player.width, player.height);
        
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
        player.x = 1500/2 - player.width/2;
        player.y = 50;
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
        batch.dispose();
        font.dispose();
    }
} 