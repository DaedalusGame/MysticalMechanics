package mysticalmechanics.block;

import mysticalmechanics.tileentity.TileEntityMergebox;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockMergebox extends BlockGearbox {
    public BlockMergebox(Material material) {
        super(material);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityMergebox();
    }
}

