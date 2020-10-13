package Core.UI.PropertyHelper.Writers;

import Core.UI.PropertyHelper.FieldWriter;
import imgui.ImGui;
import imgui.type.ImBoolean;

import java.lang.reflect.Field;

public class BooleanWriter extends FieldWriter {
    private ImBoolean _values;
    @Override
    protected void draw(Field field, Object object) throws IllegalAccessException {
        if (_values == null) _values = new ImBoolean();
        _values.set(field.getBoolean(object));
        ImGui.checkbox(field.getName() + "##", _values);
        if (_values.get() != field.getBoolean(object)) {
            field.setBoolean(object, _values.get());
        }
    }
}
