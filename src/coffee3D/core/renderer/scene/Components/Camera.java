package coffee3D.core.renderer.scene.Components;

import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.core.types.TypeHelper;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera extends SceneComponent {

    private static final long serialVersionUID = 3287874597857486658L;
    protected float _pitch, _yaw, _fov, _zMin, _zMax;
    protected boolean _bUsePerspective = true;

    public void addPitchInput(float delta) {
        _pitch += delta;
        if (_pitch < -80) _pitch = -80;
        if (_pitch > 80) _pitch = 80;
        sendRotation();
    }

    public void setPitchInput(float pitch) {
        _pitch = pitch;
        sendRotation();
    }

    public void setYawInput(float yaw) {
        _yaw = yaw;
        sendRotation();
    }

    public void addYawInput(float delta) {
        _yaw += delta;
        sendRotation();
    }

    private void sendRotation() {
        Quaternionf rot = TypeHelper.getQuat().identity()
                .rotateY((float)Math.toRadians(-_pitch))
                .rotateZ((float)Math.toRadians(_yaw));
        setRelativeRotation(rot);
    }

    public Camera() {
        super(new Vector3f().zero(), new Quaternionf().identity(), new Vector3f(1,1,1));
        _fov = 45.f;
        _zMin = 0.1f;
        _zMax = 500.f;
    }

    public Matrix4f getViewMatrix() {
        return TypeHelper.getMat4().identity().lookAt(getRelativePosition(), TypeHelper.getVector3(getRelativePosition()).add(getForwardVector()), getUpVector());
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
        return _zMin;
    }

    public float getFarClipPlane() {
        return _zMax;
    }

    public void setPerspective(boolean bEnablePerspective) { _bUsePerspective = bEnablePerspective; }
    public boolean enablePerspective() { return _bUsePerspective; }
}
