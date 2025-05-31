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
import com.badlogic.gdx.audio.Sound;

import io.github.the_actual_game.entities.Enemy;
import io.github.the_actual_game.entities.EnemyManager;
import io.github.the_actual_game.entities.Gate;
import io.github.the_actual_game.entities.GateManager;
import io.github.the_actual_game.entities.PlayerManager;
import io.github.the_actual_game.constants.GameConstants;

import java.util.List;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private SpriteBatch batch;
    private EnemyManager enemyManager;
    private PlayerManager playerManager;
    private GateManager gateManager;
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
        gateManager = new GateManager();
        
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

        if (gameStateManager.isPlaying()) {
            // Update player and handle shooting
            playerManager.update(delta);
            if (playerManager.handleShooting() && laserSound != null) {
                laserSound.play();
            }

            // Update gates and check for collisions
            gateManager.update(delta);
            Rectangle player = playerManager.getPlayer();
            for (Gate gate : gateManager.getGates()) {
                if (!gate.isUsed() && gate.rect.overlaps(player)) {
                    playerManager.adjustShots(gate.isPositive());
                    gate.setUsed();
                }
            }

            // Update enemies and check collisions
            enemyManager.update(delta, playerManager.getPlayer());
            
            // Check if any enemy has passed the player or collided with them
            Array<Enemy> enemies = enemyManager.getEnemies();
            for (Enemy enemy : enemies) {
                if (!enemy.isAlive()) continue;
                
                // Check if enemy has passed the player's y position
                if (enemy.rect.y + enemy.rect.height < playerManager.getPlayer().y) {
                    playerManager.hit();
                    enemy.rect.y = GameConstants.SCREEN_HEIGHT; // Move enemy back to top
                }
                
                // Check collision with player
                if (!playerManager.isInvulnerable() && enemy.rect.overlaps(playerManager.getPlayer())) {
                    playerManager.hit();
                }
            }

            // Check if player has lost all lives
            if (!playerManager.isAlive()) {
                gameStateManager.setGameOver(true);
                if (score > highScore) {
                    highScore = score;
                    GameStateManager.saveHighScore(SCORE_FILE, highScore);
                }
                gameStateManager.setResult();
            }

            // Bullet-enemy collision detection
            Array<Rectangle> bullets = playerManager.getBullets();
            for (int i = bullets.size - 1; i >= 0; i--) {
                Rectangle bullet = bullets.get(i);
                for (Enemy enemy : enemies) {
                    if (!enemy.isAlive()) continue;
                    if (enemy.rect.overlaps(bullet)) {
                        enemy.hit(1);
                        if (!enemy.isAlive()) {
                            score += 10;
                        }
                        bullets.removeIndex(i);
                        break;
                    }
                }
            }
        }

        // Draw shapes
        shapeRenderer.begin(ShapeType.Filled);
        
        // Draw player and bullets
        playerManager.render(shapeRenderer);
        
        // Draw enemies
        enemyManager.render(shapeRenderer);
        
        // Draw gates
        gateManager.render(shapeRenderer);
        
        shapeRenderer.end();

        // Draw score and other UI elements
        batch.begin();
        if (gameStateManager.isPlaying()) {
            font.setColor(Color.WHITE);
            String scoreText = "Score: " + score;
            font.draw(batch, scoreText, GameConstants.SCREEN_WIDTH/2 - 50, GameConstants.SCREEN_HEIGHT - 30);
        } else if (gameStateManager.isGameOver()) {
            font.setColor(Color.RED);
            font.draw(batch, "GAME OVER - Press SPACE to restart", GameConstants.SCREEN_WIDTH/2 - 150, GameConstants.SCREEN_HEIGHT/2);
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
                restartGame();
            }
        } else if (gameStateManager.isResult()) {
            font.setColor(Color.GREEN);
            font.draw(batch, "GAME OVER", GameConstants.SCREEN_WIDTH/2 - 50, GameConstants.SCREEN_HEIGHT - 100);
            font.setColor(Color.WHITE);
            font.draw(batch, "Your Score: " + score, GameConstants.SCREEN_WIDTH/2 - 70, GameConstants.SCREEN_HEIGHT - 150);
            font.draw(batch, "High Score: " + highScore, GameConstants.SCREEN_WIDTH/2 - 70, GameConstants.SCREEN_HEIGHT - 200);
            
            // Display top scores
            List<GameStateManager.ScoreEntry> topScores = GameStateManager.loadTopScores(SCORE_FILE);
            font.setColor(Color.YELLOW);
            font.draw(batch, "Top Scores:", GameConstants.SCREEN_WIDTH/2 - 60, GameConstants.SCREEN_HEIGHT - 250);
            int y = GameConstants.SCREEN_HEIGHT - 280;
            for (GameStateManager.ScoreEntry entry : topScores) {
                font.draw(batch, entry.name + ": " + entry.score, GameConstants.SCREEN_WIDTH/2 - 70, y);
                y -= 30;
            }
            
            font.setColor(Color.WHITE);
            font.draw(batch, "Press SPACE to restart", GameConstants.SCREEN_WIDTH/2 - 100, y - 30);
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
                restartGame();
            }
        }
        batch.end();
    }

    private void restartGame() {
        gameStateManager.reset();
        playerManager.reset();
        enemyManager.reset();
        gateManager.reset();
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
