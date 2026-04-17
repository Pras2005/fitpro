# FitPlan Android App

This workspace now contains a native Android app in Kotlin using Jetpack Compose and Material 3, based on the `FitPlan-Smart-Workout-Planner.pdf` brief.

## What it does

- Accepts height and weight input
- Calculates BMI instantly
- Maps BMI to a body category
- Recommends a bulk, cut, or maintain strategy
- Generates a weekly workout split tailored to the category
- Splits the app into `Home`, `Insights`, and `Plan` screens with bottom navigation
- Uses a more polished visual style with gradients, layered cards, and stronger section hierarchy

## Project structure

- `app/src/main/java/com/fitplan/app/MainActivity.kt`: main UI and workout-planning logic
- `app/src/main/java/com/fitplan/app/ui/theme`: Material 3 theme setup
- `app/src/main/res`: manifest, launcher icon, and Android resources

## Open in Android Studio

1. Open `/home/admin/Desktop/mad3` in Android Studio.
2. Let Android Studio download the Gradle distribution and Android dependencies.
3. Run the `app` configuration on an emulator or device.

## Notes

- Local Gradle tooling is not installed in this environment, so the project was scaffolded without executing a full Android build here.
