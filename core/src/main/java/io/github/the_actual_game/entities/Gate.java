package io.github.the_actual_game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Gate {
    public Rectangle rect;
    private boolean used;
    private int powerLevel; // Negative = bad, Positive = good
    private static final int MAX_POWER_LEVEL = 5;
    private static final int MIN_POWER_LEVEL = -7;
    private GateType type;
    private int currentLevel;
    private int hitCount;

    public enum GateType {
        SPEED,
        SHOTS
    }

    public Gate(float x, float y, float width, float height, int level) {
        this.rect = new Rectangle(x, y, width, height);
        this.used = false;
        this.currentLevel = level;
        this.hitCount = 0;
        // Start with a random negative power level between -7 and -4
        this.powerLevel = MathUtils.random(MIN_POWER_LEVEL, -4);
        // Randomly assign type
        this.type = MathUtils.randomBoolean() ? GateType.SPEED : GateType.SHOTS;
    }

    public boolean isPositive() {
        return powerLevel > 0;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed() {
        this.used = true;
    }

    public void hit() {
        if (!used) {
            hitCount++;
            // Calculate required hits based on level (exponential scaling)
            int requiredHits = (int)Math.pow(2, currentLevel - 1);
            
            // Only improve power level if we've accumulated enough hits
            if (hitCount >= requiredHits) {
                powerLevel = Math.min(powerLevel + 1, MAX_POWER_LEVEL);
                hitCount = 0; // Reset hit count for next power level increase
            }
        }
    }

    public int getPowerLevel() {
        return powerLevel;
    }

    public GateType getType() {
        return type;
    }

    public Color getColor() {
        if (used) {
            return new Color(0.5f, 0.5f, 0.5f, 0.5f); // Gray for used gates
        }
        
        if (powerLevel > 0) {
            // Positive gates: Green with intensity based on power level
            float intensity = Math.min(powerLevel / (float)MAX_POWER_LEVEL, 1.0f);
            return new Color(0, intensity, 0, 1);
        } else {
            // Negative gates: Red with intensity based on power level
            float intensity = Math.min(Math.abs(powerLevel) / (float)Math.abs(MIN_POWER_LEVEL), 1.0f);
            return new Color(intensity, 0, 0, 1);
        }
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        // Draw the gate
        shapeRenderer.setColor(getColor());
        shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);

        // Draw the power level number and type symbol
        batch.begin();
        font.setColor(Color.WHITE);
        
        // Draw the type symbol
        String symbol = getTypeSymbol();
        float symbolWidth = font.draw(batch, symbol, 0, 0).width;
        font.draw(batch, symbol, 
                 rect.x + (rect.width - symbolWidth) / 2, 
                 rect.y + 50); // Position symbol above the power level
        
        // Draw the power level and hit progress
        String powerText = (powerLevel >= 0 ? "+" : "") + powerLevel;
        if (hitCount > 0) {
            int requiredHits = (int)Math.pow(2, currentLevel - 1);
            powerText += " (" + hitCount + "/" + requiredHits + ")";
        }
        float textWidth = font.draw(batch, powerText, 0, 0).width;
        font.draw(batch, powerText, 
                 rect.x + (rect.width - textWidth) / 2, 
                 rect.y + 30); // Position power level above the gate
        batch.end();
    }

    private String getTypeSymbol() {
        if (type == GateType.SPEED) {
            return powerLevel >= 0 ? ">>>" : "<<<";
        } else {
            return powerLevel >= 0 ? "+1" : "-1";
        }
    }
}
