# 1단계 - 지하철 노선 관리

## 1. 요구사항 정의

### 1.1. 명시된 요구사항

#### 1.1.1. 요구사항

##### 1.1.1.0. 도메인 설명

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

> 학습 내용 간단히 정리 [[Markdown 보기]](./summary.md)


#### 2.1.2. Response에서 빈 배열이 표출되지 않는 이유

    결론 : get~ 메서드를 생성하지 않아서 빈 배열의 내용이 표출되지 않았다.

1. 현상
    ```json
    HTTP/1.1 200 
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Mon, 14 Jun 2021 22:44:34 GMT
    Keep-Alive: timeout=60
    Connection: keep-alive
    
    [
    {
        "id": 1,
        "name": "2호선",
        "color": "#FFFFFF",
        "stations": [
    
            ],
            "createdDate": "2021-06-15T07:44:33.909713",
            "modifiedDate": "2021-06-15T07:44:33.909713"
        },
        {
            "id": 2,
            "name": "4호선",
            "color": "#000000",
            "stations": [
                
            ],
            "createdDate": "2021-06-15T07:44:33.919948",
            "modifiedDate": "2021-06-15T07:44:33.919948"
        }
    ]
    ```

2. 코드 추가
    ```java
    public List<Station> getStations() {
        return stations;
    }
    ```

