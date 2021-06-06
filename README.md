# 인수 테스트 주도 개발 - [2단계] 인수 테스트 리팩터링

## 요구사항
### 지하철 노선 관리 기능을 구현하기

- [X] 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기
  - 두 종점역은 구간의 형태로 관리되어야 함
- [X] 노선 조회 시 응답 결과에 역 목록 추가하기
  - 상행역 부터 하행역 순으로 정렬되어야 함

#
## 구현 내용
### 1. 관계 정의
- Line(1) - Section(N)
- Line(N) - Station(N)
- Station(1) - Section(N)


### 2. 구간 Entity 생성
- 하항 역(Station)
- 상행 역(Station)
- 거리 (distance)


### 3. 변경된 노선 생성/조회
- 변경된 노선 생성/조회하는 테스트 코드 추가
- 테스트 성공 구현 
- 변경된 노선 생성/조회 테스트 코드 기존 생성 테스트 코드로 변경


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

## 노선 생성 API Request / Response
### 노선 생성 request
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

### 노선 생성 response
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
