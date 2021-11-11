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
- [ ] 노선(Line) 생성 시, 상행, 하행, 거리 정보를 요청 파라미터에 추가

### 2. 노선 조회 시 응답 결과에 역 목록을 추가한다.
- [ ] 노선(Line) 조회 시 역(Station) 목록을 함께 응답할 수 있도록 변경
- [ ] 상행역 부터 하행역 순으로 정렬
- [ ] Response Model 에 역(Station) 목록 추가