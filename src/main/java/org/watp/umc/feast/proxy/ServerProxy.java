package org.watp.umc.feast.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ServerProxy implements IProxy {
	@Override
	public void init() {
	}
	
	@Override
	public World getClientWorld() {
		throw new IllegalStateException("can not run on server");
	}
	
	@Override
	public PlayerEntity getClientPlayer() {
		throw new IllegalStateException("can not run on server");
	}
}
