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

---

## 🚀 1단계 - 지하철역 인수 테스트 작성
### ✏️ 기능 요구사항
- [x] 지하철역 관련 인수 테스트를 완성
  - [x] 지하철역 목록 조회 인수 테스트 작성
  - [x] 지하철역 삭제 인수 테스트 작성

### 프로그래밍 요구사항
- [x] 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링

---

## 🚀 2단계 - 지하철 노선 기능
### ✏️ 기능 요구사항
- [ ] 요구사항 설명에서 제공되는 인수 조건을 기반으로 지하철 노선 관리 기능을 구현하세요.
- [ ] 인수 조건을 검증하는 인수 테스트를 작성하세요.

### ✏️ 기능 목록
- [x] 지하철 노선 생성
  - [x] 테스트 코드, 리팩토링
- [x] 지하철 노선 목록 조회
  - [x] 테스트 코드, 리팩토링
- [x] 지하철 노선 조회
  - [x] 테스트 코드, 리팩토링
- [x] 지하철 노선 수정
  - [x] 테스트 코드
  - [ ] 리팩토링
- [ ] 지하철 노선 삭제

### ✏️리팩토링
  - [ ] StationConstant 클래스를 만들고 ROOT_PATH 상수를 사용한다.
  - [ ] 각 엔티티의 공통 필드를 BaseEntity에 정의한다.
  - [ ] 한글 메서드명으로 변경한다.
  - [ ] 코드의 가독성을 위해 감싸는 방식이 아닌 다른 방식으로 사용,getResponseId(createResponse).
  - [ ] 매 요청마다 map을 만들어 보내는 코드 리팩토링 -> map 이름 변경, 코드 모듈화. HashMap 제네릭

### 🙋🏻질문 할 것
- 정상 저장 검증을 위해 매번 테스트 마다 데이터 전부를 검증 할 필요가 있을까요? (line, stations 데이터)  
  노선_정보_확인

### ✏️ 프로그래밍 요구 사항
- [ ] 인수 조건을 검증하는 인수 테스트 작성
  - [ ] 인수 테스트를 충족하는 기능 구현
  - [ ] 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트를 서로 격리 시키세요.
- [ ] 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요.


### 🤝 인수 조건

지하철노선 생성
```
When 지하철 노선을 생성하면  
Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.  
```
<br>

지하철노선 목록 조회
```
Given 2개의 지하철 노선을 생성하고  
When 지하철 노선 목록을 조회하면  
Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.  
```
<br>

지하철노선 조회
```
Given 지하철 노선을 생성하고  
When 생성한 지하철 노선을 조회하면  
Then 생성한 지하철 노선의 정보를 응답받을 수 있다.  
```
<br>

지하철노선 수정
```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 수정하면
Then 해당 지하철 노선 정보는 수정된다.
```
<br>

지하철노선 삭제
```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 삭제하면
Then 해당 지하철 노선 정보는 삭제된다.
```
