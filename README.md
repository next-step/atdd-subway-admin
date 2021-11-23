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

## 🚩 기능 구현 목록
### 지하철 노선 관리
- [x] 인수테스트 먼저 작성
    - [x] `LineAcceptanceTest` 완성

- [x] 기능 구현
    - [x] 지하철 노선 생성 (POST /lines) - created
    - [x] 목록 조회 (GET /lines) - ok collection
    - [x] 조회 (GET /lines/{id}) - ok
    - [x] 수정 (PUT /lines/{id}) - ok
    - [x] 삭제 (DELETE /lines/{id}) - no content

- [x] 인수테스트 리팩토링
    - [x] 반복되는 코드 메서드로 분리
    - [x] 의도 드러내기 - 한글 메서드로 분류
    - [x] 다른 인수 테스트에서 재사용 가능한지 확인 후 추상화하기

### 인수테스트 리팩토링
- [x] 라인 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가
  - [x] 구간 형태로 관리되어야 한다
  - [x] 라인 Create 인수테스트 리팩토링
- [x] 라인 조회시 응답 결과에 역 목록 추가
  - [x] 상행 -> 하행 순으로 정렬
  - [x] 라인 getList 인수테스트 리팩토링
- [x] 기능 구현
  - [x] RequestDto 변경
  - [x] 구간 도메인 추가
  - [x] 라인 - 구간 - station 연관관계 설정
  
### 구간 추가 기능
- [x] 기능 구현전 인수테스트 작성
  - [x] `/lines/{id}/setions`
    ```json
    {
      "downStationId": "4",
      "upStationId": "2",
      "distance": 10
    }
    ```
  - [x] 상행역 앞에 역이 등록된 경우 - 상행역 앞에 새로운 역 추가, 새로운 역이 상행역
  - [x] 하행역 뒤에 역이 등록된 경우 - 하행역 뒤에 새로운 역 추가, 새로운 역이 하행역
  - [x] 역 사이에 새로운 역을 등록한 경우 - 길이 재계산 필요
- [x] 예외 케이스 처리 인수 테스트 작성
  - [x] 역 사이에 새로운 역이 등록된 경우 기존 역사이보다 길이가 작아야 함.
  - [x] 상행, 하행이 모두 등록된 경우 -> 저장안됨
  - [x] 상행역과 하행역이 모두 포함되어 었지 않는 경우 -> 저장안됨.
- [ ] 기능 구현
  - [ ] 예외 케이스 처리
  - [ ] 상행역 앞에 새로운 역 추가 기능
  - [ ] 하행역 뒤에 새로운 역 추가 기능
  - [ ] 역 사이에 새로운 역 추가 기능


## 📝 License

This project is [MIT](https://github.com/next-step/atdd-subway-admin/blob/master/LICENSE.md) licensed.
