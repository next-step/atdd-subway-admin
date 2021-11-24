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


## 1단계 - 지하철 노선 관리
### 요구사항 지하철 노선 관리 기능을 구현하기
* 인수 테스트 작성
  * [x] 생성
  * [x] 중복 생성 실패
  * [x] 목록 조회
  * [x] 조회
  * [x] 수정
  * [x] 삭제
* 기능 목록: 생성 / 목록 조회 / 조회 / 수정 / 삭제
  * [x] 생성
  * [x] 중복 생성 실패
  * [x] 목록 조회
  * [x] 조회
  * [x] 수정
  * [x] 삭제
* [x] 기능 구현 후 인수 테스트 리팩터링
### 피드백 공부 내용 정리
클래스 레벨에서 @transactional 을 걸어주고 읽기 전용 메서드에서 @transactional(readOnly = true) 를 사용해주셨는데요
반대로 클래스 레벨에서 @transactional(readOnly = true) 을 사용하고 데이터 변경이 있을때 @transactional을 걸어주는 건 어떨까요? [링크](https://github.com/next-step/atdd-subway-admin/pull/449#discussion_r755177519)
~~~
 @transactional(readOnly = true)을 사용하면
  강제로 플러시 호출하지 않는 한 플러시가 일어나지 않는다.
  따라서 엔티티의 등록, 수정, 삭제가 동작하지 않고
  읽기 전용으로, 영속성 컨텍스트는 변경 감지를 위한 스냅샷을 보관하지 않으므로 성능이 향상된다.
 - 참고 : https://willseungh0.tistory.com/75
 
제 생각에는 말씀 해주신 방법을 적용하게 되면 아래와 같은 내용을 얻을 수 있을 거 같습니다.
 * 성능 향상을 기대할수 있다.
 * 개발자의 실수로 등록, 수정, 삭제가 발생되는 것을 방지 할 수 있다.
~~~
* 메소드 레퍼런스
  * 메소드 레퍼런스(Method Reference)는 Lambda 표현식을 더 간단하게 표현하는 방법입니다.
  * 메소드 레퍼런스는 ClassName::MethodName 형식으로 입력합니다. 메소드를 호출하는 것이지만 괄호()는 써주지 않고 생략합니다.
  * 참고자료 : https://codechacha.com/ko/java8-method-reference/
* Exception 공통처리 [링크](https://github.com/next-step/atdd-subway-admin/pull/449#discussion_r755182659)
  * 참고자료1 : https://javachoi.tistory.com/253
  * 참고자료2 : https://velog.io/@banjjoknim/RestControllerAdvice
* AssertAll
  * 참고: https://sas-study.tistory.com/316, https://escapefromcoding.tistory.com/358?category=1184809