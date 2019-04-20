Obadiah
=======

[![Build Status](https://img.shields.io/travis/xizzhu/Obadiah.svg)](https://travis-ci.org/xizzhu/Obadiah)
[![Coverage Status](https://img.shields.io/coveralls/github/xizzhu/Obadiah.svg)](https://coveralls.io/github/xizzhu/Obadiah)
[![API](https://img.shields.io/badge/API-21%2B-green.svg?style=flat)](https://developer.android.com/about/versions/android-5.0.html)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![JitPack](https://img.shields.io/jitpack/v/github/xizzhu/Obadiah.svg)](https://jitpack.io/#xizzhu/Obadiah)

Yet another simple key-value store for Android.

You can find the latest release notes at: [CHANGELOG.md](CHANGELOG.md)

Download
--------
For *Gradle* users, add the following to your `build.gradle`:
```gradle
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation "com.github.xizzhu:Obadiah:0.1.0"
}
```

Or to your `build.gradle.kts`:
```gradle
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.xizzhu:Obadiah:0.1.0")
}
```

For *Maven* and others, see instructions [here](https://jitpack.io/#xizzhu/Obadiah).

Usage
-----
* Open store:
```kotlin
val kvStore = KVStore.createInstance(this@MainActivity)

// remember to close after usage
launch {
  kvStore.close()
}

// or use the use() extension function
launch {
  kvStore.use {
    // the store will be automatically closed when this returns
  }
}
```

* Read values from store:
```kotlin
launch {
  KVStore.createInstance(this@MainActivity).use {
    Log.i(TAG, "'key' = " + it.get("key", "defaultValue"))
  }
}
```

* Write values to store:
```kotlin
launch {
  KVStore.createInstance(this@MainActivity).use {
    it.edit().put("key1", "value1").put("key2", "value2").commit()
  }
}
```

License
-------
    Copyright (C) 2019 Xizhi Zhu

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
