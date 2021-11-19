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

### 요구사항

- 기능 목록: 생성 / 목록 조회 / 조회 / 수정 / 삭제
- 기능 구현 전 인수 테스트 작성
- 기능 구현 후 인수 테스트 리팩터링

### 기능목록

- [X] 지하철 노선 생성
  - [X] 인수 테스트 시나리오 및 테스트 작성
    - [X] 지하철 노선을 생성한다
    - [X] 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다
    - [X] 공백의 노선명으로 지하철 노선을 생성한다
    - [X] 공백의 노선색상으로 지하철 노선을 생성한다
  - [X] 기능 테스트 및 구현
    - [X] 기존에 존재하는 노선명 존재시 오류를 던진다
    - [X] Controller에서 필수 파라메터를 검증한다
    - [X] LineControllerAdvice로 예외 상황을 관리한다
- [X] 지하철 노선 목록 조회
  - [X] 인수 테스트 시나리오 및 테스트 작성
    - [X] 지하철 노선 목록을 조회한다
  - [X] 기능 테스트 및 구현
    - [X] 지하철 노선 목록 조회 기능 추가
- [X] 지하철 노선 조회
  - [X] 인수 테스트 시나리오 및 테스트 작성
    - [X] 지하철 노선을 조회한다
    - [X] 등록되지 않은 지하철 노선을 조회한다
  - [X] 기능 테스트 및 구현
    - [X] 지하철 노선 조회 구현
    - [X] 등록되지 않은 노선 구현시 오류를 던진다
- [ ] 지하철 노선 수정
  - [X] 인수 테스트 시나리오 및 테스트 작성
    - [X] 지하철 노선을 수정한다
    - [X] 등록되지 않은 지하철 노선을 수정한다
    - [X] 공백의 노선명으로 지하철 노선을 수정한다
    - [X] 공백의 노선색상으로 지하철 노선을 수정한다
  - [ ] 기능 테스트 및 구현
- [ ] 지하철 노선 삭제
  - [ ] 인수 테스트 시나리오 및 테스트 작성
  - [ ] 기능 테스트 및 구현
