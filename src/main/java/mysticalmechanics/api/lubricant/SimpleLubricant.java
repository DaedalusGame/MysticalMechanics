package mysticalmechanics.api.lubricant;

import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SimpleLubricant implements ILubricant {
    ResourceLocation resLoc;
    Color color;
    Map<String, Object> parameters = new HashMap<>();

    public SimpleLubricant(ResourceLocation resLoc, Color color) {
        this.resLoc = resLoc;
        this.color = color;
    }

    public SimpleLubricant setParameter(String name, Object value) {
        parameters.put(name, value);
        return this;
    }

    @Override
    public ResourceLocation getType() {
        return resLoc;
    }

    @Override
    public String getUnlocalizedName() {
        return "mysticalmechanics.lubricant."+resLoc.getResourcePath();
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public Object getParameter(String name) {
        return parameters.get(name);
    }
}
