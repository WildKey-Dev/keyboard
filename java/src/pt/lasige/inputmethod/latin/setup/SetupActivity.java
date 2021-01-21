/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pt.lasige.inputmethod.latin.setup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import pt.lasige.inputmethod.logger.DataBaseFacade;
import pt.lasige.inputmethod.study.AlternateFingerTappingActivity;
import pt.lasige.inputmethod.study.PromptLauncherActivity;
import pt.lasige.inputmethod.study.questionnaire.QuestionnaireLauncherActivity;
import pt.lasige.inputmethod.study.CompositionActivity;
import pt.lasige.inputmethod.study.TranscriptionActivity;

public final class SetupActivity extends Activity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataBaseFacade.getInstance().getFb().setConfigIDListener(getApplicationContext());
        DataBaseFacade.getInstance().setDemo(false);

        Intent i = getIntent();

        if(i.getStringExtra("type") != null &&
                i.getStringExtra("question-id") != null &&
                i.getStringExtra("phrase") != null &&
                i.getStringExtra("type").equals("transcription")){
            final Intent intent = new Intent();
            intent.setClass(this, TranscriptionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("question-id", i.getStringExtra("question-id"));
            intent.putExtra("phrase", i.getStringExtra("phrase"));
            startActivity(intent);

        }else if(i.getStringExtra("type") != null &&
                i.getStringExtra("question-id") != null &&
                i.getStringExtra("question") != null &&
                i.getStringExtra("type").equals("questionnaire")){
            final Intent intent = new Intent();
            intent.setClass(this, QuestionnaireLauncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("question-id", i.getStringExtra("question-id"));
            intent.putExtra("question", i.getStringExtra("question"));
            startActivity(intent);
            if (!isFinishing()) {
                finish();
            }

        }else if(i.getStringExtra("type") != null &&
                i.getStringExtra("question-id") != null &&
                i.getStringExtra("question") != null &&
                i.getStringExtra("type").equals("composition")){
            final Intent intent = new Intent();
            intent.setClass(this, CompositionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("question-id", i.getStringExtra("question-id"));
            intent.putExtra("question", i.getStringExtra("question"));
            startActivity(intent);
            if (!isFinishing()) {
                finish();
            }
        }else if(i.getStringExtra("type") != null &&
                i.getStringExtra("question-id") != null &&
                i.getStringExtra("type").equals("alternate-finger-tapping")){
            final Intent intent = new Intent();
            intent.setClass(this, AlternateFingerTappingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("question-id", i.getStringExtra("question-id"));
            startActivity(intent);
            if (!isFinishing()) {
                finish();
            }
        }else if(i.getStringExtra("type") != null &&
                i.getStringExtra("type").equals("notification")){

                Intent pra = new Intent(getApplicationContext(), PromptLauncherActivity.class);
                startActivity(pra);
                finish();
        }else {

            final Intent intent = new Intent();
            intent.setClass(this, SetupWizardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            if (!isFinishing()) {
                finish();
            }
        }
    }
}
