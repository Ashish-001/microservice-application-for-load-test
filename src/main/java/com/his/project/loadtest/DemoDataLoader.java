package com.his.project.loadtest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.his.project.loadtest.client.ApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoDataLoader {
    
    private final ApiClient apiClient;
    private final Gson gson;
    
    public DemoDataLoader(String baseUrl) {
        this.apiClient = new ApiClient(baseUrl);
        this.gson = new Gson();
    }
    
    public void loadDemoData() {
        System.out.println("==========================================");
        System.out.println("  Loading Demo Data to Microservices");
        System.out.println("==========================================\n");
        
        // Load products
        System.out.println("1. Loading Products...");
        loadProducts();
        
        // Wait a bit
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Create inventory entries
        System.out.println("\n2. Creating Inventory Entries...");
        createInventoryEntries();
        
        // Wait a bit
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Check inventory
        System.out.println("\n3. Checking Inventory...");
        checkInventory();
        
        // Wait a bit
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Place orders
        System.out.println("\n4. Placing Orders...");
        placeOrders();
        
        System.out.println("\n==========================================");
        System.out.println("  Demo Data Loading Complete!");
        System.out.println("==========================================\n");
    }
    
    private void loadProducts() {
        try {
            // Generate 1000 products programmatically
            List<Map<String, Object>> products = generateProducts(1000);
            int success = 0;
            int failed = 0;
            
            System.out.println("  Generating and loading 1000 products...");
            
            for (Map<String, Object> product : products) {
                String productJson = gson.toJson(product);
                boolean result = apiClient.createProductWithData(productJson);
                if (result) {
                    success++;
                    // Only print every 100th product to avoid cluttering output
                    if (success % 100 == 0) {
                        System.out.println("  ✓ Created: " + product.get("name") + " (Progress: " + success + "/1000)");
                    }
                } else {
                    failed++;
                    System.out.println("  ✗ Failed: " + product.get("name"));
                }
                // Reduced delay to overload the API
                Thread.sleep(10);
            }
            
            System.out.println("\n  Summary: " + success + " created, " + failed + " failed");
        } catch (Exception e) {
            System.err.println("Error loading products: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private List<Map<String, Object>> generateProducts(int count) {
        List<Map<String, Object>> products = new ArrayList<>();
        String[] categories = {"Electronics", "Computers", "Phones", "Accessories", "Home", "Kitchen", 
                              "Fitness", "Gaming", "Audio", "Cameras", "TVs", "Wearables", "Smart Home", 
                              "Appliances", "Tools"};
        String[] brands = {"TechPro", "SmartBrand", "EliteTech", "ProMax", "UltraTech", "PrimeTech", 
                          "Apex", "Nexus", "Vertex", "Quantum", "Fusion", "Nova", "Titan", "Zenith", "Pinnacle"};
        
        for (int i = 1; i <= count; i++) {
            Map<String, Object> product = new HashMap<>();
            String category = categories[i % categories.length];
            String brand = brands[i % brands.length];
            String model = "Model-" + String.format("%04d", i);
            
            product.put("name", brand + " " + category + " " + model);
            product.put("description", "High-quality " + category.toLowerCase() + " from " + brand + 
                       ". Premium features and excellent performance. Model number: " + model);
            product.put("skuCode", "SKU-" + category.toUpperCase().replace(" ", "-") + "-" + 
                       String.format("%04d", i));
            // Generate prices between $19.99 and $9999.99
            double price = 19.99 + (Math.random() * 9980.0);
            product.put("price", Math.round(price * 100.0) / 100.0);
            
            products.add(product);
        }
        
        return products;
    }
    
    private void createInventoryEntries() {
        try {
            List<Map<String, Object>> products = loadJsonFile("demo-products.json");
            int success = 0;
            int failed = 0;
            
            for (Map<String, Object> product : products) {
                String skuCode = (String) product.get("skuCode");
                // Create inventory with initial quantity of 100 for each product
                boolean result = apiClient.createInventory(skuCode, 100);
                if (result) {
                    success++;
                    System.out.println("  ✓ Created inventory: " + skuCode + " (qty: 100)");
                } else {
                    failed++;
                    System.out.println("  ✗ Failed: " + skuCode);
                }
                Thread.sleep(150); // Small delay between requests
            }
            
            System.out.println("\n  Summary: " + success + " created, " + failed + " failed");
        } catch (Exception e) {
            System.err.println("Error creating inventory entries: " + e.getMessage());
        }
    }
    
    private void checkInventory() {
        try {
            List<Map<String, Object>> checks = loadJsonFile("demo-inventory-checks.json");
            int success = 0;
            int failed = 0;
            
            for (Map<String, Object> check : checks) {
                String skuCode = (String) check.get("skuCode");
                int quantity = ((Double) check.get("quantity")).intValue();
                boolean result = apiClient.checkInventoryWithParams(skuCode, quantity);
                if (result) {
                    success++;
                    System.out.println("  ✓ Checked: " + skuCode + " (qty: " + quantity + ")");
                } else {
                    failed++;
                    System.out.println("  ✗ Failed: " + skuCode);
                }
                Thread.sleep(100);
            }
            
            System.out.println("\n  Summary: " + success + " checked, " + failed + " failed");
        } catch (Exception e) {
            System.err.println("Error checking inventory: " + e.getMessage());
        }
    }
    
    private void placeOrders() {
        try {
            List<Map<String, Object>> orders = loadJsonFile("demo-orders.json");
            int success = 0;
            int failed = 0;
            
            for (Map<String, Object> order : orders) {
                String orderJson = gson.toJson(order);
                boolean result = apiClient.placeOrderWithData(orderJson);
                if (result) {
                    success++;
                    Map<String, Object> userDetails = (Map<String, Object>) order.get("userDetails");
                    System.out.println("  ✓ Order placed for: " + userDetails.get("email"));
                } else {
                    failed++;
                    Map<String, Object> userDetails = (Map<String, Object>) order.get("userDetails");
                    System.out.println("  ✗ Failed for: " + userDetails.get("email"));
                }
                Thread.sleep(300);
            }
            
            System.out.println("\n  Summary: " + success + " orders placed, " + failed + " failed");
        } catch (Exception e) {
            System.err.println("Error placing orders: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> loadJsonFile(String filename) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/" + filename);
        if (inputStream == null) {
            throw new IOException("File not found: " + filename);
        }
        
        Type type = new TypeToken<List<Map<String, Object>>>(){}.getType();
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, type);
        }
    }
    
    public static void main(String[] args) {
        String baseUrl = args.length > 0 ? args[0] : "http://35.198.90.238:9000";
        DemoDataLoader loader = new DemoDataLoader(baseUrl);
        loader.loadDemoData();
    }
}

