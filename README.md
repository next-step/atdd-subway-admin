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
- [] 지하철 노선 관련 인수테스트 작성-`LineAcceptanceTest`를 완성
  - [] 지하철 노선을 생성한다.
  - [] 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.
  - [] 지하철 노선 목록을 조회한다.
  - [] 지하철 노선을 조회한다.
  - [] 지하철 노선을 수정한다.
  - [] 지하철 노선을 제거한다.
- [] 지하철 노선 관련 기능 구현-`LineController`를 통해 요청 및 처리하는 기능을 구현
  - [] 노선 목록을 조회한다.
  - [] 노선을 조회한다.
  - [] 노선을 수정한다.
  - [] 노선을 제거한다.
- [] 인수 테스트 리팩터링
  - [] 인수 테스트의 각 스템을 메서드로 분리하여 재사용

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
