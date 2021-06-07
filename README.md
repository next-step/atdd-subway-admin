# 인수 테스트 주도 개발 - [3단계] 구간 추가 기능

## 요구사항
### 지하철 구간 등록 기능을 구현하기

- [ ] 기능 구현 전 인수 테스트 작성
- [ ] 예외 케이스 처리 인수 테스트 작성


#
## 구현 목록
### 테스트 케이스 작성
- Entity
- 인수 테스트

### 정렬된 역 목록 조회
- 노선 조회 시, 역 정보 포함
- 역 정보 정렬

### Entity 추출
- 거리

### 구간 등록
- 역과 역 사이 구간 등록
- 예외 처리
  - 거리에 따른 예외 처리
    - 같거나 큰 경우 예외
  - 이미 노선에 상행/하행이 모두 등록되어 있을 경우 예외 처리
  

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
## 구간 등록 API
### 구간 등록 request
```http request
POST /lines/1/sections HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8
host: localhost:52165

{
    "downStationId": "4",
    "upStationId": "2",
    "distance": 10
}
```
