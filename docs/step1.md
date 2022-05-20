## 요구사항

### 기능 요구사항
- 지하철역 인수 테스트를 완성하세요.
  - [ ] 지하철역 목록 조회 인수 테스트 작성하기
  - [ ] 지하철역 삭제 인수 테스트 작성하기
  
### 프로그래밍 요구사항
- 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요.
- 각각의 테스트를 동작시키면 잘 동작하지만 한번에 동작시키면 실패할 수 있습니다. 이번 단계에서는 이 부분에 대해 고려하지 말고 각각의 인수 테스트를 작성하는 것에 집중해서 진행하세요.

#### API 명세

![image](https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/8594f161d5be4a869d7397bcdf56d645)

## 힌트

### 인수 테스트 리팩터링

#### JsonPath
- Json 문서를 읽어오는 DSL
- JsonPath를 사용하면 Response Dto 객체로 받을 필요 없이 필요한 값만 추출하여 검증에 사용할 수 있음

![image2](https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/b4cd8f88676f429091a497ae9374e71a)