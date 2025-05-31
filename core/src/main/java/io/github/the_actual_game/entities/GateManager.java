package io.github.the_actual_game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

import io.github.the_actual_game.constants.GameConstants;
import io.github.the_actual_game.constants.LevelConfig;

public class GateManager {
    private Array<Gate> gates;
    private float spawnTimer;
    private LevelConfig currentLevelConfig;

    public GateManager() {
        gates = new Array<>();
        spawnTimer = 0;
        setLevel(0); // Start at level 1 (index 0)
    }

    public void setLevel(int level) {
        currentLevelConfig = GameConstants.LEVEL_CONFIGS[level];
        gates.clear();
        spawnTimer = 0;
    }

    public void update(float delta) {
        // Update spawn timer
        spawnTimer += delta;
        if (spawnTimer >= currentLevelConfig.getGateSpawnInterval()) {
            spawnGatePair();
            spawnTimer = 0;
        }

        // Update gate positions
        for (int i = gates.size - 1; i >= 0; i--) {
            Gate gate = gates.get(i);
            gate.rect.y -= currentLevelConfig.getGateSpeed() * delta;
            
            // Remove gates that are off screen or used
            if (gate.rect.y + gate.rect.height < 0 || gate.isUsed()) {
                gates.removeIndex(i);
            }
        }
    }

    private void spawnGatePair() {
        // Spawn a pair of gates, one positive and one negative
        float leftX = 0; // Start of first pane
        float rightX = GameConstants.PANE_WIDTH + GameConstants.PANE_SEPARATOR_WIDTH; // Start of second pane
        boolean leftIsPositive = Math.random() < 0.5;

        // Create left gate
        gates.add(new Gate(leftX, GameConstants.SCREEN_HEIGHT,
                          GameConstants.GATE_WIDTH, GameConstants.GATE_HEIGHT, leftIsPositive));
        
        // Create right gate
        gates.add(new Gate(rightX, GameConstants.SCREEN_HEIGHT,
                          GameConstants.GATE_WIDTH, GameConstants.GATE_HEIGHT, !leftIsPositive));
    }

    public void render(ShapeRenderer shapeRenderer) {
        for (Gate gate : gates) {
            shapeRenderer.setColor(gate.getColor());
            shapeRenderer.rect(gate.rect.x, gate.rect.y, gate.rect.width, gate.rect.height);
        }
    }

    public Array<Gate> getGates() {
        return gates;
    }

    public void reset() {
        setLevel(0);
    }
}
