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