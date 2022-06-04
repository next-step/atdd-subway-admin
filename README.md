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

<br>

## 3단계 - 구간 추가 기능

### 요구사항
- [ ] 지하철 구간 추가 기능을 구현한다.
    - [ ] Line 과 Station 사이에 구간을 표현할 수 있는 LineStation 테이블을 추가한다.
    - [ ] Line 이 LineStation 을 여러개 포함 할 수 있는 구조로 바꾼다.
    - [ ] 도메인 로직에 구간 추가 에 대한 validation 조건을 추가한다. (상행, 하행, 거리)


### 프로그래밍 요구사항
아래의 순서로 기능을 구현하세요.
- 요구사항 설명을 참고하여 인수 조건을 정의
- 인수 조건을 검증하는 인수 테스트 작성
- 인수 테스트를 충족하는 기능 구현
- 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트를 서로 격리 시키세요.
- 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요.

### 요구사항 설명
- 요구사항 설명에서 제공되는 요구사항을 기반으로 지하철 구간 추가 기능을 구현하세요.
- 요구사항을 정의한 인수 조건을 조출하세요.
- 인수 조건을 검증하는 인수 테스트를 작성하세요.
- 예외 케이스에 대한 검증도 포함하세요.

인수 조건

구간 추가 API 명세

```
POST /lines/1/sections HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8
host: localhost:52165

{
    "downStationId": "4",
    "upStationId": "2",
    "distance": 10
}
```

구간 등록 인수 테스트 예시 

```
@DisplayName("노선에 구간을 등록한다.")
@Test
void addSection() {
    // when
    // 지하철_노선에_지하철역_등록_요청

    // then
    // 지하철_노선에_지하철역_등록됨
}
```
