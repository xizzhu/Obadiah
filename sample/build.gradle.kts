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

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileOptions {
        sourceCompatibility = Versions.java
        targetCompatibility = Versions.java
    }

    buildToolsVersion(Versions.Sdk.buildTools)
    compileSdkVersion(Versions.Sdk.compile)

    defaultConfig {
        applicationId = Configurations.sampleApplicationId

        minSdkVersion(Versions.Sdk.min)
        targetSdkVersion(Versions.Sdk.target)

        versionCode = Versions.SampleApp.code
        versionName = Versions.SampleApp.name
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
    }

    packagingOptions {
        exclude("META-INF/atomicfu.kotlin_module")
    }
}

dependencies {
    implementation(project(":lib"))

    implementation(Dependencies.Kotlin.stdlib)
    implementation(Dependencies.Kotlin.coroutinesAndroid)
}
