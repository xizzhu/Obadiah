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

package me.xizzhu.android.obadiah.sample

import android.app.Activity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.xizzhu.android.obadiah.KVStore
import me.xizzhu.android.obadiah.sqlite.KVSQLiteStore
import kotlin.coroutines.CoroutineContext

class MainActivity : Activity(), CoroutineScope {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val storeName = "storeName"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        StrictMode.enableDefaults()

        writeToStore()
        readFromStore()
    }

    private fun writeToStore() {
        launch {
            KVStore.createInstance(this@MainActivity, storeName).edit()
                    .put("key1", "value1")
                    .put("key2", "value2")
                    .commit()
            Log.i(TAG, "Written to store")
        }
    }

    private fun readFromStore() {
        launch {
            val store: KVStore = KVStore.createInstance(this@MainActivity, storeName)
            Log.i(TAG, "Reading from store...")
            Log.i(TAG, "'key1' = " + store.get("key1", ""))
            Log.i(TAG, "'key2' = " + store.get("key2", ""))
            Log.i(TAG, "'non-exist' = " + store.get("non-exist", "random default value"))
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}
