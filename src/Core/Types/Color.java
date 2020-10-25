package Core.Types;

import org.joml.Vector4f;

public class Color {

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
}
