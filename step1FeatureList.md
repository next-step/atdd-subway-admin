## 1단계 ATDD 시나리오 정리

### Feature: 지하철 노선 관리 기능<br>

Scenario: 지하철 노선을 생성<br>
When: 지하철 노선 생성 요청<br>
Then: 지하철 노선 생성 성공<br>

Scenario: 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성<br>
Given: 지하철 노선 등록<br>
When: 지하철 노선 생성 요청<br>
Then: 지하철 노선 생성 실패<br>

Scenario: 지하철 노선 목록을 조회<br>
Given: 지하철 노선 2개 등록<br>
When: 지하철 노선 목록 조회 요청<br>
Then: 지하철 노선 목록 확인<br>

Scenario: 지하철 노선을 조회<br>
Given: 지하철 노선 등록<br>
When: 지하철 노선 조회 요청<br>
And: 없는 id로 조회시 에러 발생<br>
Then: 지하철 노선 확인<br>

Scenario: 지하철 노선을 수정<br>
Given: 지하철 노선 등록<br>
When: 지하철 노선 수정 요청<br>
And: 없는 id로 수정 요청시 에러 발생<br>
Then: 지하철 노선 수정 확인<br>

Scenario: 지하철 노선을 제거<br>
Given: 지하철 노선 등록<br>
When: 지하철 노선 제거 요청<br>
And: 없는 id로 삭제 요청시 에러 발생<br>
Then: 지하철 노선 제거 확인<br>
