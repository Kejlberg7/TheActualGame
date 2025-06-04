package io.github.the_actual_game.entities;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.Color;
import io.github.the_actual_game.utils.ModelManager;
import io.github.the_actual_game.constants.GameConstants;

public class Gate {
    public enum GateType { SPEED, SHOTS }
    
    private Model3D model;
    private GateType type;
    private int powerLevel;
    private boolean used;
    private Color color;
    private float rotationAngle;
    private static final float ROTATION_SPEED = 90f; // degrees per second

    public Gate(float x, float y, float width, float height, boolean isPositive) {
        this.model = new Model3D(
            ModelManager.getInstance().getModel("models/gate.g3db"),
            x, y, 0,
            1.0f
        );
        
        this.type = Math.random() < 0.5 ? GateType.SPEED : GateType.SHOTS;
        this.powerLevel = isPositive ? 1 : -1;
        this.used = false;
        this.color = new Color();
        updateColor();
    }

    public void update(float delta) {
        // Move the gate downward
        model.translate(0, -GameConstants.GATE_SPEED * delta, 0);
    }

    public void hit() {
        if (!used) {
            if (powerLevel > 0) {
                powerLevel = Math.min(powerLevel + 1, 5);
            } else {
                powerLevel = Math.max(powerLevel - 1, -5);
            }
            updateColor();
        }
    }

    private void updateColor() {
        // Set color based on type and power level
        float intensity = Math.min(Math.abs(powerLevel) / 5f, 1f);
        if (type == GateType.SPEED) {
            if (powerLevel > 0) {
                color.set(0, intensity, 0, 1); // Green for speed up
            } else {
                color.set(intensity, 0, 0, 1); // Red for slow down
            }
        } else {
            if (powerLevel > 0) {
                color.set(0, 0, intensity, 1); // Blue for more shots
            } else {
                color.set(intensity, intensity, 0, 1); // Yellow for fewer shots
            }
        }
        
        // Update model color
        model.getModelInstance().materials.first()
            .set(ColorAttribute.createDiffuse(color));
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        if (!used) {
            modelBatch.render(model.getModelInstance(), environment);
        }
    }

    public Model3D getModel() {
        return model;
    }

    public GateType getType() {
        return type;
    }

    public int getPowerLevel() {
        return powerLevel;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed() {
        this.used = true;
        System.out.println("Gate used: type=" + type + ", powerLevel=" + powerLevel);
    }

    public void dispose() {
        if (model != null) {
            model.dispose();
        }
    }
}
