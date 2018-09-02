package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBoundScroll extends Item implements IResetUseOnDamage {

    public static final String name = "bound_scroll";
    public static final ResourceLocation registryName = new ResourceLocation(Waystones.MOD_ID, name);

    public ItemBoundScroll() {
        setCreativeTab(Waystones.creativeTab);
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

    private void setBoundTo(ItemStack itemStack, @Nullable WaystoneEntry entry) {
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
            itemStack.setTagCompound(tagCompound);
        }

        if (entry != null) {
            tagCompound.setTag("WaystonesBoundTo", entry.writeToNBT());
        } else {
            tagCompound.removeTag("WaystonesBoundTo");
        }
    }

    @Nullable
    protected WaystoneEntry getBoundTo(EntityPlayer player, ItemStack itemStack) {
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            return WaystoneEntry.read(tagCompound.getCompoundTag("WaystonesBoundTo"));
        }

        return null;
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack heldItem = player.getHeldItem(hand);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileWaystone) {
            TileWaystone tileWaystone = ((TileWaystone) tileEntity).getParent();
            ((BlockWaystone) Waystones.blockWaystone).activateWaystone(player, world, tileWaystone);

            if (!world.isRemote) {
                setBoundTo(heldItem, new WaystoneEntry(tileWaystone));
                player.sendStatusMessage(new TextComponentTranslation("waystones:scrollBound", tileWaystone.getWaystoneName()), true);
            }

            Waystones.proxy.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, pos, 2f);

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityLivingBase entity) {
        if (!world.isRemote && entity instanceof EntityPlayer) {
            WaystoneEntry boundTo = getBoundTo((EntityPlayer) entity, itemStack);
            if (boundTo != null) {
                double distance = entity.getDistance(boundTo.getPos().getX(), boundTo.getPos().getY(), boundTo.getPos().getZ());
                if (distance <= 2.0) {
                    return itemStack;
                }

                if (WaystoneManager.teleportToWaystone((EntityPlayer) entity, boundTo)) {
                    if (!((EntityPlayer) entity).capabilities.isCreativeMode) {
                        itemStack.shrink(1);
                    }
                }
            }
        }

        return itemStack;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        WaystoneEntry boundTo = getBoundTo(player, itemStack);
        if (boundTo != null) {
            if (!player.isHandActive() && world.isRemote) {
                Waystones.proxy.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, new BlockPos(player.posX, player.posY, player.posZ), 2f);
            }

            if (Waystones.proxy.isVivecraftInstalled()) {
                onItemUseFinish(itemStack, world, player);
            } else {
                player.setActiveHand(hand);
            }

            return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
        } else {
            player.sendStatusMessage(new TextComponentTranslation("waystones:scrollNotBound"), true);
            return new ActionResult<>(EnumActionResult.FAIL, itemStack);
        }

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) {
            return;
        }

        WaystoneEntry lastEntry = getBoundTo(player, itemStack);
        if (lastEntry != null) {
            tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", TextFormatting.DARK_AQUA + lastEntry.getName()));
        } else {
            tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", I18n.format("tooltip.waystones:none")));
        }
    }

}
