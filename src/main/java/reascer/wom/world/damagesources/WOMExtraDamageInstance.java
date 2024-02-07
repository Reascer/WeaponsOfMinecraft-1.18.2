package reascer.wom.world.damagesources;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;

public class WOMExtraDamageInstance extends ExtraDamageInstance {

	public WOMExtraDamageInstance(ExtraDamage calculator, float[] params) {
		super(calculator, params);
	}
	
	public static final ExtraDamage WOM_TARGET_MAX_HEALTH = new ExtraDamage((attacker, itemstack, target, baseDamage, params) -> {
		return target.getMaxHealth() * (float)params[0];
	}, (itemstack, tooltips, baseDamage, params) -> {
		tooltips.append(new TranslatableComponent("damage.wom.target_max_health", 
				new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(params[0] * 100F) + "%").withStyle(ChatFormatting.RED)
			).withStyle(ChatFormatting.DARK_GRAY));
	});
	
	public static final ExtraDamage WOM_TARGET_CURRENT_HEALTH = new ExtraDamage((attacker, itemstack, target, baseDamage, params) -> {
		return target.getHealth() * (float)params[0];
	}, (itemstack, tooltips, baseDamage, params) -> {
		tooltips.append(new TranslatableComponent("damage.wom.target_current_health", 
				new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(params[0] * 100F) + "%").withStyle(ChatFormatting.RED)
			).withStyle(ChatFormatting.DARK_GRAY));
	});
	
	public static final ExtraDamage WOM_SWEEPING_EDGE_ENCHANTMENT = new ExtraDamage((attacker, itemstack, target, baseDamage, params) -> {
		int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SWEEPING_EDGE, itemstack);
		float modifier = (i > 0) ? (float)i / (float)(i + 1.0F) : 0.0F;
		
		return baseDamage * modifier * (float)params[0];
	}, (itemstack, tooltips, baseDamage, params) -> {
		int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SWEEPING_EDGE, itemstack);
		
		if (i > 0) {
			double modifier = (double)i / (double)(i + 1.0D);
			double damage = baseDamage * modifier * (float)params[0];
			
			tooltips.append(new TranslatableComponent("damage.wom.sweeping_edge_enchant", 
						new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(damage)).withStyle(ChatFormatting.DARK_PURPLE),
					i).withStyle(ChatFormatting.DARK_GRAY));
		}
	});

}
