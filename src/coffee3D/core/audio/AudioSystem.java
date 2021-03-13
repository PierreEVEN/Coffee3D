package coffee3D.core.audio;

import coffee3D.core.io.log.Log;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;

public class AudioSystem {

    private static AudioSystem _instance;
    public static AudioSystem Get() {
        if (_instance == null) _instance = new AudioSystem();
        return _instance;
    }

    private final long aiDevice;
    private final long aiContext;

    private AudioSystem() {
        //Start by acquiring the default device
        aiDevice = ALC10.alcOpenDevice((ByteBuffer)null);

        //Create a handle for the device capabilities, as well.
        ALCCapabilities deviceCaps = ALC.createCapabilities(aiDevice);
        // Create context (often already present, but here, necessary)
        IntBuffer contextAttribList = BufferUtils.createIntBuffer(16);

        // Note the manner in which parameters are provided to OpenAL...
        contextAttribList.put(ALC_REFRESH);
        contextAttribList.put(60);

        contextAttribList.put(ALC_SYNC);
        contextAttribList.put(ALC_FALSE);

        // Don't worry about this for now; deals with effects count
        contextAttribList.put(ALC_MAX_AUXILIARY_SENDS);
        contextAttribList.put(2);

        contextAttribList.put(0);
        contextAttribList.flip();
        //create the context with the provided attributes
        aiContext = ALC10.alcCreateContext(aiDevice, contextAttribList);

        if(!ALC10.alcMakeContextCurrent(aiContext)) Log.Fail("failed to make openal context current");

        AL.createCapabilities(deviceCaps);
    }
}
