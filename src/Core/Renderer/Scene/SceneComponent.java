package Core.Renderer.Scene;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public abstract class SceneComponent {
    private Vector3f _position;
    private Quaternionf _rotation;
    private Vector3f _scale;
    private SceneComponent _parent = null;
    private List<SceneComponent> _children = null;
    private Scene _parentScene;

    protected SceneComponent(Vector3f position, Quaternionf rotation, Vector3f scale) {
        _position = position;
        _rotation = rotation;
        _scale = scale;
    }

    public Vector3f getLocalPosition() {
        return _position;
    }
    public Quaternionf getLocalRotation() { return _rotation; }
    public Vector3f getLocalScale() { return _scale; }

    public Matrix4f getLocalTransformationMatrix() {
        return new Matrix4f()
                .identity()
                .translate(getLocalPosition())
                .rotate(getLocalRotation())
                .scale(getLocalScale());
    }

    public Matrix4f getWorldTransformationMatrix() {
        if (_parent != null) {
            return new Matrix4f(_parent.getWorldTransformationMatrix()).mul(getLocalTransformationMatrix());
        }
        else {
            return getLocalTransformationMatrix();
        }
    }

    public final void drawInternal(Scene context) {
        draw(context);
        if (_children != null) {
            for (SceneComponent comp : _children) {
                comp.drawInternal(context);
            }
        }
    }

    protected abstract void draw(Scene context);

    public void setPosition(Vector3f position) {
        _position = position;
    }

    public void setRotation(Quaternionf quat) {
        _rotation = quat;
    }

    public void setScale(Vector3f scale) {
        _scale = scale;
    }

    public float getRoll() {
        Vector3f angles = new Vector3f();
        getLocalRotation().getEulerAnglesXYZ(angles);
        return angles.x;
    }

    public float getPitch() {
        Vector3f angles = new Vector3f();
        getLocalRotation().getEulerAnglesXYZ(angles);
        return angles.y;
    }

    public float getYaw() {
        Vector3f angles = new Vector3f();
        getLocalRotation().getEulerAnglesXYZ(angles);
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
        setPosition(new Vector3f(getLocalPosition()).add(offset));
    }

    public void addLocalOffset(Vector3f offset) {
        Vector3f worldOffset = new Vector3f().zero()
                .add(new Vector3f(getForwardVector()).mul(offset.x))
                .add(new Vector3f(getRightVector()).mul(offset.y))
                .add(new Vector3f(getUpVector()).mul(offset.z));
        addWorldOffset(worldOffset);
    }

    public void attachToScene(Scene parentScene) {
        if (parentScene == null) return;
        detach();
        _parentScene = parentScene;
        parentScene.attachComponent(this);
    }

    public void attachToComponent(SceneComponent parent) {
        if (parent == null) return;
        if (parent._children == null) parent._children = new ArrayList<>();
        detach();
        _parent = parent;
        if (!_parent._children.contains(this)) _parent._children.add(this);
    }

    public void detach() {
        if (_parent != null && _parent._children.contains(this)) _parent._children.remove(this); //Detach from parent component
        if (_parentScene != null) _parentScene.detachComponent(this); //Detach from scene
        _parentScene = null;
        _parent = null;
    }
}
