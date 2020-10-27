package Editor.Controller;

import Core.Controller.DefaultController;
import Core.Controller.IGameController;
import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.RenderScene;
import Core.Renderer.Window;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL46.*;

public class EditorController extends DefaultController {

    public EditorController(RenderScene scene) {
        super(scene);
    }

    @Override
    public void keyCallback(int key, int scancode, int action, int mods) {
        super.keyCallback(key, scancode, action, mods);
        if (!Window.GetPrimaryWindow().captureMouse()) return;
        if (action == GLFW.GLFW_PRESS) {
            switch (key) {
                case GLFW.GLFW_KEY_F1 -> Window.GetPrimaryWindow().setDrawMode(GL_FILL);
                case GLFW.GLFW_KEY_F2 -> Window.GetPrimaryWindow().setDrawMode(GL_LINE);
                case GLFW.GLFW_KEY_F3 -> Window.GetPrimaryWindow().setDrawMode(GL_POINT);
                case GLFW.GLFW_KEY_F5 -> getScene().getCamera().setPerspective(!getScene().getCamera().enablePerspective());
            }
        }
    }

    @Override
    public void mouseButtonCallback(int button, int action, int mods) {
        super.mouseButtonCallback(button, action, mods);
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {}
    }
}
