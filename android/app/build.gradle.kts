plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("jacoco")
}

android {
    namespace = "com.bceassociates.livetarget"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bceassociates.livetarget"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "0.92"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.7.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    
    lint {
        abortOnError = false
        checkReleaseBuilds = true
        warningsAsErrors = false
        htmlReport = true
        xmlReport = true
    }
    
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
        animationsDisabled = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    // Camera dependencies
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    
    // Data persistence
    implementation(libs.androidx.datastore.preferences)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    
    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // Samsung Accessory SDK for Galaxy Watch integration
    // Note: Samsung Accessory SDK is not available in public Maven repositories
    // For CI/CD builds, we use stub implementations
    // implementation("com.samsung.android:accessory:1.4.0")
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// Jacoco Test Coverage Configuration
tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    
    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/databinding/**",
        "**/generated/**"
    )
    
    val debugTree = fileTree("${project.layout.buildDirectory.get().asFile}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }
    
    val mainSrc = "${project.projectDir}/src/main/java"
    
    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(project.layout.buildDirectory.get().asFile) {
        include("jacoco/testDebugUnitTest.exec")
    })
}