package Core.UI.PropertyHelper.Writers;

import Core.UI.PropertyHelper.FieldWriter;
import imgui.ImGui;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Field;

public class Vector3Writer extends FieldWriter {
    public Vector3Writer(Class cl) {
        super(cl);
    }

    @Override
    protected void draw(Field field, Object object) throws IllegalAccessException {
        Vector3f vec = (Vector3f) field.get(object);
        float[] values = {vec.x, vec.y, vec.z};
        ImGui.text(field.getName() + " : ");
        ImGui.sameLine();
        ImGui.dragFloat3("##" + field.getName(), values);
        if (values[0] != vec.x) {
            vec.x = values[0];
        }
        if (values[1] != vec.y) {
            vec.y = values[1];
        }
        if (values[2] != vec.z) {
            vec.z = values[2];
        }
    }
}
