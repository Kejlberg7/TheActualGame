package io.github.the_actual_game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Gate {
    public Rectangle rect;
    private boolean isPositive;
    private boolean isUsed;
    private GateType type;

    public enum GateType {
        SPEED,
        SHOTS
    }

    public Gate(float x, float y, float width, float height, boolean isPositive) {
        this.rect = new Rectangle(x, y, width, height);
        this.isPositive = isPositive;
        this.isUsed = false;
        // Randomly assign type when created
        this.type = Math.random() < 0.5 ? GateType.SPEED : GateType.SHOTS;
    }

    public boolean isPositive() {
        return isPositive;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed() {
        isUsed = true;
    }

    public GateType getType() {
        return type;
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        if (!isUsed) {
            // Draw the line
            shapeRenderer.setColor(isPositive ? Color.GREEN : Color.RED);
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);

            // Draw the symbol above the line
            batch.begin();
            font.setColor(Color.WHITE);
            String symbol = getSymbol();
            // Get the width of the text for centering
            float textWidth = font.draw(batch, symbol, 0, 0).width;
            // Draw centered above the line
            font.draw(batch, symbol, 
                     rect.x + (rect.width - textWidth) / 2, 
                     rect.y + 30); // Position text 30 pixels above the line
            batch.end();
        }
    }

    private String getSymbol() {
        if (type == GateType.SPEED) {
            return isPositive ? ">>>" : "<<<";  // Triple arrows for speed
        } else {
            return isPositive ? "+1" : "-1";    // +1/-1 for shot count
        }
    }
}
