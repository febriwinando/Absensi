plugins {
    alias(libs.plugins.android.application)
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile =
                file("/Users/nebula/Documents/Aplikasi Android/Salinan FaceAntiSpoofingMobile-main - Dev 1/appstebingtinggi.jks")
            storePassword = "123456"
            keyAlias = "key123456"
            keyPassword = "123456"
        }
        create("release") {
            storeFile =
                file("/Users/nebula/Documents/Aplikasi Android/Salinan FaceAntiSpoofingMobile-main - Dev 1/appstebingtinggi.jks")
            storePassword = "123456"
            keyAlias = "key123456"
            keyPassword = "123456"
        }
    }
    namespace = "go.pemkott.appsandroidmobiletebingtinggi"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "go.pemkott.appsandroidmobiletebingtinggi"
        minSdk = 24
        targetSdk = 36
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.pytorch.android.lite)
    implementation(libs.pytorch.android.torchvision.lite)
    implementation(libs.app.update)
    implementation(libs.face.detection)
    implementation(libs.glide)
    implementation(libs.logging.interceptor)
    implementation(libs.places)
    implementation(libs.circleimageview)
    implementation(libs.recyclerview)
    implementation(libs.lifecycle.extensions)
    implementation(libs.cardview)
    implementation(libs.browser)
    implementation(libs.play.services.auth.api.phone)
    implementation(libs.play.services.auth)
    implementation(libs.annotations)
    implementation(libs.multidex)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.volley)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.runner)
    implementation(libs.shimmer)
    implementation(libs.converter.gson)
    implementation(libs.firebase.bom)
    implementation(libs.swiperefreshlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
}