package Core.Types;

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

    public final static Color RED = new Color(1,0,0,1);
    public final static Color GREEN = new Color(0,1,0,1);
    public final static Color BLUE = new Color(0,0,1,1);
    public final static Color WHITE = new Color(1,1,1,1);
    public final static Color BLACK = new Color(0,0,0,1);
}
