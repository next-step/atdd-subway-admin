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

## Step 1 

1. 지하철 노선 생성 인수테스트 구현하기
2. 지하철 노선 생성 단위테스트 구현 및 기능 구현하기
3. 지하철 노선 조회 인수테스트 구현하기
4. 지하철 노선 조회 단위테스트 구현 및 기능 구현하기
5. 지하철 노선 수정 인수테스트 구현하기
6. 지하철 노선 수정 단위테스트 구현 및 기능 구현하기
7. 지하철 노선 삭제 인수테스트 구현하기
8. 지하철 노선 삭제 단위테스트 구현 및 기능 구현하기
9. 코드 리팩토링 

## Step 2

1. 노선 생성 시 종점역 추가하기
    - LineRequest 전달 받을 필드 추가
    - LineRequest 필드 추가에 따른 Line 인수 테스트 수정
    - LineRequest 필드 추가에 따른 Line 단위 테스트 수정
    - Station정보를 가져오는 서비스 메서드 작성
    - Section 구간 정보 추가 
    - Line과 연관 관계 설정 및 필드 추가

2. 노선 조회 응답 결과에 등록된 구간을 참고하여 역 목록 응답 추가
    - LineResponse 역목록을 포함하도록 수정
    - 역목록 응답을 검증하는 인수 테스트 수정
    - 역목록 응답을 검증하는 단위 테스트 수정
    - LineResponse에 역목록을 포함하도록 기능 추가
