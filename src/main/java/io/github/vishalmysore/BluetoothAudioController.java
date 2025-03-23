package io.github.vishalmysore;



import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.*;

@RestController
public class BluetoothAudioController {

    private static final String AUDIO_FILE_PATH = "C:\\work\\choturobo\\speech.mp3"; // Path to the MP3 file
    private static final String BLUETOOTH_DEVICE_NAME = "speaker name"; // Change to your Bluetooth speaker's name

    @GetMapping("/send-audio")
    public String sendAudio(@RequestParam String command) {
        if (!"send".equalsIgnoreCase(command)) {
            return "Invalid command! Use 'send' to play audio.";
        }

        try {
            String btUrl = findBluetoothDevice(BLUETOOTH_DEVICE_NAME);
            if (btUrl == null) {
                return "Bluetooth device not found!";
            }

            sendMp3ToBluetooth(btUrl);
            return "Audio sent to Bluetooth device successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error sending audio: " + e.getMessage();
        }
    }

    private String findBluetoothDevice(String deviceName) throws BluetoothStateException {
        LocalDevice localDevice = LocalDevice.getLocalDevice();
        DiscoveryAgent agent = localDevice.getDiscoveryAgent();

        RemoteDevice[] devices = agent.retrieveDevices(DiscoveryAgent.PREKNOWN);
        if (devices != null) {
            for (RemoteDevice device : devices) {
                try {
                    if (deviceName.equals(device.getFriendlyName(false))) {
                        return "btspp://" + device.getBluetoothAddress() + ":1;authenticate=false;encrypt=false;master=false";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void sendMp3ToBluetooth(String btUrl) throws IOException {
        StreamConnection conn = (StreamConnection) Connector.open(btUrl);
        OutputStream os = conn.openOutputStream();
        FileInputStream fis = new FileInputStream(AUDIO_FILE_PATH);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }

        fis.close();
        os.close();
        conn.close();
    }
}

