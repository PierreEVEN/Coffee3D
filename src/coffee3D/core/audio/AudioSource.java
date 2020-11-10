package coffee3D.core.audio;

import coffee3D.core.assets.types.SoundWave;
import coffee3D.core.io.log.Log;
import coffee3D.core.resources.types.WaveResource;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import java.nio.IntBuffer;

public class AudioSource {

    private SoundWave _asset;
    private int _source;

    public AudioSource() {}

    public void setSource(SoundWave asset) {
        if (_asset != null) {
            AL10.alSourceStop(_source);
            AL10.alDeleteSources(_source);
        }

        _asset = asset;
        if (_asset != null) {
            AudioSystem.Get();
            int source = AL10.alGenSources();
            AL10.alSourcei(source, AL10.AL_BUFFER, _asset.getResource().getSource());
            AL10.alSource3f(source, AL10.AL_POSITION, 0, 0, 0);
            AL10.alSource3f(source, AL10.AL_VELOCITY, 0, 0, 0);
            AL10.alSourcef(source, AL10.AL_PITCH, 1);
            AL10.alSourcef(source, AL10.AL_GAIN, 1);
            AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_FALSE);

            AL10.alSourcePlay(source);
            Log.Display("play");
            _source = source;
        }
    }
}
