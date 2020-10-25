package Core.Types;

import org.joml.Vector4f;

public class Color {

    public Color(float r, float g, float b, float a) {
        _color = new Vector4f(r,g,b,a);
    }

    private Vector4f _color;

    public Vector4f getVector() { return _color; }
}
