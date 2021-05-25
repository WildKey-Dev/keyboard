package uk.openlab.inputmethod.metrics;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import uk.openlab.inputmethod.latin.RichInputMethodManager;
import uk.openlab.inputmethod.logger.DataBaseFacade;
import uk.openlab.inputmethod.logger.Logger;
import uk.openlab.inputmethod.logger.LoggerController;
import uk.openlab.inputmethod.logger.data.StudyConstants;
import uk.openlab.inputmethod.metrics.data.ActionCount;
import uk.openlab.inputmethod.metrics.data.AutoCorrections;
import uk.openlab.inputmethod.metrics.data.CorrectedErrorRate;
import uk.openlab.inputmethod.metrics.data.CorrectionActionCount;
import uk.openlab.inputmethod.metrics.data.EntryActionCount;
import uk.openlab.inputmethod.metrics.data.ErrorCorrectionAttempts;
import uk.openlab.inputmethod.metrics.data.FlightTime;
import uk.openlab.inputmethod.metrics.data.HoldTimeDeviations;
import uk.openlab.inputmethod.metrics.data.InsertionErrorRate;
import uk.openlab.inputmethod.metrics.data.OmissionErrorRate;
import uk.openlab.inputmethod.metrics.data.SelectedSuggestions;
import uk.openlab.inputmethod.metrics.data.SubstitutionsErrorRate;
import uk.openlab.inputmethod.metrics.data.TimeOutsidePhrase;
import uk.openlab.inputmethod.metrics.data.TimePerWord;
import uk.openlab.inputmethod.metrics.data.TotalChangedCharacters;
import uk.openlab.inputmethod.metrics.data.TotalErrorRate;
import uk.openlab.inputmethod.metrics.data.TouchMajorMinor;
import uk.openlab.inputmethod.metrics.data.TouchOffsetEntered;
import uk.openlab.inputmethod.metrics.data.TouchOffsetError;
import uk.openlab.inputmethod.metrics.data.TouchOffsetTarget;
import uk.openlab.inputmethod.metrics.data.UncorrectedErrorRate;
import uk.openlab.inputmethod.metrics.data.VoiceInput;
import uk.openlab.inputmethod.metrics.data.WordsPerMinute;
import uk.openlab.inputmethod.metrics.data.WrittenCharacters;
import uk.openlab.inputmethod.metrics.data.WrittenNumbers;
import uk.openlab.inputmethod.metrics.data.WrittenSpecialChars;
import uk.openlab.inputmethod.metrics.util.TextEntryTrialController;
import uk.openlab.inputmethod.metrics.textentry.datastructures.Tuple;

public class MetricsController {

    public int mode;
    private static MetricsController instance;
    private ThreadPoolExecutor executor;
    public static MetricsController getInstance(){
        if(instance == null)
            instance = new MetricsController();

        return instance;
    }

