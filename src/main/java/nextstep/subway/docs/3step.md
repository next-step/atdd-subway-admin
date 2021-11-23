# 🚀 3단계 - 구간 추가 기능

# 요구사항

### 지하철 구간 등록 기능을 구현하기

- [X] 기능 구현 전 인수 테스트 작성
    - [X] 역 사이에 새로운 역 등록 기능
    - [X] 새로운 역 상행 종점 등록 기능
    - [X] 새로운 역 하행 종점 등록 기능
- [X] **예외 케이스 처리 인수 테스트 작성**
    - [X] 역 사이에 새로운 역이 등록될 때 기존 역사이 길이보다 크거나 같으면 등록 불가
    - [X] 상행역과 하행역이 이미 노선에 모두 등록되어 있으면 등록 불가
    - [X] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 등록 불가

### 구간 등록 API request

```
POST /lines/1/sections HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8
host: localhost:52165

{
    "downStationId": "4",
    "upStationId": "2",
    "distance": 10
}
```

# 요구사항 설명

## 지하철 구간 등록 인수 테스트 작성과 기능 구현

### 역 사이에 새로운 역을 등록할 경우

- 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정

<img src=https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/be71b2febc0c4d179c6606f9fe1a473b>

### 새로운 역을 상행 종점으로 등록할 경우

<img src=https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/2d4654cc24f949c1818773df2ae57890>

### 새로운 역을 하행 종점으로 등록할 경우

<img src=https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/832a8b49635c40b58f16fae1726909f6>

## 구간 등록 시 예외 케이스를 고려하기

### 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음

<img src=https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/13caad00374843038e304096afa418e4>

### 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음

- 아래의 이미지 에서 A-B, B-C 구간이 등록된 상황에서 B-C 구간을 등록할 수 없음(A-C 구간도 등록할 수 없음)

<img src=https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/ba4d3fa0fc86494e9e0011fdd341fbde>

### 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

<img src=https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/bf2f852db58b43fca17293bb1ba4f131>

# 힌트

## 구간 등록 인수 테스트

### SectionAcceptanceTest 예시

```java
@DisplayName("노선에 구간을 등록한다.")
@Test
void addSection(){
	// when
	// 지하철_노선에_지하철역_등록_요청

	// then
	// 지하철_노선에_지하철역_등록됨
	}

```

### @BeforeEach

- BeforeEach 애너테이션을 이용하면 테스트 클래스의 테스트 메서드 실행 전 실행
- given 절에 대한 중복 코드를 제거할 수 있음

```java
@BeforeEach
public void setUp(){
	super.setUp();

	// given
	강남역=StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
	광교역=StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);

	createParams=new HashMap<>();
	createParams.put("name","신분당선");
	createParams.put("color","bg-red-600");
	createParams.put("upStation",강남역.getId()+"");
	createParams.put("downStation",광교역.getId()+"");
	createParams.put("distance",10+"");
	신분당선=지하철_노선_등록되어_있음(createParams).as(LineResponse.class);
	}

```

### SectionRequest 예시

```java
public class SectionRequest {
	private Long upStationId;         // 상행역 아이디
	private Long downStationId;       // 하행역 아이디
	private int distance;             // 거리

    ...

```

## 구간 등록 기능 구현

### LineController 예시

```java
...
@PostMapping("/{lineId}/sections")
public ResponseEntity addSection(
@PathVariable Long lineId,
@RequestBody SectionRequest sectionRequest){
	// TODO: 구간 등록 기능 구현
	// ...
	}
	...

```

### 기능 구현 팁

- 세부적인 예외 상황을 고려하지 않고 Happy Path 경우를 검증하기 위한 인수 테스트를 먼저 만드세요.

> "Happy Path"는 '아무것도 잘못되지 않는 사용자 시나리오'를 의미한다 (All-Pass Scenario / Positive Test). 이는 사람의 실수, 엣지 케이스, 의도를 벗어난 행동을 포함하지 않기 때문에 이 시나리오 대로 테스트를 수행하면 이슈나 버그가 발생할 가능성이 현저히 낮아진다.
>

## JPA 관계 맵핑

- 지하철역은 여러개의 지하철 노선에 포함될 수 있다.
    - ex) 강남역은 2호선에 등록되어 있는 동시에 신분당선에 등록되어 있음
- 따라서 다대다 관계로 보아 @ManyToMany로 관계를 맺을 수 있음
- 하지만 다대다 관계는 여러가지 예상치 못한 문제를 발생시킬 수 있어 추천하지 않음
    - [https://ict-nroo.tistory.com/127](https://ict-nroo.tistory.com/127) 블로그를 참고하세요
- 지하철역과 지하철 노선의 맵핑 테이블을 엔티티로 두는 방법을 추천
    - 기존에 Station과 Line이 있었다면 Line에 속하는 Station을 LineStation이라는 엔티티로 도출
    - Line과 LineStation을 @ManyToOne 관계로 설정
- 참고할
  코드:[https://github.com/next-step/atdd-subway-map/blob/boorownie/src/main/java/nextstep/subway/line/domain/LineStations.java](https://github.com/next-step/atdd-subway-map/blob/boorownie/src/main/java/nextstep/subway/line/domain/LineStations.java)
    - 참고한 코드에서는 LineStation을 일급컬렉션을 묶어 LineStations로 둠
    - [JPA @Embedded And @Embeddable](https://www.baeldung.com/jpa-embedded-embeddable)을 참고하세요.