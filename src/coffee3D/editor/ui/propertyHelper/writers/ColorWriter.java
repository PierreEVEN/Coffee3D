package coffee3D.editor.ui.propertyHelper.writers;

import coffee3D.core.types.Color;
import coffee3D.editor.ui.propertyHelper.FieldWriter;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;

public class ColorWriter extends FieldWriter
{
    public ColorWriter() {
        super(Color.class);
    }

    private static String _opennedColorPicker = null;


    @Override
    protected Object draw(String field, Object object) throws IllegalAccessException {
        Color col = (Color) object;

        final float[] colVec = {col.getVector().x, col.getVector().y, col.getVector().z, col.getVector().w};
        final float[] colPower = {col.getPower()};

        ImGui.colorButton("##" + field, colVec, ImGuiColorEditFlags.PickerHueWheel);
        ImGui.sameLine();
        if (ImGui.treeNode("##treeNode " + field)) {
            FloatWriter wr = (FloatWriter)FieldWriter.Find(Float.class);

            ImGui.dragFloat("##power" + field, colPower);

            ImGui.colorPicker4("##picker" + field, colVec);
            ImGui.treePop();
        }


        if (colVec[0] != col.getVector().x ||
                        colVec[1] != col.getVector().y ||
                        colVec[2] != col.getVector().z ||
                        colVec[3] != col.getVector().w ||
                colPower[0] != col.getPower()
        ) {
            return new Color(colVec[0], colVec[1], colVec[2], colVec[3], colPower[0]);
        }




        return null;
    }
}
