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

## 1단계 - 지하철 노선 관리
### 기능 목록
* Feature: 지하철 노선 관련 기능
    * [x] Scenario: 지하철 노선 생성한다
        * when: 지하철 노선 생성 요청 한다.
        * then: 지하철 노선이 생성된다.
    * [x] Scenario: 이미 등록된 지하철 노선 이름으로 지하철 노선을 생성한다.
        * given: 지하철 노선이 등록되어 있다.
        * when: 지하철 노선을 생성 요청 한다.
        * then: 지하철 노선 생성이 실패 한다.
    * [x] Scenario: 지하철 노선 목록 조회
        * given: 지하철 노선 분당선, 2호선이 등록되어 있다.
        * when: 지하철 노선 목록 조회를 요청 한다.
        * then: 지하철 노선 목록이 응답된다.
        * then: 지하철 노선 목록에 분당선, 2호선이 포함되어 있다.
    * [x] Scenario: 지하철 노선 단건 조회
        * given: 지하철 노선 분당선이 등록되어 있다.
        * when: 지하철 노선 분당선 조회를 요청한다.
        * then: 지하철 노선이 응답된다.
        * then: 지하철 노선 분당선이 응답 된다.
    * [x] Scenario: 존재하지 않는 지하철 노선 아이디를 단건 조회
        * given: 지하철 노선 분당선이 등록되어 있다.
        * when: 지하철 노선 아이디 100을 조회를 요청한다.
        * then: 지하철 노선 단건 조회가 실패 한다.
    * [x] Scenario: 지하철 노선 수정
        * given: 지하철 노선 분당선이 등록되어 있다.
        * when: 지하철 노선 이름을 구분당선으로 수정 요청한다.
        * then: 지하철 노선이 정상 수정된다.
    * [ ] 지하철 노선 삭제
        * given: 지하철 노선 분당선이 등록되어 있다.
        * when: 지하철 노선 분당선을 삭제 요청한다.
        * then: 지하철 노선이 정상 삭제된다.
