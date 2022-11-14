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

## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/next-step/atdd-subway-admin/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/next-step/atdd-subway-admin/blob/master/LICENSE.md) licensed.

## 인수 테스트 주도 개발(ATDD) 미션 
* 인수 테스트는 블랙 박스 테스트 기반
    * 클라이언트는 표면적으로 확인할 수 잇는 요소를 바탕으로 검증
    * 실제 발생할 수 있는 **시나리오**를 바탕으로 요구사항 작성
    * 내부 구현이나 기술에 의존적이지 않음
* 인수 조건 예시
~~~yml
Feature: 최단 경로 구하기
  Scenario: 지하철 최단 경로 조회
    Given: 지하철역들이 등록되어 있다.
    And: 지하철노선이 등록되어 있다.
    And: 지하철노선에 지하철역들이 등록되어 있다.
    When: 사용자는 출발역과 도착역의 최단 경로 조회를 요청한다.
    Then: 사용자는 최단 경로의 역 정보를 응답받는다.
~~~
###  Step1 요구사항
- [X] 지하철역 목록 조회 인수 테스트 작성하기
    * 2개의 지하철역을 생성 후 조회하면, 등록한 2개의 지하철을 응답 받아야 함.
    * List<Map<>> 방식으로 호출했을 때 다중 등록 가능여부 테스트 -> __불가__
    * Map의 name 값에 ','로 구분하여 생성 요청 시 다중 등록 가능여부 테스트 -> __불가__
    * 생성API 2번 각각 호출하여 2개의 지하철역 생성 후 조회
- [ ] 지하철역 삭제 인수 테스트 작성하기
