package reascer.wom.skill.weaponinnate;

import java.util.List;
import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.skill.WOMSkillDataKeys;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.SkillConsumeEvent;


public class AgonyPlungeSkill extends WeaponInnateSkill {
	private static final UUID EVENT_UUID = UUID.fromString("c7a0ee46-56b3-4008-9fba-d2594b1e2676");
	
	public AgonyPlungeSkill(Builder<?> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getExecuter().getEventListener().addEventListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID, (event) -> {
			//container.getExecuter().getOriginal().sendMessage(new TextComponent("Plunging: " + container.getDataManager().getDataValue(WOMSkillDataKeys.PLUNGING.get()) + " | Stack: " + container.getDataManager().getDataValue(WOMSkillDataKeys.STACK.get()) + " | Plunging: " + event.getAttackDamage() ), UUID.randomUUID());
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.PLUNGING.get()) && event.getDamage() > 1.0F && container.getDataManager().getDataValue(WOMSkillDataKeys.STACK.get()) > 0) {
				float attackDamage = event.getDamage();
				event.setDamage(attackDamage * container.getDataManager().getDataValue(WOMSkillDataKeys.STACK.get()));
				//container.getExecuter().getOriginal().sendMessage(new TextComponent("Plunge attack damge: " + (attackDamage * container.getDataManager().getDataValue(WOMSkillDataKeys.STACK.get()))), UUID.randomUUID());
				container.getExecuter().getOriginal().resetFallDistance();
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.PLUNGING.get())) {
				container.getDataManager().setData(WOMSkillDataKeys.PLUNGING.get(), false);
				container.getDataManager().setData(WOMSkillDataKeys.STACK.get(), 0);
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.PLUNGING.get())) {
				container.getDataManager().setData(WOMSkillDataKeys.PLUNGING.get(), false);
				container.getDataManager().setData(WOMSkillDataKeys.STACK.get(), 0);
			}
		});
		
		
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID);
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		if (executer.getSkill(EpicFightSkills.HYPERVITALITY) == null) {
			executer.playAnimationSynchronized(WOMAnimations.AGONY_PLUNGE_FORWARD, 0);
		} else {
			executer.playAnimationSynchronized(WOMAnimations.AGONY_PLUNGE_FORWARD_X, 0);
		}
		executer.getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.PLUNGING.get(), true, executer.getOriginal());
		if (executer.getSkill(EpicFightSkills.HYPERVITALITY) == null) {
			executer.getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.STACK.get(), executer.getSkill(this).getStack(), executer.getOriginal());
		} else {
			executer.getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.STACK.get(), 1, executer.getOriginal());
			
		}
		//executer.getOriginal().sendMessage(new TextComponent("number of stack: " + executer.getSkill(this).getDataManager().getDataValue(WOMSkillDataKeys.STACK.get())), UUID.randomUUID());
		//executer.setStamina(executer.getStamina() - (5f - EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal())));
		if (!executer.getOriginal().isCreative()) {
			SkillConsumeEvent event = new SkillConsumeEvent(executer, this, this.resource, true);
			executer.getEventListener().triggerEvents(EventType.SKILL_CONSUME_EVENT, event);
			
			if (!event.isCanceled()) {
				event.getResourceType().consumer.consume(this, executer, event.getAmount());
			}
			executer.getOriginal().level().playSound(null, executer.getOriginal().xo, executer.getOriginal().yo, executer.getOriginal().zo,
	    			SoundEvents.PLAYER_HURT, executer.getOriginal().getSoundSource(), 1.0F, 1.0F);
			EpicFightDamageSource damage = executer.getDamageSource(WOMAnimations.AGONY_PLUNGE_FORWARD, InteractionHand.MAIN_HAND);
			damage.setStunType(StunType.NONE);
			executer.getOriginal().hurt(damage, executer.getOriginal().getHealth() * (0.40f - (0.10f * EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal()))));
		}

		if (executer.getSkill(EpicFightSkills.HYPERVITALITY) == null && !executer.getOriginal().isCreative()) {
			this.setStackSynchronize(executer, 0);
		}
		executer.getSkill(this).activate();
	}
	
	@Override
	public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
		List<Component> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Jump :");
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(1), "Plunge :");
		
		return list;
	}
	
	@Override
	public WeaponInnateSkill registerPropertiesToAnimation() {
		return this;
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		super.updateContainer(container);
		if (container.getDataManager().getDataValue(WOMSkillDataKeys.PLUNGING.get())) {
			container.getExecuter().getOriginal().addEffect(new MobEffectInstance(EpicFightMobEffects.STUN_IMMUNITY.get(), 5, 0,true,false,false));
			container.getExecuter().getOriginal().resetFallDistance();
		}
	}
}