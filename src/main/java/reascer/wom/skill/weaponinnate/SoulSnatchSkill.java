package reascer.wom.skill.weaponinnate;

import java.util.List;
import java.util.UUID;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import reascer.wom.gameasset.WOMAnimations;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;


public class SoulSnatchSkill extends WeaponInnateSkill{
	private static final UUID EVENT_UUID = UUID.fromString("b9d719ba-bcb8-11ec-8422-0242ac120002");
	public static final SkillDataKey<Boolean> BUFFED = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	public static final SkillDataKey<Boolean> BUFFING = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	public static final SkillDataKey<Boolean> EXPIATION = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	public static final SkillDataKey<Boolean> REDEMPTION = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	public static final SkillDataKey<Integer> TIMER = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	public static final SkillDataKey<Integer> STRENGHT = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	
	protected Boolean registerdata = true;
	
	public AttributeModifier stolen_move_speed = new AttributeModifier(EVENT_UUID, "ruine.stolen_move_speed", 0, Operation.MULTIPLY_TOTAL);
	public AttributeModifier stolen_attack_speed = new AttributeModifier(EVENT_UUID, "ruine.stolen_move_speed", 0, Operation.MULTIPLY_TOTAL);
	
	public SoulSnatchSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		container.getDataManager().registerData(BUFFED);
		container.getDataManager().registerData(BUFFING);
		container.getDataManager().registerData(STRENGHT);
		container.getDataManager().registerData(EXPIATION);
		container.getDataManager().registerData(REDEMPTION);
		container.getDataManager().registerData(TIMER);
		
