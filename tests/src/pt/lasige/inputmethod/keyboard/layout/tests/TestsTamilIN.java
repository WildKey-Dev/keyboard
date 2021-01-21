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
import pt.lasige.inputmethod.keyboard.layout.Symbols;
import pt.lasige.inputmethod.keyboard.layout.SymbolsShifted;
import pt.lasige.inputmethod.keyboard.layout.Tamil;
import pt.lasige.inputmethod.keyboard.layout.customizer.TamilCustomizer;
import pt.lasige.inputmethod.keyboard.layout.expected.ExpectedKey;

import java.util.Locale;

/**
 * ta_IN: Tamil (India)/tamil
 */
@SmallTest
public final class TestsTamilIN extends LayoutTestsBase {
    private static final Locale LOCALE = new Locale("ta", "IN");
    private static final LayoutBase LAYOUT = new Tamil(new TamilINCustomizer(LOCALE));

    @Override
    LayoutBase getLayout() { return LAYOUT; }

    private static class TamilINCustomizer extends TamilCustomizer {
        TamilINCustomizer(final Locale locale) { super(locale); }

        @Override
        public ExpectedKey getCurrencyKey() { return CURRENCY_RUPEE; }

        @Override
        public ExpectedKey[] getOtherCurrencyKeys() {
            return SymbolsShifted.CURRENCIES_OTHER_GENERIC;
        }

        // U+20B9: "₹" INDIAN RUPEE SIGN
        private static final ExpectedKey CURRENCY_RUPEE = key("\u20B9",
                Symbols.CURRENCY_GENERIC_MORE_KEYS);
    }
}
