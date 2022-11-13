<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <img alt="npm" src="https://img.shields.io/badge/npm-6.14.15-blue">
  <img alt="node" src="https://img.shields.io/badge/node-14.18.2-blue">
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

## 1단계 - 지하철 노선도 관리
> 구현된 기능을 대상으로 인수 테스트를 작성하는 단계  
> `RestAssured`를 활용하여 인수 테스틀르 만들어 볼 것

### 요구사항
- 지하철역 관련 인수테스트를 완성
  - [x] 지하철역 목록 조회 인수 테스트 작성하기
  - [x] 지하철역 삭제 인수 테스트 작성하기

### 프로그래밍 요구사항
- [x] 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 할 것
> 각각의 테스트를 동작시키면 잘 동작하지만, 한번에 동작시키면 실패할 수 있음(테스트간 격리가 되지 않았기 때문에 데이터베이스에 데이터가 축적되기 때문)  
> 이번 단계에서는 고려할 사항이 아니라고 하였으나, 강의 내용을 기반으로 테스트간 격리방법 중 `DatabaseCleanup` utils을 작성하고 활용해보자.

### 1단계 피드백
- Q) given의 중복을 피할 수 있는 방법이 있는 지 문의
- A) 테스트 코드도 서비스 코드의 작성과 동일함. `지하철 생성` 역할의 메서드를 분리하여 공통적으로 사용