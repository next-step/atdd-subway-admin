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

## [STEP1] 요구사항

### 기능 요구사항

* 지하철역 인수 테스트를 완성
  * [x] 지하철역 목록 조회 인수 테스트 작성
  * [x] 지하철역 삭제 인수 테스트 작성

### 프로그래밍 요구사항

```text
각각의 테스트를 동작시키면 잘 동작하지만 한번에 동작시키면 실패할 수 있습니다. 
이번 단계에서는 이 부분에 대해 고려하지 말고 각각의 인수 테스트를 작성하는 것에 집중해서 진행하세요.
```

#### API 명세
* 지하철역 목록
```http request
GET /stations HTTP/1.1
Accept: application/json
Host: localhost:8080
```

```http request
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-COntrol-Request-Headers
Content-Type: application/json
Content-Length: 167

[ {
    "id" : 1,
    "name" : "지하철역이름"
}, {
    "id" : 2,
    "name" : "새로운지하철역이름"
}, {
    "id" : 3,
    "name" : "또다른지하철역이름"
} ]
```

* 지하철역 삭제
```http request
DELETE /stations/1 HTTP/1.1
Host: localhost:8080
```

```http request
HTTP/1.1 204 No Content
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-COntrol-Request-Headers
```

* 지하철역 노선 등록
```http request
POST /lines HTTP/1.1
Content-Type: application/json
Accept: application/json
Content-Length: 118
Host: localhost:8080

{
  "name" : "신분당선",
  "color" : "bg-red-600",
  "upStationId": 1,
  "downStationId" : 2,
  "distance" : 10
}
```

```http request
HTTP/1.1 201 Created
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Location: /lines/1
Content-Type: application/json
Content-Type: 193

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

* 지하철역 노선 목록
```http request
GET /lines HTTP/1.1
Accept: application/json
Host: localhost:8080
```

```http request
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json
Content-Type: 391

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
}, {
  "id" : 2,
  "name" : "분당선",
  "color" : "bg-green-600",
  "stations" : [ {
    "id" : 1,
    "name" : "지하철역"
  }, {
    "id" : 3,
    "name" : "새로운지하철역"
  } ]
} ]
```

* 지하철역 노선 조회
```http request
GET /lines/1 HTTP/1.1
Accept: application/json
Host: localhost:8080
```

```http request
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json
Content-Type: 193

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

* 지하철역 노선 수정
```http request
PUT /lines/1 HTTP/1.1
Content-Type: application/json
Content-Length: 58
Host: localhost:8080

{
  "name" : "다른분당선",
  "color" : "bg-red-600"
}
```

```http request
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
```

* 지하철역 노선 삭제
```http request
DELETE /lines HTTP/1.1
Host: localhost:8080
```

```http request
HTTP/1.1 204 No Content
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
```

* 지하철 구간 등록
```http request
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

* 지하철 구간 삭제
```http request
DELETE /lines/1/sections?stationId=2 HTTP/1.1
accept: */*
host: localhost:52165
```


## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/next-step/atdd-subway-admin/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/next-step/atdd-subway-admin/blob/master/LICENSE.md) licensed.
