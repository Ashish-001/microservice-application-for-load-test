# Microservices Load Testing Tool

A Java-based load testing application for testing all microservices APIs with configurable concurrent requests and demo data loading.

## Features

- ✅ Tests all microservices APIs:
  - **Product Service**: GET and POST endpoints
  - **Order Service**: POST endpoint
  - **Inventory Service**: GET endpoint
- ✅ Configurable concurrent threads
- ✅ Configurable requests per thread
- ✅ Detailed statistics and metrics
- ✅ Per-endpoint performance analysis
- ✅ **Demo data loading** - Pre-configured sample data for all APIs

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- Microservices application running (API Gateway on port 9000)

## Managing Microservices Application

If you need to start, stop, or restart the microservices application, use the `start-all.sh` script in the microservices project:

### Starting All Services

```bash
cd /path/to/microservices-project
./start-all.sh start
```

This will:
- Start Docker services (if needed)
- Build all Spring Boot services
- Start all microservices in the correct order

### Restarting Services

After making changes to the microservices (e.g., security configuration), rebuild and restart:

```bash
# Option 1: Build then restart (recommended after code changes)
./start-all.sh build
./start-all.sh restart

# Option 2: Start everything (builds and starts)
./start-all.sh start
```

### Other Useful Commands

```bash
./start-all.sh stop           # Stop all services
./start-all.sh status          # Show status of all services
./start-all.sh logs <service>  # View logs for a specific service
```

**Available services for logs:**
- `api-gateway`
- `product-service`
- `order-service`
- `inventory-service`

**Example:**
```bash
./start-all.sh logs api-gateway
```

### Important Notes

- **After Security Config Changes**: If you modify the API Gateway security configuration, you must rebuild and restart:
  ```bash
  ./start-all.sh build
  ./start-all.sh restart
  ```

- **Service Ports**:
  - API Gateway: `9000`
  - Product Service: `8080`
  - Order Service: `8081`
  - Inventory Service: `8082`

## Quick Start

### 1. Load Demo Data First

Before running load tests, load demo data into your microservices:

```bash
./scripts/load-demo-data.sh
```

**Or using Maven directly:**
```bash
mvn exec:java -Dexec.mainClass="com.his.project.loadtest.DemoDataLoader"
```

### 2. Run Load Tests

**Interactive Mode (Recommended):**
```bash
mvn exec:java -Dexec.mainClass="com.his.project.loadtest.LoadTestRunner"
```

## Building the Project

### Using Build Script (Recommended)

```bash
./scripts/build.sh          # Compile (default)
./scripts/build.sh clean     # Clean only
./scripts/build.sh compile   # Clean and compile
./scripts/build.sh package   # Clean, compile, and package (creates JAR)
```

### Using Maven Directly

```bash
cd microservices-load-test
mvn clean compile            # Clean and compile
mvn clean package            # Clean, compile, and package
```

## Running the Load Test

### Interactive Mode (Recommended)

```bash
mvn exec:java
```

