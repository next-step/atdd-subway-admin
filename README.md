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

### 2단계: 인수 테스트 리팩터링

API 변경 대응하기

- 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기
  - 두 종점역은 구간의 형태로 관리되어야 함
  - color, name, upStationId, downStationId, distance
- 노선 조회 시 응답 결과에 역 목록 추가하기
  - 상행역 부터 하행역 순으로 정렬되어야 함
  - id, name, color, stations([station, station]), date
- 노선: 구간 목록 추가

### 3단계: 구간 추가 기능

지하철 구간 등록 기능을 구현하기

- 기능 구현 전 인수 테스트 작성
  - 역 사이에 새로운 역을 등록할 경우: 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
  - 새로운 역을 상행 종점으로 등록할 경우
  - 새로운 역을 하행 종점으로 등록할 경우
- 예외 케이스 처리 인수 테스트 작성
  - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
  - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
  - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

#### POST /lines/1/sections

- POST /lines/1/sections
- content-type: application/json; charset=UTF-8;
