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

## 2단계 - 인수 테스트 리팩터링
### 요구사항 API 변경 대응하기
* 인수테스트 추가 및 수정
  * [x] 노선 생성 시 종점역 (상행, 하행) 정보를 요청 파라미터에 함께 추가하기
    * 두 종점역은 구간의 형태로 관리되어야 함
  * [x] 노선 조회 시 응답 결과에 역 목록 추가하기
    * 상행역 부터 하행역 순으로 정렬되어야 함
* 기능 구현
  * [x] 노선엔티티에 구역 정보를 매핑
  * [x] 구역 생성
  * [x] 구역 일급 콜렉션
    * [x] 노선 생성시 구역정보 추가
  * [x 노선 목록 조회

### 피드백 공부 내용 정리
* Java Optional 바르게 쓰기
  * 참고자료 : https://homoefficio.github.io/2019/10/03/Java-Optional-%EB%B0%94%EB%A5%B4%EA%B2%8C-%EC%93%B0%EA%B8%B0/
  * 메서드가 반환할 결과값이 ‘없음’을 명백하게 표현할 필요가 있고, null을 반환하면 에러를 유발할 가능성이 높은 상황에서 메서드의 반환 타입으로 Optional을 사용하자는 것이 Optional을 만든 주된 목적이다.
* 엔티티 기본 생성자의 노출 범위
  * 참고자료 : https://wbluke.tistory.com/6
* flatMap
  * 참고자료 : https://madplay.github.io/post/difference-between-map-and-flatmap-methods-in-java
  * flatMap 메서드는 스트림의 형태가 배열과 같을 때, 모든 원소를 단일 원소 스트림으로 반환할 수 있습니다.

## 3단계 - 구간 추가 기능
지하철 구간 등록 기능을 구현하기
~~~
구간 등록 API Request
POST /lines/1/sections HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8
host: localhost:52165

{
    "downStationId": "4",
    "upStationId": "2",
    "distance": 10
}
~~~
* 기능 구현 전 인수 테스트 작성
  * [x] 역 사이에 새로운 역을 등록할 경우 -> 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
  * [x] 새로운 역을 상행 종점으로 등록할 경우
  * [x] 새로운 역을 하행 종점으로 등록할 경우
* 예외 케이스 처리 인수 테스트 작성
  * [x] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
  * [x] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
  * [x] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
* 기능 구현
  * [x] 구간 등록 기능
  * [x] request, controller , dto 생성
  * [x] 역 사이에 새로운 역을 등록
  * [x] 새로운 역을 상행 종점으로 등록
  * [x] 새로운 역을 하행 종점으로 등록
  * [x] 등록시 valid 처리
    * [x] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
    * [x] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
    * [x] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

### 피드백 공부 내용 정리
* [질문답변링크](https://github.com/next-step/atdd-subway-admin/pull/536#issuecomment-984358858)
* Exception Guide
  * 참고자료 : https://cheese10yun.github.io/spring-guide-exception/#null
  * Error Response 객체는 항상 동일한 Error Response를 가져야 합니다.
* 외부에 도메인(엔티티)를 노출하면 안되는 이유 
  * [링크](https://github.com/next-step/atdd-subway-admin/pull/536#discussion_r760711630)
  * 참고자료 : https://tecoble.techcourse.co.kr/post/2021-04-25-dto-layer-scope/
  
## 4단계 - 구간 제거 기능
지하철 구간 제거 기능을 구현하기
* HappyPath 인수테스트 작성
  * [x] 노선의 구간을 제거하는 기능
* 예외상황 인수테스트 작성
  * [ ] 구간이 하나일때는 제거 할 수 없음
* 기능 구현
  * [x] 노선의 구간을 제거하는 기능
  * [X] station으로 Section 2개를 찾는다.
  * [x] 라인 중간에 껴있는 구간을 제거하는 경우
  * [x] 상행 또는 하행역이 포함된 구간을 제거하는 경우
  * [x] 찾은 Section에 upStation, downStation, distance를 하나로 합친다.
  * [x] 구간이 하나일때는 제거 할 수 없다