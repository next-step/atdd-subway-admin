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

## 요구사항

### 1단계: 지하철 노선 관리

지하철 노선 관리 기능을 구현하기

- [ ] 기능 구현 전 인수 테스트 작성: `LineAcceptanceTest` 를 모두 완성시키세요.
- [ ] 기능 목록 (생성 / 목록 조회 / 조회 / 수정 / 삭제): 인수 테스트가 모두 성공할 수 있도록 `LineController`를 통해 요청을 받고 처리하는 기능을 구현하세요.
- [ ] 기능 구현 후 인수 테스트 리팩터링: 인수 테스트의 각 스텝들을 메서드로 분리하여 재사용하세요.

#### Requests

##### POST /lines

- POST /lines
- content-type: application/json; charset=UTF-8;

##### GET /lines

- GET /lines
- accept: application/json

##### GET /lines/1

- GET /lines/1
- accept: application/json

##### PUT /lines/1

- PUT /lines/1
- content-type: application/json; charset=UTF-8;

##### DELETE /lines/1

- DELETE /lines/1
