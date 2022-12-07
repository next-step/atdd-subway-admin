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

> m1 mac -> nvm 사용, nodejs v14.21.1 (lts) 설치 (npm v6.14.17)

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


---

## 미션수행

### [ 1단계 - 지하철역 인수 테스트 작성 ]

    구현된 기능을 대상으로 인수 테스트를 작성하는 단계입니다.
    RestAssured를 활용하여 인수 테스트를 만들어 보세요

### 기능 요구사항
- 지하철역 관련 인수 테스트를 완성하세요.
  - 지하철역 목록 조회 인수 테스트 작성하기
  - 지하철역 삭제 인수 테스트 작성하기


### 프로그래밍 요구사항
인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요.
> 각각의 테스트를 동작시키면 잘 동작하지만 한번에 동작시키면 실패할 수 있습니다.
> <br>이번 단계에서는 이 부분에 대해 고려하지 말고
> <br>각각의 인수 테스트를 작성하는 것에 집중해서 진행하세요. 

### API 명세
> #### 지하철역 목록
> ###### HTTP request
> ```http request
> GET /stations HTTP/1.1
> Accept: application/json
> Host: localhost:8080
> ```
> ###### HTTP response
> ```http request
> HTTP/1.1 200 OK
> Vary: Origin
> Vary: Access-Control-Request-Method
> Vary: Access-Control-Request-Headers
> Content-Type: application/json
> Content-Length: 167
> 
> [ {
>     "id" : 1,
>     "name" : "지하철역이름"
> }, {
>   "id" : 2,
>   "name" : "새로운지하철역이름"
> }, {
>   "id" : 3,
>   "name" : "또다른지하철역이름"
> } ]
> ```
> <br>
> 
> #### 지하철역 삭제
> ###### HTTP request
> ```http request
> DELETE /stations/1 HTTP/1.1
> Host: localhost:8080
> ```
> ###### HTTP response
> ```http request
> HTTP/1.1 204 No Content
> Vary: Origin
> Vary: Access-Control-Request-Method
> Vary: Access-Control-Request-Headers
> ```

<br>

### 힌트
#### 인수 테스트 리팩터링

#### JsonPath
- Json 문서를 읽어오는 DSL
- JsonPath를 사용하면 Response Dto 객체로 받을 필요 없이 필요한 값만 추출하여 검증에 사용할 수 있음

<br>

---
### [ 2단계 - 지하철 노선 기능 ]

    제시된 인수 조건을 기반으로 기능 구현을 하는 단계입니다.
    기능 구현 전에 인수 조건을 만족하는지 검증하는 인수 테스트를 먼저 만들고 기능구현을 해보세요.

### 기능 요구사항
- 요구사항 설명에서 제공되는 인수 조건을 기반으로 지하철 노선 관리 기능을 구현하세요.
- 인수 조건을 검증하는 인수 테스트를 작성하세요.


### 프로그래밍 요구사항

- 아래의 순서로 기능을 구현하세요.
  - 인수 조건을 검증하는 인수 테스트 작성
  - 인수 테스트를 충족하는 기능 구현
- 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트를 서로 격리 시키세요.
- 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요.


### 요구사항 설명

#### **인수 조건**

- 지하철노선 생성
```
When 지하철 노선을 생성하면
Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
```
<br>

- 지하철노선 목록 조회
```
Given 2개의 지하철 노선을 생성하고
When 지하철 노선 목록을 조회하면
Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
```
<br>

- 지하철노선 조회
```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 조회하면
Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
```
<br>

- 지하철노선 수정
```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 수정하면
Then 해당 지하철 노선 정보는 수정된다
```
<br>

- 지하철노선 삭제
```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 삭제하면
Then 해당 지하철 노선 정보는 삭제된다
```

<br>

---
### [ 3단계 - 구간 추가 기능 ]

    제시된 인수 조건을 기반으로 기능 구현을 하는 단계입니다.
    기능 구현 전에 인수 조건을 만족하는지 검증하는 인수 테스트를 먼저 만들고 기능구현을 해보세요.

### 기능 요구사항
- `요구사항 설명`에서 제공되는 요구사항을 기반으로 **지하철 구간 추가 기능**을 구현하세요.
- 요구사항을 정의한 **인수 조건**을 도출하세요.
- 인수 조건을 검증하는 **인수 테스트**를 작성하세요.
- 예외 케이스에 대한 검증도 포함하세요.


### 기능 목록
- 역 사이에 새로운 역을 등록할 경우
- 새로운 역을 상행 종점으로 등록할 경우
- 새로운 역을 하행 종점으로 등록할 경우
- 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
- 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
- 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음


### 프로그래밍 요구사항
- **인수 테스트 주도 개발 프로세스**에 맞춰서 기능을 구현하세요.
  - `요구사항 설명`을 참고하여 인수 조건을 정의
  - 인수 조건을 검증하는 인수 테스트 작성
  - 인수 테스트를 충족하는 기능 구현
- 인수 조건은 인수 테스트 메서드 상단에 주석으로 작성하세요.
  - 뼈대 코드의 인수 테스트를 참고
