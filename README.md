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

## 요구사항
- [ ] 지하철 노선 생성
- [ ] 지하철 노선 목록 조회
- [ ] 지하철 노선 조회
- [ ] 지하철 노선 수정
- [ ] 지하철 노선 삭제
- [ ] 인수 테스트 수정
- [ ] 요청, 응답 DTO 수정
- [ ] 상행선, 하행선 관리 구간 생성
- [ ] 노선 조회 시, 역 목록 응답
- [ ] 노선 조회 시, 역 목록 상행선, 하행선 순으로 정렬
- [ ] 지하철 구간을 등록한다
- [ ] 역 사이에 새로운 역을 등록한다
- [ ] 새로운 역을 상행 종점으로 등록한다
- [ ] 새로운 역을 하행 종점으로 등록한다
- [ ] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다
- [ ] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다
- [ ] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다
- [ ] 상행 종점역이 제거될 경우 다음 역이 상행 종점역이 된다
- [ ] 하행 종점역이 제거될 경우 이전 역이 하행 종점역이 된다
- [ ] 중간역이 제거될 경우 재배치한다
- [ ] 중간역이 제거될 경우 거리는 두 구간의 합이 된다
- [ ] 구간이 하나인 노선에서 상행역을 제거할 수 없다
- [ ] 구간이 하나인 노선에서 하행역을 제거할 수 없다

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
