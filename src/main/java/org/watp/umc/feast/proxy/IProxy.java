package org.watp.umc.feast.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface IProxy {
	void init();
	World getClientWorld();
	PlayerEntity getClientPlayer();
}
