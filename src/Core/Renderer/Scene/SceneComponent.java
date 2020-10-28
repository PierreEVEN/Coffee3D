package Core.Renderer.Scene;

import Core.IO.LogOutput.Log;
import Core.Maths.MathLibrary;
import Core.Renderer.RenderUtils;
import Core.Types.Color;
import Core.Types.SphereBound;
import Core.Types.TypeHelper;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.openvr.RenderModel;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_S;
import static org.lwjgl.opengl.GL11.GL_SELECT;

public class SceneComponent implements Serializable {
    private static final long serialVersionUID = 744620683032598971L;

    protected String _componentName;
    private transient boolean _showOutlines;

    public boolean doesDisplayOutlines() { return _showOutlines && RenderUtils.RENDER_MODE != GL_SELECT; }
    public void setOutlined(boolean enableOutlines) { _showOutlines = enableOutlines; }

    public String getComponentName() {
        return _componentName;
    }

    public void setComponentName(String componentName) {
        _componentName = componentName;
    }

    public SphereBound getBound() { return SphereBound.GetPoint(getWorldPosition()); }

    /**
     * constructor
     * @param position relative position
     * @param rotation relative rotation
     * @param scale    relative scale
     */
    public SceneComponent(Vector3f position, Quaternionf rotation, Vector3f scale) {
        this._position = position;
        this._rotation = rotation;
        this._scale = scale;
        _componentName = getClass().getSimpleName();
        _showOutlines = false;
    }


    /**
     * draw a root component and its children to given scene
     * @param context draw context
     */
    protected final void drawInternal(Scene context) {
        draw(context);
        if (_children != null) {
            for (SceneComponent comp : _children) {
                comp.drawInternal(context);
            }
        }
    }

    /**
     * implement to draw stuff
     * @param context scene context
     */
    protected void draw(Scene context) {}


    /******************************************************************/
    /*                                                                */
    /*                         TRANSLATION                            */
    /*                                                                */
    /******************************************************************/

    /**
     * relative position
     */
    protected Vector3f _position;

    /**
     * relative rotation
     */
    protected Quaternionf _rotation;

    /**
     * relative scale
     */
    protected Vector3f _scale;

    /**
     * get component position relative to it's parent
     * @return local position
     */
    public Vector3f getWorldPosition() { return _parent == null ? getRelativePosition() : getWorldTransformationMatrix().transformPosition(TypeHelper.getVector3(getRelativePosition())); }

    /**
     * get component position relative to it's parent
     * @return local position
     */
    public Vector3f getRelativePosition() { return _position; }

    /**
     * get component rotation relative to it's parent
     * @return local rotation
     */
    public Quaternionf getRelativeRotation() { return _rotation; }

    /**
     * get component scale relative to it's parent
     * @return local scale
     */
    public Vector3f getRelativeScale() { return _scale; }

    /**
     * build relative transformation matrix
     * @return relative transform
     */
    public Matrix4f getRelativeTransformationMatrix() {
        return TypeHelper.getMat4()
                .identity()
                .translate(getRelativePosition())
                .rotate(getRelativeRotation())
                .scale(getRelativeScale());
    }

    /**
     * build absolute transformation matrix
     * @return world transform
     */
    public Matrix4f getWorldTransformationMatrix() {
        if (_parent != null) {
            return TypeHelper.getMat4().set(_parent.getWorldTransformationMatrix()).mul(getRelativeTransformationMatrix());
        }
        else {
            return getRelativeTransformationMatrix();
        }
    }

    /**
     * set component relative position
     * @param position local position
     */
    public void setRelativePosition(Vector3f position) { this._position = position; }

    /**
     * set component relative rotation
     * @param quat local rotation
     */
    public void setRelativeRotation(Quaternionf quat) { _rotation = quat; }

    /**
     * set component relative scale
     * @param scale  local scale
     */
    public void setRelativeScale(Vector3f scale) { this._scale = scale; }

    /**
     * get component relative roll axis
     * @return local roll axis
     */
    public float getRoll() {
        Vector3f angles = TypeHelper.getVector3();
        getRelativeRotation().getEulerAnglesXYZ(angles);
        return angles.x;
    }

    /**
     * get component relative pitch axis
     * @return local pitch axis
     */
    public float getPitch() {
        Vector3f angles = TypeHelper.getVector3();
        getRelativeRotation().getEulerAnglesXYZ(angles);
        return angles.z;
    }

