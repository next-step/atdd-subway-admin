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
## 1단계 - 지하철 노선 관리
### 지하철 노선 관리 기능을 구현하기
* [x] 기능 구현 전 인수 테스트 작성
  * [x] 생성
  * [x] 목록 조회
  * [x] 조회
  * [x] 수정
  * [x] 삭제
* [x] 기능 구현 후 인수 테스트 리팩터링
  * [x] 생성
  * [x] 목록 조회
  * [x] 조회
  * [x] 수정
  * [x] 삭제

### 1단계 피드백 사항
* [x] ErrorResponse 항목에 대한 고민 필요
* [x] CustomException에 대한 고민 필요
* [x] Controller return 값 Generic Type 명시
* [x] Test 결과 확인에 대한 고민 필요

## 2단계 - 인수 테스트 리팩터링
### API 변경 대응하기
* [x] 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기
  * 두 종점역은 구간의 형태로 관리되어야 함
* [x] 노선 조회 시 응답 결과에 역 목록 추가하기
  * 상행역 부터 하행역 순으로 정렬되어야 함

### 2단계 피드백 사항
* [x] 오버엔지니어링을 지양하고 사용하지 않는 메소드는 작성하지 않는다.
* [x] 예외 메시지 신경써서 자세한 정보 제공
* [x] 엔티티 기본 생성자 protected
* [x] Stations 의 적절한 책임에 대해 고민해보기
* [x] Distance 의 적절한 책임에 대해 고민해보기
* [x] Test Fixture 클래스 생성
* [x] getUpToDownStations() 메소드에 대한 단위 테스트 작성
* [x] 변수명 작성에 더 신경쓰기
* [x] Distance 객체에서 setter 제거하기
* [ ] Sections.getUpToDownStations() 메소드 리팩터링
* [ ] 엔티티 클래스에서의 equals overriding 시 주의사항 학습하고 적용
* [ ] SectionsTest.지하철_노선_생성_요청() -> 지하철_노선_등록되어_있음() 메소드로 변경, 요청 및 검증 실행 필요!


## 지하철 노선 관리 인수 조건 정의
```
Feature: 지하철 노선(Line) 생성 / 목록 조회 / 조회 / 수정 / 삭제
  Scenario: 지하철 노선 생성
      When 사용자는 지하철 노선 생성을 요청한다.
      Then 사용자는 등록 결과를 응답받는다.
  Scenario: 지하철 노선 목록 조회
     Given 노선들이 등록되어 있다.
      When 사용자는 지하철 노선 목록 조회를 요청한다.
      Then 사용자는 목록 조회 결과를 응답받는다.
  Scenario: 지하철 노선 조회
     Given 노선이 등록되어 있다.
      When 사용자는 지하철 노선 조회를 요청한다.
      Then 사용자는 조회 결과를 응답받는다.
  Scenario: 지하철 노선 수정
     Given 노선이 등록되어 있다.
      When 사용자는 지하철 노선 수정을 요청한다.
      Then 사용자는 수정 결과를 응답받는다.
  Scenario: 지하철 노선 삭제
     Given 노선이 등록되어 있다.
      When 사용자는 지하철 노선 삭제를 요청한다.
      Then 사용자는 삭제 결과를 응답받는다.
```
