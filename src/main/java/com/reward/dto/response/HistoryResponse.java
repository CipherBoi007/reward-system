package com.reward.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponse {
    private Long transactionId;
    private String transactionType;
    private Integer points;
    private Long balanceAfter;
    private LocalDateTime transactionDate;
}