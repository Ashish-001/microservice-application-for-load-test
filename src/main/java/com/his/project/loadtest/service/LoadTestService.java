package com.his.project.loadtest.service;

import com.his.project.loadtest.client.ApiClient;
import com.his.project.loadtest.model.EndpointStats;
import com.his.project.loadtest.model.TestConfig;
import com.his.project.loadtest.model.TestResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class LoadTestService {
    private final TestConfig config;
    private final ApiClient apiClient;
    private final ExecutorService executorService;
    
    public LoadTestService(TestConfig config) {
        this.config = config;
        this.apiClient = new ApiClient(config.getGatewayBaseUrl());
        this.executorService = Executors.newFixedThreadPool(config.getThreads());
    }
    
    public TestResult runLoadTest() {
        TestResult result = new TestResult();
        List<Long> allResponseTimes = new ArrayList<>();
        Map<String, EndpointStats> endpointStats = result.getEndpointStats();
        AtomicLong totalRequests = new AtomicLong(0);
        AtomicLong successfulRequests = new AtomicLong(0);
        AtomicLong failedRequests = new AtomicLong(0);
        
        List<Future<?>> futures = new ArrayList<>();
        
        // Create tasks for each thread
        for (int i = 0; i < config.getThreads(); i++) {
            final int threadId = i;
            Future<?> future = executorService.submit(() -> {
                for (int j = 0; j < config.getRequestsPerThread(); j++) {
                    try {
                        // Test Product Service - GET
                        if (config.isTestProductService()) {
                            testEndpoint("GET /api/product", () -> {
                                long start = System.currentTimeMillis();
                                boolean success = apiClient.getProducts();
                                long responseTime = System.currentTimeMillis() - start;
                                synchronized (allResponseTimes) {
                                    allResponseTimes.add(responseTime);
                                }
                                updateStats(endpointStats, "GET /api/product", success, responseTime, 
                                    totalRequests, successfulRequests, failedRequests);
                                return responseTime;
                            });
                        }
                        
                        // Test Product Service - POST
                        if (config.isTestProductService()) {
                            testEndpoint("POST /api/product", () -> {
                                long start = System.currentTimeMillis();
                                boolean success = apiClient.createProduct();
                                long responseTime = System.currentTimeMillis() - start;
                                synchronized (allResponseTimes) {
                                    allResponseTimes.add(responseTime);
                                }
                                updateStats(endpointStats, "POST /api/product", success, responseTime,
                                    totalRequests, successfulRequests, failedRequests);
                                return responseTime;
                            });
                        }
                        
                        // Test Inventory Service - GET
                        if (config.isTestInventoryService()) {
                            testEndpoint("GET /api/inventory", () -> {
                                long start = System.currentTimeMillis();
                                boolean success = apiClient.checkInventory();
                                long responseTime = System.currentTimeMillis() - start;
                                synchronized (allResponseTimes) {
                                    allResponseTimes.add(responseTime);
                                }
                                updateStats(endpointStats, "GET /api/inventory", success, responseTime,
                                    totalRequests, successfulRequests, failedRequests);
                                return responseTime;
                            });
                        }
                        
                        // Test Order Service - POST
                        if (config.isTestOrderService()) {
                            testEndpoint("POST /api/order", () -> {
                                long start = System.currentTimeMillis();
                                boolean success = apiClient.placeOrder();
                                long responseTime = System.currentTimeMillis() - start;
                                synchronized (allResponseTimes) {
                                    allResponseTimes.add(responseTime);
                                }
                                updateStats(endpointStats, "POST /api/order", success, responseTime,
                                    totalRequests, successfulRequests, failedRequests);
                                return responseTime;
                            });
                        }
                        
                        // Delay between requests
                        if (config.getDelayMs() > 0) {
                            Thread.sleep(config.getDelayMs());
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        System.err.println("Error in thread " + threadId + ": " + e.getMessage());
                    }
                }
            });
            futures.add(future);
        }
        
        // Wait for all threads to complete
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                System.err.println("Error waiting for thread: " + e.getMessage());
            }
        }
        
        executorService.shutdown();
        
        result.setTotalRequests(totalRequests.get());
        result.setSuccessfulRequests(successfulRequests.get());
        result.setFailedRequests(failedRequests.get());
        result.setResponseTimes(allResponseTimes);
        
        return result;
    }
    
    private void testEndpoint(String endpointName, Callable<Long> test) {
        try {
            test.call();
        } catch (Exception e) {
            System.err.println("Error testing " + endpointName + ": " + e.getMessage());
        }
    }
    
    private void updateStats(Map<String, EndpointStats> endpointStats, String endpoint,
                           boolean success, long responseTime,
                           AtomicLong totalRequests, AtomicLong successfulRequests, AtomicLong failedRequests) {
        endpointStats.computeIfAbsent(endpoint, k -> new EndpointStats()).incrementRequest();
        endpointStats.get(endpoint).addResponseTime(responseTime);
        
        totalRequests.incrementAndGet();
        if (success) {
            endpointStats.get(endpoint).incrementSuccess();
            successfulRequests.incrementAndGet();
        } else {
            endpointStats.get(endpoint).incrementFailed();
            failedRequests.incrementAndGet();
        }
    }
}

