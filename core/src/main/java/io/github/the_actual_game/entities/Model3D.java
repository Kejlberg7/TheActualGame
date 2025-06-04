package io.github.the_actual_game.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Model3D {
    private ModelInstance modelInstance;
    private final Vector3 position;
    private final Vector3 dimensions;
    private final BoundingBox bounds;
    private float scale;
    private final Quaternion rotation;

    public Model3D(Model model, float x, float y, float z, float scale) {
        this.modelInstance = new ModelInstance(model);
        this.position = new Vector3(x, y, z);
        this.scale = scale;
        this.dimensions = new Vector3();
        this.bounds = new BoundingBox();
        this.rotation = new Quaternion();
        
        // Calculate bounds
        modelInstance.calculateBoundingBox(bounds);
        bounds.getDimensions(dimensions);
        dimensions.scl(scale);
        
        // Set initial transform
        updateTransform();
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        updateTransform();
    }

    public void translate(float x, float y, float z) {
        position.add(x, y, z);
        updateTransform();
    }

    public void setRotation(float x, float y, float z, float degrees) {
        rotation.setFromAxis(x, y, z, degrees);
        updateTransform();
    }

    public void setScale(float scale) {
        this.scale = scale;
        updateTransform();
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getDimensions() {
        return dimensions;
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public BoundingBox getBounds() {
        return bounds;
    }

    public boolean collidesWith(Model3D other) {
        BoundingBox thisBox = new BoundingBox(bounds);
        BoundingBox otherBox = new BoundingBox(other.getBounds());
        
        // Transform bounds to world space
        thisBox.mul(modelInstance.transform);
        otherBox.mul(other.getModelInstance().transform);
        
        return thisBox.intersects(otherBox);
    }

    private void updateTransform() {
        modelInstance.transform.setToTranslation(position);
        modelInstance.transform.rotate(rotation);
        modelInstance.transform.scale(scale, scale, scale);
    }

    public void dispose() {
        // Don't dispose the model since it's shared between instances
        modelInstance = null;
    }
} 