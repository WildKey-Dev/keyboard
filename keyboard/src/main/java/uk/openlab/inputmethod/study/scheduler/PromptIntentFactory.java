package uk.openlab.inputmethod.study.scheduler;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import uk.openlab.inputmethod.latin.R;
import uk.openlab.inputmethod.logger.data.Prompt;
import uk.openlab.inputmethod.study.AlternateFingerTappingActivity;
import uk.openlab.inputmethod.study.CompositionActivity;
import uk.openlab.inputmethod.study.questionnaire.QuestionnaireLauncherActivity;
import uk.openlab.inputmethod.study.TranscriptionActivity;

public class PromptIntentFactory {

    public static Intent getIntentForPrompt(Context context, Prompt p, String promptInternalID){
        Intent intent;
        switch (p.getType()){
            case "transcription":
                intent = new Intent(context, TranscriptionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("study-id", ScheduleController.getInstance().getConfig().getStudyId());
                intent.putExtra("question-id", promptInternalID);
                intent.putExtra("sub-type", p.getSubType());
                if(p.getSubType().equals("time"))
                    intent.putExtra("duration", p.getDuration());
                String[] phrases = new String[p.getPhrases().size()];
                intent.putExtra("phrases", p.getPhrases().toArray(phrases));
                break;
            case "questionnaire":
                intent = new Intent(context, QuestionnaireLauncherActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("study-id", ScheduleController.getInstance().getConfig().getStudyId());
                intent.putExtra("question-id", promptInternalID);
                String[] questions = new String[p.getQuestions().size()];
                intent.putExtra("questions", p.getQuestions().toArray(questions));
//                intent.putExtra("scale", p.getScale());
//                String[] scaleSteps = new String[p.getScaleSteps().size()];
//                intent.putExtra("scale-steps", p.getScaleSteps().toArray(scaleSteps));
                break;
            case "composition":
                intent = new Intent(context, CompositionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("study-id", ScheduleController.getInstance().getConfig().getStudyId());
                intent.putExtra("question-id", promptInternalID);
                intent.putExtra("sub-type", p.getSubType());
                if(p.getSubType().equals("time"))
                    intent.putExtra("duration", p.getDuration());
                String[] mQuestions = new String[p.getQuestions().size()];
                intent.putExtra("questions", p.getQuestions().toArray(mQuestions));
                break;
            case "alternate-finger-tapping":
                intent = new Intent(context, AlternateFingerTappingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("study-id", ScheduleController.getInstance().getConfig().getStudyId());
                intent.putExtra("question-id", promptInternalID);
                break;
            default:
                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                ScheduleController.getInstance().queue.remove(p.getPromptId());
                return null;
        }

        return intent;
    }
}
