# APK Build Guide

이 문서는 FITT 프로젝트에서 APK 파일을 생성하고 위치를 확인하는 방법을 정리합니다.

## 1. APK 생성 명령

PowerShell에서 프로젝트 루트로 이동한 뒤 실행합니다.

```powershell
.\gradlew.bat :app:assembleDebug
```

빌드가 성공하면 아래 문구가 표시됩니다.

```text
BUILD SUCCESSFUL
```

## 2. APK 생성 위치

Debug APK는 프로젝트 내부의 아래 경로에 생성됩니다.

```text
app\build\outputs\apk\debug\app-debug.apk
```

전체 경로:

```text
C:\Users\GlassWorld\IdeaProjects\gw-fitt\app\build\outputs\apk\debug\app-debug.apk
```

현재 프로젝트 기준으로 이 파일이 앱 설치 파일입니다.

## 3. APK 파일 확인

PowerShell에서 확인하려면:

```powershell
Get-ChildItem .\app\build\outputs\apk\debug\
```

예상 파일:

```text
app-debug.apk
output-metadata.json
```

## 4. 에뮬레이터 또는 기기에 설치

에뮬레이터나 USB 디버깅이 켜진 Android 기기가 연결되어 있다면 아래 명령으로 설치할 수 있습니다.

```powershell
.\gradlew.bat installDebug
```

직접 APK를 설치하려면 `adb`를 사용할 수도 있습니다.

```powershell
C:\Users\GlassWorld\AppData\Local\Android\Sdk\platform-tools\adb.exe install -r .\app\build\outputs\apk\debug\app-debug.apk
```

## 5. 빌드 전 확인할 것

`local.properties`에 Android SDK 경로가 잡혀 있어야 합니다.

```properties
sdk.dir=C\:\\Users\\GlassWorld\\AppData\\Local\\Android\\Sdk
```

PowerShell에서 Java를 못 찾는다면 JDK를 임시로 잡고 빌드합니다.

```powershell
$env:JAVA_HOME="C:\Users\GlassWorld\.jdks\corretto-17.0.19"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat :app:assembleDebug
```

## 6. Release APK가 필요한 경우

현재 문서는 개발용 Debug APK 기준입니다.

Release APK는 별도 서명 설정이 필요합니다. 서명 설정 없이 아래 명령만 실행하면 배포용 APK로는 부족할 수 있습니다.

```powershell
.\gradlew.bat :app:assembleRelease
```
