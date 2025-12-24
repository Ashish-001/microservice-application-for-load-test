package com.his.project.loadtest;

import com.his.project.loadtest.model.TestConfig;
import com.his.project.loadtest.model.TestResult;
import com.his.project.loadtest.service.LoadTestService;

import java.util.Scanner;

public class LoadTestRunner {
    
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  Microservices Load Testing Tool");
        System.out.println("==========================================\n");
        
        TestConfig config;
        
        // Check if command-line arguments are provided for non-interactive mode
        if (args.length > 0) {
            config = parseCommandLineArgs(args);
            System.out.println("Running in non-interactive mode:");
            if (config.getRequestsPerService() != null) {
                System.out.println("  Requests per service endpoint: " + config.getRequestsPerService());
                if (config.isTestProductService()) {
                    System.out.println("  Product Service: GET " + config.getRequestsPerService() + " + POST " + config.getRequestsPerService() + " = " + (config.getRequestsPerService() * 2) + " total");
                }
                System.out.println("  Inventory Service: " + (config.isTestInventoryService() ? "GET " + config.getRequestsPerService() : "disabled"));
                System.out.println("  Order Service: " + (config.isTestOrderService() ? "POST " + config.getRequestsPerService() : "disabled"));
            } else {
                System.out.println("  Total Requests: " + calculateTotalRequests(config));
            }
            System.out.println("  Threads: " + config.getThreads());
            System.out.println("  Requests per thread: " + config.getRequestsPerThread());
            System.out.println("  Delay: " + config.getDelayMs() + " ms");
        } else {
            Scanner scanner = new Scanner(System.in);
            config = getTestConfiguration(scanner);
            scanner.close();
        }
        
        System.out.println("\nStarting load test...");
        System.out.println("Press Ctrl+C to stop early\n");
        
        LoadTestService loadTestService = new LoadTestService(config);
        TestResult result = loadTestService.runLoadTest();
        
        // Print results
        printResults(result);
    }
    
    private static TestConfig parseCommandLineArgs(String[] args) {
        TestConfig config = new TestConfig();
        
        // Default: all services enabled
        config.setTestProductService(true);
        config.setTestOrderService(true);
        config.setTestInventoryService(true);
        
        Integer totalRequestsTarget = null;
        
        // Parse arguments
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "--total-requests":
                case "-t":
                    if (i + 1 < args.length) {
                        totalRequestsTarget = Integer.parseInt(args[++i]);
                    }
                    break;
                case "--threads":
                case "-n":
                    if (i + 1 < args.length) {
                        config.setThreads(Integer.parseInt(args[++i]));
                    }
                    break;
                case "--requests-per-thread":
                case "-r":
                    if (i + 1 < args.length) {
                        config.setRequestsPerThread(Integer.parseInt(args[++i]));
                    }
                    break;
                case "--delay":
                case "-d":
                    if (i + 1 < args.length) {
                        config.setDelayMs(Long.parseLong(args[++i]));
                    }
                    break;
                case "--url":
                case "-u":
                    if (i + 1 < args.length) {
                        config.setGatewayBaseUrl(args[++i]);
                    }
                    break;
                case "--no-product":
                    config.setTestProductService(false);
                    break;
                case "--no-order":
                    config.setTestOrderService(false);
                    break;
                case "--no-inventory":
                    config.setTestInventoryService(false);
                    break;
                case "--requests-per-service":
                case "-s":
                    if (i + 1 < args.length) {
                        config.setRequestsPerService(Integer.parseInt(args[++i]));
                    }
                    break;
            }
        }
        
        // Calculate requests per thread if requests per service was specified
        if (config.getRequestsPerService() != null) {
            // Each endpoint gets the full amount: Product GET (10k), Product POST (10k), Inventory GET (10k), Order POST (10k)
            // So we need enough iterations to cover all endpoints
            int maxRequestsPerService = config.getRequestsPerService();
            int maxIterations = maxRequestsPerService; // Each endpoint needs this many iterations
            
            if (maxIterations > 0) {
                int threads = config.getThreads();
                // Add some buffer to ensure we reach all targets
                config.setRequestsPerThread((int) Math.ceil((double) maxIterations / threads) + 100);
            }
        } else if (totalRequestsTarget != null) {
            // Original logic for total requests
            int endpointsPerIteration = 0;
            if (config.isTestProductService()) endpointsPerIteration += 2; // GET + POST
            if (config.isTestInventoryService()) endpointsPerIteration += 1; // GET
            if (config.isTestOrderService()) endpointsPerIteration += 1; // POST
            
            if (endpointsPerIteration > 0) {
                int iterationsNeeded = (int) Math.ceil((double) totalRequestsTarget / endpointsPerIteration);
                int threads = config.getThreads();
                config.setRequestsPerThread((int) Math.ceil((double) iterationsNeeded / threads));
            }
        }
        
        return config;
    }
    
    private static int calculateTotalRequests(TestConfig config) {
        int endpointsPerIteration = 0;
        if (config.isTestProductService()) endpointsPerIteration += 2; // GET + POST
        if (config.isTestInventoryService()) endpointsPerIteration += 1; // GET
        if (config.isTestOrderService()) endpointsPerIteration += 1; // POST
        
        return config.getThreads() * config.getRequestsPerThread() * endpointsPerIteration;
    }
    
    private static TestConfig getTestConfiguration(Scanner scanner) {
        TestConfig config = new TestConfig();
        
        System.out.println("Configuration:");
        System.out.println("--------------");
        
        System.out.print("API Gateway Base URL [http://35.198.90.238:9000]: ");
        String gatewayUrl = scanner.nextLine().trim();
        config.setGatewayBaseUrl(gatewayUrl.isEmpty() ? "http://35.198.90.238:9000" : gatewayUrl);
        
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