    /**
     * get component relative yaw axis
     * @return local yaw axis
     */
    public float getYaw() {
        Vector3f angles = TypeHelper.getVector3();
        getRelativeRotation().getEulerAnglesXYZ(angles);
        return angles.y;
    }

    /**
     * get component local forward unit vector
     * @return forward vector
     */
    public Vector3f getForwardVector() {
        Vector3f vec = TypeHelper.getVector3();
        _rotation.normalizedPositiveX(vec);
        return vec;
    }

    /**
     * get component local right unit vector
     * @return right vector
     */
    public Vector3f getRightVector() {
        Vector3f vec = TypeHelper.getVector3();
        _rotation.normalizedPositiveY(vec);
        return vec.mul(-1f);
    }

    /**
     * get component local up unit vector
     * @return up vector
     */
    public Vector3f getUpVector() {
        Vector3f vec = TypeHelper.getVector3();
        _rotation.normalizedPositiveZ(vec);
        return vec;
    }

    /**
     * add local position offset to this component rotation
     * @param offset local movement
     */
    public void addLocalOffset(Vector3f offset) {
        _position.x += getForwardVector().x * offset.x +
                getRightVector().x * offset.y +
                getUpVector().x * offset.z;
        _position.y += getForwardVector().y * offset.x +
                getRightVector().y * offset.y +
                getUpVector().y * offset.z;
        _position.z += getForwardVector().z * offset.x +
                getRightVector().z * offset.y +
                getUpVector().z * offset.z;
    }

    /**
     * add local position offset to parent component rotation
     * @param offset relative movement
     */
    public void addRelativeOffset(Vector3f offset) {
        _position.x += offset.x;
        _position.y += offset.y;
        _position.z += offset.z;
    }
    
    /******************************************************************/
    /*                                                                */
    /*                         SCENE GRAPH                            */
    /*                                                                */
    /******************************************************************/

    /**
     * parent component
     */
    private SceneComponent _parent = null;

    /**
     * attached children
     */
    private List<SceneComponent> _children = null;

    /**
     * parent scene
     */
    private transient Scene _parentScene;

    public List<SceneComponent> getChildren() { return _children; }

    /**
     * make this component a root of the given scene.
     * (also detach this component from it's previous parent)
     * @param parentScene
     */
    public void attachToScene(Scene parentScene) {
        if (parentScene == null) return;
        detach();
        _parentScene = parentScene;
        parentScene.attachComponent(this);
    }

    /**
     * make given component parent of this one
     * (also detach this component from it's previous parent)
     * @param parent
     */
    public void attachToComponent(SceneComponent parent) {
        if (parent == null) return;
        if (parent._children == null) parent._children = new ArrayList<>();
        detach();
        _parent = parent;
        if (!_parent._children.contains(this)) _parent._children.add(this);
    }

    /**
     * detach component from parent scene or component.
     * don't forget to attach it to a scene or component
     * else it become a zombie component
     */
    public void detach() {
        if (_parent != null && _parent._children.contains(this)) _parent._children.remove(this); //Detach from parent component
        if (_parentScene != null) _parentScene.detachComponent(this); //Detach from scene
        _parentScene = null;
        _parent = null;
    }

    /**
     * a zombie component is a component that has no parent scene or component.
     * (should never append)
     * @return is zombie
     */
    public boolean isZombie() {
        return _parentScene == null && _parent == null;
    }

    /**
     * Is this component a root component
     * true if no parent component or scene is found
     * @return is root
     */
    public boolean isRoot() {
        return _parentScene != null && _parent == null;
    }

    /**
     * Test if this component has given parent in it's hierarchy.
     * @param parent wanted parent
     * @return has parent
     */
    public boolean isChildOf(SceneComponent parent) {
        if (parent == this) return true;
        if (_parent != null) return _parent.isChildOf(parent);
        return false;
    }

    /**
     * Test if this component has given child in it's hierarchy
     * @param child wanted child
     * @return has child
     */
    public boolean isParentOf(SceneComponent child) {
        if (child == this) return true;
        if (_children != null) {
            for (SceneComponent tChild : _children) {
                if (tChild.isParentOf(child)) return true;
            }
        }
        return false;
    }
}
