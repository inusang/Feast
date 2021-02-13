package org.watp.umc.feast.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import org.watp.umc.feast.Feast;
import org.watp.umc.feast.inventory.DairyMachineContainer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.watp.umc.feast.network.NetWorking;
import org.watp.umc.feast.network.PacketDMProductionTargetC2S;
import org.watp.umc.feast.tileentity.DairyMachineTileEntity;

import java.util.List;
import java.util.stream.Collectors;

public class DairyMachineGuiContainer extends ContainerScreen<DairyMachineContainer> {
	private static final ResourceLocation TEXTURE=new ResourceLocation(Feast.MODID+":textures/gui/container/dairy_machine_table.png");
	private static final int buttonListX=45;
	private static final int buttonListY=166;
	private static final ProduceButtonPosition produceCreamButton=new ProduceButtonPosition(56, 51);
	private static final ProduceButtonPosition produceButterButton=new ProduceButtonPosition(79, 51);
	private static final ProduceButtonPosition produceCheeseButton=new ProduceButtonPosition(102, 51);

	public DairyMachineGuiContainer(DairyMachineContainer container, PlayerInventory pi, ITextComponent title) {
		super(container, pi, title);
	}

	@Override
	public void render(MatrixStack mStack, int mouseX, int mouseY, float partial) {
		this.init();
		super.render(mStack, mouseX, mouseY, partial);
		this.renderHoveredTooltip(mStack, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float partial, int mouseX, int moustY) {
		this.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		int left=(this.width-this.xSize)/2;
		int top=(this.height-this.ySize)/2;
		this.blit(stack, left, top, 0, 0, this.xSize, this.ySize);
		int barHeight=15;
		int barWidth=this.container.getTileEntity().getProgressVisible();
		this.blit(stack,left+66,top+28,0, 166, barWidth, barHeight);
		this.renderButtons(stack, left, top);
	}

	private void renderButtons(MatrixStack stack, int left,int top) {
		DairyMachineTileEntity te=this.container.getTileEntity();
		List<DairyMachineTileEntity.WorkMode> unselecteds= Lists.newArrayList(DairyMachineTileEntity.WorkMode.values());
		if (te.getProductionTarget()== Items.AIR) {
			blitButtonUnSelected(stack ,unselecteds, left, top);
		}
		else {
			unselecteds.remove(DairyMachineTileEntity.WorkMode.getByItem(te.getProductionTarget()));
			blitButtonSelected(stack, DairyMachineTileEntity.WorkMode.getByItem(te.getProductionTarget()), left, top);
			if (te.isOperable()) {
				blitButtonUnSelected(stack, unselecteds, left, top);
			}
			else {
				blitButtonCantSelected(stack, unselecteds, left, top);
			}
		}
	}

	private void blitButton(MatrixStack stack, DairyMachineTileEntity.WorkMode mode, int left, int top, int offsetY) {
		if (mode== DairyMachineTileEntity.WorkMode.CREAM){
			this.blit(stack, left+produceCreamButton.getX(), top+produceCreamButton.getY(), buttonListX, buttonListY+offsetY, 18, 18);
		}
		else if (mode== DairyMachineTileEntity.WorkMode.BUTTER){
			this.blit(stack, left+produceButterButton.getX(), top+produceButterButton.getY(), buttonListX+18, buttonListY+offsetY, 18, 18);
		}
		else if (mode== DairyMachineTileEntity.WorkMode.CHEESE) {
			this.blit(stack, left+produceCheeseButton.getX(), top+produceCheeseButton.getY(), buttonListX+36, buttonListY+offsetY, 18, 18);
		}
	}

	private void blitButtonSelected(MatrixStack stack, DairyMachineTileEntity.WorkMode mode, int left, int top){
		blitButton(stack, mode, left, top, 18);
	}

	private void blitButtonUnSelected(MatrixStack stack, List<DairyMachineTileEntity.WorkMode> modes, int left, int top) {
		modes.stream().map(mode -> {blitButton(stack, mode, left, top ,0); return mode;}).collect(Collectors.toList());
	}

	private void blitButtonCantSelected(MatrixStack stack, List<DairyMachineTileEntity.WorkMode> modes, int left, int top) {
		modes.stream().map(mode -> {blitButton(stack, mode, left, top, 36); return mode;}).collect(Collectors.toList());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		int left=(this.width-this.xSize)/2;
		int top=(this.height-this.ySize)/2;
		if (mouseX>left+produceCreamButton.getX() && mouseX<left+produceCreamButton.getX()+18 && mouseY>top+produceCreamButton.getY() && mouseY<top+produceCreamButton.getY()+18){
			Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			NetWorking.INSTANCE.sendToServer(new PacketDMProductionTargetC2S(this.container.getTileEntity().getPos(), DairyMachineTileEntity.WorkMode.CREAM.getProductionTarget().getRegistryName().toString()));
		}
		else if (mouseX>left+produceButterButton.getX() && mouseX<left+produceButterButton.getX()+18 && mouseY>top+produceButterButton.getY() && mouseY<top+produceButterButton.getY()+18){
			Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			NetWorking.INSTANCE.sendToServer(new PacketDMProductionTargetC2S(this.container.getTileEntity().getPos(), DairyMachineTileEntity.WorkMode.BUTTER.getProductionTarget().getRegistryName().toString()));
		}
		else if (mouseX>left+produceCheeseButton.getX() && mouseX<left+produceCheeseButton.getX()+18 && mouseY>top+produceCheeseButton.getY() && mouseY<top+produceCheeseButton.getY()+18){
			Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			NetWorking.INSTANCE.sendToServer(new PacketDMProductionTargetC2S(this.container.getTileEntity().getPos(), DairyMachineTileEntity.WorkMode.CHEESE.getProductionTarget().getRegistryName().toString()));
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	private static final class ProduceButtonPosition {
		private final int x;
		private final int y;

		public ProduceButtonPosition(int x, int y) {
			this.x=x;
			this.y=y;
		}

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}
	}
}
