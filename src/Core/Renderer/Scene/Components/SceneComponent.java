package Core.Renderer.Scene.Components;

import Core.Renderer.Scene.Scene;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public abstract class SceneComponent {
    private Vector3f _position;
    private Quaternionf _rotation;
    private SceneComponent _parent = null;
    private List<SceneComponent> _children = null;

    protected SceneComponent(Vector3f position, Quaternionf rotation) {
        _position = position;
        _rotation = rotation;
    }

    public Vector3f getPosition() {
        return _position;
    }

    public abstract void draw(Scene context);

    public void setPosition(Vector3f position) {
        _position = position;
    }

    public void setRotation(Quaternionf quat) {
        _rotation = quat;
    }

    public Quaternionf getRotation() { return _rotation; }

    public float getRoll() {
        Vector3f angles = new Vector3f();
        getRotation().getEulerAnglesXYZ(angles);
        return angles.x;
    }

    public float getPitch() {
        Vector3f angles = new Vector3f();
        getRotation().getEulerAnglesXYZ(angles);
        return angles.y;
    }

    public float getYaw() {
        Vector3f angles = new Vector3f();
        getRotation().getEulerAnglesXYZ(angles);
        return angles.z;
    }

    public Vector3f getForwardVector() {
        Vector3f vec = new Vector3f();
        _rotation.normalizedPositiveX(vec);
        return vec;
    }

    public Vector3f getRightVector() {
        Vector3f vec = new Vector3f();
        _rotation.normalizedPositiveZ(vec);
        return vec;
    }

    public Vector3f getUpVector() {
        Vector3f vec = new Vector3f();
        _rotation.normalizedPositiveY(vec);
        return vec;
    }

    public void addWorldOffset(Vector3f offset) {
        setPosition(new Vector3f(getPosition()).add(offset));
    }

    public void addLocalOffset(Vector3f offset) {
        Vector3f worldOffset = new Vector3f().zero()
                .add(new Vector3f(getForwardVector()).mul(offset.x))
                .add(new Vector3f(getRightVector()).mul(offset.y))
                .add(new Vector3f(getUpVector()).mul(offset.z));
        addWorldOffset(worldOffset);
    }

    public void attachToComponent(SceneComponent parent) {
        detach();
        if (parent == null) return;
        _parent = parent;
        if (!_parent._children.contains(this)) _parent._children.add(this);
    }

    public void detach() {
        if (_parent != null && _parent._children.contains(this)) _parent._children.remove(this);
        _parent = null;
    }
}
