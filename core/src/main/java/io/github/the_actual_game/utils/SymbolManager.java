package io.github.the_actual_game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SymbolManager {
    private static TextureAtlas atlas;
    
    public static void initialize() {
        atlas = new TextureAtlas(Gdx.files.internal("symbols.atlas"));
    }
    
    public static TextureRegion getSymbol(String name) {
        if (atlas == null) {
            initialize();
        }
        return atlas.findRegion(name);
    }
    
    public static void dispose() {
        if (atlas != null) {
            atlas.dispose();
            atlas = null;
        }
    }
} 