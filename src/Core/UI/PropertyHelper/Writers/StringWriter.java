package Core.UI.PropertyHelper.Writers;

import Core.UI.PropertyHelper.FieldWriter;
import imgui.ImGui;
import imgui.type.ImString;

import java.lang.reflect.Field;

public class StringWriter extends FieldWriter
{

    ImString text = new ImString();

    public StringWriter() {
        super(String.class);
    }

    @Override
    protected Object draw(String field, Object object) throws IllegalAccessException {

        text.set((String)object);
        if  (text != null) {
            ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
            ImGui.inputText("##" + field, text);
            if (!text.get().equals(object)) {
                return text.get();
            }
        }


        return null;
    }
}
