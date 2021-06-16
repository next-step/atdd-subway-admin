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

<details>
<summary style="font-Weight:bold;font-size=25px;">1단계 - 지하철 노선 관리 </summary>
<div>

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
</div>
</details>

---

<details>
<summary style="font-Weight: bold; font-siz:25px;"> 2단계 - 인수 테스트 리팩터링 </summary>
<div>

### 요구사항

* 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기
  * 두 종점역은 구간의 형태로 관리되어야 함
* 노선 조회 시 응답 결과에 역 목록 추가하기
  * 상행역 부터 하행역 순으로 정렬되어야 함
  
### 요구사항 설명
#### 노선 생성 시 두 종점역 추가하기
* 인수 테스트와 DTO 등 수정
```java
public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;       // 추가
    private Long downStationId;     // 추가
    private int distance;           // 추가
    ...
}
```
#### 노선 객체에서 구간 정보를 관리하기
* 노선 생성시 전달되는 두 종점역은 노선의 상태로 관리되는 것이 아니라 구간으로 관리되어야 함
```java
public class Line {
    ...
    private List<Section> sections;
    ...
}
```

#### 노선의 역 목록을 조회하는 기능 구현하기
* 노선 조회 시 역 목록을 함께 응답할 수 있도록 변경
* 노선에 등록된 구간을 순서대로 정렬하여 상행 종점부터 하행 종점까지 목록을 응답하기
* 필요시 노선과 구간(혹은 역)의 관계를 새로 맺기

### 추가 구현 list
* Sections 일급컬렙션 생성
* section 과 station 관계 설정
* (수정) section 에 upStation과 downStation, distance가 상태값으로 있는 구조로 수정
  * 1호선엔 a-b 구간 b-c 구간 으로 관리 되어야함

</div>
</details>

---

## 3단계 - 구간 추가 기능

### 요구사항 정의
#### 역 사이에 새로운 역을 등록할 경우
* 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
```
asis A-C 7m  추가 구간 A-B 4m
beto A-B, B-C 각각 4m, 3m
```
#### 새로운 역을 상행 종점으로 등록할 경우
```
asis A-B 4m 추가 구간 C-A 3m
beto C-A, A-B 각각 3m, 4m
```
#### 새로운 역을 하행 종점으로 등록할 경우
```
asis A-B 4m 추가 구간 B-C 3m
beto A-B, B-C 각각 4m, 3m
```

### 예외 케이스
* 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
* 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
* 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
    
### 수정 내역 피드백
* Line과 Station 의 N:M 연관관계를 매핑하는 LineStation add 로직 관계를 알 수 있도록 구현
* 동일 선의 Service 레이어는 서로 의존을 지양한다. -> 계속해서 의존하게 되면 스파게티 코드가 될 수 있음.
* (추가 수정) 각 컴포넌트의 공통 Exception 을 핸들러해주는 ```@RestControllerAdvice``` 를 구현 ( 컨트롤러의 모든 Exception을 핸들함 )
    * ```@RestController``` 는 하나의 컨트롤러만 핸들러
