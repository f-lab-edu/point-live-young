# Product Domain Requirements

---

### 1. CRUD

- **상품 등록 (관리자)**
    - **User Story** : As an admin, I want to create products, so that they are available for sale
    - **Acceptance Criteria**

| **항목** | **내용** |
| --- | --- |
| 필수 입력 | 상품명, 설명, 가격, 재고, 유효기간, 카테고리, 이미지, 상품 코드 |
| 권한 | 관리자만 |
| 검증 | 가격 > 0, 재고 > 0, 유효기간 ≥ 오늘 |
| 동작 | 성공시 productId 반환, 이미지 업로드 실패시 롤백 |
| 예외 | 400(검증 실패), 403(권한 없음) |

<br/>

- **수정**
    - **User Story** : As an admin, I want to edit product fields, so that product information stays up-to-date
    - **Acceptance Criteria**

| **항목** | **내용** |
| --- | --- |
| 수정 가능 필드 | 이름, 설명, 가격, 재고, 유효기간, 이미지 |
| 권한 | 관리자만  |
| 동작 | 부분 수정 가능 |
| 검증 | 가격 > 0, 재고 > 0, 유효기간 ≥ 오늘 |
| 예외 | 400, 403, 404 |

<br/>

- **삭제**
    - **User Story** : As an admin, I want to delete products, so that discontinued or expired items are no longer shown
    - **Acceptance Criteria**

| **항목** | **내용** |
| --- | --- |
| 권한 | 관리자만 가능 |
| 방식 | soft delete |
| 정책 | 유효기간 경과 시 자동 삭제, 관리자 직접 삭제, 재고 0 일시 자동 삭제 |
| 예외 | 403, 404 |

<br/>

- **상세 조회**
    - **User Story** : As a user, I want to view product details, so that I can decide whether to purchase
    - **Acceptance Criteria**

| **항목** | **내용** |
| --- | --- |
| 포함 데이터 | 상품 기본 정보, 이미지 목록, 재고, 유효기간, 가격, 이벤트 가격 |
| 조건 | 삭제/만료 상품, 재고 0인 상품은 노출 X |
| 예외 | 404 |

<br/>

---

### 2. 검색 기능

- **User Story** : As a user, I want to search and filter products, so that I can quickly find what I’m looking for
- **Acceptance Criteria**

| **항목** | **내용** |
| --- | --- |
| 검색 조건 | 키워드(제목), 카테고리, 가격 범위(min ~ max) |
| 정렬 | 최신순, 가격 오름/내림 |
| 페이징 | offset/limit |
| 예외 | 400 (가격 범위 오류) |

<br/>

---

### 3. 이벤트 상품 노출 (이벤트 도메인)

- **User Story** : As a user, I want to view limited-time discounted products, so that I’m motivated to purchase during event periods
- **Acceptance Criteria**

| **항목** | **내용** |
| --- | --- |
| 시간 조건 | 매일 16:00 ~ 17:00 |
| 선정 조건 | 가격 ≥ 30,000, 재고 ≥ 1, 유효기간 3일 이내 |
| 정책 | 매일 랜덤 20개, 20% 할인 |
| 예외 | 시간 외 요청 시 204 |