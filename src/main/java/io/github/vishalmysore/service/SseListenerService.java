package io.github.vishalmysore.service;

import com.t4a.processor.AIProcessingException;
import com.t4a.processor.OpenAiActionProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseListenerService {

    @Value("${arduino.ip}")
    private String arduinoIp;

    private OpenAiActionProcessor openAiActionProcessor;
    private final Map<LocalDateTime, String> learningLogs = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws IOException {
        log.info("Initializing SSE listener...");
        openAiActionProcessor = new OpenAiActionProcessor();
        listenToSse(arduinoIp);
    }

    public void listenToSse(String sseUrl) throws IOException {
        while (true) {  // Keep trying to listen indefinitely
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpGet httpGet = new HttpGet(sseUrl);
                httpGet.setHeader("Accept", "text/event-stream");

                try (CloseableHttpResponse response = client.execute(httpGet)) {
                    HttpEntity entity = response.getEntity();

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
                        String line;
                        while (true) {
                            line = reader.readLine();
                            if (line == null) {
                                log.info("Server closed the connection. Reconnecting...");
                                break;  // Exit the inner while loop to reconnect
                            }

                            if (!line.trim().isEmpty()) {
                                log.info("Received SSE event: " + line);
                                handleSensorData(line);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                log.error("Error while listening to SSE stream, retrying...", e);
                try {
                    // Adding a small delay before reconnecting to avoid excessive retries
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void handleSensorData(String event) {
        LocalDateTime timestamp = LocalDateTime.now();
        learningLogs.put(timestamp, event);

        if (event.startsWith("temperature:")) {
            double temperature = Double.parseDouble(event.split(":")[1].trim());
            handleTemperature(temperature, timestamp);
        } else if (event.startsWith("distance:")) {
            int distance = Integer.parseInt(event.split(":")[1].trim());
            handleDistance(distance, timestamp);
        } else if (event.startsWith("brightness:")) {
            int brightness = Integer.parseInt(event.split(":")[1].trim());
            handleBrightness(brightness, timestamp);
        } else if (event.startsWith("waterDetected:")) {
            boolean waterDetected = Boolean.parseBoolean(event.split(":")[1].trim());
            handleWaterDetection(waterDetected, timestamp);
        } else {
            log.warn("Unknown event received: " + event);
        }
    }

    private void handleTemperature(double temperature, LocalDateTime timestamp) {
        log.info("Temperature: " + temperature + "°C");
        if (temperature > 30.0) {
            log.warn("High temperature detected! Triggering alert.");
            try {
                String action = "Chotu robot temperature is " + temperature + " degree celsius";
                openAiActionProcessor.processSingleAction(action);
                learningLogs.put(timestamp, action);
            } catch (AIProcessingException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void handleDistance(int distance, LocalDateTime timestamp) {
        log.info("Distance: " + distance + " cm");
        if (distance < 50) {
            log.warn("Danger! Object is too close.");
            try {
                String action = "Chotu robot detected an object at a distance of " + distance + " cm";
                openAiActionProcessor.processSingleAction(action);
                learningLogs.put(timestamp, action);
            } catch (AIProcessingException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void handleBrightness(int brightness, LocalDateTime timestamp) {
        log.info("Brightness: " + brightness);
        if (brightness < 100) {
            log.info("It’s dark! Turning on lights.");
            try {
                String action = "Chotu robot detected low brightness of " + brightness;
                openAiActionProcessor.processSingleAction(action);
                learningLogs.put(timestamp, action);
            } catch (AIProcessingException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void handleWaterDetection(boolean waterDetected, LocalDateTime timestamp) {
        if (waterDetected) {
            try {
                String action = "Chotu robot detected water";
                openAiActionProcessor.processSingleAction(action);
                learningLogs.put(timestamp, action);
            } catch (AIProcessingException e) {
                log.error(e.getMessage());
            }
        } else {
            log.info("No water detected.");
        }
    }

    private void handleHumidity(double humidity, LocalDateTime timestamp) {
        log.info("Humidity: " + humidity + "%");
        if (humidity > 70.0) {
            log.warn("High humidity detected! Triggering alert.");
            try {
                String action = "Chotu robot detected high humidity of " + humidity + "%";
                openAiActionProcessor.processSingleAction(action);
                learningLogs.put(timestamp, action);
            } catch (AIProcessingException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void handleMotionDetection(boolean motionDetected, LocalDateTime timestamp) {
        if (motionDetected) {
            log.info("Motion detected!");
            try {
                String action = "Chotu robot detected motion";
                openAiActionProcessor.processSingleAction(action);
                learningLogs.put(timestamp, action);
            } catch (AIProcessingException e) {
                log.error(e.getMessage());
            }
        } else {
            log.info("No motion detected.");
        }
    }
}