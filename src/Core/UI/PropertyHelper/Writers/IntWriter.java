package Core.UI.PropertyHelper.Writers;

import Core.UI.PropertyHelper.FieldWriter;
import imgui.ImGui;

import java.lang.reflect.Field;

public class IntWriter extends FieldWriter {
    public IntWriter() {
        super(Integer.TYPE);
    }

    @Override
    protected boolean draw(String fieldName, Object object) throws IllegalAccessException {

        Integer obj = (Integer)object;

        int[] values = {obj};
        ImGui.dragInt(fieldName + "##", values);
        if (values[0] != obj) {
            obj = values[0];
        }
        return false;
    }
}
