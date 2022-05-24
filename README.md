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


---

## 인수 테스트 주도 개발 미션
### 기능 목록 도출

### 1단계 - 지하철역 인수 테스트
- [X] 지하철역 도메인 리팩토링
- [X] 지하철역 생성 인수 테스트 작성
  - 지하철 역 생성하기
    - When. 지하철역을 생성하면
    - Then. 지하철역이 생성된다.
  - 중복된 지하철역 생성하기
    - Given. 지하철역을 생성하고
    - When. 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
    - Then. 지하철역이 생성이 안된다.
- [X] 지하철역 목록 조회 인수 테스트 작성
  - 지하철역을 조회한다.
    - Given. 2개의 지하철역을 생성하고
    - When. 지하철역 목록을 조회하면
    - Then. 2개의 지하철역을 응답 받는다.
- [X] 지하철역 삭제 인수 테스트 작성
    - 지하철역을 제거한다.
        - Given. 지하철역을 생성하고
        - When. 그 지하철역을 삭제하면


### 2단계 - 지하철 노선 기능
**지하철 노선에 대한 인수 테스트 시나리오 작성**

- [X]  지하철 노선 생성 인수테스트 작성
  - When 지하철 노선을 생성하면
  - Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
- [X]  지하철 노선 목록 조회 인수테스트 작성
  - Given 2개의 지하철 노선을 생성하고
  - When 지하철 노선 목록을 조회하면
  - Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
- [X]  지하철 노선 조회 인수테스트 작성
  - Given 지하철 노선을 생성하고
  - When 생성한 지하철 노선을 조회하면
  - Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
- [X]  지하철 노선 수정 인수테스트 작성
  - Gvien 지하철 노선을 생성하고
  - When 생성한 지하철 노선을 수정하면
  - Then 해당 지하철 노선 정보는 수정된다.
- [X]  지하철 노선 삭제 인수테스트 작성
  - Given 지하철 노선을 생성하고
  - When 생성한 지하철 노선을 삭제하면
  - Then 해당 지하철 노선 정보는 삭제된다.


**자하철 노선 기능 구현**

- [ ]  지하철 노선 도메인 개발
- [ ]  지하철 노선 도메인 테스트 코드 작성
- [ ]  지하철 구간 도메인 개발
- [ ]  지하철 구간 도메인 테스트 코드 작성
- [ ]  지하철 노선과 지하철 구간의 연관관계 매핑
- [ ]  지하철 구간과 지하철 역의 연관관계 매핑

**지하철 노선 기능에 대한 인수 테스트 API 구현**

- [ ]  지하철 노선 생성 API
- [ ]  지하철 노선 전체 조회 API
- [ ]  지하철 노선 단일 조회 API
- [ ]  지하철 노선 수정 API
- [ ]  지하철 노선 삭제 API
 
- [ ]  인수테스트 격리시키기
- [ ]  인수테스트 리팩토링