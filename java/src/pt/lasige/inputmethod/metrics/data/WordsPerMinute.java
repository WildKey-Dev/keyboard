package pt.lasige.inputmethod.metrics.data;

import android.util.Log;

public class WordsPerMinute extends Metric {
    public WordsPerMinute() {
        super();
    }

    public double execute(String text, double time) {

        int textLen = text.length();

        return (double) (textLen) / 5 * (60F / (time / 1000F)) ; //a "word" is any five characters
    }
}