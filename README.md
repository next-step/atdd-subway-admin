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

## 요구사항 정리

### 리뷰어님 커맨트 
- [X] List<Secton>를 일급콜렉션 클래스 Sections 으로 변경
- [X] `지하철_노선_등록(팔호선_역_모음.get("암사역"))`와 같이 모호한 코드를 다음과 같이 변경
  - [X] `지하철_노선_등록(팔호선, "암사역", "천호역", 1300)`과 같이 구간과 거리를 포함하도록 변경

### 미션 요구사항
- [X] 역 사이에 새로운 역을 등록 기능
  - [X] 상행역은 서로 같고 하행역이 다를 경우 조건
  - [X] 새로운 길이가 기존 길이보다 크거나 같다면 예외 발생
  - [X] 구간 재설정
    - [X] 기존 구간 삭제
    - [X] `기존 상행역, 신규 하행역, 신규 거리` 로 구간 생성
    - [X] `신규 하행역, 기존 하행역, 기존거리 - 신규 거리` 로 구간 생성

- [X] 새로운 역을 상행 종점으로 등록할 경우
  - [X] 새로운 하행역이 기존 상행역과 같을 경우 조건
  - [X] `신규 상행역, 신규 하행역, 신규 거리` 로 구간 생성

- [X] 새로운 역을 하행 종점으로 등록할 경우
  - [X] 새로운 상행역이 기존 하행역과 같을 경우 조건
  - [X] `신규 상행역, 신규 하행역, 신규 거리` 로 구간 생성

- [X] 예외 케이스 고려하기
  - [X] 구간 등록시 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 BAD_REQUEST 전달
  - [X] 구간 등록시 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 BAD_REQUEST 전달

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
