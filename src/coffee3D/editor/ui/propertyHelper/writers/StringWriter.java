package coffee3D.editor.ui.propertyHelper.writers;

import coffee3D.core.io.log.Log;
import coffee3D.editor.ui.propertyHelper.FieldWriter;
import imgui.ImGui;
import imgui.type.ImString;

public class StringWriter extends FieldWriter
{

    ImString text = new ImString();

    public StringWriter() {
        super(String.class);
        if (text.getBufferSize() < 100) text.resize(100);

    }

    @Override
    protected Object draw(String field, Object object) {

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
