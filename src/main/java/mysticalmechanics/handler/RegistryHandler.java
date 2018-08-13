package mysticalmechanics.handler;

import mysticalmechanics.MysticalMechanics;
import mysticalmechanics.block.BlockAxle;
import mysticalmechanics.block.BlockCreativeMechSource;
import mysticalmechanics.block.BlockGearbox;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;

public class RegistryHandler {
    @GameRegistry.ObjectHolder("mysticalmechanics:axle_iron")
    public static BlockAxle IRON_AXLE;
    @GameRegistry.ObjectHolder("mysticalmechanics:gearbox_frame")
    public static BlockGearbox GEARBOX_FRAME;
    @GameRegistry.ObjectHolder("mysticalmechanics:creative_mech_source")
    public static BlockCreativeMechSource CREATIVE_MECH_SOURCE;

    @GameRegistry.ObjectHolder("mysticalmechanics:gear_iron")
    public static Item IRON_GEAR;

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event)
    {
        IRON_AXLE = (BlockAxle) new BlockAxle(Material.IRON).setRegistryName(MysticalMechanics.MODID,"axle_iron").setUnlocalizedName("axle_iron").setCreativeTab(CreativeTabs.REDSTONE);
        GEARBOX_FRAME = (BlockGearbox) new BlockGearbox(Material.IRON).setRegistryName(MysticalMechanics.MODID,"gearbox_frame").setUnlocalizedName("gearbox_frame").setCreativeTab(CreativeTabs.REDSTONE);
        CREATIVE_MECH_SOURCE = (BlockCreativeMechSource) new BlockCreativeMechSource().setRegistryName(MysticalMechanics.MODID, "creative_mech_source").setUnlocalizedName("creative_mech_source").setCreativeTab(CreativeTabs.REDSTONE);

        event.getRegistry().register(IRON_AXLE);
        event.getRegistry().register(GEARBOX_FRAME);
        event.getRegistry().register(CREATIVE_MECH_SOURCE);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemBlock(IRON_AXLE).setRegistryName(IRON_AXLE.getRegistryName()));
        event.getRegistry().register(new ItemBlock(GEARBOX_FRAME).setRegistryName(GEARBOX_FRAME.getRegistryName()));
        event.getRegistry().register(new ItemBlock(CREATIVE_MECH_SOURCE).setRegistryName(CREATIVE_MECH_SOURCE.getRegistryName()));

        event.getRegistry().register(IRON_GEAR = new Item().setRegistryName(MysticalMechanics.MODID, "gear_iron").setUnlocalizedName("gear_iron").setCreativeTab(CreativeTabs.REDSTONE));

        OreDictionary.registerOre("gearIron",IRON_GEAR);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        registerItemModel(Item.getItemFromBlock(IRON_AXLE),0,"inventory");
        registerItemModel(Item.getItemFromBlock(GEARBOX_FRAME),0,"inventory");
        registerItemModel(Item.getItemFromBlock(CREATIVE_MECH_SOURCE),0,"inventory");

        registerItemModel(IRON_GEAR,0,"inventory");
    }

    @SideOnly(Side.CLIENT)
    public void registerItemModel(@Nonnull Item item, int meta, String variant)
    {
        ModelLoader.setCustomModelResourceLocation(item,meta,new ModelResourceLocation(item.getRegistryName(),variant));
    }
}
