# 요구사항 
## 1단계 
  - 지하철역 목록 조회 인수 테스트 작성하기
  - 지하철역 삭제 인수 테스트 작성하기
  - 인수 테스트 리팩터링
## 2단계
  - 지하철노선 생성 인수테스트 작성 및 기능 구현
  - 지하철노선 목록조회 인수테스트 작성 및 기능 구현
  - 지하철노선 조회 인수테스트 작성 및 기능 구현
  - 지하철노선 수정 인수테스트 작성 및 기능 구현
  - 지하철노선 삭제 인수테스트 작성 및 기능 구현
## 3단계
  - 구간 추가 인수테스트 작성 및 기능 구현
  - 예외상황도 테스트 할 것 
## 4단계 
  - 구간 제거 인수테스트 작성 및 기능 구현 
  - 예외상황도 테스트 할 것
    
# 작업 로그 
## 1단계 
  - [X] 지하철역 목록 조회 인수 테스트 작성하기
  - [X] 지하철역 삭제 인수 테스트 작성하기
  - [X] 인수 테스트 리팩터링

## 2단계
  - [X] 인수테스트 작성
    - [X] 지하철노선 생성
    - [X] 지하철노선 목록조회
    - [X] 지하철노선 조회
    - [X] 지하철노선 수정
    - [X] 지하철노선 삭제

  - [X] 기능구현 
    - [X] 지하철노선 생성
    - [X] 지하철노선 목록조회
    - [X] 지하철노선 조회
    - [X] 지하철노선 수정
    - [X] 지하철노선 삭제

## 3단계
  - [X] 요구사항을 충족시키기 위해 DB스키마 변경 
    - [X] Section 엔티티 생성 및 연관관계 매핑 (https://ict-nroo.tistory.com/127)
    - [X] Sections 일급 컬렉션 작성 
  - [X] 구간 추가 인수테스트 작성
  - [X] 구간 추가 기능구현
  - [X] 예외 상황에 대한 인수테스트 추가 작성 

## 4단계
  - [X] 구간 제거 인수테스트 작성
  - [X] 구간 제거 기능구현
  - [X] 예외 상황에 대한 인수테스트 추가 작성


# 리뷰 내용 반영
## 2단계 리뷰
  - [X] Setting > Editor > Global > Ensure every saved file ends with a line break 설정
  - [X] @JsonIgnoreProperties의 ignoreUnknown 속성 제거 
  - [X] LineResponse 작성 및 코드 리팩토링
    - [X] Line.distance 제거
    - [X] Line 엔티티의 @JsonIgnoreProperties 제거
  - [X] 단위테스트 실행 속도 개선
    - @DirtiesContext는 Spring Application 전체를 reload하는 거라 시간이 오래 걸림  
    - 필요한 부분은 Database의 초기화
    - @DirtiesContext, @Sql은 모두 org.springframework.test.context.TestExecutionListener기반으로 구현되어있음
    - 모든 테이블 및 시퀀스를 초기화하는 TestExecutionListener 구현체 작성 
    - 참고자료 : https://stackoverflow.com/questions/56712707/springboottest-vs-contextconfiguration-vs-import-in-spring-boot-unit-test

## 3단계 리뷰
  - [X] 구간 추가시 응답으로 Line을 직접 return 하지 않도록 수정 
  - [X] LineResponse 생성자 접근 제한
  - [X] Stream API 사용한 부분 줄 나누기
