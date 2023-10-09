package reascer.wom.world.item;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.main.WeaponsOfMinecraft;
import yesman.epicfight.main.EpicFightMod;

public class DemonSealItem extends Item {
	@OnlyIn(Dist.CLIENT)
	private List<Component> tooltipExpand;
	
	public DemonSealItem(Properties properties) {
		super(properties);
		if (EpicFightMod.isPhysicalClient()) {
			this.tooltipExpand = new ArrayList<Component> ();
			this.tooltipExpand.add(Component.literal(""));
			this.tooltipExpand.add(Component.translatable("item." + WeaponsOfMinecraft.MODID + ".demon_seal.tooltip"));
		}
	}
	
	
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		for (Component txtComp : tooltipExpand) {
			tooltip.add(txtComp);
		}
	}
}