package Core.Types;

import org.joml.Vector3f;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class TypeHelper {

    private static int _vecIndex = 0;

    private final static ArrayList<Vector3f> _items = new ArrayList<>();

    public static void nextFrame() {
        _vecIndex = 0;
    }

    public static Vector3f getVector(float x, float y, float z) {
        if (_vecIndex >= _items.size()) {
            _items.add(new Vector3f());
        }
        Vector3f item = _items.get(_vecIndex);
        item.x = x;
        item.y = y;
        item.z = z;
        _vecIndex++;
        return item;
    }

}
