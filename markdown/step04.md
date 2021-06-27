# 4단계 - 구간 제거 기능

## 1. 요구사항 정의

### 1.1. 명시된 요구사항

#### 1.1.1. 요구사항

##### 1.1.1.0. 지하철 구간 제거 기능을 구현하기
- 노선의 구간을 제거하는 기능을 구현하기
- 구간 삭제 시 예외 케이스를 고려하기

##### 1.1.1.1. 추가사항

- 지하철 구간 기능
    - 삭제 Request
        ```json
        DELETE /lines/1/sections?stationId=2 HTTP/1.1
        accept: */*
        host: localhost:52165
        ```

##### 1.1.1.2. 요구사항 설명

###### 1.1.1.2.1. 노선의 구간을 제거하는 기능을 구현하기

- 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
- 중간역이 제거될 경우 재배치를 함
    - 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 됨
    - 거리는 두 구간의 거리의 합으로 정함

###### 1.1.1.2.2. 구간 삭제 시 예외 케이스를 고려하기

- 기능 설명을 참고하여 예외가 발생할 수 있는 경우를 검증할 수 있는 인수 테스트를 만들고 이를 성공 시키세요.

> 예시) 노선에 등록되어있지 않은 역을 제거하려 한다.

1. 구간이 하나인 노선에서 마지막 구간을 제거할 때
    - 제거할 수 없음
    
#### 1.1.2. 힌트

##### 1.1.2.1. 구간 제거

- 구간 제거 요청 처리

```java
@DeleteMapping("/{lineId}/sections")
public ResponseEntity removeLineStation(
        @PathVariable Long lineId, 
        @RequestParam Long stationId) {
    lineService.removeSectionByStationId(lineId, stationId);
    return ResponseEntity.ok().build();
}
```

### 1.2. 기능 요구사항 정리

|구분 | 상세 |구현방법     |
|:----:  |:------  |:---------|
|구간 관리|• 지하철 구간 제거(HappyPath)|• `LineController` 수정<br>• `SectionGroup` 내 제거 기능 추가|
|구간 관리|• 지하철 구간 제거 예외 처리|• `CannotDeleteSectionException` 생성<br>• `LineController` 수정<br>• `SectionGroup` 내 제거 유효성 체크|

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
    - [x] 0-4.Slack을 통해 merge가 되는지 확인한 후에 코드 리뷰 4단계 과정으로 다음 단계 미션을 진행
        - [x] 0-4-1.gregolee(master) branch로 체크아웃 : `git checkout gregolee`
        - [x] 0-4-2.step3 branch 삭제 : `git branch -D step3`
        - [x] 0-4-3.step3 branch 삭제 확인 : `git branch -a`
        - [x] 0-4-4.원본(next-step) git repository를 remote로 연결 (미션 당 1회) : `git remote add -t gregolee upstream https://github.com/next-step/atdd-subway-admin`
        - [x] 0-4-5.원본(next-step) git repository를 remote로 연결 확인 : `git remote -v`
        - [x] 0-4-6.원본(next-step) git repository에서 merge된 나의 branch(gregolee)를 fetch : `git fetch upstream gregolee`
        - [x] 0-4-7.remote에서 가져온 나의 branch로 rebase : `git rebase upstream/gregolee`
        - [x] 0-4-7.gregolee -> step4로 체크아웃 : `git checkout -b step4`
    - [x] 0-5.리뷰어님의 리뷰를 반영한 코드로 수정
        - [x] 0-5-1.NextStep에서 제공하는 이미지 제거
        - [x] 0-5-2.테스트 코드 아래 쪽으로 배치 : `SectionAcceptanceTest.java` 36~48 line
        - [x] 0-5-3.`SectionAcceptanceTest`
            - [x] 0-5-3-1.Long 타입 반환 메서드 만들기
            - [x] 0-5-3-2.네이밍 변경 : 보다 직관적으로 읽히도록 변경
            - [x] 0-5-3-3.테스트 내용 변경 : 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.
        - [x] 0-5-4.`SectionGroup.java`
            - [x] 0-5-4-1.`checkAddSection()` -> `validateSection()`
            - [x] 0-5-4-2.추가(add) 로직 좀 더 단순하게 작성
            - [x] 0-5-4-3.sort() : 정렬이 되어 있어서 필요한가? ===> 필요합니다. UI 테스트 간 section이 정렬되지 않아 전달받는 경우가 생겼습니다.
            - [x] 0-5-4-4.`upStation`이 같은 경우, `downStation`이 같은 경우 분기할 것
        - [x] 0-5-5.`StationGroup.java` : 필요성이 없으면 제거
        - [x] 0-5-6.`Section.java`
            - [x] 0-5-6-1.`findSectionIndexWhenTargetSectionIsInner()` : 존재여부 관리
        - [x] 0-5-7.`Distance.java`
            - [x] 0-5-7-1.`adjust~` : 명칭 변경 `minus~`
            - [x] 0-5-7-2.불변객체로 변경하기
- [x] 1.자바 코드 컨벤션을 위한 세팅
    - [x] 1-1.[gradle-editorconfig](https://naver.github.io/hackday-conventions-java/#editorconfig) 적용
    - [x] 1-2.[gradle-checkstyle](https://naver.github.io/hackday-conventions-java/#checkstyle) 적용
    - [x] 1-3.[IntelliJ](https://naver.github.io/hackday-conventions-java/#_intellij) 적용
    - [x] 1-4.[Github](https://naver.github.io/hackday-conventions-java/#_github) 적용
- [x] 2.학습
    - [x] 2-1.존재 시 추가 작성
- [x] 3.분석 및 설계
    - [x] 3-1.step04.md 초안 작성
    - [x] 3-2.ATDD 작성
- [x] 4.구현
    - [x] 4-1.Scenario: 노선의 중간 구간을 제거한다.
    - [x] 4-2.Scenario: 노선의 상행종점 구간을 제거한다.
    - [x] 4-3.Scenario: 노선의 하행종점 구간을 제거한다.
    - [x] 4-4.Scenario: 구간이 하나인 노선에서 마지막 구간을 제거할 수 없다.
- [x] 5.테스트
    - [x] 5-1.Gradle build Success 확인
    - [x] 5-2.checkstyle 문제없는지 확인 (Java Convention)
    - [x] 5-3.요구사항 조건들 충족했는지 확인
        - [x] 5-3-1.핵심 단위 로직 테스트 
    - [x] 5-4.인수 테스트 확인
    - [x] 5-5.UI 테스트 확인
- [ ] 6.인수인계
    - [ ] 6-1.소감 및 피드백 정리
        - [ ] 6-1-1.느낀점 & 배운점 작성
        - [ ] 6-1-2.피드백 요청 정리
    - [ ] 6-2.코드리뷰 요청 및 피드백
        - [ ] 6-1-1.step4를 gregolee/atdd-subway-admin로 push : `git push origin step4`
        - [ ] 6-1-2.pull request(PR) 작성
    - [ ] 6-3.Slack을 통해 merge가 되는지 확인한 후에 미션 종료

### 2.3. ATDD 작성

ATDD 작성 [Markdown 보기](./atdd.md)

## 3. 인수인계

### 3.1. 느낀점 & 배운점

#### 3.1.1. 느낀점

-   

#### 3.1.2. 배운점

- 

### 3.2. 피드백 요청

- 