package io.github.the_actual_game.entities;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.Color;
import io.github.the_actual_game.utils.ModelManager;

public class Enemy {
    private Model3D model;
    protected int life;
    protected int initialLife;
    protected Color color;

    public Enemy(float x, float y, float width, float height, int life) {
        this.model = new Model3D(
            ModelManager.getInstance().getModel("models/enemy.g3db"),
            x, y, 0,
            1.0f
        );
        this.life = life;
        this.initialLife = life;
        this.color = new Color(0, 1, 0, 1);
        updateColor();
    }

    public boolean isAlive() {
        return life > 0;
    }

    public void hit(int damage) {
        life -= damage;
        if (life > 0) {
            float lifePercentage = life / (float)initialLife;
            
            // Update color based on remaining life
            if (lifePercentage > 0.5f) {
                float transition = (1 - lifePercentage) * 2;
                color.set(transition, 1, 0, 1);
            } else {
                float transition = lifePercentage * 2;
                color.set(1, transition, 0, 1);
            }
            updateColor();
        }
    }

    private void updateColor() {
        // Update the model's material color
        model.getModelInstance().materials.first()
            .set(ColorAttribute.createDiffuse(color));
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        if (isAlive()) {
            modelBatch.render(model.getModelInstance(), environment);
        }
    }

    public Model3D getModel() {
        return model;
    }

    public void dispose() {
        if (model != null) {
            model.dispose();
        }
    }

    public void reset(float x, float y) {
        if (model != null) {
            model.dispose();
        }
        this.model = new Model3D(
            ModelManager.getInstance().getModel("models/enemy.g3db"),
            x, y, 0,
            1.0f
        );
        this.life = initialLife;
        this.color.set(0, 1, 0, 1);
        updateColor();
    }
} 