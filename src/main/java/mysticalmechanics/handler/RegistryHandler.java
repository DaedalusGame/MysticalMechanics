package mysticalmechanics.handler;

import mysticalmechanics.MysticalMechanics;
import mysticalmechanics.block.*;
import mysticalmechanics.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;

import static net.minecraftforge.fml.common.registry.GameRegistry.*;

@Mod.EventBusSubscriber
public class RegistryHandler {
    @ObjectHolder("mysticalmechanics:axle_iron")
    public static BlockAxle IRON_AXLE;
    @ObjectHolder("mysticalmechanics:gearbox_frame")
    public static BlockGearbox GEARBOX_FRAME;
    @ObjectHolder("mysticalmechanics:mergebox_frame")
    public static BlockGearbox MERGEBOX_FRAME;
    @ObjectHolder("mysticalmechanics:creative_mech_source")
    public static BlockCreativeMechSource CREATIVE_MECH_SOURCE;

    @ObjectHolder("mysticalmechanics:gear_iron")
    public static Item IRON_GEAR;
    @ObjectHolder("mysticalmechanics:gear_gold")
    public static Item GOLD_GEAR;
    @ObjectHolder("mysticalmechanics:gear_gold_on")
    public static Item GOLD_GEAR_ON;
    @ObjectHolder("mysticalmechanics:gear_gold_off")
    public static Item GOLD_GEAR_OFF;
    @ObjectHolder("mysticalmechanics:gear_fan")
    public static Item FAN;

    @ObjectHolder("mysticalmechanics:block.gear.add")
    public static SoundEvent GEAR_ADD;
    @ObjectHolder("mysticalmechanics:block.gear.remove")
    public static SoundEvent GEAR_REMOVE;

    @ObjectHolder("mysticalmechanics:block.gearbox.fast.lv1")
    public static SoundEvent GEARBOX_FAST_LV1;
    @ObjectHolder("mysticalmechanics:block.gearbox.fast.lv2")
    public static SoundEvent GEARBOX_FAST_LV2;
    @ObjectHolder("mysticalmechanics:block.gearbox.fast.lv3")
    public static SoundEvent GEARBOX_FAST_LV3;
    @ObjectHolder("mysticalmechanics:block.gearbox.mid.lv1")
    public static SoundEvent GEARBOX_MID_LV1;
    @ObjectHolder("mysticalmechanics:block.gearbox.mid.lv2")
    public static SoundEvent GEARBOX_MID_LV2;
    @ObjectHolder("mysticalmechanics:block.gearbox.mid.lv3")
    public static SoundEvent GEARBOX_MID_LV3;
    @ObjectHolder("mysticalmechanics:block.gearbox.slow.lv1")
    public static SoundEvent GEARBOX_SLOW_LV1;
    @ObjectHolder("mysticalmechanics:block.gearbox.slow.lv2")
    public static SoundEvent GEARBOX_SLOW_LV2;
    @ObjectHolder("mysticalmechanics:block.gearbox.slow.lv3")
    public static SoundEvent GEARBOX_SLOW_LV3;
    @ObjectHolder("mysticalmechanics:block.gearbox.very_slow.lv1")
    public static SoundEvent GEARBOX_VERYSLOW_LV1;
    @ObjectHolder("mysticalmechanics:block.gearbox.very_slow.lv2")
    public static SoundEvent GEARBOX_VERYSLOW_LV2;
    @ObjectHolder("mysticalmechanics:block.gearbox.very_slow.lv3")
    public static SoundEvent GEARBOX_VERYSLOW_LV3;

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        IRON_AXLE = (BlockAxle) new BlockAxle(Material.IRON).setRegistryName(MysticalMechanics.MODID, "axle_iron").setUnlocalizedName("axle_iron").setCreativeTab(MysticalMechanics.creativeTab).setHardness(5.0F).setResistance(10.0F);
        GEARBOX_FRAME = (BlockGearbox) new BlockGearbox(Material.IRON).setRegistryName(MysticalMechanics.MODID, "gearbox_frame").setUnlocalizedName("gearbox_frame").setCreativeTab(MysticalMechanics.creativeTab).setHardness(5.0F).setResistance(10.0F);
        MERGEBOX_FRAME = (BlockMergebox) new BlockMergebox(Material.IRON).setRegistryName(MysticalMechanics.MODID, "mergebox_frame").setUnlocalizedName("mergebox_frame").setCreativeTab(MysticalMechanics.creativeTab).setHardness(5.0F).setResistance(10.0F);
        CREATIVE_MECH_SOURCE = (BlockCreativeMechSource) new BlockCreativeMechSource().setRegistryName(MysticalMechanics.MODID, "creative_mech_source").setUnlocalizedName("creative_mech_source").setCreativeTab(MysticalMechanics.creativeTab).setHardness(5.0F).setResistance(10.0F);

        event.getRegistry().register(IRON_AXLE);
        event.getRegistry().register(GEARBOX_FRAME);
        event.getRegistry().register(MERGEBOX_FRAME);
        event.getRegistry().register(CREATIVE_MECH_SOURCE);

