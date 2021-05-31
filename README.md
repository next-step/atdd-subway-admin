
--------------------
# 1단계 - 지하철 노선 관리

##요구사항

###지하철 노선 관리 기능을 구현하기  

> 기능 목록: 생성 / 목록 조회 / 조회 / 수정 / 삭제

> 기능 구현 전 인수 테스트 작성  

> 기능 구현 후 인수 테스트 리팩터링

##
> 기능목록: 생성 / 목록조회 / 수정 / 삭제
> 1. 구현 전 인수테스트 작성
> 2. 기능구현
> 3. 리팩터링
##

![img_2.png](img_2.png)

## 요구사항 설명
### 지하철 노선 관련 기능의 인수 테스트를 작성하기

- LineAcceptanceTest 를 모두 완성시키세요.

```java
@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청

        // then
        // 지하철_노선_생성됨
    }

    ...
}
```
### 지하철 노선 관련 기능 구현하기
- 인수 테스트가 모두 성공할 수 있도록 LineController를 통해 요청을 받고 처리하는 기능을 구현하세요.
```java
@RestController
@RequestMapping("/lines")
public class LineController {

    ...
    
    @PostMapping
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        // TODO
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> findAllLines() {
        // TODO
    }
    
    ...
}
```
### 인수 테스트 리팩터링
- 인수 테스트의 각 스텝들을 메서드로 분리하여 재사용하세요.
    - ex) 인수 테스트 요청 로직 중복 제거 등

##힌트  

###RestAssured  

> 미리 제공한 StationAcceptanceTest 코드를 활용하세요 :)

### given
- 요청을 위한 값을 설정 (header, content type 등)
- body가 있는 경우 body 값을 설정 함
### when
- 요청의 url와 method를 설정
### then
- 응답의 결과를 관리
- response를 추출하거나 response 값을 검증할 수 있음
> 자세한 사용법은 Usage Guide를 참고

### 프론트엔드
- 구현한 기능이 정상적으로 동작하는지 확인을 하기 위한 코드입니다.
- 반드시 페이지에 맞게 설계를 할 필요는 없고 프론트엔드 코드를 수정해도 무방합니다.

#### 지하철 노선 관리 페이지
![img_1.png](img_1.png)

------------------------


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
