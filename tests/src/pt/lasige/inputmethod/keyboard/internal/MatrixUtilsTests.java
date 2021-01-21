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

package pt.lasige.inputmethod.keyboard.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import pt.lasige.inputmethod.keyboard.internal.MatrixUtils.MatrixOperationFailedException;

import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class MatrixUtilsTests {
    // "run tests" -c pt.lasige.inputmethod.keyboard.internal.MatrixUtilsTests
    private static final boolean DEBUG = false;
    private static final float EPSILON = 0.00001f;

    @Test
    public void testMulti() {
        final float[][] matrixA = {{1, 2}, {3, 4}};
        final float[][] matrixB = {{5, 6}, {7, 8}};
        final float[][] retval = new float[2][2];
        try {
            MatrixUtils.multiply(matrixA, matrixB, retval);
        } catch (MatrixOperationFailedException e) {
            assertTrue(false);
        }
        if (DEBUG) {
            MatrixUtils.dump("multi", retval);
        }
        assertEquals(retval[0][0], 19, EPSILON);
        assertEquals(retval[0][1], 22, EPSILON);
        assertEquals(retval[1][0], 43, EPSILON);
        assertEquals(retval[1][1], 50, EPSILON);
    }

    @Test
    public void testInverse() {
        final int N = 4;
        final float[][] matrix =
                {{1, 2, 3, 4}, {4, 0, 5, 6}, {6, 4, 2, 0}, {6, 4, 2, 1}};
        final float[][] inverse = new float[N][N];
        final float[][] tempMatrix = new float[N][N];
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < N; ++j) {
                tempMatrix[i][j] = matrix[i][j];
            }
        }
        final float[][] retval = new float[N][N];
        try {
            MatrixUtils.inverse(tempMatrix, inverse);
        } catch (MatrixOperationFailedException e) {
            assertTrue(false);
        }
        try {
            MatrixUtils.multiply(matrix, inverse, retval);
        } catch (MatrixOperationFailedException e) {
            assertTrue(false);
        }
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < N; ++j) {
                assertEquals(((i == j) ? 1.0f : 0.0f), retval[i][j], EPSILON);
            }
        }
    }
}