- 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트를 서로 격리 시키세요.
- 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요.

<br>

### 요구사항 설명

#### [ API 명세 ]
**request**
```http request
POST /lines/1/sections HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8
host: localhost:52165

{
    "downStationId": 4,
    "upStationId": 2,
    "distance": 10
}
```

#### [ 지하철 구간 등록 인수 테스트 작성과 기능 구현 ]
- 역 사이에 새로운 역을 등록할 경우
  - 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정

#### [ 구간 등록 시 예외 케이스를 고려하기 ]
- 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음

- 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
  - 아래의 이미지 에서 A-B, B-C 구간이 등록된 상황에서 B-C 구간을 등록할 수 없음(A-C 구간도 등록할 수 없음)

- 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

<br>

### 힌트
- 구간 등록 인수 테스트
  - SectionAcceptanceTest 예시
  ```java
  @DisplayName("노선에 구간을 등록한다.")
  @Test
  void addSection() {
    // when
    // 지하철_노선에_지하철역_등록_요청
  
     // then
     // 지하철_노선에_지하철역_등록됨
  }
  ```
  - @BeforeEach
    - BeforeEach 애너테이션을 이용하면 테스트 클래스의 테스트 메서드 실행 전 실행
    - given 절에 대한 중복 코드를 제거할 수 있음

    ```java
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
    - SectionRequest 예시
    ```java
    public class SectionRequest {
    private Long upStationId;         // 상행역 아이디
    private Long downStationId;       // 하행역 아이디
    private int distance;             // 거리
    ...
    ```
<br>

- 구간 등록 기능 구현
    - LineController 예시
    ```java
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
  - 기능 구현 팁
    - 세부적인 예외 상황을 고려하지 않고 Happy Path 경우를 검증하기 위한 인수 테스트를 먼저 만드세요.
    > "Happy Path"는 '아무것도 잘못되지 않는 사용자 시나리오'를 의미한다 (All-Pass Scenario / Positive Test).
    <br>이는 사람의 실수, 엣지 케이스, 의도를 벗어난 행동을 포함하지 않기 때문에
    <br>이 시나리오 대로 테스트를 수행하면 이슈나 버그가 발생할 가능성이 현저히 낮아진다.
<br>

- JPA 관계 맵핑
```
  - 지하철역은 여러개의 지하철 노선에 포함될 수 있다.
    - ex) 강남역은 2호선에 등록되어 있는 동시에 신분당선에 등록되어 있음
  - 따라서 다대다 관계로 보아 @ManyToMany로 관계를 맺을 수 있음
  - 하지만 다대다 관계는 여러가지 예상치 못한 문제를 발생시킬 수 있어 추천하지 않음
    - https://ict-nroo.tistory.com/127 블로그를 참고
  - 지하철역과 지하철 노선의 맵핑 테이블을 엔티티로 두는 방법을 추천
    - 기존에 Station과 Line이 있었다면 Line에 속하는 Station을 LineStation이라는 엔티티로 도출
    - Line과 LineStation을 @ManyToOne 관계로 설정
  - 참고할 코드: https://github.com/next-step/atdd-subway-map/blob/boorownie/src/main/java/nextstep/subway/line/domain/LineStations.java
    - 참고한 코드에서는 LineStation을 일급컬렉션을 묶어 LineStations로 둠
    - JPA @Embedded And @Embeddable을 참고하세요.
```

<br>

### **인수 조건**

- 지하철 노선에 구간 등록
```
Given 지하철 역과 노선을 생성하고
When 지하철 노선에 지하철역 등록 요청하면
Then 지하철 노선에 지하철역 등록된다
```
<br>

- 지하철 노선에 등록된 지하철역 목록조회
```
Given 지하철 역을 노선에 등록하고
When 지하철 노선에 등록된 구간정보 목록을 조회하면
Then 지하철 노선에 등록된 구간정보 목록이 조회된다
```
<br>

- 지하철 노선에 등록된 지하철역 구간정보 조회
```
Given 지하철 역을 노선에 등록하고
When 지하철 노선에 등록된 지하철역 구간정보를 조회하면
Then 지하철 노선에 등록된 지하철역 구간정보가 조회된다
```
<br>

- 지하철 노선에 상행 종점 등록
```
Given 지하철 노선에 상행 종점과 하행 종점을 등록하고
When 지하철 노선에 새로운 역을 상행 종점으로 등록 요청하면
Then 지하철 노선에 상행 종점이 등록된다
```
<br>

- 지하철 노선에 하행 종점 등록
```
Given 지하철 노선에 상행 종점과 하행 종점을 등록하고
When 지하철 노선에 새로운 역을 하행 종점으로 등록 요청하면
Then 지하철 노선에 하행 종점이 등록된다
```
<br>

- 지하철 노선에 등록된 두 역 사이에 지하철 역 등록
```
Given 지하철 노선에 구간을 등록하고
When 지하철 노선에 새로운 역을 등록된 두 역 사이에 등록 요청하면
Then 지하철 노선에 지하철 역이 등록된다
```

<br>

### **예외 케이스**

- 지하철 노선의 두 역 사이에 기존 역 사이 길이보다 큰 새로운 역을 등록
```
Given 두 지하철 역과 1개의 노선을 생성, 노선에 두 역을 등록하고 
When 등록된 두 역 사이에 기존 역 사이 길이보다 큰 구간길이의 지하철역을 등록하면
Then 지하철 노선에 지하철역이 등록되지 않는다
```
<br>

- 이미 노선에 등록된 역을 상행역과 하행역으로 등록
```
Given 두 지하철 역과 1개의 노선을 생성, 노선에 두 역을 등록하고 
When 등록된 두 역을 다시 노선에 등록하면
Then 지하철 노선에 지하철역이 등록되지 않는다
```
<br>

- 노선에 등록되지 않은 역을 상행역과 하행역으로 등록
```
Given 4개의 지하철 역과 1개의 노선을 생성, 노선에 두 역을 등록하고 
When 노선에 등록되지 않은 두 지하철 역을 노선에 등록하면
Then 지하철 노선에 지하철역이 등록되지 않는다
```

<br>

---
### [ 4단계 - 구간 제거 기능 ]

### 기능 요구사항
- `요구사항 설명`에서 제공되는 요구사항을 기반으로 **지하철 구간 제거 기능**을 구현하세요.
- 요구사항을 정의한 **인수 조건**을 도출하세요.
- 인수 조건을 검증하는 **인수 테스트**를 작성하세요.
- 예외 케이스에 대한 검증도 포함하세요.


### 기능 목록
- 종점을 제거하는 경우
- 가운데 역을 제거하는 경우
- 구간이 하나인 노선에서 역을 제거하는 경우


### 프로그래밍 요구사항
- **인수 테스트 주도 개발 프로세스**에 맞춰서 기능을 구현하세요.
  - `요구사항 설명`을 참고하여 인수 조건을 정의
  - 인수 조건을 검증하는 인수 테스트 작성
  - 인수 테스트를 충족하는 기능 구현
- 인수 조건은 인수 테스트 메서드 상단에 주석으로 작성하세요.
  - 뼈대 코드의 인수 테스트를 참고
- 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트를 서로 격리 시키세요.
- 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요.

<br>

### 요구사항 설명

#### [ API 명세 ]
**request**
```http request
DELETE /lines/1/sections?stationId=2 HTTP/1.1
accept: */*
host: localhost:52165
```

#### [ 노선의 구간을 제거하는 기능을 구현 ]
- 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
- 중간역이 제거될 경우 재배치를 함
  - 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 됨
  - 거리는 두 구간의 거리의 합으로 정함

#### [ 구간 삭제 시 예외 케이스를 고려하기 ]
- 기능 설명을 참고하여 예외가 발생할 수 있는 경우를 검증할 수 있는 인수 테스트를 만들고 이를 성공 시키세요.
> 예시) 노선에 등록되어있지 않은 역을 제거하려 한다.

