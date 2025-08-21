# Requirements

이 폴더는 프로젝트의 **도메인별 요구사항 정의서**를 모아둔 공간입니다.  
각 문서는 회원, 상품, 포인트, 결제, 이벤트 등 주요 도메인에 대한 기능 요구사항을 설명합니다.

## 구조
- [`user.md`](./user.md) : 회원 도메인 요구사항
- [`product.md`](./product.md) : 상품 도메인 요구사항
- [`point.md`](./point.md) : 포인트 도메인 요구사항
- [`payment.md`](./payment.md) : 결제 도메인 요구사항
- [`event.md`](./event.md) : 이벤트 도메인 요구사항

## 작성 규칙
- 새로운 기능이 추가되면 해당 도메인 문서에 요구사항을 업데이트합니다.
- 문서는 **User Story + Acceptance Criteria** 형식으로 작성합니다.
- 공통 규칙이나 전체적인 요구사항은 최상위 `README.md`에 정리합니다.