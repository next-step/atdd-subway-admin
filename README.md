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

<br>

## 💡 미션 소개 - 지하철 노선도 관리
### 도메인 설명
#### 지하철 역(station)
* 속성
  * 이름(name)
#### 지하철 구간(section)
* 지하철 (상행 방향)역과 (하행 방향역 사이의 연결정보)
* 속성
  * 길이(distance)
#### 지하철 노선(line)
* 지하철 구간의 모음, 구간에 포함된 지하철 역의 연결 정보
* 속성
  * 노선 이름(name)
  * 노선 색(color)

## 1단계 - 지하철역 인수 테스트 작성
### 요구 사항
* 구현된 기능을 대상으로 인수 테스트 작성
* RestAssured 활용한 인수 테스트 작성
#### 기능 요구사항
* [x] 지하철역 관련 인수테스트 완성
  * [x] 지하철역 목록 조회 인수 테스트 작성
  * [x] 지하철역 삭제 인수 테스트 작성
#### 프로그래밍 요구사항
* 인수테스트의 재사용성과 가독성, 빠른 테스트 의도 파악을 위해 인수 테스트 리팩토링
  * [x] JsonPath 활용
    * JsonPath를 사용하면 Response Dto 객체로 받을 필요 없이, 필요한 값만 추출하여 검증에 사용 가능

### API 명세
#### 지하철역 목록
**[ HTTP request ]**
```json
GET /stations HTTP/1.1
Accept: application/json
Host: localhost:8080
```
**[ HTTP response ]**
```json
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json
Content-Length: 167

[   {
    "id": 1,
    "name": "지하철역이름"
},  {
    "id": 2,
    "name": "새로운지하철역이름"
},  {
    "id": 3,
    "name": "또다른지하철역이름"
}   ]
```
#### 지하철역 삭제
**[ HTTP request ]**
```json
DELETE /stations/1 HTTP/1.1
Host: localhost:8080
```
**[ HTTP response ]**
```json
HTTP/1.1 204 No Content
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
```

### 개념 정리
#### 인수 테스트
* 인수 조건을 검증하는 테스트
* 실제 요청/응답하는 환경과 유사하게 테스트 환경을 구성
  **[ 인수 조건 예시 ]**
```text
Feature: 최단 경로 구하기

    Scenario: 지하철 최단 경로 조회
        Given 지하철역들이 등록되어 있다.
        And 지하철노선이 등록되어 있다.
        And 지하철노선에 지하철역들이 등록되어 있다.
        When 사용자는 출발역과 도착역의 최단 경로 조회를 요청한다.
        Then 사용자는 최단 경로의 역 정보를 응답받는다.
```
#### SpringBootTest
* 테스트 시 사용할 ApplicationContext를 쉽게 지정하게 도와줌
* webEnvironment 속성
  * MOCK: Mocking된 웹 환경 제공, MockMvc를 사용한 테스트 가능 (default)
  * RANDOM_PORT: 실제 웹 환경 구성
  * DEFINED_PORT: 실제 웹 환경 구성, 지정한 포트 listen
  * NONE: 아무런 웹 환경 구성하지 않음
* MockMvc VS WebTestClient VS RestAssured
  * MockMvc
    * @SpringBootTest의 webEnvironment.MOCK과 함께 사용 가능하며, mocking된 web environment에서 테스트
  * WebTestClient
    * @SpringBootTest의 webEnvironment.RANDOM_PORT나 DEFINED_PORT와 함께 사용(Netty 기본으로 사용)
  * RestAssured
    * 실제 web environment(tomcat) 사용하여 테스트

## 2단계 - 지하철 노선 기능
### 요구 사항
* 제시된 인수 조건 기반으로 기능 구현
  * 기능 구현 전 인수 조건을 만족하는지 검증하는 인수 테스트를 먼저 만들고 기능 구현
#### 기능 요구사항
* 요구사항 설명에서 제공되는 인수 조건을 기반으로 지하철 노선 관리 기능 구현
* 인수 조건 검증하는 인수 테스트 작성
* [x] 지하철 노선 생성
* [x] 지하철 노선 목록 조회
* [x] 지하철 노선 조회
* [x] 지하철 노선 수정
* [x] 지하철 노선 삭제
#### 요구사항 설명
**[ 지하철노선 생성 ]**
```text
When 지하철 노선을 생성하면
Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
```
**[ 지하철노선 목록 조회 ]**
```text
Given 2개의 지하철 노선을 생성하고
When 지하철 노선 목록을 조회하면
Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
```
**[ 지하철노선 조회 ]**
```text
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 조회하면
Then 생성한 지하철 노선의 정보를 응답받을 수 있다
```
**[ 지하철노선 수정 ]**
```text
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 수정하면
Then 해당 지하철 노선 정보는 수정된다
```

**[ 지하철노선 삭제 ]**
```text
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 삭제하면
Then 해당 지하철 노선 정보는 삭제된다
```
#### 프로그래밍 요구사항
* 인수 조건 검증하는 인수 테스트 작성 -> 인수 테스트를 충족하는 기능 구현
* 인수 테스트의 결과가 다른 인수 테스트에 영향 주지 않도록 인수 테스트 격리(@DirtiesContext, @Sql, Table Truncate)
  * [x] @Sql 활용하여 격리 진행
