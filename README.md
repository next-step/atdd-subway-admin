### 1단계 미션 요구사항

- [ ] 기능 구현 전 인수 테스트 작성
- [ ] 지하철 노선 관리 기능을 구현하기
  - [ ] 생성 [SUB-1]
  - [ ] 목록 조회 [SUB-2]
  - [ ] 조회 [SUB-3]
  - [ ] 수정 [SUB-4]
  - [ ] 삭제 [SUB-5]
- [ ] 기능 구현 후 인수 테스트 리팩터링

## 사용자 스토리

---

### [EPIC] 지하철 노선 - Line

#### [SUB-1] 사용자는 노선의 이름과 색상을 입력해 노선을 **생성** 할 수 있다.

- 생성된 노선의 아이디, 이름, 색상값, 생성시간, 수정시간 을 반환 받을 수 있다.
- 노선의 이름 또는 색상값이 빈값일 경우 노선 생성이 실패하며 에러 메시지가 발생한다.
  <br/> 에러메시지 예시 : "노선의 이름을 입력해주세요."
- 노선의 이름이 이미 존재할 경우 노선 생성이 실패하여 에러 메시지가 발생한다.
  <br/> 에러메시지 예시 : "노선의 이름이 중복되었습니다."

#### [SUB-2] 사용자는 생성된 노선 리스트를 조회 할 수 있다.

- 노선 리스트의 항목은 노선 이름 과 색상값으로 이루어져 있다.

#### [SUB-3] 사용자는 해당 노선의 아이디를 통해서 노선 정보를 조회 할 수 있다.

- 노선 정보는 노선의 이름과 색상값으로 이루어져 있다.
- 해당 아이디의 노선이 존재하지 않을 경우 에러 메시지가 발생한다.
  <br/> 에러메시지 예시 : "해당 노선의 아이디가 존재하지 않습니다."

#### [SUB-4] 사용자는 노선의 이름과 색상값을 수정할 수 있다.

- 노선의 이름 또는 색상값이 빈값일 경우 노선 생성이 실패하며 에러 메시지가 발생한다.
  <br/> 에러메시지 예시 : "노선의 이름을 입력해주세요."
- 노선의 이름이 이미 존재할 경우 노선 생성이 실패하여 에러 메시지가 발생한다.
  <br/> 에러메시지 예시 : "노선의 이름이 중복되었습니다."
- 해당 아이디의 노선이 존재하지 않을 경우 에러 메시지가 발생한다.
  <br/> 에러메시지 예시 : "해당 노선의 아이디가 존재하지 않습니다."

#### [SUB-5] 사용자는 해당 노선의 아이디를 통해서 노선을 삭제 할 수 있다.

- 해당 아이디의 노선이 존재하지 않을 경우 에러 메시지가 발생한다.
  <br/> 에러메시지 예시 : "해당 노선의 아이디가 존재하지 않습니다."

---

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
