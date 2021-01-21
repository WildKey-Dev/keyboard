package pt.lasige.inputmethod.logger;

import android.content.Context;
import android.util.Log;

import pt.lasige.inputmethod.latin.settings.Settings;
import pt.lasige.inputmethod.logger.data.StudyConstants;
import pt.lasige.inputmethod.metrics.MetricsController;

import static android.content.Context.MODE_PRIVATE;

public class LoggerController {

    private static LoggerController instance;
    private Logger logger;
    private boolean implicitLog = true, log = true, logTouch = true, isInputPassword = false;

    public static LoggerController getInstance(){
        if(instance == null)
            instance = new LoggerController();

        return instance;
    }

    private LoggerController() {logger = new Logger();}

    public void resetLogger() {
        logger = new Logger();
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean isImplicitLog() {
        return implicitLog;
    }

    public void setImplicitLog(boolean implicitLog) {
        this.implicitLog = implicitLog;
    }

    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public void setLog(Context context, boolean log) {
        this.log = log;
        context.getSharedPreferences(Settings.PREF_LOG, MODE_PRIVATE).edit().putBoolean(Settings.PREF_LOG, this.log).apply();
    }

    public void setLogTouch(boolean logTouch) {
        this.logTouch = logTouch;
    }

    public boolean isLogTouch() {
        return logTouch;
    }

    public boolean isInputPassword() {
        return isInputPassword;
    }

    public void setInputPassword(boolean inputPassword) {
        isInputPassword = inputPassword;
    }

    public boolean shouldILog(){
        return (MetricsController.getInstance().mode != StudyConstants.IMPLICIT_MODE || isLog()) && !isInputPassword();
    }
}