    private MetricsController() {
        setMode(StudyConstants.IMPLICIT_MODE);
        executor = new ThreadPoolExecutor(4, 8,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    public void setMode(int mode) {
        this.mode = mode;
        LoggerController.getInstance().resetLogger();
    }

    private void calculateAll(final Logger log, final String targetPhrase, String questionID, String studyID, int phraseNumber, boolean removeSuggestionsFromInputStream){

        if(LoggerController.getInstance().isInputPassword() || !LoggerController.getInstance().isLog() || log.getTranscribe().isEmpty()) {
            return;
        }

        TextEntryTrialController trial = null;

        try {

            //fresh trial
            trial = configTrial(log, targetPhrase, removeSuggestionsFromInputStream);

        }catch (Exception e){
            DataBaseFacade.getInstance().write("discarded", "something went wrong with trial calculation", "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/implicit_mode/"+String.valueOf(System.currentTimeMillis())+"/phrases/"+0+"/");
            DataBaseFacade.getInstance().write("timestamp", System.currentTimeMillis(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/implicit_mode/"+String.valueOf(System.currentTimeMillis())+"/phrases/"+0+"/");
        }

        int eca, wc, autoC, ss, vi, wn, wsc, ac, cac, eac;
        double wpm, cpa, cpc, cpe, ae, ce, ee;
        float uer, cer, ter, oer, ier, ser, tcc;
        ArrayList<Tuple> toe, tmm;
        ArrayList<Long> htd, ft, tpw, top;
        String myInputStream = log.getInputBuffer(false);
        String myTranscribe = log.getTranscribe();
        double time = (log.getFlightTimeBuffer().get(log.getFlightTimeBuffer().size()-1) -
                log.getFlightTimeBuffer().get(0));

        try{
            wpm = new WordsPerMinute().execute(log.getTranscribe(), time);
            DataBaseFacade.getInstance().write("words-per-minute", wpm, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("words-per-minute", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            if(trial == null){
                DataBaseFacade.getInstance().write("uncorrected-error-rate", "trial object was null", "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }else {
                uer = new UncorrectedErrorRate().execute(trial.getTrial());
                DataBaseFacade.getInstance().write("uncorrected-error-rate", uer, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }
        }catch (Exception e){
            DataBaseFacade.getInstance().write("uncorrected-error-rate", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            if(trial == null){
                DataBaseFacade.getInstance().write("corrected-error-rate", "trial object was null", "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }else {
                cer = new CorrectedErrorRate().execute(trial.getTrial());
                DataBaseFacade.getInstance().write("corrected-error-rate", cer, "/users/" + DataBaseFacade.getInstance().getFbUserID() + "/completedTasks/" + studyID + "/" + questionID + "/phrases/" + phraseNumber + "/");
            }
        }catch (Exception e){
            DataBaseFacade.getInstance().write("corrected-error-rate", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            if(trial == null){
                DataBaseFacade.getInstance().write("total-error-rate", "trial object was null", "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }else {
                ter = new TotalErrorRate().execute(trial.getTrial());
                DataBaseFacade.getInstance().write("total-error-rate", ter, "/users/" + DataBaseFacade.getInstance().getFbUserID() + "/completedTasks/" + studyID + "/" + questionID + "/phrases/" + phraseNumber + "/");
            }
        }catch (Exception e){
            DataBaseFacade.getInstance().write("total-error-rate", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            if(trial == null){
                DataBaseFacade.getInstance().write("omission-error-rate", "trial object was null", "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }else {
                oer = new OmissionErrorRate().execute(trial.getTrial());
                DataBaseFacade.getInstance().write("omission-error-rate", oer, "/users/" + DataBaseFacade.getInstance().getFbUserID() + "/completedTasks/" + studyID + "/" + questionID + "/phrases/" + phraseNumber + "/");
            }
        }catch (Exception e){
            DataBaseFacade.getInstance().write("omission-error-rate", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            if(trial == null){
                DataBaseFacade.getInstance().write("insertions-error-rate", "trial object was null", "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }else {
                ier = new InsertionErrorRate().execute(trial.getTrial());
                DataBaseFacade.getInstance().write("insertions-error-rate", ier, "/users/" + DataBaseFacade.getInstance().getFbUserID() + "/completedTasks/" + studyID + "/" + questionID + "/phrases/" + phraseNumber + "/");
            }
        }catch (Exception e){
            DataBaseFacade.getInstance().write("insertions-error-rate", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            if(trial == null){
                DataBaseFacade.getInstance().write("substitutions-error-rate", "trial object was null", "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }else {
                ser = new SubstitutionsErrorRate().execute(trial.getTrial());
                DataBaseFacade.getInstance().write("substitutions-error-rate", ser, "/users/" + DataBaseFacade.getInstance().getFbUserID() + "/completedTasks/" + studyID + "/" + questionID + "/phrases/" + phraseNumber + "/");
            }
        }catch (Exception e){
            DataBaseFacade.getInstance().write("substitutions-error-rate", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            eca = new ErrorCorrectionAttempts().execute(log);
            DataBaseFacade.getInstance().write("error-correction-attempts", eca, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("error-correction-attempts", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            toe = new TouchOffsetEntered().execute(log);
            DataBaseFacade.getInstance().write("touch-offset-entered", toe, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("touch-offset-entered", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            tmm = new TouchMajorMinor().execute(log);
            DataBaseFacade.getInstance().write("touch-major-and-minor", tmm, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("touch-major-and-minor", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            htd = new HoldTimeDeviations().execute(log);
            DataBaseFacade.getInstance().write("holdtime-deviations", htd, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("holdtime-deviations", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            ft = new FlightTime().execute(log);
            DataBaseFacade.getInstance().write("flight-time", ft, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("flight-time", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            wc = new WrittenCharacters().execute(log);
            DataBaseFacade.getInstance().write("written-characters", wc, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("written-characters", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            wn = new WrittenNumbers().execute(log);
            DataBaseFacade.getInstance().write("written-numbers", wn, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("written-numbers", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            wsc = new WrittenSpecialChars().execute(log);
            DataBaseFacade.getInstance().write("written-special-characters", wsc, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("written-special-characters", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            vi = new VoiceInput().execute(log);
            DataBaseFacade.getInstance().write("voice-input", vi, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("voice-input", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            ss = new SelectedSuggestions().execute(log);
            DataBaseFacade.getInstance().write("select-suggestions", ss, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("select-suggestions", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            autoC = new AutoCorrections().execute(log);
            DataBaseFacade.getInstance().write("auto-correct", autoC, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("auto-correct", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            tpw = new TimePerWord().execute(log);
            DataBaseFacade.getInstance().write("time-per-word", tpw, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("time-per-word", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            if(trial == null){
                DataBaseFacade.getInstance().write("total-changed-characters", "trial object was null", "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }else {
                tcc = new TotalChangedCharacters().execute(trial.getTrial());
                DataBaseFacade.getInstance().write("total-changed-characters", tcc, "/users/" + DataBaseFacade.getInstance().getFbUserID() + "/completedTasks/" + studyID + "/" + questionID + "/phrases/" + phraseNumber + "/");
            }
        }catch (Exception e){
            DataBaseFacade.getInstance().write("total-changed-characters", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            ac = new ActionCount().execute(log);
            DataBaseFacade.getInstance().write("action-count", ac, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("action-count", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            cac = new CorrectionActionCount().execute(log);
            DataBaseFacade.getInstance().write("correction-action-count", cac, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("correction-action-count", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            eac = new EntryActionCount().execute(log);
            DataBaseFacade.getInstance().write("entry-action-count", eac, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("entry-action-count", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try{
            top = new TimeOutsidePhrase().execute(log);
            DataBaseFacade.getInstance().write("time-outside-phrase", top, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("time-outside-phrase", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }

        try {
            if(myInputStream.split(" ").length != myTranscribe.split(" ").length) {
                DataBaseFacade.getInstance().write("warning-input-transcribe-length-different", (myInputStream.split(" ").length - myTranscribe.split(" ").length), "/users/" + DataBaseFacade.getInstance().getFbUserID() + "/completedTasks/" + studyID + "/" + questionID + "/phrases/" + phraseNumber + "/");
            }
        }catch (Exception e){
            DataBaseFacade.getInstance().write("warning-input-transcribe-length-different", e.getMessage(), "/users/" + DataBaseFacade.getInstance().getFbUserID() + "/completedTasks/" + studyID + "/" + questionID + "/phrases/" + phraseNumber + "/");
        }
        try {
            DataBaseFacade.getInstance().write("cursor-changes", log.getCursorMoves(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("cursor-changes", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }
        try {
            DataBaseFacade.getInstance().write("input-timestamps", log.getInputTimeStamps(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }catch (Exception e){
            DataBaseFacade.getInstance().write("input-timestamps", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
        }

        try{
            //Language
            DataBaseFacade.getInstance().write(
                    "country",
                    String.format("%s", RichInputMethodManager.getInstance().getCurrentSubtypeLocale().getDisplayCountry()),
                    "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/language");
            DataBaseFacade.getInstance().write(
                    "language",
                    String.format("%s", RichInputMethodManager.getInstance().getCurrentSubtypeLocale().getDisplayLanguage()),
                    "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/language");
            DataBaseFacade.getInstance().write(
                    "name",
                    String.format("%s", RichInputMethodManager.getInstance().getCurrentSubtypeLocale().getDisplayName()),
                    "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/language");
            DataBaseFacade.getInstance().write(
                    "iso_country",
                    String.format("%s", RichInputMethodManager.getInstance().getCurrentSubtypeLocale().getISO3Country()),
                    "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/language");
            DataBaseFacade.getInstance().write(
                    "iso_language",
                    String.format("%s", RichInputMethodManager.getInstance().getCurrentSubtypeLocale().getISO3Language()),
                    "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/language");
            DataBaseFacade.getInstance().write(
                    "tag",
                    String.format("%s", RichInputMethodManager.getInstance().getCurrentSubtypeLocale().toLanguageTag()),
                    "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/language");
        }catch (Exception e){
            DataBaseFacade.getInstance().write(
                    "language",
                    String.format("%s", RichInputMethodManager.getInstance().getCurrentSubtypeLocale().getDisplayLanguage()),
                    "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/language");
        }

        if(mode == StudyConstants.IMPLICIT_MODE){
        }else if(mode == StudyConstants.TRANSCRIPTION_MODE || mode == StudyConstants.COMPOSITION_MODE){
            try {
                DataBaseFacade.getInstance().write("input-stream-og", log.getOriginalInputBuffer(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }catch (Exception e){
                DataBaseFacade.getInstance().write("input-stream-og", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }
            try {
                if(trial == null)
                    DataBaseFacade.getInstance().write("input-stream", "trial object was null", "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
                else
                    DataBaseFacade.getInstance().write("input-stream", trial.getTrial().getInputStream(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }catch (Exception e){
                DataBaseFacade.getInstance().write("input-stream", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }
            try {
                if(trial == null)
                    DataBaseFacade.getInstance().write("target_phrase", "trial object was null", "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
                else
                    DataBaseFacade.getInstance().write("target_phrase", trial.getTrial().getRequiredSentence(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }catch (Exception e){
                DataBaseFacade.getInstance().write("target_phrase", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }
            try {
                if(trial == null)
                    DataBaseFacade.getInstance().write("transcribe", "trial object was null", "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
                else
                    DataBaseFacade.getInstance().write("transcribe", trial.getTrial().getTranscribedSentence(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }catch (Exception e){
                DataBaseFacade.getInstance().write("transcribe", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }
            try {
                DataBaseFacade.getInstance().write("cursor-changes", log.getCursorChanges(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }catch (Exception e){
                DataBaseFacade.getInstance().write("cursor-changes", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }
            if (LoggerController.getInstance().isLogTouch()){
                try {
                    DataBaseFacade.getInstance().write("motion", log.getMotion(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
                }catch (Exception e){
                    DataBaseFacade.getInstance().write("motion", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
                }
                try {
                    DataBaseFacade.getInstance().write("keys", log.getKeys(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
                }catch (Exception e){
                    DataBaseFacade.getInstance().write("keys", e.getMessage(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
                }
            }
        }

        //clean memory
        if(trial != null)
            trial.stopTrial();
    }

    private TextEntryTrialController configTrial(Logger log, String targetPhrase, boolean removeSuggestionsFromInputStream){

        //fresh trial
        TextEntryTrialController trial = new TextEntryTrialController();
        String newTargetPhrase;

        if(mode == StudyConstants.IMPLICIT_MODE || mode == StudyConstants.COMPOSITION_MODE){

            newTargetPhrase = log.getTargetPhrase();
            trial.startTrial(newTargetPhrase, log.getInputBuffer(removeSuggestionsFromInputStream), log.getTranscribe());
            trial.computeResults(false);

        }else if(mode == StudyConstants.TRANSCRIPTION_MODE){

            if(targetPhrase == null) {
                // calculate other trial with our take on target phrase
                targetPhrase = log.getTargetPhrase();
            }
            trial.startTrial(targetPhrase, log.getInputBuffer(removeSuggestionsFromInputStream), log.getTranscribe());
            trial.computeResults(true);
        }

        return trial;
    }

    public void onKeyboardHide() {

        if(mode == StudyConstants.IMPLICIT_MODE){
            final Logger log = LoggerController.getInstance().getLogger();
            LoggerController.getInstance().resetLogger();
            String ts = String.valueOf(System.currentTimeMillis());
            if(log.wasEditTextEmpty() && log.getDiscardedChars() == 0){
                runMetricCalculation(log, null, ts, "implicit_mode", 0);
            }else if(log.getDiscardedChars() > 0){
                DataBaseFacade.getInstance().write("discarded", "suggestion accepted on cursor change", "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/implicit_mode/"+ts+"/phrases/"+0+"/");
                DataBaseFacade.getInstance().write("discarded-characters", log.getDiscardedChars(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/implicit_mode/"+ts+"/phrases/"+0+"/");
                DataBaseFacade.getInstance().write("timestamp", System.currentTimeMillis(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/implicit_mode/"+String.valueOf(System.currentTimeMillis())+"/phrases/"+0+"/");
            } else {
                DataBaseFacade.getInstance().write("timestamp", System.currentTimeMillis(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/implicit_mode/"+String.valueOf(System.currentTimeMillis())+"/phrases/"+0+"/");
                DataBaseFacade.getInstance().write("discarded", "edit box was not empty", "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/implicit_mode/"+ts+"/phrases/"+0+"/");
            }

        }else if(mode == StudyConstants.TRANSCRIPTION_MODE){
        }else if(mode == StudyConstants.COMPOSITION_MODE){}
    }

    public void runMetricCalculation(final Logger log, final String targetPhrase, String questionID, String studyID, int phraseNumber){

        if(targetPhrase != null){
            //calculate one time with the target phrase
            Thread thread = new Thread() {
                @Override
                public void run() {
                    calculateAll(log, targetPhrase, questionID, studyID, phraseNumber, false);
                    calculateAll(log, null, questionID+"-generated-target-phrase", studyID, phraseNumber, false);
                }
            };
            executor.execute(thread);
        }else {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    calculateAll(log, null, questionID, studyID, phraseNumber, false);
                }
            };
            executor.execute(thread);
        }

    }
}
