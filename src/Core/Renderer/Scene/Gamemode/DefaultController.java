package Core.Renderer.Scene.Gamemode;

import Core.IO.Log;
import Core.Renderer.Scene.Scene;
import Core.Renderer.Window;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL46.*;

public class DefaultController extends IGameController {


    public DefaultController(Scene scene) {
        super(scene);
    }

    @Override
    void update(Scene context) {
        float movementSpeed = 2 * (float)Window.GetPrimaryWindow().getDeltaTime();

        if (GLFW.glfwGetKey(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS)
            getScene().getCamera().addLocalOffset(new Vector3f(movementSpeed, 0, 0));
        if (GLFW.glfwGetKey(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS)
            getScene().getCamera().addLocalOffset(new Vector3f(-movementSpeed, 0, 0));
        if (GLFW.glfwGetKey(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS)
            getScene().getCamera().addLocalOffset(new Vector3f(0, movementSpeed, 0));
        if (GLFW.glfwGetKey(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS)
            getScene().getCamera().addLocalOffset(new Vector3f(0, -movementSpeed, 0));
        if (GLFW.glfwGetKey(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS)
            getScene().getCamera().addLocalOffset(new Vector3f(0, 0, movementSpeed));
        if (GLFW.glfwGetKey(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS)
            getScene().getCamera().addLocalOffset(new Vector3f(0, 0, -movementSpeed));
    }

    @Override
    void mouseEvent(int button, int action, int mods) {}

    @Override
    void keyboardEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            switch (key) {
                case GLFW.GLFW_KEY_ESCAPE -> Window.GetPrimaryWindow().switchCursor();
                case GLFW.GLFW_KEY_F1 -> Window.GetPrimaryWindow().setDrawMode(GL_FILL);
                case GLFW.GLFW_KEY_F2 -> Window.GetPrimaryWindow().setDrawMode(GL_LINE);
                case GLFW.GLFW_KEY_F3 -> Window.GetPrimaryWindow().setDrawMode(GL_POINT);
                case GLFW.GLFW_KEY_F5 -> getScene().getCamera().setPerspective(!getScene().getCamera().enablePerspective());
            }
        }
    }

    @Override
    void scrollEvent(double xOffset, double yOffset) {
        getScene().getCamera().setFieldOfView(getScene().getCamera().getFieldOfView() + (float)yOffset * -2);
    }

    @Override
    protected void cursorPosEvent(double x, double y) {
        super.cursorPosEvent(x, y);
        if (Window.GetPrimaryWindow().captureMouse()) {
            getScene().getCamera().addYawInput((float)getCursorDeltaX() * (float)Window.GetPrimaryWindow().getDeltaTime() * 50);
            getScene().getCamera().addPitchInput((float)getCursorDeltaY() * (float)Window.GetPrimaryWindow().getDeltaTime() * 50);
        }
    }
}
