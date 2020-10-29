package coffee3D.core.ui.hud;

public class ButtonBehavior {
    private ButtonBehavior() {}
    private static final ButtonBehavior _instance = new ButtonBehavior();

    public static ButtonBehavior Get(float sensitivity) {
        _instance._sensitivity = sensitivity;
        return _instance;
    }

    public float _sensitivity;
}
