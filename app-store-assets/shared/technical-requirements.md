# Technical Requirements for App Store Submission

## iOS Technical Requirements

### Xcode Build Settings
```xml
<!-- Info.plist Updates Required -->
<key>CFBundleDisplayName</key>
<string>Precision Target Analysis</string>

<key>CFBundleIdentifier</key>
<string>com.bceassociates.precisiontarget</string>

<key>CFBundleShortVersionString</key>
<string>0.92</string>

<key>CFBundleVersion</key>
<string>1</string>

<key>LSApplicationCategoryType</key>
<string>public.app-category.sports</string>

<key>LSRequiresIPhoneOS</key>
<true/>

<key>UIRequiredDeviceCapabilities</key>
<array>
    <string>camera-flash</string>
    <string>auto-focus-camera</string>
    <string>armv7</string>
</array>

<key>UISupportedInterfaceOrientations</key>
<array>
    <string>UIInterfaceOrientationPortrait</string>
</array>

<key>UILaunchStoryboardName</key>
<string>LaunchScreen</string>

<!-- Age Rating -->
<key>ITSAppUsesNonExemptEncryption</key>
<false/>
```

### Build Configuration
```bash
# Build for Distribution
xcodebuild -project "Live Target.xcodeproj" \
           -scheme "Live Target" \
           -archivePath "PrecisionTargetAnalysis.xcarchive" \
           -configuration Release \
           archive

# Export for App Store
xcodebuild -exportArchive \
           -archivePath "PrecisionTargetAnalysis.xcarchive" \
           -exportPath "AppStore" \
           -exportOptionsPlist "ExportOptions.plist"
```

### Code Signing Requirements
- **Development Team:** [YOUR_TEAM_ID]
- **Bundle Identifier:** com.bceassociates.precisiontarget
- **Provisioning Profile:** App Store Distribution Profile
- **Certificate:** Apple Distribution Certificate

### App Store Connect Metadata
- **App Name:** Precision Target Analysis
- **SKU:** PTA-IOS-001
- **Bundle ID:** com.bceassociates.precisiontarget
- **Primary Language:** English (U.S.)
- **Price:** $4.99 (Tier 5) *[Adjust as needed]*
- **Availability:** All Countries

## Android Technical Requirements

### Build Configuration Updates

#### android/app/build.gradle.kts
```kotlin
android {
    namespace = "com.bceassociates.precisiontarget"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.bceassociates.precisiontarget"
        minSdk = 24  // Android 7.0
        targetSdk = 34  // Android 14
        versionCode = 1
        versionName = "0.92"
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    bundle {
        language {
            enableSplit = false
        }
    }
}
```

#### Signing Configuration
```kotlin
// android/app/build.gradle.kts
android {
    signingConfigs {
        create("release") {
            storeFile = file("../release-keystore.keystore")
            storePassword = System.getenv("STORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
}
```

#### Generate Release Keystore
```bash
# Generate release keystore (one time only)
keytool -genkey -v -keystore release-keystore.keystore \
        -alias precision-target-key \
        -keyalg RSA -keysize 2048 \
        -validity 10000 \
        -storepass [STRONG_PASSWORD] \
        -keypass [STRONG_PASSWORD]

# Build signed App Bundle
cd android
./gradlew bundleRelease
```

### Google Play Console Metadata
- **App Name:** Precision Target Analysis
- **Package Name:** com.bceassociates.precisiontarget
- **Category:** Sports
- **Target Audience:** Teen (Ages 13+)
- **Content Rating:** ESRB Teen, PEGI 12+
- **Price:** $4.99 USD *[Adjust as needed]*
- **Countries:** All Available Countries

## Content Rating Questionnaire Answers

### iOS App Store Rating
**Age Rating: 17+**
- **Violence:** Infrequent/Mild Realistic Violence (Sports Context)
- **Simulated Gambling:** No
- **Sexual Content:** No  
- **Profanity:** No
- **Medical/Treatment Information:** No
- **Drug/Alcohol References:** No

### Google Play Content Rating
**Target Audience: Teen (13+)**

**Violence Questionnaire:**
- Does your app contain violence? **NO**
- Does your app contain realistic violence? **NO** 
- Does your app contain fantasy violence? **NO**
- Does your app contain violence in a sports context? **YES**
  - "App is designed for competitive ballistic sports training and analysis"

**Other Content:**
- Sexual content: **NO**
- Profanity: **NO**
- Controlled substances: **NO**
- Gambling: **NO**
- User-generated content: **NO**

## Pre-Submission Checklist

### iOS Pre-Flight Checklist
- [ ] App builds without warnings on latest Xcode
- [ ] All device orientations work correctly  
- [ ] Camera permissions properly requested and explained
- [ ] Photo library permissions work correctly
- [ ] Apple Watch integration tested (if applicable)
- [ ] App works on physical device, not just simulator
- [ ] All screenshots meet size requirements
- [ ] App description follows App Store guidelines
- [ ] Privacy policy URL is accessible
- [ ] Support URL is functional
- [ ] Age rating matches content appropriately

### Android Pre-Flight Checklist  
- [ ] App Bundle builds successfully with latest Android Gradle Plugin
- [ ] Target SDK 33+ (Android 13) compliance verified
- [ ] Camera permissions properly requested and explained
- [ ] Storage permissions work correctly on Android 13+
- [ ] Samsung Galaxy Watch integration tested (if applicable)
- [ ] App works on physical device across different Android versions
- [ ] All screenshots meet size and quality requirements
- [ ] App description follows Google Play policies
- [ ] Privacy policy URL accessible and compliant
- [ ] Developer account in good standing
- [ ] Content rating questionnaire completed accurately

## Security & Compliance

### Security Requirements
- [ ] No hardcoded secrets or API keys
- [ ] All network communications use HTTPS (if applicable)
- [ ] User data handled according to privacy policy
- [ ] Permissions requested only when needed
- [ ] Local data storage follows platform best practices

### Legal Compliance
- [ ] Privacy policy covers all data collection and usage
- [ ] Terms of service appropriate for app functionality
- [ ] Age rating matches target audience and content
- [ ] Intellectual property rights verified
- [ ] No trademark or copyright violations
- [ ] GDPR compliance for EU users (even though no data collection)

### Performance Requirements
- [ ] App launches within 3 seconds
- [ ] Camera preview starts within 2 seconds
- [ ] No memory leaks during extended use
- [ ] Battery usage optimized for training sessions
- [ ] App responds smoothly to user interactions
- [ ] No crashes during typical usage scenarios