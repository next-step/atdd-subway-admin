# 1단계 - 지하철 노선 관리

## 1. 요구사항 정의

### 1.1. 명시된 요구사항

#### 1.1.1. 요구사항

##### 1.1.1.0. 도메인 설명

![mission-description](../documents/common/mission-description.png)

- 지하철 역(station)
    - 지하철 역 속성:
        - 이름(name)
- 지하철 구간(section)
    - 지하철 (상행 방향)역과 (하행 방향)역 사이의 연결 정보
    - 지하철 구간 속성:
        - 길이(distance)
- 지하철 노선(line)
    - 지하철 구간의 모음으로 구간에 포함된 지하철 역의 연결 정보
    - 지하철 노선 속성:
        - 노선 이름(name)
        - 노선 색(color)

##### 1.1.1.1. 지하철 노선 관리 기능을 구현하기

- 기능 목록: 생성 / 목록 조회 / 조회 / 수정 / 삭제
- 생성
    - 지하철 노선 생성 request
        ```json
        POST /lines HTTP/1.1
        accept: */*
        content-type: application/json; charset=UTF-8
        
        {
            "color": "bg-red-600",
            "name": "신분당선"
        }
        ```
    - 지하철 노선 생성 response
        ```json
        HTTP/1.1 201 
        Location: /lines/1
        Content-Type: application/json
        Date: Fri, 13 Nov 2020 00:11:51 GMT
        
        {
            "id": 1,
            "name": "신분당선",
            "color": "bg-red-600",
            "createdDate": "2020-11-13T09:11:51.997",
            "modifiedDate": "2020-11-13T09:11:51.997"
        }
        ```
- 목록 조회
    - 지하철 노선 목록 조회 request
        ```json
        GET /lines HTTP/1.1
        accept: application/json
        host: localhost:49468
        ```
    - 지하철 노선 목록 조회 response
        ```json
        HTTP/1.1 200
        Content-Type: application/json
        Date: Fri, 13 Nov 2020 00:11:51 GMT
        
        [
            {
                "id": 1,
                "name": "신분당선",
                "color": "bg-red-600",
                "stations": [
        
                ],
                "createdDate": "2020-11-13T09:11:52.084",
                "modifiedDate": "2020-11-13T09:11:52.084"
            },
            {
                "id": 2,
                "name": "2호선",
                "color": "bg-green-600",
                "stations": [
                    
                ],
                "createdDate": "2020-11-13T09:11:52.098",
                "modifiedDate": "2020-11-13T09:11:52.098"
            }
        ]
        ```
- 조회
    - 지하철 노선 조회 request
        ```json
        GET /lines/1 HTTP/1.1
        accept: application/json
        host: localhost:49468
        ```
    - 지하철 노선 조회 response
        ```json
        HTTP/1.1 200 
        Content-Type: application/json
        Date: Fri, 13 Nov 2020 00:11:51 GMT
        
        {
            "id": 1,
            "name": "신분당선",
            "color": "bg-red-600",
            "stations": [
        
            ],
            "createdDate": "2020-11-13T09:11:51.866",
            "modifiedDate": "2020-11-13T09:11:51.866"
        }
        ```
- 수정
    - 지하철 노선 수정 request
        ```json
        PUT /lines/1 HTTP/1.1
        accept: */*
        content-type: application/json; charset=UTF-8
        content-length: 45
        host: localhost:49468
        
        {
            "color": "bg-blue-600",
            "name": "구분당선"
        }
        ```
    - 지하철 노선 수정 response
        ```json
        HTTP/1.1 200 
        Date: Fri, 13 Nov 2020 00:11:51 GMT
        ```
- 삭제
    - 지하철 노선 삭제 request
        ```json
        DELETE /lines/1 HTTP/1.1
        accept: */*
        host: localhost:49468
        ```
    - 지하철 노선 삭제 response
        ```json
        HTTP/1.1 204 
        Date: Fri, 13 Nov 2020 00:11:51 GMT
        ```
    
##### 1.1.1.2. 기능 구현 전 인수 테스트 작성

###### 1.1.1.2.1. 지하철 노선 관련 기능의 인수 테스트를 작성하기

`LineAcceptanceTest` 를 모두 완성시키세요.

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

###### 1.1.1.2.2. 지하철 노선 관련 기능 구현하기

인수 테스트가 모두 성공할 수 있도록 `LineController`를 통해 요청을 받고 처리하는 기능을 구현하세요.

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

##### 1.1.1.3. 기능 구현 후 인수 테스트 리팩터링

