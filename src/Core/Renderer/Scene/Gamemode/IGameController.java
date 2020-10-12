package Core.Renderer.Scene.Gamemode;

import Core.IO.Inputs.IInputListener;
import Core.Renderer.Scene.Scene;
import Core.Renderer.Window;
import org.lwjgl.glfw.*;

public abstract class IGameController implements IInputListener {

    private final Scene _scene;

    private double _cursorPosX = 0;
    private double _cursorPosY = 0;

    private double _cursorDeltaX = 0;
    private double _cursorDeltaY = 0;

    protected IGameController(Scene scene) {
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

    protected Scene getScene() { return _scene; }
}
