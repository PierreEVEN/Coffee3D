package coffee3D.editor.components;

import coffee3D.core.IEngineModule;
import coffee3D.core.controller.DefaultController;
import coffee3D.core.io.inputs.GlfwInputHandler;
import coffee3D.core.io.inputs.IInputListener;
import coffee3D.core.io.log.Log;
import coffee3D.core.maths.MathLibrary;
import coffee3D.core.renderer.AssetReferences;
import coffee3D.core.renderer.Window;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.core.resources.factories.ImportZAxis;
import coffee3D.core.resources.factories.MaterialFactory;
import coffee3D.core.resources.factories.MeshFactory;
import coffee3D.core.resources.types.MaterialResource;
import coffee3D.core.resources.types.MeshResource;
import coffee3D.editor.controller.EditorController;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.naming.ldap.Control;


public class GizmoComponent extends SceneComponent implements IInputListener {
    private static final long serialVersionUID = -5687518908809847596L;

    private static transient MeshResource gizmoMesh[];
    private static transient MaterialResource gizmoMaterial;

    private transient final Vector3f _baseLocation = new Vector3f();
    private transient SceneComponent _attachedComponent = null;
    private transient final Vector3f _lastDir = new Vector3f();
    private transient final Vector3f _dir = new Vector3f();
    private transient final Vector3f _dirb = new Vector3f();
    private transient final Vector3f _movementVector = new Vector3f();
    private transient final Vector3f _localMovement = new Vector3f();

    public GizmoComponent() {
        super(new Vector3f().zero(), new Quaternionf().identity(), new Vector3f(1,1,1));
        _bCanTranslate = false;
        _gizmoMovementAxisOverride = GizmoMovementDirection.NULL;
        gizmoMovementMode = GizmoMovementDirection.NULL;
        GlfwInputHandler.AddListener(this);
    }

    private transient final Vector3f cursorDirection = new Vector3f();
    private transient final Vector3f farPoint = new Vector3f();
    private transient final Vector3f fx = new Vector3f();
    private transient final Vector3f fy = new Vector3f();
    private transient final Vector3f fz = new Vector3f();
    private transient boolean _bCanTranslate;
    private transient float gizmoScale;
    private transient boolean _bTranslate;
    private transient GizmoMovementDirection gizmoMovementMode;
    private transient GizmoMovementDirection _gizmoMovementAxisOverride;
    private transient float mousePosX, mousePosY, mouseLastX, mouseLastY;

    public boolean isInTranslation() {
        return _bTranslate;
    }

    public boolean displayTranslation() {
        return (_bCanTranslate || getGizmoMovementMode() != GizmoMovementDirection.NULL) && _attachedComponent != null;
    }

    public GizmoMovementDirection getGizmoMovementMode() {
        return _gizmoMovementAxisOverride == GizmoMovementDirection.NULL ? gizmoMovementMode : _gizmoMovementAxisOverride;
    }

