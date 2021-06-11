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



## :pen: 요구사항 정리

## Step1

- [x] 지하철 노선 관리 기능을 구현하기.
  - [x] 기능 목록: 생성/ 목록 조회/ 조회/ 수정/ 삭제
  - [x] 기능 구현 전 인수 테스트 작성
  - [x] 기능 구현 후 인스테스트 리펙터링

## Step2

- [x] 노선 생성 시 두 종점역 추가
  - [x] 노선 생성 시 종점역(상행, 하행, distance) 정보를 파라미터에 추가
  - [x] 두 종점역은 구간의 형태로 관리
- [x] 노선 객체에서 구간 정보 관리
  - [x] 구간 entity 구성 및 연관관계 매핑
- [x] 노선의 역 목록을 조회하는 기능 구현
  - [x] 노선 조회 시 역 목록을 함께 응답할 수 있도록 변경
  - [x] 노선에 등록된 구간을 순서대로 정렬하여 상행종점부터 하행 종점까지 목록을 응답
  - [x] 노선 구간(or 역)의 관계를 새로 맺는다.

## Step3

- [ ] 지하철 구간 등록기능 구현

  - [ ] 역 사이에 새로운 역을 등록하는 경우

    길이가 동일하거나 크면 오류발생

    이미 존재하는 구간인 경우 오류발생

    상행, 하행 어디에도 포함되지 않는경우 등록 불가

  - [ ] 상행 종점으로 역을 등록하는 경우

  - [ ] 하행 종점으로 역을 등록하는 경우

#### API

```http
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

- 인수테스트 작성

- 예외 케이스 처리 인수테스트 작성
