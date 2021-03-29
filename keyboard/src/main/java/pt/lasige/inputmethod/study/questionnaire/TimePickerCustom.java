package pt.lasige.inputmethod.study.questionnaire;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.quickbirdstudios.surveykit.FinishReason;
import com.quickbirdstudios.surveykit.Identifier;
import com.quickbirdstudios.surveykit.StepIdentifier;
import com.quickbirdstudios.surveykit.SurveyTheme;
import com.quickbirdstudios.surveykit.backend.views.step.StepView;
import com.quickbirdstudios.surveykit.result.QuestionResult;
import com.quickbirdstudios.surveykit.result.StepResult;
import com.quickbirdstudios.surveykit.steps.Step;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.Date;

import pt.lasige.inputmethod.latin.R;

public class TimePickerCustom implements Step {

    boolean optional;
    StepIdentifier id;
    String question, continueText, time = "";

    public TimePickerCustom(String question, String continueText, boolean optional, StepIdentifier id) {
        this.optional = optional;
        this.id = id;
        this.question = question;
        this.continueText = continueText;
    }

    @NotNull
    @Override
    public StepIdentifier getId() {
        return this.id;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @NotNull
    @Override
    public StepView createView(@NotNull Context context, @Nullable StepResult stepResult) {
        return new StepView(context, id, optional) {
            View root;
            final boolean nextEnabled = true;
            SurveyTheme surveyTheme;
            Date start, end;

            @Override
            public void setupViews() {
                this.start = Calendar.getInstance().getTime();

                root = inflate(context, R.layout.custom_questionnaire_time_picker, this);

                ((TextView) root.findViewById(R.id.tv_question)).setText(question);

                root.findViewById(R.id.button_continue).setOnClickListener(view -> {
                    this.getOnNextListener().invoke(createResults());
                });
                root.findViewById(R.id.headerBackButtonImage).setOnClickListener(view -> {
                    this.getOnBackListener().invoke(createResults());
                });
                root.findViewById(R.id.headerCancelButton).setOnClickListener(view -> {
                    AlertDialog aDialog = new AlertDialog.Builder(context)
                            .setTitle(R.string.leave)
                            .setMessage(R.string.leave_message)
                            .setNeutralButton(R.string.leave_neutral_button, (dialog, which) -> {
                            })
                            .setNegativeButton(R.string.leave_negative_button, (dialog, which) -> {
                                this.getOnCloseListener().invoke(createResults(), FinishReason.Completed);
                            })
                            .create();

                    aDialog.setOnShowListener(dialog -> aDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED));
                    aDialog.show();
                });
                if(!time.isEmpty()){
                    ((TimePicker) findViewById(R.id.timePicker)).setHour(Integer.parseInt(time.split(":")[0]));
                    ((TimePicker) findViewById(R.id.timePicker)).setMinute(Integer.parseInt(time.split(":")[1]));
                }
                root.findViewById(R.id.button_skip_question).setOnClickListener(view -> {
                    this.getOnSkipListener();
                });
                root.findViewById(R.id.button_skip_question).setVisibility(GONE);
            }

            @Override
            public void style(@NotNull SurveyTheme surveyTheme) {
                this.surveyTheme = surveyTheme;
                colorMainButtonEnabledState();
                ((Button) root.findViewById(R.id.headerCancelButton)).setTextColor(surveyTheme.getTextColor());
                ((ImageView) root.findViewById(R.id.headerBackButtonImage)).getBackground().setTint(surveyTheme.getTextColor());
            }

            @NotNull
            @Override
            public QuestionResult createResults() {
                this.end = Calendar.getInstance().getTime();
                time = String.format("%d:%d",
                        ((TimePicker) findViewById(R.id.timePicker)).getHour(),
                        ((TimePicker) findViewById(R.id.timePicker)).getMinute());
                return new TimePickerCustomResult(
                        time,
                        id, start, end);
            }

            @Override
            public boolean isValidInput() {
                return false;
            }

            private void colorMainButtonEnabledState() {
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.main_button_background);
                if (nextEnabled) {
                    drawable.setColorFilter(surveyTheme.getThemeColor(), PorterDuff.Mode.SRC_IN);
                    ((Button) root.findViewById(R.id.button_continue)).setTextColor(surveyTheme.getTextColor());
                    ((Button) root.findViewById(R.id.button_continue)).setEnabled(true);
                } else {
                    ((Button) root.findViewById(R.id.button_continue)).setTextColor(ContextCompat.getColor(context, R.color.disabled_grey));
                    ((Button) root.findViewById(R.id.button_continue)).setEnabled(false);
                }
                ((Button) root.findViewById(R.id.button_continue)).setBackground(drawable);
                ((Button) root.findViewById(R.id.button_continue)).setText(continueText);
            }
        };
    }

    static class TimePickerCustomResult implements QuestionResult, Parcelable {

        String answer;
        Identifier id;
        Date start, end;

        public TimePickerCustomResult(String answer, Identifier id, Date start, Date end) {
            this.answer = answer;
            this.id = id;
            this.start = start;
            this.end = end;
        }

        protected TimePickerCustomResult(Parcel in) { }

        public final Creator<TimePickerCustomResult> CREATOR = new Creator<TimePickerCustomResult>() {
            @Override
            public TimePickerCustomResult createFromParcel(Parcel in) {
                return new TimePickerCustomResult(in);
            }

            @Override
            public TimePickerCustomResult[] newArray(int size) {
                return new TimePickerCustomResult[size];
            }
        };

        @NotNull
        @Override
        public String getStringIdentifier() {
            return id.getId();
        }

        @NotNull
        @Override
        public Date getEndDate() {
            return end;
        }

        @Override
        public void setEndDate(@NotNull Date date) {

        }

        @NotNull
        @Override
        public Identifier getId() {
            return id;
        }

        @NotNull
        @Override
        public Date getStartDate() {
            return start;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
        }

        public String getAnswer() {
            return answer;
        }
    }
}
