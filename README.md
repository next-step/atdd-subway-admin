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


---
### Step3 구현 필요 기능 내역 ### 
1. Line과 Station은 다대다 매핑이 필요하다
 - ManyToMany는 사이 테이블에 대한 추가 정보 저장이 불가하여, Line_Station Entity 생성 후 각각 ManyToOne, ManyToOne으로 해결한다. 
2. 구간 정보를 담은 Section 구현이 필요하다. 
 - 구간의 각 Station정보와 해당하는 Line, 거리 Section순서 등을 포함한다.
3. 구간저장시 에러처리가 필요하다.
 - 구간 저장할 역이 상행 종점으로 되어야하는가?
 - 구간 저장할 역이 하행 종점으로 되어야 하는가?
 - 구간 저장시, 구간 설정할 두 역중 한곳이라도 line에 포함이 되어있는가?
 - 구간 저장시, 이미 저장된 구간인가?
 - 신규 구간의 distance가 포함될 구간의 distance를 넘지 않는가? 
4. 위 구간에 대한 저장 기능을 구현한다.

---
### Step4 구현 필요 기능 내역 ###
1. 노선 구간 제거 기능 
 - 제거할 구간이 line의 가장 앞 구간 여부
   - line의 출발역 변경 및 distance변경
 - 제거할 구간이 line의 가장 끝 구간 여부 
   - line의 종창역 변경 및 distance변경
 - 제거할 구간이 사이에 있는 경우 
   - 해당 station이 포함되어있는 2개의 section을 묶는다. 
 - 에러처리
   - 지워야할 역이 없는경우
 