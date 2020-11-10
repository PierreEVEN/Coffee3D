package coffee3D.core.resources.factories;

import coffee3D.core.io.log.Log;
import coffee3D.core.resources.types.WaveResource;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import sun.misc.IOUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.nio.ByteBuffer;


class AudioChannelFormat {
    public final static int MONO = 1;
    public final static int STEREO = 2;
}

public class AudioFactory {

    public static WaveResource FromFile(String resourceName, File source) {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(source);

            AudioFormat format = stream.getFormat();
            if (format.isBigEndian()) Log.Error("Failed to load audio source : can't handle Big Endian formats yet");

            int openALFormat = -1;
            switch (format.getChannels()) {
                case AudioChannelFormat.MONO:
                    switch (format.getSampleSizeInBits()) {
                        case 8:
                            openALFormat = AL10.AL_FORMAT_MONO8;
                            break;
                        case 16:
                            openALFormat = AL10.AL_FORMAT_MONO16;
                            break;
                    }
                    break;
                case AudioChannelFormat.STEREO:
                    switch (format.getSampleSizeInBits()) {
                        case 8:
                            openALFormat = AL10.AL_FORMAT_STEREO8;
                            break;
                        case 16:
                            openALFormat = AL10.AL_FORMAT_STEREO16;
                            break;
                    }
                    break;
            }

            //load data into a byte buffer
            //I've elected to use IOUtils from Apache Commons here, but the core
            //notion is to load the entire stream into the byte array--you can
            //do this however you would like.
            byte[] audioBuffer = IOUtils.readAllBytes(stream);
            ByteBuffer audioData = BufferUtils.createByteBuffer(audioBuffer.length).put(audioBuffer);
            audioData.flip();
            return new WaveResource(resourceName, openALFormat, format, audioData, stream);
        }
        catch (Exception e) {
            Log.Error("Failed to read audio data : " + e.getMessage());
        }
        Log.Error("failed to load audio data");
        return null;
    }
}
