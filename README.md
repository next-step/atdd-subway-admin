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


## 1단계 첫 피드백 
### 1. CustomException 언제 사용해야할까 ?

#### 🔹 표준 예외를 적극적으로 사용하자!
   - 자바에는 무수한 `Exception` 존재한다. 의미를 명확하게 할 수 있다면 `CustomException` 지양해야한다.
   - 지나치게 많아진다면 메모리 문제도 발생할 수 있고, 클래스 로딩에도 시간이 더 소요될 가능성이 있다.
   - 또한 관리 대상이 될 수 있다. 무수한 `CustomException` 관리 차원에 좋다고 볼 수 있을까?

#### 🔹 그렇다면 언제 사용해야할까? 
   - 어렸을 적 놀이터에서 놀다보면 어머니가 밥먹을 시간이라고 찾을 때가 있다 
   - 그 수많은 아이들 중 내 아이라는 확신을 어떻게 하면 가질 수 있을까? 
     - 바로 이름을 명시하면된다.(이름으로도 정보 전달이 가능하다.) 
   - 만약 `CustomException`을 처리해야하는 상황에서 명시하지 못한다면 우리는 한 가지의 debug 사용을 해야할 것이다.

   - 예외에 대한 응집도가 향상된다.
   - `CustomException`을 사용하지 않고 무수한 `메시지 내용`을 `exception`에 담아 던지는 경우가 있다. 
     - 사실 메시지가 길어진다는 것은 `추상화의 필요성`을 얘기하는 조짐일 수있는 것이다.
     - 메시지의 분포보다 관리의 분포가 오히려 관리하기 용이할 것이다.

#### 🔹 `너무나 많은 Exceptin` 관리할 수 없을까 ?
   + 예외 생성 비용을 절감한다.
   + 아래 방식은 무수한 `stack trace` 내용을 커스텀 할 수 있는 부분이다
```java
@Override
    public synchronized Throwable fillInStackTrace() {
    return this;
}
```
   + 상황에 따라 정보를 다르게 주는 예외가 아니라 단순하게 메세지만 넘겨준다면 해당 예외를 캐싱해두는 것도 비용 절감의 방법이다.
```java
    public static final CustomException CUSTOM_EXCEPTION = new CustomException("대충 예외라는 내용")
```

### 2. EntityNotFoundException  언제 예외를 발생 시킬까?
1. `EntityNotFoundException`은 `PersistenceException`의 상속하고있습니다.
2. `PersistenceException` 언제 예외를 발생 시킬까요?
  + 트랜잭션 범위 내에서 아래와 같은 오류를 발생할경우 해당 예외가 하고있습니다.
    + NoResultException
    + NonUniqueResultException
    + LockTimeoutException
    + QueryTimeoutException

3. `EntityNotFoundException`은 언제 예외가 발생할까요 ?
   + EntityManager.getReference 
     + 엔티티 참조에 대한 액세스(프록시) 했지만 실제 `엔티티`가 존재하지 않는경우
   + EntityManager.refresh 
     + 데이터베이스에서 다시 조회하여 엔티티에 변경사항을 덮어쓰는 경우
     + `Session` 장기적 세션 유지? 를 위한경우 구현해서 사용하는 것은 좋지 않다. 즉 일회성이다? 
   + EntityManager.lock
     + 비관적 잠금이면서 `데이터베이스에 해당 엔티티가 존재하지 않는 경우 `
       + OptimisticLockException
         + JPA는 조회시에 트랜잭션 내부에 버전 속성의 값을 보유하고 트랜잭션이 `업데이트를 하기 전에 버전 속성`을 다시 확인합니다.
           그 동안에 버전 정보가 변경이 되면 발생 
         + 비관적 잠금이지만 속성으로 `@Version`명시하는 경우 낙관적 버전검사도 수행합니다. 만약 충돌시에 예외
       + PessimisticLockException
         + 잠금은 Shared Lock 또는 Exclusive Lock 둘중에 하나만 획득할 수 있으며 그 락을 획득하는데 실패하면 발생되는 예외입니다.
         

### 3. `EntityNotFoundException`은 package javax.persistence 위치하고 있다!
   + `spring 패키지` 영역이 아니다.
   + `hibernate 패키지` 영역도 아니다.
    
2. `javax` 패키지에 있다 `x` 
   +  x 무엇인가?
       + 표준 라이브러리에 속하지 않는 다른 다양한 확장 패키지

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



### 비관적 잠금, 낙관적 잠금

1. 예시를 한번들어보자!
	1. 운영자는 배송상태를 변경하기 위해 주문정보를 조회한다.
	2. 고객은 배송지를 변경하기 위해 주문정보를 조회한다.
	3. 운영자는 배송상태를 배송중으로 변경한다.
	4. 고객이 배송지 변경한다.(그러나 배송중인 상품은 배송지를 변경할 수 없는 정책이 존재)
	5. 운영자의 행동이 DB에 반영된다.
	6. 고객의 행동이 DB에 반영된다.

#### 비관적 잠금(선점 잠금선점 잠금, Pessimistic Lock)
> 비관적 잠금은 어떤 스레드가 주문 정보를 먼저 구했다면 주문 정보를 통해 어떠한 기능의 수행이 끝나기 전까지는
> 다른 스레드들이 주문정보를 구하지 못하도록 막는, 잠그는 방식이다. 다음과 같이 말이다.

```java
Order order = entityManager.find(Order.class, orderNo, LockModeType.PESSIMISTIC_WRITE);
```

3. 비관적 잠금과 교착 상태

+ 비관적 잠금을 사용할 때 교착상태가 발생할 수 있다.

1. 스레드 1: A 정보를 구하고 잠금
2. 스레드 2: B 정보를 구하고 잠금
3. 스레드 1: B 정보를 구하고자할 때 블로킹
4. 스레드 2: A 정보를 구하고자할 때 블로킹

위의 시나리오를 수행하면 두 스레드 모두 영원히 작업을 끝낼 수 없는 교착상태(Dead lock)에 빠지게 된다.
이런 문제가 발생하지 않도록 하려면 잠금을 시도할 때 최대 잠금 시간을 지정하면된다.

#### 낙관적 잠금(비선점 잠금, Optimistic Lock)

1) 운영자는 배송상태를 변경하기 위해 version이 1인 주문정보를 조회한다.
2) 고객은 배송지를 변경하기 위해 version이 1인 주문정보를 조회한다.
3) 운영자는 배송상태를 배송중으로 변경한다.
4) 고객은 배송지를 변경한다.
5) 운영자의 행동이 DB에 반영됨과 함께 version이 2로 업데이트 된다.
6) 고객의 행동이 DB에 반영됨과 함께 version 1 정보를 version 2로 업데이트 할 때 이미 DB엔 주문 정보 version이 2이므로 누군가 수정했다고 판단하여 트랜잭션 커밋이 실패된다.


즉, 주문 정보에 `version 프로퍼티를 포함시켜 선점(잠금)` 없이 트랜잭션 충돌을 방지 할 수 있다.

JPA는 낙관적 잠금을 지원하기 때문에 엔티티에 version으로 사용할 매핑되는 컬럼 프로퍼티에 @Version 어노테이션을 명시하면 구현이 가능하다.
```java
@Entity
@Table(name = "order")
public class Order {
    ...
    
    @Version
    private long version;
    
    ...
}
```


## 링크 모음
[낙관적 비관적잠금](https://velog.io/@lsb156/JPA-Optimistic-Lock-Pessimistic-Lock#%EC%95%94%EC%8B%9C%EC%A0%81-%EC%9E%A0%EA%B8%88-implicit-lock)