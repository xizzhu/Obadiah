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

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY)
class DatabaseHelper(context: Context, name: String) : SQLiteOpenHelper(context, name, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_VERSION = 1
    }

    val tableHelper = TableHelper(this)

    override fun onCreate(db: SQLiteDatabase) {
        TableHelper.createTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // do nothing
    }
}
