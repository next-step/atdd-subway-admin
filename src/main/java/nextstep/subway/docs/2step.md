# 🚀 2단계 - 인수 테스트 리팩터링

# 요구사항

### API 변경 대응하기

- [ ] 노선 생성
    - [ ] 상행, 하행 종점역 정보 포함
    - [ ] 상행, 하행 종점역은 **구간**의 형태로 관리
- [ ] 노선 조회
    - [ ] 역 목록 추가
    - [ ] 상행역 부터 하행역 순으로 정렬

변경된 노선 API - 노선 생성 Request
**노선 생성 request**

```javascript
POST / lines
HTTP / 1.1
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

변경된 노선 API - 노선 조회 Response
**노선 조회 response**

```javascript
HTTP / 1.1
200
Content - Type
:
application / json
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

# 요구사항 설명

## 노선 생성 시 두 종점역 추가하기

- 인수 테스트와 DTO 등 수정이 필요함

```java
public class LineRequest {
	private String name;
	private String color;
	private Long upStationId;       // 추가
	private Long downStationId;     // 추가
	private int distance;           // 추가
    ...
}

```

## 노선 객체에서 구간 정보를 관리하기

- 노선 생성시 전달되는 두 종점역은 노선의 상태로 관리되는 것이 아니라 구간으로 관리되어야 함

```java
public class Line {
    ...
	private List<Section> sections;
    ...
}

```

## 노선의 역 목록을 조회하는 기능 구현하기

- 노선 조회 시 역 목록을 함께 응답할 수 있도록 변경
- 노선에 등록된 구간을 순서대로 정렬하여 상행 종점부터 하행 종점까지 목록을 응답하기
- 필요시 노선과 구간(혹은 역)의 관계를 새로 맺기

# 힌트

## 기능 변경 시 인수 테스트를 먼저 변경하기

- 기능(혹은 스펙) 변경 시 테스트가 있는 환경에서 프로덕션 코드를 먼저 수정할 경우 어려움을 겪을 수 있음
    - 프로덕션 코드를 수정하고 그에 맞춰 테스트 코드를 수정해 주어야 해서 두번 작업하는 느낌
- 항상 테스트를 먼저 수정한 다음 프로덕션을 수정하자!
- 더 좋은 방법은 기존 테스트는 두고 새로운 테스트를 먼저 만들고 시작하자!

## 프론트엔드

<img src=https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/8600b9890a63425f91f73d8c5e0aa8ea>