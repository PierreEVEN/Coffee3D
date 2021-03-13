package coffee3D.core.controller;

import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.Window;
import coffee3D.core.types.TypeHelper;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class DefaultController extends IGameController {

    private float _speed = 100f;
    private float _mouseSensitivity = .18f;
    private final Vector3f currentSpeed = new Vector3f().zero();
    private boolean _enableCameraMovements = true;
    public void enableCameraMovements(boolean bEnable) { _enableCameraMovements = bEnable; }
    private boolean _bWasLastFrameCapturing;

    public DefaultController(RenderScene scene) {
        super(scene);
    }

    public float getMouseSensitivity() { return _mouseSensitivity; }
    public void setMouseSensitivity(float sensitivity) { _mouseSensitivity = sensitivity; }


    @Override
    public void update() {
        double deltaTime = Window.GetPrimaryWindow().getDeltaTime();
        float movementSpeed = _speed * (float)Window.GetPrimaryWindow().getDeltaTime();
        currentSpeed.mul(Math.max(0, 1 - (float) (deltaTime * 20)));
        if (Window.GetPrimaryWindow().captureMouse() && _enableCameraMovements) {

            if (GLFW.glfwGetKey(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS)
                currentSpeed.x += movementSpeed;
            if (GLFW.glfwGetKey(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS)
                currentSpeed.x -= movementSpeed;
            if (GLFW.glfwGetKey(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS)
                currentSpeed.y += movementSpeed;
            if (GLFW.glfwGetKey(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS)
                currentSpeed.y -= movementSpeed;
            if (GLFW.glfwGetKey(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS)
                currentSpeed.z += movementSpeed;
            if (GLFW.glfwGetKey(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS)
                currentSpeed.z -= movementSpeed;
        }

        getScene().getCamera().addLocalOffset(TypeHelper.getVector3(currentSpeed).mul((float) deltaTime));
    }

    @Override
    public void cursorPosCallback(double x, double y) {
        super.cursorPosCallback(x, y);
        if (!Window.GetPrimaryWindow().captureMouse() || !_enableCameraMovements) {
            _bWasLastFrameCapturing = false;
            return;
        }
        if (!_bWasLastFrameCapturing) {
            _bWasLastFrameCapturing = true;
            return;
        }
        getScene().getCamera().addYawInput((float) getCursorDeltaX() * _mouseSensitivity);
        getScene().getCamera().addPitchInput((float) getCursorDeltaY() * _mouseSensitivity);
    }


    @Override
    public void keyCallback(int key, int scancode, int action, int mods) {
        if (!Window.GetPrimaryWindow().captureMouse()) return;
        if (action != GLFW.GLFW_RELEASE && _enableCameraMovements) {
            switch (key) {
                case GLFW.GLFW_KEY_C : getScene().getCamera().setFieldOfView(getScene().getCamera().getFieldOfView() / 1.2f); break;
                case GLFW.GLFW_KEY_X : getScene().getCamera().setFieldOfView(getScene().getCamera().getFieldOfView() * 1.2f); break;
            }
        }
    }

    @Override
    public void charCallback(int chr) {}

    @Override
    public void mouseButtonCallback(int button, int action, int mods) {}

    @Override
    public void scrollCallback(double xOffset, double yOffset) {
        if (!Window.GetPrimaryWindow().captureMouse()) return;
        if (yOffset != 0) _speed *= yOffset / 4 + 1;
    }
}
