
package reascer.wom.skill.weaponinnate;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.gameasset.WOMSounds;
import reascer.wom.particle.WOMParticles;
import reascer.wom.skill.WOMSkillDataKeys;
import reascer.wom.world.item.WOMItems;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageType;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.DeathHarvestOrb;
import yesman.epicfight.world.entity.eventlistener.DealtDamageEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.SkillConsumeEvent;

public class DemonicAscensionSkill extends WeaponInnateSkill {
	private static final UUID EVENT_UUID = UUID.fromString("61ec318a-10f6-11ed-861d-0242ac120002");
	
	public DemonicAscensionSkill(Builder builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getExecuter().getEventListener().addEventListener(EventType.CLIENT_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
			if (event.getPlayerPatch().getOriginal().getItemInHand(InteractionHand.MAIN_HAND).getItem() == WOMItems.ANTITHEUS.get() && (container.getExecuter().getEntityState().canBasicAttack() || container.getDataManager().getDataValue(WOMSkillDataKeys.DARKNESS_TARGET_HITED.get()) || (container.getDataManager().getDataValue(WOMSkillDataKeys.ACTIVE.get()) && container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) >= (40*20) || container.getExecuter().getOriginal().isCreative()))) {
				event.getPlayerPatch().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
			if (event.getPlayerPatch().getOriginal().getItemInHand(InteractionHand.MAIN_HAND).getItem() == WOMItems.ANTITHEUS.get() && (container.getExecuter().getEntityState().canBasicAttack() || container.getDataManager().getDataValue(WOMSkillDataKeys.DARKNESS_TARGET_HITED.get()) || (container.getDataManager().getDataValue(WOMSkillDataKeys.ACTIVE.get()) && container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) >= (40*20) || container.getExecuter().getOriginal().isCreative()))) {
				event.getPlayerPatch().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
				if(!container.getExecuter().isLogicalClient()) {
					container.getDataManager().setDataSync(WOMSkillDataKeys.SHOOT.get(), true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				}
			}
			if(!container.getExecuter().isLogicalClient() && event.getPlayerPatch().getOriginal().getItemInHand(InteractionHand.MAIN_HAND).getItem() == WOMItems.ANTITHEUS.get()) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.ZOOM_COOLDOWN.get(), 40, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				container.getDataManager().setDataSync(WOMSkillDataKeys.ZOOM.get(), true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
			PlayerPatch<?> playerpatch = container.getExecuter();
			
			if (!event.getDamageSource().getAnimation().equals(WOMAnimations.ANTITHEUS_PULL)) {
				if (container.getDataManager().getDataValue(WOMSkillDataKeys.ASCENDING.get())) {
					if (event.getTarget().hasEffect(MobEffects.WITHER)) {
						((ServerLevel) event.getPlayerPatch().getOriginal().level()).sendParticles( ParticleTypes.SOUL, 
								event.getTarget().getX(), 
								event.getTarget().getY() + 1.2D, 
								event.getTarget().getZ(), 
								48, 0.0D, 0.0D, 0.0D, 0.05D);
						event.getPlayerPatch().getOriginal().level().playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
				    			SoundEvents.WITHER_HURT, container.getExecuter().getOriginal().getSoundSource(), 1.5F, 0.5F);
						if (event.getTarget().getEffect(MobEffects.WITHER).getAmplifier() == 2) {
							((ServerLevel) event.getPlayerPatch().getOriginal().level()).sendParticles( ParticleTypes.SOUL_FIRE_FLAME, 
									event.getTarget().getX(), 
									event.getTarget().getY() + 1.2D, 
									event.getTarget().getZ(), 
									24, 0.0D, 0.0D, 0.0D, 0.05D);
							event.getPlayerPatch().getOriginal().level().playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
					    			SoundEvents.WITHER_SKELETON_HURT, container.getExecuter().getOriginal().getSoundSource(), 1.5F, 0.5F);
						}
						event.getTarget().playSound(SoundEvents.WITHER_HURT, 1.5f, 0.5f);
						int wither_lvl = event.getTarget().getEffect(MobEffects.WITHER).getAmplifier()+1;
						float WitherCatharsis = (float) ((event.getTarget().getEffect(MobEffects.WITHER).getDuration()/20) * ( wither_lvl == 0 ? 0.5f : wither_lvl));
						//container.getExecuter().getOriginal().sendMessage(new TextComponent("Damage dealt: " + WitherCatharsis + " on " + event.getTarget().getMaxHealth() + "/" + event.getTarget().getHealth()), UUID.randomUUID());
						//container.getExecuter().getOriginal().sendMessage(new TextComponent("Catarsis healing: " + WitherCatharsis/2 + " on " + event.getPlayerPatch().getOriginal().getMaxHealth() + "/" + event.getPlayerPatch().getOriginal().getHealth()), UUID.randomUUID());
						EpicFightDamageSource damage = event.getPlayerPatch().getDamageSource(WOMAnimations.ANTITHEUS_PULL, InteractionHand.MAIN_HAND);
						event.getTarget().hurt(damage, WitherCatharsis*0.5f);
						event.getPlayerPatch().getOriginal().heal(WitherCatharsis*0.5f);
						container.getDataManager().setDataSync(WOMSkillDataKeys.WITHERAFTEREFFECT.get(),2,event.getPlayerPatch().getOriginal());
						container.getDataManager().setDataSync(WOMSkillDataKeys.TIMER.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) + (wither_lvl*20*2), event.getPlayerPatch().getOriginal());
						event.getTarget().removeEffect(MobEffects.WITHER); 
					}
				}
				//System.out.println("Datavalue.Active : " + container.getDataManager().getDataValue(ACTIVE) + " | Datavalue.Ascending : " + container.getDataManager().getDataValue(WOMSkillDataKeys.ASCENDING.get()) + " | Datavalue.WitherCatharsis : " + container.getDataManager().getDataValue(WOMSkillDataKeys.WITHERCATHARSIS.get())) ;
				if (container.getDataManager().getDataValue(WOMSkillDataKeys.ACTIVE.get()) && !container.getDataManager().getDataValue(WOMSkillDataKeys.ASCENDING.get()) && container.getDataManager().getDataValue(WOMSkillDataKeys.WITHERCATHARSIS.get())) {
					if (event.getTarget().hasEffect(MobEffects.WITHER)) {
							((ServerLevel) event.getPlayerPatch().getOriginal().level()).sendParticles( ParticleTypes.SOUL, 
									event.getTarget().getX(), 
									event.getTarget().getY() + 1.2D, 
									event.getTarget().getZ(), 
									48, 0.0D, 0.0D, 0.0D, 0.05D);
							event.getPlayerPatch().getOriginal().level().playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
					    			SoundEvents.WITHER_HURT, container.getExecuter().getOriginal().getSoundSource(), 1.5F, 0.5F);
							if (event.getTarget().getEffect(MobEffects.WITHER).getAmplifier() == 2) {
								((ServerLevel) event.getPlayerPatch().getOriginal().level()).sendParticles( ParticleTypes.SOUL_FIRE_FLAME, 
										event.getTarget().getX(), 
										event.getTarget().getY() + 1.2D, 
										event.getTarget().getZ(), 
										24, 0.0D, 0.0D, 0.0D, 0.05D);
								event.getPlayerPatch().getOriginal().level().playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
						    			SoundEvents.WITHER_SKELETON_HURT, container.getExecuter().getOriginal().getSoundSource(), 1.5F, 0.5F);
							}
							
							
							//container.getExecuter().getOriginal().sendMessage(new TextComponent("Damage dealt: " + WitherCatharsis + " on " + event.getTarget().getMaxHealth() + "/" + event.getTarget().getHealth()), UUID.randomUUID());
							//container.getExecuter().getOriginal().sendMessage(new TextComponent("Catarsis healing: " + WitherCatharsis/2 + " on " + event.getPlayerPatch().getOriginal().getMaxHealth() + "/" + event.getPlayerPatch().getOriginal().getHealth()), UUID.randomUUID());
							int wither_lvl = event.getTarget().getEffect(MobEffects.WITHER).getAmplifier()+1;
							float WitherCatharsis = (float) ((event.getTarget().getEffect(MobEffects.WITHER).getDuration()/20) * ( wither_lvl == 0 ? 0.5f : wither_lvl));
							DamageSource damage = event.getPlayerPatch().getDamageSource(WOMAnimations.ANTITHEUS_PULL, InteractionHand.MAIN_HAND);
							event.getTarget().hurt(damage, WitherCatharsis*0.8f);
							event.getPlayerPatch().getOriginal().heal(WitherCatharsis*0.2f);
							container.getDataManager().setDataSync(WOMSkillDataKeys.TIMER.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) + (wither_lvl*20*2), event.getPlayerPatch().getOriginal());
							if (container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) > 666*20) {
								EpicFightDamageSource selfdamage = event.getPlayerPatch().getDamageSource(WOMAnimations.ANTITHEUS_PULL, InteractionHand.MAIN_HAND);
								selfdamage.setStunType(StunType.NONE);
								event.getPlayerPatch().getOriginal().hurt(selfdamage, event.getPlayerPatch().getOriginal().getHealth()*10);
								container.getDataManager().setDataSync(WOMSkillDataKeys.TIMER.get(), 667*20, event.getPlayerPatch().getOriginal());
							}
							event.getTarget().removeEffect(MobEffects.WITHER);
					}
				}
			}
			container.getExecuter().getOriginal().resetFallDistance();
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.ASCENDING.get())) {
				if (!event.getPlayerPatch().getSkill(SkillSlots.WEAPON_PASSIVE).isEmpty()) {
					event.getPlayerPatch().getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(WOMSkillDataKeys.PARTICLE.get(), true, event.getPlayerPatch().getOriginal());					
				}
				event.getPlayerPatch().getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.ASCENDING.get(), false, event.getPlayerPatch().getOriginal());
				event.getPlayerPatch().getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.SUPERARMOR.get(), false, event.getPlayerPatch().getOriginal());
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			if (!event.getAnimation().equals(WOMAnimations.ANTITHEUS_SHOOT) && !event.getAnimation().equals(WOMAnimations.ANTITHEUS_PULL)) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.ZOOM.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				container.getDataManager().setDataSync(WOMSkillDataKeys.SHOOT.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
			
			if (!container.getDataManager().getDataValue(WOMSkillDataKeys.WITHERCATHARSIS.get())) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.WITHERCATHARSIS.get(),true,event.getPlayerPatch().getOriginal());
			}
			
			if (event.getAnimation().equals(WOMAnimations.ANTITHEUS_ASCENDED_DEATHFALL)) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.WITHERCATHARSIS.get(),true,event.getPlayerPatch().getOriginal());
				if (!container.getExecuter().getOriginal().isCreative()) {
					/*
					event.getPlayerPatch().getOriginal().level().playSound(null, event.getPlayerPatch().getOriginal().xo, event.getPlayerPatch().getOriginal().yo, event.getPlayerPatch().getOriginal().zo,
			    			SoundEvents.PLAYER_HURT, event.getPlayerPatch().getOriginal().getSoundSource(), 1.0F, 1.0F);
					*/
					EpicFightDamageSource damage = event.getPlayerPatch().getDamageSource(WOMAnimations.ANTITHEUS_PULL, InteractionHand.MAIN_HAND);
					damage.setStunType(StunType.NONE);
					event.getPlayerPatch().getOriginal().hurt(damage, event.getPlayerPatch().getOriginal().getHealth() * 0.1F);
					//event.getPlayerPatch().getOriginal().setHealth(event.getPlayerPatch().getOriginal().getHealth() * 0.9F);
				}
				container.getDataManager().setDataSync(WOMSkillDataKeys.WITHERAFTEREFFECT.get(),2,event.getPlayerPatch().getOriginal());
			}
			
			if (event.getAnimation().equals(WOMAnimations.ANTITHEUS_ASCENDED_BLACKHOLE)) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.WITHERCATHARSIS.get(),true,event.getPlayerPatch().getOriginal());
				container.getDataManager().setDataSync(WOMSkillDataKeys.WITHERAFTEREFFECT.get(),0,event.getPlayerPatch().getOriginal());
			}
			
			if (event.getAnimation().equals(WOMAnimations.ANTITHEUS_ASCENDED_AUTO_1)) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.WITHERCATHARSIS.get(),true,event.getPlayerPatch().getOriginal());
				container.getDataManager().setDataSync(WOMSkillDataKeys.WITHERAFTEREFFECT.get(),0,event.getPlayerPatch().getOriginal());
			}
			
			if (event.getAnimation().equals(WOMAnimations.ANTITHEUS_ASCENDED_AUTO_2)) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.WITHERCATHARSIS.get(),true,event.getPlayerPatch().getOriginal());
				container.getDataManager().setDataSync(WOMSkillDataKeys.WITHERAFTEREFFECT.get(),0,event.getPlayerPatch().getOriginal());
			}
			
			if (event.getAnimation().equals(WOMAnimations.ANTITHEUS_ASCENDED_AUTO_3)) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.WITHERCATHARSIS.get(),true,event.getPlayerPatch().getOriginal());
				container.getDataManager().setDataSync(WOMSkillDataKeys.WITHERAFTEREFFECT.get(),0,event.getPlayerPatch().getOriginal());
			}
			
			if (event.getAnimation().equals(WOMAnimations.ANTITHEUS_ASCENDED_BLINK)) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.WITHERCATHARSIS.get(),false,event.getPlayerPatch().getOriginal());
				if (!container.getExecuter().getOriginal().isCreative()) {
					/*
					event.getPlayerPatch().getOriginal().level().playSound(null, event.getPlayerPatch().getOriginal().xo, event.getPlayerPatch().getOriginal().yo, event.getPlayerPatch().getOriginal().zo,
			    			SoundEvents.PLAYER_HURT, event.getPlayerPatch().getOriginal().getSoundSource(), 1.0F, 1.0F);
					event.getPlayerPatch().getOriginal().setHealth(event.getPlayerPatch().getOriginal().getHealth()-container.getDataManager().getDataValue(WOMSkillDataKeys.WITHERAFTEREFFECT.get()));
					*/
					EpicFightDamageSource damage = event.getPlayerPatch().getDamageSource(WOMAnimations.ANTITHEUS_PULL, InteractionHand.MAIN_HAND);
					damage.setStunType(StunType.NONE);
					event.getPlayerPatch().getOriginal().hurt(damage, container.getDataManager().getDataValue(WOMSkillDataKeys.WITHERAFTEREFFECT.get()));
				}
				container.getDataManager().setDataSync(WOMSkillDataKeys.WITHERAFTEREFFECT.get(),container.getDataManager().getDataValue(WOMSkillDataKeys.WITHERAFTEREFFECT.get())+2,event.getPlayerPatch().getOriginal());
			}
			
			if (event.getAnimation().equals(WOMAnimations.ANTITHEUS_ASCENSION)) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.WITHERCATHARSIS.get(),false,event.getPlayerPatch().getOriginal());
				container.getDataManager().setDataSync(WOMSkillDataKeys.WITHERAFTEREFFECT.get(),0,event.getPlayerPatch().getOriginal());
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_POST, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.BLACKHOLE_TIMER.get()) > 100) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.BLACKHOLE_ACTIVE.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.SUPERARMOR.get())) {
				event.getDamageSource().setStunType(StunType.NONE);
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.CLIENT_ITEM_USE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_POST, EVENT_UUID);
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		if (executer.getSkill(this).isActivated()) { 
			executer.playAnimationSynchronized(WOMAnimations.ANTITHEUS_LAPSE, 0);
			cancelOnServer(executer, args);
			executer.getSkill(this).deactivate();
			executer.modifyLivingMotionByCurrentItem();
		} else {
			executer.playAnimationSynchronized(WOMAnimations.ANTITHEUS_ASCENSION, 0);
			executer.getSkill(this).getDataManager().setData(WOMSkillDataKeys.TIMER.get(), (int)((20*20) * (1 + (EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal())/2f))));
			executer.getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.SUPERARMOR.get(), true, executer.getOriginal());
			executer.getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.ACTIVE.get(), false, executer.getOriginal());
			executer.getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.ASCENDING.get(), false, executer.getOriginal());
			this.setMaxDurationSynchronize(executer, 666*20);
			this.setDurationSynchronize(executer, executer.getSkill(this).getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()));
			executer.getSkill(this).activate();
			executer.modifyLivingMotionByCurrentItem();
			SkillConsumeEvent event = new SkillConsumeEvent(executer, this, this.resource, true);
			executer.getEventListener().triggerEvents(EventType.SKILL_CONSUME_EVENT, event);
			
			if (!event.isCanceled()) {
				event.getResourceType().consumer.consume(this, executer, event.getAmount());
			}
			this.setStackSynchronize(executer, 1);
			executer.getSkill(this).activate();
		}
	}
	
	@Override
	public void cancelOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		executer.getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.ACTIVE.get(), false,executer.getOriginal());
		executer.getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.ASCENDING.get(), false,executer.getOriginal());
		executer.getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.SUPERARMOR.get(), true, executer.getOriginal());
		if (executer.getSkill(SkillSlots.WEAPON_PASSIVE) != null) {
			if (executer.getSkill(SkillSlots.WEAPON_PASSIVE).getSkill() == WOMSkills.DEMON_MARK_PASSIVE) {
				executer.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(WOMSkillDataKeys.PARTICLE.get(), false, executer.getOriginal());
				executer.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(WOMSkillDataKeys.LAPSE.get(), true, executer.getOriginal());
			}
		}
		this.setStackSynchronize(executer, executer.getSkill(this).getStack() - 1);
		this.setDurationSynchronize(executer, 0);
		executer.modifyLivingMotionByCurrentItem();
		super.cancelOnServer(executer, args);
	}
	
	@Override
	public boolean resourcePredicate(PlayerPatch<?> playerpatch) {
		float consumption = this.getDefaultConsumeptionAmount(playerpatch);
		
		SkillConsumeEvent event = new SkillConsumeEvent(playerpatch, this, this.resource, consumption, false);
		playerpatch.getEventListener().triggerEvents(EventType.SKILL_CONSUME_EVENT, event);
		
		if (event.isCanceled()) {
			return false;
		}
		
		if (event.getResourceType().predicate.canExecute(this, playerpatch, event.getAmount())) {
			return true;
		} else {
			if (playerpatch.getSkill(EpicFightSkills.HYPERVITALITY) != null &&
					playerpatch.getSkill(EpicFightSkills.FORBIDDEN_STRENGTH) != null) {
				if (playerpatch.getSkill(EpicFightSkills.HYPERVITALITY).getStack() > 0) {
					playerpatch.getSkill(EpicFightSkills.HYPERVITALITY).setMaxDuration(event.getSkill().getMaxDuration());
					playerpatch.getSkill(EpicFightSkills.HYPERVITALITY).activate();
					
					float ressource = playerpatch.getSkill(this).getResource();
					if (playerpatch.getSkill(this).getStack() == 1) {
						ressource = 666f;
					}
					
					playerpatch.getSkill(EpicFightSkills.HYPERVITALITY).setMaxResource(120 * (1 - ressource/playerpatch.getSkill(this).getMaxResource()));
					DamageSource damage = event.getPlayerPatch().getDamageSource(WOMAnimations.ANTITHEUS_PULL, InteractionHand.MAIN_HAND);
					playerpatch.getOriginal().hurt(damage, (playerpatch.getOriginal().getHealth() * (1 - ressource/playerpatch.getSkill(this).getMaxResource()))-1);
					if (!playerpatch.isLogicalClient()) {
						this.setConsumptionSynchronize((ServerPlayerPatch)playerpatch, 0);
					}
					return true;
				}
			}
			return false;
		}
	}
	
	@Override
	public boolean canExecute(PlayerPatch<?> executer) {
		if (executer.isLogicalClient()) {
			return super.canExecute(executer);
		} else {
			ItemStack itemstack = executer.getOriginal().getMainHandItem();
			return EpicFightCapabilities.getItemStackCapability(itemstack).getInnateSkill(executer, itemstack) == this && executer.getOriginal().getVehicle() == null;
		}
	}
	
	@Override
	public boolean isExecutableState(PlayerPatch<?> executer) {
		executer.updateEntityState();
		EntityState playerState = executer.getEntityState();
		return !(executer.getOriginal().isFallFlying() || executer.currentLivingMotion == LivingMotions.FALL || !playerState.canUseSkill() || !executer.getEntityState().canBasicAttack());
	}
	
	@Override
	public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
		List<Component> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		
		return list;
	}
	
	/*
	@OnlyIn(Dist.CLIENT)
	@Override
	public void onScreen(LocalPlayerPatch playerpatch, float resolutionX, float resolutionY) {
		if (playerpatch.getSkill(this).isActivated()) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, new ResourceLocation(EpicFightMod.MODID, "textures/gui/overlay/true_berserk.png"));
			GlStateManager._enableBlend();
			GlStateManager._disableDepthTest();
			GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Tesselator tessellator = Tesselator.getInstance();
		    BufferBuilder bufferbuilder = tessellator.getBuilder();
		    bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		    bufferbuilder.vertex(0, 0, 1).uv(0, 0).endVertex();
		    bufferbuilder.vertex(0, resolutionY, 1).uv(0, 1).endVertex();
		    bufferbuilder.vertex(resolutionX, resolutionY, 1).uv(1, 1).endVertex();
		    bufferbuilder.vertex(resolutionX, 0, 1).uv(1, 0).endVertex();
		    tessellator.end();
		}
	}
	*/
	
	@Override
	public void updateContainer(SkillContainer container) {
		super.updateContainer(container);
		if(!container.getExecuter().isLogicalClient()) {
			for (String tag : container.getExecuter().getOriginal().getTags()) {
				if (tag.contains("antitheus_pull:")) {
					container.getDataManager().setDataSync(WOMSkillDataKeys.DARKNESS_TARGET.get(), Integer.valueOf(tag.substring(15)), ((ServerPlayerPatch)container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(WOMSkillDataKeys.DARKNESS_TARGET_HITED.get(), true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(WOMSkillDataKeys.DARKNESS_ACTIVATE_PORTAL.get(), true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(WOMSkillDataKeys.DARKNESS_PORTAL_TIMER.get(), 320, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
					container.getExecuter().getOriginal().getTags().remove(tag);
					break;
				}
			}
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.DARKNESS_TARGET_HITED.get())) {
				LivingEntity target = (LivingEntity) container.getExecuter().getOriginal().level().getEntity(container.getDataManager().getDataValue(WOMSkillDataKeys.DARKNESS_TARGET.get()));
				if (target != null) {
					container.getDataManager().setDataSync(WOMSkillDataKeys.DARKNESS_TARGET_VEC.get(), new Vec3f((float) target.getX(), (float) target.getY(), (float) target.getZ()), ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				}
			}
		}
		if (container.getDataManager().getDataValue(WOMSkillDataKeys.DARKNESS_TARGET_REAPED.get())) {
			if(!container.getExecuter().isLogicalClient()) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.DARKNESS_TARGET_REAPED.get(), false,((ServerPlayerPatch)container.getExecuter()).getOriginal());
				LivingEntity target = (LivingEntity) container.getExecuter().getOriginal().level().getEntity(container.getDataManager().getDataValue(WOMSkillDataKeys.DARKNESS_TARGET.get()));
				if (target != null) {
					Vec3f vec3 = container.getDataManager().getDataValue(WOMSkillDataKeys.DARKNESS_TARGET_VEC.get());
					float dpx = vec3.x;
					float dpy = vec3.y;
					float dpz = vec3.z;
					target.teleportTo(dpx, dpy+1, dpz);
					((ServerLevel) container.getExecuter().getOriginal().level()).sendParticles( WOMParticles.ANTITHEUS_PUNCH.get(), 
							target.getX(), 
							target.getY() + 1.2D, 
							target.getZ(), 
							1, 0.0D, 0.0D, 0.0D, 0.00D);
				}
			}
			if(container.getExecuter().isLogicalClient()) {
				Vec3f vec3 = container.getDataManager().getDataValue(WOMSkillDataKeys.DARKNESS_TARGET_VEC.get());
				float target_x = vec3.x;
				float target_y = vec3.y;
				float target_z = vec3.z;
				
				int n = 40; // set the number of particles to emit
				double r = 0.4; // set the radius of the disk to 1
				double t = 0.01; // set the thickness of the disk to 0.1
				
				for (int i = 0; i < n; i++) {
					double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
					double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness
					
					// calculate the emission direction in Cartesian coordinates using the polar coordinates
					double x = r * Math.cos(phi) * Math.cos(theta);
					double y = r * Math.cos(phi) * Math.sin(theta);
					double z = r * Math.sin(phi);
					
					// create a Vector3f object to represent the emission direction
					Vec3f direction = new Vec3f((float)x, (float)y, (float)z);
					
					// rotate the direction vector to align with the forward vector
					OpenMatrix4f rotation = new OpenMatrix4f().rotate((float) Math.toRadians(90), new Vec3f(1, 0, 0));
					OpenMatrix4f.transform3v(rotation, direction, direction);
					
					// emit the particle in the calculated direction, with some random velocity added
					container.getExecuter().getOriginal().level().addParticle(ParticleTypes.LARGE_SMOKE,
							(target_x),
							(target_y),
							(target_z),
							(float)(direction.x),
							(float)(direction.y),
							(float)(direction.z));
				}
				
				for (int i = 0; i < 24; i++) {
					container.getExecuter().getOriginal().level().addParticle(ParticleTypes.LARGE_SMOKE,
							target_x + ((new Random().nextFloat() - 0.5F)),
							target_y + 0.2F,
							target_z + ((new Random().nextFloat() - 0.5F)),
							0,
							((new Random().nextFloat()) * 1.5F),
							0);
				}
			}
		}
		if (container.getDataManager().getDataValue(WOMSkillDataKeys.SHOOT_COOLDOWN.get()) > 0) {
			if(!container.getExecuter().isLogicalClient()) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.SHOOT_COOLDOWN.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.SHOOT_COOLDOWN.get())-1,((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
		}
		if (container.getDataManager().getDataValue(WOMSkillDataKeys.DARKNESS_PORTAL_TIMER.get()) > 0) {
			if(!container.getExecuter().isLogicalClient()) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.DARKNESS_PORTAL_TIMER.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.DARKNESS_PORTAL_TIMER.get())-1,((ServerPlayerPatch)container.getExecuter()).getOriginal());
				LivingEntity target = (LivingEntity) container.getExecuter().getOriginal().level().getEntity(container.getDataManager().getDataValue(WOMSkillDataKeys.DARKNESS_TARGET.get()));
				if (target != null) {
					if (target.isDeadOrDying()) {
						container.getDataManager().setDataSync(WOMSkillDataKeys.DARKNESS_PORTAL_TIMER.get(), 0,((ServerPlayerPatch)container.getExecuter()).getOriginal());
					}
					if (container.getDataManager().getDataValue(WOMSkillDataKeys.DARKNESS_PORTAL_TIMER.get()) == 0) {
						container.getDataManager().setDataSync(WOMSkillDataKeys.DARKNESS_ACTIVATE_PORTAL.get(), false,((ServerPlayerPatch)container.getExecuter()).getOriginal());
						container.getDataManager().setDataSync(WOMSkillDataKeys.DARKNESS_TARGET_HITED.get(), false,((ServerPlayerPatch)container.getExecuter()).getOriginal());
					}
				}
			}
			if(container.getExecuter().isLogicalClient()) {
				Vec3f vec3 = container.getDataManager().getDataValue(WOMSkillDataKeys.DARKNESS_PORTAL_VEC.get());
				float dpx = vec3.x;
				float dpy = vec3.y;
				float dpz = vec3.z;
				
				// EFFET AU SOL
				
				int n = 10; // set the number of particles to emit
				double r = 0.35; // set the radius of the disk to 1
				double t = 0.01; // set the thickness of the disk to 0.1
				
				for (int i = 0; i < n; i++) {
				    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
				    double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness

				    // calculate the emission direction in Cartesian coordinates using the polar coordinates
				    double x = r * Math.cos(phi) * Math.cos(theta);
				    double y = r * Math.cos(phi) * Math.sin(theta);
				    double z = r * Math.sin(phi);
				    
				 // create a Vector3f object to represent the emission direction
				    Vec3f direction = new Vec3f((float)x * new Random().nextFloat(), (float)y * new Random().nextFloat(), (float)z * new Random().nextFloat());

				    // rotate the direction vector to align with the forward vector
				    OpenMatrix4f rotation = new OpenMatrix4f().rotate((float) Math.toRadians(90), new Vec3f(1, 0, 0));
				    OpenMatrix4f.transform3v(rotation, direction, direction);
				    
				    // emit the particle in the calculated direction, with some random velocity added
				    container.getExecuter().getOriginal().level().addParticle(ParticleTypes.SMOKE,
					        (dpx),
					        (dpy),
					        (dpz),
					        (float)(direction.x),
					        (float)(direction.y),
					        (float)(direction.z));
				}
				
				
				// FAILLE
				
				n = 20; // set the number of particles to emit
				r = 0.35; // set the radius of the disk to 1
				
				for (int i = 0; i < n; i++) {
					double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
				    double phi = Math.acos(2 * new Random().nextDouble() - 1); // generate a random polar angle in the upper hemisphere

				    // calculate the emission direction in Cartesian coordinates using the polar coordinates
				    double x = r * Math.sin(phi) * Math.cos(theta);
				    double y = r * Math.sin(phi) * Math.sin(theta);
				    double z = r * Math.cos(phi);
				    
				 // create a Vector3f object to represent the emission direction
				    Vec3f direction = new Vec3f((float)x * (new Random().nextFloat() * 2.0f), (float)y * (new Random().nextFloat() * 4.0f), (float)z * new Random().nextFloat()*2.0f );

				    // rotate the direction vector to align with the forward vector
				    OpenMatrix4f rotation = new OpenMatrix4f().rotate(container.getDataManager().getDataValue(WOMSkillDataKeys.DARKNESS_PORTAL_ANGLE.get()), new Vec3f(0, 1, 0));
				    OpenMatrix4f.transform3v(rotation, direction, direction);
				    
				    // emit the particle in the calculated direction, with some random velocity added
				    container.getExecuter().getOriginal().level().addParticle(ParticleTypes.SMOKE,
					        (dpx + direction.x),
					        (dpy + direction.y + 1.7f) ,
					        (dpz + direction.z),
					        (float)(0),
					        (float)(0),
					        (float)(0));
				}
			}
		}
		
		if (container.getDataManager().getDataValue(WOMSkillDataKeys.ZOOM_COOLDOWN.get()) > 0) {
			if(container.getExecuter().isLogicalClient()) {
				if (!container.getDataManager().getDataValue(WOMSkillDataKeys.ACTIVE.get())) {
					if (container.getDataManager().getDataValue(WOMSkillDataKeys.ZOOM.get())) {
						ClientEngine.getInstance().renderEngine.zoomIn();
					}
				}
			} else {
				container.getDataManager().setDataSync(WOMSkillDataKeys.ZOOM_COOLDOWN.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.ZOOM_COOLDOWN.get())-1,((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
		} else {
			if(container.getExecuter().isLogicalClient()) {
				ClientEngine.getInstance().renderEngine.zoomOut(0);
			}
		}
		
		if(!container.getExecuter().isLogicalClient()) {
			ServerPlayerPatch executer = (ServerPlayerPatch) container.getExecuter();
			int sweeping_edge = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal());
			
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.SHOOT.get()) && !container.getExecuter().getOriginal().isUsingItem() && (container.getExecuter().getEntityState().canBasicAttack() || container.getDataManager().getDataValue(WOMSkillDataKeys.DARKNESS_TARGET_HITED.get()) || (container.getDataManager().getDataValue(WOMSkillDataKeys.ACTIVE.get()) && container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) >= 40*20 || container.getExecuter().getOriginal().isCreative()))) {
				container.getExecuter().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
				container.getDataManager().setDataSync(WOMSkillDataKeys.SHOOT.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				if (container.getDataManager().getDataValue(WOMSkillDataKeys.ACTIVE.get())) {
					if (container.getDataManager().getDataValue(WOMSkillDataKeys.BLACKHOLE_TIMER.get()) == 0 && (container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) >= 80*20 || container.getExecuter().getOriginal().isCreative())) {
						if (!container.getExecuter().getOriginal().isCreative()) {
							container.getDataManager().setDataSync(WOMSkillDataKeys.TIMER.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) - 80*20, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
						}
						container.getExecuter().playAnimationSynchronized(WOMAnimations.ANTITHEUS_ASCENDED_BLACKHOLE, 0);
						container.getDataManager().setDataSync(WOMSkillDataKeys.BLACKHOLE_TIMER.get(), 140, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
						container.getDataManager().setDataSync(WOMSkillDataKeys.BLACKHOLE_ACTIVE.get(), true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
						
					}
				} else {
					
					// PULL
					
					LivingEntity target = (LivingEntity) container.getExecuter().getOriginal().level().getEntity(container.getDataManager().getDataValue(WOMSkillDataKeys.DARKNESS_TARGET.get()));
					if (target == null) {
						container.getDataManager().setDataSync(WOMSkillDataKeys.DARKNESS_TARGET_HITED.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
					}
					if (container.getDataManager().getDataValue(WOMSkillDataKeys.DARKNESS_TARGET_HITED.get())) {
						container.getDataManager().setDataSync(WOMSkillDataKeys.DARKNESS_TARGET_HITED.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
						
						if (container.getExecuter().getEntityState().canBasicAttack()) {
							container.getExecuter().playAnimationSynchronized(WOMAnimations.ANTITHEUS_PULL, 0);
						}
						
						
						EpicFightDamageSource damage = container.getExecuter().getDamageSource(WOMAnimations.ANTITHEUS_PULL, InteractionHand.MAIN_HAND);
						damage.setStunType(StunType.HOLD);
						container.getExecuter().getOriginal().level().playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
				    			SoundEvents.WITHER_BREAK_BLOCK, container.getExecuter().getOriginal().getSoundSource(), 1.5F, 2.0F);
						float WitherCatharsis = 0;
						if (target.hasEffect(MobEffects.WITHER)) {
							damage.setImpact(4f);
							damage.addRuntimeTag(EpicFightDamageType.WEAPON_INNATE);
							int wither_lvl = target.getEffect(MobEffects.WITHER).getAmplifier()+1;
							WitherCatharsis = (float) ((target.getEffect(MobEffects.WITHER).getDuration()/20) * ( wither_lvl == 0 ? 0.5f : wither_lvl));
							((ServerLevel) container.getExecuter().getOriginal().level()).sendParticles( ParticleTypes.SOUL, 
									target.getX(), 
									target.getY() + 1.2D, 
									target.getZ(), 
									48, 0.0D, 0.0D, 0.0D, 0.05D);
							container.getExecuter().getOriginal().level().playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
					    			SoundEvents.WITHER_HURT, container.getExecuter().getOriginal().getSoundSource(), 1.5F, 0.5F);
							if (target.getEffect(MobEffects.WITHER).getAmplifier() == 2) {
								((ServerLevel) container.getExecuter().getOriginal().level()).sendParticles( ParticleTypes.SOUL_FIRE_FLAME, 
										target.getX(), 
										target.getY() + 1.2D, 
										target.getZ(), 
										24, 0.0D, 0.0D, 0.0D, 0.05D);
								container.getExecuter().getOriginal().level().playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
						    			SoundEvents.WITHER_SKELETON_HURT, container.getExecuter().getOriginal().getSoundSource(), 1.5F, 0.5F);
							}

							Player original = container.getExecuter().getOriginal();
							
							if (container.getExecuter().getSkill(EpicFightSkills.DEATH_HARVEST) != null) {
								int damage2 = (int)original.getAttributeValue(Attributes.ATTACK_DAMAGE);
								for (int i = 0; i < wither_lvl*3; i++) {
									DeathHarvestOrb harvestOrb = new DeathHarvestOrb(original, target.getX(), target.getY() + target.getBbHeight() * 0.5D, target.getZ(), damage2);
									original.level().addFreshEntity(harvestOrb);
								}
							}
							container.getExecuter().getOriginal().heal(WitherCatharsis*0.2f);
							target.removeEffect(MobEffects.WITHER);
						}
						
						
						Vec3f vec3 = container.getDataManager().getDataValue(WOMSkillDataKeys.DARKNESS_PORTAL_VEC.get());
						float dpx = vec3.x;
						float dpy = vec3.y;
						float dpz = vec3.z;
						container.getDataManager().setDataSync(WOMSkillDataKeys.DARKNESS_TARGET_REAPED.get(), true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
						container.getDataManager().setDataSync(WOMSkillDataKeys.DARKNESS_ACTIVATE_PORTAL.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
						container.getDataManager().setDataSync(WOMSkillDataKeys.DARKNESS_PORTAL_TIMER.get(), 0, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
						if (target.hurt(damage,1 + ((target.getMaxHealth() - target.getHealth()) * 0.1F) + (WitherCatharsis * 0.8f))) {
							container.getExecuter().getEventListener().triggerEvents(EventType.DEALT_DAMAGE_EVENT_POST, new DealtDamageEvent((ServerPlayerPatch)container.getExecuter(), target, damage, 1 +((target.getMaxHealth() - target.getHealth()) * 0.1F) + (WitherCatharsis * 0.8f)));
							if (target.isAlive()) {
								target.teleportTo(dpx,(target instanceof Player ? 0:dpy - 100), dpz);
							}
						}
					} else {
						
						// SHOOT
						boolean hypervitality = false;
						if (container.getExecuter().getSkill(EpicFightSkills.HYPERVITALITY) != null) {
							hypervitality = true;
						}
						
						boolean forbidden_strength = false;
						if (container.getExecuter().getSkill(EpicFightSkills.FORBIDDEN_STRENGTH) != null) {
							forbidden_strength = true;
						}
						
						float initial_shoot_consumption = (float) (66.6f * 2f * (-(1f/(Math.sqrt(sweeping_edge+1)))+1));
						float ressource = container.getExecuter().getSkill(this).getResource();
						
						if (container.getStack() == 1) {
							ressource = 666f;
						}
						
						float StaminaConsumption = 0;
						float HealthConsumption = 0;
						float ressourceConsumption = 0;
						
						boolean canShoot = false;
						
						float ressource_after_consumption = ressource - initial_shoot_consumption;
						ressourceConsumption = initial_shoot_consumption;
						
						if (ressource_after_consumption < 0 && hypervitality) {
							ressource_after_consumption = executer.getStamina() + ressource_after_consumption/2;
							
							StaminaConsumption = ressource_after_consumption/2;
							ressourceConsumption = ressource;
							
							if (ressource_after_consumption < 0 && forbidden_strength) {
								ressource_after_consumption = executer.getOriginal().getHealth() + ressource_after_consumption;
								
								HealthConsumption = ressource_after_consumption;
								StaminaConsumption = executer.getStamina();
							}
						}

						if (ressource_after_consumption > 0) {
							canShoot = true;
						}
						
						if ((canShoot && container.getDataManager().getDataValue(WOMSkillDataKeys.SHOOT_COOLDOWN.get()) == 0) || container.getExecuter().getOriginal().isCreative()) {
							if (canShoot || container.getStack() == 1 || container.getExecuter().getOriginal().isCreative()) {
								if (!container.getExecuter().getOriginal().isCreative()) {
									Player player = executer.getOriginal();
									executer.setStamina(executer.getStamina() - StaminaConsumption);
									player.setHealth(player.getHealth() - HealthConsumption);
									this.setConsumptionSynchronize((ServerPlayerPatch) executer,ressource - ressourceConsumption);	
									
									if (HealthConsumption > 0) {
										player.level().playSound(null, player.getX(), player.getY(), player.getZ(), EpicFightSounds.FORBIDDEN_STRENGTH.get(), player.getSoundSource(), 1.0F, 1.0F);
										((ServerLevel)player.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, player.getX(), player.getY(0.5D), player.getZ(), (int)ressource_after_consumption, 0.1D, 0.0D, 0.1D, 0.2D);
									}
								}
								
								OpenMatrix4f transformMatrix =  new OpenMatrix4f();
								transformMatrix.translate(new Vec3f(0,0.0F,-3.0F));
								OpenMatrix4f correction = new OpenMatrix4f().rotate(-(float) Math.toRadians(container.getExecuter().getOriginal().getViewYRot(1) + 180), new Vec3f(0, 1, 0));
								OpenMatrix4f.mul(correction,transformMatrix,transformMatrix);

								float dpx = transformMatrix.m30 + (float) container.getExecuter().getOriginal().getX();
								float dpy = (float) container.getExecuter().getOriginal().getY();
								float dpz = transformMatrix.m32 + (float) container.getExecuter().getOriginal().getZ();
								while (!container.getExecuter().getOriginal().level().isEmptyBlock(new BlockPos.MutableBlockPos(dpx,dpy,dpz))) {
									dpy++;
								}
								
								Vec3f vec = new Vec3f(dpx, dpy, dpz);
								
								container.getDataManager().setDataSync(WOMSkillDataKeys.DARKNESS_PORTAL_VEC.get(), vec, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
								container.getDataManager().setDataSync(WOMSkillDataKeys.DARKNESS_PORTAL_ANGLE.get(), -(float) Math.toRadians(container.getExecuter().getOriginal().getViewYRot(1) + 180F), ((ServerPlayerPatch)container.getExecuter()).getOriginal());
								container.getDataManager().setDataSync(WOMSkillDataKeys.SHOOT_COOLDOWN.get(), 6*20, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
								container.getExecuter().playAnimationSynchronized(WOMAnimations.ANTITHEUS_SHOOT, 0);
							}
						}
					}
					container.getDataManager().setDataSync(WOMSkillDataKeys.ZOOM_COOLDOWN.get(), 40, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(WOMSkillDataKeys.ZOOM.get(), true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				}
			} else {
				if (container.getExecuter().getOriginal().isUsingItem()) {
					container.getExecuter().getOriginal().setSprinting(false);
					container.getDataManager().setDataSync(WOMSkillDataKeys.ZOOM_COOLDOWN.get(), 40, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				}
			}
		}
		if(!container.getExecuter().isLogicalClient()) {
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.BLACKHOLE_TIMER.get()) > 0) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.BLACKHOLE_TIMER.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.BLACKHOLE_TIMER.get())-1,((ServerPlayerPatch)container.getExecuter()).getOriginal());
				if (container.getDataManager().getDataValue(WOMSkillDataKeys.BLACKHOLE_ACTIVE.get())) {
					if (container.getDataManager().getDataValue(WOMSkillDataKeys.BLACKHOLE_TIMER.get()) == 100) {
						container.getExecuter().getOriginal().level().playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
								WOMSounds.ANTITHEUS_BLACKKHOLE.get(), container.getExecuter().getOriginal().getSoundSource(), 0.8F, 0.9F);
					}
					if (container.getDataManager().getDataValue(WOMSkillDataKeys.BLACKHOLE_TIMER.get()) < 100 && container.getDataManager().getDataValue(WOMSkillDataKeys.BLACKHOLE_TIMER.get()) > 0) {
						Vec3 blackhole_pos = container.getDataManager().getDataValue(WOMSkillDataKeys.BLACKHOLE_VEC.get()).toDoubleVector();
						AABB box = AABB.ofSize(blackhole_pos,50, 50, 50);
						
						List<Entity> list = container.getExecuter().getOriginal().level().getEntities(container.getExecuter().getOriginal(),box);
						
						for (Entity entity : list) {
							if (!(entity instanceof ArmorStand)) {
								double distance_to_target = Math.sqrt(Math.pow(blackhole_pos.x() - entity.getX(), 2) + Math.pow(blackhole_pos.z() - entity.getZ(), 2) + Math.pow(blackhole_pos.y() - entity.getY(), 2));
								double power = -1.00 / (0.4 + (distance_to_target*0.2));
								double d1 = blackhole_pos.x() - entity.getX();
								double d2 = blackhole_pos.y()-1 - entity.getY();
								double d0;
								
								if (container.getDataManager().getDataValue(WOMSkillDataKeys.BLACKHOLE_TIMER.get()) % 2 != 0) {
									power = 0;
								}
								
								if (container.getDataManager().getDataValue(WOMSkillDataKeys.BLACKHOLE_TIMER.get()) == 1) {
									power = 1.0;
									d2 = blackhole_pos.y()-1.1 - entity.getY();
									if (entity instanceof ItemEntity || entity instanceof ExperienceOrb) {
										power = 0.1;
									}
								}
								for (d0 = blackhole_pos.z() - entity.getZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
									d1 = (Math.random() - Math.random()) * 0.01D;
								}
								
								if (entity instanceof LivingEntity) {
									power *= 1.0D - ((LivingEntity) entity).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
								}
								
								entity.hasImpulse = true;
								Vec3 vec3 = entity.getDeltaMovement();
								Vec3 vec31 = (new Vec3(d1, d2, d0)).normalize().scale(power);
								
								if (container.getDataManager().getDataValue(WOMSkillDataKeys.BLACKHOLE_TIMER.get()) % 10 == 0 && entity instanceof LivingEntity && distance_to_target <= 10) {
									LivingEntity target = (LivingEntity) entity;
									EpicFightDamageSource damage = container.getExecuter().getDamageSource(WOMAnimations.ANTITHEUS_PULL, InteractionHand.MAIN_HAND);
									int chance = Math.abs(new Random().nextInt()) % 100;
									int sweping = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal());
									if (chance < 20f + (sweping*10f) ) {
										if (target.hasEffect(MobEffects.WITHER)) {
											target.removeEffect(MobEffects.WITHER);
											target.addEffect(new MobEffectInstance(MobEffects.WITHER, (6 + (2 * EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal()))) *20, 2, false, true));
										} else {
											target.addEffect(new MobEffectInstance(MobEffects.WITHER, (6 + (2 * EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal()))) *20, 1, false, true));
										}
									}
									target.hurt(damage,1);
								}
								
								if (container.getDataManager().getDataValue(WOMSkillDataKeys.BLACKHOLE_TIMER.get()) == 1 && entity instanceof LivingEntity) {
									LivingEntity target = (LivingEntity) entity;
									EpicFightDamageSource damage = container.getExecuter().getDamageSource(WOMAnimations.ANTITHEUS_PULL, InteractionHand.MAIN_HAND);
									damage.setStunType(StunType.HOLD);
									float WitherCatharsis = 0;
									if (target.hasEffect(MobEffects.WITHER)) {
										damage.setImpact(2.5f);
										int wither_lvl = target.getEffect(MobEffects.WITHER).getAmplifier()+1;
										WitherCatharsis = (float) ((target.getEffect(MobEffects.WITHER).getDuration()/20) * ( wither_lvl == 0 ? 0.5f : wither_lvl));
										((ServerLevel) container.getExecuter().getOriginal().level()).sendParticles( ParticleTypes.SOUL, 
												target.getX(), 
												target.getY() + 1.2D, 
												target.getZ(), 
												48, 0.0D, 0.0D, 0.0D, 0.05D);
										container.getExecuter().getOriginal().level().playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
								    			SoundEvents.WITHER_HURT, container.getExecuter().getOriginal().getSoundSource(), 1.5F, 0.5F);
										((ServerLevel) container.getExecuter().getOriginal().level()).sendParticles( ParticleTypes.SOUL_FIRE_FLAME, 
													target.getX(), 
													target.getY() + 1.2D, 
													target.getZ(), 
													(target.getEffect(MobEffects.WITHER).getAmplifier()+1)*4, 0.0D, 0.0D, 0.0D, 0.05D);
										container.getExecuter().getOriginal().level().playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
									    			SoundEvents.WITHER_SKELETON_HURT, container.getExecuter().getOriginal().getSoundSource(), 1.5F, 0.5F);
										target.removeEffect(MobEffects.WITHER);
									}
									target.hurt(damage,4 + (WitherCatharsis));
								}

								entity.setDeltaMovement(vec3.x / 2.0D - vec31.x, vec3.y / 2.0D - vec31.y, vec3.z / 2.0D - vec31.z);
							}
						}
					}
				}
			}
		}
		if (container.getDataManager().getDataValue(WOMSkillDataKeys.ACTIVE.get())) {
			if(!container.getExecuter().isLogicalClient()) {
				if (container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) > 0) {
					if (!container.getExecuter().getOriginal().isCreative()) {
						container.getDataManager().setData(WOMSkillDataKeys.TIMER.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get())-1);
					}
					container.getExecuter().getOriginal().addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,5, 0,true,false,false));
					container.getExecuter().getOriginal().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,5, 1,true,false,false));
					if (!container.getExecuter().getSkill(SkillSlots.WEAPON_PASSIVE).isEmpty()) {
						if (!container.getExecuter().getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().getDataValue(WOMSkillDataKeys.PARTICLE.get())) {
							container.getExecuter().getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(WOMSkillDataKeys.PARTICLE.get(), true, (ServerPlayer) container.getExecuter().getOriginal());					
						}
					}
					this.setDurationSynchronize((ServerPlayerPatch) container.getExecuter(),(int)(container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get())));
					if (container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) == 0) {
						container.getExecuter().playAnimationSynchronized(WOMAnimations.ANTITHEUS_LAPSE, 0);
						cancelOnServer((ServerPlayerPatch)container.getExecuter(), null);
						container.deactivate();
						((ServerPlayerPatch)container.getExecuter()).modifyLivingMotionByCurrentItem();
					}
				}
			}
		} else {
			if(!container.getExecuter().isLogicalClient()) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.WITHERCATHARSIS.get(), false ,((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
		}
		
		if (container.getDataManager().getDataValue(WOMSkillDataKeys.SUPERARMOR.get())) {
			container.getExecuter().getOriginal().addEffect(new MobEffectInstance(EpicFightMobEffects.STUN_IMMUNITY.get(), 5, 0,true,false,false));
		}
	}

	@Override
	public WeaponInnateSkill registerPropertiesToAnimation() {
		return this;
	}
}