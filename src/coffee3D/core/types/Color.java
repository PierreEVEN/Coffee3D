package coffee3D.core.types;

import imgui.ImGui;
import org.joml.Vector4f;

import java.io.Serializable;

public class Color implements Serializable {

    private static final long serialVersionUID = -1497952084847816013L;

    public Color(float r, float g, float b, float a) {
        _color = new Vector4f(r,g,b,a);
        _power = 1f;
    }

    public Color(float r, float g, float b, float a, float power) {
        _color = new Vector4f(r,g,b,a);
        _power = power;
    }

    private Vector4f _color;
    private float _power;

    public Vector4f getVector() { return _color; }
    public float getPower() { return _power; }

    public int asInt() {
        return ImGui.getColorU32(_color.x, _color.y, _color.z, _color.w);
    }

    public final static Color _instance = new Color(0,0,0,1);
    public final static int GetInt(float r, float g, float b, float a) {
        _instance.getVector().set(r, g, b, a);
        return _instance.asInt();
    }

    public final static Color RED = new Color(1,0,0,1);
    public final static Color GREEN = new Color(0,1,0,1);
    public final static Color BLUE = new Color(0,0,1,1);
    public final static Color WHITE = new Color(1,1,1,1);
    public final static Color BLACK = new Color(0,0,0,1);
}
