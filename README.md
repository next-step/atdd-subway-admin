<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <img alt="npm" src="https://img.shields.io/badge/npm-%3E%3D%205.5.0-blue">
  <img alt="node" src="https://img.shields.io/badge/node-%3E%3D%209.3.0-blue">
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

## 👨🏻‍💻 Todo list

- [x] [**사전 제공**] 지하철 역 관리
- [x] 코드 이해 및 Polishing
- [x] [**1단계**] 지하철 노선 관리
  - [x] 지하철 노선을 생성한다.
  - [x] 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.
  - [x] 지하철 노선 목록을 조회한다.
  - [x] 지하철 노선을 조회한다.
  - [x] 지하철 노선을 수정한다.
  - [x] 지하철 노선을 제거한다.

- [x] [**2단계**] 지하철 노선에 구간 등록  
  - [x] 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기  
    - 두 종점역은 **구간**의 형태로 관리되어야 함  
  - [x] 노선 조회 시 응답 결과에 역 목록 추가하기  
    - **상행역 부터 하행역 순으로 정렬되어야 함**  

- [ ] [**3단계**] 노선에 등록된 역 목록 조회
- [ ] [**4단계**] 지하철 노선에 구간 제외

## [1단계] 지하철 노선 관리

**구현해야 될 부분**

![image.png](https://tva1.sinaimg.cn/large/008i3skNgy1gr55v4rh3sj30vb0u0wk1.jpg)



![image.png](https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/155885260e20466497cbf3f344cf7a5d)

## 지하철 역(station)

- 지하철 역 속성:
  - 이름(name)

## 지하철 구간(section)

- 지하철 (상행 방향)역과 (하행 방향)역 사이의 연결 정보
- 지하철 구간 속성:
  - 길이(distance)

## 지하철 노선(line)

- 지하철 구간의 모음으로 구간에 포함된 지하철 역의 연결 정보
- 지하철 노선 속성:
  - 노선 이름(name)
  - 노선 색(color)

## [2단계] 인수 테스트 리팩터링  

## TODO

[**2단계**] 지하철 노선에 구간 등록  

- [x] 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기  
  - 두 종점역은 **구간**의 형태로 관리되어야 함  
- [x] 노선 조회 시 응답 결과에 역 목록 추가하기  
  - **상행역 부터 하행역 순으로 정렬되어야 함**
---
- [x] service > getStations 을 Line Entity에 위임하기
- [x] 노선 정보 업데이트시 역 목록 보이도록 변경
--- 피드백 리팩토링
- [x] stationRepository.findById(request.getDownStationId()) 중복 코드 제거
- [x] Inline Variable 제거
- [x] getStations() 중복 코드 제거

## 노선 생성 시 두 종점역 추가하기

- 인수 테스트와 DTO 등 수정이 필요함

```java
public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;       // 추가
    private Long downStationId;     // 추가
    private int distance;           // 추가
    ...
}
```

## 노선 객체에서 구간 정보를 관리하기

- 노선 생성시 전달되는 두 종점역은 노선의 상태로 관리되는 것이 아니라 구간으로 관리되어야 함

```java
public class Line {
    ...
    private List<Section> sections;
    ...
}
```

## 노선의 역 목록을 조회하는 기능 구현하기

- 노선 조회 시 역 목록을 함께 응답할 수 있도록 변경
- 노선에 등록된 구간을 순서대로 정렬하여 상행 종점부터 하행 종점까지 목록을 응답하기
- 필요시 노선과 구간(혹은 역)의 관계를 새로 맺기



# [3단계] 구간 추가 기능

## 요구사항

### 지하철 구간 등록 기능을 구현하기

- 기능 구현 전 인수 테스트 작성

- **예외 케이스 처리 인수 테스트 작성**

  ```http
  구간 등록 API Request
  POST /lines/1/sections HTTP/1.1
  accept: */*
  content-type: application/json; charset=UTF-8
  host: localhost:52165
  
  {
      "downStationId": "4",
      "upStationId": "2",
      "distance": 10
  }
  
  # 
  ```

  

### 요구사항 설명

### 지하철 구간 등록 인수 테스트 작성과 기능 구현

#### 역 사이에 새로운 역을 등록할 경우

- 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
  ![image.png](https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/be71b2febc0c4d179c6606f9fe1a473b)

#### 새로운 역을 상행 종점으로 등록할 경우

![image.png](https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/2d4654cc24f949c1818773df2ae57890)

#### 새로운 역을 하행 종점으로 등록할 경우

![image.png](https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/832a8b49635c40b58f16fae1726909f6)

### 구간 등록 시 예외 케이스를 고려하기

#### 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음

![image.png](https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/13caad00374843038e304096afa418e4)

#### 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음

- 아래의 이미지 에서 A-B, B-C 구간이 등록된 상황에서 B-C 구간을 등록할 수 없음(A-C 구간도 등록할 수 없음)
  ![image.png](https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/ba4d3fa0fc86494e9e0011fdd341fbde)

#### 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

![image.png](https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/bf2f852db58b43fca17293bb1ba4f131)

### 힌트

### 구간 등록 인수 테스트

#### SectionAcceptanceTest 예시

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

#### @BeforeEach

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

#### SectionRequest 예시

```java
public class SectionRequest {
    private Long upStationId;         // 상행역 아이디
    private Long downStationId;       // 하행역 아이디
    private int distance;             // 거리

    ...
```

### 구간 등록 기능 구현

#### LineController 예시

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

### 기능 구현 팁

- 세부적인 예외 상황을 고려하지 않고 Happy Path 경우를 검증하기 위한 인수 테스트를 먼저 만드세요.

> "Happy Path"는 '아무것도 잘못되지 않는 사용자 시나리오'를 의미한다 (All-Pass Scenario / Positive Test). 이는 사람의 실수, 엣지 케이스, 의도를 벗어난 행동을 포함하지 않기 때문에 이 시나리오 대로 테스트를 수행하면 이슈나 버그가 발생할 가능성이 현저히 낮아진다.

#### JPA 관계 맵핑

- 지하철역은 여러개의 지하철 노선에 포함될 수 있다.

  - ex) 강남역은 2호선에 등록되어 있는 동시에 신분당선에 등록되어 있음

