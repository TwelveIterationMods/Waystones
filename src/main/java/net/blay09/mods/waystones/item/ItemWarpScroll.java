package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWarpScroll extends Item implements IResetUseOnDamage {

    private static final String NBT_WARP_SCROLL_TARGET = "WarpScrollTarget";

    public static final String name = "warp_scroll";
    public static final ResourceLocation registryName = new ResourceLocation(Waystones.MOD_ID, name);

    public ItemWarpScroll() {
        setCreativeTab(Waystones.creativeTab);
        setRegistryName(name);
        setUnlocalizedName(registryName.toString());
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemStack) {
        return WaystoneConfig.general.warpScrollUseTime;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack itemStack) {
        if (Waystones.proxy.isVivecraftInstalled()) {
            return EnumAction.NONE;
        }

        return EnumAction.BOW;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null && compound.hasKey(NBT_WARP_SCROLL_TARGET, Constants.NBT.TAG_LIST)) {
            return "item.waystones:warp_scroll_bound";
        }

        return super.getUnlocalizedName(stack);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityLivingBase entityLiving) {
        NBTTagCompound compound = itemStack.getTagCompound();
        if (compound != null && compound.hasKey(NBT_WARP_SCROLL_TARGET, Constants.NBT.TAG_LIST)) {
            if (!world.isRemote && entityLiving instanceof EntityPlayer) {
                NBTTagList tagList = compound.getTagList(NBT_WARP_SCROLL_TARGET, Constants.NBT.TAG_INT);
                int x = tagList.tagCount() > 0 ? ((NBTTagInt) tagList.get(0)).getInt() : (int) entityLiving.posX;
                int y = tagList.tagCount() > 1 ? ((NBTTagInt) tagList.get(1)).getInt() : (int) entityLiving.posY;
                int z = tagList.tagCount() > 2 ? ((NBTTagInt) tagList.get(2)).getInt() : (int) entityLiving.posZ;
                int dimension = tagList.tagCount() > 3 ? ((NBTTagInt) tagList.get(3)).getInt() : entityLiving.getEntityWorld().provider.getDimension();
                WaystoneManager.teleportToPosition((EntityPlayer) entityLiving, world, new BlockPos(x, y, z), entityLiving.getHorizontalFacing(), dimension);
                if (!((EntityPlayer) entityLiving).capabilities.isCreativeMode) {
                    itemStack.shrink(1);
                }
            }
        } else if (world.isRemote && entityLiving instanceof EntityPlayer) {
            Waystones.proxy.openWaystoneSelection((EntityPlayer) entityLiving, WarpMode.WARP_SCROLL, entityLiving.getActiveHand(), null);
        }

        return itemStack;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (!player.isHandActive() && world.isRemote) {
            Waystones.proxy.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, new BlockPos(player.posX, player.posY, player.posZ), 2f);
        }
        if (Waystones.proxy.isVivecraftInstalled()) {
            onItemUseFinish(itemStack, world, player);
        } else {
            player.setActiveHand(hand);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack itemStack) {
        return true;
    }


}
