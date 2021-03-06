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

package me.xizzhu.android.obadiah

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import me.xizzhu.android.obadiah.sqlite.KVSQLiteStore

interface KVStore {
    companion object {
        fun createInstance(context: Context, name: String = "me.xizzhu.android.obadiah",
                           dispatcher: CoroutineDispatcher = Dispatchers.IO): KVStore =
                KVSQLiteStore(context, name, dispatcher)
    }

    interface Editor {
        fun clear(): Editor

        fun put(key: String, value: String): Editor

        fun remove(key: String): Editor

        suspend fun commit()
    }

    interface OnChangeListener {
        fun onValueChanged(store: KVStore, key: String, newValue: String)
    }

    fun addListener(listener: OnChangeListener): KVStore

    fun removeListener(listener: OnChangeListener): KVStore

    fun edit(): Editor

    suspend fun has(key: String): Boolean

    suspend fun get(key: String, defaultValue: String): String

    suspend fun close()
}

suspend inline fun <R> KVStore.use(block: (KVStore) -> R): R {
    try {
        return block(this)
    } finally {
        close()
    }
}
