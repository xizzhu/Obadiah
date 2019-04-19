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
            assertEquals("random value", sqliteStore.get("random-key", "random value"))
        }
    }

    @Test
    fun testSaveThenRead() {
        runBlocking {
            sqliteStore.edit().put("key", "value").commit()
            assertTrue(sqliteStore.contains("key"))
            assertEquals("value", sqliteStore.get("key", ""))
        }
    }

    @Test
    fun testSaveThenRemove() {
        runBlocking {
            sqliteStore.edit().put("key1", "value1").put("key2", "value2").commit()
            assertTrue(sqliteStore.contains("key1"))
            assertTrue(sqliteStore.contains("key2"))

            sqliteStore.edit().remove("key1").commit()
            assertFalse(sqliteStore.contains("key1"))
            assertTrue(sqliteStore.contains("key2"))
            assertEquals("value2", sqliteStore.get("key2", ""))
        }
    }

    @Test
    fun testSaveThenClear() {
        runBlocking {
            sqliteStore.edit().put("key1", "value1").put("key2", "value2").commit()
            assertTrue(sqliteStore.contains("key1"))
            assertTrue(sqliteStore.contains("key2"))

            sqliteStore.edit().clear().commit()
            assertFalse(sqliteStore.contains("key1"))
            assertFalse(sqliteStore.contains("key2"))
        }
    }

    @Test
    fun testSaveThenClearAndSave() {
        runBlocking {
            sqliteStore.edit().put("key1", "value1").put("key2", "value2").commit()
            assertTrue(sqliteStore.contains("key1"))
            assertTrue(sqliteStore.contains("key2"))

            sqliteStore.edit().clear().put("key3", "value3").commit()
            assertFalse(sqliteStore.contains("key1"))
            assertFalse(sqliteStore.contains("key2"))
            assertTrue(sqliteStore.contains("key3"))
            assertEquals("value3", sqliteStore.get("key3", ""))
        }
    }
}
