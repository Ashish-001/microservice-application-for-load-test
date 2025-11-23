package com.his.project.loadtest.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestResult {
    private long totalRequests;
    private long successfulRequests;
    private long failedRequests;
    private List<Long> responseTimes;
    private Map<String, EndpointStats> endpointStats = new HashMap<>();
    
    public long getTotalRequests() {
        return totalRequests;
    }
    
    public void setTotalRequests(long totalRequests) {
        this.totalRequests = totalRequests;
    }
    
    public long getSuccessfulRequests() {
        return successfulRequests;
    }
    
    public void setSuccessfulRequests(long successfulRequests) {
        this.successfulRequests = successfulRequests;
    }
    
    public long getFailedRequests() {
        return failedRequests;
    }
    
    public void setFailedRequests(long failedRequests) {
        this.failedRequests = failedRequests;
    }
    
    public double getSuccessRate() {
        if (totalRequests == 0) return 0.0;
        return (successfulRequests * 100.0) / totalRequests;
    }
    
    public List<Long> getResponseTimes() {
        return responseTimes;
    }
    
    public void setResponseTimes(List<Long> responseTimes) {
        this.responseTimes = responseTimes;
    }
    
    public long getMinResponseTime() {
        return responseTimes != null && !responseTimes.isEmpty() 
            ? responseTimes.stream().mapToLong(Long::longValue).min().orElse(0)
            : 0;
    }
    
    public long getMaxResponseTime() {
        return responseTimes != null && !responseTimes.isEmpty()
            ? responseTimes.stream().mapToLong(Long::longValue).max().orElse(0)
            : 0;
    }
    
    public double getAvgResponseTime() {
        return responseTimes != null && !responseTimes.isEmpty()
            ? responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0)
            : 0.0;
    }
    
    public long getMedianResponseTime() {
        if (responseTimes == null || responseTimes.isEmpty()) return 0;
        List<Long> sorted = responseTimes.stream().sorted().toList();
        int middle = sorted.size() / 2;
        return sorted.size() % 2 == 0
            ? (sorted.get(middle - 1) + sorted.get(middle)) / 2
            : sorted.get(middle);
    }
    
    public Map<String, EndpointStats> getEndpointStats() {
        return endpointStats;
    }
    
    public void setEndpointStats(Map<String, EndpointStats> endpointStats) {
        this.endpointStats = endpointStats;
    }
}

