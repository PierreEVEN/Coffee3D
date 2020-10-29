package coffee3D.core.io.inputs;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Refactor glfw input system
 */
public class GlfwInputHandler
{
    private static List<IInputListener> _listeners = new ArrayList<>();

    /**
     * Add listener
     * @param listener listener
     */
    public static void AddListener(IInputListener listener) {
        if (_listeners == null) {
            _listeners = new ArrayList<>();
        }
        _listeners.add(listener);
    }

    /**
     * Remove listener
     * @param listener listener
     */
    public static void UnbindListener(IInputListener listener) {
        if (listener != null && _listeners != null) {
            _listeners.remove(listener);
        }
    }

    private static void keyCallback(int keycode, int scancode, int action, int mods) {
        for (IInputListener listener : _listeners) {
            listener.keyCallback(keycode, scancode, action, mods);
        }
    }

    private static void charCallback(int chr) {
        for (IInputListener listener : _listeners) {
            listener.charCallback(chr);
        }
    }

    private static void mouseButtonCallback(int button, int action, int mods) {
        for (IInputListener listener : _listeners) {
            listener.mouseButtonCallback(button, action, mods);
        }
    }

    private static void scrollCallback(double xOffset, double yOffset) {
        for (IInputListener listener : _listeners) {
            listener.scrollCallback(xOffset, yOffset);
        }
    }

    private static void cursorPosCallback(double x, double y) {
        for (IInputListener listener : _listeners) {
            listener.cursorPosCallback(x, y);
        }
    }

    /**
     * initialize (should be done once by the engine)
     * @param glfwWindowHandle window context
     */
    public static void Initialize(long glfwWindowHandle) {
        glfwSetKeyCallback(glfwWindowHandle, (w, key, scancode, action, mods) -> keyCallback(key, scancode, action, mods));
        glfwSetCharCallback(glfwWindowHandle, (w, c) -> charCallback(c));
        glfwSetMouseButtonCallback(glfwWindowHandle, (w, button, action, mods) -> mouseButtonCallback(button, action, mods));
        glfwSetScrollCallback(glfwWindowHandle, (w, xOffset, yOffset) -> scrollCallback(xOffset, yOffset));
        glfwSetCursorPosCallback(glfwWindowHandle, (w, x, y) -> cursorPosCallback(x, y));
    }


}