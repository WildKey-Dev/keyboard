package pt.lasige.inputmethod.logger;

import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.HashMap;

import pt.lasige.inputmethod.keyboard.Key;
import pt.lasige.inputmethod.latin.SuggestedWords;
import pt.lasige.inputmethod.latin.common.Constants;
import pt.lasige.inputmethod.logger.data.CursorChange;
import pt.lasige.inputmethod.logger.data.KeyEventData;
import pt.lasige.inputmethod.logger.data.MotionEventData;
import pt.lasige.inputmethod.logger.data.StudyConstants;
import pt.lasige.inputmethod.metrics.MetricsController;
import pt.lasige.inputmethod.metrics.textentry.datastructures.Input;
import pt.lasige.inputmethod.metrics.textentry.datastructures.Tuple;

import static pt.lasige.inputmethod.latin.common.Constants.CODE_DELETE;
import static pt.lasige.inputmethod.latin.common.Constants.CODE_ENTER;
import static pt.lasige.inputmethod.latin.common.Constants.CODE_SPACE;

public class Logger {

    private String inputBuffer = "", wordInputBuffer = "", wordTranscribe = "";
    private final ArrayList<Long> flightTimeBuffer = new ArrayList<>();
    private final ArrayList<Long> holdTimeBuffer = new ArrayList<>();
    private final ArrayList<Long> timePerWord = new ArrayList<>();
    private final ArrayList<Long> timeOutsideCurrentPhrase = new ArrayList<>();
    private final ArrayList<Tuple> touchMajorMinor = new ArrayList<>();
    private final ArrayList<Tuple> touchOffset = new ArrayList<>();
    private ArrayList<Tuple> motion = new ArrayList<>();
    private ArrayList<Tuple> keys = new ArrayList<>();
    private final ArrayList<Tuple> actions = new ArrayList<>();
    private final ArrayList<Long> inputTimeStamps = new ArrayList<>();
    private final ArrayList<SuggestedWords> suggestions = new ArrayList<>();
    private final HashMap<String, ArrayList<Tuple>> mSuggestedWordsHashMap = new HashMap<>();
    private long downTS, wordStartTS, wordFinishTS, outPhraseTS = -1;
    private float tMajor = -1, tMinor = -1;
    private boolean isEventRunning = false, lastInputWasDelete = false, lastInputWasSpace = false,
            lastSpaceWasProgrammatic = false, willAutoCorrect = false, autoCorrected = false,
            lastInputWasDeletable = false;
    private final boolean onlyCountFirstSubstitutionForEachWord = false;
    private boolean isCursorOnEnd = true, ignoreInput = false, wasEditTextEmpty = true;
    private int numbers = 0, specialChars = 0, suggestionsSelected = 0, autoCorrection = 0,
            voiceInput = 0, cursorMoves = 0, compositionStartIndex = 0, discardedChars = 0;
    private final ArrayList<CursorChange> cursorChanges = new ArrayList<>();
    private SuggestedWords currentSuggestionList;

    public Logger() {}

    /**
     * Getters and setters
     */

    public void setAutoCorrected(boolean autoCorrected) {
        this.autoCorrected = autoCorrected;
    }

    public void setLastInputWasDeletable(boolean lastInputWasDeletable) {
        this.lastInputWasDeletable = lastInputWasDeletable;
    }

    public void setCompositionStartIndex(int compositionStartIndex) {

        //messaging apps hitting the send button
        if(inputBuffer.length() != 0 && compositionStartIndex == 0){
            MetricsController.getInstance().onKeyboardHide();
            return;
        }

        if (compositionStartIndex > 0)
            this.compositionStartIndex = compositionStartIndex;

    }

    public ArrayList<Tuple> getMotion() {
        return motion;
    }

    public ArrayList<Tuple> getKeys() {
        return keys;
    }

    public void setKeys(ArrayList<Tuple> keys) {

        if(!LoggerController.getInstance().shouldILog())
            return;

        this.keys = keys;
    }

    public ArrayList<Long> getTimeOutsideCurrentPhrase() {
        return timeOutsideCurrentPhrase;
    }

    public long getOutPhraseTS() {
        return outPhraseTS;
    }

