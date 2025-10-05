# Flashlight App (Ocean Professional)

## Overview
This is a simple Android flashlight application that provides a single, centered toggle button to switch the device torch on and off. The app is implemented in Kotlin, uses the Camera2 API via CameraManager.setTorchMode, and follows a clean, modern “Ocean Professional” theme. It requests camera permission at runtime on Android 6.0 (API 23) and above, and handles the absence of a camera flash gracefully by disabling the toggle and displaying an informative message.

## Features
- Single central toggle button that switches the flashlight on and off using CameraManager.setTorchMode.
- Runtime CAMERA permission request for Android 6.0+ (API 23+) with a clear message when permission is required.
- Graceful handling for devices without a flash: the toggle button is disabled and a “No flashlight available on this device” message is shown.
- Live torch state updates using CameraManager.TorchCallback to keep the UI consistent with system torch state.
- Portrait-only orientation with minimal UI and subtle fade animations to reinforce state changes.
- AppCompat/Material-friendly approach with an “Ocean Professional” palette.

## Design Theme (Ocean Professional)
The UI follows a modern, minimalist aesthetic with rounded shapes, subtle shadows, and smooth transitions.

Color palette used across the app:
- Primary: #2563EB
- Secondary/Success: #F59E0B
- Error: #EF4444
- Background: #f9fafb
- Surface: #ffffff
- Text: #111827

Implementation details:
- Theme: Theme.OceanProfessional (AppCompat Light NoActionBar) defined in res/values/themes.xml and applied in AndroidManifest.xml.
- Colors: Defined in res/values/colors.xml and referenced by theme, backgrounds, and button styles.
- Background: Subtle gradient surface background (res/drawable/bg_surface.xml).
- Button: Large circular toggle button with ocean colors and selected state (res/drawable/bg_button_ocean.xml).
- Icons: Vector drawables for flash on/off (res/drawable/ic_flash_on_24.xml, res/drawable/ic_flash_off_24.xml).
- Layout: activity_main.xml centers the icon, button, and status text using ConstraintLayout.
- Animations: Subtle fade-in/fade-out animations on toggle (res/anim/fade_in.xml, res/anim/fade_out.xml).

## Permissions and Privacy
- Required permissions:
  - CAMERA (android.permission.CAMERA) is requested at runtime on API 23+ to control the torch via Camera2.
  - FLASHLIGHT (android.permission.FLASHLIGHT) is declared for broader OEM compatibility, although it is deprecated on many API levels. The app primarily uses CameraManager.setTorchMode.
- Declared features:
  - android.hardware.camera and android.hardware.camera.flash are marked as android:required="false" so that devices without a camera or flash can still install the app; the app will gracefully disable the toggle in that case.
- Privacy: The app does not capture images or video. Camera permission is only used to control the device torch. No data is collected or transmitted.

## Build and Run
This project uses the Declarative Gradle DSL with the experimental Android ecosystem plugin.

Common commands:
- Build debug APK:
  - ./gradlew :app:assembleDebug
- Install on a connected device (USB or emulator):
  - ./gradlew :app:installDebug

After installation, locate the “Flashlight” app on your device and launch it. On first run on Android 6.0+ you will be prompted to grant the Camera permission.

Project configuration highlights:
- Module: flashlight_app_frontend/app
- Min SDK: 23
- Compile SDK: 34
- Kotlin/AndroidX/AppCompat and ConstraintLayout are used for the UI.

## Testing on Device
- Physical device recommended: Most emulators do not emulate the camera flash/torch hardware. As a result, torch toggling may not function in emulators even if the app installs and runs. Use a physical Android device with a back camera and flash for accurate behavior.
- First use: When you press the toggle button the first time on Android 6.0+ and the app does not yet have permission, it will request CAMERA permission. Grant it to enable torch control.
- Torch state updates: If an external action changes the torch state (e.g., quick settings tile), the app listens via TorchCallback and updates the UI to reflect the current state.

## Troubleshooting
- Button disabled with “No flashlight available”:
  - Your device may not have a hardware flash, or the app could not find a suitable camera with flash. This is expected for devices without flash and for most emulators.
- Permission denied:
  - If you deny the Camera permission, the app disables the toggle. To enable it later, go to System Settings > Apps > Flashlight > Permissions and grant the Camera permission, then relaunch the app.
- Torch not toggling:
  - Ensure you are using a physical device with a flash.
  - Verify Camera permission is granted.
  - Try closing the app and reopening it to reinitialize CameraManager.
- Emulator limitations:
  - Emulators generally lack torch hardware; the toggle will not turn on a flash. Use a real device for end-to-end validation.

## Notes
- Orientation: The activity is locked to portrait.
- Safe shutdown: On destroy, the app attempts to turn the torch off as a safety measure.
- APIs used:
  - CameraManager.setTorchMode for torch control.
  - CameraManager.TorchCallback for live updates.
  - Activity Result API for runtime permission request flow.
- Accessibility and UX:
  - Clear status text displays “Flashlight is ON/OFF”.
  - The toggle is disabled when flash is unavailable, preventing confusion.
- Theme adherence:
  - The “Ocean Professional” palette is applied through AppCompat theme, colors, and drawables to maintain a cohesive, professional look and feel.
