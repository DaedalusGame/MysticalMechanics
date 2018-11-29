package mysticalmechanics;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.IGearBehavior;
import mysticalmechanics.api.IMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.apiimpl.MysticalMechanicsAPIImpl;
import mysticalmechanics.handler.RegistryHandler;
import mysticalmechanics.tileentity.TileEntityAxle;
import mysticalmechanics.tileentity.TileEntityCreativeMechSource;
import mysticalmechanics.tileentity.TileEntityGearbox;
import mysticalmechanics.tileentity.TileEntityMergebox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreIngredient;

import javax.annotation.Nullable;

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

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit();

        MysticalMechanicsAPI.IMPL = new MysticalMechanicsAPIImpl();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new RegistryHandler());

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

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
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
    }
}
