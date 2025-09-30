## Android Clean Architecture Template (Kotlin, Compose, Hilt)

Jetpack Compose, Hilt, Retrofit/OkHttp, Sandwich(ApiResponse) 등을 기본 구성으로 포함한 멀티 모듈 Clean Architecture 템플릿입니다. 실무 초기 셋업과 포트폴리오 프로젝트 시작에 바로 적용할 수 있도록 구성했습니다.

### 목차
- 1. 개요
- 2. 아키텍처 개요
- 3. 모듈 구조
- 4. 기술 스택 및 버전
- 5. 빠른 시작
- 6. 빌드와 실행
- 7. 환경 설정 가이드
- 8. 데이터 흐름 예시
- 9. 테스트
- 10. 코드 스타일 가이드
- 11. 새로운 기능 모듈 추가 가이드
- 12. FAQ
- 13. 라이선스

## 1) 개요
- 목적: 유지보수성, 확장성, 테스트 용이성을 우선한 앱 템플릿
- 패턴: MVVM + Clean Architecture
- UI: Jetpack Compose(Material 3), 다이내믹 컬러(안드로이드 12+)
- DI: Hilt 전면 적용, 모듈 경계 기반 의존성 구성
- 네트워크: Retrofit + OkHttp + Sandwich(ApiResponse)
- 활용: 포트폴리오, 사이드 프로젝트, 프로덕션 초기화

## 2) 아키텍처 개요
클린 아키텍처 원칙에 따라 계층별 책임을 분리합니다.

- Domain: 비즈니스 규칙(인터페이스, 유스케이스)
- Data: 외부 데이터 소스 구현(네트워크 등), Repository 구현, DI 모듈
- Feature: 화면 단위 UI 및 ViewModel, Domain에만 의존
- App: 애플리케이션 엔트리, 글로벌 셋업
- Resources: 디자인 시스템(테마/타이포/컬러) 및 공용 UI 리소스

데이터 흐름은 단방향입니다.

UI(Compose) → ViewModel → Domain(Repository 인터페이스) → Data(RepositoryImpl → Service) → Remote

## 3) 모듈 구조
```
app/                 # Application, Activity, 전역 DI(AppModule)
domain/              # 비즈니스 인터페이스(예: MainRepository)
data/                # Retrofit/OkHttp, Repository 구현, DI(Network/Api/Repository)
feature-main/        # 예시 Feature 화면(MainView, MainViewModel)
resources/           # 디자인 시스템(Theme/Color/Type)
```

핵심 파일
- `app/app/HiltApp.kt`: `@HiltAndroidApp` Application
- `app/di/AppModule.kt`: 전역 Context 등 제공
- `data/di/NetworkModule.kt`: Retrofit/OkHttp/Sandwich 구성, 로그 레벨 빌드 타입 분기
- `data/di/ApiModule.kt`: Retrofit 서비스 바인딩(`MainService`)
- `data/di/RepositoryModule.kt`: `MainRepositoryImpl` → `MainRepository` 바인딩
- `feature-main/viewmodel/MainViewModel.kt`: Hilt 주입, StateFlow로 UI 상태 노출
- `feature-main/ui/MainView.kt`: Compose UI + 다이나믹 컬러 + 프리뷰

## 4) 기술 스택 및 버전
- Kotlin: 1.9.0 (Kapt 1.9.23)
- Android Gradle Plugin: 8.2.0
- Compile/Target SDK: 34, Min SDK: 24, JVM Target: 11
- Compose BOM: 2023.08.00, Activity Compose: 1.8.2
- Material3, Lifecycle: 2.7.0(ViewModel/Runtime/SavedState)
- Hilt: 2.51(+ hilt-navigation-compose)
- Retrofit: 2.10.0, OkHttp: 4.12.0
- Sandwich: 2.0.6(ApiResponse)
- Glide: 4.14.2(+ compose)

## 5) 빠른 시작
사전 준비
- Android Studio Iguana(2023.2.1) 이상 권장
- JDK 11

클론/오픈
```bash
git clone https://github.com/ok0035/AndroidTemplate.git
cd AndroidTemplate
```
Android Studio로 열고 Gradle Sync 수행 후 `app` 모듈을 실행합니다.

