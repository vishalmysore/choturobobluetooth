package io.github.vishalmysore.service;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

@Agent
public class ArduinoService {
    @Value("${arduino.ip}")
    private String arduinoIp;

    private final RestTemplate restTemplate;

    public ArduinoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Action(description = "this action is to move Chotu robot forward")
    public String moveChotuForward(int steps) {
        String url = "http://" + arduinoIp + "/moveForward?steps=" + steps;
        return restTemplate.getForObject(url, String.class);
    }

    @Action(description = "this action is to move Chotu robot backward")
    public String moveChotuBackward(int steps) {
        String url = "http://" + arduinoIp + "/moveBackward?steps=" + steps;
        return restTemplate.getForObject(url, String.class);
    }

    @Action(description = "stop the Chotu robot")
    public String stopChotu(int steps) {
        String url = "http://" + arduinoIp + "/stop";
        return restTemplate.getForObject(url, String.class);
    }

    @Action(description = "move chotu robot in random direction, make it dance")
    public String moveRandom(int steps) {
        String url = "http://" + arduinoIp + "/moveRandom";
        return restTemplate.getForObject(url, String.class);
    }

    @Action(description = "push and pull chotu move it back and forth")
    public String pushPull(int steps) {
        String url = "http://" + arduinoIp + "/pushPull";
        return restTemplate.getForObject(url, String.class);
    }

    @Action(description = "rotate chotu robot counterclockwise")
    public String rotateCounterclockwise(int degrees) {
        String url = "http://" + arduinoIp + "/rotateCounterclockwise?degrees=" + degrees;
        return restTemplate.getForObject(url, String.class);
    }

    @Action(description = "make chotu robot jump")
    public String jumpChotu(int height) {
        String url = "http://" + arduinoIp + "/jump?height=" + height;
        return restTemplate.getForObject(url, String.class);
    }

    @Action(description = "make chotu robot spin")
    public String spinChotu(int speed) {
        String url = "http://" + arduinoIp + "/spin?speed=" + speed;
        return restTemplate.getForObject(url, String.class);
    }
    @Action(description = "move chotu robot diagonally")
    public String moveDiagonally(int steps) {
        String url = "http://" + arduinoIp + "/moveDiagonally?steps=" + steps;
        return restTemplate.getForObject(url, String.class);
    }

    @Action(description = "make chotu robot play a sound")
    public String playSound(String sound) {
        String url = "http://" + arduinoIp + "/playSound?sound=" + sound;
        return restTemplate.getForObject(url, String.class);
    }

    @Action(description = "make chotu robot flash its lights")
    public String flashLights(int duration) {
        String url = "http://" + arduinoIp + "/flashLights?duration=" + duration;
        return restTemplate.getForObject(url, String.class);
    }

    @Action(description = "make chotu robot take a picture")
    public String takePicture() {
        String url = "http://" + arduinoIp + "/takePicture";
        return restTemplate.getForObject(url, String.class);
    }

    @Action(description = "send chotu robot's status")
    public String sendStatus() {
        String url = "http://" + arduinoIp + "/sendStatus";
        return restTemplate.getForObject(url, String.class);
    }
}
