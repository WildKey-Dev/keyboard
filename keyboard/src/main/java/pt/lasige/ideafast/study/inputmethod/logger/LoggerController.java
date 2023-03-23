package pt.lasige.ideafast.study.inputmethod.logger;

import android.content.Context;

import pt.lasige.ideafast.study.inputmethod.logger.data.StudyConstants;
import pt.lasige.ideafast.study.inputmethod.metrics.MetricsController;

public class LoggerController {

    private static LoggerController instance;
    private Logger logger;
    boolean privateMode = false, logTouch = true, isInputPassword = false;

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

    public boolean isPrivateMode() {
        return privateMode;
    }

    public void setPrivateMode(boolean privateMode) {
        this.privateMode = privateMode;
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

    public void setOrientation(String orientation){
        logger.setOrientation(orientation);
    }

    public boolean shouldILog(){
        if(MetricsController.getInstance().mode == StudyConstants.IMPLICIT_MODE)
            if(privateMode)// the user turned of the log, this remains until the keyboard is closed
                return false;
            else
                return Implicit.getInstance().isOn();
        else
            return !isInputPassword;
    }
}
