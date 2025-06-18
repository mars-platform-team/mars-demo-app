package org.example;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RestController
public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final Tracer tracer = GlobalOpenTelemetry.getTracer("org.example.App");

    public static void main(String[] args) {
        logger.info("Starting application...");
        SpringApplication.run(App.class, args);
        logger.info("Application started.");
    }

    @GetMapping("/")
    public String sayHello() {
        Span span = tracer.spanBuilder("sayHello").startSpan();
        try (Scope scope = span.makeCurrent()) {
            logger.info("Handling request to '/' endpoint. TraceID: {}", span.getSpanContext().getTraceId());
            return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Welcome to MARS</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            margin: 0;
                            padding: 0;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            height: 100vh;
                            background: linear-gradient(135deg, #1e90ff, #ff6347);
                            color: white;
                            text-align: center;
                        }
                        h1 {
                            font-size: 3rem;
                            margin-bottom: 1rem;
                        }
                        p {
                            font-size: 1.5rem;
                        }
                        ul {
                            list-style: none;
                            padding: 0;
                        }
                        li {
                            margin: 10px 0;
                            font-size: 1.2rem;
                            display: flex;
                            align-items: center;
                        }
                        li a {
                            color: #ffd700;
                            text-decoration: none;
                            font-weight: bold;
                            margin-left: 10px;
                        }
                        li a:hover {
                            text-decoration: underline;
                        }
                        .icon {
                            font-size: 1.5rem;
                            margin-right: 10px;
                        }
                    </style>
                </head>
                <body>
                    <div>
                        <h1>Welcome to MARS!</h1>
                        <p>Your Spring Boot application is running smoothly.</p>
                        <p>Explore the available endpoints:</p>
                        <ul>
                            <li><span class="icon">⚙️</span><a href="/cpu-load?duration=10">Simulate CPU Load</a></li>
                            <li><span class="icon">💾</span><a href="/memory-load?size=10">Simulate Memory Load</a></li>
                            <li><span class="icon">♻️</span><a href="/reset-memory">Reset Memory Load</a></li>
                            <li><span class="icon">✅</span><a href="/actuator/health">Check Health</a></li>
                            <li><span class="icon">📂</span><a href="/simulate-objectstore">Simulate Object Store</a></li>
                        </ul>
                    </div>
                </body>
                </html>
            """;
        } finally {
            span.end();
        }
    }

    @GetMapping("/cpu-load")
    public String increaseCpuLoad(@RequestParam(defaultValue = "10") int duration) {
        Span span = tracer.spanBuilder("increaseCpuLoad").startSpan();
        try (Scope scope = span.makeCurrent()) {
            logger.info("Simulating CPU load for {} seconds. TraceID: {}", duration, span.getSpanContext().getTraceId());
            long endTime = System.currentTimeMillis() + duration * 1000;
            while (System.currentTimeMillis() < endTime) {
                for (int i = 0; i < 50000; i++) {
                    Math.pow(Math.random(), Math.random());
                }
            }
            logger.info("CPU load simulation completed. TraceID: {}", span.getSpanContext().getTraceId());
            return "CPU load increased for " + duration + " seconds.";
        } finally {
            span.end();
        }
    }

    @GetMapping("/memory-load")
    public String increaseMemoryLoad(@RequestParam(defaultValue = "10") int size) {
        Span span = tracer.spanBuilder("increaseMemoryLoad").startSpan();
        try (Scope scope = span.makeCurrent()) {
            logger.info("Simulating memory load of {} MB. TraceID: {}", size, span.getSpanContext().getTraceId());
            List<byte[]> memoryHog = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                memoryHog.add(new byte[1024 * 1024]);
            }
            logger.info("Memory load simulation completed. TraceID: {}", span.getSpanContext().getTraceId());
            return "Memory load increased by " + size + " MB.";
        } finally {
            span.end();
        }
    }

    @GetMapping("/reset-memory")
    public String resetMemoryLoad() {
        Span span = tracer.spanBuilder("resetMemoryLoad").startSpan();
        try (Scope scope = span.makeCurrent()) {
            logger.info("Resetting memory load. TraceID: {}", span.getSpanContext().getTraceId());
            System.gc();
            logger.info("Memory load reset completed. TraceID: {}", span.getSpanContext().getTraceId());
            return "Memory load reset.";
        } finally {
            span.end();
        }
    }

    @GetMapping("/simulate-objectstore")
    public String simulateObjectStore() {
        Span span = tracer.spanBuilder("simulateObjectStore").startSpan();
        try (Scope scope = span.makeCurrent()) {
            logger.info("Simulating object store directory usage. TraceID: {}", span.getSpanContext().getTraceId());
            String objectStoreDir = System.getProperty("com.arjuna.ats.arjuna.objectstore.objectStoreDir", System.getProperty("user.dir"));
            File dir = new File(objectStoreDir);

            if (!dir.exists()) {
                logger.info("Object store directory does not exist. Attempting to create: {}", objectStoreDir);
                if (!dir.mkdirs()) {
                    logger.error("Failed to create object store directory: {}", objectStoreDir);
                    return "Error: Failed to create object store directory at " + objectStoreDir;
                }
            }

            File testFile = new File(dir, "test.txt");
            try {
                if (testFile.createNewFile() || testFile.exists()) {
                    String content = "This is a test file created for simulating the object store directory.";
                    java.nio.file.Files.writeString(testFile.toPath(), content);
                    logger.info("Successfully wrote to test file in object store directory: {}", testFile.getAbsolutePath());
                    return "Successfully wrote to test file in object store directory: " + testFile.getAbsolutePath();
                } else {
                    logger.error("Failed to create or access test file in object store directory: {}", testFile.getAbsolutePath());
                    return "Error: Failed to create or access test file in object store directory: " + testFile.getAbsolutePath();
                }
            } catch (IOException e) {
                logger.error("IOException while writing to test file in object store directory: {}", e.getMessage());
                return "Error: IOException while writing to test file in object store directory: " + e.getMessage();
            }
        } finally {
            span.end();
        }
    }
}
