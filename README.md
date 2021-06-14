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

## 🚀 1단계 - 지하철 노선 관리

### 요구사항
#### 기능 목록
1. 노선 생성
2. 노선 목록 조회
3. 노선 조회
4. 노선 수정
5. 노선 삭제

#### 요구사항 설명
1. 지하철 노선 관련 기능의 인수 테스트 작성
2. 지하철 노선 관련 기능 구현
3. 인수 테스트 리팩터링

#### 노선 관리 API Request / Response
1. 지하철 노선 생성
    1. Request
       ```
        POST /lines HTTP/1.1
        accept: */*
        content-type: application/json; charset=UTF-8
        
        {
        "color": "bg-red-600",
        "name": "신분당선"
        }
        ```
    2. Response
       ```
        {
            "id": 1,
            "name": "신분당선",
            "color": "bg-red-600",
            "createdDate": "2020-11-13T09:11:51.997",
            "modifiedDate": "2020-11-13T09:11:51.997"
        }
        ```

2. 지하철 노선 목록 조회
    1. Request
       ```
        GET /lines HTTP/1.1
        accept: application/json
        host: localhost:49468
        ```
    2. Response
       ```
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
       
3. 지하철 노선 조회
   1. Request
      ```
        GET /lines/1 HTTP/1.1
        accept: application/json
        host: localhost:49468
       ```
   2. Response
      ```
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

4. 지하철 노선 수정
    1. Request
       ```
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
    2. Response
       ```
        HTTP/1.1 200
        Date: Fri, 13 Nov 2020 00:11:51 GMT
        ```

5. 지하철 노선 삭제
    1. Request
       ```
        DELETE /lines/1 HTTP/1.1
        accept: */*
        host: localhost:49468
        ```
    2. Response
       ```
        HTTP/1.1 204
        Date: Fri, 13 Nov 2020 00:11:51 GMT
       ```

## 디렉토리 구조
```
│  ├─java
│  │  └─nextstep
│  │      └─subway
│  │          │  PageController.java
│  │          │  SubwayApplication.java
│  │          │
│  │          ├─common
│  │          │      BaseEntity.java              공통 컬럼 관리 엔티티
│  │          │
│  │          ├─line                              지하철 노선 관리
│  │          │  ├─application
│  │          │  │      LineService.java
│  │          │  │
│  │          │  ├─domain
│  │          │  │      Line.java
│  │          │  │      LineRepository.java
│  │          │  │
│  │          │  ├─dto
│  │          │  │      LineRequest.java
│  │          │  │      LineResponse.java
│  │          │  │
│  │          │  └─ui
│  │          │          LineController.java
│  │          │
│  │          └─station                           지하철 역 관리
│  │              ├─application
│  │              │      StationService.java
│  │              │
│  │              ├─domain
│  │              │      Station.java
│  │              │      StationRepository.java
│  │              │
│  │              ├─dto
│  │              │      StationRequest.java
│  │              │      StationResponse.java
│  │              │
│  │              └─ui
│  │                      StationController.java
│  │
│  └─resources
│      │  application.properties
│      │  logback-access.xml
│      │
│      ├─static
│      │  └─images
│      │          logo_small.png
│      │          main_logo.png
│      │
│      └─templates
│              index.html
│
└─test
   │  test.iml
   │
   └─java
       └─nextstep
           └─subway
               │  AcceptanceTest.java
               │  SubwayApplicationTests.java
               │
               ├─line
               │      LineAcceptanceTest.java
               │
               ├─station
               │      StationAcceptanceTest.java
               │
               └─utils
                       DatabaseCleanup.java
```