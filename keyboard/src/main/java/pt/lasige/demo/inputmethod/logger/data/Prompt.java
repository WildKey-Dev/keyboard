package pt.lasige.demo.inputmethod.logger.data;

import java.util.ArrayList;

public class Prompt {
    String type;
    String subType;
    int duration;
    int maxScale;
    String promptId;
    String question;
    String higherBound;
    String lowerBound;
    String orientation;
    ArrayList<String> questions;
    ArrayList<String> phrases;
    ArrayList<String> scaleSteps;
    ArrayList<String> options;
    TimeFrame tf;

    public Prompt() {
    }

    public String getPromptId() {
        return promptId;
    }

    public void setPromptId(String promptId) {
        this.promptId = promptId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public int getScale() {
        return scaleSteps.size();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ArrayList<String> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<String> questions) {
        this.questions = questions;
    }

    public ArrayList<String> getPhrases() {
        return phrases;
    }

    public void setPhrases(ArrayList<String> phrases) {
        this.phrases = phrases;
    }

    public ArrayList<String> getScaleSteps() {
        return scaleSteps;
    }

    public void setScaleSteps(ArrayList<String> scaleSteps) {
        this.scaleSteps = scaleSteps;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public int getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(int maxScale) {
        this.maxScale = maxScale;
    }

    public String getHigherBound() {
        return higherBound;
    }

    public void setHigherBound(String higherBound) {
        this.higherBound = higherBound;
    }

    public String getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(String lowerBound) {
        this.lowerBound = lowerBound;
    }

    public TimeFrame getTimeFrame() {
        return tf;
    }

    public void setTimeFrame(TimeFrame tf) {
        this.tf = tf;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    @Override
    public String toString() {
        return "Prompt{" +
                "type='" + type + '\'' +
                ", subType='" + subType + '\'' +
                ", duration=" + duration +
                ", maxScale=" + maxScale +
                ", promptId='" + promptId + '\'' +
                ", question='" + question + '\'' +
                ", higherBound='" + higherBound + '\'' +
                ", lowerBound='" + lowerBound + '\'' +
                ", questions=" + questions +
                ", phrases=" + phrases +
                ", scaleSteps=" + scaleSteps +
                ", options=" + options +
                '}';
    }
}
