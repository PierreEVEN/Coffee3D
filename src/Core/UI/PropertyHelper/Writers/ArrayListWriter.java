package Core.UI.PropertyHelper.Writers;

import Core.IO.LogOutput.Log;
import Core.UI.PropertyHelper.FieldWriter;
import Core.UI.PropertyHelper.StructureReader;
import imgui.ImGui;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class ArrayListWriter<T>  extends FieldWriter {
    public ArrayListWriter() { super(ArrayList.class); }

    @Override
    protected Object draw(String field, Object object) throws IllegalAccessException {

        if (ImGui.treeNode(field)) {
            ArrayList<T> array = (ArrayList) object;
            int removedItem = -1;
            for (int i = 0; i < array.size(); ++i) {
                if (array.get(i) != null) {
                    Object result = StructureReader.WriteObj(array.get(i), "[" + i + "]");
                    if (result != null) {
                        array.set(i, (T)result);
                    }
                }
                else {
                    ImGui.text("none");
                }
                if (array.size() > 0) {
                    ImGui.sameLine();
                    if (ImGui.button("-##" + field + i)) {
                        removedItem = i;
                    }
                }
            }
            if (removedItem >= 0) {
                array.remove(removedItem);
            }
            if (ImGui.button("+##" + field)) {

                if (array.getClass().getComponentType() == null) {
                    Log.Display("putain sa mere " + array.getClass());
                }

                try {
                    Constructor ctor = array.getClass().getComponentType().getConstructor();
                    if (ctor != null) {
                        T instancedItem = (T)ctor.newInstance();
                        if (instancedItem != null) {
                            array.add(instancedItem);
                        }
                        else {
                            array.add(null);
                        }
                    }
                    else {
                        array.add(null);
                    }
                }
                catch (Exception e) {
                    Log.Warning("failed to instantiate object : " + e.getMessage());
                }
            }
            ImGui.treePop();
        }



        return null;
    }


}
