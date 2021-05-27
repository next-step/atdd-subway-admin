# 인수 테스트 주도 개발
## 1단계 - 지하철 노선 관리

### 요구사항
- [x] 기능목록: 생성 / 목록 조회 / 조회 / 수정 / 삭제
- [ ] 기능구현 전 인수 테스트 작성
- [ ] 기능구현 후 인스 테스트 리팩토링

**지하철 노선 생성 request**
```http request
POST /lines HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8

{
    "color": "bg-red-600",
    "name": "신분당선"
}
```

**지하철 노선 생성 response**
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

**지하철 노선 목록 조회 request**
```http request
GET /lines HTTP/1.1
accept: application/json
host: localhost:49468
```

**지하철 노선 목록 조회 response**
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

**지하철 노선 조회 request**
```http request
GET /lines/1 HTTP/1.1
accept: application/json
host: localhost:49468
```

**지하철 노선 조회 response**
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

**지하철 노선 수정 request**
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

**지하철 노선 수정 response**
```http request
HTTP/1.1 200 
Date: Fri, 13 Nov 2020 00:11:51 GMT
```

**지하철 노선 삭제 request**
```http request
DELETE /lines/1 HTTP/1.1
accept: */*
host: localhost:49468
```

**지하철 노선 삭제 response**
```http request
DELETE /lines/1 HTTP/1.1
HTTP/1.1 204 
Date: Fri, 13 Nov 2020 00:11:51 GMT
```
