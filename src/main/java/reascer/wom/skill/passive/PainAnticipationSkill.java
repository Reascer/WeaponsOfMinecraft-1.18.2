package reascer.wom.skill.passive;

import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class PainAnticipationSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("8f274f40-ea63-11ec-8fea-0242ac120002");
	protected static final SkillDataKey<Integer> TIMER = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	protected static final SkillDataKey<Boolean> ACTIVE = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	protected static final SkillDataKey<Integer> DUREE = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	
	protected int maxtimer;
	protected int maxduree;
	
	protected float damage_reduction;
	
	public PainAnticipationSkill(Builder<? extends Skill> builder) {
		super(builder);
		maxtimer = 40;
		maxduree = 40;
		damage_reduction = 0.6f;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(TIMER);
		container.getDataManager().registerData(ACTIVE);
		container.getDataManager().registerData(DUREE);
		
		container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_POST, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(TIMER) == 0 || container.getDataManager().getDataValue(ACTIVE)) {
                event.getDamageSource().setStunType(StunType.NONE);
                event.setAmount(event.getAmount()*damage_reduction);
                event.getPlayerPatch().getOriginal().level.playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
		    			SoundEvents.LARGE_AMETHYST_BUD_BREAK, container.getExecuter().getOriginal().getSoundSource(), 2.0F, 1.0F);
				((ServerLevel) container.getExecuter().getOriginal().level()).sendParticles( ParticleTypes.SMOKE, 
						container.getExecuter().getOriginal().getX() - 0.2D, 
						container.getExecuter().getOriginal().getY() + 1.3D, 
						container.getExecuter().getOriginal().getZ() - 0.2D, 
						10, 0.6D, 0.8D, 0.6D, 0.1f);
				container.getDataManager().setDataSync(ACTIVE, true,event.getPlayerPatch().getOriginal());
            }
        });
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(TIMER) == 0) {
				 event.getPlayerPatch().getOriginal().level().playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
			    			SoundEvents.LARGE_AMETHYST_BUD_BREAK, container.getExecuter().getOriginal().getSoundSource(), 2.0F, 1.0F);
				((ServerLevel) container.getExecuter().getOriginal().level()).sendParticles( ParticleTypes.SMOKE, 
						container.getExecuter().getOriginal().getX() - 0.2D, 
						container.getExecuter().getOriginal().getY() + 1.3D, 
						container.getExecuter().getOriginal().getZ() - 0.2D, 
						10, 0.6D, 0.8D, 0.6D, 0.05f);
				container.getDataManager().setDataSync(ACTIVE, true,event.getPlayerPatch().getOriginal());
            }
			container.getDataManager().setDataSync(TIMER, maxtimer,event.getPlayerPatch().getOriginal());
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_POST, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldDraw(SkillContainer container) {
		return true;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y) {
		PoseStack poseStack = guiGraphics.pose();
		poseStack.pushPose();
		poseStack.translate(0, (float)gui.getSlidingProgression(), 0);
		RenderSystem.setShaderTexture(0, this.getSkillTexture());
		if (container.getDataManager().getDataValue(ACTIVE)) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			guiGraphics.blit(this.getSkillTexture(), (int)x, (int)y, 24, 24, 0, 0, 1, 1, 1, 1);
			guiGraphics.drawString(gui.font, String.valueOf((container.getDataManager().getDataValue(DUREE)/20)+1), x+7, y+13, 16777215,true);
		} else {
			if (container.getDataManager().getDataValue(TIMER) > 0) {
				RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 0.5F);
			} else {

				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			}
			guiGraphics.blit(this.getSkillTexture(), (int)x, (int)y, 24, 24, 0, 0, 1, 1, 1, 1);
			if (container.getDataManager().getDataValue(TIMER) > 0) {
				guiGraphics.drawString(gui.font, String.valueOf((container.getDataManager().getDataValue(TIMER)/20)+1), x+7, y+13, 16777215,true);
			}
		}
		poseStack.popPose();
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
	super.updateContainer(container);
		if (container.getDataManager().getDataValue(TIMER) > 0 && container.getDataManager().getDataValue(DUREE)  == 0) {
			if(!container.getExecuter().isLogicalClient()) {
				container.getDataManager().setDataSync(TIMER, container.getDataManager().getDataValue(TIMER)-1,((ServerPlayerPatch) container.getExecuter()).getOriginal());
				if (container.getDataManager().getDataValue(TIMER) == 0) {
					container.getDataManager().setDataSync(DUREE, maxduree,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					((ServerLevel) container.getExecuter().getOriginal().level()).playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
				    			SoundEvents.LARGE_AMETHYST_BUD_BREAK, container.getExecuter().getOriginal().getSoundSource(), 2.0F, 2.0F);
					((ServerLevel) container.getExecuter().getOriginal().level()).sendParticles(DustParticleOptions.REDSTONE, 
							container.getExecuter().getOriginal().getX() - 0.2D, 
							container.getExecuter().getOriginal().getY() + 1.3D, 
							container.getExecuter().getOriginal().getZ() - 0.2D, 
							30, 0.6D, 0.8D, 0.6D, 0.05);
				}
			}
		}
		if (container.getDataManager().getDataValue(ACTIVE)) {
			container.getExecuter().getOriginal().addEffect(new MobEffectInstance(EpicFightMobEffects.STUN_IMMUNITY.get(), 5, 0,true,false,false));
			if (container.getDataManager().getDataValue(DUREE) > 0) {
				if(!container.getExecuter().isLogicalClient()) {
					container.getDataManager().setDataSync(DUREE, container.getDataManager().getDataValue(DUREE)-1,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					((ServerLevel) container.getExecuter().getOriginal().level()).sendParticles( DustParticleOptions.REDSTONE, 
							container.getExecuter().getOriginal().getX() - 0.2D, 
							container.getExecuter().getOriginal().getY() + 1.3D, 
							container.getExecuter().getOriginal().getZ() - 0.2D, 
							4, 0.6D, 0.8D, 0.6D, 0.05);
					if (container.getDataManager().getDataValue(DUREE) == 0) {
						((ServerLevel) container.getExecuter().getOriginal().level()).playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
				    			SoundEvents.LARGE_AMETHYST_BUD_BREAK, container.getExecuter().getOriginal().getSoundSource(), 2.0F, 0.5F);
						container.getDataManager().setDataSync(ACTIVE, false,((ServerPlayerPatch) container.getExecuter()).getOriginal());
						container.getDataManager().setDataSync(TIMER, maxtimer,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					}
				}
			} else {
				if(!container.getExecuter().isLogicalClient()) {
					container.getDataManager().setDataSync(ACTIVE, false,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(TIMER, maxtimer,((ServerPlayerPatch) container.getExecuter()).getOriginal());
				}
			}
		}
	}
}