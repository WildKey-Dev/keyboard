package pt.lasige.inputmethod.metrics;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pt.lasige.inputmethod.latin.RichInputMethodManager;
import pt.lasige.inputmethod.logger.DataBaseFacade;
import pt.lasige.inputmethod.logger.Logger;
import pt.lasige.inputmethod.logger.LoggerController;
import pt.lasige.inputmethod.logger.data.CursorChange;
import pt.lasige.inputmethod.logger.data.StudyConstants;
import pt.lasige.inputmethod.metrics.data.ActionCount;
import pt.lasige.inputmethod.metrics.data.AutoCorrections;
import pt.lasige.inputmethod.metrics.data.CorrectedErrorRate;
import pt.lasige.inputmethod.metrics.data.CorrectionActionCount;
import pt.lasige.inputmethod.metrics.data.EntryActionCount;
import pt.lasige.inputmethod.metrics.data.ErrorCorrectionAttempts;
import pt.lasige.inputmethod.metrics.data.FlightTime;
import pt.lasige.inputmethod.metrics.data.HoldTimeDeviations;
import pt.lasige.inputmethod.metrics.data.InsertionErrorRate;
import pt.lasige.inputmethod.metrics.data.OmissionErrorRate;
import pt.lasige.inputmethod.metrics.data.SelectedSuggestions;
import pt.lasige.inputmethod.metrics.data.SubstitutionsErrorRate;
import pt.lasige.inputmethod.metrics.data.TimeOutsidePhrase;
import pt.lasige.inputmethod.metrics.data.TimePerWord;
import pt.lasige.inputmethod.metrics.data.TotalChangedCharacters;
import pt.lasige.inputmethod.metrics.data.TotalErrorRate;
import pt.lasige.inputmethod.metrics.data.TouchMajorMinor;
import pt.lasige.inputmethod.metrics.data.TouchOffsetEntered;
import pt.lasige.inputmethod.metrics.data.TouchOffsetError;
import pt.lasige.inputmethod.metrics.data.TouchOffsetTarget;
import pt.lasige.inputmethod.metrics.data.UncorrectedErrorRate;
import pt.lasige.inputmethod.metrics.data.VoiceInput;
import pt.lasige.inputmethod.metrics.data.WordsPerMinute;
import pt.lasige.inputmethod.metrics.data.WrittenCharacters;
import pt.lasige.inputmethod.metrics.data.WrittenNumbers;
import pt.lasige.inputmethod.metrics.data.WrittenSpecialChars;
import pt.lasige.inputmethod.metrics.util.TextEntryTrialController;
import pt.lasige.inputmethod.metrics.textentry.datastructures.Tuple;

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

            int eca, wc, autoC, ss, vi, wn, wsc, ac, cac, eac;
            double wpm, cpa, cpc, cpe, ae, ce, ee;
            float uer, cer, ter, oer, ier, ser, tcc;
            ArrayList<Tuple> toe, tmm;
            ArrayList<Long> htd, ft, tpw, top;
            String myInputStream = log.getInputBuffer(false);
            String myTranscribe = log.getTranscribe();
            double time = (log.getFlightTimeBuffer().get(log.getFlightTimeBuffer().size()-1) -
                    log.getFlightTimeBuffer().get(0));

            wpm = new WordsPerMinute().execute(log.getTranscribe(), time);

            uer = new UncorrectedErrorRate().execute(trial.getTrial());
            cer = new CorrectedErrorRate().execute(trial.getTrial());
            ter = new TotalErrorRate().execute(trial.getTrial());
            oer = new OmissionErrorRate().execute(trial.getTrial());
            ier = new InsertionErrorRate().execute(trial.getTrial());
            ser = new SubstitutionsErrorRate().execute(trial.getTrial());
            eca = new ErrorCorrectionAttempts().execute(log);
            toe = new TouchOffsetEntered().execute(log);
            new TouchOffsetTarget().execute();
            new TouchOffsetError().execute();
            tmm = new TouchMajorMinor().execute(log);
            htd = new HoldTimeDeviations().execute(log);
            ft = new FlightTime().execute(log);
            wc = new WrittenCharacters().execute(log);
            wn = new WrittenNumbers().execute(log);
            wsc = new WrittenSpecialChars().execute(log);
            vi = new VoiceInput().execute(log);
            ss = new SelectedSuggestions().execute(log);
            autoC = new AutoCorrections().execute(log);
            tpw = new TimePerWord().execute(log);
            tcc = new TotalChangedCharacters().execute(trial.getTrial());
            ac = new ActionCount().execute(log);
            cac = new CorrectionActionCount().execute(log);
            eac = new EntryActionCount().execute(log);
            top = new TimeOutsidePhrase().execute(log);

            if(myInputStream.split(" ").length != myTranscribe.split(" ").length) {
                DataBaseFacade.getInstance().write("warning-input-transcribe-length-different", (myInputStream.split(" ").length - myTranscribe.split(" ").length), "/users/" + DataBaseFacade.getInstance().getFbUserID() + "/completedTasks/" + studyID + "/" + questionID + "/phrases/" + phraseNumber + "/");
            }
            DataBaseFacade.getInstance().write("words-per-minute", wpm, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("uncorrected-error-rate", uer, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("corrected-error-rate", cer, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("total-error-rate", ter, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("omission-error-rate", oer, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("insertions-error-rate", ier, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("substitutions-error-rate", ser, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("error-correction-attempts", eca, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("touch-offset-entered", toe, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("touch-major-and-minor", tmm, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("holdtime-deviations", htd, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("flight-time", ft, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("written-characters", wc, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("written-numbers", wn, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("written-special-characters", wsc, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("voice-input", vi, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("select-suggestions", ss, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("auto-correct", autoC, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("time-per-word", tpw, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("cursor-changes", log.getCursorMoves(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("total-changed-characters", tcc, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("action-count", ac, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("correction-action-count", cac, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("entry-action-count", eac, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("input-timestamps", log.getInputTimeStamps(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            DataBaseFacade.getInstance().write("time-outside-phrase", top, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");

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


            if(mode == StudyConstants.IMPLICIT_MODE){
            }else if(mode == StudyConstants.TRANSCRIPTION_MODE){
                DataBaseFacade.getInstance().write("input-stream-og", log.getOriginalInputBuffer(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
                DataBaseFacade.getInstance().write("input-stream", trial.getTrial().getInputStream(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
                DataBaseFacade.getInstance().write("target_phrase", trial.getTrial().getRequiredSentence(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
                DataBaseFacade.getInstance().write("transcribe", trial.getTrial().getTranscribedSentence(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
                if (LoggerController.getInstance().isLogTouch()){
                    DataBaseFacade.getInstance().write("motion", log.getMotion(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
                    DataBaseFacade.getInstance().write("keys", log.getKeys(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
                }
            }else if(mode == StudyConstants.COMPOSITION_MODE){
                DataBaseFacade.getInstance().write("target_phrase", trial.getTrial().getRequiredSentence(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/phrases/"+phraseNumber+"/");
            }
        }catch (Exception e){
            DataBaseFacade.getInstance().write("discarded", "something went wrong with metrics calculation", "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/implicit_mode/"+String.valueOf(System.currentTimeMillis())+"/phrases/"+0+"/");
            DataBaseFacade.getInstance().write("timestamp", System.currentTimeMillis(), "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/implicit_mode/"+String.valueOf(System.currentTimeMillis())+"/phrases/"+0+"/");
            e.printStackTrace();
        }

        //clean memory
        //log.clearBuffers();//no need anymore
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
                    calculateAll(log, targetPhrase, questionID, studyID, phraseNumber, false);
                }
            };
            executor.execute(thread);
        }

    }
}
