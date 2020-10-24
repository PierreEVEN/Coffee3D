package Core.UI.PropertyHelper;

import Core.Assets.Asset;
import Core.Assets.AssetReference;
import Core.IO.LogOutput.Log;
import Core.UI.PropertyHelper.Writers.*;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class FieldWriter {

    private Class _cl;
    private static ArrayList<FieldWriter> _writers;
    public static void RegisterWriter(FieldWriter _writer) {
        if (_writers == null) _writers = new ArrayList<>();
        _writers.add(_writer);
    }
    public static FieldWriter Find(Class cl) {
        if (_writers == null) return null;
        for (FieldWriter writer : _writers) {
            if (writer.getType().isAssignableFrom(cl)) {
                return writer;
            }
        }
        return null;
    }

    public FieldWriter(Class cl) {
        _cl = cl;
    }

    public Class getType() { return _cl; }

    protected abstract Object draw(String field, Object object) throws IllegalAccessException;

    public static void RegisterPrimitiveWriters() {
        RegisterWriter(new FloatWriter());
        RegisterWriter(new IntWriter());
        RegisterWriter(new BooleanWriter());
        RegisterWriter(new StringWriter());
        RegisterWriter(new Vector3Writer());
        RegisterWriter(new QuaternionWriter());
        RegisterWriter(new AssetWriter());
        RegisterWriter(new ArrayListWriter());
    }
}
