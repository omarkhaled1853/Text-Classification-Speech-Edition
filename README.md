# Text Classification (Speech Edition) Android Demo

### Overview

This App will accept text entered into a field and classify it as either
positive or negative with a provided confidence score. The supported
classification models include Word Vector and MobileBERT.

And it will also speech the classification either postive or negative
based on the provided confidence score

if postive confidence score with more than 0.5 it'll speech "Postive"

if negative confidence score with more than 0.5 it'll speech "Negative"

These instructions walk you through building and running the demo on an Android
device.

The model files are downloaded via Gradle scripts when you build and run the
app. You don't need to do any steps to download TFLite models into the project
explicitly.

https://github.com/user-attachments/assets/27247e59-7e14-423e-8f5b-2e3952ffcb5c

## Build the demo using Android Studio

### Prerequisites

* The **[Android Studio](https://developer.android.com/studio/index.html)** IDE.
  This sample has been tested on Android Studio Chipmunk.

* A physical or emulated Android device with a minimum OS version of SDK 21
  (Android 5.0) with developer mode enabled. The process of enabling
  developer mode may vary by device.

### Building

* Open Android Studio. From the Welcome screen, select Open an existing
    Android Studio project.

* From the Open File or Project window that appears, navigate to and select
    the mediapipe/examples/text_classification/android directory.
    Click OK.

* If it asks you to do a Gradle Sync, click OK.

* With your Android device connected developer mode
    enabled, click on the green Run arrow in Android Studio.

### Models used

Downloading, extraction, and placing the models into the assets folder is
managed automatically by the download_model.gradle file.
