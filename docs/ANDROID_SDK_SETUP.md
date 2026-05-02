# Android SDK Setup

이 문서는 FITT 프로젝트를 실행하기 위해 Android SDK를 설치하고 프로젝트에 연결하는 방법을 정리합니다.

## 1. JDK와 Android SDK 차이

Android 프로젝트 실행에는 JDK와 Android SDK가 모두 필요합니다.

```text
JDK         Gradle, Kotlin, Java 컴파일 실행용
Android SDK Android API, build-tools, platform-tools, emulator 제공
```

`local.properties`의 `sdk.dir`에는 JDK 경로가 아니라 Android SDK 경로가 들어가야 합니다.

올바른 예:

```properties
sdk.dir=C\:\\Users\\GlassWorld\\AppData\\Local\\Android\\Sdk
```

잘못된 예:

```properties
sdk.dir=C\:\\Users\\GlassWorld\\.jdks\\openjdk-26.0.1
```

## 2. Android SDK 설치

1. Android Studio를 엽니다.
2. `File` > `Settings`로 이동합니다.
3. `Appearance & Behavior` > `System Settings` > `Android SDK`를 엽니다.
4. 상단의 `Android SDK Location`을 확인합니다.
5. `SDK Platforms` 탭에서 `Android 14.0 (API 34)`를 체크합니다.
6. `SDK Tools` 탭에서 아래 항목을 체크합니다.
   - `Android SDK Build-Tools`
   - `Android SDK Platform-Tools`
   - `Android Emulator`
   - `Android SDK Command-line Tools`
7. `Apply`를 눌러 설치합니다.

현재 프로젝트 기준 권장 SDK 경로:

```text
C:\Users\GlassWorld\AppData\Local\Android\Sdk
```

## 3. 프로젝트 SDK 경로 설정

프로젝트 루트의 `local.properties`를 확인합니다.

```properties
sdk.dir=C\:\\Users\\GlassWorld\\AppData\\Local\\Android\\Sdk
```

Android Studio에서 SDK 위치를 변경했다면, `local.properties`의 `sdk.dir` 값도 같은 경로로 맞춰야 합니다.

## 4. Gradle JDK 설정

Android SDK와 별도로 Gradle JDK도 설정해야 합니다.

1. Android Studio에서 `File` > `Settings`로 이동합니다.
2. `Build, Execution, Deployment` > `Build Tools` > `Gradle`을 엽니다.
3. `Gradle JDK`를 JDK 17로 설정합니다.

현재 PC에서 사용할 수 있는 JDK 예:

```text
C:\Users\GlassWorld\.jdks\corretto-17.0.19
```

## 5. Project Structure에서 Android SDK 선택

`Please select Android SDK` 오류가 뜨면 아래를 확인합니다.

1. `File` > `Project Structure...`를 엽니다.
2. `Project Settings` > `Project`로 이동합니다.
3. `SDK`를 `Android API 34`로 선택합니다.
4. 없다면 `Platform Settings` > `SDKs`에서 `+`를 눌러 Android SDK를 추가합니다.
5. SDK Home Path는 Android SDK 경로를 선택합니다.

```text
C:\Users\GlassWorld\AppData\Local\Android\Sdk
```

## 6. 빌드 확인

PowerShell에서 프로젝트 루트로 이동한 뒤 실행합니다.

```powershell
.\gradlew.bat :app:assembleDebug
```

빌드가 성공하면 아래처럼 표시됩니다.

```text
BUILD SUCCESSFUL
```

Debug APK는 프로젝트 내부의 아래 경로에 생성됩니다.

```text
app\build\outputs\apk\debug\app-debug.apk
```

APK 생성과 설치 방법은 [APK_BUILD.md](APK_BUILD.md)를 참고합니다.

## 7. 에뮬레이터로 실행

1. Android Studio에서 `Device Manager`를 엽니다.
2. `Create Virtual Device`를 누릅니다.
3. `Pixel 7` 또는 `Pixel 8`을 선택합니다.
4. 시스템 이미지는 `API 34`를 선택합니다.
5. 에뮬레이터를 실행합니다.
6. 상단 실행 대상에서 에뮬레이터를 선택하고 `Run`을 누릅니다.

터미널에서 설치하려면:

```powershell
.\gradlew.bat installDebug
```

## 8. 자주 나는 오류

### `Please select Android SDK`

Android Studio가 프로젝트 SDK를 Android SDK로 인식하지 못한 상태입니다.

확인할 곳:

```text
File > Project Structure > Project > SDK
```

`Android API 34`를 선택합니다.

### `SDK location not found`

`local.properties`의 `sdk.dir`가 없거나 잘못된 경로입니다.

```properties
sdk.dir=C\:\\Users\\GlassWorld\\AppData\\Local\\Android\\Sdk
```

### `JAVA_HOME is not set`

JDK가 PATH에 없거나 Gradle JDK가 잡히지 않은 상태입니다.

Android Studio의 `Gradle JDK`를 JDK 17로 설정합니다.

### SDK 경로에 `.jdks`가 들어가 있는 경우

`.jdks`는 JDK 설치 위치입니다. Android SDK 경로가 아닙니다.

```text
잘못됨: C:\Users\GlassWorld\.jdks\openjdk-26.0.1
올바름: C:\Users\GlassWorld\AppData\Local\Android\Sdk
```
