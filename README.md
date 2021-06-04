<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <img alt="npm" src="https://img.shields.io/badge/npm-%3E%3D%205.5.0-blue">
  <img alt="node" src="https://img.shields.io/badge/node-%3E%3D%209.3.0-blue">
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

---

<details>
<summary style="font-Weight: bold; font-siz:25px;">인수 테스트 실습 미션</summary>
<div>

## 기능 구현 
* 지하철 노선 생성 기능의 인수 테스트를 작성하기
  * LineAcceptanceTest의 createLine, createLine2메서드 구현
    
## 주요 기능
* AcceptanceTest 를 상속받아 Port를 Test별 공통적으로 적용
  * ```@LocalServerPort```
    
* DatabaseCleanup 을 setUp을 통해 모든 Entity의 테이블을 Truncate 
* ```@ExceptionHandler``` Annotation으로 예외처리를 핸들러해줌

</div>
</details>

---
## 1단계 - 지하철 노선 관리

### 요구 사항
* 지하철 노선 관련 기능의 인수 테스트를 작성하기
  -[ ] ```LineAcceptanceTest``` 를 모두 완성
  
* 지하철 노선 관련 기능 구현하기
  -[ ] 인수 테스트가 모두 성공할 수 있도록 ```LineController```를 통해 요청을 받고 처리하는 기능을 구현
* 인수 테스트 리팩터링
  -[ ] 인수 테스트의 각 스텝들을 메서드로 분리하여 재사용
  
### RestAssured
> given
>> 요청을 위한 값을 설정 (header, content type 등)<br>
>> body가 있는 경우 body 값을 설정 함

> when
>>요청의 url와 method를 설정
 
>then
>>응답의 결과를 관리<br>
>>response를 추출하거나 response 값을 검증할 수 있음

### 구현 목록
* 지하철 목록 조회
```
GET /lines HTTP/1.1
accept: application/json
host: localhost:49468
```
* 지하철 노선 조회
```
GET /lines/1 HTTP/1.1
accept: application/json
host: localhost:49468
```
* 지하철 노선 수정
```
PUT /lines/1 HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8
content-length: 45
host: localhost:49468

{
    "color": "bg-blue-600",
    "name": "구분당선"
}
```
* 지하철 노선 삭제
```
DELETE /lines/1 HTTP/1.1
accept: */*
host: localhost:49468
```