    public ArrayList<Long> getHoldTimeBuffer() {
        return holdTimeBuffer;
    }

    public ArrayList<Tuple> getTouchMajorMinor() {
        return touchMajorMinor;
    }

    public ArrayList<Tuple> getTouchOffset() {
        return touchOffset;
    }

    public void setEventRunning(boolean eventRunning) {
        isEventRunning = eventRunning;
    }

    public ArrayList<Long> getFlightTimeBuffer() {
        return flightTimeBuffer;
    }

    public ArrayList<Tuple> getActions() {
        return actions;
    }

    public void trimActionsArray(){
        if(actions.size() > 0)
            if((int) actions.get(actions.size()-1).t1 == Input.ACTION_SPACE)
                actions.remove(actions.size()-1);
    }

    public String getTranscribe() {
        return substituteDeletedChars(getInputBuffer(false));
    }

    public ArrayList<Long> getInputTimeStamps() {
        return inputTimeStamps;
    }

    public String getOriginalInputBuffer(){
        return  inputBuffer;
    }

    public int getCursorMoves() {
        return cursorMoves;
    }

    public ArrayList<CursorChange> getCursorChanges() {
        return cursorChanges;
    }

    public int getNumbers() {
        return numbers;
    }

    public void setNumbers(int numbers) {
        this.numbers = numbers;
    }

    public int getSpecialChars() {
        return specialChars;
    }

    public int getSuggestionsSelected() {
        return suggestionsSelected;
    }

    public int getAutoCorrection() {
        return autoCorrection;
    }

    public int getVoiceInput() {
        return voiceInput;
    }

    public int getDiscardedChars() {
        return discardedChars;
    }

    public boolean wasEditTextEmpty() {
        return wasEditTextEmpty;
    }

    public void setWasEditTextEmpty(boolean wasEditTextEmpty) {

        this.wasEditTextEmpty = wasEditTextEmpty;

        if(!this.wasEditTextEmpty) {
            ignoreInput = true;
        }else {
            // on transcription or on composition
            // the keyboard fires a wrong wasEditTextEmpty
            // so we allow for a second one that is correct
            if(MetricsController.getInstance().mode == StudyConstants.TRANSCRIPTION_MODE ||
                    MetricsController.getInstance().mode == StudyConstants.COMPOSITION_MODE){

                ignoreInput = false;
            }
        }
    }

    public ArrayList<Long> getTimePerWord() {
        if(!lastInputWasSpace)
            timePerWord.add(wordFinishTS - wordStartTS);

        return timePerWord;
    }

    /**
     * Custom getters
     * Consolidate the info before returning it
     */

