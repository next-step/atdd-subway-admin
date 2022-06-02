## step3

- [x] 구간 도메인 엔티티 생성
    - [x] 구간은 distance, downStation, upStation를 가진다.
    - [x] 라인과 구간은 1:N 관계
- [x] 구간 일급 컬렉션 생성
    - [x] 구간 추가 기능
- [x] 라인 도메인에 구간 생성 함수 생성

```
Given 지하철 노선을 생성하고
When 역 사이에 새로운 역을 등록하면
Then 지하철 노선의 구간 목록 조회 시 추가한 구간을 찾을 수 있다.
```

- [ ] 지하철 라인에 구간 추가 기능

```
Given 상행과 하행을 생성하고
When 새로운 역을 상행 종점으로 등록하면
Then 새로운 역이 상행 종점으로 변경된다.
Then 기존의 상행 종점은 노선의 일반 역으로 변경된다.
```

- [ ] 새로운 역을 상행 종점으로 등록 할 수 있다

```
Given 상행과 하행을 생성하고
When 새로운 역을 하행 종점으로 등록하면
Then 새로운 역이 하행 종점으로 변경된다.
Then 기존의 하행 종점은 노선의 일반 역으로 변경된다.
```

- [ ] 새로운 역을 하행 종점으로 등록 할 수 있다

```
Given 상행과 하행을 생성하고
When 기존 역 사이 길이보다 크거나 같은 길이로 등록을 시도하면
Then 예외가 발생한다.
```

- [ ] 기존 역 사이 길이보다 크거나 같은 길이로 등록을 하려고 하면 예외가 발생한다

```
Given 상행과 하행을 생성하고
When 이미 등록된 역을 구간으로 등록하려고 하면
Then 예외가 발생한다.
```

- [ ] 이미 등록된 역을 구간으로 등록하려고 하면 예외가 발생한다.

```
Given 상행과 하행을 생성하고
When 상행역 혹은 하행역을 포함하지 않고, 구간으로 등록하려고 하면
Then 예외가 발생한다.
```

- [ ] 항행역과 하행역 둘 중 하나도 포함되어 있지 않으면 예외가 발생한다.

## step2

- [x] 지하철노선 생성 인수 테스트 작성
- [x] 지하철노선 생성 구현 ( 생성 시, 상행종점역과 하행종점역을 등록 )

```
When 지하철 노선을 생성하면
Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
```

- [x] 지하철노선 목록 조회 인수 테스트 작성
- [x] 지하철노선 목록 조회 구현

```
Given 2개의 지하철 노선을 생성하고
When 지하철 노선 목록을 조회하면
Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
```

- [x] 지하철노선 조회 인수 테스트 작성
- [x] 지하철노선 조회 구현 ( 노선 조회 시, 포함된 역 목록이 함께 응답)

```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 조회하면
Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
```

- [x] 지하철노선 수정 인수 테스트 작성
- [x] 지하철노선 수정 구현

```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 수정하면
Then 해당 지하철 노선 정보는 수정된다
```

- [x] 지하철노선 삭제 인수 테스트 작성
- [x] 지하철노선 삭제 구현

```
Given 지하철 노선을 생성하고
When 생성한 지하철 노선을 삭제하면
Then 해당 지하철 노선 정보는 삭제된다
```

- [ ] 인수 테스트 격리

### Line 요구사항

- [x] name, color, distance, 상행종점역, 하행종점역을 갖는다.
- [x] Line과 상행종점역은 ManyToOne 관계
- [x] Line과 하행종점역은 ManyToOne 관계
- [x] Distance vo 추가
- [x] Line에 Distance Embedded로 추가

## step1

### 지하철역 인수 테스트를 완성하세요.

- [x] 지하철역 목록 조회 인수 테스트 작성하기
- [x] 지하철역 삭제 인수 테스트 작성하기
- [x] 인수 테스트 코드 리팩터링

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
