package reascer.wom.skill.weaponinnate;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.skill.WomMultipleAnimationSkill;
import reascer.wom.skill.guard.CounterAttack;
import reascer.wom.world.item.HerscherItem;
import reascer.wom.world.item.MagneticShieldItem;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class RegierungSkill extends WomMultipleAnimationSkill {
	private static final SkillDataKey<Integer> COMBO = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	private static final SkillDataKey<Integer> COOLDOWN = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	public static final SkillDataKey<Boolean> GUARD_POINT = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	public static final SkillDataKey<Integer> GUARD_POINT_RESULT = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	public static final SkillDataKey<Boolean> GESETZ_SPRENGKOPF = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	public static final SkillDataKey<Boolean> SUPER_ARMOR = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	
	private static final UUID EVENT_UUID = UUID.fromString("63c38d4f-cc97-4339-bedf-d9bba36ba29f");
    
	public RegierungSkill(Builder<? extends Skill> builder) {
		super(builder, (executer) -> {
			int combo = executer.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(COMBO);
			return combo;
			
		},  WOMAnimations.GESETZ_AUTO_1, 
			WOMAnimations.GESETZ_AUTO_2, 
			WOMAnimations.GESETZ_AUTO_3,
			WOMAnimations.GESETZ_KRUMMEN,
			WOMAnimations.GESETZ_SPRENGKOPF);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(COMBO);
		container.getDataManager().registerData(COOLDOWN);
		container.getDataManager().registerData(GUARD_POINT);
		container.getDataManager().registerData(GUARD_POINT_RESULT);
		container.getDataManager().registerData(GESETZ_SPRENGKOPF);
		container.getDataManager().registerData(SUPER_ARMOR);
		
		container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_POST, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(SUPER_ARMOR)) {
				event.getDamageSource().setStunType(StunType.NONE);
			}
		});
		if(!container.getExecuter().isLogicalClient()) {
			container.getDataManager().setDataSync(COMBO, 0,((ServerPlayerPatch)container.getExecuter()).getOriginal());
			this.maxDuration += (200 * EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, ((ServerPlayerPatch)container.getExecuter()).getOriginal()));
		}
		
		container.getExecuter().getEventListener().addEventListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(GESETZ_SPRENGKOPF) && container.getRemainDuration() > 0) {
				float attackDamage = event.getDamage();
				event.setDamage(attackDamage + (container.getRemainDuration()/20));
				container.getExecuter().getOriginal().resetFallDistance();
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, (event) -> {
			container.getDataManager().setData(GUARD_POINT, false);
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			if(!container.getExecuter().isLogicalClient()) {
				container.getDataManager().setDataSync(GESETZ_SPRENGKOPF, false, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
				container.getDataManager().setDataSync(COOLDOWN, 40, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
				if (!event.getAnimation().equals(WOMAnimations.HERRSCHER_AUTO_2) &&
						!event.getAnimation().equals(WOMAnimations.GESETZ_AUTO_1)) {
					container.getDataManager().setDataSync(GUARD_POINT, false, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
				}
				
				if (event.getAnimation().equals(WOMAnimations.HERRSCHER_GUARD_HIT)) {
					this.setDurationSynchronize(event.getPlayerPatch(), container.getRemainDuration() +40);
				}
				
				if (event.getAnimation().equals(WOMAnimations.GESETZ_SPRENGKOPF)) {
					container.getDataManager().setDataSync(SUPER_ARMOR, true, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
				}
				
				if (event.getAnimation().equals(WOMAnimations.HERRSCHER_TRANE)) {
					this.setDurationSynchronize(event.getPlayerPatch(), container.getRemainDuration() +100);
					container.getDataManager().setDataSync(COMBO, 0, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				}
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_PRE, EVENT_UUID, (event) -> {
		
			
			if (container.getDataManager().getDataValue(GUARD_POINT)) {
				DamageSource damageSource = event.getDamageSource();
				boolean isFront = false;
				Vec3 sourceLocation = damageSource.getSourcePosition();
				
				if (sourceLocation != null) {
					Vec3 viewVector = event.getPlayerPatch().getOriginal().getViewVector(1.0F);
					Vec3 toSourceLocation = sourceLocation.subtract(event.getPlayerPatch().getOriginal().position()).normalize();
					
					if (toSourceLocation.dot(viewVector) > 0.0D) {
						isFront = true;
					}
				}
				
				if (isFront) {
					float impact = 0.5F;
					float knockback = 0.25F;
					
					if (event.getDamageSource() instanceof EpicFightDamageSource) {
						impact = ((EpicFightDamageSource)event.getDamageSource()).getImpact();
						knockback += Math.min(impact * 0.1F, 1.0F);
					}
					ServerPlayer playerentity = event.getPlayerPatch().getOriginal();
					event.getPlayerPatch().playSound(EpicFightSounds.CLASH.get(), -0.05F, 0.1F);
					EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument(((ServerLevel)playerentity.level), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, playerentity, damageSource.getDirectEntity());
					
					StaticAnimation animation = Animations.BIPED_HIT_SHIELD;;
					float convert = -0.05f;
					switch (container.getDataManager().getDataValue(GUARD_POINT_RESULT)) {
					case 1: {
						animation = WOMAnimations.GESETZ_AUTO_1;
						container.getDataManager().setDataSync(COMBO, 1, playerentity);
						this.setDurationSynchronize(event.getPlayerPatch(), container.getRemainDuration() +20);
						break;
					}
					case 2: {
						animation = WOMAnimations.HERRSCHER_AUTO_2;
						this.setDurationSynchronize(event.getPlayerPatch(), container.getRemainDuration() +40);
						break;
					}
					case 3: {
						animation = WOMAnimations.HERRSCHER_AUTO_3;
						this.setDurationSynchronize(event.getPlayerPatch(), container.getRemainDuration() +100);
						convert = -0.45f;
						break;
					}
					case 4: {
						animation = WOMAnimations.HERRSCHER_TRANE;
						convert = -0.15f;
						break;
					}
					case 5: {
						animation = WOMAnimations.HERRSCHER_AUTO_2;
						container.getDataManager().setDataSync(COMBO, 1, playerentity);
						this.setDurationSynchronize(event.getPlayerPatch(), container.getRemainDuration() +40);
						break;
					}
					case 6: {
						animation = WOMAnimations.HERRSCHER_BEFREIUNG;
						convert = -0.25f;
						this.setDurationSynchronize(event.getPlayerPatch(), container.getRemainDuration() +20);
						break;
					}
					default:
						animation = Animations.BIPED_HIT_SHIELD;
					}
					
					event.getPlayerPatch().playAnimationSynchronized(animation,convert);
					
					event.setCanceled(true);
					event.setResult(AttackResult.ResultType.BLOCKED);
					
					Entity directEntity = event.getDamageSource().getDirectEntity();
					LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(directEntity, LivingEntityPatch.class);
					
					if (entitypatch != null) {
						entitypatch.onAttackBlocked(event.getDamageSource(), entitypatch);
					}
				}
			}
		});
	}

	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
		container.getExecuter().getEventListener().removeListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_PRE, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_POST, EVENT_UUID);
		
		if (container.getExecuter().getSkill(WOMSkills.COUNTER_ATTACK) != null) {
			if(!container.getExecuter().isLogicalClient()) {
				container.getExecuter().getSkill(WOMSkills.COUNTER_ATTACK).getDataManager().setDataSync(CounterAttack.CONSUMPTION_VALUE, 1f,((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
		}
		
	}
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		ServerPlayer player = executer.getOriginal();
		if ((!player.isOnGround() && !player.isInWater()) && (player.level.isEmptyBlock(player.blockPosition().below()) || (player.yo - player.blockPosition().getY()) > 0.2D)) {
			if (!executer.consumeStamina(3)) {
				executer.setStamina(0);
			} else {
				executer.playAnimationSynchronized(this.attackAnimations[this.attackAnimations.length - 1], 0);
			}
		} else {
			if(executer.getOriginal().isSprinting()) {
				if (!executer.consumeStamina(2)) {
					executer.setStamina(0);
				} else {
					executer.playAnimationSynchronized(this.attackAnimations[this.attackAnimations.length - 2], 0);
				}
				executer.getSkill(this).getDataManager().setDataSync(COMBO, 0, executer.getOriginal());
			} else {
				int animation = this.getAnimationInCondition(executer);
				executer.playAnimationSynchronized(this.attackAnimations[animation], 0);
				if (executer.getSkill(this).getDataManager().getDataValue(COMBO) < 2) {
					executer.getSkill(this).getDataManager().setDataSync(COMBO, executer.getSkill(this).getDataManager().getDataValue(COMBO)+1, executer.getOriginal());
				}
				else {
					executer.getSkill(this).getDataManager().setDataSync(COMBO, 0, executer.getOriginal());
				}
				
			}
		}
		executer.getSkill(this).getDataManager().setDataSync(COOLDOWN, 40, executer.getOriginal());
	}
	
	@Override
	public boolean canExecute(PlayerPatch<?> executer) {
		if (executer.getValidItemInHand(InteractionHand.OFF_HAND).getItem() instanceof MagneticShieldItem && executer.getValidItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof HerscherItem) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isExecutableState(PlayerPatch<?> executer) {
		executer.updateEntityState();
		EntityState playerState = executer.getEntityState();
		return !(executer.getOriginal().isFallFlying() || executer.currentLivingMotion == LivingMotions.FALL || !playerState.canUseSkill() || !executer.getEntityState().canBasicAttack());
	}
	
	@Override
	public boolean resourcePredicate(PlayerPatch<?> playerpatch) {
		return true;
	}
	
	@Override
	public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
		List<Component> list = Lists.newArrayList();
		String traslatableText = this.getTranslationKey();
		
		list.add(Component.translatable(traslatableText).withStyle(ChatFormatting.WHITE).append(Component.literal(String.format("[%.0f]", this.consumption)).withStyle(ChatFormatting.AQUA)));
		list.add(Component.translatable(traslatableText + ".tooltip").withStyle(ChatFormatting.DARK_GRAY));
		return list;
	}
	
	@Override
	public WeaponInnateSkill registerPropertiesToAnimation() {
		return this;
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		super.updateContainer(container);
		if (container.getDataManager().getDataValue(COOLDOWN) > 0) {
			if(!container.getExecuter().isLogicalClient()) {
				container.getDataManager().setDataSync(COOLDOWN, container.getDataManager().getDataValue(COOLDOWN)-1,((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
		} else {
			if(!container.getExecuter().isLogicalClient()) {
				container.getExecuter().getSkill(this).getDataManager().setDataSync(COMBO, 0,((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
		}
		if (container.getExecuter().getSkill(WOMSkills.COUNTER_ATTACK) != null) {
			if (container.getExecuter().getSkill(WOMSkills.COUNTER_ATTACK).getDataManager().getDataValue(CounterAttack.CONSUMPTION_VALUE) != 0.2f) {
				if(!container.getExecuter().isLogicalClient()) {
					container.getExecuter().getSkill(WOMSkills.COUNTER_ATTACK).getDataManager().setDataSync(CounterAttack.CONSUMPTION_VALUE, 0.2f,((ServerPlayerPatch)container.getExecuter()).getOriginal());
				}
			}
		}
		
		if (container.getDataManager().getDataValue(SUPER_ARMOR)) {
			container.getExecuter().getOriginal().addEffect(new MobEffectInstance(EpicFightMobEffects.STUN_IMMUNITY.get(), 5, 0,true,false,false));
		}
		container.setResource(10.0f);
	}
}