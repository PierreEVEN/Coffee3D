package coffee3D.core.audio;

import org.joml.Vector3f;

public interface IAudioListener {

    Vector3f getListenerPosition();
    Vector3f getListenerForwardVector();
    Vector3f getListenerUpVector();


}
