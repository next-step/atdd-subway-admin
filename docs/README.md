## 1단계 - 지하철역 인수 테스트 작성
## 기능 요구 사항

* 지하철역 목록 조회 인수 테스트 작성하기
  * 지하철역을 조회한다.
* 지하철역 삭제 인수 테스트 작성하기
  * 지하철역을 제거한다.
  
## 2단계 - 지하철 노선 기능
## 기능 요구 사항

* 지하철 노선을 생성한다.
  * Line 엔티티 생성
  * Section 엔티티 생성
* 지하철 노선 목록을 조회한다.
* 지하철 노선 단건 조회한다.
* 지하철 노선을 수정한다.
* 지하철 노선을 삭제한다.


## 3단계 - 구간 추가 기능
## 기능 요구 사항
* 노선에 구간을 등록한다.
* 역 사이에 새로운 구간을 등록한다.
* 새로운 역을 상행 종점으로 등록한다.
* 새로운 역을 하행 종점으로 등록한다.
* 기존 역 사이 길이보다 크거나 같은 구간은 등록할 수 없다.
* 상행역과 하행역이 이미 노선에 등록되어 있으면 구간을 등록할 수 없다.
* 상행역과 하행역 둘 다 노선에 포함되어 있지 않으면 구간을 등록할 수 없다.

## 구간 추가 api 명세
* method : post
* url : /lines/{lineId}/sections
* request body 
```
{
    "upStationId": 1,
    "downStationId": 3,
    "distance": 7
}
```
* response body
```
{
    "lineId": 1,
    "sections": [
        {
            "id": 1,
            "upStation": {
                "id": 1,
                "name": "양재역"
            },
            "downStation": {
                "id": 3,
                "name": "판교역"
            },
            "distance": 7
        },
        {
            "id": 2,
            "upStation": {
                "id": 3,
                "name": "판교역"
            },
            "downStation": {
                "id": 2,
                "name": "정자역"
            },
            "distance": 3
        }
    ]
}
```
* response header
  * Location : /lines/{lineId}/sections/{sectionId}
* status code
  * 201 : created (성공)
  * 400 : bad request (실패)
                  