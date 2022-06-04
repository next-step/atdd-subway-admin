## STEP 1 

------------

### 요구 사항
#### 지하철 역 테스트 
- 기능 요구 사항 
- [ ] 지하철역 목록 조회 인수 테스트 작성하기
- [ ] 지하철역 삭제 인수 테스트 작성하기


- 시나리오 작성
  - 지하철 역 등록 을 하지 않고 조회 시 검색 결과가 없다. 
  - 지하철 역 등록 후 지하철 역 조회 등록 순서 대로 조회 된다.
  - 지하철 역을 등록 한 후 특정 지하철 역을 삭제 하고 조회 시 해당 역을 제외 하고 검색 된다.
  - 지하철 역 삭제 시 해당 역 정보가 없으면 삭제가 되지 않는다.


------------
------------
## STEP 2

--------------

### 요구 사항
#### 지하철 노선 기능
- 기능 요구 사항
  - 인수 조건을 기바능로 지하철 노선 관리 기능을 구현
  - 인수 조건을 검증하는 인수 테스트를 작성

    - 요구 사항
      - 인수 조건
      - 지하철 노선 생성
      ```text
        When 지하철 노선을 생성하면
        Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
      ```
      - 자하철 노선 목록 조회
      ```text
      Given 2개의 지하철 노선을 생성하고
      When 지하철 노선 목록을 조회하면
      Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
      ```
      - 지하철 노선 조회
      ```text
        Given 지하철 노선을 생성하고
        When 생성한 지하철 노선을 조회하면
        Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
      ```
      - 지하철 노선 수정
      ```text
         Given 지하철 노선을 생성하고
         When 생성한 지하철 노선을 수정하면
         Then 해당 지하철 노선 정보는 수정된다
      ```
      - 지하철노선 삭제
      ```text
         Given 지하철 노선을 생성하고
         When 생성한 지하철 노선을 삭제하면
         Then 해당 지하철 노선 정보는 삭제된다
      ```
      
------------------------
-----------------------
## STEP3

_______________________

### 요구사항
- 기능 요구 사항
  - 지하철 구간 추가 기능 
  - 인수 조건을 조출
  - 인수 테스트 
  - 예외 케이스에 대한 검증도 포함

- 프로그래밍 요구 사항
  - 인수 테스트 주도 개발 프로세스 


- API 명세
  ```http request
     POST /lines/1/sections
     content-type: application/json;
     
     {
          "downStationId": "4",
          "upStationId": "2",
          "distance": 10
     }
  ```
- 사용자 요구 사항 정리 
  - 기본 요청에 따른 시나리오 
    - mandatory 값은 distance 이다.
      - 없으면 IllegalArgsException 이 발생
    - downStationId or upStationId 는 optional 이지만 둘중 하나 값을 존재 해야 한다.
      - 하나도 없으면 IllegalArgsException 이 발생 
    - distance 값은 0 이상 이다.
      - 음수 이면 예외 발생 IllegalArgsException 이 발생
    - downStationId 이나 upStationId 입력 시 기존에 등록이 되어 있어야 한다
      - 없는 경우 EntityNotFoundException 이 발생
  
  - 추가 시나리오 
    - 새로운 역이 가운데에 추가되는 경우
      - 기존 distance  > new section 의 distance 인지 확인
        - 아니면 IllegalArgsException 에러 발생
      - 상행 과 일치한 경우 
        - 기존의 upStation 을 new downStation 으로 변경 하고 distance - new distance 값으로 업데이트 
      - 하행과 일치한 경우 
        - 기존의 downStation 을 new upStation 으로 변경 하고  distance - new distance 값으로 업데이트
    
    - 상행 , 하행 종점으로 등록한 경우
      - 그냥 추가 한다.
  
  - 리팩토링
    - 기존의 line 에 upStation, downStation, distance 를 Section 으로 변경