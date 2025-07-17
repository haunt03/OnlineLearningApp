plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.onlinelearningapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.onlinelearningapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // THÊM KHỐI packagingOptions NÀY VÀO ĐÂY (Cú pháp Kotlin DSL)
    packagingOptions {
        resources {
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LGPL2.1"
            excludes += "META-INF/ASL2.0"
            excludes += "META-INF/*.kotlin_module"
        }
    }
}

dependencies {
    // AndroidX
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation ("com.google.android.material:material:1.12.0")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    // Sử dụng kapt cho annotationProcessor trong Kotlin DSL
    // Nếu bạn đang dùng plugin 'kotlin-kapt', hãy dùng kapt thay vì annotationProcessor
    // Nếu không, hãy đảm bảo bạn đã thêm plugin 'kotlin-kapt'
    // plugins { id 'org.jetbrains.kotlin.android' id 'kotlin-kapt' }
    // kapt("androidx.room:room-compiler:2.6.1")
    // Nếu bạn không dùng Kotlin, thì annotationProcessor vẫn đúng
    annotationProcessor("androidx.room:room-compiler:2.6.1")


    // Lifecycle - ViewModel & LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")

    // RecyclerView
    implementation ("androidx.recyclerview:recyclerview:1.3.2")

    // Glide - image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // JavaMail API
    implementation ("com.sun.mail:android-mail:1.6.7")
    implementation ("com.sun.mail:android-activation:1.6.7")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
