/*
 * Copyright (C) 2014 The Android Open Source Project
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

package pt.lasige.inputmethod.latin.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import pt.lasige.inputmethod.latin.R;
import pt.lasige.inputmethod.logger.DataBaseFacade;

public final class StudySettingsFragmentSubMenu extends SubScreenFragment {
    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.prefs_screen_study_submenu);

        final Resources res = getResources();
        final Context context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Inserir password");

        View layout = LayoutInflater.from(context)
                .inflate(R.layout.change_config_dialog, null, false);
        builder.setView(layout);
        EditText input = layout.findViewById(R.id.et_config_id);
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());

        final Preference reset = findPreference("reset_tasks");
        reset.setOnPreferenceClickListener(preference -> {
            builder.setPositiveButton(getString(R.string.setup_step0_action), (dialog, which) -> {
                String password = input.getText().toString();
                if(password.equals("masterdelete")){
                    Log.d("DELETE", "ENTREI");
                    DataBaseFacade.getInstance().deleteConfig();
                }

            });
            builder.show();
            return false;
        });

        
        final EditTextPreference resetNumber = (EditTextPreference) findPreference("reset_task_number");
        final Preference resetNumberBt = findPreference("reset_task_number_bt");

        resetNumber.setSummary(resetNumber.getText());
        resetNumber.setOnPreferenceChangeListener((preference, newValue) -> {
            builder.setPositiveButton(getString(R.string.setup_step0_action), (dialog, which) -> {
                String password = input.getText().toString();
                if(password.equals("masterdelete")){
                    resetNumber.setSummary(String.valueOf(newValue));
                    resetNumberBt.setSummary(getString(R.string.delete_phrases_after_summary) + " " + String.valueOf(newValue));
                }

            });
            builder.show();
            return true;
        });
        resetNumberBt.setSummary(getString(R.string.delete_phrases_after_summary) + " " + resetNumber.getText());
        resetNumberBt.setOnPreferenceClickListener(preference -> {
            builder.setPositiveButton(getString(R.string.setup_step0_action), (dialog, which) -> {
                String password = input.getText().toString();
                if(password.equals("masterdelete")){
                    DataBaseFacade.getInstance().deleteConfig(Integer.parseInt(resetNumber.getText()));
                }
            });
            builder.show();
            return false;
        });
    }
}
