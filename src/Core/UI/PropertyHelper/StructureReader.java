package Core.UI.PropertyHelper;

import Core.IO.LogOutput.Log;
import imgui.ImGui;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class StructureReader {

    public static int debugIndex = 0;

    public static void DrawStructView(Field field, Object obj) {
        if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) return;
        field.setAccessible(true);
        debugIndex++;

        String nodeName = field.getName() + " (" + field.getType().getSimpleName() + ")" + "##" + debugIndex;

        try {
            FieldWriter foundWriter = FieldWriter.Find(field.getType().getSimpleName());

            if (foundWriter != null)
            {
                // Draw editor if found
                foundWriter.draw(field, obj);
            }
            else {
                // Case no editor were found
                if (ImGui.treeNode(nodeName)) {
                    Object subObj = field.get(obj);
                    if (subObj != null) {
                        for (Field subField : subObj.getClass().getDeclaredFields()) {
                            DrawStructView(subField, subObj);
                        }
                    }
                    ImGui.treePop();
                }
            }
        }
        catch (Exception e) {
            Log.Warning("failed to read field " + field.getName() + " : " + e.getMessage());
        }


        /*
        if (obj == null) return;
        Class objClass = obj.getClass();

        if (ImGui.treeNode(objClass.getSimpleName() + "_" + debugIndex)) {
            for (Field field : objClass.getFields()) {
                try {
                    FieldWriter foundWriter = FieldWriter.Find(field.getClass());
                    if (foundWriter != null) foundWriter.draw(obj, field);
                    else if (field.get(obj) != obj) DrawStructView(field.get(obj));
                }
                catch (Exception e) {
                    Log.Warning("failed to read field " + field.getName() + " : " + e.getMessage());
                }
            }
            ImGui.treePop();
        }
        */
    }
}
