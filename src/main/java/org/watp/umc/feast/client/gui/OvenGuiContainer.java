package org.watp.umc.feast.client.gui;

import org.watp.umc.feast.Feast;
import org.watp.umc.feast.inventory.OvenContainer;
import org.watp.umc.feast.network.NetWorking;
import org.watp.umc.feast.network.PacketOvenOpenSync;
import org.watp.umc.feast.tileentity.OvenTileEntity.VisibleIntValueType;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class OvenGuiContainer extends ContainerScreen<OvenContainer> {
	
	private static final ResourceLocation TEXTURE=new ResourceLocation(Feast.MODID+":textures/gui/container/oven_table.png");
	
	public OvenGuiContainer(OvenContainer container,PlayerInventory pi,ITextComponent title) {
		super(container,pi,title);
	}
	
	/**
	 * func_name: drawScreen(MatrixStack,int,int,float)</br>
	 */
	@Override
	public void func_230430_a_(MatrixStack mStack, int mouseX, int mouseY, float partial) {
		this.func_231160_c_();		//renderBackground() ???
		super.func_230430_a_(mStack, mouseX, mouseY, partial);
		this.func_230459_a_(mStack, mouseX, mouseY);		//renderHoveredToolTip(MatrixStack,int,int)
	}
	
	/**
	 * func_name: drawGuiContainerBackgroundLayer(MatrixStack,float,int,int)</br>
	 * field_230708_k_: width(int)</br>
	 * field_230709_l_: height(int)</br>
	 * func_238474_b_: AbstractGui.blit</br>
	 */
	@Override
	protected void func_230450_a_(MatrixStack mStack, float partial, int mouseX, int moustY) {
		this.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		int left=(this.field_230708_k_-this.xSize)/2;	//(width-xSize)/2
		int top=(this.field_230709_l_-this.ySize)/2;	//(height-ySize)/2
		this.func_238474_b_(mStack, left, top, 0, 0, this.xSize, this.ySize);	//call AbstractGui.blit
		int barHeight=23;
		int barWidth=Math.round(this.container.getIntVisibleValue(VisibleIntValueType.PROGRESS)*0.12f);
		this.func_238474_b_(mStack,left+81,top+27,0,166,barWidth,barHeight);
		barHeight=Math.round(this.container.getIntVisibleValue(VisibleIntValueType.REMAINING_ENERGY)*0.0006875f);
		barWidth=2;
		this.func_238474_b_(mStack,left+91,top+14-(barHeight-11),178,2,barWidth,barHeight);
		barHeight=Math.round(this.container.getIntVisibleValue(VisibleIntValueType.TEMPERATURE)*0.0017187f);
		barWidth=2;
		if (barHeight>7) this.func_238474_b_(mStack,left+99,top+14-(barHeight-11),178,15,barWidth,barHeight);
		else this.func_238474_b_(mStack,left+99,top+14-(barHeight-11),183,15,barWidth,barHeight);
		barHeight=Math.round(this.container.getIntVisibleValue(VisibleIntValueType.REMAINING_COOLING)*0.0006875f);
		barWidth=2;
		if (barHeight>7) this.func_238474_b_(mStack,left+107,top+14-(barHeight-11),178,28,barWidth,barHeight);
		else this.func_238474_b_(mStack,left+107,top+14-(barHeight-11),183,28,barWidth,barHeight);
		if (this.container.getOpen()==1) this.func_238474_b_(mStack,left+158,top+9,50,167,4,9);
	}
	
	/**
	 * 
	 * func_name: mouseClicked(double,double,int)</br>
	 * 
	 */
	@Override
	public boolean func_231044_a_(double posX, double posY, int key) {
		int left=(this.field_230708_k_-this.xSize)/2;
		int top=(this.field_230709_l_-this.ySize)/2;
		if (posX>=left+158 && posX<=left+162 && posY>=top+9 && posY<=top+18) {
			int open=this.container.getOpen();
			open=open==0?1:0;
			NetWorking.INSTANCE.sendToServer(new PacketOvenOpenSync(this.container.getTileEntity().getPos(),open));
		}
		return super.func_231044_a_(posX,posY,key);
	}
}
