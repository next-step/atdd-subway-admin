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

## 인수 테스트 실습
### Todo-list
- [X] 지하철 노선 생성 기능의 인수 테스트 작성하기
    - [X] 지하철 노선 생성 시도 인수 테스트 작성
    - [X] 지하철 노선 생성 확인 인수 테스트 작성
- [X] 지하철 노선 생성 기능 구현하기
- [X] 지하철 목록 조회 기능
  - [X] 지하철 목록 조회 인수 테스트 작성
  - [X] 서비스 레이어 구현(w. 유닛 테스트)
  - [X] 컨트롤러 구현
- [X] 지하철 조회 기능
  - [X] 지하철 조회 인수 테스트 작성
  - [X] 서비스 레이어 구현(w. 유닛 테스트)
  - [X] 컨트롤러 구현
- [X] 지하철 수정 기능
  - [X] 지하철 수정 인수 테스트 작성
  - [X] 서비스 레이어 구현(w. 유닛 테스트)
  - [X] 컨트롤러 구현
- [X] 지하철 삭제 기능
  - [X] 지하철 삭제 인수 테스트 작성
  - [X] 서비스 레이어 구현(w. 유닛 테스트)
  - [X] 컨트롤러 구현
  
## Step1 피드백 반영
- [ ] update 기능에서도 도메인을 직접 노출하지 않도록 개선
- [ ] 반복되는 인수테스트 과정을 추출한 static 메서드명들을 한글로 바꾸기
  - 이상한 스네이크 케이스로 인한 혼돈 방지 + 불필요한 과거 시제 뉘앙스를 살리기 위한 반복 제거
- [ ] PUT API 검증 때 좀 더 안전하게 검증하기 위해 값 변화까지 검증하기
