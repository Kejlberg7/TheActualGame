package io.github.the_actual_game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class FontManager {
    private static BitmapFont symbolFont;
    
    public static void initialize() {
        // Load Roboto font which has good Unicode support
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 24; // Adjust size as needed
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "⚡↑↓⊕⊖"; // Unicode symbols
        
        symbolFont = generator.generateFont(parameter);
        generator.dispose();
    }
    
    public static BitmapFont getSymbolFont() {
        if (symbolFont == null) {
            initialize();
        }
        return symbolFont;
    }
    
    public static void dispose() {
        if (symbolFont != null) {
            symbolFont.dispose();
            symbolFont = null;
        }
    }
} 