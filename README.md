<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <img alt="npm" src="https://img.shields.io/badge/npm-6.14.15-blue">
  <img alt="node" src="https://img.shields.io/badge/node-14.18.2-blue">
  <a href="https://edu.nextstep.camp/c/R89PYi5H" alt="nextstep atdd">
    <img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fedu.nextstep.camp%2Fc%2FR89PYi5H">
  </a>
  <img alt="GitHub" src="https://img.shields.io/github/license/next-step/atdd-subway-admin">
</p>

<br>

# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

<br>

## 🚀 Getting Started

### Install
#### npm 설치
```
cd frontend
npm install
```
> `frontend` 디렉토리에서 수행해야 합니다.

### Usage
#### webpack server 구동
```
npm run dev
```
#### application 구동
```
./gradlew bootRun
```
<br>

## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/next-step/atdd-subway-admin/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/next-step/atdd-subway-admin/blob/master/LICENSE.md) licensed.

***

## 미션 설명
<details open>
<summary> </summary>

* 미션 수행 방법 문서를 참고하여 실습 환경을 구축한다.
  * 저장소: https://github.com/next-step/atdd-subway-admin
* 지하철 노선도를 관리할 수 있는 어드민 서비스를 단계별로 구현하세요.
* 인수 테스트 주도 개발 프로세스를 단계별로 경험하세요.

***

### 도메인 설명

**지하철 역(station)**
* 지하철 역 속성:
  * 이름(name)

**지하철 구간(section)**
* 지하철 (상행 방향)역과 (하행 방향)역 사이의 연결 정보
* 지하철 구간 속성:
  * 길이(distance)

**지하철 노선(line)**
* 지하철 구간의 모음으로 구간에 포함된 지하철 역의 연결 정보
* 지하철 노선 속성:
  * 노선 이름(name)
  * 노선 색(color)
</details>

***

# 🚀 1단계 - 지하철역 인수 테스트 작성

## 미션 소개
<details open>
<summary> </summary>

* 구현된 기능을 대상으로 인수 테스트를 작성하는 단계입니다.
* RestAssured를 활용하여 인수 테스트를 만들어 보세요
</details>

## 요구사항
<details open>
<summary> </summary>

### 기능 요구사항
* 지하철역 관련 인수 테스트를 완성하세요.
  * 지하철역 목록 조회 인수 테스트 작성하기
  * 지하철역 삭제 인수 테스트 작성하기
</details>

## 구현 기능
<details open>
<summary> </summary>

* [x] 지하철역 목록 조회 인수 테스트 작성
* [x] 지하철역 삭제 인수 테스트 작성
* [x] 지하철역 인수 테스트 리펙토링
</details>

***
# 🚀 2단계 - 지하철 노선 기능

## 요구사항
<details open>
<summary> </summary>

### 프로그래밍 요구사항
* 아래의 순서로 기능을 구현하세요.
  * 인수 조건을 검증하는 인수 테스트 작성
  * 인수 테스트를 충족하는 기능 구현
* 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트를 서로 격리 시키세요.
* 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요.
</details>

## 구현 기능
<details open>
<summary> </summary>

* [x] 지하철 노선 생성
* [x] 지하철 노선 목록 조회
* [x] 지하철 노선 조회
* [x] 지하철 노선 수정
* [x] 지하철 노선 삭제
</details>


## 요구사항 설명
<details open>
<summary> </summary>

**지하철노선 생성**
```
When 지하철 노선을 생성하면
Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
```

**지하철노선 목록 조회**
```
Given 2개의 지하철 노선을 생성하고
When 지하철 노선 목록을 조회하면
Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
```

**지하철노선 조회**
```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 조회하면
Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
```

**지하철노선 수정**
```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 수정하면
Then 해당 지하철 노선 정보는 수정된다
```

**지하철노선 삭제**
```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 삭제하면
Then 해당 지하철 노선 정보는 삭제된다
```
</details>

## 힌트
<details open> 
<summary> </summary>

### 인수 테스트 격리
**@DirtiesContext**
* Spring Context를 이용한 테스트 동작 시 스프링 빈의 상태가 변경되면 해당 컨텍스트의 재사용이 불가하여 컨택스트를 다시 로드해야함
* 스프링빈의 상태가 변경되었다는 설정을 하는 애너테이션
* 테스트 DB는 메모리 디비로 컨테이너에 띄워져있는 상태이므로 컨텍스트가 다시 로드되면 기존 DB의 내용이 초기화됨

**@Sql**
* 테스트 수행 시 특정 쿼리를 동작시키는 애너테이션

**Table Truncate**
* 테이블을 조회하여 각 테이블을 Truncate시켜주는 방법

