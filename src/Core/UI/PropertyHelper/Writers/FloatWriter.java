package Core.UI.PropertyHelper.Writers;

import Core.UI.PropertyHelper.FieldWriter;
import imgui.ImGui;

import java.lang.reflect.Field;
import java.nio.FloatBuffer;

public class FloatWriter extends FieldWriter {
    public FloatWriter() {
        super(Float.class);
    }

    @Override
    protected Object draw(String field, Object object) throws IllegalAccessException {


        float[] values = {(Float)object};

        ImGui.dragFloat("##" + field, values);

        if (values[0] != (Float)object) {
            return values[0];
        }
        return null;
    }
}
