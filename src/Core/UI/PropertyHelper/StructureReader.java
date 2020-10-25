package Core.UI.PropertyHelper;

import Core.IO.LogOutput.Log;
import imgui.ImGui;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class StructureReader {

    public static int debugIndex = 0;

    public static Object WriteObj(Object obj, String nodeName) throws IllegalAccessException {
        if (obj != null) {
            FieldWriter foundWriter = FieldWriter.Find(obj.getClass());
            if (foundWriter != null) {
                // use adapted writer

                ImGui.columns(2);
                if (nodeName.charAt(0) != '#') {
                    ImGui.text(nodeName);
                }
                ImGui.nextColumn();
                Object result = foundWriter.draw(nodeName, obj);
                ImGui.columns(1);
                return result;
            }
            else {
                // Case no writer were found
                if (ImGui.treeNode(nodeName)) {
                    for (Field subField : obj.getClass().getDeclaredFields()) {
                        WriteField(subField, obj);
                    }
                    ImGui.treePop();
                }
            }
        }
        return null;
    }

    public static void WriteField(Field field, Object obj) {
        if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()) || Modifier.isTransient(field.getModifiers())|| Modifier.isPrivate(field.getModifiers())) return;
        field.setAccessible(true);
        debugIndex++;

        String nodeName = field.getName();

        try {
            // Handle array case
            if (field.getType().isArray())
            {
                Object[] tab = (Object[]) field.get(obj);
                if (ImGui.treeNode(nodeName)) {
                    for (int i = 0; i < tab.length; ++i) {
                        Object result = WriteObj(tab[i], "##[" + i + "]" + nodeName);
                        if (result != null) {
                            tab[i] = result;
                        }
                    }
                    ImGui.treePop();
                }
            }
            else {
                // Handle other properties
                Object result = WriteObj(field.get(obj), nodeName);
                if (result != null) {
                    field.set(obj, result);
                }
            }
        }
        catch (Exception e) {
            Log.Warning("failed to read field " + field.getName() + " : " + e.getMessage());
        }
    }
}
