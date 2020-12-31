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

## Step1 지하철 노선 관리 

### 요구사항
- [X] 지하철 노선 관련 기능의 인수 테스트를 작성하기
- [X] 지하철 노선 관련 기능 구현하기
- [X] 인수 테스트 리팩터링

## Step2 지하철 노선 구간 조회

### 요구사항
- [X] 노선 생성 시 종점역(상행, 하행), 구간을 함께 추가하기
- [X] 노선 조회 응답 결과에 등록된 구간을 참고하여 역 목록 응답 추가하기

## Step3 지하철 노선에 구간 등록

### 요구사항
- 지하철 구간 등록 인수 테스트 작성
    - [X] 역 사이에 새로운 역을 등록할 경우
    - [X] 새로운 역을 상행 종점으로 등록할 경우
    - [X] 새로운 역을 하행 종점으로 등록할 경우
    
- 지하철 구간 등록 기능 구현
    - `지하철 노선 구간들(LineStations)`
        - [X] 새로운 지하철 구간을 지하철 노선 구간으로 등록한다. 
    - `지하철 노선 구간(LineStation)`
        - [X] 새로운 지하철 구이 기존 구간에 추가 될 수 있는 지 확인한다.
        - [X] 새로운 지하철 구간을 반영할 수 있는지 확인한다.
        - [X] 새로운 지하철 구간을 반영하여 기존 지하철 노선 구간을 변경한다.
    - `지하철 구간(Section)`
        - 새로운 지하철 구간을 입력받아 변경된 지하철 구간을 만든다.(A-B에서 A-C를 적용 시 B-C를 반환한다)
        - [X] 구간 사이에 새로운 역을 반영하여 기존 구간을 업데이트한다.
    - `거리(Distance)`
        - [X] 다른 거리 값을 뺄수 있다.
    
- 구간 등록 예외 케이스 인수 테스트 작성
    - [X] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
    - [X] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
        -  A-B, B-C 구간이 등록된 상황에서 B-C 구간을 등록할 수 없음(A-C 구간도 등록할 수 없음)
    - [X] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
    
- 구간 등록 예외 케이스 처리 기능 구현

## Step4 지하철 노선에 구간 제거

### 요구사항
- 지하철 구간 제거 인수테스트 작성
    - [X] 지하철 노선에서 지하철 역을 제거한다.
    - [X] 지하철 구간이 하나이거나 등록되지 않은 역이면 제거할 수 없다.
        
- 노선의 구간을 제거하는 기능을 구현
    - `지하철 노선 구간들(LineStations)`
        - [ ] 지하철 노선 구간을 제거한다.
        - [ ] 지하철 노선 구간이 하나 이거나 등록되지 않은 역이면 예외 처리 한다. 
    - `지하철 노선 구간(LineStation)`
        - [ ] 지하철 노선 구간을 병합한다.
    - `지하철 구간(Section)`
        - [X] 지하철 구간을 병합한다.
    - `거리(Distance)`
        - [X] 다른 거리를 더할 수 있다.