    public String getInputBuffer(boolean removeSuggestionsFromInputStream) {

        try{
            //remove space on the end if there is one
            trimActionsArray();
            StringBuilder newInputBuffer = new StringBuilder();
            String newWord, oldWord;
            String[] data = splitBuffer(fixBuffer(consolidateBuffer(inputBuffer)));

            for (int i = 0; i < data.length; i++){
                if(data[i].contains("[[")){
                    if(data[i].startsWith("[[")){
                        newInputBuffer.append(data[i].substring(data[i].indexOf("[[") + 2, data[i].indexOf("]]")));
                        actions.add(new Tuple(Input.ACTION_SUBSTITUTION, 0));
                    }else {
                        newWord = data[i].substring(data[i].indexOf("[[") + 2, data[i].indexOf("]]"));

                        if(data[i].charAt(data[i].indexOf("[[")-1) == '<' && data[i].charAt(data[i].indexOf("[[")-2) == '<'){
                            if(i > 0)
                                oldWord = data[i-1];
                            else
                                oldWord = "";
                        }else {
                            oldWord = substituteDeletedSuggestions(substituteDeletedChars(data[i]));
                        }
                        if(newWord.equals(oldWord)) {
                            newInputBuffer.append(data[i]);
                        }else {
                            //Special cases
                            if(newWord.startsWith(oldWord)){
                                newInputBuffer.append(newWord);
                                actions.add(new Tuple(Input.ACTION_INSERT, 0));
                            }else if(newWord.endsWith(oldWord)){
                                newInputBuffer.append(newWord);
                                actions.add(new Tuple(Input.ACTION_INSERT, 0));
                            }else if(oldWord.startsWith(newWord)){
                                newInputBuffer.append(oldWord);
                                for (int j = 0; j < oldWord.length()-newWord.length(); j++) {
                                    newInputBuffer.append("<<");
                                }
                                actions.add(new Tuple(Input.ACTION_SUBSTITUTION, 0));
                            }else if(oldWord.endsWith(newWord)){
                                newInputBuffer.append(oldWord.substring(0, oldWord.indexOf(newWord)));
                                for (int j = 0; j < oldWord.length()-newWord.length(); j++) {
                                    newInputBuffer.append("<<");
                                }
                                newInputBuffer.append(newWord);
                                actions.add(new Tuple(Input.ACTION_SUBSTITUTION, 0));
                            }else{

                                //index on the replaced word of the first different char (compared with the new word)
                                //counting from left to right
                                int p1 = -1;
                                //index on the replaced word of the first different char (compared with the new word)
                                //counting from right to left
                                int p2 = -1;
                                //index on the new word of the first different char (compared with the replaced)
                                //counting from right to left
                                int p2_2 = -1;

                                int lengthDifference = Math.abs(newWord.length() - oldWord.length());

                                for (int j = 0; j < oldWord.length(); j++){
                                    if(oldWord.charAt(j) != newWord.charAt(j)){
                                        p1 = j;
                                        break;
                                    }
                                }
                                int aux;
                                for (int j = oldWord.length()-1; j >= 0; j--){
                                    aux = oldWord.length() < newWord.length() ? j + lengthDifference : j - lengthDifference;
                                    if(oldWord.charAt(j) != newWord.charAt(aux)){
                                        p2 = j;
                                        p2_2 = aux;
                                        break;
                                    }
                                }

                                int numberOfDeletes = Math.abs(p1 - p2) + 1;
                                int charsDeleted = Math.abs(p2 - p1);

                                if(oldWord.length() < newWord.length()){
                                    if(lengthDifference == charsDeleted){
                                        if(p1 > 0 &&
                                                p1 < p2 &&
                                                p1 != p2) {
                                            newInputBuffer.append(oldWord);
                                            for (int j = 0; j < p1; j++) {
                                                newInputBuffer.append("<<");
                                            }
                                            newInputBuffer.append(newWord.substring(p1));
                                            actions.add(new Tuple(Input.ACTION_SUBSTITUTION, 0));
                                        }else{
                                            newInputBuffer.append(newWord);
                                            actions.add(new Tuple(Input.ACTION_INSERT, 0));
                                        }

                                    }else{
                                        newInputBuffer.append(oldWord.substring(0, p2 + 1));//+1 bc we want to start erasing on the last different char
                                        for (int j = 0; j < numberOfDeletes; j++) {
                                            newInputBuffer.append("<<");
                                        }
                                        newInputBuffer.append(newWord.substring(p1));
                                        actions.add(new Tuple(Input.ACTION_SUBSTITUTION, 0));
                                    }

                                }else if(oldWord.length() > newWord.length()){

                                    if(p2-p1 < 0) {
                                        newInputBuffer.append(oldWord.substring(0, p1));
                                        for (int j = 0; j < oldWord.length()-newWord.length(); j++) {
                                            newInputBuffer.append("<<");
                                        }
                                        newInputBuffer.append(oldWord.substring(p1));
                                        actions.add(new Tuple(Input.ACTION_SUBSTITUTION, 0));
                                    }else {
                                        newInputBuffer.append(oldWord.substring(0, p2 + 1));//+1 bc we want to start erasing on the last different char
                                        for (int j = 0; j < numberOfDeletes; j++) {
                                            newInputBuffer.append("<<");
                                        }

                                        newInputBuffer.append(newWord.substring(p2_2));
                                        actions.add(new Tuple(Input.ACTION_SUBSTITUTION, 0));

                                    }
                                }else{ //word.length() == oldWord.length()
//                        if(p1 != oldWord.length()){
                                    newInputBuffer.append(oldWord.substring(0, p2 + 1));//+1 bc we want to start erasing on the last different char
                                    for (int j = 0; j < numberOfDeletes; j++) {
                                        newInputBuffer.append("<<");
                                    }
                                    newInputBuffer.append(newWord.substring(p2_2 - charsDeleted));
                                    actions.add(new Tuple(Input.ACTION_SUBSTITUTION, 0));
//                        }
                                }
                            }
                        }
                    }
                }else {
                    newInputBuffer.append(data[i]);
                }
                newInputBuffer.append(" ");
            }
            if(removeSuggestionsFromInputStream)
                return substituteDeletedSuggestions(newInputBuffer.toString().trim());
            else
                return newInputBuffer.toString().trim();

        }catch (Exception e){
            e.printStackTrace();
            DataBaseFacade.getInstance().write("error", "something went wrong with input buffer calculation", "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/implicit_mode/"+String.valueOf(System.currentTimeMillis())+"/phrases/"+0+"/");
            return "";
        }
    }

