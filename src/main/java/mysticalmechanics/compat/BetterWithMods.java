package mysticalmechanics.compat;

import mysticalmechanics.MysticalMechanics;
import mysticalmechanics.block.BlockConverterBWM;
import mysticalmechanics.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class BetterWithMods {
    @GameRegistry.ObjectHolder("mysticalmechanics:converter_bwm")
    public static BlockConverterBWM CONVERTER_BWM;

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        CONVERTER_BWM = (BlockConverterBWM) new BlockConverterBWM(Material.WOOD).setRegistryName(MysticalMechanics.MODID, "converter_bwm").setUnlocalizedName("converter_bwm").setCreativeTab(MysticalMechanics.creativeTab).setHardness(5.0F).setResistance(10.0F);

        event.getRegistry().register(CONVERTER_BWM);

        GameRegistry.registerTileEntity(TileEntityConverterBWM.class,new ResourceLocation(MysticalMechanics.MODID,"converter_bwm"));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(CONVERTER_BWM).setRegistryName(CONVERTER_BWM.getRegistryName()));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        registerItemModel(Item.getItemFromBlock(CONVERTER_BWM), 0, "inventory");
    }

    @SideOnly(Side.CLIENT)
    public void registerItemModel(@Nonnull Item item, int meta, String variant) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), variant));

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConverterBWM.class, new TileEntityConverterBWMRenderer());
    }
}
