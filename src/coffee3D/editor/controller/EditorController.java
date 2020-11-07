package coffee3D.editor.controller;

import coffee3D.core.IEngineModule;
import coffee3D.core.assets.AssetManager;
import coffee3D.core.controller.DefaultController;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.Window;
import coffee3D.editor.EditorModule;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL46.*;

public class EditorController extends DefaultController {

    public EditorController(RenderScene scene) {
        super(scene);
    }

    @Override
    public void keyCallback(int key, int scancode, int action, int mods) {
        super.keyCallback(key, scancode, action, mods);
        if (action == GLFW.GLFW_PRESS) {
            switch (key) {
                case GLFW.GLFW_KEY_S : {
                    if ((mods & GLFW.GLFW_MOD_SHIFT) != 0 && (mods & GLFW.GLFW_MOD_CONTROL) != 0)
                        EditorModule.SaveAll();
                } break;
            }
        }
        if (!Window.GetPrimaryWindow().captureMouse()) return;
        if (action == GLFW.GLFW_PRESS) {
            switch (key) {
                case GLFW.GLFW_KEY_F1 : Window.GetPrimaryWindow().setDrawMode(GL_FILL); break;
                case GLFW.GLFW_KEY_F2 : Window.GetPrimaryWindow().setDrawMode(GL_LINE); break;
                case GLFW.GLFW_KEY_F3 : Window.GetPrimaryWindow().setDrawMode(GL_POINT); break;
                case GLFW.GLFW_KEY_F5 : getScene().getCamera().setPerspective(!getScene().getCamera().enablePerspective()); break;
             }
        }
    }


    @Override
    public void mouseButtonCallback(int button, int action, int mods) {
        super.mouseButtonCallback(button, action, mods);
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {}
    }
}
