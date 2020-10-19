package Core.UI.PropertyHelper.Writers;

import Core.UI.PropertyHelper.FieldWriter;
import imgui.ImGui;

import java.lang.reflect.Field;
import java.nio.FloatBuffer;

public class FloatWriter extends FieldWriter {
    public FloatWriter() {
        super(Float.TYPE);
    }

    @Override
    protected boolean draw(String fieldName, Object object) throws IllegalAccessException {

        ((Float)object) = Float.valueOf(10);

        Float obj = (Float)object;

        float[] values = {obj};
        ImGui.dragFloat(fieldName + "##", values);
        if (values[0] != (Float)object) {
            obj = values[0];
        }
        return false;
    }
}
