package coffee3D.core.controller;

import coffee3D.core.maths.Interpolation;
import coffee3D.core.renderer.Window;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.types.TypeHelper;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class TopViewController extends IGameController {

    private static final Vector3f _defaultOffset = new Vector3f(0,0,5);
    private final Vector2f _currentSpeed = new Vector2f(0);
    private final Vector3f _targetPosition = new Vector3f(0);
    private final Vector3f _cameraPosition = new Vector3f(0);
    private float _distance = 10;
    private float _wantedDistance = 10;
    private float _yaw = 0;
    private float _wantedYaw = 0;
    private float _pitch = 40;
    private float _speed = 200f;
    private float _mouseSensitivity = .18f;
    private float _borderDetection = 200f;

    public void setDistance(float distance) {
        _wantedDistance = distance;
        if (_wantedDistance > 200) _wantedDistance = 200;
        if (_wantedDistance < 5) _wantedDistance = 5;
    }

    public float getSpeed() { return _speed; }
    public void setSpeed(float speed) { _speed = speed; }
    public Vector3f getCameraPosition() { return _cameraPosition; }
    public void setBorderDistance(float distance) { _borderDetection = distance; }
    public float getBorderDistance() { return _borderDetection; }
    public float getSensitivity() { return _mouseSensitivity; }
    public void setSensitivity(float sensitivity) { _mouseSensitivity = sensitivity;}


    public TopViewController(RenderScene scene) {
        super(scene);
    }

    @Override
    public void update() {
        float deltaTime = (float) Window.GetPrimaryWindow().getDeltaTime();
        float movementSpeed = _speed * deltaTime * Math.max(.5f, _distance / 20);

        _currentSpeed.mul(Math.max(0, 1 - (deltaTime * 10)));

        keyboardMovements(deltaTime, movementSpeed);
        mouseMovements(movementSpeed * 1.5f);

        _yaw = Interpolation.FInterpTo(_yaw, _wantedYaw, 10);
        _pitch = Interpolation.FInterpTo(_pitch, Math.max(20, Math.min(40, _distance * .75f + 10)), 5);
        _distance = Interpolation.FInterpTo(_distance, _wantedDistance, 10);

        getScene().getCamera().setPitchInput(_pitch);
        getScene().getCamera().setYawInput(_yaw);


        _cameraPosition.set(getScene().getCamera().getForwardVector());
        _cameraPosition.mul(_distance * -1);
        _cameraPosition.add(_targetPosition).add(_defaultOffset);

        getScene().getCamera().setRelativePosition(_cameraPosition);

    }

    private void mouseMovements(float movementSpeed) {

        float distLeft = getScene().getCursorPosX();
        float distTop = getScene().getCursorPosY();
        float distRight = getScene().getFbWidth() - distLeft;
        float distBottom = getScene().getFbHeight() - distTop;

        if (distLeft < 0 || distBottom < 0 ||distTop < 0 || distRight < 0) return;

        if (distRight < _borderDetection) {
            _currentSpeed.y -= movementSpeed * Math.min(1, (1 - distRight / _borderDetection));
        }
        if (distLeft < _borderDetection) {
            _currentSpeed.y += movementSpeed * Math.min(1, (1 - distLeft / _borderDetection));
        }
        if (distTop < _borderDetection) {
            _currentSpeed.x += movementSpeed * Math.min(1, (1 - distTop / _borderDetection));
        }
        if (distBottom < _borderDetection) {
            _currentSpeed.x -= movementSpeed * Math.min(1, (1 - distBottom / _borderDetection));
        }
    }

    private void keyboardMovements(float deltaTime, float movementSpeed) {
        long windowHandle = Window.GetPrimaryWindow().getGlfwWindowHandle();

        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS ||
                GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
            _currentSpeed.x += movementSpeed;
        }
        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS ||
                GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) {
            _currentSpeed.x -= movementSpeed;
        }
        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS ||
                GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
            _currentSpeed.y += movementSpeed;
        }
        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS ||
                GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) {
            _currentSpeed.y -= movementSpeed;
        }
        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_Q) == GLFW.GLFW_PRESS ||
                GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_DELETE) == GLFW.GLFW_PRESS) {
            _wantedYaw += deltaTime * 200;
        }
        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_E) == GLFW.GLFW_PRESS ||
                GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_PAGE_DOWN) == GLFW.GLFW_PRESS) {
            _wantedYaw -= deltaTime * 200;
        }

        Vector3f forward = TypeHelper.getVector3(getScene().getCamera().getForwardVector());
        forward.z = 0;
        forward.normalize();
        forward.mul(_currentSpeed.x * deltaTime);

        Vector3f right = TypeHelper.getVector3(getScene().getCamera().getRightVector());
        right.z = 0;
        right.normalize();
        right.mul(_currentSpeed.y * deltaTime);
        _targetPosition.add(forward);
        _targetPosition.add(right);
    }

    @Override
    public void keyCallback(int keycode, int scancode, int action, int mods) {}

    @Override
    public void charCallback(int chr) {}

    @Override
    public void cursorPosCallback(double x, double y) {
        super.cursorPosCallback(x, y);
    }

    @Override
    public void mouseButtonCallback(int button, int action, int mods) {}

    @Override
    public void scrollCallback(double xOffset, double yOffset) {
        if (yOffset != 0) setDistance(_wantedDistance *= (-yOffset) / 4 + 1);
    }
}