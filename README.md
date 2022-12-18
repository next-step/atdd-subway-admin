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

## 🚀 1단계 - 지하철역 인수 테스트 작성

### Feat Requirements

- [x] 지하철역 관련 인수 테스트를 완성하세요.
  - [x] 지하철역 목록 조회 인수 테스트 작성하기
  - [x] 지하철역 삭제 인수 테스트 작성하기

### 프로그래밍 요구사항

- [x] Refactoring AT for 재사용성과 가독성

### Hint

JsonPath : 
- Json 문서를 읽어오는 DSL
- JsonPath를 사용하면 Response Dto 객체로 받을 필요 없이 필요한 값만 추출하여 검증에 사용할 수 있음

### 시나리오

#### 지하철역 목록 조회

[x] 정상 시나리오
  - 지하철역이 존재한다.
  - 지하철역 목록을 조회한다.
  - 지하철역을 응답한다.
[Skip] 비정상 - 지하철역 목록 존재하지 않을 경우
[Skip] 비정상 - 지하철역 목록 GET 실패

#### 지하철역 삭제

[x] 정상 시나리오
  - 지하철역이 존재한다.
  - 지하철역 삭제를 요청한다.
  - 지하철역이 삭제된다.
[Skip] 비정상 - 유효하지 않은 지하철 역 ID
[Skip] 비정상 - 존재하지 않는 지하철 역 ID
[Skip] 비정상 - 지하철 역 ID 삭제 실패


## 🚀 2단계 - 지하철 노선 기능 구현

- 제시된 인수 조건을 기반으로 기능 구현을 하는 단계입니다.
- 기능 구현 전에 인수 조건을 만족하는지 검증하는 인수 테스트를 먼저 만들고 기능구현을 해보세요.

### Feat Requirement

- 요구사항 설명에서 제공되는 인수 조건을 기반으로 지하철 노선 관리 기능을 구현

- [ ] 지하철 노선 생성 Create

  > When 지하철 노선을 생성하면 Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다

- [ ] 지하철 노선 목록 조회 GetAll

  > Given 2개의 지하철 노선을 생성하고 When 지하철 노선 목록을 조회하면 Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다

- [ ] 지하철 노선 조회 Get

  > Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 조회하면 Then 생성한 지하철 노선의 정보를 응답받을 수 있다.

- [ ] 지하철 노선 수정 Update

  > Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 수정하면 Then 해당 지하철 노선 정보는 수정된다

- [ ] 지하철 노선 삭제 Delete

  > 지하철 노선을 생성하고 When 생성한 지하철 노선을 삭제하면 Then 해당 지하철 노선 정보는 삭제된다

### Programming Requirement

- 아래의 순서로 기능을 구현하세요.
- [ ] 1. 인수 테스트 작성
- [ ] 2. 기능 구현
- [ ] 3. 인수 테스트를 서로 격리 
- [ ] 4. 인수 테스트를 리팩터링 

### API Spec

노선 생성 시 상행종점역과 하행종점역을 등록합니다. 
따라서 이번 단계에서는 지하철 노선에 역을 맵핑하는 기능은 아직 없지만 노선 조회시 포함된 역 목록이 함께 응답됩니다.

### Hint

#### 인수 테스트 격리 

@DirtiesContext
- Spring Context를 이용한 테스트 동작 시 스프링 빈의 상태가 변경되면 해당 컨텍스트의 재사용이 불가하여 컨택스트를 다시 로드해야함
- 스프링빈의 상태가 변경되었다는 설정을 하는 애너테이션
- 테스트 DB는 메모리 디비로 컨테이너에 띄워져있는 상태이므로 컨텍스트가 다시 로드되면 기존 DB의 내용이 초기화됨

@Sql
- 테스트 수행 시 특정 쿼리를 동작시키는 애너테이션

Table Truncate
- 테이블을 조회하여 각 테이블을 Truncate시켜주는 방법

#### 인수 테스트 리팩터링

중복 코드 처리
지하철역과 노선 인수 테스트를 작성하면서 중복되는 부분이 발생하는데 이 부분을 리팩터링 하면 부가적인 코드는 테스트로부터 분리되어 테스트에 조금 더 집중할 수 있게됨

