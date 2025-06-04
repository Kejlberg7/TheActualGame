package io.github.the_actual_game.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import io.github.the_actual_game.constants.GameConstants;

public class ModelBuilder3D {
    private static ModelBuilder modelBuilder = new ModelBuilder();

    public static Model createPlayer() {
        return modelBuilder.createBox(
            40f, 40f, 20f,
            new Material(ColorAttribute.createDiffuse(Color.BLUE)),
            Usage.Position | Usage.Normal
        );
    }

    public static Model createEnemy() {
        modelBuilder.begin();
        
        // Create a pyramid using triangles
        modelBuilder.node().id = "base";
        Material material = new Material(ColorAttribute.createDiffuse(Color.GREEN));
        
        // Create pyramid manually using triangles
        float width = 25f;
        float height = 25f;
        float depth = 25f;
        
        modelBuilder.part("pyramid", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, material)
            .cone(width, height, depth, 4); // Using cone with 4 divisions to create a pyramid shape

        return modelBuilder.end();
    }

    public static Model createBullet() {
        return modelBuilder.createCapsule(
            6f, 12f, 8,
            new Material(ColorAttribute.createDiffuse(new Color(0.8f, 0f, 1f, 1f))),
            Usage.Position | Usage.Normal
        );
    }

    public static Model createGate() {
        modelBuilder.begin();
        
        // Main gate structure
        modelBuilder.node().id = "gate";
        modelBuilder.part("frame", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,
            new Material(ColorAttribute.createDiffuse(Color.WHITE)))
            .box(GameConstants.PANE_WIDTH, GameConstants.GATE_HEIGHT, 10f);

        // Add decorative elements
        float elementSize = 5f;
        int elements = 3;
        float spacing = 15f;
        float startX = -(spacing * (elements - 1)) / 2;

        for (int i = 0; i < elements; i++) {
            float x = startX + i * spacing;
            float y = GameConstants.GATE_HEIGHT / 2 + elementSize;
            modelBuilder.node().id = "element" + i;
            modelBuilder.part("element" + i, GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)))
                .sphere(elementSize, elementSize, elementSize, 8, 8);
        }

        return modelBuilder.end();
    }

    public static void dispose() {
        // No need to dispose the ModelBuilder itself
    }
} 