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


## 🚀 1단계 - 지하철 노선 관리
## 요구사항
* 지하철 노선 관리 기능을 구현하기
    * 기능 목록 : 생성/목록 조회/조회/수정/삭제
    * 기능 구현 전 인수 테스트 작성
    * 기능 구현 후 인수 테스트 리팩터링
    

## 기능 목록
### 노선 생성 기능
- [x] 노선 생성 기능 인수 테스트 작성
- [x] 노선 생성 기능 구현

### 노선 목록 조회 기능
- [x] 노선 목록 조회 기능 인수 테스트 작성
- [x] 노선 목록 조회 기능 구현

### 노선 조회 기능
- [x] 노선 조회 기능 인수 테스트 작성
- [x] 노선 조회 기능 구현

### 노선 수정 기능
- [x] 노선 수정 기능 인수 테스트 작성
- [x] 노선 수정 기능 구현

### 노선 삭제 기능
- [x] 노선 삭제 기능 인수 테스트 작성
- [x] 노선 삭제 기능 구현

## 🚀 2단계 - 인수 테스트 리팩터링
## 요구사항
* API 변경 대응하기
    * 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기
    * 두 종점역은 구간의 형태로 관리되어야 함
    * 노선 조회 시 응답 결과에 역 목록 추가하기
    * 상행역 부터 하행역 순으로 정렬되어야 함


## 기능 목록
- [x] 노선 생성시 종점역(상행,하행) 정보 요청 파라미터에 추가하기
- [x] 지하철역 기능 정리하기
    - [x] 생성
    - [x] 수정
    - [x] 삭제
    - [x] 조회
- [x] 노선 객체에 구간 정보 추가하기
    - [x] 구간 entity 추가
- [ ] 노선의 역 목록 조회하는 기능 추가하기
    - [ ] 상행 -> 하행순으로 정렬하기
