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

# 요구사항 정리

## Step1
* 지하철 노선 생성
* 지하철 노선 목록 조회
* 지하철 노선 조회
* 지하철 노선 수정
* 지하철 노선 삭제

## Step2
* 노선 생성 시 종점역(상행, 하행)을 함께 추가하기
  + 등록된 전철역 리스트 가져오기
  + 라인 구간에 상행, 하행역 등록하기
    - 새로운 엔티티 생성(Section)
    - 라인의 역 구간 및 거리를 저장하는 엔티티
  + 라인 생성
* 노선 조회 응답 결과에 등록된 구간을 참고하여 역 목록 응답 추가하기

## Step3
* 지하철 라인 생성 시 최초 구간 생성 추가 로직
  + 시작역과 끝 역 생성
  + 확인 및 인수테스트 정리
* 지하철 구간 등록
  + 등록된 구간에서 새로운 구간 추가하기
  + 구간 앞 등록 및 구간 사이 추가
  + 추가 시 삭제도 고려하기

## Step4
* 지하철 노선 구간 삭제 기능
* 구간 삭제 예외 케이스
  + 구간이 한개일 경우 삭제 불가
  + 노선 구간에 포함되지 않은 전철역일 경우 삭제 불가
* 구간 삭제 기능
  + 시작, 종점 전철역 삭제를 할 경우 단순 삭제
  + 시작, 종점역이 아닐경우
    - 두 구간의 시작과 끝을 합친 후, 두 구간 삭제
    - 두 구간을 합친 새로운 구간을 추가

## Getting Started

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

### 요구사항 분석

## ✏️ Code Review Process

[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/next-step/atdd-subway-admin/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/next-step/atdd-subway-admin/blob/master/LICENSE.md) licensed.
