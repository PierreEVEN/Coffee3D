package Core.Types;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class TypeHelper {

    private static int _vec3Index = 0;
    private static int _vec4Index = 0;
    private static int _math4Index = 0;

    private final static ArrayList<Vector3f> _vec3s = new ArrayList<>();
    private final static ArrayList<Vector4f> _vec4s = new ArrayList<>();
    private final static ArrayList<Matrix4f> _mat4s = new ArrayList<>();

    public static void nextFrame() {
        _vec3Index = 0;
        _vec4Index = 0;
        _math4Index = 0;
    }

    public static Vector3f getVector3(float x, float y, float z) {
        if (_vec3Index >= _vec3s.size()) {
            _vec3s.add(new Vector3f());
        }
        Vector3f item = _vec3s.get(_vec3Index);
        item.x = x;
        item.y = y;
        item.z = z;
        _vec3Index++;
        return item;
    }

    public static Vector3f getVector3(Vector3f source) {
        if (_vec3Index >= _vec3s.size()) {
            _vec3s.add(new Vector3f());
        }
        Vector3f item = _vec3s.get(_vec3Index);
        item.x = source.x;
        item.y = source.y;
        item.z = source.z;
        _vec3Index++;
        return item;
    }

    public static Vector3f getVector3() {
        if (_vec3Index >= _vec3s.size()) {
            _vec3s.add(new Vector3f());
        }
        Vector3f item = _vec3s.get(_vec3Index);
        _vec3Index++;
        return item;
    }

    public static Vector4f getVector4(float x, float y, float z, float w) {
        if (_vec4Index >= _vec4s.size()) {
            _vec4s.add(new Vector4f());
        }
        Vector4f item = _vec4s.get(_vec4Index);
        item.x = x;
        item.y = y;
        item.z = z;
        item.w = w;
        _vec4Index++;
        return item;
    }

    public static Vector4f getVector4() {
        if (_vec4Index >= _vec4s.size()) {
            _vec4s.add(new Vector4f());
        }
        Vector4f item = _vec4s.get(_vec4Index);
        _vec4Index++;
        return item;
    }

    public static Matrix4f getMat4() {
        if (_math4Index >= _mat4s.size()) {
            _mat4s.add(new Matrix4f());
        }
        Matrix4f item = _mat4s.get(_math4Index);
        _math4Index++;
        return item;
    }
}