    public String getTargetPhrase(){
        String transcribe = getTranscribe();
        StringBuilder sb = new StringBuilder();

        for (String s: transcribe.split(" ")){
            if(mSuggestedWordsHashMap.containsKey(s)){
                SuggestedWords list = getClosestSuggestion(sb.toString().length(), mSuggestedWordsHashMap.get(s));
                if(list.mTypedWordValid){
                    sb.append(list.mTypedWordInfo.mWord);
                }else {
                    if(list.mSuggestedWordInfoList.size() > 1) {
                        sb.append(list.mSuggestedWordInfoList.get(1).mWord);
                    }else{
                        if(list.mSuggestedWordInfoList.size() > 0) {
                            sb.append(list.mSuggestedWordInfoList.get(0).mWord);
                        }else {
                            sb.append(s);
                        }
                    }
                }
            }else {
                sb.append(s);
            }
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    private SuggestedWords getClosestSuggestion(int length, ArrayList<Tuple> tuples) {
        int distance = Integer.MAX_VALUE;
        SuggestedWords list = null;
        for (Tuple t: tuples){
            if(Math.abs((int) t.t1 - length) < distance){
                distance = Math.abs((int) t.t1 - length);
                list = (SuggestedWords) t.t2;
            }
        }

        return list;
    }


    /**
     * INC functions
     * Hold the number of actions of each type
     */

    public void incNumbers() {

        if(!LoggerController.getInstance().shouldILog())
            return;

        this.numbers++;
    }

    public void incSpecialChars() {

        if(!LoggerController.getInstance().shouldILog())
            return;

        this.specialChars++;
    }

    public void incSuggestionsSelected() {

        if(!LoggerController.getInstance().shouldILog())
            return;

        this.suggestionsSelected++;
    }

    public void incAutoCorrection() {

        if(!LoggerController.getInstance().shouldILog())
            return;

        this.autoCorrection++;
    }

    public void incVoiceInput() {

        if(!LoggerController.getInstance().shouldILog())
            return;

        this.voiceInput++;
    }

    public void incCursorMoves() {

        if(!LoggerController.getInstance().shouldILog())
            return;

        this.cursorMoves++;

    }

    /**
     * Points of entry
     * Where info comes in and is written to buffers
     */

    public void addSuggestion(String word, boolean addSpace, boolean wasClicked) {

        if (ignoreInput)
            return;

        if (wasClicked)
            incSuggestionsSelected();

        addToInputBuffer("[[" + word + "]]");
        keys.add(new Tuple("[[" + word + "]]"+"[[autoComplete=" + !wasClicked + "]]", System.currentTimeMillis()));

        actions.add(new Tuple(Input.ACTION_SUGGESTION, System.currentTimeMillis()));

        if(addSpace)
            writeToBuffer(" ", true);
    }

    public void addSuggestionList(SuggestedWords list){

        if(!LoggerController.getInstance().shouldILog() || ignoreInput)
            return;

        currentSuggestionList = list;

        willAutoCorrect = list.mWillAutoCorrect;
        suggestions.add(list);
    }

    public void writeToBuffer(String character, boolean logAction){

        if(!LoggerController.getInstance().shouldILog() || ignoreInput)
            return;

//        if(character.equals("<<") && lastInputWasDeletable){
        if(character.equals("<<") && getTranscribe().length() > 0){

            lastInputWasSpace = false;
            lastSpaceWasProgrammatic = false;
            lastInputWasDelete = true;
            if(logAction)
                actions.add(new Tuple(Input.ACTION_DELETE, 0));
            addToInputBuffer("<<");

            wordInputBuffer = wordInputBuffer + "<<";
            if(!wordTranscribe.isEmpty())
                wordTranscribe = wordTranscribe.substring(0, wordTranscribe.length()-1);

            setAutoCorrected(false);
            setLastInputWasDeletable(false);

        }else if(character.equals(" ")){
            addToInputBuffer(" ");
            setLastInputWasDeletable(true);

            if(logAction)
                actions.add(new Tuple(Input.ACTION_SPACE, 0));

            timePerWord.add(wordFinishTS - wordStartTS);
            wordInputBuffer = "";
            wordTranscribe = "";
            lastInputWasSpace = true;
            lastSpaceWasProgrammatic = true;
            lastInputWasDelete = false;

            if (willAutoCorrect) {
                incAutoCorrection();
                setAutoCorrected(true);
            }else
                setAutoCorrected(false);

        }else{
            if(lastInputWasSpace || inputBuffer.isEmpty())
                wordStartTS = System.currentTimeMillis();

            addToInputBuffer(character);

            wordTranscribe = wordTranscribe + character;
            wordInputBuffer = wordInputBuffer + character;
            if(logAction) {
                actions.add(new Tuple(Input.ACTION_INSERT, 0));
            }
            if(lastInputWasSpace)
                wordStartTS = System.currentTimeMillis();

            wordFinishTS = System.currentTimeMillis();

            lastInputWasSpace = false;
            lastSpaceWasProgrammatic = false;
            lastInputWasDelete = false;
            setAutoCorrected(false);
            setLastInputWasDeletable(true);
        }
    }

    public void writeToBuffer(Key key, int x, int y, long eventTime){

        if(!LoggerController.getInstance().shouldILog() || ignoreInput)
            return;


//        if(key.getCode() == CODE_DELETE && lastInputWasDeletable){
        if(key.getCode() == CODE_DELETE && getTranscribe().length() > 0){

            lastInputWasSpace = false;
            lastSpaceWasProgrammatic = false;
            lastInputWasDelete = true;
            actions.add(new Tuple(Input.ACTION_DELETE, 0));
            addToInputBuffer("<<");

            wordInputBuffer = wordInputBuffer + "<<";
            motion.add(new Tuple("DELETE", System.currentTimeMillis()));
            keys.add(new Tuple("DELETE", System.currentTimeMillis()));
            touchMajorMinor.add(new Tuple("DELETE", System.currentTimeMillis()));

            if(!wordTranscribe.isEmpty())
                wordTranscribe = wordTranscribe.substring(0, wordTranscribe.length()-1);

            if(autoCorrected){
                removeLastSuggestionFromInput();
            }

            setAutoCorrected(false);
            setLastInputWasDeletable(false);

            addToFlightTime(eventTime, key.getCode() == CODE_DELETE);
            addTouchOffset(key, x, y, eventTime, key.getCode() == CODE_DELETE);

        }else if(key.getCode() == CODE_SPACE || (key.getCode() == CODE_ENTER && inputBuffer.length() > 0)){

            if(!lastSpaceWasProgrammatic){
                if (willAutoCorrect) {
                    addSuggestion(suggestions.get(suggestions.size()-1).mSuggestedWordInfoList.get(1).mWord, false, false);
                    incAutoCorrection();
                    setAutoCorrected(true);
                }else
                    setAutoCorrected(false);

                if(currentSuggestionList != null && currentSuggestionList.mSuggestedWordInfoList.size() > 0){
                    if (!mSuggestedWordsHashMap.containsKey(currentSuggestionList.mSuggestedWordInfoList.get(0).mWord))
                        mSuggestedWordsHashMap.put(currentSuggestionList.mSuggestedWordInfoList.get(0).mWord, new ArrayList<>());
                    mSuggestedWordsHashMap.get(currentSuggestionList.mSuggestedWordInfoList.get(0).mWord).add(new Tuple(getTranscribe().length(), currentSuggestionList));
                }


                addToInputBuffer(" ");

                timePerWord.add(wordFinishTS - wordStartTS);
            }
            actions.add(new Tuple(Input.ACTION_SPACE, 0));

            wordInputBuffer = "";
            wordTranscribe = "";
            lastInputWasSpace = true;
            lastSpaceWasProgrammatic = false;
            lastInputWasDelete = false;
            setLastInputWasDeletable(true);

            addToFlightTime(eventTime, key.getCode() == CODE_DELETE);
            addTouchOffset(key, x, y, eventTime, key.getCode() == CODE_DELETE);


        }else if(Constants.isLetterCode(key.getCode())){

            if(lastInputWasSpace || inputBuffer.isEmpty())
                wordStartTS = System.currentTimeMillis();

            if(key.getCode() >= 48 &&  key.getCode() <= 57){
                incNumbers();
                setLastInputWasDeletable(false);
            }else if((key.getCode() == 45) || (key.getCode() >= 65 &&  key.getCode() <= 90) || (key.getCode() >= 97 &&  key.getCode() <= 122)){
                addToInputBuffer(Constants.printableCode(key.getCode()));

                wordTranscribe = wordTranscribe + Constants.printableCode(key.getCode());
                wordInputBuffer = wordInputBuffer + Constants.printableCode(key.getCode());
                actions.add(new Tuple(Input.ACTION_INSERT, 0));
                setLastInputWasDeletable(true);
            }else {
                incSpecialChars();
                setLastInputWasDeletable(false);
            }
            if(lastInputWasSpace)
                wordStartTS = System.currentTimeMillis();

            wordFinishTS = System.currentTimeMillis();

            lastInputWasSpace = false;
            lastSpaceWasProgrammatic = false;
            lastInputWasDelete = false;
            setAutoCorrected(false);

            addToFlightTime(eventTime, key.getCode() == CODE_DELETE);
            addTouchOffset(key, x, y, eventTime, key.getCode() == CODE_DELETE);
        }
    }

    public void logMotionEvent(MotionEvent me) {

        if(MetricsController.getInstance().mode == StudyConstants.IMPLICIT_MODE)
            return;

        if(!LoggerController.getInstance().shouldILog() || ignoreInput)
            return;

        String action = "null";
        switch (me.getAction()) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                action = "ACTION_POINTER_DOWN";
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                break;
            case MotionEvent.ACTION_POINTER_UP:
                action = "ACTION_POINTER_UP";
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                break;
        }
        MotionEventData data = new MotionEventData(
                me.getAction(),
                action,
                me.getActionIndex(),
                me.getActionMasked(),
                me.getAxisValue(MotionEvent.AXIS_X),
                me.getAxisValue(MotionEvent.AXIS_Y),
                me.getDownTime(),
                me.getPressure(),
                me.getXPrecision(),
                me.getYPrecision(),
                me.getX(),
                me.getY(),
                me.getRawX(),
                me.getRawX(),
                me.getSize(),
                me.getToolMajor(),
                me.getToolMinor(),
                me.getTouchMajor(),
                me.getTouchMinor());

        motion.add(new Tuple(data, System.currentTimeMillis()));

    }

    public void logKeyPressEvent(Key primaryKey, int x, int y){

        if(MetricsController.getInstance().mode == StudyConstants.IMPLICIT_MODE)
            return;

        if(!LoggerController.getInstance().shouldILog() || ignoreInput)
            return;

        if(primaryKey.getCode() == CODE_ENTER && inputBuffer.length() == 0)
            return;

        KeyEventData data = new KeyEventData(
                primaryKey.getCode(),
                primaryKey.getAltCode(),
                Constants.printableCode(primaryKey.getCode()),
                x,
                y
        );
        keys.add(new Tuple(data, System.currentTimeMillis()));
    }

    /**
     * UTILS
     **/
    private String consolidateBuffer(String inputBuffer) {

        StringBuilder sb = new StringBuilder(inputBuffer);
        try {
            for (CursorChange cc : cursorChanges) {
                if (!cc.getInput().isEmpty()) {
                    sb.insert(cc.getNewSelStart() - compositionStartIndex, cc.getInput());
                }
            }

            return sb.toString();
        }catch (Exception e){
            return sb.toString();
        }
    }

    private String[] splitBuffer(String inputBuffer){
        String[] splitBuffer = inputBuffer.split(" ");
        ArrayList<String> dataAux = new ArrayList<>();
        for (int i = 0; i < splitBuffer.length; i++){
            if(splitBuffer[i].contains("[[") && !splitBuffer[i].contains("]]")){
                dataAux.add(splitBuffer[i] + " " + splitBuffer[i+1]);
                i++;
            }else {
                dataAux.add(splitBuffer[i]);
            }
        }
        return dataAux.toArray(new String[0]);
    }

    private String fixBuffer(String inputBuffer) {

        if(inputBuffer.contains("<<[[")){
            int suggestionsOnARow = 0, lastLetterIndex = 0, startSuggestionIndex = 0, endSuggestionIndex = 0, lastStartSuggestionIndex = 0;
            boolean lastWasDelete = false, onSuggestion = false, lastWasSuggestion = false;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < inputBuffer.length(); i++) {
                if (onSuggestion) {
                    if (inputBuffer.charAt(i) == ']' && inputBuffer.charAt(i - 1) == ']') {
                        onSuggestion = false;
                        endSuggestionIndex = i+1;
                        suggestionsOnARow++;
                    }

                    if(lastWasDelete && !onSuggestion){
                        try{
                            sb.insert(lastLetterIndex, inputBuffer,
                                    startSuggestionIndex,
                                    endSuggestionIndex);
                        }catch (Exception e){
                            DataBaseFacade.getInstance().write("discarded", "error calculating input buffer", "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/implicit_mode/"+String.valueOf(System.currentTimeMillis())+"/phrases/"+0+"/");
                        }

                    }
                } else {
                    if (inputBuffer.charAt(i) == '[' && inputBuffer.charAt(i + 1) == '[') {
                        onSuggestion = true;
                        startSuggestionIndex = i;
                    } else if (inputBuffer.charAt(i) != '[' && inputBuffer.charAt(i) != ']' && inputBuffer.charAt(i) != ' ' && inputBuffer.charAt(i) != '<') {
                        lastLetterIndex = i + 1;
                        sb.append(inputBuffer.charAt(i));
                        suggestionsOnARow = 0;
                    }else {
                        sb.append(inputBuffer.charAt(i));
                        lastWasDelete = inputBuffer.charAt(i) == '<';
                    }
                }
            }
            return sb.toString();
        }else {
            return inputBuffer;
        }
    }

