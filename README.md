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

 * * *

## 🚀 1단계 - 지하철역 인수 테스트 작성

### 요구사항
* **지하철역 인수 테스트 완성**
  * 지하철역 목록 조회 인수 테스트를 작성한다.
  * 지하철역 삭제 인수 테스트를 작성한다.
* **인수 테스트 코드 리팩터링**
  * 인수 테스트의 재사용성과 가독성, 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 한다.

### 구현 리스트
* **재사용 가능한 테스트 코드 분리**
  * 기존 인수 테스트 코드에서 반복되는 API 호출 메서드를 추상 클래스(`BaseAcceptanceTest`)로 분리한다.
  * 실제 실행 테스트 코드에서는 해당 추상 클래스를 상속받아 재사용하도록 한다.
* **지하철역 목록 조회 인수 테스트 코드 작성**
  * 지하철역을 2개 생성 후 조회 시 2개의 지하철역이 모두 조회되는지 확인한다.
* **지하철역 삭제 인수 테스트 코드 작성**
  * 지하철역 생성, 삭제 후 지하철역 목록 조회 시 해당 지하철역이 없을을 확인한다.

 * * *

## 🚀 2단계 - 지하철 노선 기능

### 요구사항
* **지하철 노선 관리 기능 구현**
  * 주어진 인수 조건을 기반으로 지하철 노선 관리 기능을 추가한다.
  ```
  1. 지하철노선 생성
  When 지하철 노선을 생성하면
  Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
  ```
  ```
  2. 지하철노선 목록 조회
  Given 2개의 지하철 노선을 생성하고
  When 지하철 노선 목록을 조회하면
  Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
  ```
  ```
  3. 지하철노선 조회
  Given 지하철 노선을 생성하고
  When 생성한 지하철 노선을 조회하면
  Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
  ```
  ```
  4. 지하철노선 수정
  Given 지하철 노선을 생성하고
  When 생성한 지하철 노선을 수정하면
  Then 해당 지하철 노선 정보는 수정된다
  ```
  ```
  5. 지하철노선 삭제
  Given 지하철 노선을 생성하고
  When 생성한 지하철 노선을 삭제하면
  Then 해당 지하철 노선 정보는 삭제된다
  ```
* **인수 조건을 검증하는 인수 테스트 작성**
* **주어진 순서로 기능 구현**
  * 인수 조건을 검증하는 인수 테스트를 작성한다.
  * 인수 테스트를 충족하는 기능을 구현한다.
* **인수 테스트 격리**
  * 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트를 서로 격리시킨다.
* **인수 테스트 리팩터링**
  * 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링한다.
