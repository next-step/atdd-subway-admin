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
* [ ] 지하철역 관련 인수테스트 완성
  * [ ] 지하철역 목록 조회 인수 테스트 작성
  * [ ] 지하철역 삭제 인수 테스트 작성
#### 프로그래밍 요구사항
* 인수테스트의 재사용성과 가독성, 빠른 테스트 의도 파악을 위해 인수 테스트 리팩토링
  * [ ] JsonPath 활용
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