    @Override
    public void draw(Scene context) {
        float mouseDeltaX = (float) IEngineModule.Get().GetController().getCursorPosX() - mousePosX;
        float mouseDeltaY = (float) IEngineModule.Get().GetController().getCursorPosY() - mousePosY;
        mousePosX = (float) IEngineModule.Get().GetController().getCursorPosX();
        mousePosY = (float) IEngineModule.Get().GetController().getCursorPosY();
        mouseLastX = mousePosX - mouseDeltaX;
        mouseLastY = mousePosY - mouseDeltaY;

        if (_attachedComponent == null) return;

        if (gizmoMesh == null) gizmoMesh = MeshFactory.FromFile("gizmoMesh", AssetReferences.GIZMO_MESH_PATH, ImportZAxis.ZUp);
        if (gizmoMaterial == null) gizmoMaterial = MaterialFactory.FromFiles("gizmoMaterial", (AssetReferences.GIZMO_MATERIAL_PATH.getPath() + ".vert"), (AssetReferences.GIZMO_MATERIAL_PATH.getPath() + ".frag"));
        if (gizmoMaterial == null || gizmoMesh == null || gizmoMesh.length == 0) Log.Fail("failed to find gizmo asset path");

        setRelativePosition(_attachedComponent.getWorldPosition());
        setRelativeRotation(_attachedComponent.getRelativeRotation());

        gizmoScale = getWorldPosition().distance(((RenderScene)context).getCamera().getWorldPosition()) / 10;

        getRelativeScale().set(gizmoScale, gizmoScale, gizmoScale);

        if (!isInTranslation()) updateState((RenderScene)context);
        else move((RenderScene)context);
        if (displayTranslation()) {
            switch (getGizmoMovementMode()) {
                case NULL:
                    gizmoMaterial.setIntParameter("axis", 1);
                    break;
                case moveX:
                    gizmoMaterial.setIntParameter("axis", 2);
                    break;
                case moveY:
                    gizmoMaterial.setIntParameter("axis", 3);
                    break;
                case moveZ:
                    gizmoMaterial.setIntParameter("axis", 4);
                    break;
            }
        }
        else {
            gizmoMaterial.setIntParameter("axis", 0);
        }
        gizmoMaterial.use(context);
        gizmoMaterial.setModelMatrix(getWorldTransformationMatrix());
        gizmoMesh[0].use(context);
    }

    private void updateState(RenderScene scene) {

        scene.getCursorSceneDirection(cursorDirection);

        farPoint.set(cursorDirection).mul(scene.getCamera().getFarClipPlane()).add(getWorldPosition());

        if (MathLibrary.GetPointDistanceToSegment(scene.getCamera().getWorldPosition(), farPoint, getWorldPosition()) < gizmoScale / 8) {
            gizmoMovementMode = GizmoMovementDirection.NULL;
            _bCanTranslate = true;
            return;
        }
        else {
            _bCanTranslate = false;
            gizmoMovementMode = GizmoMovementDirection.NULL;

            fx.set(getForwardVector()).mul(gizmoScale).add(getWorldPosition());
            fy.set(getRightVector()).mul(gizmoScale).add(getWorldPosition());
            fz.set(getUpVector()).mul(gizmoScale).add(getWorldPosition());

            float dx = MathLibrary.GetPointDistanceToSegment(getWorldPosition(), fx, scene.getCamera().getWorldPosition());
            float dy = MathLibrary.GetPointDistanceToSegment(getWorldPosition(), fy, scene.getCamera().getWorldPosition());
            float dz = MathLibrary.GetPointDistanceToSegment(getWorldPosition(), fz, scene.getCamera().getWorldPosition());

            if (dx < dy && dx < dz) {
                if (testX(scene, gizmoScale)) gizmoMovementMode = GizmoMovementDirection.moveX;
                else if (testY(scene, gizmoScale)) gizmoMovementMode = GizmoMovementDirection.moveY;
                else if (testZ(scene, gizmoScale)) gizmoMovementMode = GizmoMovementDirection.moveZ;
            }
            if (dy < dx && dy < dz) {
                if (testY(scene, gizmoScale)) gizmoMovementMode = GizmoMovementDirection.moveY;
                else if (testX(scene, gizmoScale)) gizmoMovementMode = GizmoMovementDirection.moveX;
                else if (testZ(scene, gizmoScale)) gizmoMovementMode = GizmoMovementDirection.moveZ;
            }
            else {
                if (testZ(scene, gizmoScale)) gizmoMovementMode = GizmoMovementDirection.moveZ;
                else if (testX(scene, gizmoScale)) gizmoMovementMode = GizmoMovementDirection.moveX;
                else if (testY(scene, gizmoScale)) gizmoMovementMode = GizmoMovementDirection.moveY;
            }
        }
    }

