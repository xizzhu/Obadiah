/*
 * Copyright (C) 2019 Xizhi Zhu
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

package me.xizzhu.android.obadiah.sqlite.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import me.xizzhu.android.obadiah.sqlite.BaseSqliteTest
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@SmallTest
class TableHelperTest : BaseSqliteTest() {
    @Test
    fun testEmptyTable() {
        assertFalse(databaseHelper.tableHelper.has("random-key"))
        assertEquals("default-value", databaseHelper.tableHelper.read("random-key", "default-value"))
    }

    @Test
    fun testSaveThenRead() {
        val key = "my key"
        val value = "my value"

        databaseHelper.tableHelper.save(key, "random value")
        databaseHelper.tableHelper.save(key, value)
        assertTrue(databaseHelper.tableHelper.has(key))
        assertEquals(value, databaseHelper.tableHelper.read(key, ""))
    }
}
