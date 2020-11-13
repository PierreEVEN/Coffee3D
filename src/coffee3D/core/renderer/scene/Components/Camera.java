package coffee3D.core.renderer.scene.Components;

import coffee3D.core.audio.IAudioListener;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.core.types.TypeHelper;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera extends SceneComponent implements IAudioListener {
    private static final long serialVersionUID = 3287874597857486658L;

    protected float _pitch, _yaw, _fov, _zMin, _zMax;
    protected boolean _bUsePerspective = true;

    public void addPitchInput(float delta) {
        _pitch += delta;
        if (_pitch < -80) _pitch = -80;
        if (_pitch > 80) _pitch = 80;
    }

    public void setPitchInput(float pitch) {
        _pitch = pitch;
    }

    public void setYawInput(float yaw) {
        _yaw = yaw;
    }

    public void addYawInput(float delta) {
        _yaw += delta;
    }

    private void sendRotation() {
        setRelativeRotation(getRelativeRotation().identity().rotateZYX((float)Math.toRadians(-_yaw), (float)Math.toRadians(_pitch), 0));
    }

    public Camera() {
        super(new Vector3f().zero(), new Quaternionf().identity(), new Vector3f(1,1,1));
        _fov = 45.f;
        _zMin = 0.5f;
        _zMax = 1000.f;
    }

    public Matrix4f getViewMatrix() {
        return TypeHelper.getMat4().identity().lookAt(getRelativePosition(), TypeHelper.getVector3(getRelativePosition()).add(getForwardVector()), getUpVector());
    }

    @Override
    public void draw(Scene context) {
        sendRotation();
    }

    public void setFieldOfView(float fov) {
        _fov = fov;
        if (_fov < 1) _fov = 1;
        if (_fov > 160) _fov = 160;
    }

    public float getFieldOfView() {
        return _fov;
    }

    public float getNearClipPlane() {
        return _zMin;
    }

    public float getFarClipPlane() {
        return _zMax;
    }

    public void setPerspective(boolean bEnablePerspective) { _bUsePerspective = bEnablePerspective; }
    public boolean enablePerspective() { return _bUsePerspective; }

    @Override
    public Vector3f getListenerPosition() {
        return getWorldPosition();
    }

    @Override
    public Vector3f getListenerForwardVector() {
        return getForwardVector();
    }

    @Override
    public Vector3f getListenerUpVector() {
        return getUpVector();
    }
}
