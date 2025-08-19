# Infrastructure Architecture

이 폴더는 프로젝트의 **인프라 아키텍처 설계 자료**를 모아둔 공간입니다.  
배포 구조, 서버 구성, 데이터베이스, 캐시, 스토리지 등 운영 인프라 관련 다이어그램을 포함합니다.

---

## 구조
- `deployment-diagram/` : 서비스 배포 구조 (예: 클라우드 환경, VPC, 서브넷, 로드밸런서 등)
- `infrastructure-diagram/` : 서버, DB, 캐시, 스토리지 등 인프라 구성도
- `README.md` : 인프라 아키텍처 문서에 대한 설명

## 작성 규칙
- 다이어그램 변경 시 반드시 최신 상태로 유지합니다.
- 다이어그램은 PNG/JPG와 함께 원본 파일(Draw.io, Lucidchart, PlantUML 등)도 관리합니다.
- 보안 이슈가 될 수 있는 민감 정보(IP, 계정 등)는 포함하지 않습니다.
