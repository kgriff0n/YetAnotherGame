package dev.kgriffon.simplegame.network.packet.s2c;

public class Scoreboard {

    private int[] ids;
    private String[] usernames;
    private int[] scores;

    public Scoreboard() {
    }

    public Scoreboard(int[] ids, String[] usernames, int[] scores) {
        this.ids = ids;
        this.usernames = usernames;
        this.scores = scores;
    }

    public int[] getIds() {
        return ids;
    }

    public String[] getUsernames() {
        return usernames;
    }

    public int[] getScores() {
        return scores;
    }
}
