package Core.Renderer.Scene.Gamemode;

import Core.Renderer.Scene.Scene;
import Core.Renderer.Window;
import org.lwjgl.glfw.*;

public abstract class IGameController {

    private final Scene _scene;

    private double _cursorPosX = 0;
    private double _cursorPosY = 0;

    private double _cursorDeltaX = 0;
    private double _cursorDeltaY = 0;

    protected IGameController(Scene scene) {
        _scene = scene;
    }

    abstract void update(Scene context);
    abstract void mouseEvent(int button, int action, int mods);
    abstract void keyboardEvent(int key, int scancode, int action, int mods);
    abstract void scrollEvent(double xOffset, double yOffset);
    protected void cursorPosEvent(double x, double y) {
        _cursorDeltaX = x - _cursorPosX;
        _cursorDeltaY = y - _cursorPosY;
        _cursorPosX = x;
        _cursorPosY = y;
    }

    public double getCursorPosX() { return _cursorPosX; }
    public double getCursorPosY() { return _cursorPosY; }

    public double getCursorDeltaX() { return _cursorDeltaX; }
    public double getCursorDeltaY() { return _cursorDeltaY; }

    void enable() {
        GLFW.glfwSetMouseButtonCallback(Window.GetPrimaryWindow().getGlfwWindowHandle(), new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                mouseEvent(button, action, mods);
            }
        });

        GLFW.glfwSetKeyCallback(Window.GetPrimaryWindow().getGlfwWindowHandle(), new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                keyboardEvent(key, scancode, action, mods);
            }
        });

        GLFW.glfwSetCursorPosCallback(Window.GetPrimaryWindow().getGlfwWindowHandle(), new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                cursorPosEvent(x, y);
            }
        });

        GLFW.glfwSetScrollCallback(Window.GetPrimaryWindow().getGlfwWindowHandle(), new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xOffset, double yOffset) {
                scrollEvent(xOffset, yOffset);
            }
        });

    }

    protected Scene getScene() { return _scene; }
}
