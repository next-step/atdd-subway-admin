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


## 1단계 요구사항

* 각 미션의 요구사항을 파악한다.
* 각 미션에 관한 API를 파악한다.
* 노선의 생성의 인수테스트를 작성하고 성공하는지 테스트 한다.
* 노선의 각 테스트케이스를 작성하고, 성공을 위하여 컨트롤러를 구현한다.
* 인수테스트의 중복 로직을 리팩터링한다.


## 1단계 질문


## 2단계 요구사항

* 테스트케이스에서 파라메터에 시작, 도착 종점역과 거리를 추가한다.
* 요청 클래스와 DTO에 내용을 반영하고 요청이 제대로 들어오는지 확인한다.
* 구간 도메인을 개발한다
* 노선 조회 시 구간이 출력 될 수 있도록 연관관계를 새로 맺도록 한다.
* 노선이 구간으로 관리되도록 개발한다. (개발 구간에 대한 테스트케이스를 먼저 작성한다)
* 노선 조회를 개발한다.


## 3단계 요구사항

* 구간 등록 인수 테스트 작성하고 BeforeEach 작성
* 인수테스트에 관련 된 컨트롤러 작성
* distance 원시 값을 포장 하도록 개발
* 예외 처리 개발
    * 역 사이의 길이가 정상 적이지 않는 경우 테스트케이스 작성
    * 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
    * 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
* 리팩토링(역할에 맞는 행동 분배)
    * 도메인 역할 점검
    * 상수 제거
    * 응답이 400이 아닌 500으로 떨어지는 경우 확인
    * 인수 테스트에서 응답의 메시지를 확인 하고 테스트 가능 하도록 수정
* 테스트케이스 작성 후 개발
    * 구간 추가 - 역 사이에  새로운 역 등록
    * 새로운 역이 상행 종점
    * 새로운 역이 하행 종점
  

## 3단계 질문
Q. 인수테스트를 작성 하면서 유닛테스트는 작성은 어떤식으로 할까요?