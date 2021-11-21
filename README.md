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

This project is [MIT](https://github.com/next-step/atdd-subway-admin/blob/master/LICENSE.md)
licensed.

--------------------

## 참고

- [equals](https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/)
- > https://developer.jboss.org/docs/DOC-13933  
  java.lang.Object 문서에 따르면 hashCode()에 대해 항상 0을 반환하는 것이 완벽해야 합니다. 고유한 개체에 대해 고유한 숫자를 반환하기 위해 hashCode()를 구현하면 성능이 향상될 수 있다는 긍정적인 효과가 있습니다. 단점은 hashCode()의 동작이 equals()와 일치해야 한다는 것입니다. 객체 a와 b의 경우 a.equals(b)가 true이면 a.hashCode() == b.hashCode()보다 true여야 합니다. 그러나 a.equals(b)가 false를 반환하면 a.hashCode() == b.hashCode()가 여전히 true일 수 있습니다. hashCode()를 'return 0'으로 구현하는 것은 이러한 기준을 충족하지만 HashSet 또는 HashMap과 같은 Hash 기반 컬렉션에서는 매우 비효율적입니다.
- [Java equals()와 hashCode()에 대해](https://nesoy.github.io/articles/2018-06/Java-equals-hashcode)

# 지하철 미션

# 미션 시작

## 인수 테스트 시나리오

```text
  Feature: 지하철 노선관련기능
    Scenario: 지하철 노선을 생성한다.
      Given 지하철 노선 생성 파라미터 맵핑
      When 지하철 노선 생성 요청
      Then  지하철 노선 생성 됨
      
    Scenario: 기존 존재하는 지하철 노선 이름으로 노선 생성
      Given 지하철 노선 생성됨
      When 지하철 노선 생성요청
      Then 지하철 노선 생성 실패
      
    Scenario: 지하철 노선 목록 조회
      Given 지하철 노선등록 되어있음
      When 지하철 노선 목록 조회
      Then  등록된 노선 모두 목록에 포함되어 있음
      
    Scenario: 지하철 노선을 조회
      Given 지하철 노선 등록 되어 있음
      When 등록된 노선 조회
      Then  조회됨
      
    Scenario: 지하철 노선을 수정
      Given 지하철 노선을 등록함
      When 등록된 지하철 노선을 수정 요청
      Then  수정 응답받는다.
      
    Scenario: 지하철 노선을 제거
      Given 지하철 노선 등록 되어 있음
      When 등록된 지하철 노선 제거 요청
      Then  NO_CONTENT 정상 응답 받음
```

## 1단계 - 지하철 노선 관리

- **도메인 테스트**
    - [X] Line 생성
    - [X] Line 업데이트
    - [X] Line 삭제
- **Repository 테스트**
    - [X] Line 생성
    - [X] Line equals
    - [X] Line 전체조회
    - [X] Line delete
- **지하철 노선 관리 기능 구현**
    - [X] 노선 생성
    - [X] 노선 목록 조회
    - [X] 조회
    - [X] 수정
    - [X] 삭제
- **기능 구현 후 인수 테스트 리팩터링**