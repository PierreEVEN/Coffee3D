package Core.Renderer.Scene.Gamemode;

import Core.Renderer.Scene.Scene;
import Core.Renderer.Window;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL46.*;

public class DefaultController extends IGameController {


    public DefaultController(Scene scene) {
        super(scene);
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
    protected void cursorPosEvent(double x, double y) {
        super.cursorPosEvent(x, y);
        if (Window.GetPrimaryWindow().captureMouse()) {
            getScene().getCamera().addYawInput((float)getCursorDeltaX() * (float)Window.GetPrimaryWindow().getDeltaTime() * 100);
            getScene().getCamera().addPitchInput((float)getCursorDeltaY() * (float)Window.GetPrimaryWindow().getDeltaTime() * -100);
        }
    }
}
