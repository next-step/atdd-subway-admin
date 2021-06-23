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


## 1단계 요구사항

* 각 미션의 요구사항을 파악한다.
* 각 미션에 관한 API를 파악한다.
* 노선의 생성의 인수테스트를 작성하고 성공하는지 테스트 한다.
* 노선의 각 테스트케이스를 작성하고, 성공을 위하여 컨트롤러를 구현한다.
* 인수테스트의 중복 로직을 리팩터링한다.


## 1단계 질문


## 2단계 요구사항

* 테스트케이스에서 파라메터에 시작, 도착 종점역과 거리를 추가한다.
* 요청 클래스와 DTO에 내용을 반영하고 요청이 제대로 들어오는지 확인한다.
* 구간 도메인을 개발한다
* 노선 조회 시 구간이 출력 될 수 있도록 연관관계를 새로 맺도록 한다.
* 노선이 구간으로 관리되도록 개발한다. (개발 구간에 대한 테스트케이스를 먼저 작성한다)
* 노선 조회를 개발한다.


## 3단계 요구사항

* 구간 등록 인수 테스트 작성하고 BeforeEach 작성
* 인수테스트에 관련 된 컨트롤러 작성
* distance 원시 값을 포장 하도록 개발
* 예외 처리 개발
    * 역 사이의 길이가 정상 적이지 않는 경우 테스트케이스 작성
    * 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
    * 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
* 리팩토링(역할에 맞는 행동 분배)
    * 도메인 역할 점검
    * 상수 제거
    * 응답이 400이 아닌 500으로 떨어지는 경우 확인
    * 인수 테스트에서 응답의 메시지를 확인 하고 테스트 가능 하도록 수정
* 테스트케이스 작성 후 개발
    * 구간 추가 - 역 사이에  새로운 역 등록
    * 새로운 역이 상행 종점
    * 새로운 역이 하행 종점
* 테스트케이스 검사 추가
    * 역 추가 후 조회 시 테스트 추가
    * 조회 시 순서대로 출력이 되는지  
* 리팩토링(중복 로직 제거, 역할 분배)    
    * 중복 로직 체크 제거
    * 도메인 체크 하여 역할이 잘 분리되었는지 확인
  

## 3단계 질문
Q. 인수테스트를 작성 하면서 유닛테스트는 작성은 어떤식으로 할까요?

A. 제가 일하는 환경에서는 인수테스트 보다는 통합테스트에 가까운 형태로 진행이 되고 있어요.
통합테스트를 통해 시나리오테스트 + API 엔드포인트 테스트를 함께 진행하고 있습니다.
이 과정에서 배운 ATDD 가 잘못됐다고는 생각하지 않아요. 다만 아직 현업에서 잘 퍼지지 않은 방법인 것 같습니다.
현재는 TDD 도 정착을 하고 있는 단계라고 생각해요. 따라서 우리가 ATDD 의 선구자가 되어서 정착을 시키면 가장 좋지 않을까요 ?

Q.역이 상당히 많으면 결국 많은 select가 이루어 지지 않을까 생각이 좀 들었는데요. (sections는 결국 프록시로 이루어져 있을거라고 생각이 되었습니다.)
이런 경우 구간 테이블을 line 으로 조회하여 1차캐시에 데이터를 넣는 방법은 어떨지 문의드립니다.

A. 넵 맞습니다. 저도 동일한 방안을 생각했고요 repository 에서 Line 조회시 fetch join 과 @batchsize 어노테이션을 활용하여 미리 Station 도 같이 한꺼번에 조회했었습니다.

Q. FetchLazy에 대한 hashcode 해결방안은?

A. hashcode() 는 객체의 상태(연관관계포함) 를 숫자로 변환하여 객체를 고유한 숫자로 표현하는 것이지요.
하지만 연관관계가 Lazy 이기 때문에 숫자로 변환해주기 위해서 결국 쿼리를 실행하여야 하는데요,
이 부분은 외래키 컬럼을 한번 더 매핑함으로써 해결 할 수 있습니다.
객체지향보다는 DB 쪽에 가까운 구조이기는 합니다.

```
@JoinColum(column="line_id")
@ManyToOne(fetchType=LAZY)
Line line;

@Column(column="line_id", insertable=false, updatable=false)
Long lineId;
```

이렇게 구현을 해주시면 연관관계 객체는 우리가 필요로 할때만 쿼리가 발생하고
Long lineId 는 쿼리를 하지 않아도 DB 레코드에 이미 값이 있기 때문에 한번 더 값으로써 매핑하면 ID 를 가져올 수 있게 됩니다.
그래서 객체 대신에 아이디 값을 hashcode 생성할 때 넣어주시면 될 것 같네요.
대신 이러한 방법은 @manytoone 관계만 사용이 가능합니다. 외래키를 레코드가 갖고 있기 때문에 가능한 방식입니다.


## 4단계 요구사항
* 노선 구간 controller 생성
* 노선 구간 인수 테스트 작성 후 개발
  * 구간 삭제 시 노선에 등록 되어 있지 않은 역을 제거시 실패
  * 구간이 하나인 노선에서 역을 제거 할 때 실패
  * 구간삭제 성공
* 도메인 역할을 중점으로 리팩토링