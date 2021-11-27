### 4단계 미션 요구사항 - 구간 제거 기능

- [SUB-7] 사용자는 해당 노선의 역을 삭제 할 수 있다.


## 사용자 스토리

---

### [EPIC] 지하철 노선 - Line

#### [SUB-1] 사용자는 노선의 이름과 색상, 종점역(상행역의 아이디, 하행역의 아이디, 역간의 거리) 정보를 입력해 노선을 **생성** 할 수 있다.

- 생성된 노선의 아이디를 반환 받을 수 있다.
- 노선의 이름 또는 색상값이 빈값일 경우 노선 생성이 실패하며 에러 메시지가 발생한다.
  <br/> 에러메시지 예시 : "노선의 이름이 빈값일 수 없습니다.", "노선의 색상값이 빈값일 수 없습니다."
- 노선의 이름이 이미 존재할 경우 노선 생성이 실패하여 에러 메시지가 발생한다.
  <br/> 에러메시지 예시 : "노선의 이름이 중복되었습니다."
- 상행역의 아이디, 하행역의 아이디를 통해 역 정보를 발견하지 못했을 경우, 에러 메시지가 발생한다.
  <br/> 에러메시지 예시 : "상행역의 정보를 찾지 못하였습니다."
- 역간의 거리가 0보다 작을 경우, 에러메시지가 발생한다.
  <br/> 에러메시지 예시 : "역간의 거리는 0보다 커야 합니다."

#### [SUB-2] 사용자는 생성된 노선 리스트를 조회 할 수 있다.

- 노선 리스트의 항목은 노선 이름, 색상값, 역 목록으로 이루어져 있다.
  - 역목록은 상행역 부터 하행역 순으로 정렬 되어야 한다.
  - 역목록의 구성은 아이디, 이름, 생성시간, 수정시간 으로 구성되어 있다.
  
#### [SUB-3] 사용자는 해당 노선의 아이디를 통해서 노선 정보를 조회 할 수 있다.

- 노선 정보는 노선의 이름과 색상값으로 이루어져 있다.
- 해당 아이디의 노선이 존재하지 않을 경우 에러 메시지가 발생한다.
  <br/> 에러메시지 예시 : "해당 노선의 아이디가 존재하지 않습니다."

#### [SUB-4] 사용자는 노선의 이름과 색상값을 수정할 수 있다.

- 정상적으로 수정된 경우 노선의 업데이트 시간을 반환 받을 수 있다.
- 노선의 이름 또는 색상값이 빈값일 경우 노선 수정이 실패하며 에러 메시지가 발생한다.
  <br/> 에러메시지 예시 : "노선의 이름이 빈값일 수 없습니다.", "노선의 색상값이 빈값일 수 없습니다."
- 노선의 이름이 이미 존재할 경우 노선 수정이 실패하여 에러 메시지가 발생한다.
  <br/> 에러메시지 예시 : "노선의 이름이 중복되었습니다."
- 해당 아이디의 노선이 존재하지 않을 경우 에러 메시지가 발생한다.
  <br/> 에러메시지 예시 : "해당 노선의 아이디가 존재하지 않습니다."

#### [SUB-5] 사용자는 해당 노선의 아이디를 통해서 노선을 삭제 할 수 있다.

- 해당 아이디의 노선이 존재하지 않을 경우 에러 메시지가 발생한다.
  <br/> 에러메시지 예시 : "해당 노선의 아이디가 존재하지 않습니다."

#### [SUB-6] 사용자는 해당 노선의 역을 추가할 수 있다.

- 역 사이의 추가될 경우 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정된다.
- 역 사이의 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록 할 수 없다.
- 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.
- 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.

#### [SUB-7] 사용자는 해당 노선의 역을 삭제 할 수 있다.

- 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
- 중간역이 제거될 경우 재배치를 함 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 됨 거리는 두 구간의 거리의 합으로 정함
- 노선에 등록되어있지 않은 역을 제거하려는 경우 에러 메시지가 발생한다.
  <br/> 에러메시지 예시 : "해당 노선의 아이디가 존재하지 않습니다."
- 구간이 하나인 노선에서 마지막 구간을 제거할경우 에러 메시지가 발생한다.
  <br/> 에러메시지 예시 : "마지막 구간의 역은 삭제 할 수 없습니다."

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
