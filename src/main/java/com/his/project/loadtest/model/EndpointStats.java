package com.his.project.loadtest.model;

import java.util.ArrayList;
import java.util.List;

public class EndpointStats {
    private long requestCount = 0;
    private long successCount = 0;
    private long failedCount = 0;
    private List<Long> responseTimes = new ArrayList<>();
    
    public void incrementRequest() {
        requestCount++;
    }
    
    public void incrementSuccess() {
        successCount++;
    }
    
    public void incrementFailed() {
        failedCount++;
    }
    
    public void addResponseTime(long responseTime) {
        responseTimes.add(responseTime);
    }
    
    public long getRequestCount() {
        return requestCount;
    }
    
    public long getSuccessCount() {
        return successCount;
    }
    
    public long getFailedCount() {
        return failedCount;
    }
    
    public double getAvgResponseTime() {
        return responseTimes.isEmpty()
            ? 0.0
            : responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
    }
}

