package coffee3D.editor.ui.propertyHelper.writers;

import coffee3D.core.io.log.Log;
import coffee3D.editor.ui.propertyHelper.FieldWriter;
import imgui.ImGui;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class QuaternionWriter extends FieldWriter
{
    private final Vector3f _euler = new Vector3f();;

    public QuaternionWriter() {
        super(Quaternionf.class);
    }

    @Override
    protected Object draw(String field, Object object) throws IllegalAccessException {

        Quaternionf vec = (Quaternionf) object;
        vec.getEulerAnglesXYZ(_euler);

        _euler.x = (float)Math.toDegrees(_euler.x);
        _euler.y = (float)Math.toDegrees(_euler.y);
        _euler.z = (float)Math.toDegrees(_euler.z);

        float[] values = {_euler.x, _euler.y, _euler.z};
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.dragFloat3("##" + field, values);

        if (values[0] != _euler.x || values[1] != _euler.y || values[2] != _euler.z) {
            vec.identity().rotateXYZ((float)Math.toRadians(values[0]), (float)Math.toRadians(values[1]), (float)Math.toRadians(values[2]));
            return vec;
        }

        return null;
    }
}
