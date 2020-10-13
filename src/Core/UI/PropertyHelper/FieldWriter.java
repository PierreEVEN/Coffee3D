package Core.UI.PropertyHelper;

import Core.IO.LogOutput.Log;
import Core.UI.PropertyHelper.Writers.*;

import java.lang.reflect.Field;
import java.util.HashMap;

public abstract class FieldWriter {

    private static HashMap<String, FieldWriter> _writers;

    public static void RegisterWriter(String cl, FieldWriter _writer) {
        if (_writers == null) _writers = new HashMap<>();
        _writers.put(cl, _writer);
    }

    public static FieldWriter Find(String cl) {
        if (_writers == null) return null;
        return _writers.get(cl);
    }

    protected abstract void draw(Field field, Object object) throws IllegalAccessException;

    public static void RegisterPrimitiveWriters() {
        RegisterWriter("float", new FloatWriter());
        RegisterWriter("Vector3f", new Vector3Writer());
        RegisterWriter("Quaternionf", new QuaternionWriter());
        RegisterWriter("int", new IntWriter());
        RegisterWriter("boolean", new BooleanWriter());
    }
}
