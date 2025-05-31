package io.github.the_actual_game;

import com.badlogic.gdx.Game;

import io.github.the_actual_game.screens.GameScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
