
# 日本語Rec
Android 🤖 | iOS 🍏 (Soon) | Desktop 🖥️

A simple cross-platform Japanese character recognition app.

<img width="584" height="565" alt="Screenshot_20261009_174557" src="https://github.com/user-attachments/assets/cbcc7c20-897e-4fcf-ab26-2e98f29626e1" />

<img width="270" height="606" alt="Screenshot_20261009_180211" src="https://github.com/user-attachments/assets/abb68c92-969f-4855-9a99-a856457f8e59" />

## Requirements
### Windows
The latest [Microsoft Visual C++ Redistributable](https://learn.microsoft.com/en-us/cpp/windows/latest-supported-vc-redist?view=msvc-170#latest-supported-redistributable-version) is required for onnxruntime inference. Without it, character predictions will not work. 

## About
I created this small app to learn Kotlin / Compose Multiplatform. As a React Native dev, this project did give me some headaches, but it was fun overall. It took me about 1-2 weeks to complete the app, mostly because I'm new to Kotlin and some documentation was either missing or difficult to find.

### ONNX Model
This app uses [Dariyooo](https://github.com/dariyooo)'s (DaAppLab) [DaKanji-Single-Kanji-Recognition](https://github.com/dariyooo/DaKanji-Single-Kanji-Recognition) onnx model. Please also check out their Japanese learning app: [DaKanji](https://github.com/dariyooo/DaKanji)!
