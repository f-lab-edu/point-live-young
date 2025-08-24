# ADR-003: API 응답 구조를 어떻게 할 것인가
---

## Status
---  
Accepted

## Context
---
API를 설계할 때 응답 구조를 통일할 필요성이 있다고 판단했다.
처음에는 `ApiResponse<T>`라는 공통 응답 구조를 정의하고
성공 시 메세지 + 데이터를 함께 응답하는 방식을 고려했다.

```java
public record ApiResponse<T>(String message, T data) {}
```

예시 응답:
```json
{
  "message" : "회원가입 성공",
  "data" : {
    "id" : 1,
    "email" : "test@example.com",
    "name" : "fiat_lux"
  }
}
```

하지만 개발을 진행하다 보니 여러가지의 고민이 생겼다.
- HTTP 상태 코드만으로도 성공 여부를 충분히 표현할 수 있다.
- API마다 메세지를 작성하고 해당 객체를 생성하는 중복 코드가 증가한다.
- 학습 목적의 프로젝트이고 혼자 진행하는 프로젝트라서 굳이 공통 응답 객체를 강제하는 것이 오버 엔지니어링에 가깝다.


## Decision
---
**ApiResponse를 사용하지 않고, API별로 필요한 응답 객체를 직접 반환하기로 결정했다.**

- 정상 응답은 각 API에서 필요한 DTO 또는 엔티티를 그대로 반환
- 성공 시 **HTTP 상태 코드**를 명확하게 설정하여 성공 여부를 표현
- 추후에 사용자한테 명확한 API Response message를 제공 해야 한다면 다시 공통 응답 객체를 도입해야한다.

## Consequences
---

- 단순한 구조
- 중복 코드 감소