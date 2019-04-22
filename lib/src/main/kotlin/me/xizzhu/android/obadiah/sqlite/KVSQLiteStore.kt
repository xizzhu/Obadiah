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
import androidx.annotation.RestrictTo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.xizzhu.android.obadiah.KVStore
import me.xizzhu.android.obadiah.sqlite.internal.DatabaseHelper
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

@RestrictTo(RestrictTo.Scope.LIBRARY)
class KVSQLiteStore(context: Context, name: String, private val dispatcher: CoroutineDispatcher) : KVStore {
    private val lock: Any = Any()
    private val listeners: WeakHashMap<KVStore.OnChangeListener, Any> = WeakHashMap()
    private val databaseHelper: DatabaseHelper = DatabaseHelper(context, name)

    override fun addListener(listener: KVStore.OnChangeListener): KVStore = apply {
        synchronized(lock) { listeners[listener] = null }
    }

    override fun removeListener(listener: KVStore.OnChangeListener): KVStore = apply {
        synchronized(lock) { listeners.remove(listener) }
    }

    override fun edit(): KVStore.Editor = EditorImpl(this, lock, listeners, databaseHelper, dispatcher)

    override suspend fun has(key: String): Boolean = withContext(dispatcher) {
        synchronized(lock) { databaseHelper.tableHelper.has(key) }
    }

    override suspend fun get(key: String, defaultValue: String): String = withContext(dispatcher) {
        synchronized(lock) { databaseHelper.tableHelper.read(key, defaultValue) }
    }

    override suspend fun close() {
        withContext(dispatcher) { databaseHelper.close() }
    }

    private class EditorImpl(private val kvStore: KVSQLiteStore, private val lock: Any,
                             private val weakListeners: WeakHashMap<KVStore.OnChangeListener, Any>,
                             private val databaseHelper: DatabaseHelper,
                             private val dispatcher: CoroutineDispatcher) : KVStore.Editor {
        private val editorLok: Any = Any()

        private var clear: Boolean = false
        private val removals: HashSet<String> = HashSet()
        private val updates: HashMap<String, String> = HashMap()

        override fun clear(): KVStore.Editor = apply {
            synchronized(editorLok) {
                clear = true
            }
        }

        override fun put(key: String, value: String): KVStore.Editor = apply {
            synchronized(editorLok) {
                updates[key] = value
            }
        }

        override fun remove(key: String): KVStore.Editor = apply {
            synchronized(editorLok) {
                removals.add(key)
            }
        }

        override suspend fun commit() {
            val updated = synchronized(editorLok) { updates.toMap() }
            val listeners = synchronized(lock) { weakListeners.keys }

            withContext(dispatcher) {
                synchronized(editorLok) {
                    synchronized(lock) {
                        val db = databaseHelper.writableDatabase
                        try {
                            db.beginTransaction()

                            if (clear) {
                                databaseHelper.tableHelper.removeAll()
                            } else {
                                databaseHelper.tableHelper.remove(removals)
                            }
                            databaseHelper.tableHelper.save(updates)

                            clear = false
                            removals.clear()
                            updates.clear()
                            db.setTransactionSuccessful()
                        } finally {
                            if (db.inTransaction()) {
                                db.endTransaction()
                            }
                        }
                    }
                }
            }

            withContext(Dispatchers.Main) {
                synchronized(lock) {
                    for (l in listeners) {
                        for ((k, v) in updated) {
                            l.onValueChanged(kvStore, k, v)
                        }
                    }
                }
            }
        }
    }
}
