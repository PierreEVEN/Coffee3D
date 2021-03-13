package coffee3D.core.types;

import coffee3D.core.io.log.Log;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import org.joml.*;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class TypeHelper {
    private static int _globalUid = 0;
    private static int _vec3Index = 0;
    private static int _vec4Index = 0;
    private static int _math4Index = 0;
    private static int _quatIndex = 0;
    private static int _frameUid = 0;

    private final static ArrayList<Vector3f> _vec3s = new ArrayList<>();
    private final static ArrayList<Vector4f> _vec4s = new ArrayList<>();
    private final static ArrayList<Matrix4f> _mat4s = new ArrayList<>();
    private final static ArrayList<Quaternionf> _quats = new ArrayList<>();

    public static void nextFrame() {
        _vec3Index = 0;
        _vec4Index = 0;
        _math4Index = 0;
        _quatIndex = 0;
        _frameUid = 0;
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
        return item.identity();
    }

    public static Quaternionf getQuat() {
        if (_quatIndex >= _quats.size()) {
            _quats.add(new Quaternionf());
        }
        Quaternionf item = _quats.get(_quatIndex);
        _quatIndex++;
        return item.identity();
    }

    public static void DrawStats() {
        if (ImGui.collapsingHeader("Memory manager statistics")) {
            ImGui.text("vector3 count : " + _vec3s.size());
            ImGui.text("vector4 count : " + _vec4s.size());
            ImGui.text("matrix4 count : " + _mat4s.size());
            ImGui.text("quaternion count : " + _quats.size());
        }
    }

    public static void ClearMemory() {
        _vec3s.clear();
        _vec4s.clear();
        _mat4s.clear();
        _quats.clear();
        nextFrame();
    }

    public static int GetFrameUid() { return _frameUid++; }

    public static int MakeGlobalUid() {
        return _globalUid++;
    }

    public static int GetStructByteSize(Class<?> structClass) {
        int size = 0;
        for (Field field : structClass.getFields()) {
            if (field.getType() == Integer.TYPE) size += 4;
            else if (field.getType() == Float.TYPE) size += 4;
            else if (field.getType() == Integer.class) size += 4;
            else if (field.getType() == Float.class) size += 4;
            else if (field.getType() == Matrix4f.class) size += 16 * 4;
            else if (field.getType() == Matrix3f.class) size += 9 * 4;
            else if (field.getType() == Vector4f.class) size += 4 * 4;
            else if (field.getType() == Vector3f.class) size += 3 * 4;
            else if (field.getType() == Vector2f.class) size += 2 * 4;
            else Log.Fail("failed to calculate " + field.getType().getSimpleName() + " size");
        }
        return size;
    }

    public static int GetStructFloatSize(Class<?> structClass) {
        return GetStructByteSize(structClass) / 4;
    }

    public static float[] SerializeStructure(Object structure, float[] buffer) {
        int offset = 0;
        if (structure.getClass().isArray()) {
            Object[] array = (Object[])structure;
            int floatSize = GetStructFloatSize(array.getClass().getComponentType());
            for (Object item : array) {
                SerializeStructure(item, buffer, offset);
                offset += floatSize;
            }
        }
        else {
            SerializeStructure(structure, buffer, offset);
        }
        return buffer;
    }

    private static void SerializeStructure(Object structure, float[] buffer, int offset) {
        try {
            for (Field field : structure.getClass().getFields()) {
                if (field.get(structure) == null) Log.Display("warning : structure " + structure + " have null value for : " + field.getName());
                if (field.getType() == Float.TYPE || field.getType() == Float.class) buffer[offset++] = (field.getFloat(structure));
                else if (field.getType() == Matrix4f.class) {
                    Matrix4f val = ((Matrix4f)field.get(structure));
                    buffer[offset++] = val.get(0, 0);
                    buffer[offset++] = val.get(0, 1);
                    buffer[offset++] = val.get(0, 2);
                    buffer[offset++] = val.get(0, 3);
                    buffer[offset++] = val.get(1, 0);
                    buffer[offset++] = val.get(1, 1);
                    buffer[offset++] = val.get(1, 2);
                    buffer[offset++] = val.get(1, 3);
                    buffer[offset++] = val.get(2, 0);
                    buffer[offset++] = val.get(2, 1);
                    buffer[offset++] = val.get(2, 2);
                    buffer[offset++] = val.get(2, 3);
                    buffer[offset++] = val.get(3, 0);
                    buffer[offset++] = val.get(3, 1);
                    buffer[offset++] = val.get(3, 2);
                    buffer[offset++] = val.get(3, 3);
                }
                else if (field.getType() == Matrix3f.class) {
                    Matrix3f val = ((Matrix3f)field.get(structure));
                    buffer[offset++] = val.m00;
                    buffer[offset++] = val.m01;
                    buffer[offset++] = val.m02;
                    buffer[offset++] = val.m10;
                    buffer[offset++] = val.m11;
                    buffer[offset++] = val.m12;
                    buffer[offset++] = val.m20;
                    buffer[offset++] = val.m21;
                    buffer[offset++] = val.m22;
                }
                else if (field.getType() == Vector4f.class) {
                    Vector4f val = ((Vector4f)field.get(structure));
                    buffer[offset++] = val.x;
                    buffer[offset++] = val.y;
                    buffer[offset++] = val.z;
                    buffer[offset++] = val.w;
                }
                else if (field.getType() == Vector3f.class) {
                    Vector3f val = ((Vector3f)field.get(structure));
                    buffer[offset++] = val.x;
                    buffer[offset++] = val.y;
                    buffer[offset++] = val.z;
                }
                else if (field.getType() == Vector2f.class) {
                    Vector2f val = ((Vector2f)field.get(structure));
                    buffer[offset++] = val.x;
                    buffer[offset++] = val.y;
                }
                else Log.Fail("cannot serialize " + field.getType().getSimpleName() + " to floatBuffer");
            }
        }
        catch (Exception e) {
            Log.Fail("failed to serialize structure " + structure.getClass().getSimpleName() + " to float buffer : " + e.getMessage());
        }
    }
}
