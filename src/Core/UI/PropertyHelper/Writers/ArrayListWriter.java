package Core.UI.PropertyHelper.Writers;

import Core.UI.PropertyHelper.FieldWriter;
import imgui.ImGui;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ArrayListWriter extends FieldWriter {
    public ArrayListWriter() {
        super(ArrayList.class);
    }

    @Override
    protected boolean draw(String fieldName, Object object) throws IllegalAccessException {
        /*
        ArrayList value = (ArrayList) field.get(object);
        ImGui.text("testValue");

         */
        return false;
    }
}
