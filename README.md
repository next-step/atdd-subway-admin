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

---

# 1단계 : 지하철 노선 관리

### 1. 지하철 노선 생성과 관련된 인수 테스트를 작성한다.
- [X] 지하철 노선을 생성한다.
- [X] 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.

### 2. 지하철 노선 조회와 관련된 인수 테스트를 작성한다.
- [X] 지하철 노선 목록을 조회한다.
- [X] 지하철 노선을 조회한다.
  
### 3. 지하철 노선 수정과 관련된 인수 테스트를 작성한다.
- [X] 지하철 노선을 수정한다.
  
### 4. 지하철 노선 제거(삭제)와 관련된 인수 테스트를 작성한다.
- [X] 지하철 노선을 제거한다.

---

# 2단계 : 인수 테스트 리팩터링

### 1. 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가한다.
- [X] 구간(Section) 엔티티 생성
- [X] 노선(Line), 구간(Section), 역(Station) 연관관계 추가
- [X] 노선(Line) 생성 시, 상행, 하행, 거리 정보를 요청 파라미터에 추가

### 2. 노선 조회 시 응답 결과에 역 목록을 추가한다.
- [X] 노선(Line) 조회 시 역(Station) 목록을 함께 응답할 수 있도록 변경
- [X] Response Model 에 역(Station) 목록 추가
- [X] 상행역 부터 하행역 순으로 정렬

---

# 3단계 : 구간 추가 기능

### 1. 지하철 구간 등록 인수 테스트를 작성한다.
- [X] 역 사이에 새로운 역을 추가
  - [X] 노선에 구간 등록 로직 구현
  - [X] 기존 구간 상행역,하행역 갱신
- [X] 새로운 역을 상행 종점으로 추가
- [X] 새로운 역을 하행 종점으로 추가
- [X] 여러 신규 구간 추가 시, 지하철 역이 정렬 조회
  - [X] 등록된 새로운 역은 상행, 하행 순으로 정렬 확인

### 2. 예외 케이스 처리 인수 테스트를 작성한다.
- [X] 역 사이에 새로운 역을 추가하는 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없음
- [X] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
- [ ] 노선에 존재하는 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음(기존 역이 1개 포함되어 있어야함)