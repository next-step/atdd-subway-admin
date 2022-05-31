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

---------------
## 기능 목록
### 도메인
1. Station
   1. Name
2. Line
   1. Name
   2. Color
   3. Sections (OneToMany)
3. Section
   1. 상행 Station (ManyToOne)
   2. 하행 Station (ManyToOne)
   3. Distance
4. Sections
   1. Section의 일급컬렉션
   2. 등록 기능
      - [ ] 기존 존재하는 구간 사이에 새로운 역 등록
      - [ ] 새로운 구간을 가장 앞에 등록
      - [ ] 새로운 구간을 가장 뒤에 등록
   3. 세부 기능
      - [ ] 새로운 구간을 등록할 위치 찾기
      - [ ] 새로운 구간을 가장 앞 또는 중간에 등록 시 뒤의 구간 거리 새로 계산
   5. 예외 처리
      - [ ] 역 사이에 새로운 역을 등록할 경우, 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.
      - [ ] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.
      - [ ] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.

### 지하철역 인수테스트
1. 지하철역 생성
   1. 지하철역을 생성한다.
   2. 기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.
2. 지하철역 목록 조회
   1. 지하철역 목록을 조회한다.
3. 지하철역 삭제
   1. 지하철역을 삭제한다.

### 노선 인수테스트
1. 노선 생성
2. 노선 전체 목록 조회
3. 특정 노선 조회
4. 노선 수정
5. 노선 삭제

### 구간 인수 테스트
1. 역 사이에 새로운 역 등록
2. 새로운 역을 상행종점으로 등록
3. 새로운 역을 하행종점으로 등록
4. 역 사이에 새로운 역을 등록할 역우, 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.
5. 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.
6. 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.