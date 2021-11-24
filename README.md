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


## STEP 1 기능목록
1. 노선 생성한다.
 - 노선 생성 요청을 한다.
 - 노선이 생성된다. 

2. 노선 목록을 조회한다.
 - 노선 목록 조회를 요청한다.
 - 조회된 노선 목록을 검증한다.

3. 노선을 수정한다.
 - 노선 수정 요청을 한다.
 - 노선이 수정되었는지 검증한다.

4. 노선을 삭제한다
 - 노선 삭제 요청을 한다.

## STEP 2 
1. Feature : 노선 생성하기  
 Scenario : 노선 생성 시 상행, 하행 추가  
  Given 상행, 하행에 등록할 지하철역이 등록되어있다.  
  When 노선 생성을 요청한다.  
  Then 사용자는 상태코드를 응답받는다.  
    
2. Feature : 노선 목록조회  
 Scenario : 노선 목록 조회하기  
  Given 노선을 생성한다.  
  And 지하철역을 생성한다.  
  When 생성한 노선을 조회한다.  
  Then 사용자는 노선의 정보와 노선에 속한 지하철 역 정보를 응답받는다.  
 
## STEP 3
1. Feature : 노선에 구간 추가  
  Scenario : 노선에 구간 추가하기  
   Given 노선 상행, 하행에 등록할 지하철 역 3개가 등록되어있다.  
   And 노선이 추가되어있다.  
   When 노선에 구간 추가 요청을 한다.  
   Then 사용자는 상태코드를 응답받는다.  

2. Feature : 노선에 구간 추가  
  Scenario : 노선에 구간 추가할 시 상행, 하행이 동일한 구간이 존재할 경우
   Given 노선 상행, 하행에 등록할 지하철 역 2개가 등록되어있다.  
   And 노선이 추가되어있다.  
   And 기존에 등록된 구간과 동일한 상행, 하행에 대한 정보를 준비한다.
   When 노선에 구간 추가 요청을 한다.  
   Then 사용자는 상태코드를 응답받는다. (500)  

3. Feature : 노선에 구간 추가  
  Scenario : 노선에 기존 구간 사이에 구간이 추가될 시 기존 구간보다 거리가 크거나 같은 경우 
   Given 노선 상행, 하행에 등록할 지하철 역 2개가 등록되어있다.  
   And 노선이 추가되어있다.  
   And 기존에 등록된 구간과 동일한 상행, 하행에 대한 정보를 준비한다.
   When 노선에 구간 추가 요청을 한다.  
   Then 사용자는 상태코드를 응답받는다. (500)  
