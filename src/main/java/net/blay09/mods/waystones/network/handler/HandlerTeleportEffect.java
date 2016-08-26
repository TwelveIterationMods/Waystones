package net.blay09.mods.waystones.network.handler;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.network.message.MessageTeleportEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerTeleportEffect implements IMessageHandler<MessageTeleportEffect, IMessage> {
	@Override
	public IMessage onMessage(final MessageTeleportEffect message, MessageContext ctx) {
		Waystones.proxy.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				Minecraft mc = Minecraft.getMinecraft();
				mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_PORTAL_TRAVEL, 1f));
				for (int i = 0; i < 128; i++) {
					mc.theWorld.spawnParticle(EnumParticleTypes.PORTAL, message.getPos().getX() + (mc.theWorld.rand.nextDouble() - 0.5) * 3, message.getPos().getY() + mc.theWorld.rand.nextDouble() * 3, message.getPos().getZ() + (mc.theWorld.rand.nextDouble() - 0.5) * 3, (mc.theWorld.rand.nextDouble() - 0.5) * 2, -mc.theWorld.rand.nextDouble(), (mc.theWorld.rand.nextDouble() - 0.5) * 2);
				}
			}
		});
		return null;
	}
}
