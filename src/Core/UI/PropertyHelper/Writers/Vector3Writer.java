package Core.UI.PropertyHelper.Writers;

import Core.UI.PropertyHelper.FieldWriter;
import imgui.ImGui;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Field;

public class Vector3Writer extends FieldWriter
{
    public Vector3Writer() {
        super(Vector3f.class);
    }

    @Override
    protected Object draw(String field, Object object) throws IllegalAccessException {

        Vector3f vec = (Vector3f) object;
        float[] values = {vec.x, vec.y, vec.z};
        ImGui.dragFloat3("##" + field, values);
        if (values[0] != vec.x) {
            vec.x = values[0];
            return vec;
        }
        if (values[1] != vec.y) {
            vec.y = values[1];
            return vec;
        }
        if (values[2] != vec.z) {
            vec.z = values[2];
            return vec;
        }


        return null;
    }
}
