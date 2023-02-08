package pt.lasige.ideafast.study.inputmethod.study.questionnaire;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pt.lasige.ideafast.study.latin.R;
import pt.lasige.ideafast.study.inputmethod.study.adapters.SingleChoiceAdapter;

public class SingleChoiceCustom implements Step {

    boolean optional;
    StepIdentifier id;
    String question, continueText;

    ArrayList<String> steps;
    String checked;
    RecyclerView rv;

    public SingleChoiceCustom(String question, String continueText, ArrayList<String> steps, boolean optional, StepIdentifier id) {
        this.optional = optional;
        this.id = id;
        this.question = question;
        this.steps = steps;
        this.continueText = continueText;
        this.checked = "";
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
            boolean nextEnabled = false;
            SurveyTheme surveyTheme;
            Date start, end;
            SingleChoiceAdapter adapter;
            @Override
            public void setupViews() {
                this.start = Calendar.getInstance().getTime();

                root = inflate(context, R.layout.custom_questionnaire_multi_choice, this);

                ((TextView) findViewById(R.id.tv_question)).setText(question);

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
                root.findViewById(R.id.button_skip_question).setOnClickListener(view -> {
                    this.getOnSkipListener();
                });
                root.findViewById(R.id.button_skip_question).setVisibility(GONE);

            }

            @Override
            public void style(@NotNull SurveyTheme surveyTheme) {
                this.surveyTheme = surveyTheme;
                ((Button) root.findViewById(R.id.button_continue)).setText(continueText);
                ((Button) root.findViewById(R.id.headerCancelButton)).setTextColor(surveyTheme.getTextColor());
                ((ImageView) root.findViewById(R.id.headerBackButtonImage)).getBackground().setTint(surveyTheme.getTextColor());

                RecyclerView rv = root.findViewById(R.id.rv_multi_choice);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                rv.setLayoutManager(layoutManager);
                adapter = new SingleChoiceAdapter(
                        steps,
                        checked,
                        surveyTheme,
                        ((Button) root.findViewById(R.id.button_continue)),
                        context);
                rv.setAdapter(adapter);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                        layoutManager.getOrientation());
                rv.addItemDecoration(dividerItemDecoration);
            }

            @NotNull
            @Override
            public QuestionResult createResults() {
                this.end = Calendar.getInstance().getTime();
                checked = adapter.getChecked();
                return new SingleChoiceCustomResult(checked, id, start, end);
            }

            @Override
            public boolean isValidInput() {
                return false;
            }

        };
    }

    static class SingleChoiceCustomResult implements QuestionResult, Parcelable {

        String answer;
        Identifier id;
        Date start, end;

        public SingleChoiceCustomResult(String answer, Identifier id, Date start, Date end) {
            this.answer = answer;
            this.id = id;
            this.start = start;
            this.end = end;
        }

        protected SingleChoiceCustomResult(Parcel in) { }

        public final Creator<SingleChoiceCustomResult> CREATOR = new Creator<SingleChoiceCustomResult>() {
            @Override
            public SingleChoiceCustomResult createFromParcel(Parcel in) {
                return new SingleChoiceCustomResult(in);
            }

            @Override
            public SingleChoiceCustomResult[] newArray(int size) {
                return new SingleChoiceCustomResult[size];
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
