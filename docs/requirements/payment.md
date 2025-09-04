# Payment Domain Requirements

---

## 1. 결제 내역 조회

- **User Story** : As a logged-in user, I want to view my payment history, so that I can track my purchases
- **Acceptance Criteria**


| **항목**  | **내용** |
| --- | --- |
| 접근 권한 | 로그인 사용자 |
| 데이터 | 결제 일자, 상품명, 가격, 결제 상태(완료/취소), 사용된 포인트 |
| 필터 | 기간 필터, 결제 상태 필터 |
| 예외 | 내역 없을 시 빈 배열 |

<br/>

---

## 2. 포인트 결제로 상품 구매 (여러가지 상품)

- **User Story** : As a logged-in user, I want to buy multiple products using my points, so that I can purchase items without cash
- **Acceptance Criteria**


| **항목**  | **내용** |
| --- | --- |
| 사전 조건 | 로그인 상태, 보유 포인트 ≥ 총 상품 가격, 모든 상품 재고 ≥ 1 |
| 동작 | 구매 요청 시 포인트와 재고 검증 → 포인트 차감 → 재고 차감 → 결제 내역 기록 |
| 트랜잭션 | 포인트 차감/재고 차감/결제 기록을 하나의 트랜잭션 처리 |
| 예외 | 포인트 부족, 재고 부족, 차감/기록 실패 시 전체 롤백 |

<br/>

---
## 3. 결제 취소

- **User Story** : As a logged-in user, I want to cancel my payment, so that I can get my points back if I change my mind within the allowed time
- **Acceptance Criteria**


| **항목** | **내용**                                               |
| --- |------------------------------------------------------|
| 사전 조건 | 결제 상태= 완료, 상품 유효기간 내, 결제 후 24시간 이후                   |
| 동작 | 취소 요청 → 취소 가능 여부 확인 → 재고 복원 → 포인트 복원 → 결제 상태를 취소로 변경 |
| 포인트 복원 규칙 | 포인트 만료일이 지나면 해당 포인트는 복구 불가                           |
| 트랜잭션 | 재고 복원/포인트 복원/결제 상태 변경을 하나의 트랜잭션으로 처리                 |
| 예외 | 이미 취소됨, 포인트 복원 실패시 전체 롤백, 결제일이 24시간 지났을때             |