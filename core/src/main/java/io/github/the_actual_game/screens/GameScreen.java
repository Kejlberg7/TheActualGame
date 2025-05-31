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
import com.badlogic.gdx.utils.Array;

import io.github.the_actual_game.entities.EnemyManager;
import io.github.the_actual_game.entities.PlayerManager;
import io.github.the_actual_game.constants.GameConstants;
import com.badlogic.gdx.audio.Sound;
import io.github.the_actual_game.entities.Enemy;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private boolean gameOver;
    private BitmapFont font;
    private SpriteBatch batch;
    private EnemyManager enemyManager;
    private PlayerManager playerManager;
    private Sound laserSound;
    private int score = 0;
    private GameStateManager gameStateManager;
    private int highScore = 0;
    private final String SCORE_FILE = "scores.txt";

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
        highScore = GameStateManager.loadHighScore(SCORE_FILE);
        gameStateManager = new GameStateManager();
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

        if (gameStateManager.isPlaying() && !gameStateManager.isGameOver()) {
            // Update player and handle shooting
            playerManager.update(delta);
            if (playerManager.handleShooting() && laserSound != null) {
                laserSound.play();
            }

            // Update enemies and check collisions
            enemyManager.update(delta, playerManager.getPlayer());
            if (enemyManager.checkCollisions(playerManager.getPlayer())) {
                gameStateManager.setGameOver(true);
            }

            // Bullet-enemy collision detection
            Array<Rectangle> bullets = playerManager.getBullets();
            Array<Enemy> enemies = enemyManager.getEnemies();
            for (int i = bullets.size - 1; i >= 0; i--) {
                Rectangle bullet = bullets.get(i);
                for (Enemy enemy : enemies) {
                    if (!enemy.isAlive()) continue;
                    if (enemy.rect.overlaps(bullet)) {
                        enemy.hit(1); // Damage is 1 for now
                        if (!enemy.isAlive()) {
                            score += 10;
                        }
                        bullets.removeIndex(i);
                        break; // Bullet can only hit one enemy
                    }
                }
            }

            // Check if all enemies are dead
            if (GameStateManager.areAllEnemiesDead(enemyManager)) {
                gameStateManager.setResult();
                if (score > highScore) {
                    highScore = score;
                    GameStateManager.saveHighScore(SCORE_FILE, highScore);
                }
            }
        }

        // Draw shapes
        shapeRenderer.begin(ShapeType.Filled);
        
        // Draw player and bullets
        playerManager.render(shapeRenderer);
        
        // Draw enemies
        enemyManager.render(shapeRenderer);
        
        shapeRenderer.end();

        // Draw score in the upper right corner (only during play)
        if (gameStateManager.isPlaying()) {
            batch.begin();
            font.setColor(Color.WHITE);
            String scoreText = "Score: " + score;
            font.draw(batch, scoreText, GameConstants.SCREEN_WIDTH - 250, GameConstants.SCREEN_HEIGHT - 30);
            batch.end();
        }

        // Draw game over text if needed
        if (gameStateManager.isGameOver()) {
            batch.begin();
            font.setColor(Color.RED);
            font.draw(batch, "GAME OVER - Press SPACE to restart", 550, 450);
            batch.end();
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
                restartGame();
            }
        }

        // Draw result screen if all enemies are dead
        if (gameStateManager.isResult()) {
            batch.begin();
            font.setColor(Color.GREEN);
            font.draw(batch, "RESULTS", 650, 500);
            font.setColor(Color.WHITE);
            font.draw(batch, "Your Score: " + score, 650, 450);
            font.draw(batch, "High Score: " + highScore, 650, 400);
            font.setColor(Color.YELLOW);
            font.draw(batch, "Press SPACE to restart", 650, 350);
            batch.end();
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
                restartGame();
            }
        }
    }

    private void restartGame() {
        gameStateManager.reset();
        playerManager.reset();
        enemyManager.reset();
        score = 0;
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
