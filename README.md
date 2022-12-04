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

## Step1 - 지하철역 인수 테스트 작성

#### 요구사항 1
 - 지하철역 목록 조회 인수 테스트 작성하기
#### 요구사항 2
 - 지하철역 삭제 인수 테스트 작성하기
#### 구현 리스트
- [x] 지하철역 목록 조회 인수 테스트
  - [x] given - ( sation name { "지하철역이름", "새로운지하철역이름", "또다른지하철역이름" } )  
  - [x] when - ( method : get )
  - [x] then - ( statusCode : HttpStatus.OK(200) )

- [x] 지하철역 삭제 인수 테스트
  - [x] given - ( sation name { "강남역" } )
  - [x] when - ( method : delete )
  - [x] then - ( statusCode : HttpStatus.NO_CONTENT(204) )
#### Step1 리뷰 사항 반영
- [x] Step2 1차 리뷰 사항 반영
    - [x] 테스트 가독성 향상 - 지하철역 생성, 조회 별도의 메소드로 처리

## Step2 - 지하철 노선 기능
#### 기능 요구사항
 - 요구사항 설명에서 제공되는 인수 조건을 기반으로 지하철 노선 관리 기능을 구현하세요.
 - 인수 조건을 검증하는 인수 테스트를 작성하세요.
#### 프로그래밍 요구사항
 - 인수 조건을 검증하는 인수 테스트 작성
 - 인수 테스트를 충족하는 기능 구현
 - 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트를 서로 격리 시키세요.
 - 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요 
#### 요구사항 기능 목록
 - 지하철 노선 생성
 - 지하철 노선 목록 조회
 - 지하철 노선 조회
 - 지하철 노선 수정
 - 지하철 노선 삭제
#### 구현 리스트
- [x] 지하철 노선 초기 작업
  - [x] Entity 생성 ( name, color, start time, end time, interval time )
  - [x] Repository 생성
  - [x] Dto 생성
  - [x] Controller 생성( 생성, 조회, 수정, 삭제 )
- [x] 지하철 노선 생성
  - [x] given - ( name, color, start time, end time, interval time )
  - [x] when  - ( method : post )
  - [x] then  - ( statusCode : HttpStatus.CREATED(201) )
- [x] 지하철 노선 목록 조회
  - [x] given - (  )
  - [x] when  - ( method : get )
  - [x] then  - ( statusCode : HttpStatus.OK(200) )
- [x] 지하철 노선 조회
  - [x] given - ( name, color )
  - [x] when  - ( method : get )
  - [x] then  - ( statusCode : HttpStatus.OK(200) )
- [x] 지하철 노선 수정
  - [x] given - ( name, color )
  - [x] when  - ( method : put )
  - [x] then  - ( statusCode : HttpStatus.OK(200) )
- [x] 지하철 노선 삭제
  - [x] given - ( name, color )
  - [x] when  - ( method : delete )
  - [x] then  - ( statusCode : HttpStatus.NO_CONTENT(204) )
#### Step2 리뷰 사항 반영
- [ ] Step2 1차 리뷰 사항 반영
    - [x] api명세에 존재하지 않는 변수 지우기 ( createdDate, modifiedDate )
    - [x] 기존 생성자는 되도록 public으로 풀지 않기 ( protected로 은닉화 - 외부에서 쉽게 선언 못하도록 )
    - [x] 의존성 방향 재정의 ( respone -> entity는 가능하지만, 반대의 경우는 지양하기 )
    - [x] api경로 확인하기
    - [x] 중복된 테스트 하나로 빼내기
    
## Step3 - 구간 추가 기능    
#### Step3 리뷰 사항 반영
- [x] Step3 1차 리뷰 사항 반영
    - [x] BaseTest BeforeEach 추가
    - [x] getById는 EntityNotFoundException 에러 처리
    - [x] Stream.of 사용하기
- [ ] Step3 2차 리뷰 사항 반영
    - [x] 테스트 전부 통과하도록 처리
    - [x] 라인 구간 추가시 저장되어 있는 지하철역인지 검증 추가
    - [x] 도메인 성격에 맞는 일급컬렉션 이름 변경
    - [x] 사용하지 않는 메소드 제거
    - [x] 이미 초기화된 List에 대해서는 대체보다는 LIst에 데이터를 추가하는 로직으로 변경
    - [x] 테스트에서 한글 변수명 사용해보기
    - [x] 테스트시 재사용이 빈번한 메소드 추출
    - [x] 빈번하게 사용되는 메소드 SectionAcceptanceSupport 클래스로 추출
#### 기능 요구사항
 - 요구사항 설명에서 제공되는 요구사항을 기반으로 지하철 구간 추가 기능을 구현하세요.
 - 요구사항을 정의한 인수 조건을 조출하세요.
 - 인수 조건을 검증하는 인수 테스트를 작성하세요.
 - 예외 케이스에 대한 검증도 포함하세요.
#### 프로그래밍 요구사항
 - 인수 테스트 주도 개발 프로세스에 맞춰서 기능을 구현하세요.
 - 요구사항 설명을 참고하여 인수 조건을 정의
 - 인수 조건을 검증하는 인수 테스트 작성
 - 인수 테스트를 충족하는 기능 구현
 - 인수 조건은 인수 테스트 메서드 상단에 주석으로 작성하세요.
 - 뼈대 코드의 인수 테스트를 참고
 - 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트를 서로 격리 시키세요.
 - 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요. 
#### 요구사항 기능 목록
 - 역 사이에 새로운 역을 등록할 경우
 - 새로운 역을 상행 종점으로 등록할 경우
 - 새로운 역을 하행 종점으로 등록할 경우
 - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
 - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
#### 구현 리스트
- [x] LineStation Entity 추가
- [x] 기존 지하철 노선 등록, 수정, 조회, 삭제 리팩토링
  - [x] 등록 리팩토링
  - [x] 수정 리팩토링
  - [x] 조회 리팩토링
  - [x] 삭제 리팩토링           
- [x] 지하철 구간 등록 
  - [x] 새로운 역 등록하는 인수케이스( 예외 고려 x, 하행역, 선행역 등록 포함 )
  - [x] 예외상황 처리해서 역 등록하는 인수케이스