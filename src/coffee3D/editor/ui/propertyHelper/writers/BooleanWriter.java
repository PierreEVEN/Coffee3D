package coffee3D.editor.ui.propertyHelper.writers;

import coffee3D.editor.ui.propertyHelper.FieldWriter;
import imgui.ImGui;
import imgui.type.ImBoolean;

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
