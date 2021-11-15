[인텔리J 마크다운 사용법](https://www.jetbrains.com/help/idea/markdown.html#reformat)

# 지하철 노선도 미션

[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

### 인수 테스트 작성 방법

- 인수 테스트 클래스
	- Feature 기준으로 인수 테스트 클래스를 나눌 수 있음
	- Scenario 기준으로 인수 테스트 메서드를 작성할 수 있음
	- `하나의 Feature 내부`에 있는 `Scenario는 같은 테스트 픽스쳐를 공유`하는 것을 추천

- 간단한 성공 케이스 우선 작성
	- 여기서 말하는 간단한은 지나치게 간단한이 아님
	- 테스트가 동작하면 `실제 구조에 관해 더 좋은 생각`이 떠오를 수 있음

🔴 전체 미션 단계
===
- [x] 지하철 노선 관리
- [ ] 지하철 노선에 구간 등록
- [ ] 노선에 등록된 역 목록 조회
- [ ] 지하철 노선에 구간 제외

도메인 설명
---
1. 지하철 역(station)
	- 지하철 역은 이름을 가지고 있다.
2. 지하철 구간(section)
	- 지하철은 상행과 하행으로 나누어진다.
	- 지하철 사이에는 길이가 존재한다.
3. 지하철 노선(line)
	- `지하철 구간의 모음`으로 구간에 포함된 지하철 역의 연결 정보
	- 노선이름과 노선 색을 갖는다.


🔷 1단계 요구사항
===

### 지하철 노선 관리 기능을 구현하기
- [x] 노선 생성
- [x] 노선 전체 조회
- [x] 노선 단건 조회
- [x] 노선 수정
- [x] 노선 삭제
- [x] 기능 구현 전 인수 테스트 작성
- [x] 기능 구현 후 인수 테스트 리팩터링





--------------------
--------------------
### 인수 테스트(Acceptance Test)란? <br>

> 사용자의 관점에서 올바르게 작동하는지 테스트 <br>
> 클라이언트가 의뢰했던 소프트웨어를 인수 받을 때, 미리 전달했던 요구사항이 충족되었는지를 확인하는 테스트
>

### 인수 테스트 특징

1. 전 구간 테스트
2. BlackBox 테스트
	- 세부 구현에 영향을 받지 않기
3. 인수 테스트는 시스템 내부 코드를 가능한 직접 호출하지 말고 시스템 전 구간을 테스트를 하도록 안내하고 있기 때문에 시스템 외부에서 요청하는 방식으로 검증합니다.

### 📋 주요 기능

```java

@DisplayName("지하철 역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
	@LocalServerPort
	int port;

	@BeforeEach
	public void setUp() {
		RestAssured.port = port;
	}

	when().
		get("/lotto/{id}", 5).
	then().
		statusCode(200).
		body("lotto.lottoId",equalTo(5), "lotto.winners.winnerId", hasItems(23,54));
}
```

### 인수조건을 테스트 코드로 옮기기

``` text
Feature: 간략한 기능 서술
Background: 각 시나리오 사전 조건
Scenario: 시나리오(예시) 제목
Given: 사전조건
When: 발생해야하는 이벤트
Then: 사후조건

And: 앞선 내용에 추가적인 내용 기술
```

#### 인수 조건 예시
````text
Feature: 지하철 역 관리 기능

  Scenario: 지하철 역을 생성한다.
    When 지하철 역을 생성 요청한다.
    Then 지하철역이 생성된다.
    
  Scenario: 지하철 역을 삭제한다.
    Given 지하철 역이 등록되어있다.
    When 지하철 역을 삭제 요청한다.
    Then 지하철 역이 삭제된다.

````