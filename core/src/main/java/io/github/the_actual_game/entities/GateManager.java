package io.github.the_actual_game.entities;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;
import io.github.the_actual_game.constants.GameConstants;
import io.github.the_actual_game.constants.LevelConfig;

public class GateManager {
    private Array<Gate> gates;
    private float spawnTimer;
    private LevelConfig currentLevelConfig;
    private static final float SPAWN_CHANCE = 0.5f; // Increased to 50% chance to spawn gates
    private static final float MIN_SPAWN_INTERVAL = 2.0f; // Minimum time between spawn attempts

    public GateManager() {
        gates = new Array<>();
        spawnTimer = MIN_SPAWN_INTERVAL; // Start with a delay
        setLevel(0);
    }

    public void setLevel(int level) {
        if (level < 0) level = 0;
        if (level >= GameConstants.MAX_LEVELS) level = GameConstants.MAX_LEVELS - 1;
        
        currentLevelConfig = GameConstants.LEVEL_CONFIGS[level];
        gates.clear();
        spawnTimer = MIN_SPAWN_INTERVAL;
    }

    public void update(float delta) {
        spawnTimer += delta;
        
        // Try to spawn gates when enough time has passed
        if (spawnTimer >= MIN_SPAWN_INTERVAL) {
            if (Math.random() < SPAWN_CHANCE) {
                spawnGatePair();
            }
            spawnTimer = 0;
        }

        // Update and remove gates
        for (int i = gates.size - 1; i >= 0; i--) {
            Gate gate = gates.get(i);
            gate.update(delta);
            
            // Remove gates that are off screen or used
            if (gate.getModel().getPosition().y < 0 || gate.isUsed()) {
                gate.dispose();
                gates.removeIndex(i);
            }
        }
    }

    private void spawnGatePair() {
        float halfScreenWidth = GameConstants.SCREEN_WIDTH / 2;
        boolean leftIsPositive = Math.random() < 0.5;
        
        // Left gate - centered in left pane
        gates.add(new Gate(
            halfScreenWidth/2 - GameConstants.GATE_WIDTH/2,  // Center in left pane
            GameConstants.SCREEN_HEIGHT,
            GameConstants.GATE_WIDTH,
            GameConstants.GATE_HEIGHT,
            leftIsPositive
        ));
        
        // Right gate - centered in right pane
        gates.add(new Gate(
            halfScreenWidth + halfScreenWidth/2 - GameConstants.GATE_WIDTH/2,  // Center in right pane
            GameConstants.SCREEN_HEIGHT,
            GameConstants.GATE_WIDTH,
            GameConstants.GATE_HEIGHT,
            !leftIsPositive
        ));
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        for (Gate gate : gates) {
            gate.render(modelBatch, environment);
        }
    }

    public Array<Gate> getGates() {
        return gates;
    }

    public void reset() {
        // Clean up existing gates
        for (Gate gate : gates) {
            gate.dispose();
        }
        gates.clear();
        spawnTimer = MIN_SPAWN_INTERVAL;
        setLevel(0);
    }

    public void dispose() {
        for (Gate gate : gates) {
            gate.dispose();
        }
    }
}
