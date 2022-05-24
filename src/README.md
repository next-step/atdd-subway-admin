### 기능 요구사항

* [ ] 지하철역 목록 조회 인수 테스트 작성
* [ ] 지하철역 삭제 인수 테스트 작성

### 프로그래밍 요구사항

* 인수 테스트의 재사용성과 가독성, 빠른 테스트 의도 파악을 위해 인수 테스트를 리펙토링 해야 한다
    * 한번에 테스트를 동작시키면 실패할 수 있으므로, 각각의 인수 테스트를 작성하는 것에 집중하여 진행한다

### API 명세

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
