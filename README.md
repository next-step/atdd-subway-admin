# 1단계 - 지하철역 인수 테스트 작성

## 기능 요구사항

* RestAssured를 활용
* 지하철역 관련 인수 테스트 완성
    * 지하철역 목록 조회
    * 지하철역 삭제

## 인수 조건 및 시나리오

```
# 지하철역을 생성한다

When 지하철역을 생성하면
Then 지하철역이 생성된다
Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
```

```
# 기존에 존재하는 지하철역 이름으로 지하철역을 생성한다

Given 지하철역을 생성하고
When  기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
Then  지하철역 생성이 안된다
```

```
# 지하철역을 조회한다

Given 2개의 지하철역을 생성하고
When  지하철역 목록을 조회하면
Then  2개의 지하철역을 응답 받는다
```

```
# 지하철역을 제거한다

Given 지하철역을 생성하고
When  그 지하철역을 삭제하면
Then  그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
```

## API 명세

### 지하철역 목록

### HTTP request

```
GET /stations HTTP/1.1
Accept: application/json
Host: localhost:8080
```

### HTTP response

```
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
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

## 지하철역 삭제

### HTTP request

``` 
DELETE /stations/1 HTTP/1.1
Host: localhost:8080
```

### HTTP response

```
HTTP/1.1 204 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
```

### 인수 테스트 리펙터링 힌트

* **JsonPath**
    * Json 문서를 읽어오는 DSL
    * JsonPath를 사용하면 Response Dto 객체로 받을 필요 없이 값만 추출할 수 있음
      ![](jsonPathExample.png)

# 2단계 - 지하철 노선 기능

## 기능 요구사항

* 지하철 노선 관리 기능 구현
* 인수 테스트 작성

### 기능 목록

* 지하철 노선 생성
* 지하철 노선 목록 조회
* 지하철 노선 조회
* 지하철 노선 수정
* 지하철 노선 삭제

## 프로그래밍 요구사항

* 인수 조건을 검중하는 인수 테스트를 먼저 작성한 후, 해당하는 기능을 구현
* 인수 테스트를 서로 격리 시키기
* 인수테스트의 재사용성, 가독성, 빠른 테스트 의도 파악을 위해 테스트를 리펙터링 하기

### 인수 조건

### 지하철 노선 생성

```
When: 지하철 노선을 생성하면
Then: 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
```

### 지하철 노선 목록 조회

```
Given: 2개의 지하철 노선을 생성하고
When: 지하철 노선 목록을 조회하면
Then: 지하철 노선 목록 조회 시 2개의 노선을 찾을 수 있다
```

### 지하철 노선 조회

```
Given: 지하철 노선을 생성하고
When: 생성한 지하철 노선 목록을 조회하면
Then: 생성한 지하철 노선의 정보를 응답받을 수 있다.
```

### 지하철 노선 수정

```
Given: 지하철 노선을 생성하고
When: 생성한 지하철 노선을 수정하면
Then: 해당 지하철 노선 정보는 수정된다
```

### 지하철 노선 삭제

```
Given: 지하철 노선을 생성하고
When: 생성한 지하철 노선을 삭제하면
Then: 해당 지하철 노선 정보는 삭제된다
```

## API 명세

### 지하철 노선

### 지하철 노선 등록

### HTTP request

```
POST /lines HTTP/1.1
Accept: application/json
Host: localhost:8080
Content-Length: 118

{
    "name" : "신분당선",
    "color" : "bg-red-600",
    "upStationId" : 1,
    "downStationId" : 2,
    "distance" : 10
}
```

### HTTP response

```
HTTP/1.1 201 CREATED
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
        "name" : "새로운 지하철역"
    }  ]
}
```

### 지하철 노선 목록

### HTTP request

```
GET /lines HTTP/1.1
Accept: application/json
Host: localhost:8080
```

### HTTP response

```
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json
Content-Length: 391
 
[   {
        "id" : 1,
        "name" : "신분당선",
        "color" : "bg-red-600",
        "stations" : [ {
            "id" : 1,
            "name" : "지하철역"
        }, {
            "id" : 2,
            "name" : "새로운 지하철역"
        }  ]
}, {
        "id" : 2,
        "name" : "분당선",
        "color" : "bg-green-600",
        "stations" : [ {
            "id" : 1,
            "name" : "지하철역"
        }, {
            "id" : 3,
            "name" : "또다른 지하철역"
        }  ]
}   ]
```

### 지하철 노선 조회

### HTTP request

```
GET /lines/1 HTTP/1.1
Accept: application/json
Host: localhost:8080
```

### HTTP response

```
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
            "name" : "새로운 지하철역"
        }  ]
}
```

### 지하철 노선 수정

### HTTP request

```
PUT /lines/1 HTTP/1.1
Accept: application/json
Host: localhost:8080
Content-LengTh: 58

{
    "name" : "다른분당선",
    "color" : "bg-red-600"
}
```

### HTTP response

```
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
```

### 지하철 노선 삭제

### HTTP request

```
DELETE /lines/1 HTTP/1.1
Host: localhost:8080
```

### HTTP response

```
HTTP/1.1 204 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
```

### 힌트

### 인수 테스트 격리

### @DirtiesContext

* Spring Bean의 상태를 오염시키면 컨텍스트 재사용이 불가능하여, 컨텍스트를 다시 로드해야함
* 스프링 빈의 상태가 오염되었다는 설정을 하는 어노테이션
* H2 DB를 사용하는 테스트 환경에서, 컨텍스트를 새로 띄우면 기존 DB 내용이 초기화 된다

### @Sql

* 테스트 수행 시 특정 쿼리를 동작시키는 어노테이션

### Table Truncate

* 테이블을 조회하여 각 테이블을 Truncate 시켜주는 방법

### 인수 테스트 리펙터링

* **중복 코드 처리**
    * 지하철역과 노선 테스트의 중복되는 Feature를 적절히 분리하도록 한다