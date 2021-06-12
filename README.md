## 1단계 요구사항
* 지하철 노선 관련 기능의 인수 테스트 작성
* 지하철 노선 관련 기능 구현
* 인수 테스트 리팩터링

## 2단계 도메인 관계
* Line, Section, Station
* Line:Section = 1:N 
* Section:Station(Up) = 1:1
* Section:Station(Down) = 1:1