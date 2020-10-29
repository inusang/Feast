package org.watp.umc.feast.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.watp.umc.feast.Feast;
import org.watp.umc.feast.inventory.DairyMachineContainer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class DairyMachineGuiContainer extends ContainerScreen<DairyMachineContainer> {
	private static final ResourceLocation TEXTURE=new ResourceLocation(Feast.MODID+":textures/gui/container/dairy_machine_table.png");
	
	public DairyMachineGuiContainer(DairyMachineContainer container, PlayerInventory pi, ITextComponent title) {
		super(container,pi,title);
	}

	@Override
	protected void init() {
		super.init();
		int left=(this.width-this.xSize)/2;
		int top=(this.height-this.ySize)/2;
		ProduceModeButton creamButton=new ProduceModeButton(left+57, top+36, 45 ,166);
		ProduceModeButton butterButton=new ProduceModeButton(left+79, top+36, 63, 166);
		ProduceModeButton cheeseButton=new ProduceModeButton(left+101, top+36, 81, 166);
		this.addButton(creamButton);
		this.addButton(butterButton);
		this.addButton(cheeseButton);
	}

	@Override
	public void render(MatrixStack mStack, int mouseX, int mouseY, float partial) {
		this.init();
		super.render(mStack, mouseX, mouseY, partial);
		this.renderHoveredTooltip(mStack, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack mStack, float partial, int mouseX, int moustY) {
		this.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		int left=(this.width-this.xSize)/2;
		int top=(this.height-this.ySize)/2;
		this.blit(mStack, left, top, 0, 0, this.xSize, this.ySize);
		int barHeight=15;
		int barWidth=this.container.getTileEntity().getProgressVisible();
		this.blit(mStack,left+66,top+18,0, 166, barWidth, barHeight);
	}

	@OnlyIn(Dist.CLIENT)
	private static final class ProduceModeButton extends AbstractButton {
		private static ProduceModeButton selectedButton;
		private static boolean isSelectedNow;
		private int blitX;
		private int blitY;

		public ProduceModeButton(int x, int y, int blitX, int blitY) {
			super(x, y, 18, 18, StringTextComponent.EMPTY);
			this.blitX=blitX;
			this.blitY=blitY;
		}

		@Override
		public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
			Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
			this.blit(mStack, this.x, this.y, blitX, blitY, this.width, this.height);
		}

		@Override
		public void onClick(double p_230982_1_, double p_230982_3_) {
			super.onClick(p_230982_1_, p_230982_3_);
		}

		@Override
		public void onPress() {

		}
	}
}
