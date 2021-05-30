# 인수 테스트 주도 개발
## 2단계 - 인수 테스트 리팩터링

### 요구사항
**API 변경 대응하기**
- [ ] 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기
    * 두 종점역은 구간의 형태로 관리되어야 함
- [ ] 노선 조회 시 응답 결과에 역 목록 추가하기
    * 상행역 부터 하행역 순으로 정렬되어야 함

**노선 생성 request**
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

**노선 조회 response**
```http request
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
