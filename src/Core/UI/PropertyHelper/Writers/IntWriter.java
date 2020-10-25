package Core.UI.PropertyHelper.Writers;

import Core.UI.PropertyHelper.FieldWriter;
import imgui.ImGui;

import java.lang.reflect.Field;

public class IntWriter extends FieldWriter
{
    public IntWriter() {
        super(Integer.class);
    }

    @Override
    protected Object draw(String field, Object object) {
        int[] values = {(Integer)object};
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.dragInt("##" + field, values);
        if (values[0] != (Integer)object) {
            return values[0];
        }
        return null;
    }
}
