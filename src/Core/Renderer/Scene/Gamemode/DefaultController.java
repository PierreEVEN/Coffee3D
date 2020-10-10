package Core.Renderer.Scene.Gamemode;

import Core.Renderer.Scene.Scene;
import Core.Renderer.Window;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class DefaultController extends IGameController {


    public DefaultController(Scene scene) {
        super(scene);
    }

    @Override
    void mouseEvent(int button, int action, int mods) {}

    @Override
    void keyboardEvent(int key, int scancode, int action, int mods) {
        if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
            Window.GetPrimaryWindow().switchCursor();
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
