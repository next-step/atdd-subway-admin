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



## 👨🏻‍💻 Todo list

- [x] [**사전 제공**] 지하철 역 관리
- [x] 코드 이해 및 Polishing
- [ ] [**1단계**] 지하철 노선 관리
  - [ ] 지하철 노선을 생성한다.
  - [ ] 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.
  - [ ] 지하철 노선 목록을 조회한다.
  - [ ] 지하철 노선을 조회한다.
  - [ ] 지하철 노선을 수정한다.
  - [ ] 지하철 노선을 제거한다.

- [ ] [**2단계**] 지하철 노선에 구간 등록
- [ ] [**3단계**] 노선에 등록된 역 목록 조회
- [ ] [**4단계**] 지하철 노선에 구간 제외



## [1단계] 지하철 노선 관리

**구현해야 될 부분**

![image.png](https://tva1.sinaimg.cn/large/008i3skNgy1gr55v4rh3sj30vb0u0wk1.jpg)



![image.png](https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/155885260e20466497cbf3f344cf7a5d)

## 지하철 역(station)

- 지하철 역 속성:
  - 이름(name)

## 지하철 구간(section)

- 지하철 (상행 방향)역과 (하행 방향)역 사이의 연결 정보
- 지하철 구간 속성:
  - 길이(distance)

## 지하철 노선(line)

- 지하철 구간의 모음으로 구간에 포함된 지하철 역의 연결 정보
- 지하철 노선 속성:
  - 노선 이름(name)
  - 노선 색(color)

