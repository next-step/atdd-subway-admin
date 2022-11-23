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

> m1 mac -> nvm 사용, nodejs v14.21.1 (lts) 설치 (npm v6.14.17)

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


---

## 미션수행

    구현된 기능을 대상으로 인수 테스트를 작성하는 단계입니다.
    RestAssured를 활용하여 인수 테스트를 만들어 보세요

### [ 1단계 - 지하철역 인수 테스트 작성 ]
### 기능 요구사항
- 지하철역 관련 인수 테스트를 완성하세요.
  - 지하철역 목록 조회 인수 테스트 작성하기
  - 지하철역 삭제 인수 테스트 작성하기


### 프로그래밍 요구사항
인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요.
> 각각의 테스트를 동작시키면 잘 동작하지만 한번에 동작시키면 실패할 수 있습니다.
> <br>이번 단계에서는 이 부분에 대해 고려하지 말고
> <br>각각의 인수 테스트를 작성하는 것에 집중해서 진행하세요. 

### API 명세
> #### 지하철역 목록
> ###### HTTP request
> ```http request
> GET /stations HTTP/1.1
> Accept: application/json
> Host: localhost:8080
> ```
> ###### HTTP response
> ```http request
> HTTP/1.1 200 OK
> Vary: Origin
> Vary: Access-Control-Request-Method
> Vary: Access-Control-Request-Headers
> Content-Type: application/json
> Content-Length: 167
> 
> [ {
>     "id" : 1,
>     "name" : "지하철역이름"
> }, {
>   "id" : 2,
>   "name" : "새로운지하철역이름"
> }, {
>   "id" : 3,
>   "name" : "또다른지하철역이름"
> } ]
> ```
> <br>
> 
> #### 지하철역 삭제
> ###### HTTP request
> ```http request
> DELETE /stations/1 HTTP/1.1
> Host: localhost:8080
> ```
> ###### HTTP response
> ```http request
> HTTP/1.1 204 No Content
> Vary: Origin
> Vary: Access-Control-Request-Method
> Vary: Access-Control-Request-Headers
> ```

<br>

### 힌트
#### 인수 테스트 리팩터링

#### JsonPath
- Json 문서를 읽어오는 DSL
- JsonPath를 사용하면 Response Dto 객체로 받을 필요 없이 필요한 값만 추출하여 검증에 사용할 수 있음