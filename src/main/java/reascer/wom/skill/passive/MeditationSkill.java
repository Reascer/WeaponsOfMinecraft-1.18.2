package reascer.wom.skill.passive;

import java.util.UUID;

import org.joml.Vector3f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.gameasset.WOMAnimations;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class MeditationSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("294c9e0d-7a43-443a-a603-2dd838d9702e");
	public static final SkillDataKey<Integer> TIMER = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	protected static final SkillDataKey<Boolean> ACTIVE = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	protected static final SkillDataKey<Integer> DUREE = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	protected static final SkillDataKey<Integer> CYCLE = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	protected static final SkillDataKey<Integer> STAGE = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	
	public AttributeModifier meditation_speed = new AttributeModifier(EVENT_UUID, "meditation.meditation_speed", 0, Operation.MULTIPLY_TOTAL);
	
	public MeditationSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(TIMER);
		container.getDataManager().registerData(ACTIVE);
		container.getDataManager().registerData(DUREE);
		container.getDataManager().registerData(CYCLE);
		container.getDataManager().registerData(STAGE);
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(ACTIVE) && container.getDataManager().getDataValue(DUREE) == 0 &&
				!event.getAnimation().equals(WOMAnimations.MEDITATION_SITING) &&
				!event.getAnimation().equals(WOMAnimations.MEDITATION_BREATHING)) {
				if (container.getDataManager().getDataValue(STAGE) != 0) {
					container.getDataManager().setDataSync(ACTIVE, true,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(DUREE, container.getDataManager().getDataValue(TIMER)*6,((ServerPlayerPatch) container.getExecuter()).getOriginal());
				} else {
					container.getDataManager().setDataSync(ACTIVE, false,event.getPlayerPatch().getOriginal());
				}
				container.getDataManager().setDataSync(TIMER, 0,event.getPlayerPatch().getOriginal());
				((ServerPlayerPatch) container.getExecuter()).modifyLivingMotionByCurrentItem();
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(ACTIVE) && container.getDataManager().getDataValue(DUREE) > 0 && (container.getDataManager().getDataValue(STAGE) == 1 || container.getDataManager().getDataValue(STAGE) == 4)) {
				float attackDamage = event.getDamage();
				event.setDamage(attackDamage * 1.4f);
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.MODIFY_ATTACK_SPEED_EVENT, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(ACTIVE) && container.getDataManager().getDataValue(DUREE) > 0 && (container.getDataManager().getDataValue(STAGE) == 2 || container.getDataManager().getDataValue(STAGE) == 4)) {
				float attackSpeed = event.getAttackSpeed();
				event.setAttackSpeed(attackSpeed * 1.3f);
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_POST, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(ACTIVE) && container.getDataManager().getDataValue(DUREE) > 0 && (container.getDataManager().getDataValue(STAGE) == 3 || container.getDataManager().getDataValue(STAGE) == 4)) {
                event.setAmount(event.getAmount()*0.5f);
            }
        });
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.MODIFY_ATTACK_SPEED_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_POST, EVENT_UUID);
		if (container.getDataManager().getDataValue(ACTIVE) && container.getDataManager().getDataValue(DUREE) > 0 && (container.getDataManager().getDataValue(STAGE) == 3 || container.getDataManager().getDataValue(STAGE) == 4)) {
			container.getExecuter().getOriginal().addEffect(new MobEffectInstance(MobEffects.REGENERATION,container.getDataManager().getDataValue(DUREE), 0,true,false,false));
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldDraw(SkillContainer container) {
		return container.getDataManager().getDataValue(STAGE) > 0 || (container.getDataManager().getDataValue(ACTIVE) && container.getDataManager().getDataValue(DUREE) == 0);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y) {
		PoseStack poseStack = guiGraphics.pose();
		poseStack.pushPose();
		poseStack.translate(0, (float)gui.getSlidingProgression(), 0);
		RenderSystem.setShaderTexture(0, this.getSkillTexture());
		if (container.getDataManager().getDataValue(ACTIVE) && container.getDataManager().getDataValue(DUREE) > 0) {
			switch (container.getDataManager().getDataValue(STAGE)) {
			case 1: {
				RenderSystem.setShaderColor(1.0F, 0.3F, 0.3F, 1.0F);
				break;
			}
			case 2: {
				RenderSystem.setShaderColor(0.3F, 0.9F, 0.9F, 1.0F);
				break;
			}
			case 3: {
				RenderSystem.setShaderColor(0.9F, 0.9F, 0.3F, 1.0F);
				break;
			}
			case 4: {
				RenderSystem.setShaderColor(0.9F, 0.2F, 0.9F, 1.0F);
				break;
			}
			default:
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				break;
			}
			
			guiGraphics.blit(this.getSkillTexture(), (int)x, (int)y, 24, 24, 0, 0, 1, 1, 1, 1);
			guiGraphics.drawString(gui.font,(((container.getDataManager().getDataValue(DUREE)/20)/60) / 10 == 0 ? "0" : "") + String.valueOf((container.getDataManager().getDataValue(DUREE)/20)/60)+":"+ (((container.getDataManager().getDataValue(DUREE)/20)%60) / 10 == 0 ? "0" : "")+String.valueOf((container.getDataManager().getDataValue(DUREE)/20)%60), x, y+17, 16777215,true);
		} else {
			RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 0.5F);
			if (container.getDataManager().getDataValue(TIMER) >= 20*300) {
				RenderSystem.setShaderColor(0.8F, 0.2F, 0.8F, 0.5F);
				
			} else if (container.getDataManager().getDataValue(TIMER) >= 20*60) {
				RenderSystem.setShaderColor(0.7F, 0.7F, 0.3F, 0.5F);
				
			} else if (container.getDataManager().getDataValue(TIMER) >= 20*40) {
				RenderSystem.setShaderColor(0.3F, 0.7F, 0.7F, 0.5F);
				
			} else if (container.getDataManager().getDataValue(TIMER) >= 20*20) {
				RenderSystem.setShaderColor(0.8F, 0.3F, 0.3F, 0.5F);
				
			}
			
			guiGraphics.blit(this.getSkillTexture(), (int)x, (int)y, 24, 24, 0, 0, 1, 1, 1, 1);
			if (container.getDataManager().getDataValue(TIMER) > 0) {
				guiGraphics.drawString(gui.font,(((container.getDataManager().getDataValue(TIMER)/20)/60) / 10 == 0 ? "0" : "") + String.valueOf((container.getDataManager().getDataValue(TIMER)/20)/60)+":"+ (((container.getDataManager().getDataValue(TIMER)/20)%60) / 10 == 0 ? "0" : "")+String.valueOf((container.getDataManager().getDataValue(TIMER)/20)%60), x, y+17, 16777215,true);
			}
		}
		poseStack.popPose();
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		if (container.getExecuter().getOriginal().isCrouching()) {
			if(!container.getExecuter().isLogicalClient()) {
				if (container.getDataManager().getDataValue(TIMER) < 40) {
					container.getDataManager().setDataSync(TIMER, container.getDataManager().getDataValue(TIMER) + 1,((ServerPlayerPatch) container.getExecuter()).getOriginal());
				}
				
				if (container.getDataManager().getDataValue(TIMER) >= 40) {
					if (container.getDataManager().getDataValue(STAGE) == 3 && container.getDataManager().getDataValue(DUREE) > 0) {
						container.getExecuter().getOriginal().removeEffect(MobEffects.REGENERATION);
					}
					container.getDataManager().setDataSync(DUREE, 0,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(STAGE, 0,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(TIMER, 0,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(CYCLE, 30,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(ACTIVE, true,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					container.getExecuter().playAnimationSynchronized(WOMAnimations.MEDITATION_SITING, 0);
				}
			}
		} else {
			if(!container.getExecuter().isLogicalClient()) {
				if (container.getDataManager().getDataValue(ACTIVE)) {
					if (container.getDataManager().getDataValue(DUREE) == 0) {
						container.getDataManager().setDataSync(TIMER, container.getDataManager().getDataValue(TIMER) + 1,((ServerPlayerPatch) container.getExecuter()).getOriginal());	
					}
					if (container.getDataManager().getDataValue(TIMER) >= 20*300 || container.getDataManager().getDataValue(STAGE) == 4) {
						container.getDataManager().setDataSync(STAGE, 4,((ServerPlayerPatch) container.getExecuter()).getOriginal());
						((ServerLevel) container.getExecuter().getOriginal().level()).sendParticles( new DustParticleOptions(new Vector3f(1.0f, 0.0f, 1.0f),1.0f), 
								container.getExecuter().getOriginal().getX(), 
								container.getExecuter().getOriginal().getY() + 0.5D, 
								container.getExecuter().getOriginal().getZ(), 
								4, 0.6D, 0.6D, 0.6D, 0.05);
					
					} else if (container.getDataManager().getDataValue(TIMER) >= 20*60 || container.getDataManager().getDataValue(STAGE) == 3) {
						container.getDataManager().setDataSync(STAGE, 3,((ServerPlayerPatch) container.getExecuter()).getOriginal());
						((ServerLevel) container.getExecuter().getOriginal().level()).sendParticles(new DustParticleOptions(new Vector3f(1.0f, 1.0f, 0.4f),1.0f),
								container.getExecuter().getOriginal().getX(), 
								container.getExecuter().getOriginal().getY() + 0.5D, 
								container.getExecuter().getOriginal().getZ(), 
								3, 0.6D, 0.6D, 0.6D, 0.05);
						
					} else if (container.getDataManager().getDataValue(TIMER) >= 20*40 || container.getDataManager().getDataValue(STAGE) == 2) {
						container.getDataManager().setDataSync(STAGE, 2,((ServerPlayerPatch) container.getExecuter()).getOriginal());
						((ServerLevel) container.getExecuter().getOriginal().level()).sendParticles(new DustParticleOptions(new Vector3f(0.0f, 1.0f, 1.0f),1.0f),
								container.getExecuter().getOriginal().getX(), 
								container.getExecuter().getOriginal().getY() + 0.5D, 
								container.getExecuter().getOriginal().getZ(), 
								2, 0.6D, 0.6D, 0.6D, 0.05);
					} else if (container.getDataManager().getDataValue(TIMER) >= 20*20 || container.getDataManager().getDataValue(STAGE) == 1) {
						container.getDataManager().setDataSync(STAGE, 1,((ServerPlayerPatch) container.getExecuter()).getOriginal());
						((ServerLevel) container.getExecuter().getOriginal().level()).sendParticles(new DustParticleOptions(new Vector3f(1.0f, 0.0f, 0.0f),1.0f),
								container.getExecuter().getOriginal().getX(), 
								container.getExecuter().getOriginal().getY() + 0.5D, 
								container.getExecuter().getOriginal().getZ(), 
								1, 0.6D, 0.6D, 0.6D, 0.05);
					} else {
						container.getDataManager().setDataSync(STAGE, 0,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					}
				}
			}
		}
		if(!container.getExecuter().isLogicalClient()) {
			if (container.getDataManager().getDataValue(DUREE) > 0) {
				if (!container.getExecuter().getOriginal().isCrouching()) {
					container.getDataManager().setDataSync(TIMER, 0,((ServerPlayerPatch) container.getExecuter()).getOriginal());
				}
				container.getDataManager().setDataSync(DUREE, container.getDataManager().getDataValue(DUREE)-1,((ServerPlayerPatch) container.getExecuter()).getOriginal());
				if (container.getDataManager().getDataValue(DUREE) == 0) {
					container.getDataManager().setDataSync(ACTIVE, false,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(STAGE, 0,((ServerPlayerPatch) container.getExecuter()).getOriginal());
				}
			}
			
			if (container.getDataManager().getDataValue(ACTIVE) && container.getDataManager().getDataValue(DUREE) == 0) {
				if (container.getDataManager().getDataValue(CYCLE) > 0) {
					container.getDataManager().setDataSync(CYCLE, container.getDataManager().getDataValue(CYCLE)-1,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					if (container.getDataManager().getDataValue(CYCLE) == 0) {
						container.getExecuter().playAnimationSynchronized(WOMAnimations.MEDITATION_BREATHING, 0);
						container.getDataManager().setDataSync(CYCLE, 80,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					}
				}
				if (container.getExecuter().getOriginal().walkDist != container.getExecuter().getOriginal().walkDistO) {
					if (container.getDataManager().getDataValue(STAGE) != 0) {
						container.getDataManager().setDataSync(ACTIVE, true,((ServerPlayerPatch) container.getExecuter()).getOriginal());
						container.getDataManager().setDataSync(DUREE, container.getDataManager().getDataValue(TIMER)*6,((ServerPlayerPatch) container.getExecuter()).getOriginal());	
					} else {
						container.getDataManager().setDataSync(ACTIVE, false,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					}
					
					container.getDataManager().setDataSync(TIMER, 0,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					((ServerPlayerPatch) container.getExecuter()).modifyLivingMotionByCurrentItem();
				}
			}
			
			if (container.getDataManager().getDataValue(ACTIVE) && container.getDataManager().getDataValue(DUREE) > 0 && (container.getDataManager().getDataValue(STAGE) == 2 || container.getDataManager().getDataValue(STAGE) == 4)) {
				container.getExecuter().getOriginal().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,5, 1,true,false,false));
			}
			
			if (container.getDataManager().getDataValue(ACTIVE) && container.getDataManager().getDataValue(DUREE) > 0 && (container.getDataManager().getDataValue(STAGE) == 3 || container.getDataManager().getDataValue(STAGE) == 4)) {
				if (container.getDataManager().getDataValue(CYCLE) > 0) {
					container.getDataManager().setDataSync(CYCLE, container.getDataManager().getDataValue(CYCLE)-1,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					if (container.getDataManager().getDataValue(CYCLE) == 0) {
						container.getExecuter().getOriginal().addEffect(new MobEffectInstance(MobEffects.REGENERATION,110, 0,true,false,false));
						container.getDataManager().setDataSync(CYCLE, 100,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					}
				}
			}
		}
	}
}