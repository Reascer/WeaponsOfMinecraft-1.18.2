package reascer.wom.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.main.WeaponsOfMinecraft;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.item.EpicFightItemTier;
import yesman.epicfight.world.item.WeaponItem;

public class SolarItem extends WeaponItem {
	protected static final UUID MOVEMENT_SPEED_MODIFIER = UUID.fromString("16295ED8-B092-4A75-9A94-BCD8D56668BB");
	@OnlyIn(Dist.CLIENT)
	private List<Component> tooltipExpand;
	private float attackDamage;
	private double attackSpeed;

	public SolarItem(Item.Properties build) {
		super(EpicFightItemTier.UCHIGATANA, 0, -2.2F, build);
		if (EpicFightMod.isPhysicalClient()) {
			this.tooltipExpand = new ArrayList<Component>();
			this.tooltipExpand.add(new TextComponent(""));
			this.tooltipExpand.add(new TranslatableComponent("item." + WeaponsOfMinecraft.MODID + ".solar.tooltip"));
		}
		this.attackDamage = 11.0F;
		this.attackSpeed = -2.9F;
	}
	
	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
		return repair.getItem() == Items.IRON_INGOT;
	}
    
	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		for (Component txtComp : tooltipExpand) {
			tooltip.add(txtComp);
		}
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		if (slot == EquipmentSlot.MAINHAND) {
    		Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
    		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.attackDamage, Operation.ADDITION));
    		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", this.attackSpeed, Operation.ADDITION));
            builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(MOVEMENT_SPEED_MODIFIER, "Weapon modifier", -0.02D, Operation.ADDITION));
    	    return builder.build();
        }
        
        return super.getAttributeModifiers(slot, stack);
    }
}