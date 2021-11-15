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

# 인수테스트 주도 개발(지하철 노선도 미션)
## 1단계-지하철 노선관리
### 요구사항
- [x] 지하철 노선 관련 인수테스트 작성-`LineAcceptanceTest`를 완성
  - [x] 지하철 노선을 생성한다.
  - [x] 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.
  - [x] 지하철 노선 목록을 조회한다.
  - [x] 지하철 노선을 조회한다.
  - [x] 지하철 노선을 수정한다.
  - [x] 지하철 노선을 제거한다.
- [x] 지하철 노선 관련 기능 구현-`LineController`를 통해 요청 및 처리하는 기능을 구현
  - [x] 노선 목록을 조회한다.
  - [x] 노선을 조회한다.
  - [x] 노선을 수정한다.
  - [x] 노선을 제거한다.
- [x] 인수 테스트 리팩터링
  - [x] 인수 테스트의 각 스템을 메서드로 분리하여 재사용

### 지하철 노선 생성 전문관련 내용
#### 노선 생성
<details><summary>Request</summary>

```python
POST /lines HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8

{
    "color": "bg-red-600",
    "name": "신분당선"
}
```
</details>

<details><summary>Response</summary>

```python
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
</details>

#### 노선 목록 조회
<details><summary>Request</summary>

```python
GET /lines HTTP/1.1
accept: application/json
host: localhost:49468
```
</details>

<details><summary>Response</summary>

```python
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
</details>

#### 노선 조회
<details><summary>Request</summary>

```python
GET /lines/1 HTTP/1.1
accept: application/json
host: localhost:49468
```
</details>

<details><summary>Response</summary>

```python
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
</details>

#### 노선 수정
<details><summary>Request</summary>

```python
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
</details>

<details><summary>Response</summary>

```python
HTTP/1.1 200
Date: Fri, 13 Nov 2020 00:11:51 GMT
```
</details>


#### 노선 삭제
<details><summary>Request</summary>

```python
DELETE /lines/1 HTTP/1.1
accept: */*
host: localhost:49468
```
</details>

<details><summary>Response</summary>

```python
HTTP/1.1 204
Date: Fri, 13 Nov 2020 00:11:51 GMT
```
</details>

## 2단계-인수 테스트 리팩터링
### 요구사항
- [ ] 노선 생성 시 종점역(상행, 하행)정보를 요청 파라미터에 함께 추가
  - [ ] 두 종점역은 `구간`의 형태로 관리
- [ ] 노선 조회 시 응답 결과에 역 목록 추가
  - [ ] `상행역부터 하행역 순으로 정렬`되어야 함

### 구현 목록
- [x] 구간은 상/하행 지하철역, 구간길이로 구성된다.
- [x] 노선은 구간 정보들을 가지게 된다.
- [x] 구간정보는 구간서비스에 의해 DB에 저장된다.
- [ ] 노선생성 전문은 구간 정보도 같이 추가된다.
- [ ] 노선 조회시 역 목록은 상행역부터 하행역 순으로 정렬된다.


###  인수테스트 리팩터링관련 전문 내용
#### 노선 생성
<details><summary>Request</summary>

```python
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
</details>

#### 노선 조회
<details><summary>Response</summary>

```python
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
</details>