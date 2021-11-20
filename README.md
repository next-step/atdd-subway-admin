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

<br>

***

## 요구사항

- 기능 구현 전 인수 테스트를 작성한다.
    - 지하철 역
  ```markdown
  Feature: 지하철 역 관리 기능

    Scenario: 지하철 역을 생성한다.
        When 지하철 역을 생성 요청한다.
        Then 지하철 역이 생성된다.

    Scenario: 지하철 역 목록을 조회한다.
        Given 지하철 역이 등록되어있다.
        When 지하철 역 목록을 조회 요청한다.
        Then 지하철 역 목록이 조회된다.

    Scenario: 지하철 역을 삭제한다.
        Given 지하철 역이 등록되어있다.
        When 지하철 역을 삭제 요청한다.
        Then 지하철 역이 삭제된다.
  ```

    - 지하철 노선
  ```markdown
  Feature: 지하철 노선 관리 기능

    Scenario: 지하철 노선을 생성한다.
        When 지하철 노선을 생성 요청한다.
        Then 지하철 노선이 생성된다.
  
    Scenario: 지하철 노선 생성이 실패한다.
        Given 지하철 노선이 등록되어있다.
        When 지하철 노선을 생성 요청한다.
        Then 지하철 노선 생성이 실패한다.

    Scenario: 지하철 노선 목록을 조회한다.
        Given 지하철 노선이 등록되어있다.
        When 지하철 노선 목록을 조회 요청한다.
        Then 지하철 노선 목록이 조회된다.
    
    Scenario: 지하철 노선 1건을 조회한다.
        Given 지하철 노선이 등록되어있다.
        When 지하철 노선을 조회 요청한다.
        Then 지하철 노선이 조회된다.
  
    Scenario: 지하철 노선 조회가 실패한다.
        Given 지하철 노선이 등록되어 있지 않다.
        When 지하철 노선을 조회 요청한다.
        Then 지하철 노선이 조회가 실패한다.
  
    Scenario: 지하철 노선을 수정한다.
        Given 지하철 노선이 등록되어있다.
        When 지하철 노선을 수정 요청한다.
        Then 지하철 노선이 수정된다.

    Scenario: 지하철 노선을 삭제한다.
        Given 지하철 노선이 등록되어있다.
        When 지하철 노선을 삭제 요청한다.
        Then 지하철 노선이 삭제된다.
  
    Scenario: 지하철 노선 삭제가 실패한다.
        Given 지하철 노선이 등록되어 있지 않다.
        When 지하철 노선을 삭제 요청한다.
        Then 지하철 노선이 삭제가 실패한다.
  ```

## 기능 목록

- 지하철 역 (Station)
    - [x] 지하철 역을 생성한다.
        - [x] 기존에 존재하는 지하철역 이름으로 지하철역을 생성할 수 없다.
    - [x] 지하철 역 목록을 조회한다.
    - [x] 지하철 역을 삭제한다.

- 지하철 노선 (Line)
    - [x] 지하철 노선을 생성한다.
        - [x] 지하철 노선이 이미 등록되어 있는 경우 지하철 노선 생성에 실패한다.
    - [x] 지하철 노선 목록을 조회한다.
    - [x] 지하철 노선 1건을 조회한다.
        - [x] 지하철 노선이 등록되지 않은 경우 지하철 노선 조회가 실패한다.
    - [x] 지하철 노선을 수정한다.
    - [x] 지하철 노선을 삭제한다.
      - [x] 지하철 노선이 등록되지 않은 경우 지하철 노선 제거에 실패한다.
      