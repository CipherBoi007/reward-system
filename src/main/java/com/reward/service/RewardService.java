package com.reward.service;

import com.reward.dto.request.PurchaseRequest;
import com.reward.dto.request.RedeemRequest;
import com.reward.dto.response.HistoryResponse;
import com.reward.dto.response.RewardResponse;

import java.util.List;

public interface RewardService {
    
    RewardResponse processPurchase(PurchaseRequest request);
    
    RewardResponse processRedemption(RedeemRequest request);
    
    List<HistoryResponse> getTransactionHistory(Long customerId, Integer year, Integer month);

    List<HistoryResponse> getAllTransactionHistory(Integer year, Integer month);

}