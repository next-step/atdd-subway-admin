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

---
## 도메인

### 지하철역
- 지하철 역 속성
  - 이름(name)
  - 지하철 구간(section)

### 지하철역 구간
- 지하철 (상행 방향)역과 (하행 방향)역 사이의 연결 정보
- 지하철 구간 속성:
  - 길이(distance)
  - 지하철 노선(line)
  
### 지하철 노선
- 지하철 구간의 모음으로 구간에 포함된 지하철 역의 연결 정보
- 지하철 노선 속성:
  - 노선 이름(name)
  - 노선 색(color)

---
## 🚀 1단계 - 지하철역 인수 테스트 작성
### 기능 요구사항
-[x] 지하철역 관련 인수 테스트를 완성하세요.
  -[x] 지하철역 목록 조회 인수 테스트 작성하기
  -[x] 지하철역 삭제 인수 테스트 작성하기
### 프로그래밍 요구사항
- 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요.
--- 
## 🚀 2단계 - 지하철 노선 기능

### 기능 요구사항
- 요구사항 설명에서 제공되는 인수 조건을 기반으로 지하철 노선 관리 기능을 구현하세요.
- 인수 조건을 검증하는 인수 테스트를 작성하세요.
- 
### 프로그래밍 요구사항
- 아래의 순서로 기능을 구현하세요.
  - 인수 조건을 검증하는 인수 테스트 작성
  - 인수 테스트를 충족하는 기능 구현
- 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트를 서로 격리 시키세요.
- 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요.

### 요구사항 목록
- 인수 테스트
  - [x] 지하철 노선 생성
  - [x] 지하철 노선 목록 조회
  - [x] 지하철 노선 조회
  - [x] 지하철 노선 수정
  - [x] 지하철 노선 삭제

- 도메인
  - 지하철 노선
    - [x] 이름을 갖고 있다.
    - [x] 이름은 중복될 수 없다.
    - [x] 색을 갖고 있다.
    - [x] 색은 중복될 수 없다.
    - [x] 상행종점역을 갖고 있다.
    - [x] 하행종점역을 갖고 있다.
    - [x] 노선을 생성할 수 있다.
    - [x] 노선 목록을 조회할 수 있다.
    - [x] 노선을 조회할 수 있다.
    - [x] 노선의 이름과 색을 수정할 수 있다.
    - [x] 노선을 삭제할 수 있다.

---
## 🚀 3단계 - 구간 추가 기능

### 기능 요구사항
- 요구사항 설명에서 제공되는 요구사항을 기반으로 지하철 구간 추가 기능을 구현하세요.
- 요구사항을 정의한 `인수 조건`을 조출하세요.
- 인수 조건을 검증하는 `인수 테스트`를 작성하세요.
- 예외 케이스에 대한 검증도 포함하세요.

### 기능 목록
- [x] 역 사이에 새로운 역을 등록
- [x] 새로운 역을 상행 종점으로 등록
- [x] 새로운 역을 하행 종점으로 등록
- [x] 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
- [x] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
- [x] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

### 인수 테스트
- [x] 역 사이에 새로운 역을 등록할 수 있다.
- [x] 역 사이에 새로운 역을 등록할 때 기존 역 사이의 길이보다 크거나 같으면 등록할 수 없다.
- [x] 새로운 역을 상행 종점으로 등록할 수 있다.
- [x] 새로운 역을 하행 종점으로 등록할 수 있다.
- [x] 추가하는 역이 모두 구간에 포함되어 있으면 등록 할 수 없다.
- [x] 추가하는 역이 모두 구간에 포함되어 있지 않으면 등록 할 수 없다.

---
## 🚀 4단계 - 구간 제거 기능

### 기능 요구사항
- 요구사항 설명에서 제공되는 요구사항을 기반으로 지하철 구간 제거 기능을 구현하세요.
- 요구사항을 정의한 인수 조건을 도출하세요.
- 인수 조건을 검증하는 인수 테스트를 작성하세요.
- 예외 케이스에 대한 검증도 포함하세요.

### 기능목록
- [x] 종점을 제거
  - [x] 다음으로 오던 역이 종점이 된다.
- [x] 가운데 역을 제거 
  - [x] 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 됨
  - [x] 거리는 두 구간의 거리의 합으로 정함
- [x] 구간이 하나인 노선에서 역을 제거하는 경우 제거할 수 없다.

### 인수 테스트
- [x] 구간이 하나인 노선에서는 역을 제거할 수 없다.
- [x] 구간에 포함되지 않는 역은 제거할 수 없다.
- [x] 구간이 두개 이상인 노선에서 상행 종점을 제거하면 다음 역이 상행 종점이 된다.
- [x] 구간이 두개 이상인 노선에서 하행 종점을 제거하면 다음 역이 하행 종점이 된다.
- [x] 구간이 세개인 노선에 가운데 역을 제거할 수 있다.
