package io.github.the_actual_game.constants;

public class LevelConfig {
    private final int enemyCount;
    private final float enemySpeed;
    private final int enemyLife;
    private final float gateSpeed;
    private final int gateSpawnInterval;
    private final float bulletSpeed;
    private final int level;

    public LevelConfig(int level, int enemyCount, float enemySpeed, int enemyLife, float gateSpeed, int gateSpawnInterval, float bulletSpeed) {
        this.level = level;
        this.enemyCount = enemyCount;
        this.enemySpeed = enemySpeed;
        this.enemyLife = enemyLife;
        this.gateSpeed = gateSpeed;
        this.gateSpawnInterval = gateSpawnInterval;
        this.bulletSpeed = bulletSpeed;
    }

    public int getEnemyCount() { return enemyCount; }
    public float getEnemySpeed() { return enemySpeed; }
    public int getEnemyLife() { return enemyLife; }
    public float getGateSpeed() { return gateSpeed; }
    public int getGateSpawnInterval() { return gateSpawnInterval; }
    public float getBulletSpeed() { return bulletSpeed; }
    public int getLevel() { return level; }
} 