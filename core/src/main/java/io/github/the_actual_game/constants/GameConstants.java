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
    public static final float DEFAULT_SHOOTING_INTERVAL = 0.4f;
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
    public static final float BASE_ENEMY_SPEED = 100; // Consistent speed across levels

    // Bullet constants
    public static final float BULLET_WIDTH = 6;
    public static final float BULLET_HEIGHT = 12;
    public static final float BASE_BULLET_SPEED = 400; // Consistent bullet speed

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
        // Level 1 - Easy (5 hits to kill)
        new LevelConfig(
            1,                  // level
            5,                  // enemyCount
            BASE_ENEMY_SPEED,   // enemySpeed
            5,                  // enemyLife
            GATE_SPEED,         // gateSpeed
            5,                  // gateSpawnInterval
            BASE_BULLET_SPEED   // bulletSpeed
        ),
        // Level 2 - Medium (15 hits)
        new LevelConfig(
            2,                  // level
            8,                  // enemyCount
            BASE_ENEMY_SPEED,   // enemySpeed
            15,                 // enemyLife
            GATE_SPEED,         // gateSpeed
            5,                  // gateSpawnInterval
            BASE_BULLET_SPEED   // bulletSpeed
        ),
        // Level 3 - Hard (45 hits)
        new LevelConfig(
            3,                  // level
            10,                 // enemyCount
            BASE_ENEMY_SPEED,   // enemySpeed
            45,                 // enemyLife
            GATE_SPEED,         // gateSpeed
            4,                  // gateSpawnInterval
            BASE_BULLET_SPEED   // bulletSpeed
        ),
        // Level 4 - Very Hard (135 hits)
        new LevelConfig(
            4,                  // level
            12,                 // enemyCount
            BASE_ENEMY_SPEED,   // enemySpeed
            135,                // enemyLife
            GATE_SPEED,         // gateSpeed
            4,                  // gateSpawnInterval
            BASE_BULLET_SPEED   // bulletSpeed
        ),
        // Level 5 - Extreme (405 hits)
        new LevelConfig(
            5,                  // level
            15,                 // enemyCount
            BASE_ENEMY_SPEED,   // enemySpeed
            405,                // enemyLife
            GATE_SPEED,         // gateSpeed
            3,                  // gateSpawnInterval
            BASE_BULLET_SPEED   // bulletSpeed
        )
    };
}
