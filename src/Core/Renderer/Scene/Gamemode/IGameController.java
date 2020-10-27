package Core.Renderer.Scene.Gamemode;

import Core.IO.Inputs.IInputListener;
import Core.Renderer.RenderUtils;
import Core.Renderer.Scene.RenderScene;
import Core.Renderer.Scene.Scene;
import Core.Renderer.Window;
import org.lwjgl.glfw.*;

public abstract class IGameController implements IInputListener {

    private final RenderScene _scene;

    private double _cursorPosX = 0;
    private double _cursorPosY = 0;

    private double _cursorDeltaX = 0;
    private double _cursorDeltaY = 0;

    protected IGameController(RenderScene scene) {
        _scene = scene;
    }

    abstract void update(Scene context);

    @Override
    public void cursorPosCallback(double x, double y) {
        _cursorDeltaX = x - _cursorPosX;
        _cursorDeltaY = y - _cursorPosY;
        _cursorPosX = x;
        _cursorPosY = y;
    }

    public double getCursorPosX() { return _cursorPosX; }
    public double getCursorPosY() { return _cursorPosY; }

    public double getCursorDeltaX() { return _cursorDeltaX; }
    public double getCursorDeltaY() { return _cursorDeltaY; }

    protected RenderScene getScene() { return _scene; }
}
