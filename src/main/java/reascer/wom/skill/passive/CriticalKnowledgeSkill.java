package reascer.wom.skill.passive;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class CriticalKnowledgeSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("1d807798-1a3f-11ed-861d-0242ac120002");
	private float critDamage;
	private float critRate;
	
	public CriticalKnowledgeSkill(Builder<? extends Skill> builder) {
		super(builder);
		critRate = 20f;
		critDamage = 60f;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
			int fire = 0;
			int blast = 0;
			for (ItemStack ArmorPiece : container.getExecuter().getOriginal().getArmorSlots()) {
				fire += ArmorPiece.getEnchantmentLevel(Enchantments.FIRE_PROTECTION);
				blast += ArmorPiece.getEnchantmentLevel(Enchantments.BLAST_PROTECTION);
			}
			critRate = ((fire/16f)*80f)+20f;
			critDamage = ( 1 + (0.15f * blast)) * 100;
					
			int chance = Math.abs(new Random().nextInt()) % 100;
			//container.getExecuter().getOriginal().sendMessage(new TextComponent("chance: " + chance + " | thorn: " + thorn + " | Critchance: " + (((thorn/12f)*90f)+5f)), UUID.randomUUID());
			if (chance < critRate) {
				event.getTarget().hurt((DamageSource) event.getDamageSource(), event.getAttackDamage() * ( 1 + (0.15f * blast)));
				if(!event.getPlayerPatch().isLogicalClient()) {
					ServerPlayerPatch executer = (ServerPlayerPatch) event.getPlayerPatch();
					event.getTarget().playSound(SoundEvents.FIREWORK_ROCKET_BLAST, 1.5f, 0.5f);
					event.getTarget().playSound(SoundEvents.FIREWORK_ROCKET_TWINKLE, 1.5f, 2.0f);
					((ServerLevel) executer.getOriginal().level).sendParticles( ParticleTypes.CRIT,
							event.getTarget().getX(), 
							event.getTarget().getY() + 1.2D, 
							event.getTarget().getZ(), 
							30, 0.1D, 0.1D, 0.1D, 1.0D);
				}
			}
        });
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<Object> getTooltipArgsOfScreen(List<Object> list) {
		list.add(String.format("%.0f", this.critRate));
		list.add(String.format("%.0f", this.critDamage));
		return list;
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		super.updateContainer(container);
		int fire = 0;
		int blast = 0;
		for (ItemStack ArmorPiece : container.getExecuter().getOriginal().getArmorSlots()) {
			fire += ArmorPiece.getEnchantmentLevel(Enchantments.FIRE_PROTECTION);
			blast += ArmorPiece.getEnchantmentLevel(Enchantments.BLAST_PROTECTION);
		}
		critRate = ((fire/16f)*80f)+20f;
		critDamage = ( 1 + (0.15f * blast)) * 100;
	}
}