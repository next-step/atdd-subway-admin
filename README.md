# 인수 테스트 주도 개발 - [1단계] 지하철 노선 관리

## 요구사항
### 지하철 노선 관리 기능을 구현하기
- [ ] 기능 목록: 생성 / 목록 조회 / 조회 / 수정 / 삭제
- [X] 기능 구현 전 인수 테스트 작성
- [ ] 기능 구현 후 인수 테스트 리팩터링
#

## 도메인 정보
### 지하철 역(station)
* 지하철 역 속성: 이름(name)

### 지하철 구간(section)
* 지하철 (상행 방향)역과 (하행 방향)역 사이의 연결 정보
* 지하철 구간 속성: 길이(distance)

### 지하철 노선(line)
* 지하철 구간의 모음으로 구간에 포함된 지하철 역의 연결 정보
* 지하철 노선 속성: 노선 이름(name), 노선 색(color)

#
## 구현 내용
### 지하철 노선 관련 기능의 인수 테스트를 작성하기 
* LineAcceptanceTest
    * 지하철 노선을 생성한다.
    * 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.
    * 지하철 노선 목록을 조회한다.
    * 지하철 노선을 조회한다.
    * 지하철 노선을 수정한다.
    * 지하철 노선을 제거한다.
    
### 지하철 노선 관련 기능 구현하기
* LineController
    * 추가
    * 라인 단건 조회
    * 라인 목록 조회
    * 수정
    * 삭제

### 인수 테스트 리팩터링
* 인수 테스트의 각 스텝들을 메서드로 분리
* 스텝 메서드들을 static 선언


## 노선 관리 API Request / Response
### 지하철 노선 생성 request
```http request
POST /lines HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8

{
    "color": "bg-red-600",
    "name": "신분당선"
}
```

### 지하철 노선 생성 response
```http request
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

### 지하철 노선 목록 조회 request
```http request
GET /lines HTTP/1.1
accept: application/json
host: localhost:49468
```

### 지하철 노선 목록 조회 response
```http request
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

### 지하철 노선 조회 request
```http request
GET /lines/1 HTTP/1.1
accept: application/json
host: localhost:49468
```

### 지하철 노선 조회 response
```http request
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

### 지하철 노선 수정 request
```http request
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

### 지하철 노선 수정 response
```http request
HTTP/1.1 200 
Date: Fri, 13 Nov 2020 00:11:51 GMT
```

### 지하철 노선 삭제 request
```http request
DELETE /lines/1 HTTP/1.1
accept: */*
host: localhost:49468
```

### 지하철 노선 삭제 response
```http request
HTTP/1.1 204 
Date: Fri, 13 Nov 2020 00:11:51 GMT
```
