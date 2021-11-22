# 우아한 테크 캠프 Pro - 미션 3

## 미션명 : ATDD(인수 테스트 주도 개발)

### # 1단계 : 지하철 노선도 관리

#### 요구 사항
 
- 지하철 노선 관리 기능을 구현하기
    - 기능 목록: 생성 / 목록 조회 / 조회 / 수정 / 삭제
    - 기능 구현 전 인수 테스트 작성
    - 기능 구현 후 인수 테스트 리팩터링
    
#### 단계별 요구사항 

**1.지하철 노선 관련 기능의 인수 테스트를 작성하기**
    - LineAcceptanceTest 를 모두 완성시키세요.
```java
@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청

        // then
        // 지하철_노선_생성됨
    }

    ...
}
```


**2.지하철 노선 관련 기능 구현하기**
    - 인수 테스트가 모두 성공할 수 있도록 LineController를 통해 요청을 받고 처리하는 기능을 구현하세요.
```java
@RestController
@RequestMapping("/lines")
public class LineController {

    ...

	@PostMapping
	public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
		// TODO
	}

	@GetMapping
	public ResponseEntity<List<LineResponse>> findAllLines() {
		// TODO
	}
    
    ...
}
```
**3.인수 테스트 리팩터링**
인수 테스트의 각 스텝들을 메서드로 분리하여 재사용하세요.
ex) 인수 테스트 요청 로직 중복 제거 등# 우아한 테크 캠프 Pro - 미션 3


### # 2단계 : 인수 테스트 리팩터링

#### 요구 사항

1.노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기

- 두 종점역은 **구간**의 형태로 관리되어야 함

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



2.노선 객체에서 구간 정보를 관리하기

- 노선 생성시 전달되는 두 종점역은 노선의 상태로 관리되는 것이 아니라 구간으로 관리되어야 함

```java
public class Line {
    ...
    private List<Section> sections;
    ...
}
```



3.노선의 역 목록을 조회하는 기능 구현하기

- 노선 조회 시 역 목록을 함께 응답할 수 있도록 변경
- 노선에 등록된 구간을 순서대로 정렬하여(상행역 부터 하행역 순) 상행 종점부터 하행 종점까지 목록을 응답하기
- 필요시 노선과 구간(혹은 역)의 관계를 새로 맺기


### # 3단계 : 구간 추가 기능

#### 요구 사항

1. 지하철 구간 등록 성공 인수 테스트 작성과 기능 구현

- 역 사이에 새로운 역을 등록할 경우
    - 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
- 새로운 역을 상행 종점으로 등록할 경우
- 새로운 역을 하행 종점으로 등록할 경우

2. 구간 등록 시 예외 케이스를 고려하여 인수테스트 작성

- 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
- 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
    - A-B, B-C 구간이 등록된 상황에서 B-C 구간을 등록할 수 없음(A-C 구간도 등록할 수 없음
    - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음



#### 요청 Request 포맷

```json
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

### # 4단계 : 구간 제거 기능

#### 요구 사항

**노선의 구간을 제거하는 기능을 구현하기**

- 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
- 중간역이 제거될 경우 재배치를 함
    - 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 됨
    - 거리는 두 구간의 거리의 합으로 정함
- 예외 케이스
    - 노선에 존재하는 구간이 하나밖에 없는 경우에는 제거 요청시 예외 처리
    - 노선에 등록되어있지 않은 역 제거 요청시 예외처리



#### 요청 Request 포맷

```json
DELETE /lines/1/sections?stationId=2 HTTP/1.1
accept: */*
host: localhost:52165
```