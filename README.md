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

## 요구 사항
### 기능 요구 사항
#### 1단계 - 지하철역 인수 테스트 작성
- 지하철역 인수 테스트를 완성
  - 지하철역 목록 조회 인수 테스트 작
  - 지하철역 삭제 인수 테스트 작성
#### 2단계 - 지하철 노선 기능
- 아래 인수 조건을 기반으로 지하철 노선 관리 기능을 구현
- 인수 조건을 검증하는 인수 테스트를 작성

**인수 조건**

지하철노선 생성
```
When 지하철 노선을 생성하면
Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
```
지하철노선 목록 조회 
```
Given 2개의 지하철 노선을 생성하고
When 지하철 노선 목록을 조회하면
Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
```
지하철노선 조회
```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 조회하면
Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
```
지하철노선 수정
```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 수정하면
Then 해당 지하철 노선 정보는 수정된다
```
지하철노선 삭제
```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 삭제하면
Then 해당 지하철 노선 정보는 삭제된다
```
#### 3단계 - 구간 추가 기능
- 지하철 구간 추가 기능 구현
  - 역 사이에 새로운 역을 등록할 경우 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
  - 새로운 역을 상행 종점으로 등록할 경우
  - 새로운 역을 하행 종점으로 등록할 경우
- 요구 사항을 정의한 인수 조건을 조출
- 인수 조건을 검증하는 인수 테스트를 작성
- 예외 케이스에 대한 검증
  - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
  - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
  - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

#### 4단계 - 구간 제거 기능
- 노선의 구간을 제거하는 기능을 구현
  - 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
  - 중간역이 제거될 경우 재배치
    - 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치
    - 거리는 두 구간의 거리의 합
- 구간 삭제 시 예외 케이스에 대한 검증
  - 노선에 등록되어 있지 않은 역 제거
  - 구간이 하나인 노선에서 마지막 구간을 제거
- 요구사항을 정의한 인수 조건을 도출
- 인수 조건을 검증하는 인수 테스트를 작성

### 프로그래밍 요구사항
- 인수 테스트 주도 개발 프로세스에 맞춰서 기능 구현
  - 요구 사항 설명을 참고하여 인수 조건을 정의
  - 인수 조건을 검증하는 인수 테스트 작성
  - 인수 테스트를 충족하는 기능 구현
- 인수 조건은 인수 테스트 메서드 상단에 주석 작성
  - 뼈대 코드의 인수 테스트를 참고
- 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트 서로 격리
- 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트 리팩터링
