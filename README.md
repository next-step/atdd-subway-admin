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

## 요구사항

### step1
1. 생성 / 목록조회 / 조회 / 수정 / 삭제에 대한 인수테스트 작성
2. 기능 구현

### step2
1. API 변경 대응하기
- [x] 노선 생성 시 종점역(상행, 하행)정보를 요청 파라미터에 함께 추가하기
    - 두 종점역은 구간의 형태로 관리되어야 함
- [x] 노선 조회 시 응답 결과에 역 목록 추가하기
    - 상행역 부터 하행역 순으로 정렬되어야 함
    
### step3
1. 지하철 구간 등록 인수 테스트 작성과 기능 구현
- [x] 역 사이에 새로운 역을 등록할 경우
    - 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
- [x] 새로운 역을 상행 종점으로 등록할 경우
- [x] 새로운 역을 하행 종점으로 등록할 경우
- [x] 구간 등록 시 예외 케이스를 고려하기
    - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
- [x] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
    - 아래의 이미지 에서 A-B, B-C 구간이 등록된 상황에서 B-C 구간을 등록할 수 없음(A-C 구간도 등록할 수 없음)
- [x] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

## step4

### 요구사항

- [x] 1. 지하철 구간 제거 기능 구현
- [x] 2. 구간 삭제 시 예외 케이스를 고려하기
 

#### 요구사항 설명

1. 요청 시 `구간` 제거 요청이 아닌 `역` 제거 요청

2. 중간역이 제거되는 경우 재배치
  - A -> B -> C 구간에서 B를 제거하면 A -> C로 구간 재배치
  - A -> B, B -> C 구간 삭제
  - A -> B 3m, B -> C 가 4m이면, A -> C의 거리는 7m 로 재설정 (두 구간 거리 합산)

3. 종점이 제거되는 경우 제일 가까운 역이 새로운 종점
  - 종점 이전 역 -> 종점 구간 삭제

#### 예외 케이스

1. 제거하려는 구간이 마지막으로 남은 구간인 경우 삭제 불가능
  - 노선은 반드시 1개 이상의 구간을 포함