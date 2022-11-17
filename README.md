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

## 1단계 - 지하철 노선도 관리
> 구현된 기능을 대상으로 인수 테스트를 작성하는 단계  
> `RestAssured`를 활용하여 인수 테스틀르 만들어 볼 것

### 요구사항
- 지하철역 관련 인수테스트를 완성
  - [x] 지하철역 목록 조회 인수 테스트 작성하기
  - [x] 지하철역 삭제 인수 테스트 작성하기

### 프로그래밍 요구사항
- [x] 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 할 것
> 각각의 테스트를 동작시키면 잘 동작하지만, 한번에 동작시키면 실패할 수 있음(테스트간 격리가 되지 않았기 때문에 데이터베이스에 데이터가 축적되기 때문)  
> 이번 단계에서는 고려할 사항이 아니라고 하였으나, 강의 내용을 기반으로 테스트간 격리방법 중 `DatabaseCleanup` utils을 작성하고 활용해보자.

### 1단계 피드백
- Q) given의 중복을 피할 수 있는 방법이 있는 지 문의
- A) 테스트 코드도 서비스 코드의 작성과 동일함. `지하철 생성` 역할의 메서드를 분리하여 공통적으로 사용

## 2단계 - 지하철 노선 기능
> - 제시된 인수 조건을 기반으로 기능 구현을 하는 단계
> - 기능 구현 전에 **_인수 조건을 만족하는 지 검증하는 인수 테스트를 먼저 만들고 기능 구현_**

### 구현 기능 목록
- [x] 지하철 노선 생성
  ```text
  When 지하철 노선을 생성하면
  Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
  ```
  ```http request
  POST /lines HTTP/1.1
  Content-Type: application/json
  Accept: application/json
  Content-Length: 118
  Host:  localhost:8080
  
  {
    "name" : "신분당선",
    "color" : "bg-red-600",
    "upStationId" : "1",
    "downStationId" : "2",
    "distance" : "10",
  }
  ```
  ```http response
  HTTP/1.1 201 Created
  Vary: Origin
  Vary: Access-Control-Request-Method
  Vary: Access-Control-Request-Headers
  Location: /lines/1
  Content-Type: application/json
  Content-Length: 193
  
  {
    "id" : 1,
    "name" : "신분당선",
    "color" : "bg-red-600",
    "stations" : [ {
      "id" : 1,
      "name" : "지하철역"
    }, {
      "id" : 2,
      "name" : "새로운지하철역"
    } ]
  }
  ```

- [x] 지하철 노선 목록 조회
  ```text
  Given 2개의 지하철 노선을 생성하고
  When 지하철 노선 목록을 조회하면
  Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
  ```
  ```http request
  GET /lines HTTP/1.1
  Accept:  application/json
  Host:  localhost:8080
  ```
  ```http response
  HTTP/1.1 200 OK
  Vary: Origin
  Vary: Access-Control-Request-Method
  Vary: Access-Control-Request-Headers
  Content-Type: application/json
  Content-Length: 391
  
  [ {
    "id" : 1,
    "name" : "신분당선",
    "color" : "bg-red-600",
    "stations" : [ {
      "id" : 1,
      "name" : "지하철역"
    }, {
      "id" : 2,
      "name" : "새로운지하철역"
    } ]
  } , {
    "id" : 1,
    "name" : "분당선",
    "color" : "bg-red-600",
    "stations" : [ {
      "id" : 1,
      "name" : "지하철역"
    }, {
      "id" : 3,
      "name" : "새로운지하철역"
    } ]
  } ]
  ```

- [x] 지하철 노선 조회
  ```text
  Given 지하철 노선을 생성하고
  When 생성한 지하철 노선을 조회하면
  Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
  ```
  ```http request
  GET /lines/1 HTTP/1.1
  Accept: application/json
  Host:  localhost:8080
  ```
  ```http response
  HTTP/1.1 200 OK
  Vary: Origin
  Vary: Access-Control-Request-Method
  Vary: Access-Control-Request-Headers
  Content-Type: application/json
  Content-Length: 193
  
  {
    "id" : 1,
    "name" : "신분당선",
    "color" : "bg-red-600",
    "stations" : [ {
      "id" : 1,
      "name" : "지하철역"
    }, {
      "id" : 2,
      "name" : "새로운지하철역"
    } ]
  }
  ```

- [x] 지하철 노선 수정
  ```text
  Given 지하철 노선을 생성하고
  When 생성한 지하철 노선을 수정하면
  Then 해당 지하철 노선 정보는 수정된다
  ```
  ```http request
  PUT /lines/1 HTTP/1.1
  Content-Type: application/json
  Content-Length: 58
  Host:  localhost:8080
  
  {
    "name" : "다른분당선",
    "color" : "bg-red-600"
  }
  ```
  ```http response
  HTTP/1.1 200 OK
  Vary: Origin
  Vary: Access-Control-Request-Method
  Vary: Access-Control-Request-Headers
  ```

- [x] 지하철 노선 삭제
  ```text
  Given 지하철 노선을 생성하고
  When 생성한 지하철 노선을 삭제하면
  Then 해당 지하철 노선 정보는 삭제된다
  ```
  ```http request
  DELETE /lines/1 HTTP/1.1
  Host:  localhost:8080
  ```
  ```http response
  HTTP/1.1 204 No Content
  Vary: Origin
  Vary: Access-Control-Request-Method
  Vary: Access-Control-Request-Headers
  ```

### 프로그래밍 요구사항
- 아래의 순서로 기능을 구현
  1. 인수 조건을 검증하는 인수 테스트 작성
  2. 인수 테스트를 충족하는 기능 구현
- 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 **인수 테스트를 서로 격리**
- 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링

### 힌트
- [x] 인수 테스트 격리 
  1. @DirtiesContext(더러운 상황(?)) : Spring Context를 이용한 테스트 동작 시 스프링 빈의 상태가 변경되면 해당 컨텍스트의 재사용이 불가능하여 컨텍스트를 다시 로드해야함
     - 격리 방법
       1. 스프링 빈의 상태가 변경되었다는 설정을 하는 애너테이션
       2. 대개의 경우 테스트 DB를 메모리 디비로 컨테이너에 띄워져 있는 상태이므로 **컨텍스트를 다시 로드시키면 DB 내용이 초기화됨** 
     - 장점 : 간단하게 설정 가능
     - 단점 : 느림
  2. @Sql : **_테스트 수행 시 특정 쿼리를 동작시키는 애너테이션_**
     - @DirtiesContext의 단점은 정말 말도안되게 느리다는 것이다.  
     근본적인 문제의 원인이 DB에 남아있는 데이터 때문이라면? --> 각 테스트 실행시마다 DB의 데이터를 깨끗하게 날리면 됨(`Table Truncate`)
- [x] 인수 테스트 리팩터링
  - 중복 코드 처리 : 중복되는 부분 별도 메소드로 역할을 분리하는 식으로 리펙터링 해 볼 것
  - 프론트엔드 : 노선 관리 페이지에서 기능이 제대로 동작하는 지 확인

### 2단계 피드백
- [x] 모든 유형의 변수에 final을 붙이는 습관을 들이자!
- [x] `@DataJpaTest`와 `DatabaseCleanup`를 동시에 사용할 수 있는 방법 모색
- [ ] repository에서 바로 get하기 보단 메소드를 감쌀 것
  ```java
  // 예시
  // As-is
  Station station = stationRepository.findById(1L).get();
  // To-be
  assertThat(stationRepository.findById).{method}
  ```