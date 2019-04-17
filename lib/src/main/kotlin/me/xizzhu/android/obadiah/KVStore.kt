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

interface KVStore {
    suspend fun contains(key: String): Boolean

    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean

    suspend fun getDouble(key: String, defaultValue: Double): Double

    suspend fun getFloat(key: String, defaultValue: Float): Float

    suspend fun getInt(key: String, defaultValue: Int): Int

    suspend fun getLong(key: String, defaultValue: Long): Long

    suspend fun getString(key: String, defaultValue: String): String

    suspend fun close()
}
