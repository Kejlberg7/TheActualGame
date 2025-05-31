package io.github.the_actual_game.screens;

import java.util.*;

public class GameStateManager {
    private enum GameState { PLAYING, ENTER_NAME, RESULT }
    private GameState gameState = GameState.PLAYING;
    private boolean gameOver = false;

    public boolean isPlaying() {
        return gameState == GameState.PLAYING && !gameOver;
    }

    public boolean isEnterName() {
        return gameState == GameState.ENTER_NAME;
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

    public void setEnterName() {
        this.gameState = GameState.ENTER_NAME;
        this.gameOver = false;
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

    public static class ScoreEntry {
        public final String name;
        public final int score;
        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    public static void saveScoreWithName(String scoreFile, String name, int score) {
        List<ScoreEntry> scores = loadTopScores(scoreFile);
        scores.add(new ScoreEntry(name, score));
        scores.sort((a, b) -> Integer.compare(b.score, a.score));
        if (scores.size() > 10) scores = scores.subList(0, 10);
        try (java.io.FileWriter writer = new java.io.FileWriter(scoreFile)) {
            for (ScoreEntry entry : scores) {
                writer.write(entry.name + ":" + entry.score + "\n");
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ScoreEntry> loadTopScores(String scoreFile) {
        List<ScoreEntry> scores = new ArrayList<>();
        java.io.File file = new java.io.File(scoreFile);
        if (!file.exists()) return scores;
        try (java.util.Scanner scanner = new java.util.Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    try {
                        scores.add(new ScoreEntry(parts[0], Integer.parseInt(parts[1])));
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        scores.sort((a, b) -> Integer.compare(b.score, a.score));
        if (scores.size() > 10) scores = scores.subList(0, 10);
        return scores;
    }

    public static String handleNameInput(String currentName) {
        String name = currentName;
        for (int i = 0; i < 26; i++) {
            char c = (char)('A' + i);
            if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.A + i)) {
                name += c;
            }
        }
        for (int i = 0; i < 10; i++) {
            if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_0 + i)) {
                name += (char)('0' + i);
            }
        }
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.BACKSPACE) && name.length() > 0) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }

    public static boolean isNameEntryComplete() {
        return com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER);
    }
}