## 6) 빌드와 실행
CLI
```bash
./gradlew clean
./gradlew assembleDebug
./gradlew :app:installDebug
```

테스트
```bash
./gradlew test
./gradlew connectedAndroidTest
```

## 7) 환경 설정 가이드
기본 Base URL은 `data` 모듈의 빌드타입 별 `BuildConfig.baseUrl`로 관리합니다.

파일: `data/build.gradle.kts`
```kotlin
buildTypes {
    debug {
        buildConfigField(
            type = "String",
            name = "baseUrl",
            value = "\"https://httpbin.org/\""
        )
    }
    release {
        buildConfigField(
            type = "String",
            name = "baseUrl",
            value = "\"https://httpbin.org/\""
        )
        isMinifyEnabled = false
    }
}
```

OkHttp 로그 레벨은 빌드타입에 따라 자동 분기됩니다.
```kotlin
level = if (BuildConfig.DEBUG)
    HttpLoggingInterceptor.Level.BODY
else
    HttpLoggingInterceptor.Level.BASIC
```

변경 가이드
- 다른 환경을 사용하려면 각 빌드타입의 `baseUrl` 값을 변경합니다.
- 추가 헤더/인증이 필요하면 `NetworkModule`의 `OkHttpClient` 빌더에 인터셉터를 추가합니다.

## 8) 데이터 흐름 예시
샘플 플로우는 httpbin의 `/get` API를 호출하여 메시지를 UI에 표시합니다.

1. UI: `MainView`가 `MainViewModel.mainMessage`를 구독해 텍스트를 렌더링
2. ViewModel: 초기화 시 `MainRepository.getData` 호출
3. Domain: `MainRepository` 인터페이스를 통해 의존 역전
4. Data: `MainRepositoryImpl`이 `MainService.getData()` 호출 → Sandwich `ApiResponse`로 성공/실패 분기 → 메시지 생성

핵심 시그니처
```kotlin
interface MainRepository {
    suspend fun getData(
        onSuccess: (res: String) -> Unit,
        onError: (errorMessage: String) -> Unit
    )
}
```

## 9) 테스트
- 단위 테스트: JUnit 포함. Repository/UseCase 단위 테스트 권장
- 통합 테스트: OkHttp MockWebServer 포함, 네트워크 경로 검증 가능
- UI 테스트: Compose UI Test 의존성 포함

권장 명령어
```bash
./gradlew :data:test
./gradlew :feature-main:test
./gradlew :app:connectedAndroidTest
```

## 10) 코드 스타일 가이드
- 네이밍: 축약 지양, 의미 있는 전체 단어 사용
- 제어 흐름: 불필요한 중첩 지양, 가드절·조기 반환 활용
- Compose: 상태는 ViewModel(StateFlow 등)에서 내려받아 렌더링
- DI: 인터페이스 ↔ 구현 바인딩은 `@Binds` 사용, 범위 명확화
- 에러 처리: Sandwich `ApiResponse`로 성공/실패 경로 분리

## 11) 새로운 기능 모듈 추가 가이드
1. 새 라이브러리 모듈 생성: `feature-xxx`
2. `build.gradle.kts`에 Compose/Hilt 의존성 추가
3. UI와 ViewModel 구현, Domain 인터페이스 주입
4. `app`에서 네비게이션 또는 진입 연결

예시 의존성 블록
```kotlin
dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":resources"))
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
}
```

## 12) FAQ
- Sandwich를 사용하는 이유  
  네트워크 성공/실패/예외를 `ApiResponse`로 일관되게 다루기 쉽고, 코루틴 친화적이며 보일러플레이트를 줄일 수 있습니다.

- Resources 모듈을 분리한 이유  
  디자인 시스템(테마/타이포/컬러)의 독립성과 기능 모듈 간 재사용성을 높이기 위함입니다.

- Min SDK 24 선택 이유  
  현대 Jetpack 스택과의 호환성과 사용자 커버리지의 균형을 고려했습니다.

## 13) 라이선스
본 템플릿은 학습, 포트폴리오, 프로덕션 초기화 용도로 자유롭게 사용할 수 있습니다. 별도 라이선스가 필요할 경우 루트에 `LICENSE` 파일을 추가하는 구성을 권장합니다.

