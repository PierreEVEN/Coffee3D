package coffee3D.editor.ui.propertyHelper.writers;

import coffee3D.editor.ui.propertyHelper.FieldWriter;
import imgui.ImGui;

public class FloatWriter extends FieldWriter {
    public FloatWriter() {
        super(Float.class);
    }

    @Override
    protected Object draw(String field, Object object) throws IllegalAccessException {


        float[] values = {(Float)object};

        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.dragFloat("##" + field, values);

        if (values[0] != (Float)object) {
            return values[0];
        }
        return null;
    }
}
