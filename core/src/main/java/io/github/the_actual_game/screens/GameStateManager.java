package io.github.the_actual_game.screens;

public class GameStateManager {
    private enum GameState { PLAYING, RESULT }
    private GameState gameState = GameState.PLAYING;
    private boolean gameOver = false;

    public boolean isPlaying() {
        return gameState == GameState.PLAYING && !gameOver;
    }

    public boolean isResult() {
        return gameState == GameState.RESULT;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean value) {
        this.gameOver = value;
    }

    public void setResult() {
        this.gameState = GameState.RESULT;
        this.gameOver = false;
    }

    public void reset() {
        this.gameState = GameState.PLAYING;
        this.gameOver = false;
    }

    public static boolean areAllEnemiesDead(io.github.the_actual_game.entities.EnemyManager enemyManager) {
        for (io.github.the_actual_game.entities.Enemy enemy : enemyManager.getEnemies()) {
            if (enemy.isAlive()) return false;
        }
        return true;
    }

    public static int loadHighScore(String scoreFile) {
        java.io.File file = new java.io.File(scoreFile);
        if (!file.exists()) return 0;
        try (java.util.Scanner scanner = new java.util.Scanner(file)) {
            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void saveHighScore(String scoreFile, int score) {
        try (java.io.FileWriter writer = new java.io.FileWriter(scoreFile)) {
            writer.write(Integer.toString(score));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
} 