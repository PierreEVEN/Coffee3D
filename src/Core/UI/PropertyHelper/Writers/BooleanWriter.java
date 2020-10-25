package Core.UI.PropertyHelper.Writers;

import Core.UI.PropertyHelper.FieldWriter;
import imgui.ImGui;
import imgui.type.ImBoolean;

import java.lang.reflect.Field;

public class BooleanWriter extends FieldWriter
{
    private ImBoolean _values;

    public BooleanWriter() {
        super(Boolean.class);
    }

    @Override
    protected Object draw(String field, Object object) throws IllegalAccessException {
        if (_values == null) _values = new ImBoolean();
        _values.set((Boolean)object);
        ImGui.checkbox("##" + field, _values);
        if (_values.get() != (Boolean)object) {
            return _values.get();
        }
        return null;
    }
}
