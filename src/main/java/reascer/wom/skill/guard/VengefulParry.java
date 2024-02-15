package reascer.wom.skill.guard;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import org.joml.Vector3f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.WOMColliders;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.skill.WOMSkillDataKeys;
import reascer.wom.world.capabilities.item.WOMWeaponCategories;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.entity.eventlistener.HurtEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class VengefulParry extends GuardSkill {
	private static final UUID EVENT_UUID = UUID.fromString("802e2116-02fa-4746-937d-a89429a84113");
	
	public static GuardSkill.Builder createCounterAttackBuilder() {
		return GuardSkill.createGuardBuilder();
	}
	
	public VengefulParry(GuardSkill.Builder builder) {
		super(builder);
	}
	
	public void setPenalizer(float penalizer) {
		this.penalizer = penalizer;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		
		container.getExecuter().getEventListener().removeListener(EventType.CLIENT_ITEM_USE_EVENT, GuardSkill.EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.SERVER_ITEM_USE_EVENT, GuardSkill.EVENT_UUID);
		
		container.getExecuter().getEventListener().addEventListener(EventType.SERVER_ITEM_USE_EVENT, VengefulParry.EVENT_UUID, (event) -> {
			CapabilityItem itemCapability = event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND);
			
			// Guard triggerd
			boolean flag1 = !(event.getPlayerPatch().getOriginal().isFallFlying() || event.getPlayerPatch().currentLivingMotion == LivingMotions.FALL || !event.getPlayerPatch().getEntityState().canUseSkill() || !event.getPlayerPatch().getEntityState().canBasicAttack());
			if (this.isHoldingWeaponAvailable(event.getPlayerPatch(), itemCapability, BlockType.GUARD) && flag1 && event.getPlayerPatch().getStamina() > 0 && container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) == 0 && container.getDataManager().getDataValue(WOMSkillDataKeys.COOLDOWN.get()) == 0) {
				StaticAnimation animation;
				switch (new Random().nextInt() %3) {
				case 0: {
					animation = Animations.SWORD_GUARD_ACTIVE_HIT1;
					break;
				}
				case 1: {
					animation = Animations.SWORD_GUARD_ACTIVE_HIT2;
					break;
				}
				case 2: {
					animation = Animations.SWORD_GUARD_ACTIVE_HIT3;
					break;
				}
				default:
					animation = Animations.SWORD_GUARD_ACTIVE_HIT1;
				}
				
				if (itemCapability.getStyle(event.getPlayerPatch()) == Styles.TWO_HAND) {
					animation = new Random().nextBoolean() ? Animations.LONGSWORD_GUARD_ACTIVE_HIT1 : Animations.LONGSWORD_GUARD_ACTIVE_HIT2;
				}
				
				if (itemCapability.getWeaponCollider() == ColliderPreset.SPEAR) {
					animation = Animations.SPEAR_GUARD_HIT;
				}
				
				if (itemCapability.getWeaponCollider() == ColliderPreset.UCHIGATANA) {
					animation = Animations.UCHIGATANA_GUARD_HIT;
				}
				
				if (itemCapability.getWeaponCollider() == WOMColliders.STAFF) {
					animation = Animations.SPEAR_GUARD_HIT;
				}
				float convert = -0.05F;
				
				if (itemCapability.getWeaponCollider() == WOMColliders.AGONY) {
					animation = WOMAnimations.AGONY_GUARD_HIT_1;
				}
				
				if (itemCapability.getWeaponCollider() == WOMColliders.RUINE) {
					animation = Animations.LONGSWORD_GUARD_HIT;
				}
				
				if (itemCapability.getWeaponCollider() == WOMColliders.KATANA) {
					animation = WOMAnimations.KATANA_GUARD_HIT;
					convert = -0.15F;
				}
				
				if (itemCapability.getWeaponCollider() == WOMColliders.HERSCHER && itemCapability.getStyle(event.getPlayerPatch()) == Styles.OCHS) {
					animation = WOMAnimations.HERRSCHER_GUARD_PARRY;
				}
				
				if (itemCapability.getWeaponCollider() == WOMColliders.MOONLESS) {
					animation = WOMAnimations.MOONLESS_GUARD_HIT_1;
				}
				
				if(!event.getPlayerPatch().consumeStamina(8)){
					event.getPlayerPatch().setStamina(0);
				}
				event.getPlayerPatch().getOriginal().level().playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
		    			EpicFightSounds.CLASH.get(), container.getExecuter().getOriginal().getSoundSource(), 1.0F, 2.0F);
				event.getPlayerPatch().playAnimationSynchronized(animation, convert);
				event.getPlayerPatch().currentLivingMotion = LivingMotions.BLOCK;
				
				container.getDataManager().setDataSync(WOMSkillDataKeys.TIMER.get(), 40, event.getPlayerPatch().getOriginal());
				container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGE.get(),0 , event.getPlayerPatch().getOriginal());
				container.getDataManager().setDataSync(WOMSkillDataKeys.HOLDING_STANCE.get(),true , event.getPlayerPatch().getOriginal());
				
			} else {
				if (this.isHoldingWeaponAvailable(event.getPlayerPatch(), itemCapability, BlockType.GUARD) && event.getPlayerPatch().getEntityState().canBasicAttack() && container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) == 0) {
					event.getPlayerPatch().getOriginal().level().playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
							SoundEvents.LAVA_EXTINGUISH, container.getExecuter().getOriginal().getSoundSource(), 1.0F, 2.0F);
				}
			}
			
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.MODIFY_DAMAGE_EVENT, VengefulParry.EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGE.get()) > 0) {
				event.setDamage(container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGE.get()));
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_POST, VengefulParry.EVENT_UUID, (event) -> {
			if (event.getDamageSource().getAnimation() instanceof AttackAnimation && container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGE.get()) > 0) {
				ServerPlayerPatch entitypatch = event.getPlayerPatch();
				AttackAnimation anim = ((AttackAnimation) event.getDamageSource().getAnimation());
				AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(event.getDamageSource().getAnimation());
				float elapsedTime = player.getElapsedTime();
				Phase phase = anim.getPhaseByTime(elapsedTime);
				
				if (phase == anim.phases[0]) {
					((ServerLevel) container.getExecuter().getOriginal().level()).playSound(null,
							event.getTarget().getX(),
							event.getTarget().getY(),
							event.getTarget().getZ(),
			    			SoundEvents.CREEPER_DEATH, event.getTarget().getSoundSource(), 2.0F, 0.5f);
					
					for (int i = 0; i < (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGE.get())+1); i++) {
						((ServerLevel) event.getPlayerPatch().getOriginal().level()).sendParticles(new DustParticleOptions(new Vector3f(0.0f,0.0f,0f),1.0f),
								event.getTarget().getX() + (new Random().nextFloat()-0.5f)*3,
								event.getTarget().getY() + event.getTarget().getBbHeight()/2,
								event.getTarget().getZ() + (new Random().nextFloat()-0.5f)*3,
								2,
								0,
								0,
								0,
								0.0);
					}
					
					container.getDataManager().setDataSync(WOMSkillDataKeys.VENGENCE_DURATION.get(), 5, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				} else {
					container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGE.get(), 0, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				}
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_PRE, VengefulParry.EVENT_UUID, (event) -> {
			CapabilityItem itemCapability = event.getPlayerPatch().getHoldingItemCapability(event.getPlayerPatch().getOriginal().getUsedItemHand());
			
			if (super.isHoldingWeaponAvailable(event.getPlayerPatch(), itemCapability, BlockType.GUARD) && container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) > 0) {
				float impact = 0.5F;
				float knockback = 0.25F;
				guard(container, itemCapability, event, knockback, impact, false);
			}
		},0);
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, VengefulParry.EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) > 0) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.COOLDOWN.get(), 20, event.getPlayerPatch().getOriginal());
			}
			container.getDataManager().setDataSync(WOMSkillDataKeys.TIMER.get(),0 , event.getPlayerPatch().getOriginal());
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.ATTACK_ANIMATION_END_EVENT, VengefulParry.EVENT_UUID, (event) -> {
			container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGE.get(), 0, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_PRE, EVENT_UUID,0);
		container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.CLIENT_ITEM_USE_EVENT, EVENT_UUID);
		super.onRemoved(container);
	}
	
	@Override
	public void guard(SkillContainer container, CapabilityItem itemCapability, HurtEvent.Pre event, float knockback, float impact, boolean advanced) {
		if (this.isHoldingWeaponAvailable(event.getPlayerPatch(), itemCapability, BlockType.GUARD)) {
			DamageSource damageSource = event.getDamageSource();
			
			if (this.isBlockableSource(damageSource, true)) {
				event.getPlayerPatch().playSound(EpicFightSounds.CLASH.get(), -0.05F, 0.1F);
				ServerPlayer serveerPlayer = event.getPlayerPatch().getOriginal();
				EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument(((ServerLevel)serveerPlayer.level()), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, serveerPlayer, damageSource.getDirectEntity());
				
				container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGE.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGE.get()) + (int) event.getAmount(), serveerPlayer);
				this.dealEvent(event.getPlayerPatch(), event,advanced);
				return;
			}
		}
		
		//super.guard(container, itemCapability, event, knockback, impact, false);
	}
	
	@Override
	protected boolean isBlockableSource(DamageSource damageSource, boolean advanced) {
		return (damageSource.is(DamageTypes.MOB_PROJECTILE) && advanced) || super.isBlockableSource(damageSource, false);
	}
	
	@Nullable
	protected StaticAnimation getGuardMotion(PlayerPatch<?> playerpatch, CapabilityItem itemCapability, BlockType blockType) {
		if (blockType == BlockType.GUARD) {
			if (itemCapability.getWeaponCollider() == WOMColliders.AGONY) {
				return new Random().nextBoolean() ? WOMAnimations.AGONY_GUARD_HIT_1 : WOMAnimations.AGONY_GUARD_HIT_2;
			}
			
			if (itemCapability.getWeaponCollider() == WOMColliders.KATANA) {
				return WOMAnimations.KATANA_GUARD_HIT;
			}			
			
			if (itemCapability.getWeaponCollider() == WOMColliders.HERSCHER && itemCapability.getStyle(playerpatch) == Styles.OCHS) {
				return WOMAnimations.HERRSCHER_GUARD_HIT;
			}
			
			if (itemCapability.getWeaponCollider() == WOMColliders.MOONLESS) {
				switch (new Random().nextInt() % 3) {
					case 0: {
						return WOMAnimations.MOONLESS_GUARD_HIT_1;
					}
					case 1: {
						return WOMAnimations.MOONLESS_GUARD_HIT_2;
					}
					case 2: {
						return WOMAnimations.MOONLESS_GUARD_HIT_3;
					}
					
					default:
						return WOMAnimations.MOONLESS_GUARD_HIT_1;
				}
			}
		}
		

		return super.getGuardMotion(playerpatch, itemCapability, blockType);
	}
	
	@Override
	public Skill getPriorSkill() {
		return WOMSkills.PERFECT_BULWARK;
	}
	
	@Override
	protected boolean isAdvancedGuard() {
		return true;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldDraw(SkillContainer container) {
		return container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGE.get()) > 0 || container.getDataManager().getDataValue(WOMSkillDataKeys.COOLDOWN.get()) > 0;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y) {
		PoseStack poseStack = guiGraphics.pose();
		poseStack.pushPose();
		poseStack.translate(0, (float)gui.getSlidingProgression(), 0);
		RenderSystem.setShaderTexture(0, this.getSkillTexture());
		
		if (container.getDataManager().getDataValue(WOMSkillDataKeys.COOLDOWN.get()) > 0) {
			RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 0.5F);
		} else {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}
		
		guiGraphics.blit(this.getSkillTexture(), (int)x, (int)y, 24, 24, 0, 0, 1, 1, 1, 1);
		
		String damage = String.valueOf(container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGE.get()));
		if (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGE.get()) == 0) {
			damage = "";
		}
		guiGraphics.drawString(gui.font, damage, x+5, y+6, 16777215,true);
		
		poseStack.popPose();
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<Object> getTooltipArgsOfScreen(List<Object> list) {
		list.clear();
		list.add(String.format("%s, %s, %s, %s, %s, %s, %s, %s", WeaponCategories.UCHIGATANA, WeaponCategories.LONGSWORD, WeaponCategories.SWORD, WeaponCategories.TACHI, WeaponCategories.SPEAR, WOMWeaponCategories.AGONY , WOMWeaponCategories.RUINE, WOMWeaponCategories.STAFF).toLowerCase());
		return list;
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		super.updateContainer(container);
		if(!container.getExecuter().isLogicalClient()) {
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) > 0) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.TIMER.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get())-1, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				if (container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) > 1) {
					container.getExecuter().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
				}
				if (container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) > 5) {
					container.getExecuter().getOriginal().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5 , 10, true,false,false));
				}
				if (container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) == 0 && container.getDataManager().getDataValue(WOMSkillDataKeys.HOLDING_STANCE.get())) {
					container.getDataManager().setDataSync(WOMSkillDataKeys.COOLDOWN.get(), 40, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(WOMSkillDataKeys.HOLDING_STANCE.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
					container.getExecuter().getOriginal().stopUsingItem();
				}
			}
		} else {
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) > 1) {
				container.getExecuter().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
			}
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) == 0 && container.getDataManager().getDataValue(WOMSkillDataKeys.HOLDING_STANCE.get())) {
				container.getDataManager().setData(WOMSkillDataKeys.HOLDING_STANCE.get(), false);
				container.getExecuter().getOriginal().stopUsingItem();
			}
		}
		
		if (container.getDataManager().getDataValue(WOMSkillDataKeys.COOLDOWN.get()) > 0) {
			container.getDataManager().setData(WOMSkillDataKeys.COOLDOWN.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.COOLDOWN.get())-1);
		}
		
		if (container.getDataManager().getDataValue(WOMSkillDataKeys.VENGENCE_DURATION.get()) > 0) {
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.VENGENCE_DURATION.get()) == 1) {
				if(!container.getExecuter().isLogicalClient()) {
					container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGE.get(), 0, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				}
			}
			container.getDataManager().setData(WOMSkillDataKeys.VENGENCE_DURATION.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.VENGENCE_DURATION.get())-1);
		}
	}
}