    private boolean testX(RenderScene scene, float gizmoScale) {
        return MathLibrary.GetSegmentDistanceToLine(scene.getCamera().getWorldPosition(), cursorDirection, getWorldPosition(), fx) < gizmoScale * gizmoScale* 0.12;
    }

    private boolean testY(RenderScene scene, float gizmoScale) {
        return MathLibrary.GetSegmentDistanceToLine(scene.getCamera().getWorldPosition(), cursorDirection, getWorldPosition(), fy) < gizmoScale * gizmoScale* 0.12;
    }

    private boolean testZ(RenderScene scene, float gizmoScale) {
        return MathLibrary.GetSegmentDistanceToLine(scene.getCamera().getWorldPosition(), cursorDirection, getWorldPosition(), fz) < gizmoScale * 0.12;
    }

    public SceneComponent getComponent() { return _attachedComponent; }
    public void setComponent(SceneComponent component) {
        cancelTranslation();
        _attachedComponent = component;
        if (component != null) {
            _baseLocation.set(component.getRelativePosition());
        }
    }

    public void beginTranslation(boolean disableAxis) {
        cancelTranslation();
        if (_attachedComponent == null) return;
        _bTranslate = true;
        if (disableAxis) gizmoMovementMode = GizmoMovementDirection.NULL;
        _baseLocation.set(_attachedComponent.getRelativePosition());
        Window.GetPrimaryWindow().showCursor(false);
        ((DefaultController)IEngineModule.Get().GetController()).enableCameraMovements(false);
    }

    public void setTranslationAxis(GizmoMovementDirection override) {
        if (!isInTranslation()) return;
        beginTranslation(true);
        _gizmoMovementAxisOverride = override;
    }

    public void cancelTranslation() {
        if (_attachedComponent != null && isInTranslation()) {
            _attachedComponent.setRelativePosition(_baseLocation);
        }
        endTranslation();
    }

    public void endTranslation() {
        Window.GetPrimaryWindow().showCursor(true, true);
        ((DefaultController)IEngineModule.Get().GetController()).enableCameraMovements(true);
        _bTranslate = false;
        _gizmoMovementAxisOverride = GizmoMovementDirection.NULL;
    }

    private void move(RenderScene context) {
        Vector3f cameraPos = context.getCamera().getWorldPosition();
        float distance = cameraPos.distance(getWorldPosition());

        MathLibrary.PixelToSceneDirection(context, mousePosX, mousePosY, _dir);
        MathLibrary.PixelToSceneDirection(context, mouseLastX, mouseLastY, _lastDir);

        _dirb.set(_lastDir).mul(distance).add(cameraPos);
        _movementVector.set(_dir).mul(distance).add(cameraPos).sub(_dirb);

        if (getGizmoMovementMode() == GizmoMovementDirection.NULL) {
            _attachedComponent.addRelativeOffset(_movementVector);
            return;
        }

        float dist = _movementVector.length();
        if (_movementVector.length() == 0) return;
        _movementVector.normalize();

        switch (getGizmoMovementMode()) {
            case moveX: {
                    float offset = _movementVector.dot(_attachedComponent.getForwardVector());
                    _attachedComponent.addLocalOffset(_localMovement.set(dist * (1 / offset), 0, 0));
                } break;
            case moveY: {
                float offset = _movementVector.dot(_attachedComponent.getRightVector());
                _attachedComponent.addLocalOffset(_localMovement.set(0, dist * (1 / offset), 0));
            } break;
            case moveZ: {
                float offset = _movementVector.dot(_attachedComponent.getUpVector());
                _attachedComponent.addLocalOffset(_localMovement.set(0, 0, dist * (1 / offset)));
            } break;
        }
    }

    @Override
    public void keyCallback(int keycode, int scancode, int action, int mods) {}

    @Override
    public void charCallback(int chr) {}

    @Override
    public void mouseButtonCallback(int button, int action, int mods) {}

    @Override
    public void scrollCallback(double xOffset, double yOffset) {}

    @Override
    public void cursorPosCallback(double x, double y) {}
}