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

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import me.xizzhu.android.obadiah.KVStore
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@SmallTest
class KVSQLiteStoreTest {
    private val databaseName = "testDatabase"
    private lateinit var sqliteStore: KVSQLiteStore

    @Before
    fun setup() {
        clearLocalStorage()
        sqliteStore = KVSQLiteStore(ApplicationProvider.getApplicationContext<Context>(), databaseName, Dispatchers.IO)
    }

    private fun clearLocalStorage() {
        ApplicationProvider.getApplicationContext<Context>().deleteDatabase(databaseName)
    }

    @After
    fun tearDown() {
        runBlocking { sqliteStore.close() }
        clearLocalStorage()
    }

    @Test
    fun testEmptyTable() {
        runBlocking {
            assertFalse(sqliteStore.has("random-key"))
            assertEquals("random value", sqliteStore.get("random-key", "random value"))
        }
    }

    @Test
    fun testSaveThenRead() {
        runBlocking {
            sqliteStore.edit().put("key", "value").commit()
            assertTrue(sqliteStore.has("key"))
            assertEquals("value", sqliteStore.get("key", ""))
        }
    }

    @Test
    fun testSaveThenRemove() {
        runBlocking {
            sqliteStore.edit().put("key1", "value1").put("key2", "value2").commit()
            assertTrue(sqliteStore.has("key1"))
            assertTrue(sqliteStore.has("key2"))

            sqliteStore.edit().remove("key1").commit()
            assertFalse(sqliteStore.has("key1"))
            assertTrue(sqliteStore.has("key2"))
            assertEquals("value2", sqliteStore.get("key2", ""))
        }
    }

    @Test
    fun testSaveThenClear() {
        runBlocking {
            sqliteStore.edit().put("key1", "value1").put("key2", "value2").commit()
            assertTrue(sqliteStore.has("key1"))
            assertTrue(sqliteStore.has("key2"))

            sqliteStore.edit().clear().commit()
            assertFalse(sqliteStore.has("key1"))
            assertFalse(sqliteStore.has("key2"))
        }
    }

    @Test
    fun testSaveThenClearAndSave() {
        runBlocking {
            sqliteStore.edit().put("key1", "value1").put("key2", "value2").commit()
            assertTrue(sqliteStore.has("key1"))
            assertTrue(sqliteStore.has("key2"))

            sqliteStore.edit().clear().put("key3", "value3").commit()
            assertFalse(sqliteStore.has("key1"))
            assertFalse(sqliteStore.has("key2"))
            assertTrue(sqliteStore.has("key3"))
            assertEquals("value3", sqliteStore.get("key3", ""))
        }
    }

    @Test
    fun testListeners() {
        runBlocking {
            val updatedKeys = mutableSetOf<String>()
            val listener = object : KVStore.OnChangeListener {
                override fun onValueChanged(store: KVStore, key: String, newValue: String) {
                    assertEquals(sqliteStore, store)

                    assertFalse(updatedKeys.contains(key))
                    updatedKeys.add(key)

                    when (key) {
                        "key1" -> assertEquals("value3", newValue)
                        "key2" -> assertEquals("value2", newValue)
                        else -> throw IllegalStateException("Unexpected key: $key")
                    }
                }
            }
            sqliteStore.addListener(listener)

            sqliteStore.edit().put("key1", "value1").put("key2", "value2").put("key1", "value3").commit()
        }
    }

    @Test
    fun testAddThenRemoveListeners() {
        runBlocking {
            val listener = object : KVStore.OnChangeListener {
                override fun onValueChanged(store: KVStore, key: String, newValue: String) {
                    throw IllegalStateException("Should not be called")
                }
            }
            sqliteStore.addListener(listener)
            sqliteStore.addListener(listener)
            sqliteStore.removeListener(listener)

            sqliteStore.edit().put("key1", "value1").put("key2", "value2").commit()
        }
    }
}
