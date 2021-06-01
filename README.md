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

---

# 요구사항 정리

## Step1 - 지하철 노선 관리

### 기능 구현

- [x] 기능 목록: 생성 / 목록 조회 / 조회 / 수정 / 삭제
- [x] 기능 구현 전 인수 테스트 작성
- [x] 기능 구현 후 인수 테스트 리팩터링

#### 지하철 노선 생성

- Request

    ```text
    POST /lines HTTP/1.1
    accept: */*
    content-type: application/json; charset=UTF-8
    
    {
        "color": "bg-red-600",
        "name": "신분당선"
    }
    ```

- Response

    ```text
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

#### 지하철 노선 목록 조회

- Request

    ```text
    GET /lines HTTP/1.1
    accept: application/json
    host: localhost:49468
    ```

- Response

    ```text
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

#### 지하철 노선 조회

- Request

    ```text
    GET /lines/1 HTTP/1.1
    accept: application/json
    host: localhost:49468
    ```

- Response

    ```text
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

#### 지하철 노선 수정

- Request

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
    ```

- Response

    ```text
    HTTP/1.1 200 
    Date: Fri, 13 Nov 2020 00:11:51 GMT
    ```

#### 지하철 노선 삭제

- Request

    ```text
    DELETE /lines/1 HTTP/1.1
    accept: */*
    host: localhost:49468
    ```

- Response

    ```text
    HTTP/1.1 204 
    Date: Fri, 13 Nov 2020 00:11:51 GMT
    ```

---

## Step2 - 인수 테스트 리팩토링

### Request 변경 사항

#### 지하철 노선 생성 요청

- 노선 생성 시 두 종점역의 ID가 필요

    ```text
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

#### 지하철 노선 조회 결과      

```text
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
