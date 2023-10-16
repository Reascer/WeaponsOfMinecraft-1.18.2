package reascer.wom.skill;

import java.util.List;
import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import reascer.wom.gameasset.WOMAnimations;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;


public class AgonyPlungeSkill extends WeaponInnateSkill {
	private static final UUID EVENT_UUID = UUID.fromString("c7a0ee46-56b3-4008-9fba-d2594b1e2676");
	public static final SkillDataKey<Boolean> PLUNGING = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	public static final SkillDataKey<Integer> STACK = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	private StaticAnimation attackAnimations;
	
	public AgonyPlungeSkill(Builder builder) {
		super(builder);
		this.attackAnimations = WOMAnimations.AGONY_PLUNGE_FORWARD;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(PLUNGING);
		container.getDataManager().setData(PLUNGING, false);
		
		container.getDataManager().registerData(STACK);
		container.getDataManager().setData(STACK, 0);
		
		container.getExecuter().getEventListener().addEventListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID, (event) -> {
			//container.getExecuter().getOriginal().sendMessage(new TextComponent("Plunging: " + container.getDataManager().getDataValue(PLUNGING) + " | Stack: " + container.getDataManager().getDataValue(STACK) + " | Plunging: " + event.getAttackDamage() ), UUID.randomUUID());
			if (container.getDataManager().getDataValue(PLUNGING) && event.getDamage() > 1.0F && container.getDataManager().getDataValue(STACK) > 0) {
				float attackDamage = event.getDamage();
				event.setDamage(attackDamage * container.getDataManager().getDataValue(STACK));
				//container.getExecuter().getOriginal().sendMessage(new TextComponent("Plunge attack damge: " + (attackDamage * container.getDataManager().getDataValue(STACK))), UUID.randomUUID());
				container.getExecuter().getOriginal().resetFallDistance();
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(PLUNGING)) {
				container.getDataManager().setData(PLUNGING, false);
				container.getDataManager().setData(STACK, 0);
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(PLUNGING)) {
				container.getDataManager().setData(PLUNGING, false);
				container.getDataManager().setData(STACK, 0);
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
		executer.playAnimationSynchronized(this.attackAnimations, 0);
		executer.getSkill(this).getDataManager().setDataSync(PLUNGING, true, executer.getOriginal());
		if (executer.getSkill(EpicFightSkills.HYPERVITALITY) == null) {
			executer.getSkill(this).getDataManager().setDataSync(STACK, executer.getSkill(this).getStack(), executer.getOriginal());
		} else {
			executer.getSkill(this).getDataManager().setDataSync(STACK, 1, executer.getOriginal());
		}
		//executer.getOriginal().sendMessage(new TextComponent("number of stack: " + executer.getSkill(this).getDataManager().getDataValue(STACK)), UUID.randomUUID());
		//executer.setStamina(executer.getStamina() - (5f - EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal())));
		if (!executer.getOriginal().isCreative()) {
			executer.getOriginal().level().playSound(null, executer.getOriginal().xo, executer.getOriginal().yo, executer.getOriginal().zo,
	    			SoundEvents.PLAYER_HURT, executer.getOriginal().getSoundSource(), 1.0F, 1.0F);
			EpicFightDamageSource damage = executer.getDamageSource(attackAnimations, InteractionHand.MAIN_HAND);
			damage.setStunType(StunType.NONE);
			executer.getOriginal().hurt(damage, executer.getOriginal().getHealth() * (0.40f - (0.10f * EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal()))));
			super.executeOnServer(executer, args);
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
	public boolean isExecutableState(PlayerPatch<?> executer) {
		executer.updateEntityState();
		EntityState playerState = executer.getEntityState();
		return !(executer.getOriginal().isFallFlying() || executer.currentLivingMotion == LivingMotions.FALL || !playerState.canUseSkill());
	}
	
	@Override
	public WeaponInnateSkill registerPropertiesToAnimation() {
		return this;
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		super.updateContainer(container);
		if (container.getDataManager().getDataValue(PLUNGING)) {
			container.getExecuter().getOriginal().resetFallDistance();
		}
	}
}