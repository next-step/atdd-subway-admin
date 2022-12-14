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

## 1단계 - 지하철역 인수 테스트 작성

### 기능 요구사항

- [X] 지하철역 목록 조회 인수 테스트 작성하기
- [X] 지하철역 삭제 인수 테스트 작성하기

<br>

## 2단계 - 지하철 노선 기능

### 기능 요구사항
- 요구사항 설명에서 제공되는 인수 조건을 기반으로 지하철 노선 관리 기능을 구현하세요.
- 인수 조건을 검증하는 인수 테스트를 작성하세요.
###기능 목록
- [X] 지하철 노선 생성
- [X] 지하철 노선 목록 조회
- [X] 지하철 노선 조회
- [X] 지하철 노선 수정
- [X] 지하철 노선 삭제

<br>

## 3단계 - 구간 추가 기능
### 기능 요구사항
- [X] 요구사항 설명에서 제공되는 요구사항을 기반으로 지하철 구간 추가 기능 구현
- [X] 요구사항을 정의한 인수 조건 설정
- [X] 인수 조건을 검증하는 인수 테스트 작성
- [X] 예외 케이스에 대한 검증 포함
### 기능 목록
- [X] 역 사이에 새로운 역을 등록할 경우
- [X] 새로운 역을 상행 종점으로 등록할 경우
- [X] 새로운 역을 하행 종점으로 등록할 경우
- [X] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
- [X] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
- [X] 상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없음
- 
<br>
## Step4 - 구간 제거 기능
### 기능 요구사항
- [X] 요구사항 설명에서 제공되는 요구사항을 기반으로 지하철 구간 제거 기능을 구현
- [X] 요구사항을 정의한 인수 조건을 도출
- [X] 인수 조건을 검증하는 인수 테스트를 작성
- [X] 예외 케이스에 대한 검증도 포함

### 기능 목록
- [X] 종점 제거
- [X] 가운데 역 제거
- [X] 구간이 하나인 노선에서 역 제거 시 예외 발생
- [X] 노선에 등록되지 않은 역 제거 시 예외 발생