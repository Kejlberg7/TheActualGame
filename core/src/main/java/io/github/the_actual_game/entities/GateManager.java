package io.github.the_actual_game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import io.github.the_actual_game.constants.GameConstants;
import io.github.the_actual_game.constants.LevelConfig;

public class GateManager {
    private Array<Gate> gates;
    private float spawnTimer;
    private LevelConfig currentLevelConfig;
    private static final float GATE_HEIGHT = 4;  // Make gates thin lines

    public GateManager() {
        gates = new Array<Gate>();
        spawnTimer = 0;
        setLevel(0); // Start at level 1 (index 0)
    }

    public void setLevel(int level) {
        // Ensure level is within bounds
        if (level < 0) level = 0;
        if (level >= GameConstants.MAX_LEVELS) level = GameConstants.MAX_LEVELS - 1;
        
        currentLevelConfig = GameConstants.LEVEL_CONFIGS[level];
        gates.clear();
        spawnTimer = 0;
    }

    public void update(float delta, Array<Rectangle> bullets) {
        spawnTimer += delta;
        if (spawnTimer >= currentLevelConfig.getGateSpawnInterval()) {
            spawnGatePair();
            spawnTimer = 0;
        }

        // Check bullet collisions with gates
        for (Gate gate : gates) {
            if (!gate.isUsed()) {
                for (Rectangle bullet : bullets) {
                    if (bullet.overlaps(gate.rect)) {
                        gate.hit(); // Improve gate's power level
                        // Removed bullet removal to allow pass-through
                    }
                }
            }
        }

        // Update and remove gates
        for (int i = gates.size - 1; i >= 0; i--) {
            Gate gate = gates.get(i);
            gate.rect.y -= currentLevelConfig.getGateSpeed() * delta;
            gate.update(delta); // Update rotation
            
            // Remove gates that are off screen or used
            if (gate.rect.y + gate.rect.height < 0 || gate.isUsed()) {
                gates.removeIndex(i);
            }
        }
    }

    private void spawnGatePair() {
        float halfScreenWidth = GameConstants.SCREEN_WIDTH / 2;
        boolean leftIsPositive = MathUtils.randomBoolean();
        
        // Left gate
        gates.add(new Gate(0, GameConstants.SCREEN_HEIGHT, halfScreenWidth, GATE_HEIGHT, leftIsPositive));
        
        // Right gate (opposite of left gate)
        gates.add(new Gate(halfScreenWidth, GameConstants.SCREEN_HEIGHT, halfScreenWidth, GATE_HEIGHT, !leftIsPositive));
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        for (Gate gate : gates) {
            gate.render(shapeRenderer, batch, font);
        }
    }

    public Array<Gate> getGates() {
        return gates;
    }

    public void reset() {
        setLevel(0);
    }
}
