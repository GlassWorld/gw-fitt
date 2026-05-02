# Android Emulator Setup

이 문서는 FITT 프로젝트를 Android Studio에서 에뮬레이터로 실행하기 위한 최소 설정 절차입니다.

## 1. Android SDK 설치 확인

1. Android Studio를 엽니다.
2. `File` > `Settings` > `Appearance & Behavior` > `System Settings` > `Android SDK`로 이동합니다.
3. `SDK Platforms` 탭에서 `Android 14.0 (API 34)`를 체크합니다.
4. `SDK Tools` 탭에서 아래 항목을 체크합니다.
   - `Android SDK Build-Tools`
   - `Android SDK Platform-Tools`
   - `Android Emulator`
5. `Apply`를 눌러 설치합니다.

기본 SDK 경로는 Windows에서 보통 아래와 같습니다.

```properties
C:\Users\GlassWorld\AppData\Local\Android\Sdk
```

이 프로젝트의 `local.properties`도 위 경로를 사용하도록 설정되어 있습니다.

> Android Studio의 `Android SDK Location` 값도 이 경로와 같아야 합니다.

## 2. 에뮬레이터 생성

1. Android Studio 오른쪽 사이드바 또는 상단 메뉴에서 `Device Manager`를 엽니다.
2. `+` 버튼 또는 `Create Virtual Device`를 클릭합니다.
3. 기기 목록에서 `Pixel 7` 또는 `Pixel 8`을 선택하고 `Next`를 누릅니다.
4. 시스템 이미지에서 `API 34`를 선택합니다.
5. 이미지가 설치되어 있지 않다면 `Download`를 누른 뒤 설치를 완료합니다.
6. `Next`를 누르고 설정 화면에서 이름을 확인합니다.
7. `Finish`를 눌러 에뮬레이터 생성을 완료합니다.

## 3. 에뮬레이터 실행

1. `Device Manager`에서 생성한 기기 오른쪽의 실행 버튼을 누릅니다.
2. 에뮬레이터가 완전히 부팅될 때까지 기다립니다.
3. Android Studio 상단 실행 대상 드롭다운에서 해당 에뮬레이터를 선택합니다.

## 4. 앱 실행

Android Studio에서 상단의 `Run` 버튼을 누르거나 `Shift + F10`을 누릅니다.

터미널에서 설치하려면 다음 명령을 사용할 수 있습니다.

```powershell
.\gradlew.bat installDebug
```

빌드만 확인하려면 다음 명령을 사용합니다.

```powershell
.\gradlew.bat :app:assembleDebug
```

## 5. 자주 막히는 부분

### JAVA_HOME 또는 java를 찾지 못하는 경우

이 프로젝트는 JDK 17 사용을 권장합니다. Android Studio에서 아래 메뉴를 확인합니다.

1. `File` > `Settings` > `Build, Execution, Deployment` > `Build Tools` > `Gradle`
2. `Gradle JDK`를 JDK 17로 설정합니다.

현재 PC에는 아래 JDK가 설치되어 있습니다.

```text
C:\Users\GlassWorld\.jdks\corretto-17.0.19
```

### SDK 경로 오류가 나는 경우

`local.properties`의 `sdk.dir`가 Android SDK 경로인지 확인합니다.

```properties
sdk.dir=C\:\\Users\\GlassWorld\\AppData\\Local\\Android\\Sdk
```

JDK 경로가 들어가 있으면 Gradle Sync 또는 앱 실행이 실패할 수 있습니다.

### 실행 대상이 없다고 나오는 경우

`Device Manager`에서 에뮬레이터가 켜져 있는지 확인합니다. 에뮬레이터가 느리게 뜨는 경우 첫 부팅에 시간이 걸릴 수 있습니다.
