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

## 2단계 - 인수 테스트 리팩터링 기능 요구사항 정리
- 인수 테스트 목록
  - [X] 종점역(상행,하행)과 구간 사이 길이(distance)를 포함한 지하철 노선 생성
  - [X] 지하철 노선에 해당하는 구간 조회 (GET : /lines/{lineNo})
- 기능 요구사항 목록
  - [X] 노선 생성 시 구간 정보를 저장한다.
  - [X] 노선 조회 시 노선에 속한 역 정보를 상행역 -> 하행역 순으로 정렬한 역 정보를 포함한다.

## 3단계 - 구간 추가 기능 요구사항 정리
- 인수 테스트 목록
  - 정상 동작 인수테스트
    - [X] 역 사이에 새로운 역을 추가한다.
    - [X] 새로운 역을 상행 종점으로 추가한다.
    - [X] 새로운 역을 하행 종점으로 추가한다.
  - 예외 케이스 인수테스트
    - [X] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 추가 불가 하다.
    - [X] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가 불가 하다.
    - [X] 상행역과 하행역 둘 중 하나라도 포함되어 있지 않다면 추가 불가하다.

## 4단계 - 구간 제거 기능
- 인수 테스트 목록
  - 정상 동작 인수테스트
    - [X] Feature: 상행 구간 제거 기능
      - Scenario: 상행 구간을 제거한다.
        - Given: 두 개의 구간이 등록되어 있다.
        - When: 상행 구간을 제거한다.
        - Then: 상행 구간이 제거된다.
  
    - [X] Feature: 하행 구간 제거 기능
      - Scenario: 하행 구간을 제거한다.
        - Given: 두 개의 구간이 등록되어 있다.
        - When: 하행 구간을 제거한다.
        - Then: 하행 구간이 제거된다.
  
    - [X] Feature: 중간 구간 제거 기능
      - Scenario: 중간 구간 제거한다.
        - Given: 두 개의 구간이 등록되어 있다.
        - When: 중간 구간을 제거한다.
        - Then: 중간 구간이 제거된다.

  - 예외 케이스 인수 테스트
    - [X] Feature: 등록되지 않은 구간 제거
      - Scenario: 등록되지 않은 구간을 제거한다.
        - Given: 한 개의 구간이 등록되어 있다.
        - When: 존재하지 않는 구간을 제거한다.
        - Then: 구간 제거가 실패된다.

    - [X] Feature: 구간이 하나인 노선에서 구간 제거
      - Scenario: 구간이 하나인 노선에서 구간 제거한다.
        - Given: 한 개의 구간이 등록되어 있다.
        - When: 존재하는 구간을 제거한다.
        - Then: 구간 제거가 실패된다.