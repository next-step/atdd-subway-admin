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

## 1단계 - 지하철 노선 관리
### 기능 목록
* Feature: 지하철 노선 관련 기능
    * [x] Scenario: 지하철 노선 생성한다
        * when: 지하철 노선 생성 요청 한다.
        * then: 지하철 노선이 생성된다.
    * [x] Scenario: 이미 등록된 지하철 노선 이름으로 지하철 노선을 생성한다.
        * given: 지하철 노선이 등록되어 있다.
        * when: 지하철 노선을 생성 요청 한다.
        * then: 지하철 노선 생성이 실패 한다.
    * [x] Scenario: 지하철 노선 목록 조회
        * given: 지하철 노선 분당선, 2호선이 등록되어 있다.
        * when: 지하철 노선 목록 조회를 요청 한다.
        * then: 지하철 노선 목록이 응답된다.
        * then: 지하철 노선 목록에 분당선, 2호선이 포함되어 있다.
    * [x] Scenario: 지하철 노선 단건 조회
        * given: 지하철 노선 분당선이 등록되어 있다.
        * when: 지하철 노선 분당선 조회를 요청한다.
        * then: 지하철 노선이 응답된다.
        * then: 지하철 노선 분당선이 응답 된다.
    * [x] Scenario: 존재하지 않는 지하철 노선 아이디를 단건 조회
        * given: 지하철 노선 분당선이 등록되어 있다.
        * when: 지하철 노선 아이디 100을 조회를 요청한다.
        * then: 지하철 노선 단건 조회가 실패 한다.
    * [x] Scenario: 지하철 노선 수정
        * given: 지하철 노선 분당선이 등록되어 있다.
        * when: 지하철 노선 이름을 구분당선으로 수정 요청한다.
        * then: 지하철 노선이 정상 수정된다.
    * [x] 지하철 노선 삭제
        * given: 지하철 노선 분당선이 등록되어 있다.
        * when: 지하철 노선 분당선을 삭제 요청한다.
        * then: 지하철 노선이 정상 삭제된다.

## 2단계 - 인수 테스트 리펙터링
### 요구 사항
#### API 변경 대응하기
* 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기
    * 두 좀점역은 구간의 현태로 관리되어야함.
* 노선 조회 시 응답 결과에 역 목록 추가하기
    *상행역 부터 하행역 순으로 정렬되어야함.

노선 생성 request
```http request
POST /lines HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8

{
    "color": "bg-red-600",
    "name": "신분당선",
    "upStationId": "1",
    "downStationId": "2",
    "distance": "10"
}
```
노선 조회 response
````http request
HTTP/1.1 200 
Content-Type: application/json

[
    {
        "id": 1,
        "name": "신분당선",
        "color": "bg-red-600",
        "stations": [
            {
                "id": 1,
                "name": "강남역",
                "createdDate": "2020-11-13T12:17:03.075",
                "modifiedDate": "2020-11-13T12:17:03.075"
            },
            {
                "id": 2,
                "name": "역삼역",
                "createdDate": "2020-11-13T12:17:03.092",
                "modifiedDate": "2020-11-13T12:17:03.092"
            }
        ],
        "createdDate": "2020-11-13T09:11:51.997",
        "modifiedDate": "2020-11-13T09:11:51.997"
    }
]
````

### 기능 목록
* Feature: 지하철 노선 관련 기능 리펙토링
    * [ ] Scenario: 지하철 노선 생성한다
        * given: 신분당선의 상행 종점 지하철역, 하행 종점 지하철역이 생성되어 있다.
        * given: 구간 거리 값을 알고 있다.
        * when: 신분당선 지하철 노선 생성 요청 한다.
        * then: 지하철 노선이 생성된다.
    * [ ] Scenario: 이미 등록된 지하철 노선 이름으로 지하철 노선을 생성한다.
        * given: 신분당선의 상행 종점 지하철역, 하행 종점 지하철역이 생성되어 있다.
        * given: 구간 거리 값을 알고 있다.
        * given: 신분당선 지하철 노선이 등록되어 있다.
        * when: 지하철 노선을 생성 요청 한다.
        * then: 지하철 노선 생성이 실패 한다.
    * [ ] Scenario: 지하철 노선 목록 조회
        * given: 신분당선의 상행 종점 지하철역, 하행 종점 지하철역이 생성되어 있다.
        * given: 구간 거리 값을 알고 있다.
        * given: 지하철 노선 신분당선, 2호선이 등록되어 있다.
        * when: 지하철 노선 목록 조회를 요청 한다.
        * then: 지하철 노선 목록이 응답된다.
        * then: 지하철 노선 목록에 신분당선, 2호선이 포함되어 있다.
        * then: 지하철 노선 목록에 신분당선의 상행, 하행 종점역 정보가 존재한다.
        * then: 지하철 노선 목록에 2호선의 상행, 하행 종점역 정보가 존재한다.
    * [ ] Scenario: 지하철 노선 단건 조회
        * given: 신분당선의 상행 종점 지하철역, 하행 종점 지하철역이 생성되어 있다.
        * given: 지하철 노선 신분당선이 등록되어 있다.
        * when: 지하철 노선 신분당선 조회를 요청한다.
        * then: 지하철 노선이 응답된다.
        * then: 지하철 노선 신분당선이 응답 된다.
        * then: 지하철 노선 목록에 신분당선의 상행 종점 지하철역, 하행 종점 지하철역이 존재한다.

### 기능 구현 순서
* [ ] 인수 테스트 코드 리펙토링
* [ ] 인수 테스트가 성공할 수 있도록 프로덕션 코드 수정
    * [ ] TDD 단위 테스트 코드 작성 및 수정
