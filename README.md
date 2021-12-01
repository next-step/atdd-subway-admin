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

# 지하철 노선도 관리

- 지하철 노선도를 관리할 수 있는 어드민 서비스를 단계별로 구현하세요.

## 도메인 설명

### 지하철 역(station)

* 지하철 역 속성:
    * 이름(name)

### 지하철 구간(section)

* 지하철 (상행 방향)역과 (하행 방향)역 사이의 연결 정보
* 지하철 구간 속성:
    * 길이(distance)

### 지하철 노선(line)

* 지하철 구간의 모음으로 구간에 포함된 지하철 역의 연결 정보 지하철 노선 속성:
    * 노선 이름(name)
    * 노선 색(color)

## 미션 단계

- [사전 제공] 지하철 역 관리
- [1단계] 지하철 노선 관리
- [2단계] 지하철 노선에 구간 등록
- [3단계] 노선에 등록된 역 목록 조회
- [4단계] 지하철 노선에 구간 제외

# 1단계 - 지하철 노선 관리

- 기능 목록: 생성 / 목록 조회 / 조회 / 수정 / 삭제
- **기능 구현 전 인수 테스트 작성**
- 기능 구현 후 인수 테스트 리팩터링

### 요구사항

- [X] 지하철 노선을 생성한다.
- [X] 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.
- [X] 지하철 노선 목록을 조회한다.
- [X] 지하철 노선을 조회한다.
- [X] 지하철 노선을 수정한다.
- [X] 지하철 노선을 제거한다.

# 2단계 - 인수 테스트 리팩터링

- API 변경 대응하기

노선 생성 request
<details>
  <summary>자세히</summary>

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
</details>


노선 조회 response
<details>
  <summary>자세히</summary>

```
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
```
</details>

### 요구사항
- [X] 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기
    * 두 종점역은 구간의 형태로 관리되어야 함
- [X] 노선 조회 시 응답 결과에 역 목록 추가하기
    - [X] 상행역 부터 하행역 순으로 정렬되어야 함



# 3단계 - 구간 추가 기능

- 지하철 구간 등록 기능을 구현하기
- 기능 구현 전 인수 테스트 작성
- 예외 케이스 처리 인수 테스트 작성

구간 등록 API Request
<details>
  <summary>자세히</summary>

```http request
POST /lines/1/sections HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8
host: localhost:52165

{
    "downStationId": "4",
    "upStationId": "2",
    "distance": 10
}
```
</details>

### 요구사항
- [X] 역 사이에 새로운 역을 등록할 경우
  - [X] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
    * 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
- [ ] 새로운 역을 상행 종점으로 등록할 경우
- [ ] 새로운 역을 하행 종점으로 등록할 경우
- [X] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
  * 아래의 이미지 에서 A-B, B-C 구간이 등록된 상황에서 B-C 구간을 등록할 수 없음(A-C 구간도 등록할 수 없음)
- [X] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음


