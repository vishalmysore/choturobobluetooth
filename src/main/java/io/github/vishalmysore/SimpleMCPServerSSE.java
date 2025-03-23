package io.github.vishalmysore;

import com.t4a.JsonUtils;
import com.t4a.api.*;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.OpenAiActionProcessor;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class SimpleMCPServerSSE {

    // Store PrintWriters by session ID
    private final ConcurrentHashMap<String, PrintWriter> clients = new ConcurrentHashMap<>();

    public SimpleMCPServerSSE() throws AIProcessingException {
//        System.out.println("Created SimpleMCPServerSSE");
//
//        List<ActionGroup> grpActopm =  PredictionLoader.getInstance().getActionGroupList().getGroups();
//        OpenAiActionProcessor processor = new OpenAiActionProcessor();
//        PredictionLoader.getInstance().getPredictions().forEach((name,action) -> {
//            System.out.println("Key: " + name + " Value: " + action);
//            if (action.getActionType() == ActionType.JAVAMETHOD) {
//                log.debug(action + "");
//                JsonUtils utils = new JsonUtils();
//                Method m = null;
//                JavaMethodAction javaMethodAction = (JavaMethodAction) action;
//                Class<?> clazz = javaMethodAction.getActionClass();
//                Method[] methods = clazz.getMethods();
//                for (Method m1 : methods
//                ) {
//                    if (m1.getName().equals(javaMethodAction.getActionName())) {
//                        m = m1;
//                        break;
//                    }
//                }
//
//                String jsonStr = utils.convertMethodTOJsonString(m);
//                System.out.println("Method to Json: " + jsonStr);
//                try {
//                   String jsonRPC =  processor.query(" Convert this into a valid json rpc " + jsonStr);
//                    System.out.println("Json RPC: " + jsonRPC);
//                } catch (AIProcessingException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//        });
//
//
//        // Loop through and print the entries in the map




       // processor.processSingleAction("compareCar", "{'car1':'audi','car2':'bmw'}");
    }

    // SSE endpoint streaming session-based URLs
    @GetMapping("/sse")
    public void streamSseEvent(HttpServletResponse response) {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");

        try {
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Connection", "keep-alive");

            PrintWriter writer = response.getWriter();
            String sessionId = UUID.randomUUID().toString();

            // Store the writer by session ID
            clients.put(sessionId, writer);

            // ✅ Send initial connection message
            writer.write("event: endpoint\n");
            writer.write("data: /message?sessionId=" + sessionId + "\n\n");
            writer.flush();

            System.out.println("Client connected: " + sessionId);

            // Send initial message
            sendMessageEvent(writer, "message",
                    "{\"jsonrpc\":\"2.0\",\"id\":0,\"result\":{\"protocolVersion\":\"2024-11-05\","
                            + "\"capabilities\":{\"prompts\":{},\"resources\":{\"subscribe\":true},"
                            + "\"tools\":{},\"logging\":{}},"
                            + "\"serverInfo\":{\"name\":\"chotuserver\",\"version\":\"1.0.0\"}}}");

            // ✅ Keep the connection alive
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                try {

                  //  writer.write(": keep-alive\n\n");
                 //   writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    clients.remove(sessionId);  // Remove the client if disconnected
                }
            }, 0, 10, TimeUnit.SECONDS);

            // Keep the connection open
            TimeUnit.SECONDS.sleep(60);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Helper method to send SSE messages
    private void sendMessageEvent(PrintWriter writer, String eventName, String data) throws IOException {
        if (writer != null) {
            writer.write("event: " + eventName + "\n");
            writer.write("data: " + data + "\n\n");
            writer.flush();
        }
    }

    // Handle POST requests and send SSE events only to the matching session
    @PostMapping(value = "/message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void handleJsonRpcMessage(
            @RequestParam(value = "sessionId", required = true) String sessionId,
            @RequestBody Map<String, Object> jsonRpcRequest) {

        System.out.println("Received JSON-RPC request for session: " + sessionId);
        String method = (String) jsonRpcRequest.get("method");

        if ("tools/list".equals(method)) {

            PrintWriter writer = clients.get(sessionId);

            if (writer != null) {
                try {
                    // ✅ Send message only to the matching session
                    sendMessageEvent(writer, "message",
                            "{\"jsonrpc\":\"2.0\",\"id\":1,\"result\":{\"tools\":[{\"name\":\"echo\",\"description\":\"Echoes back the input\",\"inputSchema\":{\"type\":\"object\",\"properties\":{\"message\":{\"type\":\"string\",\"description\":\"Message to echo\"}},\"required\":[\"message\"],\"additionalProperties\":false,\"$schema\":\"http://json-schema.org/draft-07/schema#\"}},{\"name\":\"add\",\"description\":\"Adds two numbers\",\"inputSchema\":{\"type\":\"object\",\"properties\":{\"a\":{\"type\":\"number\",\"description\":\"First number\"},\"b\":{\"type\":\"number\",\"description\":\"Second number\"}},\"required\":[\"a\",\"b\"],\"additionalProperties\":false,\"$schema\":\"http://json-schema.org/draft-07/schema#\"}},{\"name\":\"printEnv\",\"description\":\"Prints all environment variables, helpful for debugging MCP server configuration\",\"inputSchema\":{\"type\":\"object\",\"properties\":{},\"additionalProperties\":false,\"$schema\":\"http://json-schema.org/draft-07/schema#\"}},{\"name\":\"longRunningOperation\",\"description\":\"Demonstrates a long running operation with progress updates\",\"inputSchema\":{\"type\":\"object\",\"properties\":{\"duration\":{\"type\":\"number\",\"default\":10,\"description\":\"Duration of the operation in seconds\"},\"steps\":{\"type\":\"number\",\"default\":5,\"description\":\"Number of steps in the operation\"}},\"additionalProperties\":false,\"$schema\":\"http://json-schema.org/draft-07/schema#\"}},{\"name\":\"sampleLLM\",\"description\":\"Samples from an LLM using MCP's sampling feature\",\"inputSchema\":{\"type\":\"object\",\"properties\":{\"prompt\":{\"type\":\"string\",\"description\":\"The prompt to send to the LLM\"},\"maxTokens\":{\"type\":\"number\",\"default\":100,\"description\":\"Maximum number of tokens to generate\"}},\"required\":[\"prompt\"],\"additionalProperties\":false,\"$schema\":\"http://json-schema.org/draft-07/schema#\"}},{\"name\":\"getTinyImage\",\"description\":\"Returns the MCP_TINY_IMAGE\",\"inputSchema\":{\"type\":\"object\",\"properties\":{},\"additionalProperties\":false,\"$schema\":\"http://json-schema.org/draft-07/schema#\"}},{\"name\":\"annotatedMessage\",\"description\":\"Demonstrates how annotations can be used to provide metadata about content\",\"inputSchema\":{\"type\":\"object\",\"properties\":{\"messageType\":{\"type\":\"string\",\"enum\":[\"error\",\"success\",\"debug\"],\"description\":\"Type of message to demonstrate different annotation patterns\"},\"includeImage\":{\"type\":\"boolean\",\"default\":false,\"description\":\"Whether to include an example image\"}},\"required\":[\"messageType\"],\"additionalProperties\":false,\"$schema\":\"http://json-schema.org/draft-07/schema#\"}}]}}");
                    System.out.println("Sent SSE event to session: " + sessionId);
                } catch (IOException e) {
                    e.printStackTrace();
                    clients.remove(sessionId);  // Remove disconnected client
                }
            } else {
                System.out.println("Session not found: " + sessionId);
            }
        }
    }
}
