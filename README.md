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

## STEP1 
### 요구사항
- 지하철 노선 관련 기능 구현 및 인수 테스트 작성 
    - 지하철 노선 생성
    - 지하철 노선 목록 조회
    - 지하철 노선 조회
    - 지하철 노선 삭제
    
## STEP2
### 요구사항
- 노선 생성 시 상행, 하행 추가
    - upStation, downStation
- 노선의 역 목록을 조회하는 기능 추가

## STEP3
### 요구사항
- 구간 등록 
    - 기능
        - 종점 상행 등록
        - 종점 하행 등록
        - 가운데 상행 등록
        - 가운데 하행 등록
    - 익셉션
        - 이미 존재하는 구간 익셉션 발생
        - 기존보다 거리가 큰 경우 익셉션 발생
    
## STEP4
### 요구사항
- 구간 제거
    - 기능
        - 구간 삭제하면 두 거리를 연결
    - 익셉션
        - 구간이 1개인 경우 익셉션
        - 역이 구간에 등록되어 있지 않으면 익셉션
