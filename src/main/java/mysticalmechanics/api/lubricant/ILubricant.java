package mysticalmechanics.api.lubricant;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public interface ILubricant {
    ResourceLocation getType();

    String getUnlocalizedName();

    Color getColor();

    Object getParameter(String name);

    default double getDouble(String name, double _default) {
        Object value = getParameter(name);
        if(value instanceof Number)
            return ((Number) value).doubleValue();
        else
            return _default;
    }

    default double getSpeedMod() {
        return getDouble("speed",1);
    }

    default double getFrictionMod() {
        return getDouble("friction",1);
    }

    default double getHeatMod() {
        return getDouble("heat",1);
    }

    default NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setString("type",getType().toString());
        return tag;
    }

    default void readFromNBT(NBTTagCompound tag) {}
}
