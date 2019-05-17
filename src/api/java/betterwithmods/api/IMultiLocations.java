package betterwithmods.api;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

/**
 * Used by Items and Blocks with a model location relative to their metadata.
 *
 * @author primetoxinz
 */
public interface IMultiLocations {
    /**
     * Locations to be used as first parameter of {@link ModelResourceLocation}
     *
     * @return String array with all locations, WITHOUT modid.
     */
    String[] getLocations();
}
