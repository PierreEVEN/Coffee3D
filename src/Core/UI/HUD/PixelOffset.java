package Core.UI.HUD;

public final class PixelOffset {
    private static PixelOffset _instance;

    public float left;
    public float top;
    public float right;
    public float bottom;

    private PixelOffset() {
        left = 0;
        top = 0;
        right = 0;
        bottom = 0;
    }

    public static PixelOffset Get(float l, float t, float r, float b) {
        if (_instance == null) _instance = new PixelOffset();
        _instance.left = l;
        _instance.top = t;
        _instance.right = r;
        _instance.bottom = b;
        return _instance;
    }

    public static final PixelOffset DEFAULT = new PixelOffset();
}
