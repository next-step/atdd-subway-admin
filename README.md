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

## 요구사항 정의

#### 인수 테스트 작성

  - [x] 지하철 역 목록
  - [x] 지하철 역 삭제
  - [x] 지하철 노선 생성
      - [x] 노선 테이블 생성
      - [x] 거리 값 오류 처리
  - [x] 지하철 노선 목록 조회
  - [x] 지하철 노선 조회
  - [x] 지하철 노선 수정
  - [x] 지하철 노선 삭제
  - [x] Controller Advice를 통한 오류 처리
  - [ ] 구간 등록 인수 테스트 작성
      - [x] Section Entity 추가
      - [x] Section Line 연관 관계 맵핑
      - [ ] 새로운 역을 역 사이에 새로운 역 등록
          - [ ] 기존 역 사이 길이 보다 크거나 같으면 오류 발생
      - [ ] 새로운 역을 상행 종점 으로 등록
      - [ ] 새로운 역을 하행 종점 으로 등록
      - [ ] 상행 역과 하행 역이 이미 노선에 모두 등록 되어 있을 경우 오류 발생
      - [ ] 상행 역과 하행 역이 하나 라도 없을 경우 오류 발생
    

