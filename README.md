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

## 1단계 기능 목록

- [x] 기능 구현 전 인수 테스트 작성
- [x] 노선 생성 구현
- [x] 노선 목록 조회 구현
- [x] 노선 단건 조회 구현
- [x] 노선 수정 구현
- [x] 노선 삭제 구현
- [x] 인수 테스트 리팩토링
- [x] line-station 다대일 연관관계 매핑
- [x] List<LineResponse> 일급 컬렉션 리팩토링
- [x] List<Station> 일급 컬렉션 리팩토링

## 2단계 기능 목록

- [x] 노선 생성시 구간 지정하는 인수 테스트 추가
- [x] 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기
    - 구간(Section)의 형태로 관리되어야 함
- [x] 노선 조회 시 응답 결과에 역 목록 추가하는 인수 테스트 추가
- [x] 노선 조회 시 응답 결과에 역 목록 추가하기
    - 상행역 부터 하행역 순으로 정렬되어야 함

## 3단계 기능 목록

- [x] LineService.saveLine 중복 제거
- [x] Line.update시 section을 제외한 나머지 파라미터로 업데이트
- [x] LineAcceptanceTest에 한글 변수명을 사용하여 가독성 확보
- [x] 구간 추가 기능 Happy Path 인수 테스트 작성
- [x] 구간 추가 기능 Happy Path 구현
- [x] 구간 추가 기능 예외 케이스 처리 인수 테스트 작성
- [x] 구간 추가 기능 예외 케이스 구현

## 4단계 기능 목록

- [x] LineController.addSection 반환형 제네릭 Void로 변경
- [x] 도메인이 dto에 의존하지 않도록 Station.from 제거
- 노선의 구간을 제거하는 기능(Happy Path) 구현하기
    - 역 3개로 이루어진 구간 2개 제공

    1. 상행 끝부분을 제거
    2. 중간 부분을 제거
    3. 하행 끝 부분을 제거

    - [x] 인수 테스트 추가
    - [x] 유닛 테스트 추가 및 기능 구현
- 구간 삭제 시 예외 케이스를 고려하기
    - [ ] 유닛 테스트 추가
        - 구간이 하나인 노선에서 구간을 제거할 경우
        - 노선에 존재하지 않는 구간을 삭제하려 하는 경우