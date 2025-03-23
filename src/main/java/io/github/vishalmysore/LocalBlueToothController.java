package io.github.vishalmysore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
@RestController
public class LocalBlueToothController {
    private static final String AUDIO_FILE_PATH = "C:/temp/speech.mp3";
    private static final String SONG_FILE_PATH = "C:/temp/bolly1.mp3";
    @Autowired
    private TextToSpeech textToSpeechService;
    @GetMapping("/sendAudioLocal")
    public String sendAudio(@RequestParam String textToPlay) {

        try {

            textToSpeechService.fetchAndSaveSpeech(textToPlay);

            play(AUDIO_FILE_PATH);
            return "Audio sent to Bluetooth device successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error sending audio: " + e.getMessage();
        }
    }

    @GetMapping("/singASong")
    public String singASong(@RequestParam String actor) {

        try {

            play(SONG_FILE_PATH);
            return "Audio sent to Bluetooth device successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error sending audio: " + e.getMessage();
        }
    }

    public void play(String path) {
        final File file = new File(path);

        try (final AudioInputStream in = getAudioInputStream(file)) {

            final AudioFormat outFormat = getOutFormat(in.getFormat());
            final Info info = new Info(SourceDataLine.class, outFormat);

            try (final SourceDataLine line =
                         (SourceDataLine) AudioSystem.getLine(info)) {

                if (line != null) {
                    line.open(outFormat);
                    line.start();
                    stream(getAudioInputStream(outFormat, in), line);
                    line.drain();
                    line.stop();
                }
            }

        } catch (UnsupportedAudioFileException
                 | LineUnavailableException
                 | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();

        final float rate = inFormat.getSampleRate();
        return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    private void stream(AudioInputStream in, SourceDataLine line)
            throws IOException {
        final byte[] buffer = new byte[4096];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }
}
