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

## 1단계 - 지하철 노선 관리
### 요구사항
지하철 노선 관리 기능을 구현하기
- 기능 구현 전 인수 테스트 작성
- 기능 구현 후 인수 테스트 리팩터링

### 구현할 기능 목록
- 기능 목록
    - [x] 생성
    - [x] 목록 조회
    - [x] 조회
    - [x] 수정
    - [x] 삭제
    
## 2단계 - 인수 테스트 리팩터링
### 요구사항
API 변경 대응하기
- [x] 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기
  - [x] 두 종점역은 구간의 형태로 관리되어야 함
- [x] 노선 조회 시 응답 결과에 역 목록 추가하기
  - [x] 상행역 부터 하행역 순으로 정렬되어야 함

## 3단계 - 구간 추가 기능
### 요구사항
- 지하철 구간 등록 기능 구현하기
  - 기능 구현 전 인수 테스트 작성 
  - 예외 케이스 처리 인수 테스트 작성

### 구현할 기능 목록
- [x] 지하철 구간 추가 기능
  - [x] 역 사이에 새로운 역 등록
    - [x] 길이 설정 
    - [x] 기존 역 연결 재설정
  - [x] 새로운 역을 상행 종점으로 등록
  - [x] 새로운 역을 하행 종점으로 등록
- [x] 예외 케이스
  - [x] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록 불가능
  - [x] 상행역과 하행역이 이미 노선에 등록되어 있으면 추가할 수 없음
  - [x] 상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없음
  
## 4단계 - 구간 제거 기능
### 요구사항
- 지하철 구간 제거 기능을 구현하기
  - 노선의 구간을 제거하는 기능을 구현하기
  - 구간 삭제 시 예외 케이스를 고려하기

### 구현할 기능 목록
- [ ] 지하철 구간 제거 기능
  - [ ] 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
  - [ ] 중간역이 제거될 경우 두 구간을 재배치함
    - [ ] 거리는 두 구간의 거리의 합으로 정함
- [ ] 예외 케이스
  - [ ] 노선이 등록되어 있지 않은 역을 제거할 수 없다.
  - [ ] 구간이 하나인 경우 구간을 제거할 수 없다.

## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/next-step/atdd-subway-admin/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/next-step/atdd-subway-admin/blob/master/LICENSE.md) licensed.