        GameRegistry.registerTileEntity(TileEntityAxle.class,new ResourceLocation(MysticalMechanics.MODID,"axle"));
        GameRegistry.registerTileEntity(TileEntityGearbox.class,new ResourceLocation(MysticalMechanics.MODID,"gearbox"));
        GameRegistry.registerTileEntity(TileEntityMergebox.class,new ResourceLocation(MysticalMechanics.MODID,"mergebox"));
        GameRegistry.registerTileEntity(TileEntityCreativeMechSource.class,new ResourceLocation(MysticalMechanics.MODID,"creative_mech_source"));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(IRON_AXLE).setRegistryName(IRON_AXLE.getRegistryName()));
        event.getRegistry().register(new ItemBlock(GEARBOX_FRAME).setRegistryName(GEARBOX_FRAME.getRegistryName()));
        event.getRegistry().register(new ItemBlock(MERGEBOX_FRAME).setRegistryName(MERGEBOX_FRAME.getRegistryName()));
        event.getRegistry().register(new ItemBlock(CREATIVE_MECH_SOURCE).setRegistryName(CREATIVE_MECH_SOURCE.getRegistryName()));

        event.getRegistry().register(IRON_GEAR = new Item().setRegistryName(MysticalMechanics.MODID, "gear_iron").setUnlocalizedName("gear_iron").setCreativeTab(MysticalMechanics.creativeTab));
        event.getRegistry().register(GOLD_GEAR = new Item().setRegistryName(MysticalMechanics.MODID, "gear_gold").setUnlocalizedName("gear_gold").setCreativeTab(MysticalMechanics.creativeTab));
        event.getRegistry().register(GOLD_GEAR_ON = new Item().setRegistryName(MysticalMechanics.MODID, "gear_gold_on").setUnlocalizedName("gear_gold_on").setCreativeTab(MysticalMechanics.creativeTab));
        event.getRegistry().register(GOLD_GEAR_OFF = new Item().setRegistryName(MysticalMechanics.MODID, "gear_gold_off").setUnlocalizedName("gear_gold_off").setCreativeTab(MysticalMechanics.creativeTab));
        event.getRegistry().register(FAN = new Item().setRegistryName(MysticalMechanics.MODID, "gear_fan").setUnlocalizedName("gear_fan").setCreativeTab(MysticalMechanics.creativeTab));

        OreDictionary.registerOre("gearIron", IRON_GEAR);
        OreDictionary.registerOre("gearGold", GOLD_GEAR);
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(registerSound("mysticalmechanics:block.gear.add"));
        event.getRegistry().register(registerSound("mysticalmechanics:block.gear.remove"));

        event.getRegistry().register(registerSound("mysticalmechanics:block.gearbox.fast.lv1"));
        event.getRegistry().register(registerSound("mysticalmechanics:block.gearbox.fast.lv2"));
        event.getRegistry().register(registerSound("mysticalmechanics:block.gearbox.fast.lv3"));
        event.getRegistry().register(registerSound("mysticalmechanics:block.gearbox.mid.lv1"));
        event.getRegistry().register(registerSound("mysticalmechanics:block.gearbox.mid.lv2"));
        event.getRegistry().register(registerSound("mysticalmechanics:block.gearbox.mid.lv3"));
        event.getRegistry().register(registerSound("mysticalmechanics:block.gearbox.slow.lv1"));
        event.getRegistry().register(registerSound("mysticalmechanics:block.gearbox.slow.lv2"));
        event.getRegistry().register(registerSound("mysticalmechanics:block.gearbox.slow.lv3"));
        event.getRegistry().register(registerSound("mysticalmechanics:block.gearbox.very_slow.lv1"));
        event.getRegistry().register(registerSound("mysticalmechanics:block.gearbox.very_slow.lv2"));
        event.getRegistry().register(registerSound("mysticalmechanics:block.gearbox.very_slow.lv3"));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        registerItemModel(Item.getItemFromBlock(IRON_AXLE), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(IRON_AXLE), 1, "normal");
        registerItemModel(Item.getItemFromBlock(GEARBOX_FRAME), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(MERGEBOX_FRAME), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(CREATIVE_MECH_SOURCE), 0, "inventory");

        registerItemModel(IRON_GEAR, 0, "inventory");
        registerItemModel(GOLD_GEAR, 0, "inventory");
        registerItemModel(GOLD_GEAR_OFF, 0, "inventory");
        registerItemModel(GOLD_GEAR_ON, 0, "inventory");
        registerItemModel(FAN, 0, "inventory");

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAxle.class, new TileEntityAxleRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGearbox.class, new TileEntityGearboxRenderer());
    }

    @SideOnly(Side.CLIENT)
    public void registerItemModel(@Nonnull Item item, int meta, String variant) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), variant));
    }

    public static SoundEvent registerSound(String soundName) {
        ResourceLocation soundID = new ResourceLocation(soundName);
        return new SoundEvent(soundID).setRegistryName(soundID);
    }
}
