package pt.lasige.cns.study.inputmethod.metrics.data;

public class WordsPerMinute extends Metric {
    public WordsPerMinute() {
        super();
    }

    public double execute(String text, double time) {

        int textLen = text.length();
        if(textLen < 5)
            return -1; //we want to have at least 5 chars to calculate this metric
        else
            return (double) (textLen) / 5 * (60F / (time / 1000F)) ; //a "word" is any five characters
    }
}