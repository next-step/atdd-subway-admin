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

## 🚀 1단계 - 지하철역 인수 테스트 작성

### Feat Requirements

- [x] 지하철역 관련 인수 테스트를 완성하세요.
  - [x] 지하철역 목록 조회 인수 테스트 작성하기
  - [x] 지하철역 삭제 인수 테스트 작성하기

### 프로그래밍 요구사항

- [x] Refactoring AT for 재사용성과 가독성

### Hint

JsonPath : 
- Json 문서를 읽어오는 DSL
- JsonPath를 사용하면 Response Dto 객체로 받을 필요 없이 필요한 값만 추출하여 검증에 사용할 수 있음

### 시나리오

#### 지하철역 목록 조회

[x] 정상 시나리오
  - 지하철역이 존재한다.
  - 지하철역 목록을 조회한다.
  - 지하철역을 응답한다.
[Skip] 비정상 - 지하철역 목록 존재하지 않을 경우
[Skip] 비정상 - 지하철역 목록 GET 실패

#### 지하철역 삭제

[x] 정상 시나리오
  - 지하철역이 존재한다.
  - 지하철역 삭제를 요청한다.
  - 지하철역이 삭제된다.
[Skip] 비정상 - 유효하지 않은 지하철 역 ID
[Skip] 비정상 - 존재하지 않는 지하철 역 ID
[Skip] 비정상 - 지하철 역 ID 삭제 실패