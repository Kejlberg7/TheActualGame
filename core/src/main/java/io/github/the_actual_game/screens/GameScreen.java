package io.github.the_actual_game.screens;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import io.github.the_actual_game.constants.GameConstants;
import io.github.the_actual_game.entities.Enemy;
import io.github.the_actual_game.entities.EnemyManager;
import io.github.the_actual_game.entities.Gate;
import io.github.the_actual_game.entities.GateManager;
import io.github.the_actual_game.entities.PlayerManager;
import io.github.the_actual_game.utils.ModelManager;

public class GameScreen implements Screen {
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Environment environment;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
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
        setupCamera();
        setupEnvironment();
        setupRendering();
        setupGame();
    }

    private void setupCamera() {
        camera = new PerspectiveCamera(35, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(GameConstants.SCREEN_WIDTH/2 - 150, GameConstants.SCREEN_HEIGHT/3, 1200);
        camera.lookAt(GameConstants.SCREEN_WIDTH/2 - 25, GameConstants.SCREEN_HEIGHT/2, 0);
        camera.near = 1f;
        camera.far = 2000f;
        camera.update();
    }

    private void setupEnvironment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    private void setupRendering() {
        modelBatch = new ModelBatch();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);
    }

    private void setupGame() {
        ModelManager.initialize();
        enemyManager = new EnemyManager();
        playerManager = new PlayerManager();
        gateManager = new GateManager();
        laserSound = Gdx.audio.newSound(Gdx.files.internal("laser-gun-81720.mp3"));
        highScore = GameStateManager.loadHighScore(SCORE_FILE);
        gameStateManager = new GameStateManager();
    }

    @Override
    public void render(float delta) {
        // Clear screen with a dark blue color
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Handle restart input
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (!gameStateManager.isPlaying()) {
                if (gameStateManager.isLevelComplete()) {
                    System.out.println("Starting next level");
                    gameStateManager.nextLevel();
                    playerManager.setLevel(gameStateManager.getCurrentLevel() - 1);
                    enemyManager.setLevel(gameStateManager.getCurrentLevel() - 1);
                    gateManager.setLevel(gameStateManager.getCurrentLevel() - 1);
                } else if (gameStateManager.isResult() || gameStateManager.isGameOver()) {
                    System.out.println("Restarting game from game over");
                    restartGame();
                }
            }
        }

        // Update camera
        camera.update();

        if (gameStateManager.isPlaying()) {
            updateGameLogic(delta);
        }

        // 3D Rendering
        modelBatch.begin(camera);
        if (gameStateManager.isPlaying()) {
            playerManager.render(modelBatch, environment);
            enemyManager.render(modelBatch, environment);
            gateManager.render(modelBatch, environment);
        }
        modelBatch.end();

        // 2D UI Rendering
        renderUI();
    }

    private void updateGameLogic(float delta) {
        playerManager.update(delta);
        if (playerManager.handleShooting() && laserSound != null) {
            laserSound.play();
        }

        gateManager.update(delta);
        enemyManager.update(delta);

        checkCollisions();
        checkGameState();
    }

    private void checkCollisions() {
        // Check collisions between game objects
        playerManager.checkCollisions(enemyManager.getEnemies(), gateManager.getGates());
        score += enemyManager.checkBulletCollisions(playerManager.getBullets());
    }

    private void checkGameState() {
        if (!playerManager.isAlive()) {
            gameStateManager.setGameOver(true);
            if (score > highScore) {
                highScore = score;
                gameStateManager.setEnterName();
            } else {
                gameStateManager.setResult();
            }
        }

        if (enemyManager.isLevelComplete()) {
            gameStateManager.setLevelComplete();
        }
    }

    private void renderUI() {
        spriteBatch.begin();
        if (gameStateManager.isPlaying()) {
            renderGameUI();
        } else if (gameStateManager.isLevelComplete()) {
            renderLevelCompleteUI();
        } else if (gameStateManager.isEnterName()) {
            renderHighScoreUI();
        } else if (gameStateManager.isResult()) {
            renderResultUI();
        }
        spriteBatch.end();
    }

    private void renderGameUI() {
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, "Score: " + score, GameConstants.SCREEN_WIDTH/2 - 50, GameConstants.SCREEN_HEIGHT - 30);
        font.draw(spriteBatch, "Level: " + gameStateManager.getCurrentLevel(), 10, GameConstants.SCREEN_HEIGHT - 60);
    }

    private void renderLevelCompleteUI() {
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, "Level Complete!", GameConstants.SCREEN_WIDTH/2 - 80, GameConstants.SCREEN_HEIGHT/2);
        font.draw(spriteBatch, "Press SPACE to continue", GameConstants.SCREEN_WIDTH/2 - 100, GameConstants.SCREEN_HEIGHT/2 - 40);
    }

    private void renderHighScoreUI() {
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, "New High Score!", GameConstants.SCREEN_WIDTH/2 - 80, GameConstants.SCREEN_HEIGHT/2);
        font.draw(spriteBatch, "Score: " + score, GameConstants.SCREEN_WIDTH/2 - 50, GameConstants.SCREEN_HEIGHT/2 - 40);
        font.draw(spriteBatch, "Press SPACE to restart", GameConstants.SCREEN_WIDTH/2 - 100, GameConstants.SCREEN_HEIGHT/2 - 80);
    }

    private void renderResultUI() {
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, "Game Over!", GameConstants.SCREEN_WIDTH/2 - 60, GameConstants.SCREEN_HEIGHT/2);
        font.draw(spriteBatch, "Score: " + score, GameConstants.SCREEN_WIDTH/2 - 50, GameConstants.SCREEN_HEIGHT/2 - 40);
        font.draw(spriteBatch, "Press SPACE to restart", GameConstants.SCREEN_WIDTH/2 - 100, GameConstants.SCREEN_HEIGHT/2 - 80);
    }

    private void restartGame() {
        System.out.println("Restarting game");
        gameStateManager.reset();
        playerManager.reset();
        enemyManager.reset();
        gateManager.reset();
        score = 0;
        gameStateManager.setGameOver(false); // Ensure game is not in game over state
    }

    @Override
    public void resize(int width, int height) {
        // Maintain aspect ratio
        float aspectRatio = (float)width / (float)height;
        camera.viewportWidth = GameConstants.SCREEN_HEIGHT * aspectRatio;
        camera.viewportHeight = GameConstants.SCREEN_HEIGHT;
        camera.update();
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        spriteBatch.dispose();
        font.dispose();
        if (laserSound != null) {
            laserSound.dispose();
        }
        ModelManager.getInstance().dispose();
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
