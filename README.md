## 기능
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

# ATDD 1단계 - 지하철 노선 관리
## 구현 목록 
- [X] 지하철 노선 생성 / 목록 조회 / 조회 / 수정 / 삭제
- [x] 기능 구현 전 인수 테스트 작성
- [X] 기능 구현 후 인수 테스트 리팩터링

# ATDD 2단계 - 인수 테스트 리팩터링
## 구현 목록
- [X] API 변경에 따른 인수 테스트 작성
    - [x] 노선 생성시 종점역(상행, 하행) 정보 파라미터 추가 (두 종점역은 구간의 형태로 관리 되어야함)
    - [X] 노선 조회 시 응답 결과에 역 목록 추가 (상행역부터 하행역 순으로 정렬되어야 함)
- [X] 구간 domain 추가
- [X] 노선에서 구간 연관관계 매핑
- [X] 노선의 역 목록을 조회하는 기능 구현

# ATDD 3단계 - 구간 추가 기능
## 구현 목록 
- 인수 테스트 작성
  - [X] 역 사이에 새로운 역을 등록할 경우
  - [X] 새로운 역을 상행 종점으로 등록할 경우
  - [X] 새로운 역을 하행 종점으로 등록할 경우
  - [X] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
  - [X] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
  - [X] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
- 기능 구현
  - [X] 구간 추가 API 
  - [X] 예외 처리 
    - [X] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
    - [X] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
    - [X] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

## 구간 등록 API Request
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