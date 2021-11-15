# 🚀 미션 소개 - 지하철 노선도 관리

# 미션 설명

- [미션 수행 방법](https://github.com/next-step/nextstep-docs/blob/master/codereview/review-step1.md) 문서를 참고하여 실습 환경을 구축한다.
    - 저장소: [https://github.com/next-step/atdd-subway-admin](https://github.com/next-step/atdd-subway-admin)
- **지하철 노선도를 관리**할 수 있는 어드민 서비스를 단계별로 구현하세요.
- **`인수 테스트 실습`**은 강의 시간에 진행됩니다. 첫 번째 리뷰 요청은 1단계 미션 구현 후 요청해 주세요.

# 미션 단계

- [X] [**사전 제공**] 지하철 역 관리~~
- [ ] [**1단계**] 지하철 노선 관리
- [ ] [**2단계**] 지하철 노선에 구간 등록
- [ ] [**3단계**] 노선에 등록된 역 목록 조회
- [ ] [**4단계**] 지하철 노선에 구간 제외

## [1단계 - 지하철 노선 관리](./docs/1step.md)

<img src=https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/663eba46f3a140f79c74c567d37e431b>

## 2단계 - 지하철 노선에 구간 등록

## 3단계 - 노선에 등록된 역 목록 조회

## 4단계 - 지하철 노선에 구간 제외

# 도메인 설명

<img src=https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/155885260e20466497cbf3f344cf7a5d>

## 지하철 역(station)

- 지하철 역 속성:
    - 이름(name)

## 지하철 구간(section)

- 지하철 (상행 방향)역과 (하행 방향)역 사이의 연결 정보
- 지하철 구간 속성:
    - 길이(distance)

## 지하철 노선(line)

- 지하철 구간의 모음으로 구간에 포함된 지하철 역의 연결 정보
- 지하철 노선 속성:
    - 노선 이름(name)
    - 노선 색(color)