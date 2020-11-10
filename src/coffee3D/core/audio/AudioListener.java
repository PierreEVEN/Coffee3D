package coffee3D.core.audio;

import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.renderer.scene.RenderScene;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import sun.misc.IOUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;

public class AudioListener {

    private final FloatBuffer _listenerOrientation;
    private IAudioListener _listener;

    private static AudioListener _instance;
    public static AudioListener Get() {
        if (_instance == null) _instance = new AudioListener();
        return _instance;
    }

    private AudioListener() {
        coffee3D.core.audio.AudioSystem.Get();
        _listenerOrientation = BufferUtils.createFloatBuffer(6 * 4);
    }


    public void bindListener(IAudioListener listener) {
        _listener = listener;
    }

    public void tick() {
        Vector3f forwardVector = _listener.getListenerForwardVector();
        Vector3f upVector = _listener.getListenerUpVector();
        Vector3f position = _listener.getListenerPosition();
        _listenerOrientation.put( 0, forwardVector.x );
        _listenerOrientation.put( 1, forwardVector.y );
        _listenerOrientation.put( 2, forwardVector.z );
        _listenerOrientation.put( 3, upVector.x );
        _listenerOrientation.put( 4, upVector.y );
        _listenerOrientation.put( 5, upVector.z );
        AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
        AL10.alListenerfv(AL10.AL_ORIENTATION, _listenerOrientation);
    }
}
