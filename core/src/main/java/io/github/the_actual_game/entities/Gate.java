package io.github.the_actual_game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import io.github.the_actual_game.utils.FontManager;
import io.github.the_actual_game.utils.SymbolManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Gate {
    public Rectangle rect;
    private boolean isUsed;
    private GateType type;
    private float rotation;
    private static final float ROTATION_SPEED = 90f; // Degrees per second
    private int powerLevel; // Negative = bad, Positive = good
    private static final int MAX_POWER_LEVEL = 5;
    private static final int MIN_POWER_LEVEL = -5;

    public enum GateType {
        SPEED,
        SHOTS
    }

    public Gate(float x, float y, float width, float height, boolean isPositive) {
        this.rect = new Rectangle(x, y, width, height);
        this.isUsed = false;
        this.type = Math.random() < 0.5 ? GateType.SPEED : GateType.SHOTS;
        this.rotation = 0;
        // Start with either positive or negative power level
        this.powerLevel = isPositive ? 1 : -1;
    }

    public void hit() {
        if (!isUsed) {
            // Increment or decrement power level based on current state
            if (powerLevel < 0) {
                powerLevel++; // Move towards positive
            } else {
                powerLevel++; // Increase positive power
            }
            
            // Clamp power level
            if (powerLevel > MAX_POWER_LEVEL) {
                powerLevel = MAX_POWER_LEVEL;
            }
        }
    }

    public boolean isPositive() {
        return powerLevel > 0;
    }

    public void update(float delta) {
        // Update rotation
        rotation += ROTATION_SPEED * delta;
        if (rotation >= 360) {
            rotation -= 360;
        }
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont defaultFont) {
        if (!isUsed) {
            // Draw the line with color based on power level
            Color gateColor = getGateColor();
            shapeRenderer.setColor(gateColor);
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);

            // Draw the rotating symbol above the line
            batch.begin();
            TextureRegion symbol = SymbolManager.getSymbol(getSymbolName());
            float symbolSize = 32;
            
            // Calculate center position for rotation
            float centerX = rect.x + (rect.width - symbolSize) / 2;
            float centerY = rect.y + 30;
            
            // Draw with rotation around center
            batch.draw(symbol,
                      centerX, centerY,
                      symbolSize/2, symbolSize/2,
                      symbolSize, symbolSize,
                      1, 1,
                      rotation);

            // Draw power level
            String powerText = String.valueOf(Math.abs(powerLevel));
            float textWidth = defaultFont.draw(batch, powerText, 0, 0).width;
            defaultFont.draw(batch, powerText,
                     centerX + (symbolSize - textWidth) / 2,
                     centerY - 10);
            
            batch.end();
        }
    }

    private Color getGateColor() {
        if (powerLevel > 0) {
            // Transition from white to green for positive
            float intensity = powerLevel / (float)MAX_POWER_LEVEL;
            return new Color(1 - intensity, 1, 1 - intensity, 1);
        } else {
            // Transition from white to red for negative
            float intensity = Math.abs(powerLevel) / (float)Math.abs(MIN_POWER_LEVEL);
            return new Color(1, 1 - intensity, 1 - intensity, 1);
        }
    }

    private String getSymbolName() {
        if (type == GateType.SPEED) {
            return isPositive() ? "speed_up" : "speed_down";
        } else {
            return isPositive() ? "shots_up" : "shots_down";
        }
    }

    public GateType getType() {
        return type;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed() {
        this.isUsed = true;
    }

    public int getPowerLevel() {
        return powerLevel;
    }
}
