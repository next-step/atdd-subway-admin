## 4단계 - 구간 제거 기능

### 요구사항

- 지하철 구간 삭제 요청

```text
DELETE /lines/1/sections?stationId=2 HTTP/1.1
accept: */*
host: localhost:52165
```

- 종점을 삭제하면, 직전 역이 종점이 됨
- 중간역을 삭제하면, 구간 역들 재배치 함
    - A-B-C에서 중간 B역 제거 시, A-C로 됨
    - A-C 거리는 두 구간(A-B, B-C)의 거리 합으로 재산정
- 예외 처리
    - 구간이 하나인 노선에서는 마지막 구간을 제거하지 못함
    - 노선에 등록되어 있지 않은 역을 제거하지 못함
    - 이 외 예외처리를 생각하여 인수테스트 작성하기

### 기능목록

- 구간들에서 상행종점을 삭제한다
- 구간들에서 하행종점을 삭제한다
- 구간들에서 중간역을 삭제한다
- 예외처리1: 노선에 해당 역이 없을 경우, 삭제 안됨
- 예외처리2: 노선에 구간이 하나일 경우, 삭제 안됨

## 3단계 - 구간 추가 기능

### 요구사항

- 구간 사이에 구간 추가 시, 새로운 길이를 뺀 나머지 계산하여 길이 설정됨
- 상행 종점에 새 구간을 추가 시, 상행 종점 앞에 추가됨
- 새 구간을 하행 종점에 추가 시, 하행 종점 뒤에 추가됨
- 예외 케이스
    - 구간 사이에 구간 추가 시, 기존 구간 길이보다 크거나 같으면 등록 안됨
    - 새로운 구간의 상행역과 하행역이 기존 역들과 같을 시, 추가 안됨
    - 구간 추가 시, 상행역과 하행역이 둘다 기존에 등록된 것이 없으면 추가 안됨

### 기능 목록

- 노선에 구간 추가 인수 테스트
- 구간 사이에 구간 추가
    - 변경될 뒷구간 찾기
    - 뒷구간 상행선 업데이트
        - 새로운 길이를 뺀 나머지 계산하여 길이 설정됨
    - 새 구간 추가
    - 기존 구간 길이보다 크거나 같으면 등록 안됨
- 상행종점에 새 구간 추가
- 하행종점에 새 구간 추가
- 예외1: 구간 사이에 구간 추가 시, 기존 구간 길이보다 크거나 같으면 등록 안됨
- 예외2: 상행과 하행역이 기존 역들과 같을 시, 추가 안됨 체크
- 예외3: 구간 추가 시, 상행역과 하행역이 둘다 기존에 등록된 것이 없으면 추가 안됨

## 2단계 - 인수 테스트 리팩터링

### 요구사항

- 두 종점역을 추가하여 노선 생성하도록 변경
- 노선 객체에서는 두 종점은 구간정보로 관리
- 노선 조회 시, 역 목록이 조회되어야 함
- 노선 조회 시, 구간이 상행 종점부터 하행 종점까지 정렬되어 조회되어야 함
- 노선과 구간 관계 설정
- 인수테스트 변경 후 작업

### 기능 목록

- 구간 등록 기능: 2개의 역과 거리를 등록한다
- 노선 생성 기능
    - 두 종점역이라는 구간을 추가로 등록한다
- 노선 조회 기능
    - 구간이 아닌 역 목록도 함께 조회
    - 역 목록 정렬: 상행종점부터 하행종점까지 정렬

## 1단계 - 지하철 노선 관리

지하철 노션 관리 기능을 구현

- 노선 생성

```text
POST /lines HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8

{
    "color": "bg-red-600",
    "name": "신분당선"
}


HTTP/1.1 201 
Location: /lines/1
Content-Type: application/json
Date: Fri, 13 Nov 2020 00:11:51 GMT

{
    "id": 1,
    "name": "신분당선",
    "color": "bg-red-600",
    "createdDate": "2020-11-13T09:11:51.997",
    "modifiedDate": "2020-11-13T09:11:51.997"
}
```

- 노선 목록 조회

```text
GET /lines HTTP/1.1
accept: application/json
host: localhost:49468


HTTP/1.1 200 
Content-Type: application/json
Date: Fri, 13 Nov 2020 00:11:51 GMT

[
    {
        "id": 1,
        "name": "신분당선",
        "color": "bg-red-600",
        "stations": [
            
        ],
        "createdDate": "2020-11-13T09:11:52.084",
        "modifiedDate": "2020-11-13T09:11:52.084"
    },
    {
        "id": 2,
        "name": "2호선",
        "color": "bg-green-600",
        "stations": [
            
        ],
        "createdDate": "2020-11-13T09:11:52.098",
        "modifiedDate": "2020-11-13T09:11:52.098"
    }
]
```

- 노선 조회

```text
GET /lines/1 HTTP/1.1
accept: application/json
host: localhost:49468


HTTP/1.1 200 
Content-Type: application/json
Date: Fri, 13 Nov 2020 00:11:51 GMT

{
    "id": 1,
    "name": "신분당선",
    "color": "bg-red-600",
    "stations": [
        
    ],
    "createdDate": "2020-11-13T09:11:51.866",
    "modifiedDate": "2020-11-13T09:11:51.866"
}
```

- 노선 수정

```text
PUT /lines/1 HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8
content-length: 45
host: localhost:49468

{
    "color": "bg-blue-600",
    "name": "구분당선"
}


HTTP/1.1 200 
Date: Fri, 13 Nov 2020 00:11:51 GMT
```

- 노선 삭제

```text
DELETE /lines/1 HTTP/1.1
accept: */*
host: localhost:49468

HTTP/1.1 204 
Date: Fri, 13 Nov 2020 00:11:51 GMT
```

---

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
