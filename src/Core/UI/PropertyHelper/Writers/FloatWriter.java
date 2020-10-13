package Core.UI.PropertyHelper.Writers;

import Core.UI.PropertyHelper.FieldWriter;
import imgui.ImGui;

import java.lang.reflect.Field;

public class FloatWriter extends FieldWriter {
    @Override
    protected void draw(Field field, Object object) throws IllegalAccessException {
        float[] values = {field.getFloat(object)};
        ImGui.dragFloat(field.getName() + "##", values);
        if (values[0] != field.getFloat(object)) {
            field.setFloat(object, values[0]);
        }
    }
}
