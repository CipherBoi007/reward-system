package com.reward.service.impl;

import com.reward.dto.request.PurchaseRequest;
import com.reward.dto.request.RedeemRequest;
import com.reward.dto.response.HistoryResponse;
import com.reward.dto.response.RewardResponse;
import com.reward.entity.Customer;
import com.reward.entity.Purchase;
import com.reward.entity.RewardTransaction;
import com.reward.entity.enums.TransactionType;
import com.reward.exception.BusinessException;
import com.reward.exception.ResourceNotFoundException;
import com.reward.repository.CustomerRepository;
import com.reward.repository.PurchaseRepository;
import com.reward.repository.RewardTransactionRepository;
import com.reward.service.RewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardServiceImpl implements RewardService {
    
    private final CustomerRepository customerRepository;
    private final PurchaseRepository purchaseRepository;
    private final RewardTransactionRepository transactionRepository;
    
    private static final int BONUS_THRESHOLD = 1000;
    private static final int BONUS_POINTS = 5;
    private static final int POINTS_PER_HUNDRED = 100;
    
    @Override
    @Transactional
    public RewardResponse processPurchase(PurchaseRequest request) {
        log.debug("Processing purchase for customer ID: {}, amount: {}", 
                  request.getCustomerId(), request.getBillAmount());
        
        Customer customer = customerRepository.findByCustomerIdAndIsActiveTrue(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer", "customerId", request.getCustomerId()));
        
        int earnedPoints = calculateEarnedPoints(request.getBillAmount());
        
        Purchase purchase = Purchase.builder()
                .customerId(customer.getCustomerId())
                .billAmount(request.getBillAmount())
                .earnedPoints(earnedPoints)
                .purchaseDate(LocalDateTime.now())
                .build();
        
        purchaseRepository.save(purchase);
        
        Long newBalance = customer.getTotalPoints() + earnedPoints;
        customer.setTotalPoints(newBalance);
        customerRepository.save(customer);
        
        RewardTransaction transaction = RewardTransaction.builder()
                .customerId(customer.getCustomerId())
                .type(TransactionType.EARN)
                .points(earnedPoints)
                .transactionDate(LocalDateTime.now())
                .pointsBalanceAfter(newBalance)
                .build();
        
        transactionRepository.save(transaction);
        
        return RewardResponse.builder()
                .transactionId(transaction.getTransactionId())
                .customerId(customer.getCustomerId())
                .transactionType(TransactionType.EARN.name())
                .points(earnedPoints)
                .balanceAfter(newBalance)
                .transactionDate(transaction.getTransactionDate())
                .message(String.format("Successfully earned %d points", earnedPoints))
                .build();
    }
    
    @Override
    @Transactional
    public RewardResponse processRedemption(RedeemRequest request) {
        log.debug("Processing redemption for customer ID: {}, points: {}", 
                  request.getCustomerId(), request.getPoints());
        
        Customer customer = customerRepository.findByCustomerIdAndIsActiveTrue(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer", "customerId", request.getCustomerId()));
        
        if (customer.getTotalPoints() < request.getPoints()) {
            throw new BusinessException(
                    String.format("Insufficient points. Available: %d, Requested: %d", 
                                  customer.getTotalPoints(), request.getPoints()));
        }
        
        Long newBalance = customer.getTotalPoints() - request.getPoints();
        customer.setTotalPoints(newBalance);
        customerRepository.save(customer);
        
        RewardTransaction transaction = RewardTransaction.builder()
                .customerId(customer.getCustomerId())
                .type(TransactionType.REDEEM)
                .points(request.getPoints())
                .transactionDate(LocalDateTime.now())
                .pointsBalanceAfter(newBalance)
                .build();
        
        transactionRepository.save(transaction);
        
        return RewardResponse.builder()
                .transactionId(transaction.getTransactionId())
                .customerId(customer.getCustomerId())
                .transactionType(TransactionType.REDEEM.name())
                .points(request.getPoints())
                .balanceAfter(newBalance)
                .transactionDate(transaction.getTransactionDate())
                .message(String.format("Successfully redeemed %d points", request.getPoints()))
                .build();
    }
    
    @Override
    public List<HistoryResponse> getTransactionHistory(Long customerId, Integer year, Integer month) {
        log.debug("Fetching transaction history for customer ID: {}, year: {}, month: {}", 
                  customerId, year, month);
        
        if (!customerRepository.existsByCustomerIdAndIsActiveTrue(customerId)) {
            throw new ResourceNotFoundException("Customer", "customerId", customerId);
        }
        
        LocalDateTime now = LocalDateTime.now();
        int queryYear = year != null ? year : now.getYear();
        int queryMonth = month != null ? month : now.getMonthValue();
        
        List<RewardTransaction> transactions = transactionRepository
                .findByCustomerIdAndYearAndMonthOrderByTransactionDateAsc(customerId, queryYear, queryMonth);
        
        return transactions.stream()
                .map(this::mapToHistoryResponse)
                .collect(Collectors.toList());
    }
    
    // ✅ NEW: Get ALL transactions for a given month/year (global history)
    @Override
    public List<HistoryResponse> getAllTransactionHistory(Integer year, Integer month) {
        log.debug("Fetching ALL transaction history for year: {}, month: {}", year, month);
        
        LocalDateTime now = LocalDateTime.now();
        int queryYear = year != null ? year : now.getYear();
        int queryMonth = month != null ? month : now.getMonthValue();
        
        List<RewardTransaction> transactions = transactionRepository
                .findByYearAndMonthOrderByTransactionDateAsc(queryYear, queryMonth);
        
        return transactions.stream()
                .map(this::mapToHistoryResponse)
                .collect(Collectors.toList());
    }
    
    private int calculateEarnedPoints(Double billAmount) {
        int basePoints = (int) (billAmount / POINTS_PER_HUNDRED);
        int bonusPoints = billAmount > BONUS_THRESHOLD ? BONUS_POINTS : 0;
        return basePoints + bonusPoints;
    }
    
    private HistoryResponse mapToHistoryResponse(RewardTransaction transaction) {
        return HistoryResponse.builder()
                .transactionId(transaction.getTransactionId())
                .transactionType(transaction.getType().name())
                .points(transaction.getPoints())
                .balanceAfter(transaction.getPointsBalanceAfter())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }
}