package com.his.project.loadtest;

import com.his.project.loadtest.client.ApiClient;
import com.his.project.loadtest.model.TestConfig;
import com.his.project.loadtest.model.TestResult;
import com.his.project.loadtest.service.LoadTestService;

import java.util.Scanner;

public class LoadTestRunner {
    
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  Microservices Load Testing Tool");
        System.out.println("==========================================\n");
        
        Scanner scanner = new Scanner(System.in);
        
        // Get configuration
        TestConfig config = getTestConfiguration(scanner);
        
        System.out.println("\nStarting load test...");
        System.out.println("Press Ctrl+C to stop early\n");
        
        LoadTestService loadTestService = new LoadTestService(config);
        TestResult result = loadTestService.runLoadTest();
        
        // Print results
        printResults(result);
        
        scanner.close();
    }
    
    private static TestConfig getTestConfiguration(Scanner scanner) {
        TestConfig config = new TestConfig();
        
        System.out.println("Configuration:");
        System.out.println("--------------");
        
        System.out.print("API Gateway Base URL [http://localhost:9000]: ");
        String gatewayUrl = scanner.nextLine().trim();
        config.setGatewayBaseUrl(gatewayUrl.isEmpty() ? "http://localhost:9000" : gatewayUrl);
        
        System.out.print("Number of threads (concurrent users) [10]: ");
        String threadsStr = scanner.nextLine().trim();
        config.setThreads(threadsStr.isEmpty() ? 10 : Integer.parseInt(threadsStr));
        
        System.out.print("Number of requests per thread [100]: ");
        String requestsStr = scanner.nextLine().trim();
        config.setRequestsPerThread(requestsStr.isEmpty() ? 100 : Integer.parseInt(requestsStr));
        
        System.out.print("Delay between requests (ms) [100]: ");
        String delayStr = scanner.nextLine().trim();
        config.setDelayMs(delayStr.isEmpty() ? 100 : Long.parseLong(delayStr));
        
        System.out.print("Test Product Service? [Y/n]: ");
        String testProduct = scanner.nextLine().trim();
        config.setTestProductService(!testProduct.equalsIgnoreCase("n"));
        
        System.out.print("Test Order Service? [Y/n]: ");
        String testOrder = scanner.nextLine().trim();
        config.setTestOrderService(!testOrder.equalsIgnoreCase("n"));
        
        System.out.print("Test Inventory Service? [Y/n]: ");
        String testInventory = scanner.nextLine().trim();
        config.setTestInventoryService(!testInventory.equalsIgnoreCase("n"));
        
        return config;
    }
    
    private static void printResults(TestResult result) {
        System.out.println("\n==========================================");
        System.out.println("  Load Test Results");
        System.out.println("==========================================\n");
        
        System.out.println("Total Requests: " + result.getTotalRequests());
        System.out.println("Successful: " + result.getSuccessfulRequests());
        System.out.println("Failed: " + result.getFailedRequests());
        System.out.println("Success Rate: " + String.format("%.2f%%", result.getSuccessRate()));
        System.out.println("\nResponse Times:");
        System.out.println("  Min: " + result.getMinResponseTime() + " ms");
        System.out.println("  Max: " + result.getMaxResponseTime() + " ms");
        System.out.println("  Average: " + String.format("%.2f", result.getAvgResponseTime()) + " ms");
        System.out.println("  Median: " + result.getMedianResponseTime() + " ms");
        System.out.println("\nPer Endpoint Statistics:");
        result.getEndpointStats().forEach((endpoint, stats) -> {
            System.out.println("\n  " + endpoint + ":");
            System.out.println("    Requests: " + stats.getRequestCount());
            System.out.println("    Success: " + stats.getSuccessCount());
            System.out.println("    Failed: " + stats.getFailedCount());
            System.out.println("    Avg Response Time: " + String.format("%.2f", stats.getAvgResponseTime()) + " ms");
        });
    }
}