    public void removeLastSuggestionFromInput(){
        String bufferClone = inputBuffer;
        try{
            if(inputBuffer.lastIndexOf("[[") != -1)
                inputBuffer = inputBuffer.substring(0, inputBuffer.lastIndexOf("[["));
        }catch (Exception e){
            inputBuffer = bufferClone;
        }
    }

    public void addDownTS(long eventTime) {

        if(!LoggerController.getInstance().shouldILog())
            return;

        downTS = eventTime;
    }

    public void addToFlightTime(long eventTime, boolean wasDelete) {

        if(!LoggerController.getInstance().shouldILog())
            return;

        flightTimeBuffer.add(eventTime);
        holdTimeBuffer.add(eventTime - downTS);

        if(wasDelete)
            holdTimeBuffer.add((long) -1);
    }

    public void addTouchMajorMinor(MotionEvent me){

        if(!LoggerController.getInstance().shouldILog())
            return;

        if(me.getTouchMinor() > tMinor)
            tMinor = me.getTouchMinor();
        if(me.getTouchMajor() > tMajor)
            tMajor = me.getTouchMajor();

        if(me.getAction() == MotionEvent.ACTION_UP){
            touchMajorMinor.add(new Tuple(tMajor, tMinor));
            tMajor = -1;
            tMinor = -1;
        }
    }

