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

package me.xizzhu.android.obadiah.sqlite

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@SmallTest
class KVSQLiteStoreTest : BaseSqliteTest() {
    private lateinit var sqliteStore: KVSQLiteStore

    override fun setup() {
        super.setup()
        sqliteStore = KVSQLiteStore(databaseHelper)
    }

    override fun tearDown() {
        runBlocking { sqliteStore.close() }
        super.tearDown()
    }

    @Test
    fun testEmptyTable() {
        runBlocking {
            assertFalse(sqliteStore.contains("random-key"))
            assertEquals(1.234, sqliteStore.getDouble("random-key", 1.234))
            assertEquals(true, sqliteStore.getBoolean("random-key", true))
            assertEquals(1.234F, sqliteStore.getFloat("random-key", 1.234F))
            assertEquals(1234, sqliteStore.getInt("random-key", 1234))
            assertEquals(1234L, sqliteStore.getLong("random-key", 1234L))
            assertEquals("random value", sqliteStore.getString("random-key", "random value"))
        }
    }

    @Test
    fun testSaveThenRead() {
        runBlocking {
            val key = "my key"
            val value = "1234"
            databaseHelper.tableHelper.save(mapOf(Pair(key, value)))

            assertTrue(sqliteStore.contains(key))
            assertEquals(value.toDouble(), sqliteStore.getDouble(key, 0.0))
            assertEquals(value.toBoolean(), sqliteStore.getBoolean(key, false))
            assertEquals(value.toFloat(), sqliteStore.getFloat(key, 0.0F))
            assertEquals(value.toInt(), sqliteStore.getInt(key, 0))
            assertEquals(value.toLong(), sqliteStore.getLong(key, 0L))
            assertEquals(value, sqliteStore.getString(key, ""))
        }
    }
}
