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

## 구현 기능 목록

1. 지하철 노선 관련 기능 구현
    - 생성
      - 노선 생성시 종점역(상행,하행)정보 함께 추가하기
    - 목록 조회
    - 조회
      - 노선 조회 시 응답 결과에 역 목록 추가 (상행역 부터 하행역 순으로 정렬)
    - 수정 
    - 삭제
2. 지하철 노선에 구간 추가 기능 구현
   1) 노선 추가하기
   2) 노선 추가시 예외 검증
      - 상/하행선이 동일한 구간 추가 불가
      - 상/하행선 둘중 하나와도 일치하지 않는 구간 추가 불가
      - 중간에 구간 추가시 기존 구간보다 긴 거리값 가지고 있을 경우 추가 불가
   3) 노선에 등록된 지하철역 정렬 함수 구현
3. 지하철 노선의 구간 제거 기능 구현
   1) 종점 제거
   2) 중간역 제거
   3) 구간 제거시 예외 검증
      - 노선에 등록되지 않은 역 제거 불가
      - 구간이 1개일때 역 제거 불가