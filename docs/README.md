# 인수 테스트 주도 개발
## 1단계 - 지하철 노선 관리

### 요구사항
- [x] 기능목록: 생성 / 목록 조회 / 조회 / 수정 / 삭제
- [x] 기능구현 전 인수 테스트 작성
- [x] 기능구현 후 인수 테스트 리팩토링

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

### 코드 리뷰 사항

- [ ] 읽기 전용 조회 메서드에 ```@Transactional(readOnly = true)``` 적용
- [ ] LineResponse의 List를 만드는 정적 팩토리 메서드 추가   
- [ ] ```@RestControllerAdvice```를 사용한 전역적 Exception Handling
- [ ] LineResponse의 ```equals() & hashcode()``` 재정의를 통해서 목록 조회 검증 코드 리팩토링
- [ ] 생성한 노선에 대해 body가 비어있는지 뿐만아니라 값도 확인 
- [ ] 생성한 노선에 대해 header값이 비어있는지 뿐만아니라 저장된 값도 확인
- [ ] 테스트에 쓸 노선 생성할 때 Map 대신 LineRequest 사용
- [ ] 테스트 메서드에서 중복부분을 분리해서 재사용 (한글 메서명 사용해보기)

