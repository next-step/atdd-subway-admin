# 요구사항
- [X] DTO 수정
- [X] LineResponse에 StationResponse 추가하기 
- [X] Section Entity 추가
    - [X] Station 2개 필요, Up, Down
- [X] Line에 Entity 매핑 및 연관관계 편의메서드 제공


# 요구사항
- [ ] Section 추가하는 API 추가
  - [ ] 예외케이스에 대한 API 추가
    - [ ] 신규 Section 사이에 Station이 존재하며 안된다
    - [ ] 이미 연결된 노선은 추가할 수 없음
      - [ ] A<->B, B<->C => A<->B, B<->C A<->C 추가 불가능
    - [ ] 상행역, 하행역 중 하나라도 노선에 포함이 안되어 있으면 불가능