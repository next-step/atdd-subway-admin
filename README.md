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

## [STEP 1] 지하철 노선 관리
* 지하철 노선 관련 기능
    * 인수 테스트를 작성하기
    * 지하철 노선 관련 기능 구현하기
        - [X] 노선 생성 POST : /lines
        - [X] 기존에 존재하는 지하철 노선 이름으로 생성
        - [X] 노선 목록 조회 GET : /lines
        - [X] 노선 조회 GET : /lines/{id}
        - [X] 노선 수정 PUT : /lines/{id}
        - [X] 노선 제거 DELETE : /lines/{id}
    * 인수 테스트 리팩터링

## [STEP 2] 지하철 노선 관리
* 지하철 노선 구간 조회 기능
    * 노선 생성 시 종점역(상행, 하행)을 함께 추가하기
        - [X] 인수 테스트 작성 및 Request DTO 수정
        - [X] 지하철 구간 도메인 생성 
        - [X] 지하철 노선 생성시 구간(상행, 하행)을 갖도록 수정
    * 노선 조회 응답 결과에 등록된 구간을 참고하여 역 목록 응답 추가하기
        - [X] DTO 수정
    * 인수 테스트 리팩터링


## [STEP 3] 지하철 노선에 구간 등록
* 지하철 노선 구간 조회 기능
    * 지하철 구간 등록 인수 테스트 작성
    * 지하철 구간 등록 기능 구현
        - [X] 역 사이에 새로운 역을 등록할 경우 : 기존 상행역 - 새로운 하행역 관계
        - [X] 역 사이에 새로운 역을 등록할 경우 : 새로운 상행역 - 기존 하행역 관계
        - [X] 새로운 역을 상행 종점으로 등록할 경우
        - [X] 새로운 역을 하행 종점으로 등록할 경우
    
    * 구간 등록 예외 케이스 인수 테스트 작성
    * 구간 등록 예외 케이스 처리 기능 구현
        - [X] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
        - [X] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
        - [X] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

## [STEP4] 지하철 노선에 구간 제거
* 지하철 노선에 구간 제거
    * 노선의 구간 제거 인수 테스트 작성
    * 노선의 구간 제거 기능 구현
        - [ ] 중간역이 제거될 경우 재배치
          * A-B-C 역이 연결되어 있을때 B역을 제거할 경우 A-C로 재배치
          * 거리는 두 구간의 합으로 수정
        - [ ] 종점이 제거될 경우 다음역이 종점이 됨
    
    * 구간 삭제시 예외 케이스 인수 테스트 작성
    * 구간 삭제시 예외 케이스 처리 기능 구현
        - [ ] 노선에 등록되지 않은 역은 제거할 수 없음
        - [ ] 등록된 구간이 1개일 때, 제거할 수 없음

## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/next-step/atdd-subway-admin/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/next-step/atdd-subway-admin/blob/master/LICENSE.md) licensed.