- 인수 테스트의 각 스텝들을 메서드로 분리하여 재사용하세요.
    - ex) 인수 테스트 요청 로직 중복 제거 등

#### 1.1.2. 힌트

##### 1.1.2.1. RestAssured

> 미리 제공한 StationAcceptanceTest 코드를 활용하세요 :)

###### 1.1.2.1.1. given

- 요청을 위한 값을 설정 (header, content type 등)
- body가 있는 경우 body 값을 설정 함

###### 1.1.2.1.2. when

- 요청의 url와 method를 설정

###### 1.1.2.1.3. then

- 응답의 결과를 관리
- response를 추출하거나 response 값을 검증할 수 있음

> 자세한 사용법은 [Usage Guide](https://github.com/rest-assured/rest-assured/wiki/Usage#examples) 를 참고

##### 1.1.2.2. 프론트엔드

- 구현한 기능이 정상적으로 동작하는지 확인을 하기 위한 코드입니다.
- 반드시 페이지에 맞게 설계를 할 필요는 없고 프론트엔드 코드를 수정해도 무방합니다.

### 1.2. 기능 요구사항 정리

|구분 | 상세 |구현방법     |
|:----:  |:------  |:---------|
|노선 관리|• 지하철 노선 생성|• `LineAcceptanceTest` 작성<br>• 원시값 포장<br>• `Entity` 변경<br>• `LineGroup` 일급콜렉션 생성<br>• `LineService` 생성 기능 수정<br>• `LineController` 생성 기능 수정|
|노선 관리|• 지하철 노선 목록 조회|• `LineAcceptanceTest` 작성<br>• `LineService` 목록 조회기능 수정<br>• `LineController` 목록 조회 기능|
|노선 관리|• 지하철 노선 조회|• `LineAcceptanceTest` 작성<br>• `LineService` 조회 기능<br>• `LineController` 조회 기능|
|노선 관리|• 지하철 노선 수정|• `LineAcceptanceTest` 작성<br>• `LineService` 수정 기능<br>• `LineController` 수정 기능|
|노선 관리|• 지하철 노선 삭제|• `LineAcceptanceTest` 작성<br>• `LineService` 삭제 기능<br>• `LineController` 삭제 기능|

### 1.3. 프로그래밍 요구사항

|구분|상세|구현 방법|
|:---:|:---|---|
|Convention|• 자바 코드 컨벤션을 지키면서 프로그래밍한다.<br>&nbsp;&nbsp;• https://naver.github.io/hackday-conventions-java/ <br>&nbsp;&nbsp;• https://google.github.io/styleguide/javaguide.html <br>&nbsp;&nbsp;•  https://myeonguni.tistory.com/1596 |- gradle-editorconfig 적용<br>- gradle-checkstyle 적용<br>- IntelliJ 적용<br>- Github 적용|
|테스트|• 모든 기능을 TDD로 구현해 단위 테스트가 존재해야 한다. 단, UI(System.out, System.in) 로직은 제외<br>&nbsp;&nbsp;• 핵심 로직을 구현하는 코드와 UI를 담당하는 로직을 구분한다.<br>&nbsp;&nbsp;•UI 로직을 InputView, ResultView와 같은 클래스를 추가해 분리한다.|- 핵심 로직 단위테스트|

### 1.4. 비기능 요구사항

|구분 |상세 |구현방법     |
|:----:  |:------  |:---------|
|요구사항|• 기능을 구현하기 전에 README.md 파일에 구현할 기능 목록을 정리해 추가한다.|- 요구사항 정의 정리|
|Convention|• git의 commit 단위는 앞 단계에서 README.md 파일에 정리한 기능 목록 단위로 추가한다.<br>&nbsp;&nbsp;• 참고문서 : [AngularJS Commit Message Conventions](https://gist.github.com/stephenparish/9941e89d80e2bc58a153)|- git commit 시 해당 convention 적용|

#### 1.4.1. AngularJS Commit Message Conventions 중

- commit message 종류를 다음과 같이 구분

```
feat (feature)
 fix (bug fix)
 docs (documentation)
 style (formatting, missing semi colons, …)
 refactor
 test (when adding missing tests)
 chore (maintain)
 ```

# 1.4.2. editorConfig setting
```
Execution failed for task ':editorconfigCheck'.
> There are .editorconfig violations. You may want to run

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output. Run with --scan to get full insights.
```

- 위와 같은 에러가 뜨면 다음을 실행한다. `./gradlew editorconfigFormat`

## 2. 분석 및 설계

### 2.1. 이번 Step 핵심 목표

#### 2.1.1. ATDD 

> 학습 내용 간단히 정리

### 2.2. Todo List

- [x] 0.기본 세팅
    - [x] 0-1.git fork/clone
        - [x] 0-1-1.NEXTSTEP 내 과제로 이동 및 '미션시작'
        - [x] 0-1-2.실습 github으로 이동
        - [x] 0-1-3.branch 'gregolee'로 변경
        - [x] 0-1-4.fork
        - [x] 0-1-5.clone : `git clone -b gregolee --single-branch https://github.com/gregolee/atdd-subway-admin.git`
        - [x] 0-1-6.branch : `git checkout -b step1`
    - [x] 0-2.요구사항 정리
    - [ ] 0-3.[AngularJS Commit Message Conventions](https://gist.github.com/stephenparish/9941e89d80e2bc58a153#generating-changelogmd) 참고
    - [ ] 0-4.Slack을 통해 merge가 되는지 확인한 후에 코드 리뷰 2단계 과정으로 다음 단계 미션을 진행
        - [ ] 0-4-1.gregolee(master) branch로 체크아웃 : `git checkout gregolee`
        - [ ] 0-4-2.step4 branch 삭제 : `git branch -D step1`
        - [ ] 0-4-3.step4 branch 삭제 확인 : `git branch -a`
        - [ ] 0-4-4.원본(next-step) git repository를 remote로 연결 (미션 당 1회) : `git remote add -t gregolee upstream https://github.com/next-step/atdd-subway-admin`
        - [ ] 0-4-5.원본(next-step) git repository를 remote로 연결 확인 : `git remote -v`
        - [ ] 0-4-6.원본(next-step) git repository에서 merge된 나의 branch(gregolee)를 fetch : `git fetch upstream gregolee`
        - [ ] 0-4-7.remote에서 가져온 나의 branch로 rebase : `git rebase upstream/gregolee`
        - [ ] 0-4-7.gregolee -> step2로 체크아웃 : `git checkout -b step2`
    - [ ] 0-5.리뷰어님의 리뷰를 반영한 코드로 수정
        - [ ] 0-5-1.적용사항 없음
- [ ] 1.자바 코드 컨벤션을 위한 세팅
    - [ ] 1-1.[gradle-editorconfig](https://naver.github.io/hackday-conventions-java/#editorconfig) 적용
    - [ ] 1-2.[gradle-checkstyle](https://naver.github.io/hackday-conventions-java/#checkstyle) 적용
    - [ ] 1-3.[IntelliJ](https://naver.github.io/hackday-conventions-java/#_intellij) 적용
    - [ ] 1-4.[Github](https://naver.github.io/hackday-conventions-java/#_github) 적용
- [ ] 2.학습
    - [ ] 2-1.RestAssured 학습 : [[Usage Guide]](https://github.com/rest-assured/rest-assured/wiki/Usage#examples)
- [x] 3.분석 및 설계
    - [x] 3-1.step01.md 초안 작성
- [ ] 4.구현
    - [ ] 4-1.엔티티 매핑
        - [ ] 4-1-1.`Answer`
        - [ ] 4-1-2.`DeleteHistory`
        - [ ] 4-1-3.`Question`
        - [ ] 4-1-4.`User`
    - [ ] 4-2.테스트 코드 작성
        - [ ] 4-2-1.`AnswerTest`
        - [ ] 4-2-2.`DeleteHistoryTest`
        - [ ] 4-2-3.`QuestionTest`
        - [ ] 4-2-4.`UserTest`
- [ ] 5.테스트
    - [ ] 5-1.Gradle build Success 확인
    - [ ] 5-2.checkstyle 문제없는지 확인 (Java Convention)
    - [ ] 5-3.요구사항 조건들 충족했는지 확인
        - [ ] 5-3-1.핵심 단위 로직 테스트
- [ ] 6.인수인계
    - [ ] 6-1.소감 및 피드백 정리
        - [ ] 6-1-1.느낀점 & 배운점 작성
        - [ ] 6-1-2.피드백 요청 정리
    - [ ] 6-2.코드리뷰 요청 및 피드백
        - [ ] 6-1-1.step1를 gregolee/jwp-qna로 push : `git push origin step1`
        - [ ] 6-1-2.pull request(PR) 작성
    - [ ] 6-3.Slack을 통해 merge가 되는지 확인한 후에 미션 종료

## 3. 인수인계

### 3.1. 느낀점 & 배운점

#### 3.1.1. 느낀점

- 

#### 3.1.2. 배운점

- 

### 3.2. 피드백 요청

- 
