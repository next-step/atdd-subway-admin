# 지하철 노선도 미션
## 1단계 : 지하철역 인수 테스트 작성
### 기능 요구사항
* 지하철역 관련 인수 테스트 작성
  * 지하철역 목록 조회 인수 테스트 작성하기
  * 지하철역 삭제 인수 테스트 작성하기

### 프로그래밍 요구사항
* 인수 테스트의 재사용성과 가독성, 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링

### API 명세
* 지하철역 목록
  * HTTP request
    ```
    GET /stations HTTP/1.1
    Accept: application/json
    Host: localhost:8080
    ```
  * HTTP response
    ```
    HTTP/1.1 200 OK
    Vary: Origin
    Vary: Access-Control-Request-Method
    Vary: Access-Control-Request-Headers
    Content-Type: application/json
    Content-Length: 167
    
    [{
      "id": 1,
      "name": "지하철역이름"
    }, {
      "id": 2,
      "name": "새로운지하철역이름"
    }, {
      "id": 3,
      "name": "또다른지하철역이름"
    }]
    ```
* 지하철역 삭제
  * HTTP request
    ```
    DELETE /stations/1 HTTP/1.1
    Host: localhost:8080
    ```
  * HTTP response
    ```
    HTTP/1.1 204 No Content
    Vary: Origin
    Vary: Access-Control-Request-Method
    Vary: Access-Control-Request-Headers
    ```

## 2단계 : 지하철 노선 기능
### 기능 요구사항
* 지하철 노선 관리 기능 구현
* 인수 조건을 검증하는 인수 테스트를 작성

### 기능 목록
[X] 지하철 노선 생성\
[X] 지하철 노선 목록 조회\
[X] 지하철 노선 조회\
[X] 지하철 노선 수정\
[X] 지하철 노선 삭제

### 프로그래밍 요구사항
* 인수 조건을 검증하는 인수 테스트 작성
* 인수 테스트를 충족하는 기능 구현
* 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트 격리
* 인수 테스트 리팩터링(재사용성, 가독성)
  * 중복 코드 정리

### 인수 조건
[지하철 노선 생성]
```
When 지하철 노선을 생성하면
Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
```
\
[지하철 노선 목록 조회]
```
Given 2개의 지하철 노선을 생성하고
When 지하철 노선 목록을 조회하면
Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
```
\
[지하철 노선 조회]
```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 조회하면
Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
```
\
[지하철 노선 수정]
```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 수정하면
Then 해당 지하철 노선 정보는 수정된다
```
\
[지하철 노선 삭제]
```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 삭제하면
Then 해당 지하철 노선 정보는 삭제된다
```

### API 명세
* 지하철 노선 등록
  * HTTP request
    ```
    POST /lines HTTP/1.1
    Content-Type: application/json
    Accept: application/json
    Content-Length: 118
    Host: localhost:8080
  
    {
      "name": "신분당선",
      "color: "bg-red-600",
      "upStationId": 1,
      "downStationId": 2,
      "distance": 10
    }
    ```
  * HTTP response
    ```
    HTTP/1.1 201 Created
    Vary: Origin
    Vary: Access-Control-Request-Method
    Vary: Access-Control-Request-Headers
    Location: /lines/1
    Content-Type: application/json
    
    {
      "id": 1,
      "name": "신분당선"
      "color": "bg-red-600",
      "stations": [{
        "id": 1,
        "name": "지하철역"
      }, {
        "id": 2,
        "name": "새로운지하철역"
      }]
    }
    ```
* 지하철 노선 목록
  * HTTP request
    ```
    GET /lines/ HTTP/1.1
    Accept: application/json
    Host: localhost:8080
    ```
  * HTTP response
    ```
    HTTP/1.1 201 Created
    Vary: Origin
    Vary: Access-Control-Request-Method
    Vary: Access-Control-Request-Headers
    Content-Type: application/json
    Content-Length: 391
    
    [{
      "id": 1,
      "name": "신분당선",
      "color": "bg-red-600",
      "stations": [{
        "id": 1,
        "name": "지하철역"
      }, {
        "id": 2,
        "name": "새로운지하철역"
      }]
    }, { 
      "id": 2,
      "name": "분당선",
      "color": "bg-green-600",
      "stations": [{
        "id": 1,
        "name": "지하철역"
      }, {
        "id": 3,
        "name": "또다른지하철역"
      }]
    }]
    ```
* 지하철 노선 조회
  * HTTP request
    ```
    GET /lines/1 HTTP/1.1
    Accept: application/json
    Host: localhost:8080
    ```
  * HTTP response
    ```
    HTTP/1.1 200 OK
    Vary: Origin
    Vary: Access-Control-Request-Method
    Vary: Access-Control-Request-Headers
    Content-Type: application/json
    Content-Length: 193
    
    {
      "id": 1,
      "name": "신분당선",
      "color": "bg-red-600",
      "stations": [{
        "id": 1,
        "name": "지하철역"
      }, {
        "id": 2,
        "name": "새로운지하철역"
      }]
    }
    ```
* 지하철 노선 수정
  * HTTP request
    ```
    PUT /lines/1 HTTP/1.1
    Content-Type: application/json
    Content-Length: 58
    Host: localhost:8080
    
    {
      "name": "다른분당선",
      "color": "bg-red-600"
    }
    ```
  * HTTP response
    ```
    HTTP/1.1 200 OK
    Vary: Origin
    Vary: Access-Control-Request-Method
    Vary: Access-Control-Request-Headers
    ```
* 지하철 노선 삭제
  * HTTP request
    ```
    DELETE /lines/1 HTTP/1.1
    Host: localhost:8080
    ```
  * HTTP response
    ```
    HTTP/1.1 204 No Content
    Vary: Origin
    Vary: Access-Control-Request-Method
    Vary: Access-Control-Request-Headers
    ```