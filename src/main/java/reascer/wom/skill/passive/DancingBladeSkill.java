package reascer.wom.skill.passive;

import java.util.Random;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class DancingBladeSkill extends PassiveSkill {
	private static final SkillDataKey<Integer> STEP = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	private static final SkillDataKey<Integer> MELODY_INDEX = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	private static final SkillDataKey<Float> SAVED_ELAPSED_TIME = SkillDataKey.createDataKey(SkillDataManager.ValueType.FLOAT);
	private static final UUID EVENT_UUID = UUID.fromString("52b08e80-bb9b-44bf-a23c-92ddd0958586");
	
	float[] melody = {
			1f,			1f, 		2f, 		1.5f, 		1.4f, 		1.3125f, 	1.1875f, 	1f, 		1.1875f, 	1.3125f,
			0.875f, 	0.875f,		2f, 		1.5f, 		1.4f, 		1.3125f, 	1.1875f, 	1f, 		1.1875f,	1.3125f,
			0.8125f,	0.8125f,	2f, 		1.5f, 		1.4f, 		1.3125f, 	1.1875f, 	1f, 		1.1875f,	1.3125f,
			0.78f, 		0.78f, 		2f, 		1.5f, 		1.4f, 		1.3125f, 	1.1875f, 	1f, 		1.1875f,	1.3125f,
			1f,			1f, 		2f, 		1.5f, 		1.4f, 		1.3125f, 	1.1875f, 	1f, 		1.1875f, 	1.3125f,
			0.875f, 	0.875f,		2f, 		1.5f, 		1.4f, 		1.3125f, 	1.1875f, 	1f, 		1.1875f,	1.3125f,
			0.8125f,	0.8125f,	2f, 		1.5f, 		1.4f, 		1.3125f, 	1.1875f, 	1f, 		1.1875f,	1.3125f,
			0.78f, 		0.78f, 		2f, 		1.5f, 		1.4f, 		1.3125f, 	1.1875f, 	1f, 		1.1875f,	1.3125f,
			1.1875f,	1.1875f,	1.1875f,	1.1875f,	1.1875f,	1f,		 	1f,			1.1875f,	1.1875f, 	1.1875f,
			1.3125f,	1.4f,		1.3125f,	1.1875f,	1F,			1.1875f, 	1.3125f,	1.1875f,	1.1875f,	1.1875f,
			1.3125f,	1.4f,		1.5f,		1.8f, 		1.5f, 		2f, 	 	2f, 		2f,			1.5f, 		2f,
			1.8f, 		1.5f, 		1.5f, 		1.5f, 		1.5f, 		1.5f, 	 	1.3125f, 	1.3125f, 	1.5f, 		1.5f,
			1.5f, 		1.5f, 	 	1.3125f,	1.5f, 		2f,			1.5f, 	 	1.3125f,	2f,			1f,			1.5f, 		
			1f,	 		1.3125f,	1f,			1.1875f,	1f,			1.8f, 		0.875f,		1.3125f,	0.875f,		1.1875f,
			0.875f,		1.125f,		0.8125f,	0.78f,		0.875f,		1f,			1.125f,		1.1875f,	1.3125f,	1.5f,
			1.8f,		0.875f
	};
	
	public DancingBladeSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		container.getDataManager().registerData(STEP);
		container.getDataManager().registerData(MELODY_INDEX);
		container.getDataManager().registerData(SAVED_ELAPSED_TIME);
		
		container.getExecuter().getEventListener().addEventListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(STEP) == 3) {
				event.setDamage(event.getDamage()*1.8f);
			}
        });
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			container.getDataManager().setDataSync(SAVED_ELAPSED_TIME, 10.0f, event.getPlayerPatch().getOriginal());
        });
		
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
			Boolean tag = true;
			float elapsedTime = 0;
			if (event.getDamageSource().getAnimation() instanceof AttackAnimation) {
				ServerPlayerPatch entitypatch = event.getPlayerPatch();
				AttackAnimation anim = ((AttackAnimation) event.getDamageSource().getAnimation());
				AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(event.getDamageSource().getAnimation());
				elapsedTime = player.getElapsedTime();
				Phase phase = anim.getPhaseByTime(elapsedTime);
				Phase previusPhase = anim.getPhaseByTime(container.getDataManager().getDataValue(SAVED_ELAPSED_TIME));
				tag = phase != previusPhase;
				if (anim.phases.length == 1) {
					tag = elapsedTime < container.getDataManager().getDataValue(SAVED_ELAPSED_TIME);
				}
				if (container.getDataManager().getDataValue(SAVED_ELAPSED_TIME) == 10.0 ) {
					tag = true;
				}
				
			}
			
			if (tag) {
				container.getDataManager().setDataSync(SAVED_ELAPSED_TIME,elapsedTime, event.getPlayerPatch().getOriginal());
				((ServerLevel) event.getPlayerPatch().getOriginal().level()).sendParticles(ParticleTypes.NOTE,
						event.getPlayerPatch().getOriginal().getX(),
						event.getPlayerPatch().getOriginal().getY() + event.getTarget().getBbHeight()/2,
						event.getPlayerPatch().getOriginal().getZ(),
						1 * container.getDataManager().getDataValue(STEP)+1,
						(new Random().nextFloat()-0.5f)*2,
						(new Random().nextFloat()-0.5f)*2,
						(new Random().nextFloat()-0.5f)*2,
						2.0);
				if (container.getDataManager().getDataValue(STEP) < 3) {
					container.getDataManager().setDataSync(STEP,container.getDataManager().getDataValue(STEP)+1, event.getPlayerPatch().getOriginal());
				} else {
					((ServerLevel) container.getExecuter().getOriginal().level()).playSound(null,
							event.getPlayerPatch().getOriginal().getX(),
							event.getPlayerPatch().getOriginal().getY(),
							event.getPlayerPatch().getOriginal().getZ(),
			    			SoundEvents.NOTE_BLOCK_SNARE.get(), event.getTarget().getSoundSource(), 2.0F, 2.0f);
					((ServerLevel) container.getExecuter().getOriginal().level()).playSound(null,
							event.getPlayerPatch().getOriginal().getX(),
							event.getPlayerPatch().getOriginal().getY(),
							event.getPlayerPatch().getOriginal().getZ(),
			    			SoundEvents.NOTE_BLOCK_BELL.get(), event.getTarget().getSoundSource(), 2.0F, melody[container.getDataManager().getDataValue(MELODY_INDEX)]);
					container.getDataManager().setDataSync(STEP,0, event.getPlayerPatch().getOriginal());
				}
				
				if (container.getDataManager().getDataValue(MELODY_INDEX) < melody.length-1) {
					((ServerLevel) container.getExecuter().getOriginal().level()).playSound(null,
							event.getPlayerPatch().getOriginal().getX(),
							event.getPlayerPatch().getOriginal().getY(),
							event.getPlayerPatch().getOriginal().getZ(),
			    			SoundEvents.NOTE_BLOCK_SNARE.get(), event.getTarget().getSoundSource(), 2.0F, 0.5f * (container.getDataManager().getDataValue(STEP)+1));
					((ServerLevel) container.getExecuter().getOriginal().level()).playSound(null,
							event.getPlayerPatch().getOriginal().getX(),
							event.getPlayerPatch().getOriginal().getY(),
							event.getPlayerPatch().getOriginal().getZ(),
			    			SoundEvents.NOTE_BLOCK_BIT.get(), SoundSource.MUSIC, 1.5F, melody[container.getDataManager().getDataValue(MELODY_INDEX)]);
					container.getDataManager().setDataSync(MELODY_INDEX,container.getDataManager().getDataValue(MELODY_INDEX)+1, event.getPlayerPatch().getOriginal());
				} else {
					container.getDataManager().setDataSync(MELODY_INDEX,0, event.getPlayerPatch().getOriginal());
				}
			}
        });
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldDraw(SkillContainer container) {
		return container.getDataManager().getDataValue(STEP) == 3;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y) {
		PoseStack poseStack = guiGraphics.pose();
		poseStack.pushPose();
		poseStack.translate(0, (float)gui.getSlidingProgression(), 0);
		RenderSystem.setShaderTexture(0, this.getSkillTexture());
		guiGraphics.blit(this.getSkillTexture(), (int)x, (int)y, 24, 24, 0, 0, 1, 1, 1, 1);
		poseStack.popPose();
	}
}