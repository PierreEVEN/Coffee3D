package coffee3D.core.controller;

import coffee3D.core.maths.Interpolation;
import coffee3D.core.renderer.Window;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.types.TypeHelper;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class TopViewController extends IGameController {



    private float defaultHeight = 0;
    private float distance = 10;
    private float wantedDistance = 10;
    private float yaw = 0;
    private float wantedYaw = 0;
    private float pitch = 40;
    private Vector2f currentSpeed;
    private float _speed = 5f;
    private float _mouseSensitivity = .18f;
    private final Vector3f targetPosition;
    private final Vector3f cameraPosition;
    private float borderDetection = 200f;




    public TopViewController(RenderScene scene) {
        super(scene);
        distance = 10;
        currentSpeed = new Vector2f(0,0);
        targetPosition = new Vector3f(0);
        cameraPosition = new Vector3f(0);
    }

    @Override
    public void update() {
        float deltaTime = (float) Window.GetPrimaryWindow().getDeltaTime();
        float movementSpeed = _speed * deltaTime * Math.max(.5f, distance / 20);

        currentSpeed.mul(Math.max(0, 1 - (deltaTime * 10)));

        keyboardMovements(deltaTime, movementSpeed);
        mouseMovements(deltaTime, movementSpeed * 1.5f);

        yaw = Interpolation.FInterpTo(yaw, wantedYaw, 10);
        pitch = Interpolation.FInterpTo(pitch, Math.max(10, Math.min(40, distance * .75f + 10)), 5);
        distance = Interpolation.FInterpTo(distance, wantedDistance, 10);

        getScene().getCamera().setPitchInput(pitch);
        getScene().getCamera().setYawInput(yaw);


        cameraPosition.set(getScene().getCamera().getForwardVector());
        cameraPosition.mul(distance * -1);
        cameraPosition.add(targetPosition);

        getScene().getCamera().setRelativePosition(cameraPosition);

    }

    private void mouseMovements(float deltaTime, float movementSpeed) {

        float distLeft = getScene().getCursorPosX();
        float distTop = getScene().getCursorPosY();
        float distRight = getScene().getFbWidth() - distLeft;
        float distBottom = getScene().getFbHeight() - distTop;

        if (distRight < borderDetection) {
            currentSpeed.y += movementSpeed * Math.min(1, (1 - distRight / borderDetection));
        }
        if (distLeft < borderDetection) {
            currentSpeed.y -= movementSpeed * Math.min(1, (1 - distLeft / borderDetection));
        }
        if (distTop < borderDetection) {
            currentSpeed.x += movementSpeed * Math.min(1, (1 - distTop / borderDetection));
        }
        if (distBottom < borderDetection) {
            currentSpeed.x -= movementSpeed * Math.min(1, (1 - distBottom / borderDetection));
        }

    }

    private void keyboardMovements(float deltaTime, float movementSpeed) {
        long windowHandle = Window.GetPrimaryWindow().getGlfwWindowHandle();

        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS ||
                GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
            currentSpeed.x += movementSpeed;
        }
        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS ||
                GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) {
            currentSpeed.x -= movementSpeed;
        }
        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS ||
                GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
            currentSpeed.y += movementSpeed;
        }
        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS ||
                GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) {
            currentSpeed.y -= movementSpeed;
        }
        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_Q) == GLFW.GLFW_PRESS ||
                GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_DELETE) == GLFW.GLFW_PRESS) {
            wantedYaw += deltaTime * 200;
        }
        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_E) == GLFW.GLFW_PRESS ||
                GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_PAGE_DOWN) == GLFW.GLFW_PRESS) {
            wantedYaw -= deltaTime * 200;
        }

        Vector3f forward = TypeHelper.getVector3(getScene().getCamera().getForwardVector());
        forward.z = 0;
        forward.normalize();
        forward.mul(currentSpeed.x);

        Vector3f right = TypeHelper.getVector3(getScene().getCamera().getRightVector());
        right.z = 0;
        right.normalize();
        right.mul(currentSpeed.y);

        targetPosition.add(forward);
        targetPosition.add(right);

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
        if (yOffset != 0) wantedDistance *= (-yOffset) / 4 + 1;
        if (wantedDistance > 200) wantedDistance = 200;
        if (wantedDistance < 1) wantedDistance = 1;
    }
}
