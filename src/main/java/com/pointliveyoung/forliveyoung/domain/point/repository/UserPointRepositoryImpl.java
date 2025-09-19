package com.pointliveyoung.forliveyoung.domain.point.repository;

import com.pointliveyoung.forliveyoung.domain.point.entity.QUserPointLot;
import com.pointliveyoung.forliveyoung.domain.point.entity.Status;
import com.pointliveyoung.forliveyoung.domain.point.entity.UserPointLot;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void updateStatusToExpiredByUser(Integer userId, LocalDateTime now) {
        QUserPointLot userPointLot = QUserPointLot.userPointLot;

        BooleanBuilder whereQuery = new BooleanBuilder()
                .and(userPointLot.user.id.eq(userId))
                .and(userPointLot.status.eq(Status.ACTIVE))
                .and(userPointLot.expirationAt.isNotNull())
                .and(userPointLot.expirationAt.loe(now));

        jpaQueryFactory
                .update(userPointLot)
                .set(userPointLot.status, Status.EXPIRED)
                .where(whereQuery)
                .execute();
    }

    @Override
    public List<UserPointLot> findPointsByUser(Integer userId, boolean activeOnly, LocalDateTime now) {
        QUserPointLot userPointLot = QUserPointLot.userPointLot;

        BooleanBuilder whereQuery = new BooleanBuilder().and(userPointLot.user.id.eq(userId));

        if (activeOnly) {
            whereQuery.and(userPointLot.status.eq(Status.ACTIVE))
                    .and(userPointLot.isNull().or(userPointLot.expirationAt.gt(now)));
        }

        OrderSpecifier<?>[] orderSpecifiers;

        if (activeOnly) {
            orderSpecifiers = new OrderSpecifier[]{
                    new CaseBuilder()
                            .when(userPointLot.expirationAt.isNull()).then(1)
                            .otherwise(0).asc(),
                    userPointLot.expirationAt.asc(),
                    userPointLot.id.asc()
            };
        } else {
            orderSpecifiers = new OrderSpecifier[]{
                    userPointLot.createdAt.desc()
            };
        }

        return jpaQueryFactory
                .selectFrom(userPointLot)
                .where(whereQuery)
                .orderBy(orderSpecifiers)
                .fetch();
    }
}
