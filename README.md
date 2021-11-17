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
### atdd-subway-web 도커 컨테이너로 실행하기

#### 1. Dockerfile 복사해서 워크스페이스 경로에 넣기

#### 2. 워크스페이스 경로로 이동
```cd ${workspace}```

#### 3. 프록시 설정 변경(로컬 아이피 넣기)
 - 127.0.0.1 => 로컬 아이피주소
![image info](./image.png) 

#### 3.도커파일 빌드
 - frontend 내 node_modules 디렉터리가 있으면 정상적으로 빌드되지 않음. 삭제 후 빌드
 - ``` docker build -t atdd-web:0.0 .```

#### 4.도커 컨테이너 실행
 - ```docker run -it --name atdd_web -p 8081:8081 atdd-web:0.0```
---


## 1단계(step1) - 지하철 노선 관리

#### 요구사항 1
- [x] : 기능 목록: 생성/목록 조회/조회/수정/삭제
- [x] : 기능 구현 전 인수 테스트 작성 
- [ ] : 기능 구현 후 인수 테스트 리팩터링

#### 요구사항 설명 
- ##### 지하철 노선 관련 기능의 인수 테스트를 작성하기
  - ```LinkAcceptanceTest```를 완성시키세요.
- ##### 지하철 노선 관련 기능 구현하기
  - 인수 테스트가 모두 성공할 수 있도록 ```LineController```를 통해 요청을 받고 처리하는 기능을 구현하세요.
- ##### 인수 테스트 리팩터링
  - 인수 테스트의 각 스텝들을 메서드로 분리하여 재사용하세요.
    - ex) 인수 테스트 요청 로직 중복 제거 등 
  
#### 힌트 
- ##### RestAssured  [link](https://github.com/rest-assured/rest-assured/wiki/Usage#examples)
  - given 
    - 요청을 위한 값을 설정(header, content Type 등)
    - body가 있는 경우 body 값을 설정함
  - when 
    - 요청의 url과 method를 설정
  - then
    - 응답의 결과를 관리
    - response를 추출하거나 response값을 관리할 수 있음 