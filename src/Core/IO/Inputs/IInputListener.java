package Core.IO.Inputs;

public interface IInputListener {
    void keyCallback(int keycode, int scancode, int action, int mods);
    void charCallback(int chr);
    void mouseButtonCallback(int button, int action, int mods);
    void scrollCallback(double xOffset, double yOffset);
    void cursorPosCallback(double x, double y);
}
