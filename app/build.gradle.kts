plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.loginproject"
    compileSdk = 34

    buildFeatures {
        dataBinding = true // Habilitar Data Binding
        viewBinding = true // Habilitar View Binding
    }

    defaultConfig {
        applicationId = "com.example.loginproject"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    //nuevas dependencias
    implementation(libs.circleimageview)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.auth)
    // Dependencias de Glide
    implementation(libs.glide)
    implementation(libs.firebase.firestore)
    implementation(libs.lombok) // Glide
    annotationProcessor(libs.glide.compiler) // Glide Compiler


    implementation(libs.lombok)
    annotationProcessor(libs.lombok)


    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}