package Core.Controller;

import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.RenderScene;
import Core.Renderer.Window;
import Core.Types.TypeHelper;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.Vector;

import static org.lwjgl.opengl.GL46.*;

public class DefaultController extends IGameController {

    private float _speed = 100f;
    private float _mouseSensitivity = .18f;
    private final Vector3f currentSpeed = new Vector3f().zero();

    public DefaultController(RenderScene scene) {
        super(scene);
    }

    @Override
    public void update() {
        double deltaTime = Window.GetPrimaryWindow().getDeltaTime();
        float movementSpeed = _speed * (float)Window.GetPrimaryWindow().getDeltaTime();
        currentSpeed.mul(Math.max(0, 1 - (float) (deltaTime * 20)));
        if (Window.GetPrimaryWindow().captureMouse()) {

            if (GLFW.glfwGetKey(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS)
                currentSpeed.x += movementSpeed;
            if (GLFW.glfwGetKey(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS)
                currentSpeed.x -= movementSpeed;
            if (GLFW.glfwGetKey(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS)
                currentSpeed.y += movementSpeed;
            if (GLFW.glfwGetKey(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS)
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
        if (!Window.GetPrimaryWindow().captureMouse()) return;
        getScene().getCamera().addYawInput((float) getCursorDeltaX() * _mouseSensitivity);
        getScene().getCamera().addPitchInput((float) getCursorDeltaY() * _mouseSensitivity);
    }


    @Override
    public void keyCallback(int key, int scancode, int action, int mods) {
       if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) Window.GetPrimaryWindow().switchCursor();
        if (!Window.GetPrimaryWindow().captureMouse()) return;
        if (action != GLFW.GLFW_RELEASE) {
            switch (key) {
                case GLFW.GLFW_KEY_C -> getScene().getCamera().setFieldOfView(getScene().getCamera().getFieldOfView() / 1.2f);
                case GLFW.GLFW_KEY_X -> getScene().getCamera().setFieldOfView(getScene().getCamera().getFieldOfView() * 1.2f);
            }
        }
    }

    @Override
    public void charCallback(int chr) {

    }

    @Override
    public void mouseButtonCallback(int button, int action, int mods) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS) Window.GetPrimaryWindow().showCursor(false);
        if (button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_RELEASE) Window.GetPrimaryWindow().showCursor(true);
    }

    @Override
    public void scrollCallback(double xOffset, double yOffset) {
        if (!Window.GetPrimaryWindow().captureMouse()) return;
        if (yOffset != 0) _speed *= yOffset / 4 + 1;
    }
}
