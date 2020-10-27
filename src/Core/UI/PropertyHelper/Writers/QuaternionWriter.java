package Core.UI.PropertyHelper.Writers;

import Core.IO.LogOutput.Log;
import Core.UI.PropertyHelper.FieldWriter;
import imgui.ImGui;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Field;

public class QuaternionWriter extends FieldWriter
{
    private Vector3f _euler;

    public QuaternionWriter() {
        super(Quaternionf.class);
    }

    @Override
    protected Object draw(String field, Object object) throws IllegalAccessException {

        Quaternionf vec = (Quaternionf) object;
        if (_euler == null) _euler = new Vector3f();
        vec.getEulerAnglesXYZ(_euler);

        _euler.x = (float)Math.toDegrees(_euler.x);
        _euler.y = (float)Math.toDegrees(_euler.y);
        _euler.z = (float)Math.toDegrees(_euler.z);

        float[] values = {_euler.x, _euler.y, _euler.z};
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.dragFloat3("##" + field, values);

        if (values[0] != _euler.x || values[1] != _euler.y || values[2] != _euler.z) {
            return vec.identity().rotateXYZ((float)Math.toRadians(values[0]), (float)Math.toRadians(values[1]), (float)Math.toRadians(values[2]));
        }

        return null;
    }
}
