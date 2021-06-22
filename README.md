## 1단계 요구사항
* 지하철 노선 관련 기능의 인수 테스트 작성
* 지하철 노선 관련 기능 구현
* 인수 테스트 리팩터링

## 2단계 도메인 관계
* Line, Section, Station
* Line:Section = 1:N 
* Section:Station(Up) = 1:1
* Section:Station(Down) = 1:1

## 3단계 구간 추가 기능
* 노선과 지하철역 맵핑을 위한 엔티티 추가 (보류)
  * LineStation
    * 노선에 포함되는 지하철역 리스트
    * Line 은 LineStation 을 OneToMany 로
    * Station 은 LineStation 을 OneToMany 로
* 테스트 코드 변경
    * SectionAcceptanceTest
        * 섹션 자체 추가 테스트
    * LineAcceptanceTest
        * 라인에 섹션 추가 테스트
* 서비스 코드 변경
  * Line Controller를 통해 Line에 구간(Section)  추가 등록
    * addSection(LineId, sectionRequest) : 노선에 새로운 역(downStation, upStation) 추가
      * section 추가 요청이 들어오면,
      * upStation 역이 있으면, 기존 구간과 새롭게 추가될 구간의 downStation 비교(이름, 거리)해서 추가를 결정
        * 추가할 수 있으면, 기존 구간 정보 변경, 추가 요청 구간 리스트에 반영
        * 추가할 수 없으면, 변경없음
      * downStation 역이 있으면, 기존 구간과 새롭게 추가될 구간의 upStation 비교(이름, 거리)해서 추가를 결정
        * 추가할 수 있으면, 기존 구간 정보 변경, 추가 요청 구간 리스트에 반영
        * 추가할 수 없으면, 변경없음
      * downStation, upStation 역이 모두 없으면 추가 못함  
  * LineStation이 꼭 필요한지 검토???
