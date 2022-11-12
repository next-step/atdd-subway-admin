## 1단계 - 지하철역 인수 테스트 작성
### 기능 요구사항
-[ ] 지하철역 관련 인수 테스트를 완성하세요.
  - [x] 지하철역 목록 조회 인수 테스트 작성하기
  - [ ] 지하철역 삭제 인수 테스트 작성하기

### 프로그래밍 요구사항
- 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요.
```text
각각의 테스트를 동작시키면 잘 동작하지만 한번에 동작시키면 실패할 수 있습니다.
이번 단계에서는 이 부분에 대해 고려하지 말고 각각의 인수 테스트를 작성하는 것에 집중해서 진행하세요.
```
### API 명세
##### 지하철역 목록
###### HTTP request
```http request
GET /station HTTP/1.1
Accept: application/json
Host: localhost:8080
```
###### HTTP response
```http request
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json
Content-Length: 167

[{
    "id" : 1,
    "name":"지하철역이용"
}, {
    "id" : 2,
    "name":"새로운지하철역이용"
},{
    "id" : 3,
    "name":"또다른지하철역이용"
}]
```

##### 지하철역 삭제
###### HTTP request
```http request
DELETE /station/1 HTTP/1.1
Host: localhost:8080
```
###### HTTP response
```http request
HTTP/1.1 204 No Content
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
```