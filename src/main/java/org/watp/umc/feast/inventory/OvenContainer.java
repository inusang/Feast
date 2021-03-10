package org.watp.umc.feast.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.watp.umc.feast.Feast;
import org.watp.umc.feast.inventory.slot.ICheckedSlot;
import org.watp.umc.feast.inventory.slot.ProductionSlot;
import org.watp.umc.feast.tileentity.OvenTileEntity;
import org.watp.umc.feast.tileentity.OvenTileEntity.VisibleIntValueType;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class OvenContainer extends CommonInteractContainer {
	private OvenTileEntity te;
	
	@OnlyIn(Dist.CLIENT)
	public OvenContainer(int windowId, PlayerInventory pi, PacketBuffer extraData) {
		this(windowId, pi, (OvenTileEntity) Minecraft.getInstance().world.getTileEntity(extraData.readBlockPos()));
	}

	public OvenContainer(int windowId, PlayerInventory pi, OvenTileEntity te) {
		super(Feast.ContainerTypes.OVEN, windowId);
		this.te=te;
		this.bindPlayerInventory(pi);
		this.bindOtherSlots(te);
		this.trackVars();
	}

	
	public Integer getIntVisibleValue(VisibleIntValueType vt) {
		if (vt==VisibleIntValueType.REMAINING_ENERGY) return te.getIntVisibleValue(VisibleIntValueType.REMAINING_ENERGY);
		else if (vt==VisibleIntValueType.REMAINING_COOLING) return te.getIntVisibleValue(VisibleIntValueType.REMAINING_COOLING);
		else if (vt==VisibleIntValueType.PROGRESS) return te.getIntVisibleValue(VisibleIntValueType.PROGRESS);
		else if (vt==VisibleIntValueType.TEMPERATURE) return te.getIntVisibleValue(VisibleIntValueType.TEMPERATURE);
		else throw new IllegalArgumentException("Illegal value type");
	}
	
	public OvenTileEntity getTileEntity() {
		return this.te;
	}
	
	public int getOpen() {
		return te.getOpen();
	}
	
	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}
	
	@Override
	protected <T extends TileEntity> void bindOtherSlots(final T te) {
		Capability<IItemHandler> itemHandlerCap=CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
		IItemHandler materialItemStack=te.getCapability(itemHandlerCap,Direction.UP).orElse(null);
		for (int i=0;i<3;i++) {
			for (int j=0;j<3;j++) {
				this.addSlot(new OvenMaterialSlot(materialItemStack,j+3*i,22+j*18,17+i*18));
			}
		}
		IItemHandler fuelItemStack=te.getCapability(itemHandlerCap,Direction.NORTH).orElse(null);
		this.addSlot(new OvenFuelSlot(fuelItemStack,0,84,53));
		IItemHandler freezeItemStack=te.getCapability(itemHandlerCap,Direction.SOUTH).orElse(null);
		this.addSlot(new OvenFreezeSlot(freezeItemStack,0,108,53));
		IItemHandler productionItemStack=te.getCapability(itemHandlerCap,Direction.DOWN).orElse(null);
		this.addSlot(new ProductionSlot(productionItemStack,0,136,34));
	}
	
	@Override
	protected void trackVars() {
		this.trackInt(new IntReferenceHolder() {
			@Override
			public void set(int value) {
				te.setProgress(value);
			}
			
			@Override
			public int get() {
				return te.getIntVisibleValue(VisibleIntValueType.PROGRESS) & 0xffff;
			}
		});
		
		this.trackInt(new IntReferenceHolder() {
			@Override
			public void set(int value) {
				te.setRemainingEnergy(value);
			}
			
			@Override
			public int get() {
				return te.getIntVisibleValue(VisibleIntValueType.REMAINING_ENERGY) & 0xffff;
			}
		});
		
		this.trackInt(new IntReferenceHolder() {
			@Override
			public void set(int value) {
				te.setTemperature(value);
			};
			
			public int get() {
				return te.getIntVisibleValue(VisibleIntValueType.TEMPERATURE) & 0xffff;
			};
		});
		
		this.trackInt(new IntReferenceHolder() {
			@Override
			public void set(int value) {
				te.setRemainingCooling(value);
			}
			
			@Override
			public int get() {
				return te.getIntVisibleValue(VisibleIntValueType.REMAINING_COOLING) & 0xffff;
			}
		});
		
		this.trackInt(new IntReferenceHolder() {
			@Override
			public void set(int value) {
				te.setOpen(value);
			}
			
			@Override
			public int get() {
				return te.getOpen() & 0xffff;
			}
		});

	}

	private class OvenMaterialSlot extends SlotItemHandler {
		public OvenMaterialSlot(IItemHandler itemHandler,int index,int xPos,int yPos) {
			super(itemHandler,index,xPos,yPos);
		}
		
		@Override
		public boolean isItemValid(ItemStack stack) {
			return super.isItemValid(stack);
		}
	}
	
	private class OvenFuelSlot extends SlotItemHandler implements ICheckedSlot {
		public OvenFuelSlot(IItemHandler itemHandler,int index,int xPos,int yPos) {
			super(itemHandler,index,xPos,yPos);
		}
		
		@Override
		public boolean isItemValid(ItemStack stack) {
			return super.isItemValid(stack) && check(stack.getItem());
		}
		
		@Override
		public boolean check(Item item) {
			return item==Items.COAL || item==Items.COAL_BLOCK || item==Items.CHARCOAL;
		}
	}

	private class OvenFreezeSlot extends SlotItemHandler implements ICheckedSlot {
		public OvenFreezeSlot(IItemHandler itemHandler,int index,int xPos,int yPos) {
			super(itemHandler,index,xPos,yPos);
		}
		
		@Override
		public boolean isItemValid(ItemStack stack) {
			return super.isItemValid(stack) && check(stack.getItem());
		}
		
		@Override
		public boolean check(Item item) {
			return item==Items.ICE || item==Items.PACKED_ICE || item==Items.BLUE_ICE;
		}
	}

}
