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

## 인수 테스트 주도 개발(ATDD) 미션 
* 인수 테스트는 블랙 박스 테스트 기반
    * 클라이언트는 표면적으로 확인할 수 잇는 요소를 바탕으로 검증
    * 실제 발생할 수 있는 **시나리오**를 바탕으로 요구사항 작성
    * 내부 구현이나 기술에 의존적이지 않음
* 인수 조건 예시
~~~yml
Feature: 최단 경로 구하기
  Scenario: 지하철 최단 경로 조회
    Given: 지하철역들이 등록되어 있다.
    And: 지하철노선이 등록되어 있다.
    And: 지하철노선에 지하철역들이 등록되어 있다.
    When: 사용자는 출발역과 도착역의 최단 경로 조회를 요청한다.
    Then: 사용자는 최단 경로의 역 정보를 응답받는다.
~~~
###  Step1 API명세 및 요구사항
#### 지하철역 목록
HTTP request
<pre>
GET /stations HTTP/1.1
Accept: application/json
Host: localhost:8080
</pre>
HTTP response
<pre>
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json
content-Length: 167
[
  {
  "id": 1,
  "name" "지하철역이름"
  },
  {
  "id": 2,
  "name" "지하철역이름2"
  }
]
</pre>
#### 지하철역 삭제
HTTP request
<pre>
DELETE /stations/1 HTTP/1.1
Host: localhost:8080
</pre>
HTTP response
<pre>
HTTP/1.1 204 No Content
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
</pre>

- [X] 지하철역 목록 조회 인수 테스트 작성하기
    * 2개의 지하철역을 생성 후 조회하면, 등록한 2개의 지하철을 응답 받아야 함.
    * List<Map<>> 방식으로 호출했을 때 다중 등록 가능여부 테스트 -> __불가__
    * Map의 name 값에 ','로 구분하여 생성 요청 시 다중 등록 가능여부 테스트 -> __불가__
    * 생성API 2번 각각 호출하여 2개의 지하철역 생성 후 조회
- [X] 지하철역 삭제 인수 테스트 작성하기
    * 지하철역 생성 후 삭제하면, 삭제된 지하철은 조회되지 않음

#### Step1 회고
* RestAssured의 사용법을 제대로 파악하지 못하여 쓸데없는? 코드를 작성함.
* .extract()를 수행하면 ExtractableResponse 타입의 객체로 받을 수 있음
* .extract()를 수행하지 않으면 객체를 받지 않고 호출 API를 수행할 수 있음
* jsonPath()는  response를 받는 동시에 사용할 수 있고, 받은 후에도 사용할 수 있음
  * EX)
  ~~~java
  // 응답 받으면서 동시에 jsonpath()로 추출하는 case
  List<String> stationNames =
                RestAssured.given().log().all()
                        .when().get("/stations")
                        .then().log().all()
                        .extract().jsonPath().getList("name", String.class);
  
  // 응답 받고 jsonpath()로 추출하는 case
  ExtractableResponse saveResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();

  long deleteTargetStationId = saveResponse.body().jsonPath().getLong("id");
  String deleteTargetStationName = saveResponse.body().jsonPath().getString("name");
  ~~~
* 참조: https://www.programcreek.com/java-api-examples/?api=io.restassured.response.ExtractableResponse
### step2 - 지하철 노선 기능
#### API 명세
<pre>
노선 생성 시 상행종점역과 하행종점역을 등록합니다. 
따라서 이번 단계에서는 지하철 노선에 역을 맵핑하는 기능은 아직 없지만 노선 조회시 포함된 역 목록이 함께 응답됩니다.
</pre>
![img.png](step2_api_desc.png)
#### 기능 요구사항 및 인수조건
- [X] 지하철 노선 생성
  * When 지하철 노선을 생성하면
  * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
- [X] 지하철 노선 목록 조회
  * Given 2개의 지하철 노선을 생성하고
  * When 지하철 노선 목록을 조회하면
  * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
