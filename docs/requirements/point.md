# Point Domain Requirements

---

**포인트 사용/취소 시 정합성**

- 포인트 써야할때 어떤 포인트를 우선 쓸건지
- 결제 취소 할때 유효기간이 지났으면 어떻게 할건지
    - 취소하자마자 포인트를 돌려줄지 등등

---

## 1. 출석 포인트 지급

- **User Story** : As a logged-in user, I want to receive daily attendance points, so that I’m rewarded for using the service regularly
- **Acceptance Criteria**


| **항목**  | **내용**                     |
| --- |----------------------------|
| 지급 조건 | 로그인 시 1일 1회만 지급            |
| 포인트 금액 | 100포인트                     |
| 유효기간 | 없음                         |
| 동작 | 로그인 시 자동 지급, 같은 날 중복 지급 차단 |
| 기록 | 지급 내역이 포인트 내역에 기록됨         |
| 예외 | 로그인하지 않은 경우 지급 불가          |

<br/>

---

## 2. 회원가입 포인트 지급

- **User Story** : As a new member, I want to receive welcome points upon registration, so that I can start using the service right away
- **Acceptance Criteria**


| **항목** | **내용**                                  |
| --- |-----------------------------------------|
| 지급 조건 | 최초 가입 1회만 지급                            |
| 포인트 금액 | 5000 포인트                                |
| 유효기간  | 지급일로부터 1개월                              |
| 동작 | 회원가입 완료 시 자동 지급, 중복 가입, 이미 지급된 경우 지급 차단 |
| 기록 | 지급 내역이 포인트 내역에 기록됨                      |
| 만료 처리 | 1개월 경과 시 해당 포인트 만료 상태로 변경               |
| 예외 | 기존 회원 재가입 시 지급 불가                       |

<br/>

---

## 3. 포인트 내역 조회

- **User Story** : As a logged-in user, I want to view my point history, so that I can track my earnings and usage
- **Acceptance Criteria**


| **항목** | **내용** |
| --- | --- |
| 접근 권한 | 로그인 사용자만 |
| 데이터 | 적립/사용/만료 내역,  날짜, 종류(출석, 결제), 유효 금액, 남은 금액, 원래 금액 |
| 필터링 | 날짜 필터 적용 가능 |
| 합계 | 총 보유 포인트 합계 제공 |
| 예외  | 내역이 없을 경우 빈 배열 반환  |

<br/>

---

## 4. 포인트 만료 처리

- **User Story** : As a system, I want to automatically expire points their validity period, so that point balances remain accurate
- **Acceptance Criteria**


| **항목** | **내용** |
| --- | --- |
| 실행 주기 | 매일 00시 (스케줄러) |
| 만료 조건 | 유효기간이 지난 포인트 |
| 동작 | 만료 포인트를 사용불가 상태로 변경, 총 보유 포인트에서 차감 |
| 기록 | 만료 내역이 포인트 내역에 기록됨 |
| 예외 | 만료 대상이 없을 경우 skip |

<br/>

---

## 5.  선착순 포인트 이벤트 참여 (이벤트 도메인으로)

- **User Story** : As a user, I want to participate in a first-come, first-served point event, so that I can get bonus points if I join early
- **Acceptance Criteria**


| **항목** | **조건** |
| --- | --- |
| 참여 조건 | 이벤트 오픈 시간, 참여 인원 ≤ 100명 |
| 포인트 금액 | 3,000 포인트 |
| 유효기간 | 지급일로부터 1주일 |
| 동작 | 참여 가능 여부 확인 (인원, 중복 참여) 후 지급 및 기록 저장 |
| 종료 조건 | 인원 마감 시 자동 종료 |
| 예외 | 마감 후 참여 시도, 중복 참여 시 차단 메세지 반환 |

<br/>

---

### 6. 포인트 충전

---