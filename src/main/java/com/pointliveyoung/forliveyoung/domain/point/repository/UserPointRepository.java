package com.pointliveyoung.forliveyoung.domain.point.repository;

import com.pointliveyoung.forliveyoung.domain.point.entity.UserPointLot;
import com.pointliveyoung.forliveyoung.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserPointRepository extends JpaRepository<UserPointLot, Integer> {

    List<UserPointLot> findAllByUserOrderByCreatedAtDesc(User user);

    @Query("""
            select up
            from UserPointLot up
            where up.user.id = :userId
              and up.status = com.pointliveyoung.forliveyoung.domain.point.entity.Status.ACTIVE
              and (up.expirationAt is null or up.expirationAt > :now)
            order by
              case when up.expirationAt is null then 1 else 0 end,
              up.expirationAt asc,
              up.id asc
            """)
    List<UserPointLot> findActivePointByUser(@Param("userId") Integer userId,
                                             @Param("now") LocalDateTime now);


}
