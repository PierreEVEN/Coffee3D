package Core.Renderer.Scene.Components;

import Core.Renderer.Scene.Scene;
import Core.Renderer.Scene.SceneComponent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera extends SceneComponent {

    public float pitch, yaw, fov, zMin, zMax;
    private static final Vector3f CAMERA_UP = new Vector3f(0, 1, 0);
    private boolean _bUsePerspective = true;


    private static final float MAX_PITCH = 80;
    private static final float MIN_PITCH = -80;

    public void addPitchInput(float delta) {
        pitch += delta;
        if (pitch < -80) pitch = -80;
        if (pitch > 80) pitch = 80;
        sendRotation();
    }

    public void addYawInput(float delta) {
        yaw += delta;
        sendRotation();
    }

    private void sendRotation() {
        Quaternionf rot = new Quaternionf().identity()
                .rotateZ((float)Math.toRadians(pitch))
                .rotateY((float)Math.toRadians(yaw));
        setRelativeRotation(rot);
    }

    public Camera(Scene parentScene) {
        super(new Vector3f().zero(), new Quaternionf().identity(), new Vector3f(1,1,1));
        attachToScene(parentScene);
        fov = 45.f;
        zMin = 0.001f;
        zMax = 500.f;
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(getRelativePosition(), new Vector3f(getRelativePosition()).add(getForwardVector()), getUpVector());
    }

    @Override
    public void draw(Scene context) {}

    public void setFieldOfView(float fov) {
        this.fov = fov;
        if (this.fov < 1) this.fov = 1;
        if (this.fov > 160) this.fov = 160;
    }

    public float getFieldOfView() {
        return fov;
    }

    public float getNearClipPlane() {
        return zMin;
    }

    public float getFarClipPlane() {
        return zMax;
    }

    public void setPerspective(boolean bEnablePerspective) { _bUsePerspective = bEnablePerspective; }
    public boolean enablePerspective() { return _bUsePerspective; }
}
