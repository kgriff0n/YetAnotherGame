package dev.kgriffon.simplegame.score;

import dev.kgriffon.simplegame.network.packet.s2c.Scoreboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScoreManager {

    private final List<ScoreEntry> scores;

    public ScoreManager() {
        this.scores = new ArrayList<>();
    }

    public void add(int id, String username) {
        scores.add(new ScoreEntry(id, username));
        sort();
    }

    public void update(int id, int newScore) {
        for (ScoreEntry score : scores) {
            if (score.getId() == id) score.setScore(newScore);
        }
        sort();
    }

    public void remove(int id) {
        scores.removeIf(entry -> entry.getId() == id);
    }

    public int getScore(int id) {
        for (ScoreEntry score : scores) {
            if (score.getId() == id) return score.getScore();
        }
        return 0;
    }

    public Scoreboard createPacket() {
        int[] ids = new int[scores.size()];
        String[] usernames = new String[scores.size()];
        int[] scoresArray = new int[scores.size()];

        for (int i = 0; i < scores.size(); i++) {
            ScoreEntry entry = scores.get(i);
            ids[i] = entry.getId();
            usernames[i] = entry.getUsername();
            scoresArray[i] = entry.getScore();
        }

        return new Scoreboard(ids, usernames, scoresArray);
    }

    private void sort() {
        scores.sort((a, b) -> b.getScore() - a.getScore());
    }
}
