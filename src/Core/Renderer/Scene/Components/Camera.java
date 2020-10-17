package Core.Renderer.Scene.Components;

import Core.Renderer.Scene.Scene;
import Core.Renderer.Scene.SceneComponent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera extends SceneComponent {

    private static final long serialVersionUID = 3287874597857486658L;
    private float _pitch, _yaw, _fov, _zmin, _zmax;
    private static final Vector3f CAMERA_UP = new Vector3f(0, 1, 0);
    private boolean _bUsePerspective = true;


    private static final float MAX_PITCH = 80;
    private static final float MIN_PITCH = -80;

    public void addPitchInput(float delta) {
        _pitch += delta;
        if (_pitch < -80) _pitch = -80;
        if (_pitch > 80) _pitch = 80;
        sendRotation();
    }

    public void addYawInput(float delta) {
        _yaw += delta;
        sendRotation();
    }

    private void sendRotation() {
        Quaternionf rot = new Quaternionf().identity()
                .rotateZ((float)Math.toRadians(_pitch))
                .rotateY((float)Math.toRadians(_yaw));
        setRelativeRotation(rot);
    }

    public Camera(Scene parentScene) {
        super(new Vector3f().zero(), new Quaternionf().identity(), new Vector3f(1,1,1));
        attachToScene(parentScene);
        _fov = 45.f;
        _zmin = 0.001f;
        _zmax = 500.f;
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(getRelativePosition(), new Vector3f(getRelativePosition()).add(getForwardVector()), getUpVector());
    }

    @Override
    public void draw(Scene context) {}

    public void setFieldOfView(float fov) {
        _fov = fov;
        if (_fov < 1) _fov = 1;
        if (_fov > 160) _fov = 160;
    }

    public float getFieldOfView() {
        return _fov;
    }

    public float getNearClipPlane() {
        return _zmin;
    }

    public float getFarClipPlane() {
        return _zmax;
    }

    public void setPerspective(boolean bEnablePerspective) { _bUsePerspective = bEnablePerspective; }
    public boolean enablePerspective() { return _bUsePerspective; }
}
