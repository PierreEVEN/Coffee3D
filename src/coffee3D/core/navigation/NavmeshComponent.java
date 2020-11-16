package coffee3D.core.navigation;

import coffee3D.core.maths.MathLibrary;
import coffee3D.core.renderer.RenderMode;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.renderer.Window;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.core.types.Color;
import coffee3D.core.types.SphereBound;
import coffee3D.core.types.TypeHelper;
import org.joml.Quaternionf;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

public class NavmeshComponent extends SceneComponent {

    private static final long serialVersionUID = -4773537143224102110L;

    protected int _sizeX;
    protected int _sizeY;
    protected float _cellSize;
    private NavMeshGrid _navmesh;
    private transient SphereBound bounds;

    private static final Vector3f _center = new Vector3f();
    private static final Vector3f _worldPosA = new Vector3f();
    private static final Vector3f _worldPosB = new Vector3f();
    private static final Vector3f _worldPosC = new Vector3f();
    private static final Vector3f _worldPosD = new Vector3f();
    private static final Color navigableColor = new Color(0, 1, 0, 0.8f);
    private static final Color notNavigableColor = new Color(1, 0, 0, 0.2f);
    private static final Color selectionColor = new Color(1, 1, 0, 1f);
    private static final Vector3f cameraPointIntersection = new Vector3f();
    private static final Vector3f up = new Vector3f(0, 0, 1);
    private static final Vector3f camDir = new Vector3f(0);
    private static final Vector2i local = new Vector2i(0);

    public NavmeshComponent(Vector3f position, Quaternionf rotation, Vector3f scale) {
        super(position, rotation, scale);
        _sizeX = 100;
        _sizeY = 40;
        _cellSize = 1f;
        newNavmesh();
    }

    public NavigationPath findPathToLocation(Vector3f source, Vector3f target) {
        return _navmesh.findPathToLocation(source, target);
    }

    @Override
    protected void draw(Scene context) {}

    @Override
    public void postDraw(Scene context) {
        super.draw(context);
        if (_cellSize != _navmesh.getCellSize() ||
                _sizeX != _navmesh.getGridSize().x ||
                _sizeY != _navmesh.getGridSize().y
        ) newNavmesh();

        _navmesh.getNavmeshLocation().set(getWorldPosition());
        if (isSelected()) {
            visualize((RenderScene) context);
            edit((RenderScene) context);
        }
    }


    @Override
    public SphereBound getBound() {
        if (bounds == null) {
            bounds = new SphereBound();
        }
        bounds.radius = _cellSize * Math.max(_sizeX, _sizeY);
        bounds.position.set(getWorldPosition());
        return bounds;
    }

    private void newNavmesh() {
        _navmesh = new NavMeshGrid(getWorldPosition(), new Vector2i(_sizeX, _sizeY), _cellSize);
    }

    private void visualize(RenderScene context) {
        if (RenderUtils.RENDER_MODE == RenderMode.Select) {
            RenderUtils.getPickMaterialDrawList()[0].use(context);
            RenderUtils.getPickMaterialDrawList()[0].getResource().setIntParameter("pickId", getComponentIndex() + 1);
            RenderUtils.getPickMaterialDrawList()[0].getResource().setModelMatrix(TypeHelper.getMat4().identity());

            _worldPosA.set(getWorldPosition());
            _worldPosB.set(_worldPosA).add(_sizeX * _cellSize, 0, 0);
            _worldPosC.set(_worldPosA).add(_sizeX * _cellSize, _sizeY * _cellSize, 0);
            _worldPosD.set(_worldPosA).add(0, _sizeY * _cellSize, 0);

            glMatrixMode(GL_MODELVIEW);
            glBegin(GL_QUADS);
            {
                glVertex3f(_worldPosA.x, _worldPosA.y, _worldPosA.z);
                glVertex3f(_worldPosB.x, _worldPosB.y, _worldPosB.z);
                glVertex3f(_worldPosC.x, _worldPosC.y, _worldPosC.z);
                glVertex3f(_worldPosD.x, _worldPosD.y, _worldPosD.z);
            }
            glEnd();
        }
        if (RenderUtils.RENDER_MODE != RenderMode.Color) return;

        context.getCursorSceneDirection(camDir);
        MathLibrary.LinePlaneIntersection(getWorldPosition(), up, camDir, context.getCamera().getWorldPosition(), cameraPointIntersection);
        _navmesh.worldToLocal(cameraPointIntersection, local);
        if (_navmesh.isLocationInNavmesh(local) && !Window.GetPrimaryWindow().captureMouse()) {
            _navmesh.localToWorld(local, cameraPointIntersection);

            RenderUtils.getDebugMaterial().getResource().setModelMatrix(TypeHelper.getMat4().identity());
            RenderUtils.getDebugMaterial().setColor(selectionColor);
            RenderUtils.getDebugMaterial().use(context);
            glMatrixMode(GL_MODELVIEW);
            glBegin(GL_QUADS);
            {
                glVertex3f(cameraPointIntersection.x - _cellSize / 2, cameraPointIntersection.y - _cellSize / 2, cameraPointIntersection.z + .1f);
                glVertex3f(cameraPointIntersection.x + _cellSize / 2, cameraPointIntersection.y - _cellSize / 2, cameraPointIntersection.z + .1f);
                glVertex3f(cameraPointIntersection.x + _cellSize / 2, cameraPointIntersection.y + _cellSize / 2, cameraPointIntersection.z + .1f);
                glVertex3f(cameraPointIntersection.x - _cellSize / 2, cameraPointIntersection.y + _cellSize / 2, cameraPointIntersection.z + .1f);
            }
            glEnd();
        }

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        float halfGridSize = _navmesh.getCellSize() / 2;

        for (NavmeshPoint _points : _navmesh.getNavmesh()) {
            _navmesh.localToWorld(_points.location, _center);
            _worldPosA.set(_center).add(-halfGridSize, -halfGridSize, 0);
            _worldPosB.set(_center).add(halfGridSize, -halfGridSize, 0);
            _worldPosC.set(_center).add(halfGridSize, halfGridSize, 0);
            _worldPosD.set(_center).add(-halfGridSize, halfGridSize, 0);

            RenderUtils.getDebugMaterial().setColor(_points.isNavigable ? navigableColor : notNavigableColor);
            RenderUtils.getDebugMaterial().use(context);
            glMatrixMode(GL_MODELVIEW);
            glBegin(GL_QUADS);
            {
                glVertex3f(_worldPosA.x, _worldPosA.y, _worldPosA.z);
                glVertex3f(_worldPosB.x, _worldPosB.y, _worldPosB.z);
                glVertex3f(_worldPosC.x, _worldPosC.y, _worldPosC.z);
                glVertex3f(_worldPosD.x, _worldPosD.y, _worldPosD.z);
            }
            glEnd();
        }
        glDisable(GL_BLEND);
    }

    private void edit(RenderScene scene) {
        if (GLFW.glfwGetMouseButton(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS && !Window.GetPrimaryWindow().captureMouse()) {
            scene.getCursorSceneDirection(camDir);
            MathLibrary.LinePlaneIntersection(getWorldPosition(), up, camDir, scene.getCamera().getWorldPosition(), cameraPointIntersection);
            _navmesh.worldToLocal(cameraPointIntersection, local);
            if (_navmesh.getPoint(local) != null) {
                _navmesh.getPoint(local).isNavigable = GLFW.glfwGetKey(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) != GLFW.GLFW_PRESS;
            }
        }
    }
}
