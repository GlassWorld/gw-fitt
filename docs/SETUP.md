# FITT 개발 환경 설정 가이드

## 사전 요구사항

| 항목 | 최소 버전 | 권장 버전 |
|---|---|---|
| Android Studio | Hedgehog (2023.1.1) | Iguana (2023.2.1) 이상 |
| JDK | 17 | 17 |
| Android SDK | API 26 | API 34 |
| Gradle | 8.7 | 8.7 |
| Kotlin | 1.9.24 | 1.9.24 |

---

## 1. 프로젝트 클론

```bash
git clone https://github.com/your-org/gw-fitt.git
cd gw-fitt
```

---

## 2. Android Studio에서 열기

1. Android Studio 실행
2. **File → Open** 선택
3. 클론한 `gw-fitt` 폴더 선택
4. **OK** 클릭
5. Gradle sync가 자동으로 시작됩니다 (최초 1~3분 소요)

> Gradle sync가 자동으로 시작되지 않으면 상단 툴바의 **🐘 Sync Project with Gradle Files** 버튼을 클릭하세요.

---

## 3. SDK 설정 확인

Android Studio에서 SDK가 설치되어 있지 않다면:

1. **File → Settings → Appearance & Behavior → System Settings → Android SDK**
2. **SDK Platforms** 탭에서 **Android 8.0 (API 26)** 이상 체크
3. **SDK Tools** 탭에서 **Android SDK Build-Tools** 최신 버전 설치
4. **Apply** → **OK**

---

## 4. 에뮬레이터 or 실기기 연결

### 에뮬레이터 생성
1. **Device Manager** (우측 사이드바) → **+** → **Create Virtual Device**
2. **Pixel 7** (권장) 선택 → **Next**
3. **API 34 (Android 14)** 선택 → **Download** (미설치 시) → **Next**
4. **Finish**

### 실기기 사용
1. 기기의 **개발자 옵션 → USB 디버깅** 활성화
2. USB 연결 후 상단 기기 드롭다운에서 기기 선택

---

## 5. 빌드 & 실행

```bash
# 디버그 APK 빌드
./gradlew assembleDebug

# 연결된 기기에 설치 및 실행
./gradlew installDebug
```

또는 Android Studio 상단의 **▶ Run** 버튼(Shift+F10) 클릭

---

## 6. 앱 초기 데이터

앱 최초 실행 시 Room DB `onCreate` 콜백으로 기본 운동 10개가 자동 삽입됩니다.

```
상체: 푸시업, 덤벨 숄더 프레스, 바벨 벤치 프레스
하체: 스쿼트, 런지, 레그 프레스
코어: 플랭크, 크런치
전신: 버피
유산소: 줄넘기
```

---

## 7. 주요 기능 흐름

### 루틴 만들기
```
루틴 탭 → + FAB → 이름/난이도/예상시간 입력 → 만들기
```

### 운동 타이머 사용
```
타이머 탭 → 세트 수 / 휴식 시간 설정 → ▶ 시작 → ⏭ 세트 완료 → 자동 휴식 타이머
```

### 운동 기록 확인
```
기록 탭 → 이번 주 요약 카드 + 일별 바 차트 → 전체 기록 목록
```

### AI 코치 사용
```
AI코치 탭 → 빠른 질문 칩 클릭 또는 직접 입력 → 응답 확인
```

---

## 8. 프로젝트 구조 한눈에 보기

```
app/src/main/java/com/gw/fitt/
├── data/           # Room DB, Retrofit, RepositoryImpl
├── domain/         # 순수 Kotlin 모델, Repository 인터페이스, UseCase
├── presentation/   # ViewModel + State + Screen (5개 피처)
├── ui/             # 공통 테마 및 컴포넌트
├── navigation/     # Screen sealed class, NavGraph, BottomNav
└── di/             # Hilt DI 모듈
```

---

## 9. 빌드 문제 해결

### Kapt 오류
```bash
./gradlew clean build --info
```

### Hilt 관련 오류
- `@HiltAndroidApp`이 `FittApplication`에 적용되어 있는지 확인
- `@AndroidEntryPoint`가 `MainActivity`에 적용되어 있는지 확인

### Room 스키마 오류
- Entity 변경 시 DB 버전을 올리고 `Migration`을 추가하거나
- 개발 중에는 `Room.databaseBuilder().fallbackToDestructiveMigration()` 사용 가능

### Gradle Sync 실패
1. **File → Invalidate Caches → Invalidate and Restart**
2. `~/.gradle/caches` 삭제 후 재시도

---

## 10. 의존성 버전 변경

모든 버전은 `gradle/libs.versions.toml`에서 중앙 관리됩니다.

```toml
[versions]
compose-bom = "2024.06.00"
hilt        = "2.51"
room        = "2.6.1"
retrofit    = "2.11.0"
```

버전 변경 후 **Gradle Sync** 실행 필요.
