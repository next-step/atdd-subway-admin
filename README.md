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

#### 프론트에서 정상적으로 접근이 안되는 경우  
```
Backend 구동 -> Front 구동 -> Front 접근
``` 
<br>

## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## "Step 1"

### 요구사항
-[ ] 지하철 노선 관련 기능의 인수 테스트를 작성하기
-[ ] 지하철 노선 관련 기능 구현하기
-[ ] 인수 테스트 리팩터링

### 기능목록  
지하철 노선 기능
- 지하철 노선 생성
- 지하철 노선 목록 조회
- 지하철 노선 조회
- 지하철 노선 수정
- 지하철 노선 삭제

## "Step 2"

### 요구사항
-[ ] 노선 생성 시 종점역(상행, 하행)을 함께 추가하기  
-[ ] 노선 조회 응답 결과에 등록된 구간을 참고하여 역 목록 응답 추가하기  

### 기능목록
노선 생성 시 종점역 추가하기  
- 인수 테스트와 DTO 등 수정이 필요함  

노선의 역 목록을 조회하는 기능 구현하기
- 노선 조회 시 역 목록을 함께 응답할 수 있도록 변경  
- 노선에 등록된 구간을 순서대로 정렬하여 상행 종점부터 하행 종점까지 목록을 응답하기  
- 필요시 노선과 구간(혹은 역)의 관계를 새로 맺기  

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/next-step/atdd-subway-admin/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/next-step/atdd-subway-admin/blob/master/LICENSE.md) licensed.
