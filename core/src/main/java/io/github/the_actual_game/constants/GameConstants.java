package io.github.the_actual_game.constants;

public class GameConstants {
    // Screen dimensions
    public static final int SCREEN_WIDTH = 360;
    public static final int SCREEN_HEIGHT = 640;
    
    // Pane constants
    public static final int NUMBER_OF_PANES = 2;
    public static final float PANE_WIDTH = SCREEN_WIDTH / NUMBER_OF_PANES;
    public static final float PANE_SEPARATOR_WIDTH = 4;

    // Player constants
    public static final float PLAYER_SPEED = 300;
    public static final float PLAYER_WIDTH = 40;
    public static final float PLAYER_HEIGHT = 40;
    public static final float PLAYER_INITIAL_Y = 50;
    public static final float PLAYER_INITIAL_X = PANE_WIDTH / 4;  // Start in the left pane
    public static final int PLAYER_DEFAULT_LIFE = 3; // Number of lives the player starts with
    public static final float DEFAULT_SHOOTING_INTERVAL = 0.1f;
    public static final float MIN_SHOOTING_INTERVAL = 0.05f;
    public static final float MAX_SHOOTING_INTERVAL = 0.5f;
    public static final int MIN_SHOT_COUNT = 1;
    public static final int MAX_SHOT_COUNT = 5;
    public static final int DEFAULT_SHOT_COUNT = 1;
    public static final float MULTI_SHOT_SPREAD = 8f; // Space between multiple shots

    // Enemy base constants
    public static final float ENEMY_WIDTH = 25;
    public static final float ENEMY_HEIGHT = 25;
    public static final float ENEMY_INITIAL_Y = 500;
    public static final int ENEMY_COUNT_PER_PANE = 3;
    public static final float ENEMY_SPACING = PANE_WIDTH / (ENEMY_COUNT_PER_PANE + 1);
    public static final float ENEMY_INITIAL_X = ENEMY_SPACING;  // Will be offset for each pane

    // Bullet constants
    public static final float BULLET_WIDTH = 6;
    public static final float BULLET_HEIGHT = 12;

    // Gate constants
    public static final float GATE_WIDTH = PANE_WIDTH; // Fill entire pane width
    public static final float GATE_HEIGHT = 8; // Make them slim
    public static final float GATE_SPEED = 150;
    public static final int GATE_SPAWN_INTERVAL = 5; // Seconds between gate spawns
    public static final int MAX_SHOTS = 5;
    public static final int MIN_SHOTS = 1;

    // Level constants
    public static final int MAX_LEVELS = 5;
    public static final LevelConfig[] LEVEL_CONFIGS = new LevelConfig[] {
        // Level 1 - Easy
        new LevelConfig(
            5,          // enemyCount
            100,        // enemySpeed
            5,          // enemyLife
            150,        // gateSpeed
            5,          // gateSpawnInterval
            400         // bulletSpeed
        ),
        // Level 2 - Medium
        new LevelConfig(
            8,          // enemyCount
            150,        // enemySpeed
            8,          // enemyLife
            200,        // gateSpeed
            4,          // gateSpawnInterval
            450         // bulletSpeed
        ),
        // Level 3 - Hard
        new LevelConfig(
            12,         // enemyCount
            200,        // enemySpeed
            10,         // enemyLife
            250,        // gateSpeed
            3,          // gateSpawnInterval
            500         // bulletSpeed
        ),
        // Level 4 - Very Hard
        new LevelConfig(
            15,         // enemyCount
            250,        // enemySpeed
            12,         // enemyLife
            300,        // gateSpeed
            2,          // gateSpawnInterval
            550         // bulletSpeed
        ),
        // Level 5 - Extreme
        new LevelConfig(
            20,         // enemyCount
            300,        // enemySpeed
            15,         // enemyLife
            350,        // gateSpeed
            2,          // gateSpawnInterval
            600         // bulletSpeed
        )
    };
}