### 인수 테스트 리팩터링
**중복 코드 처리**
* 지하철역과 노선 인수 테스트를 작성하면서 중복되는 부분이 발생하는데 이 부분을 리팩터링 하면 부가적인 코드는 테스트로부터 분리되어 테스트에 조금 더 집중할 수 있게됨
</details>

***

# 🚀 3단계 - 구간 추가 기능

## 요구사항 
<details open> 
<summary> </summary>

### 기능 요구사항
* 요구사항 설명에서 제공되는 요구사항을 기반으로 지하철 구간 추가 기능을 구현하세요.
* 요구사항을 정의한 인수 조건을 조출하세요.
* 인수 조건을 검증하는 인수 테스트를 작성하세요.
* 예외 케이스에 대한 검증도 포함하세요.

### 기능 목록
[ ]역 사이에 새로운 역을 등록할 경우
[ ]새로운 역을 상행 종점으로 등록할 경우
[ ]새로운 역을 하행 종점으로 등록할 경우
[ ]역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
[ ]상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
[ ]상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

### 프로그래밍 요구사항
* 인수 테스트 주도 개발 프로세스에 맞춰서 기능을 구현하세요.
  * 요구사항 설명을 참고하여 인수 조건을 정의
  * 인수 조건을 검증하는 인수 테스트 작성
  * 인수 테스트를 충족하는 기능 구현
* 인수 조건은 인수 테스트 메서드 상단에 주석으로 작성하세요.
  * 뼈대 코드의 인수 테스트를 참고
* 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트를 서로 격리 시키세요.
* 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요.

</details>

## 힌트
<details open>
<summary> </summary>

**SectionAcceptanceTest 예시**
```
@DisplayName("노선에 구간을 등록한다.")
@Test
void addSection() {
    // when
    // 지하철_노선에_지하철역_등록_요청

    // then
    // 지하철_노선에_지하철역_등록됨
}
```

**@BeforeEach**
* BeforeEach 애너테이션을 이용하면 테스트 클래스의 테스트 메서드 실행 전 실행
* given 절에 대한 중복 코드를 제거할 수 있음
```
@BeforeEach
public void setUp() {
    super.setUp();

    // given
    강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
    광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);

    createParams = new HashMap<>();
    createParams.put("name", "신분당선");
    createParams.put("color", "bg-red-600");
    createParams.put("upStation", 강남역.getId() + "");
    createParams.put("downStation", 광교역.getId() + "");
    createParams.put("distance", 10 + "");
    신분당선 = 지하철_노선_등록되어_있음(createParams).as(LineResponse.class);
    }
```

**SectionRequest 예시**
```
public class SectionRequest {
    private Long upStationId;         // 상행역 아이디
    private Long downStationId;       // 하행역 아이디
    private int distance;             // 거리

    ...
```

**LineController 예시**
```
...
@PostMapping("/{lineId}/sections")
public ResponseEntity addSection(
        @PathVariable Long lineId, 
        @RequestBody SectionRequest sectionRequest) {
    // TODO: 구간 등록 기능 구현
    // ...
}
...
```

**기능 구현 팁**
* 세부적인 예외 상황을 고려하지 않고 Happy Path 경우를 검증하기 위한 인수 테스트를 먼저 만드세요.
* "Happy Path"는 '아무것도 잘못되지 않는 사용자 시나리오'를 의미한다 (All-Pass Scenario / Positive Test). 이는 사람의 실수, 엣지 케이스, 의도를 벗어난 행동을 포함하지 않기 때문에 이 시나리오 대로 테스트를 수행하면 이슈나 버그가 발생할 가능성이 현저히 낮아진다.

**JPA 관계 맵핑**
* 지하철역은 여러개의 지하철 노선에 포함될 수 있다.
  * ex) 강남역은 2호선에 등록되어 있는 동시에 신분당선에 등록되어 있음
* 따라서 다대다 관계로 보아 @ManyToMany로 관계를 맺을 수 있음
* 하지만 다대다 관계는 여러가지 예상치 못한 문제를 발생시킬 수 있어 추천하지 않음
  * https://ict-nroo.tistory.com/127 블로그를 참고하세요
* 지하철역과 지하철 노선의 맵핑 테이블을 엔티티로 두는 방법을 추천
  * 기존에 Station과 Line이 있었다면 Line에 속하는 Station을 LineStation이라는 엔티티로 도출
  * Line과 LineStation을 @ManyToOne 관계로 설정
* 참고할 코드:
  * https://github.com/next-step/atdd-subway-map/blob/boorownie/src/main/java/nextstep/subway/line/domain/LineStations.java
  * 참고한 코드에서는 LineStation을 일급컬렉션을 묶어 LineStations로 둠
  * JPA @Embedded And @Embeddable을 참고하세요.
</details>