    public void addTouchOffset(Key key, int x, int y, long eventTime, boolean wasDelete){

        if(!LoggerController.getInstance().shouldILog())
            return;

        if(key == null){
            touchOffset.add(new Tuple(new Tuple(x, y), eventTime));
        }else {
            int centerX = key.getHitBox().centerX();
            int centerY = key.getHitBox().centerY();
            int offsetX;
            int offsetY;
            if(x < centerX)
                offsetX = centerX - x;
            else
                offsetX = x - centerX;

            if(y < centerY)
                offsetY = centerY - y;
            else
                offsetY = y - centerY;

            touchOffset.add(new Tuple(new Tuple(offsetX, offsetY), eventTime));
        }
    }

    private String substituteDeletedSuggestions(String input){
        StringBuilder transcribe = new StringBuilder();
        try {
            String[] data = splitBuffer(input);
            for (int i = 0; i < data.length; i++){
                if(data[i].contains("[[")){
                    transcribe.append(data[i].substring(0, data[i].indexOf("[[")));
                }else{
                    transcribe.append(data[i]);
                }
                transcribe.append(" ");
            }
        }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            transcribe = new StringBuilder("substituteDeletedSuggestions: something_went_wrong");
        }
        return transcribe.toString().trim();
    }

    private String substituteDeletedChars(String input){
        StringBuilder transcribe = new StringBuilder(input);
        int index = 0;
        try {
            while (index < transcribe.length()) {
                if(transcribe.charAt(index) == '<' && index == 0) {
                    //nothing to delete
                    //clean the two '<<'
                    transcribe.delete(index, index + 2);//end is exclusive
                    index = 0;
                }else if (transcribe.charAt(index) == '<') {
                    transcribe.delete(index - 1, index + 2);//end is exclusive
                    index = 0;
                } else {
                    index++;
                }
            }
        }catch (IndexOutOfBoundsException e) {
            DataBaseFacade.getInstance().write("error", "something went wrong with substitute deleted chars", "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/implicit_mode/"+String.valueOf(System.currentTimeMillis())+"/phrases/"+0+"/");
            transcribe = new StringBuilder("substituteDeletedChars: something_went_wrong");
        }

        return transcribe.toString().trim();
    }

    private void addToInputBuffer(String string){

        if(MetricsController.getInstance().mode == StudyConstants.IMPLICIT_MODE){
            if(discardedChars > 0){
                if(string.contains("[[") && string.contains("]]"))
                    discardedChars += string.length()-4;
                else
                    discardedChars += string.length();
                return;
            }
            if(!isCursorOnEnd) {
                if(string.contains("[[") && string.contains("]]")){
                    discardedChars += string.length()-4;
                    MetricsController.getInstance().runMetricCalculation(LoggerController.getInstance().getLogger(), null, String.valueOf(System.currentTimeMillis()), "implicit_mode", 0);
                }else {
                    inputTimeStamps.add(System.currentTimeMillis());
                    cursorChanges.get(cursorChanges.size() - 1).addToInput(string);
                }
            }else {
                inputTimeStamps.add(System.currentTimeMillis());
                inputBuffer = inputBuffer + string;
            }
        }else {
            if(!isCursorOnEnd) {
                inputTimeStamps.add(System.currentTimeMillis());
                cursorChanges.get(cursorChanges.size() - 1).addToInput(string);
            }else {
                inputTimeStamps.add(System.currentTimeMillis());
                inputBuffer = inputBuffer + string;
            }
        }
    }

    public void addCursorChange(CursorChange cursorChange){

        if(!wasEditTextEmpty) {
            keys.add(new Tuple(cursorChange.toString() + "-ignored", System.currentTimeMillis()));
            return;
        }

        incCursorMoves();
        keys.add(new Tuple(cursorChange.toString(), System.currentTimeMillis()));

        if(cursorChange.getNewSelStart() < compositionStartIndex){
            ignoreInput = true;
            outPhraseTS = System.currentTimeMillis();
            return;
        }else {
            timeOutsideCurrentPhrase.add(System.currentTimeMillis() - outPhraseTS);
            outPhraseTS = -1;
            ignoreInput = false;
        }

        int transcribeLength = getTranscribe().length();

        if (inputBuffer.length() > 0 && inputBuffer.charAt(inputBuffer.length()-1) == ' ')
            transcribeLength ++;

        isCursorOnEnd = transcribeLength == (cursorChange.getNewSelStart() - compositionStartIndex);

        cursorChanges.add(cursorChange);
    }

}
