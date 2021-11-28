#지하철 노선도 미션

## 2단계 인수 테스트 리팩터링

### 요구사항 
* 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기
  * 두 종점역은 구간의 형태로 관리되어야함
* 노선 조회 시 응답 결과에 역 목록 추가하기
  * 상행역 부터 하행역 순으로 정렬되어야 함

### 요구사항 분리
* [X] 인수테스트 구간 정보 추가
  * 지하철 노선을 생성한다.
  * 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.
  * 지하철 노선을 수정한다.
  * 지하철 노선 목록을 조회한다.
  * 지하철 노선을 조회한다.
  * 존재하지 않는 지하철역으로 노선을 생성한다.
* [X] 노선의 구간 정보 조회 인수테스트 추가
  * 지하철 노선 조회 시 구간 정보 조회 추가
  * 지하철 노선 목록 조회 시 구간 정보 조회 추가
  * 노선에 등록된 구간을 순서대로 정렬하여 상행 종점부터 하행 종점까지 목록 응답
* [X] 노선요청 `LineRequest`
  * 상행역/하행역 ID 정보 포함
  * 거리 정보 포함
* [X] 노선응답 `LineResponse`
  * 상행역~하행역 순으로 정렬된 목록 정보 포함
* [X] 노선 `Line`
  * 구간 정보 포함
  * 구간 `@OneToMany` 관계 설정 
    * `Cascade All` 설정
* [X] 구간 `Section`
  * 노선 `@ManyToOne` 관계 설정 
  * 상행역 `@ManyToOne` 관계 설정
  * 하행역 `@ManyToOne` 관계 설정
  * 거리 정보 
---

## 1단계 지하철 노선 관리

### 지하철 노선관련 인수테스트 작성 및 기능 구현
* [X] 지하철 노선을 생성한다.
* [X] 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.
* [X] 지하철 노선 목록을 조회한다.
* [X] 지하철 노선을 조회한다.
* [X] 지하철 노선을 수정한다.
* [X] 지하철 노선을 제거한다.
* [X] 오류 발생 시 Bad Request 발생한다.
---

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
