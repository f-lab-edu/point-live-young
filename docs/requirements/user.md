# User Domain Requirements

---

## 1. 회원가입

- **User Story** : As a new user, I want to sign up, so that I can start using the service
- **Acceptance Criteria**

| **항목** | **내용** |
|------|----------|
| 입력   | 아이디(이메일), 비밀번호, 이름, 닉네임, 생년월일, 성별 |
| 검증   | 이메일 형식 검증, 비밀번호 최소 8자 이상(영문/숫자 포함), 중복 아이디 불가 |
| 동작   | 회원 정보 저장 후 사용자 ID 반환 |
| 포인트  | 회원가입 시 웰컴 포인트 자동 지급 (포인트 도메인 규칙 따름) |
| 예외   | 400(검증 실패), 409(중복 아이디) |

<br/>

---

## 2. 로그인

- **User Story** : As a registered user, I want to log in, so that I can access personalized services
- **Acceptance Criteria**

| **항목**     | **내용** |
|--------------|----------|
| 필수 입력    | 아이디(이메일), 비밀번호 |
| 검증         | 이메일 존재 여부 확인, 비밀번호 일치 여부 검증 |
| 동작         | 로그인 성공 시 세션/토큰 발급 |
| 보안         | 비밀번호는 해시 암호화 저장 |
| 예외         | 400(입력값 없음), 401(인증 실패) |

<br/>

---

## 3. 회원 정보 수정

- **User Story** : As a logged-in user, I want to update my profile information, so that my account stays accurate and up-to-date
- **Acceptance Criteria**

| **항목**     | **내용** |
|--------------|----------|
| 수정 가능 필드 | 닉네임, 비밀번호, 프로필 이미지 |
| 검증         | 닉네임 중복 불가, 비밀번호 규칙 동일 적용 |
| 예외         | 400(검증 실패), 401(비로그인 사용자) |

