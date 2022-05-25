### 기능 요구사항

* [x] 지하철역 목록 조회 인수 테스트 작성
* [x] 지하철역 삭제 인수 테스트 작성
* [ ] 지하철 노선 관리 기능을 인수 조건을 기반으로 구현
* [ ] 지하철 노선 관리 인수 조건을 검증하는 인수 테스트 작성

### 프로그래밍 요구사항

* 인수 테스트의 재사용성과 가독성, 빠른 테스트 의도 파악을 위해 인수 테스트를 리펙토링 해야 한다
  * 한번에 테스트를 동작시키면 실패할 수 있으므로, 각각의 인수 테스트를 작성하는 것에 집중하여 진행한다
* 기능 구현은 아래와 같은 순서로 구현한다
  1. 인수 조건을 검증하는 인수 테스트 작성
  2. 인수 테스트를 충족하는 기능 구현
* 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트 간에 격리해야 한다

### 요구사항

#### 지하철 노선

* 지하철 노선 생성

```
When 지하철 노선을 생성하면
Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
```

* 지하철 노선 목록 조회

```
Given 2개의 지하철 노선을 생성하고
When 지하철 노선 목록을 조회하면
Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
```

* 지하철 노선 조회

```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 조회하면
Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
```

* 지하철 노선 수정

```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 수정하면
Then 해당 지하철 노선 정보는 수정된다
```

* 지하철 노선 삭제

```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 삭제하면
Then 해당 지하철 노선 정보는 삭제된다
```

### API 명세

#### 지하철역

* 지하철역 목록 조회

```http request
GET /stations HTTP/1.1

Accept: application/json
Host: localhost:8080
```

```http response
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json
Content-Length: 167

[ {
  "id" : 1,
  "name": "지하철역이름"
}, {
  "id" : 2,
  "name": "새로운지하철역이름"
}, {
  "id" : 3,
  "name": "또다른지하철역이름"
} ]
```

* 지하철역 삭제

```http request
DELETE /stations/1 HTTP/1.1
Host: localhost:8080
```

```http response
HTTP/1.1 204 No Content
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
```

#### 지하철 노선

* 지하철 노선 등록

```http request
POST /lines HTTP/1.1
Content-Type: application/json
Accept: application/json
Content-Length: 118
Host: localhost:8080

{
  "name" : "신분당선",
  "color" : "bg-red-600",
  "upStationId" : 1,
  "downStationId" : 2,
  "distance": 10
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

* 지하철 노선 목록

```http request
GET /lines HTTP/1.1
Accept: application/json
Host: localhost:8080
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
}, {
  "id" : 2,
  "name" : "분당선",
  "color" : "bg-green-600",
  "stations" : [ {
    "id" : 1,
    "name" : "지하철역"
  }, {
    "id" : 3,
    "name" : "또다른지하철역"
  } ]
} ]
```

* 지하철 노선 조회

```http request
GET /lines/1 HTTP/1.1
Accept: application/json
Host: localhost:8080
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

* 지하철 노선 수정

```http request
PUT /lines/1 HTTP/1.1
Accept: application/json
Content-Length: 58
Host: localhost:8080

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

* 지하철 노선 삭제

```http request
DELETE /lines/1 HTTP/1.1
Host: localhost:8080
```

```http response
HTTP/1.1 204 No Content
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
```
