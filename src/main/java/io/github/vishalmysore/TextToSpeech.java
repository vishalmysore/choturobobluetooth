package io.github.vishalmysore;

import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class TextToSpeech {

    private static final String API_URL = "https://api.openai.com/v1/audio/speech";
    private static final String API_KEY = System.getenv("API_KEY"); // Replace with your API key
    private static final String SAVE_PATH = "C:/temp/speech.mp3"; // Change as needed

    public  String fetchAndSaveSpeech(String text) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // JSON request body
            String jsonInputString = "{"
                    + "\"model\": \"tts-1\","
                    + "\"input\": \"" + text + "\","
                    + "\"voice\": \"alloy\""
                    + "}";

            // Send request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read response and save as file
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream in = connection.getInputStream();
                     FileOutputStream out = new FileOutputStream(SAVE_PATH)) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                return SAVE_PATH;
            } else {
                System.err.println("Error: " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
