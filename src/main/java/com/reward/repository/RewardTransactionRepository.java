package com.reward.repository;

import com.reward.entity.RewardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardTransactionRepository extends JpaRepository<RewardTransaction, Long> {
    
    @Query("SELECT rt FROM RewardTransaction rt WHERE rt.customerId = :customerId " +
           "AND YEAR(rt.transactionDate) = :year AND MONTH(rt.transactionDate) = :month " +
           "ORDER BY rt.transactionDate ASC")
    List<RewardTransaction> findByCustomerIdAndYearAndMonthOrderByTransactionDateAsc(
            @Param("customerId") Long customerId,
            @Param("year") int year,
            @Param("month") int month);

       @Query("SELECT rt FROM RewardTransaction rt " +
           "WHERE YEAR(rt.transactionDate) = :year AND MONTH(rt.transactionDate) = :month " +
           "ORDER BY rt.transactionDate ASC")
    List<RewardTransaction> findByYearAndMonthOrderByTransactionDateAsc(
            @Param("year") int year,
            @Param("month") int month);
}

