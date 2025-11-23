package com.his.project.loadtest.client;

import com.google.gson.Gson;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

public class ApiClient {
    private final String baseUrl;
    private final CloseableHttpClient httpClient;
    private final Gson gson;
    private final Random random;
    
    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClients.createDefault();
        this.gson = new Gson();
        this.random = new Random();
    }
    
    public boolean getProducts() {
        try {
            HttpGet request = new HttpGet(baseUrl + "/api/product");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return response.getCode() == 200;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean createProduct() {
        try {
            String productJson = String.format(
                "{\"name\":\"Test Product %s\",\"description\":\"Load test product\",\"skuCode\":\"TEST-%s\",\"price\":%.2f}",
                UUID.randomUUID().toString().substring(0, 8),
                UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                10.0 + random.nextDouble() * 990.0
            );
            
            HttpPost request = new HttpPost(baseUrl + "/api/product");
            request.setHeader("Content-Type", "application/json; charset=UTF-8");
            request.setEntity(new StringEntity(productJson, StandardCharsets.UTF_8));
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return response.getCode() == 201;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean checkInventory() {
        try {
            String skuCode = "IPHONE-15-PRO-256";
            int quantity = 1 + random.nextInt(5);
            HttpGet request = new HttpGet(
                baseUrl + "/api/inventory?skuCode=" + skuCode + "&quantity=" + quantity
            );
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return response.getCode() == 200;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean placeOrder() {
        try {
            String orderJson = String.format(
                "{\"skuCode\":\"IPHONE-15-PRO-256\",\"price\":999.99,\"quantity\":%d,\"userDetails\":{\"email\":\"test%d@example.com\",\"firstName\":\"Test\",\"lastName\":\"User\"}}",
                1 + random.nextInt(3),
                random.nextInt(10000)
            );
            
            HttpPost request = new HttpPost(baseUrl + "/api/order");
            request.setHeader("Content-Type", "application/json; charset=UTF-8");
            request.setEntity(new StringEntity(orderJson, StandardCharsets.UTF_8));
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return response.getCode() == 201;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean createProductWithData(String productJson) {
        try {
            HttpPost request = new HttpPost(baseUrl + "/api/product");
            request.setHeader("Content-Type", "application/json; charset=UTF-8");
            request.setEntity(new StringEntity(productJson, StandardCharsets.UTF_8));
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getCode();
                if (statusCode != 201) {
                    String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    System.err.println("Product creation failed. Status: " + statusCode + ", Response: " + responseBody);
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            System.err.println("Product creation error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean placeOrderWithData(String orderJson) {
        try {
            HttpPost request = new HttpPost(baseUrl + "/api/order");
            request.setHeader("Content-Type", "application/json; charset=UTF-8");
            request.setEntity(new StringEntity(orderJson, StandardCharsets.UTF_8));
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getCode();
                if (statusCode != 201) {
                    String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    System.err.println("Order placement failed. Status: " + statusCode + ", Response: " + responseBody);
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            System.err.println("Order placement error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean checkInventoryWithParams(String skuCode, int quantity) {
        try {
            HttpGet request = new HttpGet(
                baseUrl + "/api/inventory?skuCode=" + skuCode + "&quantity=" + quantity
            );
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return response.getCode() == 200;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean createInventory(String skuCode, int quantity) {
        try {
            HttpPost request = new HttpPost(
                baseUrl + "/api/inventory?skuCode=" + skuCode + "&quantity=" + quantity
            );
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getCode();
                if (statusCode != 201) {
                    String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    System.err.println("Inventory creation failed. Status: " + statusCode + ", Response: " + responseBody);
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            System.err.println("Inventory creation error: " + e.getMessage());
            return false;
        }
    }
}

