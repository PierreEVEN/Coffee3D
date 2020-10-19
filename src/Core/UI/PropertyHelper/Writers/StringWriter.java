package Core.UI.PropertyHelper.Writers;

import Core.UI.PropertyHelper.FieldWriter;
import imgui.ImGui;

import java.lang.reflect.Field;

public class StringWriter extends FieldWriter {
    public StringWriter() {
        super(String.class);
    }

    @Override
    protected boolean draw(Field field, Object object) throws IllegalAccessException {
        String value = (String)field.get(object);
        if  (value != null) {
            ImGui.text(value);
        }
        return false;
    }
}