This will prompt you for:
- API Gateway Base URL (default: http://localhost:9000)
- Number of threads (concurrent users) (default: 10)
- Number of requests per thread (default: 100)
- Delay between requests in milliseconds (default: 100)
- Which services to test (Product, Order, Inventory)

### Quick Start Example

```bash
# Press Enter for all defaults to run a quick test
mvn exec:java -Dexec.mainClass="com.his.project.loadtest.LoadTestRunner"
```

## Example Configuration

```
API Gateway Base URL [http://localhost:9000]: 
Number of threads (concurrent users) [10]: 20
Number of requests per thread [100]: 50
Delay between requests (ms) [100]: 50
Test Product Service? [Y/n]: 
Test Order Service? [Y/n]: 
Test Inventory Service? [Y/n]: 
```

## Test Results

The tool provides comprehensive statistics:

- **Total Requests**: Total number of requests sent
- **Success Rate**: Percentage of successful requests
- **Response Times**: Min, Max, Average, and Median
- **Per-Endpoint Statistics**: Detailed metrics for each API endpoint

### Sample Output

```
==========================================
  Load Test Results
==========================================

Total Requests: 4000
Successful: 3850
Failed: 150
Success Rate: 96.25%

Response Times:
  Min: 45 ms
  Max: 1250 ms
  Average: 234.56 ms
  Median: 198 ms

Per Endpoint Statistics:

  GET /api/product:
    Requests: 1000
    Success: 980
    Failed: 20
    Avg Response Time: 156.23 ms

  POST /api/product:
    Requests: 1000
    Success: 950
    Failed: 50
    Avg Response Time: 312.45 ms

  GET /api/inventory:
    Requests: 1000
    Success: 970
    Failed: 30
    Avg Response Time: 189.12 ms

  POST /api/order:
    Requests: 1000
    Success: 950
    Failed: 50
    Avg Response Time: 456.78 ms
```

## APIs Tested

### Product Service
- **GET /api/product** - Retrieve all products
- **POST /api/product** - Create a new product

### Order Service
- **POST /api/order** - Place a new order

### Inventory Service
- **GET /api/inventory** - Check product availability

## Demo Data Loading

### Using Script

```bash
./scripts/load-demo-data.sh [API_GATEWAY_URL]
```

### Using Maven

```bash
mvn exec:java -Dexec.mainClass="com.his.project.loadtest.DemoDataLoader" -Dexec.args="http://localhost:9000"
```

### Custom API Gateway URL

```bash
./scripts/load-demo-data.sh http://localhost:9000
```

## Tips for Load Testing

1. **Load Demo Data First**: Always load demo data before running load tests
2. **Start Small**: Begin with fewer threads and requests to ensure everything works
3. **Monitor Resources**: Watch CPU, memory, and database connections during tests
4. **Check Logs**: Monitor application logs for errors or warnings
5. **Gradual Increase**: Gradually increase load to find breaking points
6. **Use Grafana**: Monitor metrics in Grafana (http://localhost:3000) during load tests
7. **Check Zipkin**: View distributed traces in Zipkin (http://localhost:9411)

## Troubleshooting

### Connection Refused
- Ensure the API Gateway is running on the specified port
- Check that all microservices are up and running
- Verify services are started using `./start-all.sh status`

### 401 Unauthorized Errors
- If you get 401 errors when loading demo data, the API Gateway security configuration may need to be updated
- Rebuild and restart the API Gateway:
  ```bash
  cd /path/to/microservices-project
  ./start-all.sh build
  ./start-all.sh restart
  ```
- Check API Gateway logs: `./start-all.sh logs api-gateway`

### High Failure Rate
- Check if services are overloaded
- Verify database connections
- Check application logs for errors
- Make sure demo data is loaded first
- Review service logs: `./start-all.sh logs <service-name>`

### Slow Response Times
- Monitor resource usage (CPU, Memory)
- Check database performance
- Verify network latency
- Check if services are running properly: `./start-all.sh status`

### Demo Data Not Loading
- Ensure JSON files are in `src/main/resources/data/`
- Check that API Gateway is accessible
- Verify services are running
- Check for authentication errors (401) - see "401 Unauthorized Errors" above

## Project Structure

```
microservices-load-test/
├── pom.xml
├── README.md
├── scripts/
│   ├── build.sh               # Build script
│   └── load-demo-data.sh      # Load demo data script
├── data/                      # Demo data files (source)
│   ├── demo-products.json
│   ├── demo-orders.json
│   └── demo-inventory-checks.json
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── his/
        │           └── project/
        │               └── loadtest/
        │                   ├── LoadTestRunner.java      # Main entry point
        │                   ├── DemoDataLoader.java      # Demo data loader
        │                   ├── client/
        │                   │   └── ApiClient.java       # HTTP client for API calls
        │                   ├── model/
        │                   │   ├── TestConfig.java      # Configuration model
        │                   │   ├── TestResult.java      # Results model
        │                   │   └── EndpointStats.java   # Per-endpoint statistics
        │                   └── service/
        │                       └── LoadTestService.java  # Load testing logic
        └── resources/
            └── data/          # Demo data files (compiled)
                ├── demo-products.json
                ├── demo-orders.json
                └── demo-inventory-checks.json
```

## License

This is a load testing tool for the microservices application.
