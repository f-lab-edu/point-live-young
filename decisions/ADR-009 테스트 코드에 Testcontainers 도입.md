# ADR-009 테스트 코드에 Testcontainers 도입
---

## Status
---  
Accepted

## Context
---
우리 서비스에서 단순한 CRUD 검증과 QueryDSL 기반 검색 로직과 동시성 로직을 다룬다

기존 문제
- H2와 같은 In-memory DB는 MySQL과 Dialect, 제약조건 처리 방식이 다르다
- 동시성 테스트에서는 H2 환경에서는 락 동작이나 트랜잭션 처리 방식이 MySQL 과 달라서 오버셀 상황을 제대로 재현하기 어렵다고 판단한다

단순한 Mock/H2 테스트로는 서비스 품질을 담보하기 어렵다고 판단한다



## Decision
---

테스트 환경에 Testcontainers를 도입한다
- @DynamicPropertySource로 동적으로 DB 접속 정보를 주입하고, ddl-auto=create-drop으로 매 실행마다 스키마를 초기화한다
- Repository(QueryDSL) 테스트와 Order 동시성 테스트를 실제 MySQL 환경에서 실행해, 운영 상황과 최대한 유사하게 시뮬레이션한다

## Consequences
---

**장점**
- QueryDSL 쿼리와 동시성 로직을 실제 MySQL에서 실행·검증 가능
- 상품 검색, 주문·취소 동시성 시나리오 같은 비즈니스 로직을 실제 환경과 동일하게 테스트할 수 있다

**단점**
- Testcontainers 자체에 대한 러닝커브가 있으며 설정을 잘못하면 테스트 실행이 불안정해질 수 있다