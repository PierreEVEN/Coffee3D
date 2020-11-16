package coffee3D.core.renderer.scene;

import coffee3D.core.assets.types.MaterialInterface;
import coffee3D.core.assets.types.Texture2D;
import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.renderer.AssetReferences;
import coffee3D.core.renderer.RenderMode;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.resources.factories.MaterialFactory;
import coffee3D.core.resources.factories.MeshFactory;
import coffee3D.core.resources.types.MaterialResource;
import coffee3D.core.resources.types.MeshResource;
import coffee3D.core.resources.types.Texture2DResource;
import coffee3D.core.resources.types.TextureResource;
import coffee3D.core.types.SphereBound;
import coffee3D.core.types.Vertex;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.*;

public class SceneComponent implements Serializable {
    private static final long serialVersionUID = 744620683032598971L;

    protected String _componentName;
    private transient int _stencilValue;
    private transient boolean _isSelected;

    public int getStencilValue() { return _stencilValue; }
    public void setStencilValue(int stencilValue) { _stencilValue = stencilValue; }
    public void setIsSelected(boolean isSelected) { setStencilValue(isSelected ? 1 : 0); _isSelected = isSelected; }
    public boolean isSelected() { return _isSelected; }

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
        _position = position;
        _rotation = rotation;
        _scale = scale;

