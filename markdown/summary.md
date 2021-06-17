# ATDD

> 우아한테크캠프 Pro 2기 내용을 정리했습니다.
> - 저작권에 따라 문제가 되는 사항이 있다면 연락주시면 해당 내용을 변경, 제거토록 하겠습니다.

## 1. 개요

### 1.1. TDD Cycle

![TDD-Cycle image](../documents/summary/TDD-cycle.png)

- TDD Cycle 진행 어려움
    - 내가 어디쯤 진행하고 있고, 잘하고 있는지 의문이 든다.
- 나무만 보고 숲을 보지 못하는 경우가 발생한다.

### 1.2. 인수테스트 주도 개발(ATDD)

> TDD의 단점을 보완하기 위해 인수 테스트(acceptance test)를 먼저 구현한 후 이후 단위 테스트를 통해 기능을 완성해 가는 과정으로 애플리케이션을 개발

![ATDD-introduction](../documents/summary/ATDD-introduction.png)

- 다양한 관점을 가진 팀원(기획, 개발, 테스터 등)들과 협업을 위함
- 위 그림 중 두번째 그림처럼 커뮤니케이션 없이 진행하다보면 결과물이 나온 시점에서 결과물이 다르게 나올 수 있다.
- ATDD는 이러한 리스크를 사전에 방지하고, 기획 단계부터 인수 테스트(acceptance test)를 통해 공통의 이해를 도모하여 프로젝트를 진행한다.

### 1.3. ATDD Cycle

> [Acceptance Test Driven Development](https://mysoftwarequality.wordpress.com/2013/11/12/when-something-works-share-it/)

![ATDD-Cycle image](../documents/summary/ATDD-cycle.png)
![ATDD-Cycle image-1](../documents/summary/ATDD-cycle-1.png)

### 1.4. ATDD Process

![ATDD-process image](../documents/summary/ATDD-process.png)

#### 1.4.1. 요구사항(인수 조건)

> [Acceptance Criteria: Purposes, Formats, and Best Practices](https://www.altexsoft.com/blog/business/acceptance-criteria-purposes-formats-and-best-practices/)

- 인수테스트(acceptance test)가 충족해야할 조건
    - 기술 용어가 사용되지 않고 일반 사용자들이 이해할 수 있는 단어를 사용한다.
- 인수 조건을 표현하는 것은 다음과 같이 있다.
    - 시나리오 : scenario-oriented (the Given/When/Then template) 
    - 룰 : rule-oriented (the checklist template)
    - 커스텀 : custom formats
  
#### 1.4.2. 인수테스트(acceptance test)

- 사용자의 관점에서 올바르게 작동하는지 확인하는 테스트

#### 1.4.3. 문서화

- API 문서화 (Spring Rest Docs 등)
  - 문서화를 위해서 Mock 서버 및 DTO 정의가 필요하다.
- 다른 개발자들과 협업 시 의사소통에 큰 장점
  - Front-end, Back-end 개발자들 간 작업하는데 유리하다.

#### 1.4.4. TDD(기능 구현)

- 인수테스트 기반으로 기능 구현함

## 2. 인수 테스트 도구

### 2.1. 인수 테스트(acceptance test)

#### 2.1.1. 특징

- 전구간 테스트 : 요청/응답 기준으로 전 구간을 검증한다.
- Black Box 테스트 : 세부 구현에 영향을 받지 않게 구현.

#### 2.1.2. 인수 테스트 vs. 통합 테스트
|     |인수테스트|통합테스트|
|:---:| :---: | :---: |
|대상  |해당 시스템 전체|통합 모듈|
|어떻게 |Black Box|Gray Box|