		container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_POST, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(BUFFING)) {
				event.getDamageSource().setStunType(StunType.NONE);
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(BUFFING)) {
				if (!container.getDataManager().getDataValue(BUFFED)) {
					if (container.getDataManager().getDataValue(STRENGHT) < 40) {
						container.getDataManager().setDataSync(STRENGHT, container.getDataManager().getDataValue(STRENGHT)+1,event.getPlayerPatch().getOriginal());
					}
					if (event.getTarget().hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
						event.getTarget().removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
						event.getTarget().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (12 + (4 * EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal()))) * 20, 0, false, true));
					} else {
						event.getTarget().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (9 + (3 * EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal()))) * 20, 0, false, true));
					}
					
					if (event.getTarget().hasEffect(MobEffects.DIG_SLOWDOWN)) {
						event.getTarget().removeEffect(MobEffects.DIG_SLOWDOWN);
						event.getTarget().addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, (12 + (4 * EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal()))) * 20, 0, false, true));
					} else {
						event.getTarget().addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, (9 + (3 * EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal()))) * 20, 0, false, true));
					}
					event.getPlayerPatch().setStamina(event.getPlayerPatch().getStamina() + (event.getPlayerPatch().getMaxStamina() * 0.05f));
					event.getPlayerPatch().getOriginal().heal(1 + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal()));
					((ServerLevel) container.getExecuter().getOriginal().level).sendParticles(ParticleTypes.REVERSE_PORTAL, 
							event.getTarget().xo, 
							event.getTarget().yo + 0.2f, 
							event.getTarget().zo, 
							20, 0, 0, 0, 0.4);
					((ServerLevel) container.getExecuter().getOriginal().level).sendParticles(ParticleTypes.PORTAL, 
							event.getTarget().xo, 
							event.getTarget().yo+ 0.2f, 
							event.getTarget().zo, 
							20, 0, 0, 0, 0.4);
					 event.getPlayerPatch().getOriginal().level.playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
				    			SoundEvents.CHAIN_BREAK, container.getExecuter().getOriginal().getSoundSource(), 2.0F, 0.5F);
				}
			}
			if (container.getDataManager().getDataValue(EXPIATION)) {
				if (!container.getDataManager().getDataValue(BUFFED)) {
					container.getDataManager().setDataSync(BUFFED, true, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(TIMER, 100 * (1 + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal())),((ServerPlayerPatch) container.getExecuter()).getOriginal());			
				} else {
					container.getDataManager().setDataSync(TIMER,container.getDataManager().getDataValue(TIMER) - (1 * 20),((ServerPlayerPatch) container.getExecuter()).getOriginal());
				}
				if (container.getDataManager().getDataValue(STRENGHT) < 40) {
					container.getDataManager().setDataSync(STRENGHT, container.getDataManager().getDataValue(STRENGHT)+1,event.getPlayerPatch().getOriginal());
				}
				stolen_move_speed = new AttributeModifier(EVENT_UUID, "ruine.stolen_move_speed", (( 0.03D * container.getDataManager().getDataValue(STRENGHT))), Operation.MULTIPLY_TOTAL);
				stolen_attack_speed = new AttributeModifier(EVENT_UUID, "ruine.stolen_attack_speed", (( 0.015D * container.getDataManager().getDataValue(STRENGHT))), Operation.MULTIPLY_TOTAL);
				container.getExecuter().getOriginal().getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(stolen_move_speed);
				container.getExecuter().getOriginal().getAttribute(Attributes.ATTACK_SPEED).removeModifier(stolen_attack_speed);
				
				container.getExecuter().getOriginal().getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(stolen_move_speed);
				container.getExecuter().getOriginal().getAttribute(Attributes.ATTACK_SPEED).addPermanentModifier(stolen_attack_speed);
				
				event.getPlayerPatch().modifyLivingMotionByCurrentItem();
			}
			if (container.getDataManager().getDataValue(REDEMPTION)) {
				if (!container.getDataManager().getDataValue(BUFFED)) {
					container.getDataManager().setDataSync(BUFFED, true, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(TIMER, 100 * (1 + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal())),((ServerPlayerPatch) container.getExecuter()).getOriginal());			
				} else {
					if (container.getDataManager().getDataValue(STRENGHT) > 1) {
						container.getDataManager().setDataSync(STRENGHT, container.getDataManager().getDataValue(STRENGHT)-1,event.getPlayerPatch().getOriginal());
						container.getDataManager().setDataSync(TIMER,container.getDataManager().getDataValue(TIMER) + ((3 + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal())) * 20),((ServerPlayerPatch) container.getExecuter()).getOriginal());
					}
				}
				stolen_move_speed = new AttributeModifier(EVENT_UUID, "ruine.stolen_move_speed", (( 0.03D * container.getDataManager().getDataValue(STRENGHT))), Operation.MULTIPLY_TOTAL);
				stolen_attack_speed = new AttributeModifier(EVENT_UUID, "ruine.stolen_attack_speed", (( 0.015D * container.getDataManager().getDataValue(STRENGHT))), Operation.MULTIPLY_TOTAL);
				container.getExecuter().getOriginal().getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(stolen_move_speed);
				container.getExecuter().getOriginal().getAttribute(Attributes.ATTACK_SPEED).removeModifier(stolen_attack_speed);
				
				container.getExecuter().getOriginal().getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(stolen_move_speed);
				container.getExecuter().getOriginal().getAttribute(Attributes.ATTACK_SPEED).addPermanentModifier(stolen_attack_speed);
				
				event.getPlayerPatch().modifyLivingMotionByCurrentItem();
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_PRE, EVENT_UUID, (event) -> {
			
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event) -> {
			if (event.getAnimation().equals(WOMAnimations.RUINE_PLUNDER) && container.getDataManager().getDataValue(STRENGHT) > 0) {
				if (!container.getExecuter().isLogicalClient()) {
					container.getDataManager().setDataSync(BUFFED, true, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(BUFFING, false, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(TIMER, container.getDataManager().getDataValue(TIMER) + (200 * (1 + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal()))),((ServerPlayerPatch) container.getExecuter()).getOriginal());
					stolen_move_speed = new AttributeModifier(EVENT_UUID, "ruine.stolen_move_speed", (( 0.03D * container.getDataManager().getDataValue(STRENGHT))), Operation.MULTIPLY_TOTAL);
					stolen_attack_speed = new AttributeModifier(EVENT_UUID, "ruine.stolen_attack_speed", (( 0.015D * container.getDataManager().getDataValue(STRENGHT))), Operation.MULTIPLY_TOTAL);
					container.getExecuter().getOriginal().getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(stolen_move_speed);
					container.getExecuter().getOriginal().getAttribute(Attributes.ATTACK_SPEED).removeModifier(stolen_attack_speed);
					
					container.getExecuter().getOriginal().getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(stolen_move_speed);
					container.getExecuter().getOriginal().getAttribute(Attributes.ATTACK_SPEED).addPermanentModifier(stolen_attack_speed);
					event.getPlayerPatch().modifyLivingMotionByCurrentItem();
				}
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			if (!event.getAnimation().equals(WOMAnimations.RUINE_PLUNDER)) {
				container.getDataManager().setData(BUFFING, false);
			}
			if (!event.getAnimation().equals(WOMAnimations.RUINE_EXPIATION)) {
				container.getDataManager().setData(EXPIATION, false);
			}
			if (!event.getAnimation().equals(WOMAnimations.RUINE_REDEMPTION)) {
				container.getDataManager().setData(REDEMPTION, false);
			}
		});
		
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_POST, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_PRE, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
		container.getDataManager().setData(BUFFED, false);
		container.getDataManager().setData(STRENGHT, 0);
		container.getExecuter().getOriginal().getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(stolen_move_speed);
		container.getExecuter().getOriginal().getAttribute(Attributes.ATTACK_SPEED).removeModifier(stolen_attack_speed);
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		ServerPlayer player = executer.getOriginal();
		if ((!player.isOnGround() && !player.isInWater()) && player.fallDistance < 0.1f && (player.level.isEmptyBlock(player.blockPosition().below()) || (player.yo - player.blockPosition().getY()) > 0.2D)) {
			executer.playAnimationSynchronized(WOMAnimations.RUINE_REDEMPTION, 0);
			executer.getSkill(this).getDataManager().setDataSync(REDEMPTION, true, executer.getOriginal());
			if (executer.getSkill(this).getStack() > 1) {
				this.setStackSynchronize(executer, executer.getSkill(this).getStack() - 1);
			} else {
				if (executer.getSkill(EpicFightSkills.FORBIDDEN_STRENGTH) != null) {
					executer.consumeStamina(24 - executer.getSkill(this).getResource());
					this.setConsumptionSynchronize(executer,0);
				} else {
					executer.getSkill(this).getDataManager().setDataSync(EXPIATION, false, executer.getOriginal());
				}
			}
		} else if(executer.getOriginal().isSprinting()) {
			executer.playAnimationSynchronized(WOMAnimations.RUINE_EXPIATION, 0);
			executer.getSkill(this).getDataManager().setDataSync(EXPIATION, true, executer.getOriginal());
			if (executer.getSkill(this).getStack() > 1) {
				this.setStackSynchronize(executer, executer.getSkill(this).getStack() - 1);
			} else {
				if (executer.getSkill(EpicFightSkills.FORBIDDEN_STRENGTH) != null) {
					executer.consumeStamina(24 - executer.getSkill(this).getResource());
					this.setConsumptionSynchronize(executer,0);
				} else {
					executer.getSkill(this).getDataManager().setDataSync(EXPIATION, false, executer.getOriginal());
				}
			}
			
		} else if (executer.getSkill(this).getStack() == 10 || executer.getOriginal().isCreative()) {
			this.setStackSynchronize(executer, 0);
			executer.playAnimationSynchronized(WOMAnimations.RUINE_PLUNDER, 0);
			executer.getSkill(this).getDataManager().setData(BUFFING, true);
			executer.getSkill(this).getDataManager().setData(BUFFED, false);
			executer.getSkill(this).getDataManager().setData(STRENGHT, 0);
		}
	}
	
	@Override
	public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
		List<Component> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Thrust :");
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(1), "Rip out :");
		
		return list;
	}
	
	@Override
	public WeaponInnateSkill registerPropertiesToAnimation() {
		return this;
	}
	
	@Override
	public void cancelOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		super.cancelOnServer(executer, args);
		executer.getSkill(this).getDataManager().setData(BUFFED, false);
		executer.getSkill(this).getDataManager().setData(STRENGHT, 0);
		executer.getOriginal().getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(stolen_move_speed);
		executer.getOriginal().getAttribute(Attributes.ATTACK_SPEED).removeModifier(stolen_attack_speed);
		executer.modifyLivingMotionByCurrentItem();
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		super.updateContainer(container);
		if (container.getDataManager().getDataValue(BUFFING)) {
			container.getExecuter().getOriginal().addEffect(new MobEffectInstance(EpicFightMobEffects.STUN_IMMUNITY.get(), 5, 0,true,false,false));
		}
		if (container.getDataManager().getDataValue(BUFFED)) {
			if (container.getDataManager().getDataValue(TIMER) > 0) {
				if(!container.getExecuter().isLogicalClient()) {
					((ServerLevel) container.getExecuter().getOriginal().level).sendParticles( ParticleTypes.REVERSE_PORTAL, 
							container.getExecuter().getOriginal().getX() - 0.15D, 
							container.getExecuter().getOriginal().getY() + 1.05D, 
							container.getExecuter().getOriginal().getZ() - 0.15D, 
							4, 0.3D, 0.4D, 0.3D, 0.05);
					if (container.getDataManager().getDataValue(TIMER) % 20 == 0) {
						((ServerLevel) container.getExecuter().getOriginal().level).sendParticles( container.getDataManager().getDataValue(STRENGHT) == 40 ? ParticleTypes.END_ROD : ParticleTypes.SOUL_FIRE_FLAME, 
								container.getExecuter().getOriginal().getX() - 0.15D, 
								container.getExecuter().getOriginal().getY() + 1.05D, 
								container.getExecuter().getOriginal().getZ() - 0.15D, 
								container.getDataManager().getDataValue(STRENGHT), 0.3D, 0.4D, 0.3D, 0.01);
					}
					container.getDataManager().setDataSync(TIMER, container.getDataManager().getDataValue(TIMER)-1,((ServerPlayerPatch) container.getExecuter()).getOriginal());
				}
			} else {
				if(!container.getExecuter().isLogicalClient()) {
					container.getSkill().cancelOnServer((ServerPlayerPatch)container.getExecuter(), null);
				}
			}
		}
		if (container.getStack() == 0) {
			if(!container.getExecuter().isLogicalClient()) {
				this.setStackSynchronize((ServerPlayerPatch)container.getExecuter(), 1);
			}
		}
	}
}