- 따라서 다대다 관계로 보아 @ManyToMany로 관계를 맺을 수 있음

- 하지만 다대다 관계는 여러가지 예상치 못한 문제를 발생시킬 수 있어 추천하지 않음

  - https://ict-nroo.tistory.com/127 블로그를 참고하세요

- 지하철역과 지하철 노선의 맵핑 테이블을 엔티티로 두는 방법을 추천

  - 기존에 Station과 Line이 있었다면 Line에 속하는 Station을 LineStation이라는 엔티티로 도출
  - Line과 LineStation을 @ManyToOne 관계로 설정

- 참고할 코드:

  https://github.com/next-step/atdd-subway-map/blob/boorownie/src/main/java/nextstep/subway/line/domain/LineStations.java

  - 참고한 코드에서는 LineStation을 일급컬렉션을 묶어 LineStations로 둠
  - [JPA @Embedded And @Embeddable](https://www.baeldung.com/jpa-embedded-embeddable)을 참고하세요.

### [3단계]Todo list

- [ ] 기능 구현 전 인수 테스트 작성
  
  - [x] Controller, Request 코드 추가
  - [x] 역 사이에 새로운 역을 등록할 경우 인수 테스트 작성
  - [x] 새로운 역을 상행 종점으로 등록할 경우 인수 테스트 작성
  - [x] 새로운 역을 하행 종점으로 등록할 경우 인수 테스트 작성
  - [x] ~~JPA 관계 맵핑~~ 
  - [x] 인수 테스트 코드 Pass 되도록 작성
  
- [x] **예외 케이스 처리 인수 테스트 작성**

  - [x] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
  - [x] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
  - [x] 상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없음

  

역이 만들어지면서 호선을 배정받는다. 라이프사이클이 동일하다는 건가?

Station 과 Line이 그럼 동일한가?
