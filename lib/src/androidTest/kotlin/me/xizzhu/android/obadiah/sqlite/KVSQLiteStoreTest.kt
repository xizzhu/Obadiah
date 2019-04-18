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
            sqliteStore.edit()
                    .putBoolean("boolean", true)
                    .putDouble("double", 1.23456789)
                    .putFloat("float", 1.234F)
                    .putInt("int", 12345)
                    .putLong("long", 1234567890L)
                    .putString("string", "0123456789")
                    .apply()

            assertTrue(sqliteStore.contains("boolean"))
            assertEquals(true, sqliteStore.getBoolean("boolean", false))

            assertTrue(sqliteStore.contains("double"))
            assertEquals(1.23456789, sqliteStore.getDouble("double", 0.0))

            assertTrue(sqliteStore.contains("float"))
            assertEquals(1.234F, sqliteStore.getFloat("float", 0.0F))

            assertTrue(sqliteStore.contains("int"))
            assertEquals(12345, sqliteStore.getInt("int", 0))

            assertTrue(sqliteStore.contains("long"))
            assertEquals(1234567890L, sqliteStore.getLong("long", 0L))

            assertTrue(sqliteStore.contains("string"))
            assertEquals("0123456789", sqliteStore.getString("string", ""))
        }
    }

    @Test
    fun testSaveThenRemove() {
        runBlocking {
            sqliteStore.edit().putBoolean("boolean", true).putDouble("double", 1.23456789).apply()

            assertTrue(sqliteStore.contains("boolean"))
            assertEquals(true, sqliteStore.getBoolean("boolean", false))

            assertTrue(sqliteStore.contains("double"))
            assertEquals(1.23456789, sqliteStore.getDouble("double", 0.0))

            sqliteStore.edit().remove("boolean").apply()
            assertFalse(sqliteStore.contains("boolean"))
            assertTrue(sqliteStore.contains("double"))
            assertEquals(1.23456789, sqliteStore.getDouble("double", 0.0))
        }
    }

    @Test
    fun testSaveThenClear() {
        runBlocking {
            sqliteStore.edit().putBoolean("boolean", true).putDouble("double", 1.23456789).apply()

            assertTrue(sqliteStore.contains("boolean"))
            assertEquals(true, sqliteStore.getBoolean("boolean", false))

            assertTrue(sqliteStore.contains("double"))
            assertEquals(1.23456789, sqliteStore.getDouble("double", 0.0))

            sqliteStore.edit().clear().apply()
            assertFalse(sqliteStore.contains("boolean"))
            assertFalse(sqliteStore.contains("double"))
        }
    }

    @Test
    fun testSaveThenClearAndSave() {
        runBlocking {
            sqliteStore.edit().putBoolean("boolean", true).putDouble("double", 1.23456789).apply()

            assertTrue(sqliteStore.contains("boolean"))
            assertEquals(true, sqliteStore.getBoolean("boolean", false))

            assertTrue(sqliteStore.contains("double"))
            assertEquals(1.23456789, sqliteStore.getDouble("double", 0.0))

            sqliteStore.edit().clear().putInt("int", 12345).apply()
            assertFalse(sqliteStore.contains("boolean"))
            assertFalse(sqliteStore.contains("double"))
            assertTrue(sqliteStore.contains("int"))
            assertEquals(12345, sqliteStore.getInt("int", 0))
        }
    }
}
