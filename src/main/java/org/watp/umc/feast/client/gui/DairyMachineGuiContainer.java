package org.watp.umc.feast.client.gui;

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
	public void render(MatrixStack mStack, int mouseX, int mouseY, float partial) {
		this.init();
		super.render(mStack, mouseX, mouseY, partial);
		this.renderHoveredTooltip(mStack, mouseX, mouseY);
	}
	
	/**
	 * drawGuiContainerBackgroundLayer(MatrixStack,float,int,int)</br>
	 * field_230708_k_: width(int)</br>
	 * field_230709_l_: height(int)</br>
	 * func_238474_b_: AbstractGui.blit</br>
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack mStack, float partial, int mouseX, int moustY) {
		//GlStateManager.color4f(1.0f,1.0f,1.0f,1.0f);
		this.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		int left=(this.width-this.xSize)/2;	//(width-xSize)/2
		int top=(this.height-this.ySize)/2;	//(height-ySize)/2
		this.blit(mStack, left, top, 0, 0, this.xSize, this.ySize);	//call AbstractGui.blit
		int barHeight=15;
		int barWidth=this.container.getTileEntity().getProgressVisible();
		this.blit(mStack,left+66,top+33,0,166,barWidth,barHeight);
	}
}
