package pt.lasige.cns.study.inputmethod.metrics.data;


import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.Input;

public class Metric {


    boolean ignoreCase = true;

    public Metric() {}

    /**
     * In information theory, linguistics and computer science, the Levenshtein distance is a string metric for
     * measuring the difference between two sequences. Informally, the Levenshtein distance between two words is the
     * minimum number of single-character edits required to change one word into the other.
     * @param a string
     * @param b string
     * @return the Levenshtein distance
     */
    public int levenshteinDistance(String a, String b){
        if (a.length() == 0) return b.length();
        if (b.length() == 0) return a.length();
        String tmp;
        int prev, val;
        // swap to save some memory O(min(a,b)) instead of O(a)
        if (a.length() > b.length()) {
            tmp = a;
            a = b;
            b = tmp;
        }

        int[] row = new int[a.length() + 1];
        // init the row
        for (int i = 0; i <= a.length(); i++) {
            row[i] = i;
        }

        // fill in the rest
        for (int i = 1; i <= b.length(); i++) {
            prev = i;
            for (int j = 1; j <= a.length(); j++) {
                if (b.charAt(i - 1) == a.charAt(j - 1)) {
                    val = row[j - 1]; // match
                } else {
                    val = Math.min(row[j - 1] + 1, // substitution
                            Math.min(prev + 1, // insertion
                                    row[j] + 1)); // deletion
                }
                row[j - 1] = prev;
                prev = val;
            }
            row[a.length()] = prev;
        }
        return row[a.length()];
    }

    /**
     * Incorrect and Not Fixed (INF)
     * @param presentString
     * @param transcribe
     * @return
     */
    public int getInf(String presentString, String transcribe) {
        if (ignoreCase){
            presentString = presentString.toLowerCase();
            transcribe = transcribe.toLowerCase();
        }

        return levenshteinDistance(presentString, transcribe);
    }

    /**
     * Correct (C)
     * @param presentString
     * @param transcribe
     * @return
     */
    public int getC(int inf, String presentString, String transcribe) {
        if (ignoreCase){
            presentString = presentString.toLowerCase();
            transcribe = transcribe.toLowerCase();
        }
        return Math.max(presentString.length(), transcribe.length()) - inf;
    }

    /**
     * Incorrect and Fixed (IF)
     * @param inputWord
     * @return
     */
    public int getIf(Input inputWord){

        int incorrectAndFixed = 0;

        for (int i = 0; i < inputWord.getActions().size(); i++){
            if ((int) inputWord.getActions().get(i).t1 == Input.ACTION_DELETE) {
                incorrectAndFixed++;
            }
        }

        return incorrectAndFixed;
    }


}