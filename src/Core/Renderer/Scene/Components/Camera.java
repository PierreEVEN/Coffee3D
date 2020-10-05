package Core.Renderer.Scene.Components;

import Core.Renderer.Scene.Scene;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera extends SceneComponent {

    private float _pitch, _yaw;

    private static final float MAX_PITCH = 80;
    private static final float MIN_PITCH = -80;

    public void addPitchInput(float delta) {
        _pitch += delta;
        if (_pitch > MAX_PITCH) _pitch = MAX_PITCH;
        if (_pitch < MIN_PITCH) _pitch = MIN_PITCH;
    }

    public void addyawInput(float delta) {
        _yaw += delta;
    }

    public Camera(Vector3f position, Quaternionf rotation) {
        super(position, rotation);
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f();
    }

    @Override
    public void draw(Scene context) {

    }
}
