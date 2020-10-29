package coffee3D.core.ui.hud;


public final class NodeAnchor {
    private static NodeAnchor _instance;
    public static NodeAnchor Get(float mX, float mY, float MX, float MY) {
        if (_instance == null) _instance = new NodeAnchor(mX, mY, MX, MY);
        _instance.minX = mX;
        _instance.minY = mY;
        _instance.maxX = MX;
        _instance.maxY = MY;
        return _instance;
    }

    private NodeAnchor(float l, float t, float r, float b) {
        minX = l;
        minY = t;
        maxX = r;
        maxY = b;
    }

    public float minX;
    public float minY;
    public float maxX;
    public float maxY;

    public static final NodeAnchor TOP_LEFT = new NodeAnchor(0,0,0,0);
    public static final NodeAnchor TOP_CENTER = new NodeAnchor(.5f,0,.5f,0);
    public static final NodeAnchor TOP_RIGHT = new NodeAnchor(1,0,1,0);
    public static final NodeAnchor TOP_FILL = new NodeAnchor(0,0,1,0);

    public static final NodeAnchor LEFT_CENTER = new NodeAnchor(0,.5f,0,.5f);
    public static final NodeAnchor CENTER = new NodeAnchor(.5f,.5f,.5f,.5f);
    public static final NodeAnchor RIGHT_CENTER = new NodeAnchor(1,.5f,1,.5f);
    public static final NodeAnchor FILL_X = new NodeAnchor(0,.5f,1,.5f);

    public static final NodeAnchor BOTTOM_LEFT = new NodeAnchor(1,0,1,0);
    public static final NodeAnchor BOTTOM_CENTER = new NodeAnchor(1,.5f,1,.5f);
    public static final NodeAnchor BOTTOM_RIGHT = new NodeAnchor(1,1,1,1);
    public static final NodeAnchor BOTTOM_FILL = new NodeAnchor(0,1,1,1);

    public static final NodeAnchor FILL_LEFT = new NodeAnchor(0,0,0, 1);
    public static final NodeAnchor FILL_Y = new NodeAnchor(.5f,0,.5f, 1);
    public static final NodeAnchor FILL_RIGHT = new NodeAnchor(1,0,1, 1);
    public static final NodeAnchor FILL = new NodeAnchor(0,0,1,1);
}