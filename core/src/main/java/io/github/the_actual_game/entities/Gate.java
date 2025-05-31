package io.github.the_actual_game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class Gate {
    public Rectangle rect;
    private boolean isPositive;
    private Color color;
    private boolean used;

    public Gate(float x, float y, float width, float height, boolean isPositive) {
        this.rect = new Rectangle(x, y, width, height);
        this.isPositive = isPositive;
        this.color = isPositive ? Color.GREEN : Color.RED;
        this.used = false;
    }

    public boolean isPositive() {
        return isPositive;
    }

    public Color getColor() {
        return color;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed() {
        this.used = true;
    }
}
