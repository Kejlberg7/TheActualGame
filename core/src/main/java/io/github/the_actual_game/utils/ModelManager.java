package io.github.the_actual_game.utils;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

public class ModelManager implements Disposable {
    private static ModelManager instance;
    private final ObjectMap<String, Model> models;

    private ModelManager() {
        models = new ObjectMap<>();
        createModels();
    }

    public static ModelManager getInstance() {
        if (instance == null) {
            instance = new ModelManager();
        }
        return instance;
    }

    private void createModels() {
        // Create and store all models
        models.put("models/player.g3db", ModelBuilder3D.createPlayer());
        models.put("models/enemy.g3db", ModelBuilder3D.createEnemy());
        models.put("models/bullet.g3db", ModelBuilder3D.createBullet());
        models.put("models/gate.g3db", ModelBuilder3D.createGate());
    }

    public Model getModel(String modelPath) {
        return models.get(modelPath);
    }

    @Override
    public void dispose() {
        for (Model model : models.values()) {
            model.dispose();
        }
        ModelBuilder3D.dispose();
    }

    public static void initialize() {
        getInstance();
    }
} 