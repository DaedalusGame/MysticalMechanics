package mysticalmechanics;

import mysticalmechanics.api.*;
import mysticalmechanics.apiimpl.ConfigValue;
import mysticalmechanics.apiimpl.MysticalMechanicsAPIImpl;
import mysticalmechanics.compat.TheOneProbe;
import mysticalmechanics.handler.RegistryHandler;
import mysticalmechanics.handler.RightClickHandler;
import mysticalmechanics.tileentity.*;
import mysticalmechanics.util.FanBehavior;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreIngredient;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

@Mod(modid = MysticalMechanics.MODID, acceptedMinecraftVersions = "[1.12, 1.13)")
@Mod.EventBusSubscriber
public class MysticalMechanics
{
    public static final String MODID = "mysticalmechanics";
    public static final String NAME = "Mystical Mechanics";

    @SidedProxy(clientSide = "mysticalmechanics.ClientProxy",serverSide = "mysticalmechanics.ServerProxy")
    public static IProxy proxy;

    @Mod.Instance(MysticalMechanics.MODID)
    public static MysticalMechanics instance;

    public static CreativeTabs creativeTab;

    public static Configuration config;

    //UNITS
    public static String FORCE_UNIT;

    //RENDER
    public static boolean RENDER_GEAR_HOLOGRAM;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        creativeTab = new CreativeTabs("mysticalmechanics.name") {
            @Override
            public ItemStack getTabIconItem() {
                return new ItemStack(RegistryHandler.IRON_GEAR);
            }
        };

        if(config == null)
        {
            config = new Configuration(event.getSuggestedConfigurationFile());
            loadConfig();
        }

        if(Loader.isModLoaded("theoneprobe"))
            TheOneProbe.init();

        proxy.preInit();

        MysticalMechanicsAPIImpl impl = new MysticalMechanicsAPIImpl();
        impl.registerConfigValue(new ConfigValue<String>(String.class,"units.forceUnit") {
            @Override
            public String getValue() {
                return FORCE_UNIT;
            }

            @Override
            public void setValue(String value) {
                FORCE_UNIT = value;
            }
        });
        impl.registerConfigValue(new ConfigValue<Boolean>(Boolean.class,"render.renderGearHologram") {
            @Override
            public Boolean getValue() {
                return RENDER_GEAR_HOLOGRAM;
            }

            @Override
            public void setValue(Boolean value) {
                RENDER_GEAR_HOLOGRAM = value;
            }
        });
        MysticalMechanicsAPI.IMPL = impl;

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new RegistryHandler());
        MinecraftForge.EVENT_BUS.register(new RightClickHandler());

        CapabilityManager.INSTANCE.register(IMechCapability.class, new Capability.IStorage<IMechCapability>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IMechCapability> capability, IMechCapability instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IMechCapability> capability, IMechCapability instance, EnumFacing side, NBTBase nbt) {
                //NOOP
            }
        }, DefaultMechCapability::new);
    }

    private void loadConfig() {
        FORCE_UNIT = config.getString("forceUnit", "units", "", "Set this to a non-empty string to force a specific unit.");

        RENDER_GEAR_HOLOGRAM = config.getBoolean("renderGearHologram","render", true, "Whether gearboxes should render a hologram for inserted/extracted gears.");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        NumberFormat format = new DecimalFormat("0.##");
        MysticalMechanicsAPI.IMPL.registerUnit(new IMechUnit<Double>() {
            @Override
            public String getName() {
                return "default";
            }

            @Override
            public int getPriority() {
                return -999999;
            }

            @Override
            public double getZero() {
                return 0;
            }

            @Override
            public double getNeutral() {
                return 1;
            }

            @Override
            public double convertToPower(Double unit) {
                return unit;
            }

            @Override
            public Double convertToUnit(double power) {
                return power;
            }

            @Override
            @SideOnly(Side.CLIENT)
            public String format(double power) {
                return I18n.format("mysticalmechanics.unit.default",format.format(power));
            }
        });

        GameRegistry.registerTileEntity(TileEntityAxle.class,"mysticalmechanics:axle");
        GameRegistry.registerTileEntity(TileEntityGearbox.class,"mysticalmechanics:gearbox");
        GameRegistry.registerTileEntity(TileEntityMergebox.class,"mysticalmechanics:mergebox");
        GameRegistry.registerTileEntity(TileEntityCreativeMechSource.class,"mysticalmechanics:creative_mech_source");

        MysticalMechanicsAPI.IMPL.registerGear(new ResourceLocation(MODID,"gear_iron"), new OreIngredient("gearIron"), new IGearBehavior() {
            @Override
            public double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, double power) {
                return power;
            }

            @Override
            public void visualUpdate(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear) {
                //NOOP
            }
        });
        MysticalMechanicsAPI.IMPL.registerGear(new ResourceLocation(MODID,"gear_gold"), new OreIngredient("gearGold"), new IGearBehavior() {
            @Override
            public double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, double power) {
                return power;
            }

            @Override
            public void visualUpdate(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear) {
                //NOOP
            }
        });
        MysticalMechanicsAPI.IMPL.registerGear(new ResourceLocation(MODID,"gear_gold_on"), Ingredient.fromItem(RegistryHandler.GOLD_GEAR_ON), new IGearBehavior() {
            @Override
            public double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, double power) {
                boolean powered = tile.getWorld().isBlockPowered(tile.getPos());
                return !powered ? power : 0;
            }

            @Override
            public void visualUpdate(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear) {
                //NOOP
            }
        });
        MysticalMechanicsAPI.IMPL.registerGear(new ResourceLocation(MODID,"gear_gold_off"), Ingredient.fromItem(RegistryHandler.GOLD_GEAR_OFF), new IGearBehavior() {
            @Override
            public double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, double power) {
                boolean powered = tile.getWorld().isBlockPowered(tile.getPos());
                return powered ? power : 0;
            }

            @Override
            public void visualUpdate(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear) {
                //NOOP
            }
        });
        MysticalMechanicsAPI.IMPL.registerGear(new ResourceLocation(MODID,"gear_fan"), Ingredient.fromItem(RegistryHandler.FAN), new FanBehavior());
    }

}