### TODO

[x] Apply previous feedback
> 다음 스텝에서 StationAcceptanceTestHelper.createStation 메서드가 ExtractableResponse자체를 반환하도록하여서, HttpStatusCode체크는 테스트메서드안에서 검증하는 걸로 바꾸도록 하겠습니다.

[x] 1. 인수 테스트 작성(LineAcceptanceTest)

- [x] 지하철 노선 생성 Create

  > When 지하철 노선을 생성하면 Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다

- [x] 지하철 노선 목록 조회 GetAll

  > Given 2개의 지하철 노선을 생성하고 When 지하철 노선 목록을 조회하면 Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다

- [x] 지하철 노선 수정 Update

  > Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 수정하면 Then 해당 지하철 노선 정보는 수정된다

- [x] 지하철 노선 삭제 Delete

  > 지하철 노선을 생성하고 When 생성한 지하철 노선을 삭제하면 Then 해당 지하철 노선 정보는 삭제된다

[x] 2. 기능 구현
- [x] Create Section Domain Model
- [x] Create Line Domain Model
- [x] Create Line Service
- [x] Create Line DTO - Request
- [x] Create Line DTO - Response
- [x] Create Line Controller

[x] 3. 인수 테스트를 서로 격리 

[x] 4. 인수 테스트를 리팩터링 


## 🚀 3단계 - 구간 추가 기능

- 제시된 인수 조건을 기반으로 기능 구현을 하는 단계입니다.
- 기능 구현 전에 인수 조건을 만족하는지 검증하는 인수 테스트를 먼저 만들고 기능구현을 해보세요.

### 기능 요구사항

- 요구사항 설명에서 제공되는 요구사항을 기반으로 지하철 구간 추가 기능을 구현하세요.
- 요구사항을 정의한 인수 조건을 조출하세요.
- 인수 조건을 검증하는 인수 테스트를 작성하세요.
- 예외 케이스에 대한 검증도 포함하세요.

### 기능 목록

- 역 사이에 새로운 역을 등록할 경우
- 새로운 역을 상행 종점으로 등록할 경우
- 새로운 역을 하행 종점으로 등록할 경우
- 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
- 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
- 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

### 프로그래밍 요구사항
- 인수 테스트 주도 개발 프로세스에 맞춰서 기능을 구현하세요.
- 요구사항 설명을 참고하여 인수 조건을 정의
- 인수 조건을 검증하는 인수 테스트 작성
- 인수 테스트를 충족하는 기능 구현
- 인수 조건은 인수 테스트 메서드 상단에 주석으로 작성하세요.
- 뼈대 코드의 인수 테스트를 참고
- 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트를 서로 격리 시키세요.
- 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요.


### TODO

[x] Apply previous feedback

- [x] Controller Layer : Class Lv Trasnaction(readOnly=true) -> Trasnaction(readOnly=false) 
- [x] AcceptanceTest Layer : Befor each test method, DB 데이터만 롤백 하도록 수정

[x] 1. 인수 테스트 작성

- [x] 지하철 노선 생성 Create

  > When 지하철 노선을 생성하면 Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다

- [x] 지하철 노선 목록 조회 GetAll

  > Given 2개의 지하철 노선을 생성하고 When 지하철 노선 목록을 조회하면 Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다

- [x] 지하철 노선 수정 Update

  > Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 수정하면 Then 해당 지하철 노선 정보는 수정된다

- [x] 지하철 노선 삭제 Delete

  > 지하철 노선을 생성하고 When 생성한 지하철 노선을 삭제하면 Then 해당 지하철 노선 정보는 삭제된다

[x] 2. 지하철 구간 추가 기능 구현

- [x] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음

- [x] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음

- [x] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

- [x] 역 사이에 새로운 역을 등록할 경우

- [x] 새로운 역을 상행 종점으로 등록할 경우

- [x] 새로운 역을 하행 종점으로 등록할 경우

[x] 3. 인수 테스트를 서로 격리 

[x] 4. 인수 테스트를 리팩터링 