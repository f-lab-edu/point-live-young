# Database

이 폴더는 프로젝트의 **데이터베이스 설계 및 관련 자료**를 모아둔 공간입니다.  
ERD 다이어그램, 스키마 정의서, 마이그레이션 SQL 등을 포함합니다.

## 구조
- `erd/` : 개념/논리 모델을 표현한 ERD 다이어그램 및 설명
- `schema/` : 실제 DB 테이블 생성 스크립트 및 물리 모델 정의
- `README.md` : 데이터베이스 관련 설명과 문서 가이드

## 작성 규칙
- ERD 변경 시 `erd/`에 다이어그램 이미지와 설명을 업데이트합니다.
- 스키마 정의 변경 시 `schema/`의 SQL 파일 및 문서에 반영합니다.

## 참고
- DBMS: MySQL (예시)
- 표준 스키마 네이밍 규칙: snake_case
- ERD 툴: ERDCloud

---

### ERD Diagram

현재 버전 : [version-001](./erd/erd-version-001.png)