* 인수 테스트의 재사용성, 가독성, 빠른 테스트 의도 파악을 위해 인수 테스트 리팩토링
  * 중복되는 부분 발생 시, 리팩토링하며 부가적인 코드는 테스트로부터 분리하여 테스트에 집중하도록 함
### API 명세
![지하철 노선 API 명세](https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/05ea24170d7745b48add07a114b79562)

## 3단계 - 구간 추가 기능
### 요구 사항
* 제시된 인수 조건 기반으로 기능 구현
  * 기능 구현 전 인수 조건을 만족하는지 검증하는 인수 테스트를 먼저 만들고 기능 구현
#### 기능 요구사항
* 지하철 구간 추가 기능 구현
* 요구사항을 정의한 인수 조건 추출
* 인수 조건을 검증하는 인수 테스트 작성
  * 예외 케이스에 대한 검증 포함
* [x] 역 사이에 새로운 역을 등록할 경우 
* [x] 새로운 역을 상행 종점으로 등록할 경우 
* [x] 새로운 역을 하행 종점으로 등록할 경우 
* [x] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음 
* [x] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음 
* [x] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
#### 프로그래밍 요구사항
* 인수 테스트 주도 개발 프로세스에 맞추어 기능 구현할 것
  * [x] 요구사항 설명을 참고해 인수 조건 정의
  * [x] 인수 조건을 검증하는 인수 테스트 작성
  * [x] 인수 테스트를 충족하는 기능 구현
* [x] 인수 조건은 인수 테스트 메소드 상단에 주석 작성
  * 뼈대 코드의 인수 테스트 참고
  * 인수 테스트 서로 격리 및 재사용성과 가독성, 빠른 테스트 의도 파악을 위해 리팩토링

### JPA 관계 매핑
* 지하철역은 여러개의 지하철 노선에 포함될 수 있다.
  * 지하철역:노선 = 1:N
* 노선에는 여러 지하철역이 포함될 수 있다.
  * 노선:지하철역 = 1:N
* 따라서, 지하철역:노선 = N:N -> N:1과 1:N 관계로 풀어서 매핑하기 위해 중간에 Section 엔티티 생성
  * 지하철역과 노선매핑:노선 = N:1
  * 지하철역과 노선매핑:지하철 = N:1

### API 명세
**[ HTTP request ]**
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
[ HTTP response ]
```json
HTTP/1.1 201 Created
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json
Content-Length: 230

{
    "id": 1,
    "name": "신분당선",
    "color": "bg-red-600",
    "stations": [ {
        "id": 1,
        "name": "지하철역"
    },  {
        "id": 3,
        "name": "기존지하철역"
    },  {
        "id": 4,
        "name": "새로운지하철역"
    } ]
}
```

## 4단계 - 구간 제거 기능
### 요구 사항
* 제시된 인수 조건 기반으로 기능 구현
  * 기능 구현 전 인수 조건을 만족하는지 검증하는 인수 테스트를 먼저 만들고 기능 구현
#### 기능 요구사항
* 지하철 구간 제거 기능 구현
* 요구사항을 정의한 인수 조건 추출
* 인수 조건을 검증하는 인수 테스트 작성
  * 예외 케이스에 대한 검증 포함
* [ ] 상행 종점을 제거하는 경우 -> 제거하고자 하는 역이 포함된 section이 1개(UpStation으로 포함)
  * 상행 종점이 제거될 경우, 해당 역 다음역이 상행 종점이 됨 -> A - B - C가 있을 때 A를 제거하면 line의 distance도 바뀌어야 하고, A가 속한 section도 제거되야 함
* [ ] 하행 종점을 제거하는 경우 -> 제거하고자 하는 역이 포함된 section이 1개(DownStation으로 포함)
  * 하행 종점이 제거될 경우, 해당 역 전역이 하행 종점이 됨 -> A - B - C가 있을 때 C를 제거하면 line의 distance도 바뀌어야 하고, C가 속한 section도 제거되야 함
* [ ] 가운데 역을 제거하는 경우 -> 제거하고자 하는 역이 포함된 section이 2개인 상황
  * 중간역이 제거될 경우, section 재배치 -> A - B - C가 있을 때 B가 제거되면, A - B section은 제거하고, B - C section은 upStation이 A로 바뀌어야 하고, A - B, B - C간 distance로 section의 distane 변경 필요
* [ ] 구간이 하나인 노선에서 역을 제거하려 할 경우 제거할 수 없음
* [ ] 존재하지 않는 구간(노선에 존재하지 않는 역)을 제거하려 한다면 제거할 수 없음
#### 프로그래밍 요구사항
* 인수 테스트 주도 개발 프로세스에 맞추어 기능 구현할 것
  * [ ] 요구사항 설명을 참고해 인수 조건 정의
  * [ ] 인수 조건을 검증하는 인수 테스트 작성
  * [ ] 인수 테스트를 충족하는 기능 구현
* [ ] 인수 조건은 인수 테스트 메소드 상단에 주석 작성
  * 뼈대 코드의 인수 테스트 참고
  * 인수 테스트 서로 격리 및 재사용성과 가독성, 빠른 테스트 의도 파악을 위해 리팩토링

### API 명세
**[ HTTP request ]**
```json
DELETE /lines/1/sections?stationId=2 HTTP/1.1
accept: */*
host: localhost:52165
```
