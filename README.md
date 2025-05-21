# Spring Boot Application with Kubernetes Deployment

This project is a Gradle-based Spring Boot application designed for deployment in Kubernetes. It includes configurations for Horizontal Pod Autoscaler (HPA) testing with custom metrics.

## Features
- Gradle build system
- Dockerfile for containerization
- Kubernetes manifests for deployment
- Environment variables for JVM tuning
- HPA testing with custom metrics
- Simulated CPU and memory load endpoints for testing

## Prerequisites
- Java 21
- Docker
- Kubernetes cluster
- kubectl configured for your cluster

## Build and Run
1. Build the application JAR:
   ```bash
   ./gradlew build
   ```
2. Build the Docker image:
   ```bash
   docker build -t app3:latest .
   ```
3. Deploy to Kubernetes:
   ```bash
   kubectl apply -f k8s-manifests/
   ```

## Available Endpoints
1. **`GET /cpu-load`**  
   - **Description**: Increases CPU load for a specified duration.  
   - **Parameters**:  
     - `duration` (optional, default: 10): Duration in seconds for which the CPU load will be increased.  
   - **Response**: A message indicating the CPU load duration.

2. **`GET /memory-load`**  
   - **Description**: Increases memory usage by allocating a specified amount of memory.  
   - **Parameters**:  
     - `size` (optional, default: 10): Amount of memory to allocate in MB.  
   - **Response**: A message indicating the memory load size.

3. **`GET /reset-memory`**  
   - **Description**: Resets memory usage by triggering garbage collection.  
   - **Parameters**: None.  
   - **Response**: A message indicating that memory load has been reset.

4. **Actuator Endpoints**  
   - **`GET /actuator/health`**: Provides the health status of the application.  
   - **`GET /actuator/metrics`**: Provides application metrics, such as memory and CPU usage.  
   - **`GET /actuator/info`**: Displays application-specific information (if configured).  

## Prometheus Integration
This application exposes metrics in Prometheus format at the `/actuator/prometheus` endpoint. Ensure Prometheus is configured to scrape this endpoint for metrics like `jvm_threads_live_threads` and `jvm_memory_used_bytes`.

### Verifying Prometheus Endpoint
1. Start the application.
2. Access the Prometheus metrics at:
   ```bash
   curl http://<application-host>:8080/actuator/prometheus
   ```
3. Ensure Prometheus-formatted metrics are displayed.

### Example Prometheus Scrape Configuration
```yaml
scrape_configs:
  - job_name: 'autoscaling-demo'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['<app-service-name>:8080']
```

## JVM Metrics in Kubernetes Dashboards

This application exposes JVM metrics via the `/actuator/prometheus` endpoint. These metrics can be scraped by Prometheus and displayed in Kubernetes dashboards.

### Key Metrics
1. **`jvm_memory_used_bytes`**: Tracks memory usage.
2. **`jvm_threads_live_threads`**: Tracks live thread count.

### Steps to View Metrics
1. Deploy the Prometheus Adapter using the provided `prometheus-adapter-config.yaml`.
2. Ensure Prometheus is configured to scrape the `/actuator/prometheus` endpoint.
3. Use Kubernetes dashboards (e.g., Grafana) to visualize the metrics.

## Testing HPA
Refer to the `HPA-Testing.md` file for detailed steps on testing Horizontal Pod Autoscaler with custom metrics.