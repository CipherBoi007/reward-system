package com.reward.controller;

import com.reward.dto.request.PurchaseRequest;
import com.reward.dto.request.RedeemRequest;
import com.reward.dto.response.HistoryResponse;
import com.reward.dto.response.RewardResponse;
import com.reward.service.RewardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
@Validated
public class RewardController {
    
    private final RewardService rewardService;
    
    @PostMapping("/purchase")
    public ResponseEntity<RewardResponse> processPurchase(@Valid @RequestBody PurchaseRequest request) {
        RewardResponse response = rewardService.processPurchase(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping("/redeem")
    public ResponseEntity<RewardResponse> redeemPoints(@Valid @RequestBody RedeemRequest request) {
        RewardResponse response = rewardService.processRedemption(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/customer/{customerId}/history")
    public ResponseEntity<List<HistoryResponse>> getCustomerHistory(
            @PathVariable @Min(value = 1, message = "Customer ID must be positive") Long customerId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        List<HistoryResponse> history = rewardService.getTransactionHistory(customerId, year, month);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<HistoryResponse>> getAllHistory(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        List<HistoryResponse> history = rewardService.getAllTransactionHistory(year, month);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

}