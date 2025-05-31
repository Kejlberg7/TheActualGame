package io.github.the_actual_game.constants;

public class GameConstants {
    // Screen dimensions
    public static final int SCREEN_WIDTH = 1500;
    public static final int SCREEN_HEIGHT = 900;
    
    // Pane constants
    public static final int NUMBER_OF_PANES = 2;
    public static final float PANE_WIDTH = SCREEN_WIDTH / NUMBER_OF_PANES;
    public static final float PANE_SEPARATOR_WIDTH = 10;

    // Player constants
    public static final float PLAYER_SPEED = 300;
    public static final float PLAYER_WIDTH = 40;
    public static final float PLAYER_HEIGHT = 40;
    public static final float PLAYER_INITIAL_Y = 50;
    public static final float PLAYER_INITIAL_X = PANE_WIDTH / 4;  // Start in the left pane

    // Enemy constants
    public static final float ENEMY_SPEED = 150;
    public static final float ENEMY_WIDTH = 30;
    public static final float ENEMY_HEIGHT = 30;
    public static final float ENEMY_INITIAL_Y = 700;
    public static final int ENEMY_COUNT_PER_PANE = 3;
    public static final float ENEMY_SPACING = PANE_WIDTH / (ENEMY_COUNT_PER_PANE + 1);
    public static final float ENEMY_INITIAL_X = ENEMY_SPACING;  // Will be offset for each pane

    // Bullet constants
    public static final float BULLET_WIDTH = 10;
    public static final float BULLET_HEIGHT = 20;
    public static final float BULLET_SPEED = 600;
}
