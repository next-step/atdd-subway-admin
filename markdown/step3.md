# 요구사항
- [ ] 기능 구현 전 인수 테스트 작성
- [ ] 예외 케이스 처리 인수 테스트 작성

# 요구사항 설명
### 역 사이에 새로운 역을 등록할 경우
- 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정

### 새로운 역을 상행 종점으로 등록할 경우

### 새로운 역을 하행 종점으로 등록할 경우

## 구간 등록 시 예외 케이스를 고려하기
### 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음

### 상행선역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음

### 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

# JPA 관계 맵핑
- 지하철역은 여러개의 지하철 노선에 포함될 수 있다.
    - ex) 강남역은 2호선에 등록되어 있는 동시에 신분당선에 등록되어 있음
- 따라서 다대다 관계로 보아 @ManyToMany로 관계를 맺을 수 있음
- 하지만 다대다 관계는 여러가지 예상치 못한 문제를 발생시킬 수 있어 추천하지 않음
    - https://ict-nroo.tistory.com/127 블로그를 참고하세요
- 지하철역과 지하철 노선의 맵핑 테이블을 엔티티로 두는 방법을 추천
    - 기존에 Station과 Line이 있었다면 Line에 속하는 Station을 LineStation이라는 엔티티로 도출
    - Line과 LineStation을 @ManyToOne 관계로 설정
- 참고할 코드:
  https://github.com/next-step/atdd-subway-map/blob/boorownie/src/main/java/nextstep/subway/line/domain/LineStations.java
- 참고한 코드에서는 LineStation을 일급컬렉션을 묶어 LineStations로 둠
- JPA @Embedded And @Embeddable을 참고하세요.