- 구간이 하나인 노선에서 마지막 구간을 제거할 때
  - 제거할 수 없음

<br>

### 힌트
- 구간 제거
  - 구간 제거 요청 처리
```java
@DeleteMapping("/{lineId}/sections")
public ResponseEntity removeLineStation(
        @PathVariable Long lineId, 
        @RequestParam Long stationId) {
    lineService.removeSectionByStationId(lineId, stationId);
    return ResponseEntity.ok().build();
}
```

<br>

### **인수 조건**

- 지하철 노선에 등록된 상행 종점 제거
```
Given 지하철 노선에 구간을 등록하고
When 지하철 노선에 등록된 상행 종점을 제거 요청하면
Then 지하철 노선의 기존 상행 종점이 제거되고 이어져있던 역이 새로운 상행 종점이 된다 
```
<br>

- 지하철 노선에 등록된 하행 종점 제거
```
Given 지하철 노선에 구간을 등록하고
When 지하철 노선에 등록된 하행 종점을 제거 요청하면
Then 지하철 노선의 기존 하행 종점이 제거되고 이어져있던 역이 새로운 하행 종점이 된다 
```
<br>

- 지하철 노선에 등록된 두 역 사이의 역을 제거
```
Given 지하철 노선에 3개 등록하고 (2개 구간)
When 세 역 중 가운데 역을 제거 요청하면
Then 가운데 역이 제거되고 나머지 두 역이 하나의 구간으로 재배치된다
Then 구간 제거 전의 두 구간 길이의 합은 재배치된 구간의 길이와 같다
```

<br>

### **예외 케이스**

- 구간이 하나인 노선에서 마지막 구간을 제거
```
Given 지하철 노선에 하나의 구간을 등록하고
When 노선에 등록된 두 역 중 한 역을 제거 요청 하면
Then 지하철 노선에 등록된 역이 제거되지 않는다
```
<br>

- 지하철 노선에 등록되어 있지 않은 역을 제거
```
Given 지하철 노선에 구간을 등록하고
When 노선에 등록된 역이 아닌 역을 제거 요청 하면
Then 지하철 노선에 등록된 역이 제거되지 않는다
```
