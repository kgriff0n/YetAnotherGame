package dev.kgriffon.simplegame.score;

public class ScoreEntry {

    private final int id;
    private final String username;
    private int score;

    public ScoreEntry(int id, String username) {
        this.id = id;
        this.username = username;
        this.score = 0;
    }

    public ScoreEntry(int id, String username, int score) {
        this.id = id;
        this.username = username;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
