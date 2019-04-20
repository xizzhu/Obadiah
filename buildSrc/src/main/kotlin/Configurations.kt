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

import org.gradle.api.JavaVersion

object Configurations {
    val sampleApplicationId = "me.xizzhu.android.obadiah.sample"
}

object Versions {
    object SampleApp {
        const val code = 100
        const val name = "0.1.0"
    }

    object AndroidMaven {
        const val classpath = "2.1"
    }

    object Coveralls {
        const val classpath = "2.8.2"
    }

    object Sdk {
        const val classpath = "3.4.0"
        const val buildTools = "28.0.3"
        const val compile = 28
        const val min = 21
        const val target = 28
    }

    val java = JavaVersion.VERSION_1_8

    object Kotlin {
        const val classpath = "1.3.30"
        const val core = "1.3.30"
        const val coroutines = "1.2.0"
    }

    object AndroidX {
        const val annotation = "1.0.1"

        const val junit = "1.1.0"
        const val testRules = "1.1.1"
    }

    const val mockito = "2.27.0"
}

object Dependencies {
    object Sdk {
        const val classpath = "com.android.tools.build:gradle:${Versions.Sdk.classpath}"
    }

    object AndroidMaven {
        const val classpath = "com.github.dcendents:android-maven-gradle-plugin:${Versions.AndroidMaven.classpath}"
    }

    object Coveralls {
        const val classpath = "org.kt3k.gradle.plugin:coveralls-gradle-plugin:${Versions.Coveralls.classpath}"
    }

    object Kotlin {
        const val classpath = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin.classpath}"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.Kotlin.core}"
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Kotlin.coroutines}"

        const val test = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.Kotlin.core}"
        const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.Kotlin.coroutines}"
    }

    object AndroidX {
        const val annotation = "androidx.annotation:annotation:${Versions.AndroidX.annotation}"

        const val junit = "androidx.test.ext:junit:${Versions.AndroidX.junit}"
        const val testRules = "androidx.test:rules:${Versions.AndroidX.testRules}"
        const val testRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    object Mockito {
        const val mockito = "org.mockito:mockito-core:${Versions.mockito}"
    }
}
