package io.github.the_actual_game.screens;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import io.github.the_actual_game.constants.GameConstants;
import io.github.the_actual_game.entities.Enemy;
import io.github.the_actual_game.entities.EnemyManager;
import io.github.the_actual_game.entities.Gate;
import io.github.the_actual_game.entities.GateManager;
import io.github.the_actual_game.entities.PlayerManager;

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
    private String currentName = "";

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
                    // Randomly choose between adjusting shot count or shooting speed
                    if (Math.random() < 0.5) {
                        playerManager.adjustShotCount(gate.isPositive());
                    } else {
                        playerManager.adjustShootingSpeed(gate.isPositive());
                    }
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
                    gameStateManager.setEnterName();
                } else {
                    gameStateManager.setResult();
                }
            }

            // Check if level is complete
            if (enemyManager.isLevelComplete()) {
                gameStateManager.setLevelComplete();
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
            
            // Draw level indicator
            String levelText = "Level: " + gameStateManager.getCurrentLevel();
            font.draw(batch, levelText, 10, GameConstants.SCREEN_HEIGHT - 60);
        } else if (gameStateManager.isLevelComplete()) {
            font.setColor(Color.GREEN);
            String levelCompleteText = "Level " + gameStateManager.getCurrentLevel() + " Complete!";
            float levelCompleteWidth = font.draw(batch, levelCompleteText, 0, 0).width;
            font.draw(batch, levelCompleteText, GameConstants.SCREEN_WIDTH/2 - levelCompleteWidth/2, GameConstants.SCREEN_HEIGHT/2);

            font.setColor(Color.WHITE);
            String pressSpaceText = "Press SPACE to continue";
            float pressSpaceWidth = font.draw(batch, pressSpaceText, 0, 0).width;
            font.draw(batch, pressSpaceText, GameConstants.SCREEN_WIDTH/2 - pressSpaceWidth/2, GameConstants.SCREEN_HEIGHT/2 - 40);

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
                gameStateManager.nextLevel();
                int newLevel = gameStateManager.getCurrentLevel() - 1; // Convert back to 0-based
                enemyManager.setLevel(newLevel);
                playerManager.setLevel(newLevel);
                gateManager.setLevel(newLevel);
            }
        } else if (gameStateManager.isGameOver()) {
            font.setColor(Color.RED);
            String gameOverText = "GAME OVER - Press SPACE to restart";
            float gameOverWidth = font.draw(batch, gameOverText, 0, 0).width;
            font.draw(batch, gameOverText, GameConstants.SCREEN_WIDTH/2 - gameOverWidth/2, GameConstants.SCREEN_HEIGHT/2);
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
                restartGame();
            }
        } else if (gameStateManager.isEnterName()) {
            font.setColor(Color.GREEN);
            String highScoreText = "NEW HIGH SCORE!";
            float highScoreWidth = font.draw(batch, highScoreText, 0, 0).width;
            font.draw(batch, highScoreText, GameConstants.SCREEN_WIDTH/2 - highScoreWidth/2, GameConstants.SCREEN_HEIGHT - 100);

            font.setColor(Color.WHITE);
            String scoreText = "Score: " + score;
            float scoreWidth = font.draw(batch, scoreText, 0, 0).width;
            font.draw(batch, scoreText, GameConstants.SCREEN_WIDTH/2 - scoreWidth/2, GameConstants.SCREEN_HEIGHT - 150);

            String enterNameText = "Enter your name:";
            float enterNameWidth = font.draw(batch, enterNameText, 0, 0).width;
            font.draw(batch, enterNameText, GameConstants.SCREEN_WIDTH/2 - enterNameWidth/2, GameConstants.SCREEN_HEIGHT - 200);

            String currentNameText = currentName;
            float currentNameWidth = font.draw(batch, currentNameText, 0, 0).width;
            font.draw(batch, currentNameText, GameConstants.SCREEN_WIDTH/2 - currentNameWidth/2, GameConstants.SCREEN_HEIGHT - 250);

            String enterDoneText = "Press ENTER when done";
            float enterDoneWidth = font.draw(batch, enterDoneText, 0, 0).width;
            font.draw(batch, enterDoneText, GameConstants.SCREEN_WIDTH/2 - enterDoneWidth/2, GameConstants.SCREEN_HEIGHT - 300);

            currentName = GameStateManager.handleNameInput(currentName);
            if (GameStateManager.isNameEntryComplete()) {
                GameStateManager.saveScoreWithName(SCORE_FILE, currentName, score);
                gameStateManager.setResult();
            }
        } else if (gameStateManager.isResult()) {
            font.setColor(Color.GREEN);
            String gameOverText = "GAME OVER";
            float gameOverWidth = font.draw(batch, gameOverText, 0, 0).width;
            font.draw(batch, gameOverText, GameConstants.SCREEN_WIDTH/2 - gameOverWidth/2, GameConstants.SCREEN_HEIGHT - 100);

            font.setColor(Color.WHITE);
            String yourScoreText = "Your Score: " + score;
            float yourScoreWidth = font.draw(batch, yourScoreText, 0, 0).width;
            font.draw(batch, yourScoreText, GameConstants.SCREEN_WIDTH/2 - yourScoreWidth/2, GameConstants.SCREEN_HEIGHT - 150);

            String highScoreText = "High Score: " + highScore;
            float highScoreWidth = font.draw(batch, highScoreText, 0, 0).width;
            font.draw(batch, highScoreText, GameConstants.SCREEN_WIDTH/2 - highScoreWidth/2, GameConstants.SCREEN_HEIGHT - 200);

            // Display top scores
            List<GameStateManager.ScoreEntry> topScores = GameStateManager.loadTopScores(SCORE_FILE);
            font.setColor(Color.YELLOW);
            String topScoresText = "Top Scores:";
            float topScoresWidth = font.draw(batch, topScoresText, 0, 0).width;
            font.draw(batch, topScoresText, GameConstants.SCREEN_WIDTH/2 - topScoresWidth/2, GameConstants.SCREEN_HEIGHT - 250);

            int y = GameConstants.SCREEN_HEIGHT - 280;
            for (GameStateManager.ScoreEntry entry : topScores) {
                String scoreEntryText = entry.name + ": " + entry.score;
                float scoreEntryWidth = font.draw(batch, scoreEntryText, 0, 0).width;
                font.draw(batch, scoreEntryText, GameConstants.SCREEN_WIDTH/2 - scoreEntryWidth/2, y);
                y -= 30;
            }

            font.setColor(Color.WHITE);
            String restartText = "Press SPACE to restart";
            float restartWidth = font.draw(batch, restartText, 0, 0).width;
            font.draw(batch, restartText, GameConstants.SCREEN_WIDTH/2 - restartWidth/2, y - 30);
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
