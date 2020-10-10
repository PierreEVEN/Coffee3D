package Core.Renderer.Scene.Components;

import Core.Renderer.Scene.Scene;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera extends SceneComponent {

    private float _fov, _zmin, _zmax;
    private static final Vector3f CAMERA_UP = new Vector3f(0, 1, 0);
    private boolean _bUsePerspective = true;


    private static final float MAX_PITCH = 80;
    private static final float MIN_PITCH = -80;

    public void addPitchInput(float delta) {
        getRotation().rotateZ((float)Math.toRadians(delta));
    }

    public void addYawInput(float delta) {
        getRotation().rotateY((float)Math.toRadians(delta));
    }

    public Camera(Vector3f position, Quaternionf rotation) {
        super(position, rotation);
        _fov = 45.f;
        _zmin = 0.0001f;
        _zmax = 100.f;
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(getPosition(), new Vector3f(getPosition()).add(getForwardVector()), CAMERA_UP);
    }

    @Override
    public void draw(Scene context) {}

    public void setFieldOfView(float fov) {
        _fov = fov;
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
