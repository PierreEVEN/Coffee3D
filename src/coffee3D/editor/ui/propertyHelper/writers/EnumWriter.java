package coffee3D.editor.ui.propertyHelper.writers;

import imgui.ImGui;
import imgui.type.ImInt;

import java.lang.reflect.Field;

public class EnumWriter {

    public static Object Draw(Field enumField, Object object) throws IllegalAccessException {

        if (ImGui.beginCombo("test", enumField.get(object).toString())) {
            for (Object item : enumField.getType().getEnumConstants()) {
                if (ImGui.selectable(item.toString())) {
                    enumField.set(object, item);
                }
            }
            ImGui.endCombo();
        }
        return null;
    }
}
