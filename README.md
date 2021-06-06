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

### step1 - 지하철 노선도 관리
-[x] 노선 생성 인수 테스트 작성
-[x] 노선 이름 중복 인수 테스트 작성 & 노선 생성 기능 구현
-[x] 노선 목록 조회 인수 테스트 작성
-[x] 노선 목록 조회 기능 구현
-[x] 노선 조회 인수 테스트 작성
-[x] 노선 조회 기능 구현
-[x] 노선 수정 인수 테스트 작성
-[x] 노선 수정 기능 구현
-[x] 노선 삭제 인수 테스트 작성
-[x] 노선 삭제 기능 구현
-[x] 인수 테스트 리팩터링

### step2 - 인수 테스트 리팩터링
-[x] 노선 생성 시 종점역 정보를 요청 파라미터에 추가
    -[x] 테스트 코드 변경
    -[x] 프로덕션 코드 변경
      -[x] Line 저장 시 종점역 정보가 없다면 저장할 수 없다.
      -[x] Line 저장 시 Section 을 함께 저장해야 한다. 
      -[x] Section 은 자신의 Line 과 상행방향역 하행방향역 정보를 갖는다.
-[x] 노선 조회 시 응답 결과에 역 목록 추가하기
    -[x] 테스트 코드 변경
    -[x] 프로덕션 코드 변경
    -[ ] 성능 최적화
