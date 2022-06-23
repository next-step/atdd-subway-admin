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
<details>
<summary>1단계</summary>

#### 지하철역 인수 테스트 작성
- [x] 지하철역 목록 조회 인수 테스트
    - 지하철역 등록
    - 지하철역 목록 조회
    - 등록한 지하철역이 있는지 확인
- [x] 지하철역 삭제 인수 테스트
    - 등록된 지하철역 삭제
    - 목록 조회하여 확인
  
</details>

<details>
<summary>2단계</summary>

#### 지하철 노선 기능
- [x] 지하철 노선 생성 인수 테스트
  - 지하철 노선 생성
  - 지하철 노선 목록 조회
  - 생성한 노선이 있는지 확인
- [x] 지하철 노선 목록 조회
  - 2개의 지하철 노선 생성
  - 노선 목록 조회
- [x] 지하철 노선 수정
  - 생성한 지하철 노선 수정
  - 노선 조회
  - 수정 확인
- [x] 지하철 노선 삭제
  - 지하철 노선 생성
  - 노선 삭제
  - 노선 조회 시 삭제되었는지 확인

</details>

<details>
<summary>3단계</summary>

#### 지하철 구간 등록 인수 테스트
- [X] 역 사이에 새로운 역을 등록할 경우
  - 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
- [X] 새로운 역을 상행 종점으로 등록할 경우
  - 지하철 구간 등록
  - 노선의 상행역 변경
- [X] 새로운 역을 하행 종점으로 등록할 경우
  - 지하철 구간 등록
  - 노선의 하행역 변경

#### 예외 케이스
- 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
- 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
- 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

</details>

<details>
<summary>4단계</summary>

#### 구간 제거 기능
- [x] 종점이 제거될 경우 다음으로 오던 역이 종점으로 변경
- [x] 중간역이 제거될 경우 재배치
  - `A-B-C` 에서 B역을 제거하는 경우, `A-C`로 재배치
</details>

#### 예외 케이스
- 노선에 등록되지 않은 역 제거
- 구간이 하나인 노선일 시 구간 제거 불가능 

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
