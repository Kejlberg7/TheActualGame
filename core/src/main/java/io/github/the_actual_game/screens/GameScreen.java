package io.github.the_actual_game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Color;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private Rectangle player;
    private Array<Rectangle> enemies;
    private static final float PLAYER_SPEED = 300;
    private static final float PLAYER_WIDTH = 40;
    private static final float PLAYER_HEIGHT = 40;

    public GameScreen() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1500, 900);
        
        shapeRenderer = new ShapeRenderer();
        
        // Create player at the bottom center of the screen
        player = new Rectangle();
        player.width = PLAYER_WIDTH;
        player.height = PLAYER_HEIGHT;
        player.x = 1500/2 - player.width/2;
        player.y = 50;
        
        // Create some initial enemies
        enemies = new Array<Rectangle>();
        spawnEnemies();
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

        // Draw shapes
        shapeRenderer.begin(ShapeType.Filled);
        
        // Draw player (blue rectangle)
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(player.x, player.y, player.width, player.height);
        
        // Draw enemies (red rectangles)
        shapeRenderer.setColor(Color.RED);
        for (Rectangle enemy : enemies) {
            shapeRenderer.rect(enemy.x, enemy.y, enemy.width, enemy.height);
        }
        
        shapeRenderer.end();
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
    }
} 