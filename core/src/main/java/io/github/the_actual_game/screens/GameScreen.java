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
import com.badlogic.gdx.audio.Sound;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private Rectangle player;
    private Array<Rectangle> enemies;
    private Array<Rectangle> bullets;
    private static final float PLAYER_SPEED = 300;
    private static final float PLAYER_WIDTH = 40;
    private static final float PLAYER_HEIGHT = 40;
    private boolean gameOver;
    private BitmapFont font;
    private SpriteBatch batch;
    private EnemyManager enemyManager;
    private static final float BULLET_WIDTH = 10;
    private static final float BULLET_HEIGHT = 20;
    private static final float BULLET_SPEED = 600;
    private boolean spacePressedLastFrame = false;
    private Sound laserSound;

    public GameScreen() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1500, 900);

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);
        enemyManager = new EnemyManager();

        // Create player at the bottom center of the screen
        player = new Rectangle();
        player.width = PLAYER_WIDTH;
        player.height = PLAYER_HEIGHT;
        player.x = 1500/2 - player.width/2;
        player.y = 50;

        // Create some initial enemies
        enemies = new Array<Rectangle>();
        spawnEnemies();
        // Initialize bullets array
        bullets = new Array<Rectangle>();
        // Load laser sound
        laserSound = Gdx.audio.newSound(Gdx.files.internal("laser-gun-81720.mp3"));
    }

    private void spawnEnemies() {
        for (int i = 0; i < 5; i++) {
            Rectangle enemy = new Rectangle();
            enemy.width = 30;
            enemy.height = 30;
            enemy.x = 100 + i * 300;
            enemy.y = 700;
            enemies.add(enemy);
        }
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

            // Handle shooting
            boolean spacePressed = Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.SPACE);
            if (spacePressed && !spacePressedLastFrame) {
                Rectangle bullet = new Rectangle();
                bullet.width = BULLET_WIDTH;
                bullet.height = BULLET_HEIGHT;
                bullet.x = player.x + player.width / 2 - BULLET_WIDTH / 2;
                bullet.y = player.y + player.height;
                bullets.add(bullet);
                // Play laser sound
                if (laserSound != null) {
                    laserSound.play();
                }
            }
            spacePressedLastFrame = spacePressed;

            // Update bullets
            for (int i = bullets.size - 1; i >= 0; i--) {
                Rectangle bullet = bullets.get(i);
                bullet.y += BULLET_SPEED * delta;
                if (bullet.y > 900) {
                    bullets.removeIndex(i);
                }
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

        // Draw bullets (yellow rectangles)
        shapeRenderer.setColor(Color.YELLOW);
        for (Rectangle bullet : bullets) {
            shapeRenderer.rect(bullet.x, bullet.y, bullet.width, bullet.height);
        }
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
        if (laserSound != null) {
            laserSound.dispose();
        }
        batch.dispose();
        font.dispose();
    }
}
