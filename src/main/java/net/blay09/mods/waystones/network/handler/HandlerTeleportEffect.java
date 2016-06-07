package net.blay09.mods.waystones.network.handler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.network.message.MessageTeleportEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class HandlerTeleportEffect implements IMessageHandler<MessageTeleportEffect, IMessage> {
	@Override
	public IMessage onMessage(final MessageTeleportEffect message, MessageContext ctx) {
		Waystones.proxy.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				Minecraft mc = Minecraft.getMinecraft();
				mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("portal.travel"), 1f));
				for (int i = 0; i < 128; i++) {
					mc.theWorld.spawnParticle("portal", message.getPos().getX() + (mc.theWorld.rand.nextDouble() - 0.5) * 3, message.getPos().getY() + mc.theWorld.rand.nextDouble() * 3, message.getPos().getZ() + (mc.theWorld.rand.nextDouble() - 0.5) * 3, (mc.theWorld.rand.nextDouble() - 0.5) * 2, -mc.theWorld.rand.nextDouble(), (mc.theWorld.rand.nextDouble() - 0.5) * 2);
				}
			}
		});
		return null;
	}
}
