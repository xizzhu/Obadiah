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
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.xizzhu.android.obadiah.KVStore
import me.xizzhu.android.obadiah.sqlite.internal.DatabaseHelper

class KVSQLiteStore : KVStore {
    private val databaseHelper: DatabaseHelper

    constructor(context: Context, name: String) : this(DatabaseHelper(context, name))

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @VisibleForTesting
    constructor(databaseHelper: DatabaseHelper) {
        this.databaseHelper = databaseHelper
    }

    override suspend fun contains(key: String): Boolean = withContext(Dispatchers.IO) {
        databaseHelper.tableHelper.has(key)
    }

    override fun edit(): KVStore.Editor = EditorImpl(databaseHelper)

    override suspend fun get(key: String, defaultValue: String): String = withContext(Dispatchers.IO) {
        databaseHelper.tableHelper.read(key, defaultValue)
    }

    override suspend fun close() {
        withContext(Dispatchers.IO) { databaseHelper.close() }
    }

    private class EditorImpl(private val databaseHelper: DatabaseHelper) : KVStore.Editor {
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
            withContext(Dispatchers.IO) {
                synchronized(editorLok) {
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
    }
}
