package Core.UI.HUD;

import imgui.ImColor;

public class ButtonBehavior {
    private ButtonBehavior() {}
    private static final ButtonBehavior _instance = new ButtonBehavior();

    public static ButtonBehavior Get(float sensitivity) {
        _instance._sensitivity = sensitivity;
        return _instance;
    }

    public float _sensitivity;
}
