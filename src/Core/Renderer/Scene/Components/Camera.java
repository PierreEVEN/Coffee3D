package Core.Renderer.Scene.Components;

import Core.Renderer.Scene.Scene;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera extends SceneComponent {

    private float _pitch, _yaw;
    private static final Vector3f CAMERA_UP = new Vector3f(0, 0, 1);

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

    public Vector3f getFront() {
        return new Vector3f(
                (float) Math.cos(Math.toRadians(_pitch)) * (float) Math.sin(Math.toRadians(_yaw)),
                (float) Math.sin(Math.toRadians(_pitch)),
                (float) Math.cos(Math.toRadians(_pitch)) * (float) Math.sin(Math.toRadians(_yaw))
        );
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(getPosition(), getPosition().add(getFront()), CAMERA_UP);
    }

    @Override
    public void draw(Scene context) {

    }
}
