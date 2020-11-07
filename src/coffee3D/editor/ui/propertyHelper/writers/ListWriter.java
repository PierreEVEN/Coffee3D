package coffee3D.editor.ui.propertyHelper.writers;

import coffee3D.editor.ui.propertyHelper.FieldWriter;
import coffee3D.editor.ui.propertyHelper.StructureReader;
import imgui.ImGui;

import java.util.ArrayList;

public class ListWriter<T>  extends FieldWriter {
    public ListWriter() { super(ArrayList.class); }

    @Override
    protected Object draw(String field, Object object) throws IllegalAccessException {

        if (ImGui.collapsingHeader(field)) {
            ArrayList<T> array = (ArrayList) object;
            if (ImGui.beginChild("child_" + field, ImGui.getContentRegionAvailX(), array.size() * 40)) {
                int removedItem = -1;
                for (int i = 0; i < array.size(); ++i) {
                    if (array.size() > 0) {
                        if (ImGui.button("-##" + field + i)) {
                            removedItem = i;
                            ImGui.endChild();
                            continue;
                        }
                        ImGui.sameLine();
                    }
                    if (array.get(i) != null) {
                        Object result = StructureReader.WriteObj(array.get(i), "##" + field + "[" + i + "]");
                        if (result != null) {
                            array.set(i, (T) result);
                        }
                    } else {
                        ImGui.text("none");
                    }
                }
                if (removedItem >= 0) {
                    array.remove(removedItem);
                }
            }
            ImGui.endChild();
        }



        return null;
    }


}
