package coffee3D.core.ui.subWindows;

import coffee3D.core.io.inputs.GlfwInputHandler;
import coffee3D.core.io.inputs.IInputListener;
import coffee3D.core.renderer.Window;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public abstract class SubWindow implements IInputListener {

    private static List<SubWindow> _windows;
    private ImBoolean _bDisplay;
    private String _windowName;
    protected boolean bHasMenuBar = false;
    private float _wPosX = 0, _wPosY = 0, _wSizeX = 0, _wSizeY = 0;
    private boolean _bCanDisplay;
    private boolean _bIsHovered;
    private boolean _bIsDocked;


    public SubWindow(String windowName) {
        _bDisplay = new ImBoolean(true);
        if (_windows == null) _windows = new ArrayList<>();
        _windows.add(this);
        _windowName = windowName;
        GlfwInputHandler.AddListener(this);
    }

    protected abstract void draw();

    private final void drawInternal() {
        if (!_bDisplay.get()) {
            close();
            _windows.remove(this);
            return;
        }

        if (_bDisplay.get()) {
            if (ImGui.begin(_windowName, _bDisplay, bHasMenuBar ? ImGuiWindowFlags.MenuBar : ImGuiWindowFlags.None)) {
                _wPosX = ImGui.getWindowPosX();
                _wPosY = ImGui.getWindowPosY();
                _wSizeX = ImGui.getWindowSizeX();
                _wSizeY = ImGui.getWindowSizeY();
                _bCanDisplay = true;
                draw();
                _bIsHovered = ImGui.isWindowHovered();
                _bIsDocked = ImGui.isWindowDocked();
            }
            else {
                _bCanDisplay = false;
            }
            ImGui.end();
        }
    }

    public void close() {
        _bDisplay.set(false);
        GlfwInputHandler.UnbindListener(this);
    }

    public static void DrawWindows() {
        if (_windows == null) return;
        for (int i = _windows.size() - 1; i >= 0; --i) {
            _windows.get(i).drawInternal();
        }
    }


    public boolean isMouseInsideWindow() {
        float posX = (float) Window.GetPrimaryWindow().getCursorPosX();
        float posY = (float) Window.GetPrimaryWindow().getCursorPosY();
        if (posX < _wPosX || posY < _wPosY) return false;
        if (posX > _wPosX + _wSizeX || posY > _wPosY + _wSizeY) return false;
        return _bCanDisplay && _bIsHovered;
    }

    public float getWindowPosX() { return _wPosX; }
    public float getWindowPosY() { return _wPosY; }
    public float getWindowSizeX() { return _wSizeX; }
    public float getWindowSizeY() { return _wSizeY; }

    @Override
    public void keyCallback(int keycode, int scancode, int action, int mods) {
        if (!isMouseInsideWindow()) return;
        if (action == GLFW.GLFW_PRESS) {
            if (keycode == GLFW.GLFW_KEY_ESCAPE) {
                if (!_bIsDocked) close();
            }
        }
    }

    @Override
    public void charCallback(int chr) {

    }

    @Override
    public void mouseButtonCallback(int button, int action, int mods) {

    }

    @Override
    public void scrollCallback(double xOffset, double yOffset) {

    }

    @Override
    public void cursorPosCallback(double x, double y) {

    }
}
