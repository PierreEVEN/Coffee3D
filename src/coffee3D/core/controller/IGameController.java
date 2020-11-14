package coffee3D.core.controller;

import coffee3D.core.io.inputs.GlfwInputHandler;
import coffee3D.core.io.inputs.IInputListener;
import coffee3D.core.renderer.scene.RenderScene;

public abstract class IGameController implements IInputListener {

    private final RenderScene _scene;

    private double _cursorPosX = 0;
    private double _cursorPosY = 0;

    private double _cursorDeltaX = 0;
    private double _cursorDeltaY = 0;

    public IGameController(RenderScene scene) {
        GlfwInputHandler.AddListener(this);
        _scene = scene;
    }

    public abstract void update();

    @Override
    public void cursorPosCallback(double x, double y) {
        _cursorDeltaX = x - _cursorPosX;
        _cursorDeltaY = y - _cursorPosY;
        _cursorPosX = x;
        _cursorPosY = y;
    }

    public double getCursorDeltaX() { return _cursorDeltaX; }
    public double getCursorDeltaY() { return _cursorDeltaY; }

    public double getCursorPosX() { return _cursorPosX; }
    public double getCursorPosY() { return _cursorPosY; }

    protected RenderScene getScene() { return _scene; }
}
