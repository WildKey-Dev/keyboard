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

package pt.lasige.inputmethod.keyboard.layout.tests;

import android.test.suitebuilder.annotation.SmallTest;

import pt.lasige.inputmethod.keyboard.layout.LayoutBase;
import pt.lasige.inputmethod.keyboard.layout.Qwerty;
import pt.lasige.inputmethod.keyboard.layout.customizer.UzbekCustomizer;
import pt.lasige.inputmethod.keyboard.layout.expected.ExpectedKeyboardBuilder;

import java.util.Locale;

/**
 * uz_UZ: Uzbek (Uzbekistan)/qwerty
 */
@SmallTest
public final class TestsUzbekQwerty extends LayoutTestsBase {
    private static final Locale LOCALE = new Locale("uz", "UZ");
    private static final LayoutBase LAYOUT = new Qwerty(new UzbekQwertyCustomizer(LOCALE));

    @Override
    LayoutBase getLayout() { return LAYOUT; }

    private static class UzbekQwertyCustomizer extends UzbekCustomizer {
        UzbekQwertyCustomizer(final Locale locale) { super(locale); }

        @Override
        protected void setUzbekKeys(final ExpectedKeyboardBuilder builder) {
            // QWERTY layout doesn't have Uzebk keys.
        }
    }
}