3. 결과
    ```json
    HTTP/1.1 200 
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Mon, 14 Jun 2021 22:44:34 GMT
    Keep-Alive: timeout=60
    Connection: keep-alive
    
    [
        {
        "id": 1,
        "name": "2호선",
        "color": "#FFFFFF",
        "stations": [
    
            ],
            "createdDate": "2021-06-15T07:44:33.909713",
            "modifiedDate": "2021-06-15T07:44:33.909713"
        },
        {
            "id": 2,
            "name": "4호선",
            "color": "#000000",
            "stations": [
                
            ],
            "createdDate": "2021-06-15T07:44:33.919948",
            "modifiedDate": "2021-06-15T07:44:33.919948"
        }
    ]
    ```

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
    - [x] 0-3.[AngularJS Commit Message Conventions](https://gist.github.com/stephenparish/9941e89d80e2bc58a153#generating-changelogmd) 참고
    - [x] 0-4.Slack을 통해 merge가 되는지 확인한 후에 코드 리뷰 2단계 과정으로 다음 단계 미션을 진행
        - [x] 0-4-1.gregolee(master) branch로 체크아웃 : `git checkout gregolee`
        - [x] 0-4-2.step1 branch 삭제 : `git branch -D step1`
        - [x] 0-4-3.step1 branch 삭제 확인 : `git branch -a`
        - [x] 0-4-4.원본(next-step) git repository를 remote로 연결 (미션 당 1회) : `git remote add -t gregolee upstream https://github.com/next-step/atdd-subway-admin`
        - [x] 0-4-5.원본(next-step) git repository를 remote로 연결 확인 : `git remote -v`
        - [x] 0-4-6.원본(next-step) git repository에서 merge된 나의 branch(gregolee)를 fetch : `git fetch upstream gregolee`
        - [x] 0-4-7.remote에서 가져온 나의 branch로 rebase : `git rebase upstream/gregolee`
        - [x] 0-4-7.gregolee -> step2로 체크아웃 : `git checkout -b step2`
    - [x] 0-5.리뷰어님의 리뷰를 반영한 코드로 수정
        - [x] 0-5-1.적용사항 없음
- [x] 1.자바 코드 컨벤션을 위한 세팅
    - [x] 1-1.[gradle-editorconfig](https://naver.github.io/hackday-conventions-java/#editorconfig) 적용
    - [x] 1-2.[gradle-checkstyle](https://naver.github.io/hackday-conventions-java/#checkstyle) 적용
    - [x] 1-3.[IntelliJ](https://naver.github.io/hackday-conventions-java/#_intellij) 적용
    - [x] 1-4.[Github](https://naver.github.io/hackday-conventions-java/#_github) 적용
- [x] 2.학습
    - [x] 2-1.RestAssured 학습 : [[Usage Guide]](https://github.com/rest-assured/rest-assured/wiki/Usage#examples)
    - [x] 2-2.LiveTemplates - IntelliJ
- [x] 3.분석 및 설계
    - [x] 3-1.step01.md 초안 작성
    - [x] 3-2.ATDD 작성
- [x] 4.구현
    - [x] 4-1.ATDD 작성
        - [x] 4-1-1.`StationAcceptanceTest`
        - [x] 4-1-2.`LineAcceptanceTest`
    - [x] 4-2.vo 작성
        - [x] 4-2-1.`Station`
            - [x] 4-2-1-1.`Name`
        - [x] 4-2-2.`Line`
            - [x] 4-2-2-1.`Name`
            - [x] 4-2-2-1.`Color`
    - [x] 4-3.entity 작성
        - [x] 4-3-1.`Station`
        - [x] 4-3-2.`Line`
    - [x] 4-4.repository 작성
        - [x] 4-4-1.`StationRepository`
        - [x] 4-4-2.`LineRepository`
    - [x] 4-5.service 작성
        - [x] 4-5-1.`StationService`
        - [x] 4-5-2.`LineService`
    - [x] 4-6.controller 작성
        - [x] 4-6-1.`StationController`
        - [x] 4-6-2.`LineController`
    - [x] 4-7.추가 리팩터링
        - [x] 4-7-1.일급콜렉션 적용
            - [x] 4-7-1-1.`StationGroup`
        - [x] 4-7-2.엔티티 연관과계 매핑
            - [x] 4-7-2-1.`Station`
            - [x] 4-7-2-2.`Line`
- [x] 5.테스트
    - [x] 5-1.Gradle build Success 확인
    - [x] 5-2.checkstyle 문제없는지 확인 (Java Convention)
    - [x] 5-3.요구사항 조건들 충족했는지 확인
        - [x] 5-3-1.핵심 단위 로직 테스트
    - [x] 5-4.인수 테스트 확인
    - [x] 5-5.UI 테스트 확인
- [x] 6.인수인계
    - [x] 6-1.소감 및 피드백 정리
        - [x] 6-1-1.느낀점 & 배운점 작성
        - [x] 6-1-2.피드백 요청 정리
    - [x] 6-2.코드리뷰 요청 및 피드백
        - [x] 6-1-1.step1를 gregolee/atdd-subway-admin로 push : `git push origin step1`
        - [x] 6-1-2.pull request(PR) 작성
    - [x] 6-3.Slack을 통해 merge가 되는지 확인한 후에 미션 종료

### 2.3. ATDD 작성

ATDD 작성 [Markdown 보기](./atdd.md)

## 3. 인수인계

### 3.1. 느낀점 & 배운점

#### 3.1.1. 느낀점

- 마일스톤 같은 ATDD
    - TDD를 진행하면서 길을 많이 잃었습니다.
        - 진행하다보면 도메인 간 책임과 역할이 꼬여버려 리팩토링하는데 생각보다 많은 시간을 소요했습니다.
    - 개발자만의 공유가 아닌 기획자, 개발자 등 여러 참여자의 의사소통이 원활히 가능할 것 같습니다.
        - 특히 개발자 간의 소통이 잘 이루어지기 어려운 경우가 많았습니다.
        - 기술적으로 설명하기엔 각자 알고 있는 지식 수준의 차이가 있고, 문서화하기엔 많은 시간이 필요합니다.
        - 인수테스트를 통하여 공유 가능한 방식으로 소통하는 점을 간접 경험할 수 있어서 좋다고 생각합니다.
    

#### 3.1.2. 배운점

- TDD는 개발자가 구현하는 코드에 집중한다면, ATDD는 추상적인 개념을 구체화하는 것에 집중하는 것을 배웠습니다.
    - TDD를 통해 테스트 및 그 테스트에 도달하는 코드를 작성하는데 집중합니다.
    - ATDD는 하나의 주제(기능, 인수 조건 등)을 구체화하여 동일한 내용으로 구상토록 집중합니다.
    - 보통 SI/SM 사업에서 고생하는 이유는 하나의 요구사항을 서로 다른 관점으로 바라보는데서 시간과 개발 공수가 차이가 납니다.
    - ATDD를 적용하면 프로젝트의 팀원들과 공유함으로써 적어도 하나의 합의된 목표에 도달할 수 있다는 장점이 있습니다.
    - 다만 이러한 방향을 팀원 모두가 공유하고 문화 정착을 위해서는 시행착오가 오래 걸릴 것으로 생각합니다.

### 3.2. 피드백 요청

- 이번 단계에서는 피드백 요청 드릴 사항이 없습니다.