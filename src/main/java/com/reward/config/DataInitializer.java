package com.reward.config;

import com.reward.entity.Customer;
import com.reward.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    
    @Bean
    @Profile("!test")
    public CommandLineRunner initializeData(CustomerRepository customerRepository) {
        return args -> {
            if (customerRepository.count() == 0) {
                log.info("Initializing sample customer data");
                
                Customer customer1 = Customer.builder()
                        .name("John Doe")
                        .totalPoints(500L)
                        .isActive(true)
                        .build();
                
                Customer customer2 = Customer.builder()
                        .name("Jane Smith")
                        .totalPoints(750L)
                        .isActive(true)
                        .build();
                
                customerRepository.save(customer1);
                customerRepository.save(customer2);
                
                log.info("Sample customers created successfully");
            }
        };
    }
}