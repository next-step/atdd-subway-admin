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


## 기능구현 목록
### 미션 소개 - 지하철 노선도 관리

### 1단계 - 지하철 노선 관리
- 지하철 노선 객체 단위테스트 작성
  - [X] 지하철 노선 정보 수정 메소드 단위 테스트
- 지하철 노선 레파지토리 단위테스트 작성
  - [X] 지하철 노선 생성 테스트
  - [X] 지하철 노선 조회 테스트
    - [X] 지하철 노선 목록 조회 테스트
    - [X] 지하철 노선 단일 조회 테스트
  - [X] 지하철 노선 수정 테스트
  - [X] 지하철 노선 삭제 테스트
- 기능 구현 전 인수 테스트 작성
  - Feature: 지하철 노선 관리 기능 
    - [X] Scenario: 지하철 노선을 생성한다. 
      - When 지하철 노선을 생성 요청한다. 
      - Then 지하철 노선이 생성된다.
    - [X] Scenario: 지하철 노선을 삭제한다. 
      - Given 지하철 노선이 등록되어있다. 
      - When 지하철 노선을 삭제 요청한다. 
      - Then 지하철 노선이 삭제된다.
    - [X] Scenario: 지하철 노선을 조회한다.
      - Given 지하철 노선이 등록되어있다.
      - When 지하철 노선을 조회 요청한다.
      - Then 지하철 노선이 조회된다.
    - [X] Scenario: 지하철 노선들을 조회한다.
      - Given 지하철 노선들이 등록되어있다.
      - When 지하철 노선들을 조회 요청한다.
      - Then 지하철 노선들이 조회된다.
    - [X] Scenario: 지하철 노선을 수정한다.
      - Given 지하철 노선이 등록되어있다.
      - When 지하철 노선을 수정 요청한다.
      - Then 지하철 노선이 수정된다.
- 기능 구현
  - [X] 지하철 노선 생성 API
  - [X] 지하철 노선 단일 조회 API
  - [X] 지하철 노선 목록 조회 API
  - [X] 지하철 노선 정보 수정 API
  - [X] 지하철 노선 삭제 API 
- 기능 구현 후 인수 테스트 리팩터링

### 2단계 - 인수 테스트 리팩토링
- 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기
- 기능 구현 전 인수테스트 작성
  - Feature: 지하철 노선 및 구관 관리 기능
    - [X] Scenario: 지하철 노선을 생성한다.
      - given 지하철 역을 생성 요청한다.
      - When 지하철 노선을 생성 요청한다.
      - Then 지하철 노선이 생성된다.
      - When 지하철 노선을 조회 요청한다.
      - Then 지하철 노선이 조회된다.
      - Then 지하철 노선 구간이 조회된다.
- 기능 구현
  - 노선
    - 구간과의 관계
      - [X] 하나의 노선은 여러 구간을 가질 수 있다.
      - [X] 구간과 노선은 다대일(N:1) 관계
  - 역
    - 구간과의 관계
      - [X] 하나의 역은 여러 구간을 가질 수 있다.
      - [X] 구간과 역은 다대일(N:1) 관계
  - 구간
    - 역과의 관계
      - [X] 하나의 역은 여러 구간을 가질 수 있다.
      - [X] 역과 구간은 일대다(1:N) 관계
    - 노선과의 관계
      - [X] 하나의 노선은 여러 구간을 가질 수 있다.
      - [X] 노선과 구간은 일대다(1:N) 관계
- 기능 구현 후 인수 테스트 리팩터링
  - [X] 픽스쳐 메소드 생성