package org.homework.model;

public class QuizState {
    private String currentTopic;
    private boolean waitingForAnswer;
    private int correctCount;

    public QuizState() {
    }

    public String getCurrentTopic() {
        return currentTopic;
    }

    public void setCurrentTopic(String currentTopic) {
        this.currentTopic = currentTopic;
    }

    public boolean isWaitingForAnswer() {
        return waitingForAnswer;
    }

    public void setWaitingForAnswer(boolean waitingForAnswer) {
        this.waitingForAnswer = waitingForAnswer;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void incrementCorrect() {
        this.correctCount++;
    }
}
