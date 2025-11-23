package com.his.project.loadtest.model;

public class TestConfig {
    private String gatewayBaseUrl = "http://localhost:9000";
    private int threads = 10;
    private int requestsPerThread = 100;
    private long delayMs = 100;
    private boolean testProductService = true;
    private boolean testOrderService = true;
    private boolean testInventoryService = true;
    
    public String getGatewayBaseUrl() {
        return gatewayBaseUrl;
    }
    
    public void setGatewayBaseUrl(String gatewayBaseUrl) {
        this.gatewayBaseUrl = gatewayBaseUrl;
    }
    
    public int getThreads() {
        return threads;
    }
    
    public void setThreads(int threads) {
        this.threads = threads;
    }
    
    public int getRequestsPerThread() {
        return requestsPerThread;
    }
    
    public void setRequestsPerThread(int requestsPerThread) {
        this.requestsPerThread = requestsPerThread;
    }
    
    public long getDelayMs() {
        return delayMs;
    }
    
    public void setDelayMs(long delayMs) {
        this.delayMs = delayMs;
    }
    
    public boolean isTestProductService() {
        return testProductService;
    }
    
    public void setTestProductService(boolean testProductService) {
        this.testProductService = testProductService;
    }
    
    public boolean isTestOrderService() {
        return testOrderService;
    }
    
    public void setTestOrderService(boolean testOrderService) {
        this.testOrderService = testOrderService;
    }
    
    public boolean isTestInventoryService() {
        return testInventoryService;
    }
    
    public void setTestInventoryService(boolean testInventoryService) {
        this.testInventoryService = testInventoryService;
    }
}

