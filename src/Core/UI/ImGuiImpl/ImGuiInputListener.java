package Core.UI.ImGuiImpl;

import Core.IO.Inputs.IInputListener;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SUPER;

public class ImGuiInputListener implements IInputListener {

    private ImGuiIO _io;

    public ImGuiInputListener(long glfwWindowHandle) {
        _io = ImGui.getIO();

        _io.setSetClipboardTextFn(new ImStrConsumer() {
            @Override
            public void accept(final String s) {
                glfwSetClipboardString(glfwWindowHandle, s);
            }
        });

        _io.setGetClipboardTextFn(new ImStrSupplier() {
            @Override
            public String get() {
                return glfwGetClipboardString(glfwWindowHandle);
            }
        });
    }

    @Override
    public void keyCallback(int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            _io.setKeysDown(key, true);
        } else if (action == GLFW_RELEASE) {
            _io.setKeysDown(key, false);
        }

        _io.setKeyCtrl(_io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || _io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
        _io.setKeyShift(_io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || _io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
        _io.setKeyAlt(_io.getKeysDown(GLFW_KEY_LEFT_ALT) || _io.getKeysDown(GLFW_KEY_RIGHT_ALT));
        _io.setKeySuper(_io.getKeysDown(GLFW_KEY_LEFT_SUPER) || _io.getKeysDown(GLFW_KEY_RIGHT_SUPER));
    }

    @Override
    public void charCallback(int c) {
        if (c != GLFW_KEY_DELETE) {
            _io.addInputCharacter(c);
        }
    }

    @Override
    public void mouseButtonCallback(int button, int action, int mods) {
        final boolean[] mouseDown = new boolean[5];

        mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE;
        mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE;
        mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE;
        mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE;
        mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE;

        _io.setMouseDown(mouseDown);

        if (!_io.getWantCaptureMouse() && mouseDown[1]) {
            ImGui.setWindowFocus(null);
        }
    }

    @Override
    public void scrollCallback(double xOffset, double yOffset) {
        _io.setMouseWheelH(_io.getMouseWheelH() + (float) xOffset);
        _io.setMouseWheel(_io.getMouseWheel() + (float) yOffset);
    }

    @Override
    public void cursorPosCallback(double x, double y) {}
}
