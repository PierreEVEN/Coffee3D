package Core.UI.PropertyHelper;

import Core.IO.LogOutput.Log;
import imgui.ImGui;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class StructureReader {

    public static int debugIndex = 0;


    public static Object WriteObj(Object obj, String nodeName) {
        try {
            return WriteObj(obj, nodeName, false);
        } catch (IllegalAccessException e) {
            Log.Warning("failed to edit object " + obj.getClass().getSimpleName() + " properties : " + e.getMessage());
            return null;
        }
    }

    private static Object WriteObj(Object obj, String nodeName, boolean bIsChild) throws IllegalAccessException {
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
                if (bIsChild && ImGui.treeNode(nodeName)) {
                    drawObjectDefault(obj, obj.getClass());
                    ImGui.treePop();
                }
                else {
                    drawObjectDefault(obj, obj.getClass());
                }
            }
        }
        return null;
    }

    private static void drawObjectDefault(Object obj, Class cl) {

        if (cl.getSuperclass() != null && cl.getSuperclass() != Object.class) {
            drawObjectDefault(obj, cl.getSuperclass());
        }

        for (Field subField : cl.getDeclaredFields()) {
            WriteField(subField, obj);
        }
    }

    private static void WriteField(Field field, Object obj) {
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
                        Object result = WriteObj(tab[i], "##[" + i + "]" + nodeName, true);
                        if (result != null) {
                            tab[i] = result;

                            if (obj instanceof SerializableData) {
                                ((SerializableData)obj).edit();
                            }
                        }
                    }
                    ImGui.treePop();
                }
            }
            else {
                // Handle other properties
                Object result = WriteObj(field.get(obj), nodeName, true);
                if (result != null) {
                    field.set(obj, result);

                    Log.Display(obj.getClass().getSimpleName());
                    if (obj instanceof SerializableData) {
                        ((SerializableData)obj).edit();
                    }
                }
            }
        }
        catch (Exception e) {
            Log.Warning("failed to read field " + field.getName() + " : " + e.getMessage());
        }
    }
}
