package Core.Renderer.Scene.Gamemode;

import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.RenderScene;
import Core.Renderer.Scene.Scene;
import Core.Renderer.Scene.SceneComponent;
import Core.Renderer.Window;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL46.*;

public class DefaultController extends IGameController {

    float speed = 2.f;

    public DefaultController(RenderScene scene) {
        super(scene);
    }

    @Override
    void update(Scene context) {
        if (!Window.GetPrimaryWindow().captureMouse()) return;
        float movementSpeed = speed * (float)Window.GetPrimaryWindow().getDeltaTime();

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
    public void cursorPosCallback(double x, double y) {
        super.cursorPosCallback(x, y);
        if (!Window.GetPrimaryWindow().captureMouse()) return;
        getScene().getCamera().addYawInput((float) getCursorDeltaX() * 0.5f);
        getScene().getCamera().addPitchInput((float) getCursorDeltaY() * 0.5f);
    }


    @Override
    public void keyCallback(int key, int scancode, int action, int mods) {
       if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) Window.GetPrimaryWindow().switchCursor();
        if (!Window.GetPrimaryWindow().captureMouse()) return;
        if (action == GLFW.GLFW_PRESS) {
            switch (key) {
                case GLFW.GLFW_KEY_F1 -> Window.GetPrimaryWindow().setDrawMode(GL_FILL);
                case GLFW.GLFW_KEY_F2 -> Window.GetPrimaryWindow().setDrawMode(GL_LINE);
                case GLFW.GLFW_KEY_F3 -> Window.GetPrimaryWindow().setDrawMode(GL_POINT);
                case GLFW.GLFW_KEY_F5 -> getScene().getCamera().setPerspective(!getScene().getCamera().enablePerspective());
                case GLFW.GLFW_KEY_PAGE_UP -> getScene().getCamera().setFieldOfView(getScene().getCamera().getFieldOfView() / 1.2f);
                case GLFW.GLFW_KEY_PAGE_DOWN -> getScene().getCamera().setFieldOfView(getScene().getCamera().getFieldOfView() * 1.2f);
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
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {}
    }

    @Override
    public void scrollCallback(double xOffset, double yOffset) {
        if (!Window.GetPrimaryWindow().captureMouse()) return;
        if (yOffset != 0) speed *= yOffset / 4 + 1;
    }
}
