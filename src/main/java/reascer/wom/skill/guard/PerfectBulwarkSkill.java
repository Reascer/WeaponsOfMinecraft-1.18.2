package reascer.wom.skill.guard;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.WOMColliders;
import reascer.wom.world.capabilities.item.WOMWeaponCategories;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.skill.guard.GuardSkill.BlockType;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.IndirectEpicFightDamageSource;
import yesman.epicfight.world.damagesource.SourceTags;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.HurtEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class PerfectBulwarkSkill extends GuardSkill {
	private static final UUID EVENT_UUID = UUID.fromString("8665b153-4bc3-4480-adb4-96bdd66e35e6");
	private static final SkillDataKey<Boolean> PARRYING = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	private static final SkillDataKey<Integer> CHARGE = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	private static final SkillDataKey<Integer> COOLDOWN = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	
	public static GuardSkill.Builder createCounterAttackBuilder() {
		return GuardSkill.createGuardBuilder()
			.addAdvancedGuardMotion(WeaponCategories.SWORD, (itemCap, playerpatch) -> itemCap.getStyle(playerpatch) == Styles.ONE_HAND ?
				Animations.SWORD_AUTO3 :
					itemCap.getStyle(playerpatch) == Styles.OCHS ? WOMAnimations.HERRSCHER_TRANE : Animations.SWORD_DUAL_AUTO3)
			.addAdvancedGuardMotion(WeaponCategories.LONGSWORD, (itemCap, playerpatch) ->
				Animations.LONGSWORD_DASH)
			.addAdvancedGuardMotion(WeaponCategories.TACHI, (itemCap, playerpatch) ->
				Animations.TACHI_DASH)
			.addAdvancedGuardMotion(WeaponCategories.SPEAR, (itemCap, playerpatch) ->
				Animations.SPEAR_DASH)
			.addAdvancedGuardMotion(WOMWeaponCategories.AGONY, (itemCap, playerpatch) ->
				WOMAnimations.AGONY_CLAWSTRIKE)
			.addAdvancedGuardMotion(WOMWeaponCategories.RUINE, (itemCap, playerpatch) ->
				WOMAnimations.RUINE_COUNTER)
			.addAdvancedGuardMotion(WOMWeaponCategories.STAFF, (itemCap, playerpatch) ->
				WOMAnimations.STAFF_DASH)
			.addAdvancedGuardMotion(WeaponCategories.UCHIGATANA, (itemCap, playerpatch) ->
				WOMAnimations.KATANA_SHEATHED_DASH )
			;
	}
	
	public PerfectBulwarkSkill(GuardSkill.Builder builder) {
		super(builder);
	}
	
	public void setPenalizer(float penalizer) {
		this.penalizer = penalizer;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		container.getDataManager().registerData(PARRYING);
		container.getDataManager().registerData(CHARGE);
		container.getDataManager().registerData(COOLDOWN);
		
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_PRE, GuardSkill.EVENT_UUID,1);
		container.getExecuter().getEventListener().removeListener(EventType.CLIENT_ITEM_USE_EVENT, GuardSkill.EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.SERVER_ITEM_USE_EVENT, GuardSkill.EVENT_UUID);
		
		container.getExecuter().getEventListener().addEventListener(EventType.CLIENT_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
			CapabilityItem itemCapability = event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND);
			
			if (this.isHoldingWeaponAvailable(event.getPlayerPatch(), itemCapability, BlockType.GUARD) && container.getDataManager().getDataValue(COOLDOWN) == 0) {
				event.getPlayerPatch().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.SERVER_ITEM_USE_EVENT, PerfectBulwarkSkill.EVENT_UUID, (event) -> {
			CapabilityItem itemCapability = event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND);
			
			if (this.isHoldingWeaponAvailable(event.getPlayerPatch(), itemCapability, BlockType.GUARD) && container.getDataManager().getDataValue(COOLDOWN) == 0) {
				event.getPlayerPatch().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
				container.getDataManager().setDataSync(PARRYING, true, event.getPlayerPatch().getOriginal());
			} else {
				if (this.isHoldingWeaponAvailable(event.getPlayerPatch(), itemCapability, BlockType.GUARD) && event.getPlayerPatch().getEntityState().canBasicAttack() && container.getDataManager().getDataValue(COOLDOWN) > 0) {
					event.getPlayerPatch().getOriginal().level.playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
							SoundEvents.LAVA_EXTINGUISH, container.getExecuter().getOriginal().getSoundSource(), 1.0F, 2.0F);
				}
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.SERVER_ITEM_STOP_EVENT, PerfectBulwarkSkill.EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(PARRYING) && container.getDataManager().getDataValue(CHARGE) >= 5) {
				container.getDataManager().setDataSync(PARRYING, false, event.getPlayerPatch().getOriginal());
				container.getDataManager().setDataSync(COOLDOWN, 100, event.getPlayerPatch().getOriginal());
				CapabilityItem itemCapability = event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND);
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
				
				float convert = 0.15F;
				
				if (itemCapability.getWeaponCollider() == WOMColliders.AGONY) {
					animation = WOMAnimations.AGONY_GUARD_HIT_1;
				}
				
				if (itemCapability.getWeaponCollider() == WOMColliders.RUINE) {
					animation = Animations.LONGSWORD_GUARD_HIT;
				}
				
				if (itemCapability.getWeaponCollider() == WOMColliders.KATANA) {
					animation = WOMAnimations.KATANA_GUARD_HIT;
					convert = -0.05F;
				}
				
				if (itemCapability.getWeaponCollider() == WOMColliders.HERSCHER && itemCapability.getStyle(event.getPlayerPatch()) == Styles.OCHS) {
					animation = WOMAnimations.HERRSCHER_GUARD_PARRY;
				}
				
				if (itemCapability.getWeaponCollider() == WOMColliders.MOONLESS) {
					animation = WOMAnimations.MOONLESS_GUARD_HIT_1;
				}
				
				if(!event.getPlayerPatch().consumeStamina(3)){
					event.getPlayerPatch().setStamina(0);
				}
				
				event.getPlayerPatch().getOriginal().level.playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
						EpicFightSounds.CLASH.get(), container.getExecuter().getOriginal().getSoundSource(), 1.0F, 0.5F);
				((ServerLevel) event.getPlayerPatch().getOriginal().level).sendParticles(ParticleTypes.CLOUD, 
						container.getExecuter().getOriginal().getX(), 
						container.getExecuter().getOriginal().getY() + 0.75D, 
						container.getExecuter().getOriginal().getZ(), 
						60, 0.0D, 0.0D, 0.0D, 0.5D);
				
				event.getPlayerPatch().playAnimationSynchronized(animation, convert);
				
				AABB box = AABB.ofSize(event.getPlayerPatch().getOriginal().position(),10, 5, 10);
				
				List<Entity> list = container.getExecuter().getOriginal().level.getEntities(container.getExecuter().getOriginal(),box);
				
				for (Entity entity : list) {
					double power = 1.00;
					double d1 = event.getPlayerPatch().getOriginal().position().x() - entity.getX();
					double d2 = event.getPlayerPatch().getOriginal().position().y()-1 - entity.getY();
					double d0;
					
					for (d0 = event.getPlayerPatch().getOriginal().position().z() - entity.getZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
						d1 = (Math.random() - Math.random()) * 0.01D;
					}
					
					if (entity instanceof LivingEntity) {
						power *= 1.0D - ((LivingEntity) entity).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
						entity.hasImpulse = true;
						Vec3 vec3 = entity.getDeltaMovement();
						Vec3 vec31 = (new Vec3(d1, d2, d0)).normalize().scale(power);
						IndirectEpicFightDamageSource damage = (IndirectEpicFightDamageSource) new IndirectEpicFightDamageSource("perfect_bulkwark_shockwave", container.getExecuter().getOriginal(), container.getExecuter().getOriginal(), StunType.LONG);
						damage.setImpact(3.0f);
						LivingEntity target = (LivingEntity) entity;
						target.hurt(damage,container.getDataManager().getDataValue(CHARGE)*2);
						entity.setDeltaMovement(vec3.x / 2.0D - vec31.x, vec3.y / 2.0D - vec31.y, vec3.z / 2.0D - vec31.z);
					}
				}
				container.getDataManager().setDataSync(CHARGE, 0, event.getPlayerPatch().getOriginal());
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_PRE, EVENT_UUID, (event) -> {
			CapabilityItem itemCapability = event.getPlayerPatch().getHoldingItemCapability(event.getPlayerPatch().getOriginal().getUsedItemHand());
			
			if (this.isHoldingWeaponAvailable(event.getPlayerPatch(), itemCapability, BlockType.GUARD) && event.getPlayerPatch().getOriginal().isUsingItem()) {
				container.getDataManager().setDataSync(CHARGE,1 + container.getDataManager().getDataValue(CHARGE) , event.getPlayerPatch().getOriginal());
				
				if (container.getDataManager().getDataValue(CHARGE) >= 5){
					((ServerLevel) container.getExecuter().getOriginal().level).sendParticles(new DustParticleOptions(new Vector3f(0.8f,0.75f,0.65f), 1.0F), 
							container.getExecuter().getOriginal().getX() - 0.2D, 
							container.getExecuter().getOriginal().getY() + 1.3D, 
							container.getExecuter().getOriginal().getZ() - 0.2D, 
							30, 0.6D, 0.8D, 0.6D, 0.05);
				}
				float impact = 0.5F;
				float knockback = 0.25F;
				
				if (event.getDamageSource() instanceof EpicFightDamageSource epicfightDamageSource) {
					if (epicfightDamageSource.hasTag(SourceTags.GUARD_PUNCTURE)) {
						return;
					}
					
					impact = ((EpicFightDamageSource)event.getDamageSource()).getImpact();
					knockback += Math.min(impact * 0.1F, 1.0F);
				}
				
				this.guard(container, itemCapability, event, knockback, impact, false);
			}
		}, 0);
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_PRE, EVENT_UUID,0);
		container.getExecuter().getEventListener().removeListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.CLIENT_ITEM_USE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.SERVER_ITEM_STOP_EVENT, EVENT_UUID);
		super.onRemoved(container);
	}
	
	@Override
	public void guard(SkillContainer container, CapabilityItem itemCapability, HurtEvent.Pre event, float knockback, float impact, boolean advanced) {
		DamageSource damageSource = event.getDamageSource();
		
		if (this.isBlockableSource(damageSource, advanced)) {
			event.getPlayerPatch().getOriginal().level.playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
					EpicFightSounds.CLASH.get(), container.getExecuter().getOriginal().getSoundSource(), 1.0F, 0.9F + (0.15f * container.getDataManager().getDataValue(CHARGE)));
			
			ServerPlayer serveerPlayer = event.getPlayerPatch().getOriginal();
			EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument(((ServerLevel)serveerPlayer.level), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, serveerPlayer, damageSource.getDirectEntity());
			
			if (damageSource.getDirectEntity() instanceof LivingEntity) {
				knockback += EnchantmentHelper.getKnockbackBonus((LivingEntity)damageSource.getDirectEntity()) * 0.1F;
			}
			
			float penalty = container.getDataManager().getDataValue(PENALTY) + this.getPenalizer(itemCapability);
			event.getPlayerPatch().knockBackEntity(damageSource.getDirectEntity().position(), knockback);
			boolean enoughStamina = event.getPlayerPatch().consumeStamina(penalty * impact);
			container.getDataManager().setDataSync(PENALTY, penalty, event.getPlayerPatch().getOriginal());
			BlockType blockType = enoughStamina ? BlockType.GUARD : BlockType.GUARD_BREAK;
			StaticAnimation animation = this.getGuardMotion(event.getPlayerPatch(), itemCapability, blockType);
			
			if (animation != null) {
				event.getPlayerPatch().playAnimationSynchronized(animation, 0.0F);
			}
			
			if (blockType == BlockType.GUARD_BREAK) {
				event.getPlayerPatch().playSound(EpicFightSounds.NEUTRALIZE_MOBS.get(), 3.0F, 0.0F, 0.1F);
			}
			
			this.dealEvent(event.getPlayerPatch(), event, advanced);
		}
	}
	
	@Override
	protected boolean isBlockableSource(DamageSource damageSource, boolean advanced) {
		return (damageSource.isProjectile() && advanced) || super.isBlockableSource(damageSource, false);
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
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldDraw(SkillContainer container) {
		return container.getDataManager().getDataValue(CHARGE) > 0 || container.getDataManager().getDataValue(COOLDOWN) > 0;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawOnGui(BattleModeGui gui, SkillContainer container, PoseStack poseStack, float x, float y) {
		poseStack.pushPose();
		poseStack.translate(0, (float)gui.getSlidingProgression(), 0);
		RenderSystem.setShaderTexture(0, this.getSkillTexture());
		
		if (container.getDataManager().getDataValue(COOLDOWN) > 0) {
			RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 0.5F);
		} else {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}
		
		GuiComponent.blit(poseStack, (int)x, (int)y, 24, 24, 0, 0, 1, 1, 1, 1);
		
		String string = "";
		
		if (container.getDataManager().getDataValue(COOLDOWN) > 0) {
			string = String.valueOf((container.getDataManager().getDataValue(COOLDOWN)/20)+1);
		} else {
			string = String.valueOf(container.getDataManager().getDataValue(CHARGE));
			if (container.getDataManager().getDataValue(CHARGE) == 0) {
				string = "";
			}
		}
		
		gui.font.drawShadow(poseStack, string, x+5, y+6, 16777215);
		
		poseStack.popPose();
	}
	
	@Override
	public Skill getPriorSkill() {
		return EpicFightSkills.GUARD;
	}
	
	@Override
	protected boolean isAdvancedGuard() {
		return true;
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
		
		if (container.getDataManager().getDataValue(COOLDOWN) > 0) {
			container.getDataManager().setData(COOLDOWN, container.getDataManager().getDataValue(COOLDOWN)-1);
		}
	}
}