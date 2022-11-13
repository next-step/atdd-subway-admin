## 1단계 - 지하철역 인수 테스트 작성
### 기능 요구사항
-[x] 지하철역 관련 인수 테스트를 완성하세요.
  - [x] 지하철역 목록 조회 인수 테스트 작성하기
  - [x] 지하철역 삭제 인수 테스트 작성하기

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
<br/>
<hr/>

## 2단계 - 지하철 노선 기능
### 기능 요구사항
- 요구사항 설명에서 제공되는 인수 조건을 기반으로 지하철 노선 관리 기능을 구현하세요.
- 인수 조건을 검증하는 인수 테스트를 작성하세요.

#### 기능 목록
- [x] 지하철 노선 생성
- [x] 지하철 노선 목록 조회
- [x] 지하철 노선 조회
- [x] 지하철 노선 수정
- [x] 지하철 노선 삭제

### 프로그래밍 요구사항
- 아래의 순서로 기능을 구현하세요.
  - 인수 조건을 검증하는 인수 테스트 작성
  - 인수 테스트를 충족하는 기능 구현
- 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트를 서로 격리 시키세요.
- 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요.

### 요구사항 설명
<details>
<summary>인수조건</summary>

**지하철노선 생성**
```text
When 지하철 노선을 생성하면
Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
```

**지하철노선 목록 조회**
```text
Given 2개의 지하철 노선을 생성하고
When 지하철 노선 목록을 조회하면
Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
```

**지하철노선 조회**
```text
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 조회하면
Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
```

**지하철노선 수정**
```text
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 수정하면
Then 해당 지하철 노선 정보는 수정된다
```

**지하철노선 삭제**
```text
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 삭제하면
Then 해당 지하철 노선 정보는 삭제된다
```
</details>