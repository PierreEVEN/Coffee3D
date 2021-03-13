package coffee3D.core.resources.types;

import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.resources.GraphicResource;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class WaveResource extends GraphicResource {

    private IntBuffer _audioPtr;
    private long _length;
    private final int _openAlFormat;
    private final AudioFormat _audioFormat;
    private AudioInputStream _audioStream;
    private ByteBuffer _audioData;

    public WaveResource(String resourceName, int openAlFormat, AudioFormat audioFormat, ByteBuffer audioData, AudioInputStream audioStream) {
        super(resourceName);
        _openAlFormat = openAlFormat;
        _audioFormat = audioFormat;
        _audioStream = audioStream;
        _audioData = audioData;
    }

    public long getLength() { return _length; }
    public int getSource() { return _audioPtr == null ? 0 : _audioPtr.get(); }

    @Override
    public void load() {
        _audioPtr = BufferUtils.createIntBuffer(1);
        AL10.alBufferData(_audioPtr.get(), _openAlFormat, _audioData, (int) _audioFormat.getSampleRate());
        _length = (long) (1000f * _audioStream.getFrameLength() / _audioFormat.getFrameRate());
        _audioStream = null;
        _audioData = null;
    }

    @Override
    public void unload() {}

    @Override
    public void use(Scene context) {

    }
}
