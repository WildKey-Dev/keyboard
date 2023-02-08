package pt.lasige.ideafast.study.inputmethod.study.questionnaire.data;

import java.util.ArrayList;

public class QuestionDataHolder {
    String studyID;
    String questionnaireID;
    String questionID;
    String response;
    ArrayList<String> responses;
    int scale;
    long timeSpent;
    long start;
    long end;

    public QuestionDataHolder(String studyID, String questionID, String questionnaireID, String response, long start, long end, long timeSpent) {
        this.studyID = studyID;
        this.questionnaireID = questionnaireID;
        this.questionID = questionID;
        this.response = response;
        this.timeSpent = timeSpent;
        this.start = start;
        this.end = end;
    }

    public QuestionDataHolder(String studyID, String questionID, String questionnaireID, ArrayList<String> responses, long start, long end, long timeSpent) {
        this.studyID = studyID;
        this.questionnaireID = questionnaireID;
        this.questionID = questionID;
        this.responses = responses;
        this.timeSpent = timeSpent;
        this.start = start;
        this.end = end;
    }

    public QuestionDataHolder(String studyID, String questionID, String questionnaireID, int scale, long start, long end, long timeSpent) {
        this.studyID = studyID;
        this.questionnaireID = questionnaireID;
        this.questionID = questionID;
        this.scale = scale;
        this.timeSpent = timeSpent;
        this.start = start;
        this.end = end;
    }

    public QuestionDataHolder(String studyID, String questionID, String questionnaireID, int scale, String response, long start, long end, long timeSpent) {
        this.studyID = studyID;
        this.questionnaireID = questionnaireID;
        this.questionID = questionID;
        this.scale = scale;
        this.response = response;
        this.timeSpent = timeSpent;
        this.start = start;
        this.end = end;
    }

    public String getStudyID() {
        return studyID;
    }

    public String getQuestionnaireID() {
        return questionnaireID;
    }

    public String getQuestionID() {
        return questionID;
    }

    public String getResponse() {
        return response;
    }

    public ArrayList<String> getResponses() {
        return responses;
    }

    public int getScale() {
        return scale;
    }

    public long getTimeSpent() {
        return timeSpent;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }
}
