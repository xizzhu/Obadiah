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

import android.content.ContentValues
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.annotation.RestrictTo
import androidx.annotation.WorkerThread

@RestrictTo(RestrictTo.Scope.LIBRARY)
class TableHelper(private val sqliteHelper: SQLiteOpenHelper) {
    companion object {
        private const val TABLE_NAME = "keyValueTable"
        private const val COLUMN_KEY = "key"
        private const val COLUMN_VALUE = "value"

        @WorkerThread
        fun createTable(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE $TABLE_NAME ($COLUMN_KEY TEXT PRIMARY KEY, $COLUMN_VALUE TEXT NOT NULL);")
        }
    }

    private val db by lazy { sqliteHelper.writableDatabase }

    @WorkerThread
    fun has(key: String): Boolean = DatabaseUtils.queryNumEntries(
            db, TABLE_NAME, "$COLUMN_KEY = ?", arrayOf(key)) > 0

    @WorkerThread
    fun read(key: String, defaultValue: String): String {
        var cursor: Cursor? = null
        try {
            cursor = db.query(TABLE_NAME, arrayOf(COLUMN_VALUE),
                    "$COLUMN_KEY = ?", arrayOf(key), null, null, null)
            return if (cursor.moveToNext()) {
                cursor.getString(0)
            } else {
                defaultValue
            }
        } finally {
            cursor?.close()
        }
    }

    @WorkerThread
    fun save(key: String, value: String) {
        val values = ContentValues(2).apply {
            put(COLUMN_KEY, key)
            put(COLUMN_VALUE, value)
        }
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    @WorkerThread
    fun remove(keys: Collection<String>) {
        if (keys.isEmpty()) {
            return
        }

        val whereClause = StringBuilder("$COLUMN_KEY IN (")
        repeat(keys.size) { whereClause.append("?,") }
        whereClause.deleteCharAt(whereClause.length - 1).append(')')
        db.delete(TABLE_NAME, whereClause.toString(), keys.toTypedArray())
    }

    @WorkerThread
    fun removeAll() {
        db.delete(TABLE_NAME, null, null)
    }
}