- [X] 지하철 노선 조회
  * Given 지하철 노선을 생성하고
  * When 생성한 지하철 노선을 조회하면
  * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
- [X] 지하철 노선 수정
  * Given 지하철 노선을 생성하고
  * When 생성한 지하철 노선을 수정하면
  * Then 해당 지하철 노선 정보는 수정된다
- [X] 지하철 노선 삭제
  * Given 지하철 노선을 생성하고
  * When 생성한 지하철 노선을 삭제하면
  * Then 해당 지하철 노선 정보는 삭제된다
#### Step2 회고
 * RestAssured의 body()의 파라미터는 map 형식이 아닌, controller에서 지정한 형식도 가능 
   * 테스트 목적에 맞게 request 형식으로 활용
 * given, when, then, given, when, then... 형식이면 TestFactory의 DynamicTest 활용
    * 테스트가 좀 더 가독성이 좋아짐
    * https://tecoble.techcourse.co.kr/post/2020-07-31-dynamic-test/
 * @Transactional 대상이면, save, update 메서드를 호출할 필요가 없음
    * using the findOne method call within a transactional method it has become managed from that point by the persistence provider.
    * https://stackoverflow.com/questions/46708063/springboot-jpa-need-no-save-on-transactional
### step3 - 구간 추가 기능
#### 요구상항 기능목록
- [ ] 역 사이에 새로운 역을 등록할 경우
  * 새로운 길이를 뺸 나머지를 새롭게 추가된 역과의 길이로 설정
  * ex) asis: A-7m-C -> A-4m-B 추가 -> tobe: A-4m-B-3m-C
  * 기존 Section 사이로 새로운 Section이 들어오는 경우
    * upstation 끼리 동일한 경우 / downstation 끼리 동일한 경우
      * distance 비교(요청온 distance가 작아야 함
      * 기존 Section, 새로 들어온 Section 기반 2개의 Section 생성 후 기존 Section 삭제 
      * upStation이 같으면, 기존 Section의 downStation을 새로 들어온 downStation 설정 
        -> 새로 들어온 Section의 upStation을 기존 Section의 downStation으로 설정
      * downStation이 같으면, 기존 Section의 upStation을 새로 들어온 upStation 설정 
        -> 새로 들어온  Section의 downStation을 기존 Section의 upStation으로 설정
- [ ] 새로운 역을 상행 종점으로 등록할 경우
  * 새로 들어온 Section의 upStation이 Null인 경우
  * 기존 upStation이 Null인 Section의 upStation을 새로 들어온 Section의 downStation으로 설정 
- [ ] 새로운 역을 하행 종점으로 등록할 경우
  * 새로 들어온 Section의 downStation이 Null인 경우
  * 기존 downStation이 Null인 Section의 downStation을 새로 들어온 Section의 upStation으로 설정
- [ ] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
- [ ] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
- [ ] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
#### 구간등록 API명세
HTTP request
<pre>
POST /lines/{id}/sections/ HTTP/1.1
Accept: */*
content-type: application/json; charset=UTF-8
host: localhost:52165

{
    "downStationId": 4,
    "upStationId": 2,
    "distance": 10
}
</pre>
#### JPA 관계 매핑
* 다대다 보다는 매핑테이블을 엔티티로 두는 방법을 활용
  * 기존에 Station과 Line이 있었다면 Line에 속하는 Station을 LineStation이라는 엔티티로 도출
  * Line과 LineStation을 @ManyToOne 관계로 설정
* 참고내용
  * 다대다 이슈: https://ict-nroo.tistory.com/127
  * 참고 코드: https://github.com/next-step/atdd-subway-map/blob/boorownie/src/main/java/nextstep/subway/line/domain/LineStations.java
  * JPA @Embedded And @Embeddable 문서 참고: https://www.baeldung.com/jpa-embedded-embeddable
  
