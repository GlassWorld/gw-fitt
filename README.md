# FITT

개인 맞춤 운동 관리 Android 앱

---

## 개요

| 항목 | 내용 |
|---|---|
| Package | `com.gw.fitt` |
| 언어 | Kotlin |
| 최소 SDK | API 26 (Android 8.0 Oreo) |
| 빌드 시스템 | Gradle Kotlin DSL |
| UI | Jetpack Compose + Material3 |
| 아키텍처 | Clean Architecture |

---

## 기술 스택

| 분류 | 라이브러리 | 버전 |
|---|---|---|
| UI | Jetpack Compose BOM | 2024.06.00 |
| 네비게이션 | Navigation Compose | 2.7.7 |
| DI | Hilt | 2.51 |
| DB | Room | 2.6.1 |
| 네트워크 | Retrofit2 | 2.11.0 |
| 네트워크 | OkHttp3 | 4.12.0 |
| 비동기 | Kotlinx Coroutines | 1.8.1 |
| 로컬 저장소 | DataStore Preferences | 1.1.1 |
| 차트 | Vico Compose | 1.13.1 |
| 시스템 UI | Accompanist | 0.34.0 |

---

## 화면 구성

| 탭 | 화면 | 기능 |
|---|---|---|
| 홈 | HomeScreen | 이번 주 운동 통계, 최근 루틴 목록 |
| 루틴 | RoutineScreen | 루틴 목록 조회, 생성(이름/난이도/시간), 삭제 |
| 타이머 | TimerScreen | 세트 타이머(초 단위), 자동 휴식 카운트다운, 세트/횟수 설정 |
| 기록 | LogScreen | 이번 주 요약 카드, 일별 바 차트, 전체 운동 기록 목록 |
| AI코치 | CoachScreen | 키워드 기반 운동 조언 챗봇, 빠른 질문 칩 |

---

## 프로젝트 구조

```
com.gw.fitt/
├── data/
│   ├── local/
│   │   ├── entity/               Room Entity (4개)
│   │   │   ├── ExerciseEntity
│   │   │   ├── RoutineEntity
│   │   │   ├── RoutineExerciseEntity
│   │   │   └── WorkoutLogEntity
│   │   ├── dao/                  DAO Interface (4개)
│   │   │   ├── ExerciseDao
│   │   │   ├── RoutineDao
│   │   │   ├── RoutineExerciseDao  (JOIN 쿼리 포함)
│   │   │   └── WorkoutLogDao
│   │   ├── RoutineExerciseWithDetail.kt
│   │   └── FittDatabase.kt       @Database + prepopulate 콜백
│   ├── remote/
│   │   ├── api/                  Retrofit Service Interface
│   │   └── dto/                  Request / Response DTO
│   └── repository/               RepositoryImpl (3개)
├── domain/
│   ├── model/                    Exercise · Routine · RoutineExercise
│   │                             RoutineWithExercises · WorkoutLog · WeeklyStats
│   ├── repository/               Repository Interface (3개)
│   └── usecase/
│       ├── routine/              GetRoutines · GetRoutineDetail · Create · Delete
│       ├── log/                  GetWorkoutLogs · GetWeeklyStats · SaveWorkoutLog
│       └── coach/                GetCoachTips (키워드 응답 로직)
├── presentation/
│   ├── home/                     HomeScreen · HomeViewModel · HomeState
│   ├── routine/                  RoutineScreen · RoutineViewModel · RoutineState
│   ├── timer/                    TimerScreen · TimerViewModel · TimerState
│   ├── log/                      LogScreen · LogViewModel · LogState
│   └── coach/                    CoachScreen · CoachViewModel · CoachState
├── ui/
│   ├── theme/                    FittTheme · Color · Typography · Shape
│   └── component/                FittButton · FittCard · FittTopBar · FittBadge
├── navigation/
│   ├── Screen.kt                 sealed class + Graph 경로 상수
│   ├── FittNavGraph.kt           5개 중첩 NavGraph
│   └── FittBottomNav.kt          하단 탭 네비게이션
└── di/
    ├── DatabaseModule.kt         DB + DAO @Singleton 제공
    ├── NetworkModule.kt          Retrofit + OkHttp 제공
    └── RepositoryModule.kt       Interface ↔ Impl @Binds
```

---

## 아키텍처

```
Presentation  →  Domain  →  Data
  ViewModel        UseCase       RepositoryImpl
  Screen           Repository       Room DAO / Retrofit
  State            Model
```

- **Presentation**: Compose UI + `@HiltViewModel` + `StateFlow<State>` 단방향 데이터 흐름
- **Domain**: 순수 Kotlin, Android 의존성 없음, Repository 인터페이스 + UseCase
- **Data**: Room(로컬) + Retrofit(원격), RepositoryImpl에서 Entity ↔ Domain 매핑

---

## 네비게이션

```
root_graph
├── home_graph    →  HomeScreen
├── routine_graph →  RoutineScreen
├── timer_graph   →  TimerScreen
├── log_graph     →  LogScreen
└── coach_graph   →  CoachScreen
```

탭 전환 시 `saveState = true` / `restoreState = true`로 각 탭의 백스택과 상태를 보존합니다.

---

## 테마

| 토큰 | 라이트 | 다크 |
|---|---|---|
| Primary | `#1A1A1A` | `#E8FF5A` |
| Background | `#FFFFFF` | `#121212` |
| Surface | `#F5F5F5` | `#1E1E1E` |
| **Accent** | `#E8FF5A` | `#E8FF5A` |

Accent는 `MaterialTheme.fittColors.accent`로 접근합니다.

---

## Room DB

| 테이블 | 설명 |
|---|---|
| `exercises` | 운동 목록 (카테고리: 상체/하체/코어/전신/유산소) |
| `routines` | 사용자 루틴 |
| `routine_exercises` | 루틴-운동 중간 테이블 (복합 PK, FK CASCADE) |
| `workout_logs` | 운동 완료 기록 |

앱 최초 실행 시 기본 운동 10개 자동 삽입.

---

## 빠른 시작

자세한 내용은 [docs/SETUP.md](docs/SETUP.md) 참조

```bash
git clone https://github.com/your-org/gw-fitt.git
# Android Studio에서 폴더 열기 → Gradle Sync → Run
```

---

## 구현 완료

- [x] Clean Architecture 폴더 구조
- [x] Room DB (Entity 4개, DAO 4개, prepopulate)
- [x] Domain Layer (Model, Repository Interface, UseCase)
- [x] Data Layer (RepositoryImpl, DI 바인딩)
- [x] Compose 테마 (FittTheme, Color, Typography, Shape)
- [x] 공통 컴포넌트 (FittButton, FittCard, FittTopBar, FittBadge)
- [x] 5탭 네비게이션 (중첩 NavGraph, 백스택 보존)
- [x] HomeScreen — 주간 통계, 최근 루틴
- [x] RoutineScreen — 목록, 생성 다이얼로그, 삭제
- [x] TimerScreen — 코루틴 세트 타이머 + 휴식 카운트다운
- [x] LogScreen — 일별 바 차트, 운동 기록 목록
- [x] CoachScreen — 키워드 기반 AI 코치 챗봇