        _componentName = getClass().getSimpleName();
        _stencilValue = 0;
    }

    protected final void preDrawInternal(Scene context) {
        preDraw(context);
        if (_children != null) {
            for (SceneComponent comp : _children) {
                comp.preDrawInternal(context);
            }
        }
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

    protected final void postDrawInternal(Scene context) {
        postDraw(context);
        if (_children != null) {
            for (SceneComponent comp : _children) {
                comp.postDrawInternal(context);
            }
        }
    }

    protected void preDraw(Scene context) {}

    /**
     * implement to draw stuff
     * @param context scene context
     */
    protected void draw(Scene context) {
        if (RenderUtils.WITH_EDITOR) drawBillboard(context, null, (float) Math.max(0.3, Math.min(2, ((RenderScene)context).getCamera().getWorldPosition().distance(getWorldPosition()) / 50)));
    }

    protected void postDraw(Scene context) {}

    public TextureResource getComponentIcon() { return AssetReferences.GetIconSceneComponent().getResource(); }

    private static MeshResource _billboardMesh;
    private static MaterialResource _billboardMaterial;
    private static MeshResource GetBillboardMesh() {
        if (_billboardMesh == null) {
            Vertex[] vertices = new Vertex[]{
                    new Vertex(new Vector3f(-1, -1, 0), new Vector2f(0, 0)),
                    new Vertex(new Vector3f(1, -1, 0), new Vector2f(1, 0)),
                    new Vertex(new Vector3f(1, 1, 0), new Vector2f(1, 1)),
                    new Vertex(new Vector3f(-1, 1, 0), new Vector2f(0, 1)),
            };
            int[] triangles = new int[]{0, 1, 2, 0, 2, 3};
            _billboardMesh = MeshFactory.FromResources("BillboardMesh", vertices, triangles);
        }
        return _billboardMesh;
    }
    private static MaterialResource GetBillboardMaterial() {
        if (_billboardMaterial == null) {
            _billboardMaterial = MaterialFactory.FromFiles("BillboardMaterial", AssetReferences.BILLBOARD_MATERIAL_PATH + ".vert", AssetReferences.BILLBOARD_MATERIAL_PATH + ".frag");
        }
        return _billboardMaterial;
    }

    protected void drawBillboard(Scene context, Texture2D texture, float size) {
        switch (RenderUtils.RENDER_MODE) {
            case Select:
                RenderUtils.getBillboardPickMaterial().use(context);
                RenderUtils.getBillboardPickMaterial().getResource().setIntParameter("pickId", getComponentIndex() + 1);
                RenderUtils.getBillboardPickMaterial().getResource().setVec4Parameter("position", getBound().position, size);
                GetBillboardMesh().use(context);
                break;
            case Color:
                GetBillboardMaterial().use(context);
                GetBillboardMaterial().setVec4Parameter("position", getBound().position, size);
                RenderUtils.ActivateTexture(0);
                GetBillboardMaterial().setIntParameter("image", 0);
                if (texture == null) glBindTexture(GL_TEXTURE_2D, getComponentIcon().getTextureHandle());
                else glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
                GetBillboardMesh().use(context);
                break;
            case Stencil: {
                if (getStencilValue() != 0) {
                    RenderUtils.getBillboardPickMaterial().use(context);
                    RenderUtils.getBillboardPickMaterial().getResource().setIntParameter("pickId", getStencilValue());
                    RenderUtils.getBillboardPickMaterial().getResource().setVec4Parameter("position", getBound().position, size);
                    GetBillboardMesh().use(context);
                }
                break;
            }
        }
    }


    /*                                                                */
    /*                         TRANSLATION                            */
    /*                                                                */

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

    private transient Vector3f _forwardVector = new Vector3f();
    private transient Vector3f _rightVector = new Vector3f();
    private transient Vector3f _upVector = new Vector3f();
    private transient Vector3f _eulerAngles = new Vector3f();
    private transient Vector3f _worldPosition = new Vector3f();
    private transient Matrix4f relativeTransformationMatrix = new Matrix4f();
    private transient Matrix4f worldTransformationMatrix = new Matrix4f();

    /**
     * get component position relative to it's parent
     * @return local position
     */
    public Vector3f getWorldPosition() {
        if (_worldPosition == null) _worldPosition = new Vector3f();
        return _parent == null ?
                getRelativePosition() :
                getWorldTransformationMatrix().transformPosition(_worldPosition);
    }

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
        if (relativeTransformationMatrix == null) relativeTransformationMatrix = new Matrix4f();
        return relativeTransformationMatrix
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
            if (worldTransformationMatrix == null) worldTransformationMatrix = new Matrix4f();
            return worldTransformationMatrix.set(_parent.getWorldTransformationMatrix()).mul(getRelativeTransformationMatrix());
        }
        else {
            return getRelativeTransformationMatrix();
        }
    }

    /**
     * set component relative position
     * @param position local position
     */
    public void setRelativePosition(Vector3f position) { _position.set(position); }

    /**
     * set component relative rotation
     * @param quat local rotation
     */
    public void setRelativeRotation(Quaternionf quat) { _rotation.set(quat); }

    /**
     * set component relative scale
     * @param scale  local scale
     */
    public void setRelativeScale(Vector3f scale) { _scale.set(scale); }

    /**
     * get component relative roll axis
     * @return local roll axis
     */
    public float getRoll() {
        if (_eulerAngles == null) _eulerAngles = new Vector3f();
        getRelativeRotation().getEulerAnglesXYZ(_eulerAngles);
        return _eulerAngles.x;
    }

    /**
     * get component relative pitch axis
     * @return local pitch axis
     */
    public float getPitch() {
        if (_eulerAngles == null) _eulerAngles = new Vector3f();
        getRelativeRotation().getEulerAnglesXYZ(_eulerAngles);
        return _eulerAngles.z;
    }

    /**
     * get component relative yaw axis
     * @return local yaw axis
     */
    public float getYaw() {
        if (_eulerAngles == null) _eulerAngles = new Vector3f();
        getRelativeRotation().getEulerAnglesXYZ(_eulerAngles);
        return _eulerAngles.y;
    }

    /**
     * get component local forward unit vector
     * @return forward vector
     */
    public Vector3f getForwardVector() {
        if (_forwardVector == null) _forwardVector = new Vector3f();
        _rotation.transform(_forwardVector.set(1, 0,0));
        return _forwardVector;
    }

    /**
     * get component local right unit vector
     * @return right vector
     */
    public Vector3f getRightVector() {
        if (_rightVector == null) _rightVector = new Vector3f();
        _rotation.transform(_rightVector.set(0, 1,0));
        return _rightVector;
    }

    /**
     * get component local up unit vector
     * @return up vector
     */
    public Vector3f getUpVector() {
        if (_upVector == null) _upVector = new Vector3f();
        _rotation.transform(_upVector.set(0, 0,1));
        return _upVector;
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

    /*                                                                */
    /*                         SCENE GRAPH                            */
    /*                                                                */

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
     * @param parentScene parent scene
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
     * @param parent parent
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
        if (_parent != null) _parent._children.remove(this); //Detach from parent component
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

    public SceneComponent getParent() {
        return _parent;
    }

    private int _componentIndex;

    public void setComponentIndex(int index) { _componentIndex = index; }
    public int getComponentIndex() { return _componentIndex; }
}
