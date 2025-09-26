# ADR-013: 상품 검색 API 최적화를 위한 Full-Text Index 도입

## Status

Accepted

## Context

- 서비스의 상품 검색 API는 name, description 칼럼을 대상으로 키워드 검색을 수행한다.
- 데이터셋 규모는 약 50만 개 상품 데이터 기준이다.
- 가존에는 LIKE 쿼리를 사용하여 키워드 검색을 수행했고 평균 응답 속도가 약 1.6초로 측정되었다.
- 응답 속도 개선을 위해 전문 검색 인덱스(Full-Text INDEX) 도입을 고려하고 있다.

**왜 전문 검색 인덱스를 고려하는가?**
- LIKE 검색은 인덱스를 효율적으로 활용하지 못하여 풀 스캔(Full Table Scan)이 발생한다.
- Full-Text Index는 각 단어를 역색인 구조로 관리하여 많은 텍스트 데이터에서도 빠른 키워드 매칭을 지원한다.
- 또 Boolean/Natural Language 모드 같은 다양한 검색 모드를 제공하여 단순 부분 문자열 검색 보다 유연한 검색이 가능하다.

## Decision

- MySQL product 테이블의 product_name, description 칼럼에 FULLTEXT 인덱스를 추가한다.
- 한국어 검색 개선을 위해 MySQL 8.0 이상에서 제공하는 n-gram Full-Text Parser를 사용한다.
```sql
ALTER TABLE `product`
    ADD FULLTEXT KEY `idx_ft_product_name_desc` (`product_name`, `description`) WITH PARSER ngram;
```

- 검색 쿼리는 MATCH...AGAINST 구문을 사용하여 구현한다.
```sql
SELECT * FROM product
WHERE MATCH(product_name, description) AGAINST('검색어' IN NATURAL LANGUAGE MODE);
```

## Consequences
**장점**
- 평균 검색 응답 속도가 약 1.6초에서 -> 약 400ms로 개선되었다.
- Boolean/Natural Language 모드로 다양한 검색 요구사항에 대응할 수 있다.

<br/>

**단점**
- INSERT/UPDATE/DELETE 시 FULLTEXT 인덱스 유지 비용이 발생한다.
- 한국어 형태소 분석의 한계로 인해 검색 정확도가 완벽하지 않을 수 있다.

<br/>

**추후 고려사항**
- Elasticsearch, Solr 같은 전문 검색 엔진 도입 검토
- 사용자 검색 로그 분석을 통한 검색어 추천 기능 추가
