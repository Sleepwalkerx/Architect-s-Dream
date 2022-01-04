package sleepwalker.architectsdream.items;

import net.minecraft.item.Item;

public class ItemDarkDust extends Item {

	public ItemDarkDust(Item.Properties properties){
        super(properties);
	}

    /*@Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
            EnumFacing facing, float hitX, float hitY, float hitZ) {
    	if (!worldIn.isRemote) {
             Structure structure = StructureUtils.findMatchingStructure(pos, worldIn);
            if(structure != null){
                //player.getHeldItem(hand).shrink(1);
                player.sendStatusMessage(new TextComponentTranslation("j12.multiblock.formed.success").setStyle(new Style().setColor(TextFormatting.GREEN)), false);
                //return EnumActionResult.SUCCESS;
            } else {
                player.sendStatusMessage(new TextComponentTranslation("j12.multiblock.formed.failure").setStyle(new Style().setColor(TextFormatting.RED).setBold(true)), false);
                //return EnumActionResult.PASS;
            }
            // SuperchestPartIndex formed = worldIn.getBlockState(pos).getValue(BlockSuperchest.FORMED);
            // if (formed == SuperchestPartIndex.UNFORMED) {
            //     if (MultiBlockTools.formMultiblock(SuperchestMultiBlock.INSTANCE, worldIn, pos)) {
            //         player.sendStatusMessage(new TextComponentTranslation("j12.multiblock.formed.success").setStyle(new Style().setColor(TextFormatting.GREEN)), true);
            //     } else {
            //         player.sendStatusMessage(new TextComponentTranslation("j12.multiblock.formed.failure").setStyle(new Style().setColor(TextFormatting.RED).setBold(true)), false);
            //     }
            // } else {
            //     if (!MultiBlockTools.breakMultiblock(SuperchestMultiBlock.INSTANCE, worldIn, pos)) {
            //         player.sendStatusMessage(new TextComponentTranslation("j12.multiblock.unformed.failure").setStyle(new Style().setColor(TextFormatting.RED).setBold(true)), false);
            //     }
            // }
        }
        return EnumActionResult.PASS;
    }*/
}
