package Core.UI.PropertyHelper.Writers;

import Core.UI.PropertyHelper.FieldWriter;
import imgui.ImGui;

import java.lang.reflect.Field;

public class IntWriter extends FieldWriter {
    public IntWriter(Class cl) {
        super(cl);
    }

    @Override
    protected void draw(Field field, Object object) throws IllegalAccessException {
        int[] values = {field.getInt(object)};
        ImGui.dragInt(field.getName() + "##", values);
        if (values[0] != field.getInt(object)) {
            field.setInt(object, values[0]);
        }
    }
}
