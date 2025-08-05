package com.example.dissertation_tester.GameLogic;

public class GameData {
    private int numberCorrectAnswers;
    private String badge;

    // Required empty constructor for Firebase
    public GameData() {}

    public GameData(int numberCorrectAnswers, String badge) {
        this.numberCorrectAnswers = numberCorrectAnswers;
        this.badge = badge;
    }

    public int getNumberOfAnswers() {
        return numberCorrectAnswers;
    }

    public void setNumberOfAnswers(int numberCorrectAnswers) {
        this.numberCorrectAnswers = numberCorrectAnswers;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }
}
