package reascer.wom.skill.weaponpassive;

import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.skill.WOMSkillDataKeys;
import reascer.wom.skill.weaponinnate.TrueBerserkSkill;
import reascer.wom.world.capabilities.item.WOMWeaponCategories;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class TormentPassiveSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("72eabb8f-f889-4302-80bb-690bb557a008");
	
	public TormentPassiveSkill(Builder<?> builder) {
		super(builder.setActivateType(ActivateType.DURATION_INFINITE));
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		if(!container.getExecuter().isLogicalClient()) {
			container.getDataManager().setDataSync(WOMSkillDataKeys.MOVESPEED.get(), true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
		}
		
		container.getExecuter().getEventListener().addEventListener(EventType.CLIENT_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
			if (event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WOMWeaponCategories.TORMENT && container.getExecuter().getEntityState().canBasicAttack()) {
				if (container.getExecuter().getStamina() > 0) {
					event.getPlayerPatch().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
					container.getExecuter().getOriginal().setSprinting(false);
				}
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
			if (event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WOMWeaponCategories.TORMENT && container.getExecuter().getEntityState().canBasicAttack()) {
				if (container.getExecuter().getStamina() > 0) {
					event.getPlayerPatch().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
					container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGING.get(), true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
					container.getExecuter().getOriginal().setSprinting(false);
				}
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGED_ATTACK.get()) || container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGED_ATTACK.get())) {
				event.setDamage(event.getDamage()*3);
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
			ServerPlayerPatch entitypatch = event.getPlayerPatch();
			AttackAnimation anim = ((AttackAnimation) event.getDamageSource().getAnimation());
			AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(event.getDamageSource().getAnimation());
			float elapsedTime = player.getElapsedTime();
			Phase phase = anim.getPhaseByTime(elapsedTime);
			
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGED_ATTACK.get()) && phase == anim.phases[0]) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGED_ATTACK.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				((ServerLevel) event.getPlayerPatch().getOriginal().level()).sendParticles( ParticleTypes.SMOKE, 
						event.getTarget().getX() - 0.15D, 
						event.getTarget().getY() + 1.2D, 
						event.getTarget().getZ() - 0.15D, 
						25, 0.0D, 0.0D, 0.0D,
						0.2D);
				((ServerLevel) event.getPlayerPatch().getOriginal().level()).sendParticles( ParticleTypes.LAVA, 
						event.getTarget().getX() - 0.15D, 
						event.getTarget().getY() + 1.2D, 
						event.getTarget().getZ() - 0.15D, 
						25, 0.0D, 0.0D, 0.0D,
						1.0D);
			} else {
				container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGED_ATTACK.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGED_ATTACK.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_POST, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.SUPER_ARMOR.get()) || container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGING.get())) {
				event.setAmount(event.getAmount()*0.8f);
				event.getDamageSource().setStunType(StunType.NONE);
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			if (event.getAnimation().equals(WOMAnimations.SHADOWSTEP_BACKWARD) ||
				event.getAnimation().equals(WOMAnimations.SHADOWSTEP_FORWARD)) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.MOVESPEED.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
			
			if (!event.getAnimation().equals(WOMAnimations.TORMENT_AUTO_1) &&
				!event.getAnimation().equals(WOMAnimations.TORMENT_AUTO_2) &&
				!event.getAnimation().equals(WOMAnimations.TORMENT_AUTO_3) &&
				!event.getAnimation().equals( WOMAnimations.TORMENT_AUTO_4)) {
			}
			container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGED_ATTACK.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGED_ATTACK.get())) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGED_ATTACK.get(), true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
			container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGING.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.SAVED_CHARGE.get()) < 20) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.SAVED_CHARGE.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGING_TIME.get()) , ((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
			container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGING_TIME.get(), 0 , ((ServerPlayerPatch)container.getExecuter()).getOriginal());
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event) -> {
			if (!event.getAnimation().equals(WOMAnimations.TORMENT_AUTO_1) &&
				!event.getAnimation().equals(WOMAnimations.TORMENT_AUTO_2) &&
				!event.getAnimation().equals(WOMAnimations.TORMENT_AUTO_3) &&
				!event.getAnimation().equals(WOMAnimations.TORMENT_AUTO_4)) {
			}
			container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGED_ATTACK.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
			container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGED_ATTACK.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
		});
		
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
		container.getExecuter().getEventListener().removeListener(EventType.CLIENT_ITEM_USE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_POST, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldDraw(SkillContainer container) {
		if (container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getSkill() instanceof TrueBerserkSkill) {
			return (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGING.get()) || container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGED_ATTACK.get()) || container.getDataManager().getDataValue(WOMSkillDataKeys.SAVED_CHARGE.get()) > 0) && !container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(WOMSkillDataKeys.ACTIVE.get());
		}
		return false;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y) {
		PoseStack poseStack = guiGraphics.pose();
		poseStack.pushPose();
		poseStack.translate(0, (float)gui.getSlidingProgression(), 0);
		RenderSystem.setShaderTexture(0, WOMSkills.TRUE_BERSERK.getSkillTexture());
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		guiGraphics.blit(WOMSkills.TRUE_BERSERK.getSkillTexture(), (int)x, (int)y, 24, 24, 0, 0, 1, 1, 1, 1);
		int charge = 0;
		if (container.getDataManager().getDataValue(WOMSkillDataKeys.SAVED_CHARGE.get()) > 0) {
			charge = (container.getDataManager().getDataValue(WOMSkillDataKeys.SAVED_CHARGE.get())+10)/30;
		} else {
			charge = (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGING_TIME.get())+10)/30;
		}
			
		if (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGED_ATTACK.get())) {
			guiGraphics.drawString(gui.font, String.valueOf(charge), x+10, y+4, 16777215,true);
			guiGraphics.drawString(gui.font, "x3", x+7, y+13, 16777215,true);
		} else {
			guiGraphics.drawString(gui.font, String.valueOf(charge), x+10, y+6, 16777215,true);
		}
		poseStack.popPose();
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
//		if(container.getExecuter().isLogicalClient()) {
//			if ((container.getExecuter().getCurrentLivingMotion() == LivingMotions.WALK || container.getExecuter().getCurrentLivingMotion() == LivingMotions.RUN) && !container.getExecuter().getOriginal().isUsingItem()) {
//				PlayerPatch<?> entitypatch = container.getExecuter();
//				float interpolation = 0.0F;
//				OpenMatrix4f transformMatrix;
//				transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(interpolation), Armatures.BIPED.toolR);
//				transformMatrix.translate(new Vec3f(0,-0.0F,-1.2F));
//				OpenMatrix4f.mul(new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO + 180F), new Vec3f(0, 1, 0)),transformMatrix,transformMatrix);
//					transformMatrix.translate(new Vec3f(0,0.0F,-(new Random().nextFloat() * 1.0f)));
//				
//				float dpx = transformMatrix.m30 + (float) entitypatch.getOriginal().getX();
//				float dpy = transformMatrix.m31 + (float) entitypatch.getOriginal().getY();
//				float dpz = transformMatrix.m32 + (float) entitypatch.getOriginal().getZ();
//				BlockState blockstate = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
//				BlockPos blockpos = new BlockPos.MutableBlockPos(dpx,dpy,dpz);
//				while ((blockstate.getBlock() instanceof BushBlock || blockstate.isAir()) && !blockstate.is(Blocks.VOID_AIR)) {
//					dpy--;
//					blockstate = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
//				}
//				
//				while (blockstate instanceof FractureBlockState) {
//					blockpos = new BlockPos.MutableBlockPos(dpx,dpy--,dpz);
//					blockstate = entitypatch.getOriginal().level().getBlockState(blockpos.below());
//				}
//				if ((transformMatrix.m31 + entitypatch.getOriginal().getY()) < dpy+1.50f) {
//					for (int i = 0; i < 2; i++) {
//						entitypatch.getOriginal().level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate),
//								(transformMatrix.m30 + entitypatch.getOriginal().getX()),
//								(transformMatrix.m31 + entitypatch.getOriginal().getY())-0.2f,
//								(transformMatrix.m32 + entitypatch.getOriginal().getZ()),
//								(new Random().nextFloat() - 0.5F)*0.005f,
//								(new Random().nextFloat()) * 0.02f,
//								(new Random().nextFloat() - 0.5F)*0.005f);
//					}
//				}
//			}
//		}
//		
//		if (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGED_ATTACK.get())) {
//			PlayerPatch<?> entitypatch = container.getExecuter();
//			int numberOf = 2;
//			float partialScale = 1.0F / (numberOf - 1);
//			float interpolation = 0.0F;
//			OpenMatrix4f transformMatrix;
//			for (int i = 0; i < numberOf; i++) {
//				transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(interpolation), Armatures.BIPED.toolR);
//				transformMatrix.translate(new Vec3f(0,0.0F,-1.0F));
//				OpenMatrix4f.mul(new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO + 180F), new Vec3f(0, 1, 0)),transformMatrix,transformMatrix);
//					transformMatrix.translate(new Vec3f(0,0.0F,-(new Random().nextFloat() * 1.0f)));
//					entitypatch.getOriginal().level().addParticle(new DustParticleOptions(new Vector3f(0.8f,0.6f,0f),1.0f),
//						(transformMatrix.m30 + entitypatch.getOriginal().getX() + (new Random().nextFloat() - 0.5F)*0.55f),
//						(transformMatrix.m31 + entitypatch.getOriginal().getY() + (new Random().nextFloat() - 0.5F)*0.55f),
//						(transformMatrix.m32 + entitypatch.getOriginal().getZ() + (new Random().nextFloat() - 0.5F)*0.55f),
//						0,
//						0,
//						0);
//					entitypatch.getOriginal().level().addParticle(ParticleTypes.FLAME,
//							(transformMatrix.m30 + entitypatch.getOriginal().getX() + (new Random().nextFloat() - 0.5F)*0.75f),
//							(transformMatrix.m31 + entitypatch.getOriginal().getY() + (new Random().nextFloat() - 0.5F)*0.75f),
//							(transformMatrix.m32 + entitypatch.getOriginal().getZ() + (new Random().nextFloat() - 0.5F)*0.75f),
//							0,
//							0,
//							0);
//				interpolation += partialScale;
//			}
//		}
		if(!container.getExecuter().isLogicalClient()) {
			AttributeModifier charging_Movementspeed = new AttributeModifier(EVENT_UUID, "torment.charging_movespeed", 3, Operation.MULTIPLY_TOTAL);
			ServerPlayerPatch executer = (ServerPlayerPatch) container.getExecuter();
			int sweeping_edge = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal());
			
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGING.get()) && !container.getExecuter().getOriginal().isUsingItem() && container.getExecuter().getEntityState().canBasicAttack()){
				container.getDataManager().setDataSync(WOMSkillDataKeys.MOVESPEED.get(), true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				int animation_timer = container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGING_TIME.get());
				if (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGING_TIME.get()) < 20 && container.getDataManager().getDataValue(WOMSkillDataKeys.SAVED_CHARGE.get()) >= 20) {
					animation_timer = container.getDataManager().getDataValue(WOMSkillDataKeys.SAVED_CHARGE.get());
				}
				if (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGING_TIME.get()) >= 110) {
					container.getExecuter().getOriginal().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,4, 2,true,false,false));
					container.getExecuter().getOriginal().level().playSound(null,
							container.getExecuter().getOriginal().getX(),
							container.getExecuter().getOriginal().getY(),
							container.getExecuter().getOriginal().getZ(),
							EpicFightSounds.WHOOSH_BIG.get(), SoundSource.PLAYERS,
							1.0F, 1.2F);
					if (!container.getExecuter().getOriginal().isCreative()) {
						if (!container.getExecuter().consumeStamina(3)) {
							container.getExecuter().setStamina(0);
						}
					}
				} else if (animation_timer >= 80) {
					container.getExecuter().playAnimationSynchronized(WOMAnimations.TORMENT_CHARGED_ATTACK_3, 0);
				} else if (animation_timer >= 50) {
					container.getExecuter().playAnimationSynchronized(WOMAnimations.TORMENT_CHARGED_ATTACK_2, 0);
				} else if (animation_timer >= 20) {
					container.getExecuter().playAnimationSynchronized(WOMAnimations.TORMENT_CHARGED_ATTACK_1, 0);
				} else {
					
					if (!container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(WOMSkillDataKeys.ACTIVE.get())) {
						container.getExecuter().getOriginal().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,4, 1,true,false,false));
						container.getExecuter().getOriginal().level().playSound(null,
								container.getExecuter().getOriginal().getX(),
								container.getExecuter().getOriginal().getY(),
								container.getExecuter().getOriginal().getZ(),
								EpicFightSounds.WHOOSH_BIG.get(), SoundSource.PLAYERS,
								1.0F, 1.2F);
						if (!container.getExecuter().getOriginal().isCreative()) {
							if (!container.getExecuter().consumeStamina(3)) {
								container.getExecuter().setStamina(0);
							}
						}
					} else {
						container.getExecuter().getOriginal().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,7, 3,true,false,false));
						container.getExecuter().getOriginal().level().playSound(null,
								container.getExecuter().getOriginal().getX(),
								container.getExecuter().getOriginal().getY(),
								container.getExecuter().getOriginal().getZ(),
								EpicFightSounds.WHOOSH_BIG.get(), SoundSource.PLAYERS,
								1.0F, 1.2F);
						float stamina = container.getExecuter().getStamina();
						float maxStamina = container.getExecuter().getMaxStamina();
						float staminaRegen = (float)container.getExecuter().getOriginal().getAttributeValue(EpicFightAttributes.STAMINA_REGEN.get());
						int regenStandbyTime = 900 / (int)(30 * staminaRegen);
						
						if (container.getExecuter().getTickSinceLastAction() > regenStandbyTime) {
							if (!container.getExecuter().getOriginal().isCreative()) {
								float staminaFactor = 1.0F + (float)Math.pow((stamina / (maxStamina - stamina * 0.5F)), 2);
								if (!container.getExecuter().consumeStamina(2 + maxStamina * 0.05F * staminaFactor * staminaRegen)) {
									container.getExecuter().setStamina(0);
								}
							}
						} else {
							if (!container.getExecuter().getOriginal().isCreative()) {
								if (!container.getExecuter().consumeStamina(2)) {
									container.getExecuter().setStamina(0);
								}
							}
						}
					}
				}
				container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGING.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGING_TIME.get(), 0, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				container.getDataManager().setDataSync(WOMSkillDataKeys.SAVED_CHARGE.get(), 0, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				container.getExecuter().getOriginal().getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(charging_Movementspeed);
			}
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGING.get())) {
				container.getExecuter().getOriginal().addEffect(new MobEffectInstance(EpicFightMobEffects.STUN_IMMUNITY.get(), 5, 0,true,false,false));
				
				if (container.getExecuter().getOriginal().getAttribute(Attributes.MOVEMENT_SPEED).getModifier(EVENT_UUID) == null && container.getDataManager().getDataValue(WOMSkillDataKeys.MOVESPEED.get())) {
					container.getExecuter().getOriginal().getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(charging_Movementspeed);
				}

				container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGING_TIME.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGING_TIME.get()) +1, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				if (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGING_TIME.get()) <= 130) {
					if (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGING_TIME.get()) == 20) {
						container.getDataManager().setDataSync(WOMSkillDataKeys.SAVED_CHARGE.get(), 0, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
						container.getExecuter().getOriginal().level().playSound(null,
								container.getExecuter().getOriginal().getX(),
								container.getExecuter().getOriginal().getY(),
								container.getExecuter().getOriginal().getZ(),
								SoundEvents.ANVIL_LAND, SoundSource.PLAYERS,
								1.0F, 0.6F);
						this.consume_stamina(container);
					}
					if (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGING_TIME.get()) == 50) {
						container.getExecuter().getOriginal().level().playSound(null,
								container.getExecuter().getOriginal().getX(),
								container.getExecuter().getOriginal().getY(),
								container.getExecuter().getOriginal().getZ(),
								SoundEvents.ANVIL_LAND, SoundSource.PLAYERS,
								1.0F, 0.65F);
						this.consume_stamina(container);
					}
					if (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGING_TIME.get()) == 80) {
						container.getExecuter().getOriginal().level().playSound(null,
								container.getExecuter().getOriginal().getX(),
								container.getExecuter().getOriginal().getY(),
								container.getExecuter().getOriginal().getZ(),
								SoundEvents.ANVIL_LAND, SoundSource.PLAYERS,
								1.0F, 0.7F);
						this.consume_stamina(container);
					}
					if (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGING_TIME.get()) == 110) {
						container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGED_ATTACK.get(), true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
						container.getExecuter().getOriginal().level().playSound(null,
								container.getExecuter().getOriginal().getX(),
								container.getExecuter().getOriginal().getY(),
								container.getExecuter().getOriginal().getZ(),
								SoundEvents.ANVIL_LAND, SoundSource.PLAYERS,
								1.0F, 0.5F);
						container.getExecuter().getOriginal().level().playSound(null,
								container.getExecuter().getOriginal().getX(),
								container.getExecuter().getOriginal().getY(),
								container.getExecuter().getOriginal().getZ(),
								SoundEvents.BELL_BLOCK, SoundSource.MASTER,
								2.5F, 0.5F);
						this.consume_stamina(container);
					}
					if (container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGING_TIME.get()) == 130) {
						container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGING_TIME.get(), 0, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
						container.getExecuter().getOriginal().getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(charging_Movementspeed);
					}
				}
			} else {
				container.getExecuter().getOriginal().getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(charging_Movementspeed);
			}
		}
	}
	
	public void consume_stamina(SkillContainer container) {
		if (!container.getExecuter().getOriginal().isCreative()) {
			if (container.getExecuter().getStamina() <= 0) {
				container.getExecuter().getOriginal().stopUsingItem();
			}
			if (!container.getExecuter().consumeStamina(3)) {
				container.getExecuter().setStamina(0);
				container.getExecuter().getOriginal().level().playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
						SoundEvents.LAVA_EXTINGUISH, container.getExecuter().getOriginal().getSoundSource(), 1.0F, 2.0F);
			}
		}
	}
}