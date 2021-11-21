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

### 요구사항

- 기능 목록: 생성 / 목록 조회 / 조회 / 수정 / 삭제
- 기능 구현 전 인수 테스트 작성
- 기능 구현 후 인수 테스트 리팩터링

### 기능목록

- [X] 지하철 노선 생성
  - [X] 인수 테스트 시나리오 및 테스트 작성
    - [X] 지하철 노선을 생성한다
    - [X] 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다
    - [X] 공백의 노선명으로 지하철 노선을 생성한다
    - [X] 공백의 노선색상으로 지하철 노선을 생성한다
  - [X] 기능 테스트 및 구현
    - [X] 기존에 존재하는 노선명 존재시 오류를 던진다
    - [X] Controller에서 필수 파라메터를 검증한다
    - [X] LineControllerAdvice로 예외 상황을 관리한다
- [X] 지하철 노선 목록 조회
  - [X] 인수 테스트 시나리오 및 테스트 작성
    - [X] 지하철 노선 목록을 조회한다
  - [X] 기능 테스트 및 구현
    - [X] 지하철 노선 목록 조회 기능 추가
- [X] 지하철 노선 조회
  - [X] 인수 테스트 시나리오 및 테스트 작성
    - [X] 지하철 노선을 조회한다
    - [X] 등록되지 않은 지하철 노선을 조회한다
  - [X] 기능 테스트 및 구현
    - [X] 지하철 노선 조회 구현
    - [X] 등록되지 않은 노선 구현시 오류를 던진다
- [X] 지하철 노선 수정
  - [X] 인수 테스트 시나리오 및 테스트 작성
    - [X] 지하철 노선을 수정한다
    - [X] 등록되지 않은 지하철 노선을 수정한다
    - [X] 공백의 노선명으로 지하철 노선을 수정한다
    - [X] 공백의 노선색상으로 지하철 노선을 수정한다
  - [X] 기능 테스트 및 구현
    - [X] 노선 수정 기능 구현
    - [X] 등록된 노선이 없을 때 오류 발생
- [X] 지하철 노선 삭제
  - [X] 인수 테스트 시나리오 및 테스트 작성
    - [X]  지하철 노선을 제거한다
    - [X]  등록되지 않은 지하철 노선을 제거한다
  - [X] 기능 테스트 및 구현
    - [X] 노선 삭제 기능 구현
    - [X] 등록된 노선이 없을 때 오류 발생

## 2단계 - 인수 테스트 리팩터링

### 요구사항

- 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기
  - 두 종점역은 구간의 형태로 관리되어야 함
- 노선 조회 시 응답 결과에 역 목록 추가하기
  - 상행역 부터 하행역 순으로 정렬되어야 함

#### 노선 생성 request

```http request
POST /lines HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8
```

```json
{
    "color": "bg-red-600",
    "name": "신분당선",
    "upStationId": "1",
    "downStationId": "2",
    "distance": "10"
}
```

#### 노선 조회 response

```http request
HTTP/1.1 200 
Content-Type: application/json
```

```json
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

### 기능목록

- [ ] 노선 생성
  - [X] 인수테스트 작성
    - [X] 상행선, 하행선, 거리가 포함된 노선을 생성 요청한다
    - [X] 상행선, 하행선, 거리 중 한개 이상 미포함하여 노선을 생성 요청한다
    - [X] 상행선, 하행선에 등록이 안된 역을 포함한 노선을 생성 요청한다
  - [ ] 기능 구현
    - [X] 요청시 상행선, 하행선, 거리를 모두 입력하거나, 모두 입력하지 않아야 한다.
    - [X] 구간 도메인 생성
    - [ ] 노선 저장시 구간 요청이 있으면 구간 생성하여 저장
- [ ] 노선 조회
  - [ ] 인수테스트 작성
  - [ ] 기능 구현
- [ ] 노선 수정
  - [ ] 인수테스트 작성
  - [ ] 기능 구현
