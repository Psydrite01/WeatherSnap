# WeatherSnap

## Live Weather Reporting with Camera Evidence

WeatherSnap is an Android application that combines real-time weather data with photographic evidence to create comprehensive weather reports. Users can search for any location, capture current conditions with their device camera, add contextual notes, and save reports for future reference.

![WeatherSnap Screenshots](screenshots/weather_snap_demo.gif) <!-- Add actual screenshots if available -->

## ✨ Features

- **Accurate Weather Data**: Powered by Open-Meteo API for reliable geocoding and forecast information
- **Camera Integration**: Native camera access with automatic image optimization and compression
- **Smart Image Processing**: Automatic EXIF orientation correction, downscaling to 1280px max dimension, and quality compression (~60%)
- **Rich Reporting**: Combine weather metrics, photographic evidence, and field notes in each report
- **Local Persistence**: Saved reports stored securely using Room database
- **Modern UI**: Beautiful Material Design 3 interface built with Jetpack Compose
- **Offline First**: Recent searches cached for 24 hours to reduce API calls
- **Permission Handling**: Graceful runtime camera permission management

## 📱 Screens

| Weather Search | Create Report | Saved Reports |
|:--------------:|:-------------::|:-------------:|
| ![Weather Screen](screenshots/weather_screen.png) | ![Create Report](screenshots/create_report.png) | ![Saved Reports](screenshots/saved_reports.png) |

## 🏗️ Architecture

WeatherSnap follows modern Android architecture principles:

```
Presentation Layer (UI)
    ↓
ViewModels (WeatherViewModel, ReportsViewModel)
    ↓
Domain Layer (Use Cases, Models)
    ↓
Data Layer (Repositories, Remote/Data Sources)
    ↓
Local Database (Room) & Network (Retrofit)
```

### Key Components
- **Jetpack Compose**: Declarative UI toolkit
- **Hilt**: Dependency injection framework
- **Coroutines**: Asynchronous programming
- **Room**: Local data persistence
- **Retrofit**: Type-safe HTTP client
- **CameraX**: Camera functionality abstraction
- **Coil**: Image loading library
- **Accompanist**: Runtime permissions handling

## 🔧 Technical Details

### API Integration
- **Geocoding API**: `https://geocoding-api.open-meteo.com/v1/search`
- **Weather API**: `https://api.open-meteo.com/v1/forecast`

### Data Models
- **CityResult**: Location information from geocoding service
- **WeatherInfo**: Complete weather data including current conditions, hourly & daily forecasts
- **ReportEntity**: Saved report with weather data, compressed image, notes, and metadata
- **CompressedImageResult**: Original vs compressed image size tracking

### Image Processing Pipeline
1. Capture raw image via CameraX
2. Read and correct EXIF orientation
3. Downscale to max 1280px on longest edge
4. Compress to ~60% quality JPEG
5. Store via FileProvider for secure sharing
6. Track original vs compressed sizes for reporting

## 📦 Dependencies

### Core Libraries
- **AndroidX Compose BOM**: 2026.02.01
- **Kotlin**: 2.0.21
- **Retrofit**: 2.11.0
- **Room**: 2.6.1
- **Hilt**: 2.53.1
- **CameraX**: 1.3.4
- **Coil**: 2.7.0
- **Accompanist Permissions**: 0.34.0

### Development Tools
- **Android Gradle Plugin**: 8.13.2
- **KSP**: 2.0.21-1.0.28
- **Jetpack Compose Compiler**: Integrated with Kotlin 2.0.21

## 🚀 Getting Started

### Prerequisites
- Android Studio Flamingo or later
- JDK 11
- Android SDK 36 (compileSdk)
- Minimum SDK 26 (minSdk)

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/WeatherSnap.git
   ```
2. Open in Android Studio
3. Sync Gradle dependencies
4. Run on emulator or physical device (API 26+)

### Configuration
The app requires internet access for weather data and camera permissions for photo capture. No API keys are needed as it uses the free Open-Meteo service.

## 🧪 Testing

### Unit Tests
ViewModels and repository logic are unit-tested using JUnit and Mockito.

### Instrumented Tests
UI tests verify navigation, input handling, and camera integration using Espresso and Compose Testing.

Run tests:
```bash
./gradlew testDebugUnitTest
./gradlew connectedAndroidTest
```

## 📱 Permissions

The app requests only essential permissions:
- **CAMERA**: For capturing weather evidence photos
- **INTERNET**: For fetching weather data from Open-Meteo API

Permissions are requested at runtime with clear explanations.

## 🔒 Privacy & Security

- No personal data is collected or transmitted beyond weather API calls
- Images are stored locally in app-specific cache directories
- Reports remain on device unless explicitly shared by user
- Network calls use HTTPS for data-in-transit protection
- FileProvider ensures secure image sharing between components

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgements

- [Open-Meteo](https://open-meteo.com/) for providing free weather API
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern UI toolkit
- [CameraX](https://developer.android.com/training/camerax) for camera abstraction
- [Coil](https://coil-kt.github.io/coil/) for image loading
- [Accompanist](https://github.com/google/accompanist) for permission handling

## 📞 Support

For issues, feature requests, or contributions:
1. Check existing [issues](https://github.com/yourusername/WeatherSnap/issues)
2. Submit new issues with detailed reproduction steps
3. Follow standard GitHub flow for pull requests

---

*Intern Assessment Project: WeatherSnap*
*Built with Kotlin & Jetpack Compose*