### 요구사항 도출
- 요청 기능 구현
    + 목록조회 
    + 단건 조회
    + 단건 수정
    + 단건 삭제

- 요청 URL에 매핑되는 Controller 구현
    + [X] 노선 생성 요청
    + [X] 노선 목록조회 요청
    + [X] 노선 조회 요청
    + [X] 노선 수정 요청
    + [X] 노선 삭제 요청
- Controller에서 요청 처리에 사용되는 서비스 메소드 구현
    + [X] 신규 노선 등록
    + [X] 전체 지하철 라인 목록조회
    + [X] Id기준 단건 조회
    + [X] Id기준 업데이트
    + [X] Id기준 삭제

### 요구사항 예시
#### 지하철 노선 생성 
- request
```
POST /lines HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8

{
    "color": "bg-red-600",
    "name": "신분당선"
}
```
- response
```
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
---
#### 지하철 노선 목록 조회
- request
```
GET /lines HTTP/1.1
accept: application/json
host: localhost:49468
```
- response
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
---
#### 지하철 노선 조회
- request
```
GET /lines/1 HTTP/1.1
accept: application/json
host: localhost:49468
```
- response
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
---
#### 지하철 노선 수정
- request
```PUT /lines/1 HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8
content-length: 45
host: localhost:49468

{
    "color": "bg-blue-600",
    "name": "구분당선"
}
```
- response
```
HTTP/1.1 200 
Date: Fri, 13 Nov 2020 00:11:51 GMT
```
---
#### 지하철 노선 삭제
- request
```
DELETE /lines/1 HTTP/1.1
accept: */*
host: localhost:49468
```
- response
```
HTTP/1.1 204 
Date: Fri, 13 Nov 2020 00:11:51 GMT
```
