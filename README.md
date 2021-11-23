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

# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

<br>

## 🚀 Getting Started

### Install
#### npm 설치
```
cd frontend
npm install
```
> `frontend` 디렉토리에서 수행해야 합니다.

### Usage
#### webpack server 구동
```
npm run dev
```
#### application 구동
```
./gradlew bootRun
```
<br>

## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/next-step/atdd-subway-admin/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/next-step/atdd-subway-admin/blob/master/LICENSE.md) licensed.

---

### [1단계] 지하철 노선 관리

- [X] 지하철 노선 관련 기능의 인수 테스트 작성
- 기능 목록 / 구현
  - [X] 생성
  - [X] 목록 조회
  - [X] 조회
  - [X] 수정
  - [X] 삭제
- [X] 기능 구현 후 인수 테스트 리팩터링
  - [X] 중복 제거
  - [X] 스텝 메서드 만들기
  - [X] 스텝 메서드 한글명으로 바꾸기
  - [X] 스텝 메서드들을 다른 인수 테스트에서 재사용 가능하게 함

---

### [2단계] 인수 테스트 리팩터링

- [X] 인수 테스트 수정
- [X] DTO 수정
- [X] 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기
  ```
  public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;       // 추가
    private Long downStationId;     // 추가
    private int distance;           // 추가
    ...
  }
  ```
- 두 종점역은 구간의 형태로 관리되어야 함
- [X] 노선 객체에서 구간 정보 관리(양방향 연관관계)
  ```
    public class Line {
    ...
      private List<Section> sections;
    ...
    }
  ```

- [X] 노선 조회 시 응답 결과에 역 목록 추가하기
  - [X] 노선 조회 시 역 목록을 함께 응답함
  - [X] 상행역 부터 하행역 순으로 정렬되어야 함

---

### [3단계] 구간 추가 기능

지하철 구간 등록 기능 구현하기

- 구간 등록 API Request

```
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
- [X] 기능 구현 전 구간 등록 인수 테스트 작성
- [X] 예외 케이스 처리 인수 테스트 작성
  - [X] 하행역이 두 역 사이에 등록 될 경우
  - [X] 상행역이 두 역 사이에 등록 될 경우
  - [X] 상행 종점이 새로 등록될 경우
  - [X] 하행 종점이 새로 등록될 경우
  - [X] 역 사이에 새로운 역을 등록할 때 기존 역 사이 길이보다 작아야 한다.
  - [X] 상행역과 하행역 모두가 구간에 등록되어 있으면 추가할 수 없다.
  - [X] 상행역과 하행역 둘 중 하나가 구간에 포함되어있지 않으면 추가 할 수 없다.
  - [X] 존재하지 않는 노선에 구간을 추가할 수 없다.
- [X] 기능 구현
- [X] 테스트 리펙터링
- [X] 기능 리펙터링

---

### [4단계] 구간 제거 기능

지하철 구간 제거 기능 구현하기(구간 제거보다는 역 제거가 맞는말 같음)

- 구간 삭제 Request

```
  DELETE /lines/1/sections?stationId=2 HTTP/1.1
  accept: */*
  host: localhost:52165
```

- [X] 구간 삭제 인수 테스트 작성
- [X] 예외 케이스 처리 인수 테스트 작성
  - [X] 하행 종점이 제거될 경우 종점의 상행역이 종점이 된다.
  - [X] 상행 종점(맨 처음역)이 제거되는 경우에도 다음으로 오는 역이 종점이 된다.
  - [X] 중간역이 제거될 경우 재배치함
    - [X] 삭제되는 역의 상행역과 하행역으로 재배치 된다.
    - [X] 거리는 두 구간의 합으로 정한다.
  - [X] 실패 인수 테스트 작성
- [ ] 기능 구현
- [ ] 기능 리펙터링
- [ ] 테스트 리펙터링

#### 삭제 경우의 수

- 상행 종점이 경우
- 하행 종점인 경우
- 중간역인 경우

#### 예외 발생 케이스

- [ ] 노선에 등록되지 않은 역은 제거할 수 없다.
- [ ] 구간이 하나인 노선은 제거할 수 없다.
- [ ] 존재하지 않는 지하철 역으로 제거할 수 없다.
- [ ] 존재하지 않는 노선에 구간을 제거할 수 없다.