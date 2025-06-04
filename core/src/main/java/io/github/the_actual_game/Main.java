package io.github.the_actual_game;

import com.badlogic.gdx.Game;
import io.github.the_actual_game.screens.GameScreen;
import io.github.the_actual_game.utils.FontManager;
import io.github.the_actual_game.utils.SymbolManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {
        FontManager.initialize();
        SymbolManager.initialize();
        setScreen(new GameScreen());
    }

    @Override
    public void dispose() {
        super.dispose();
        FontManager.dispose();
        SymbolManager.dispose();
    }
}
