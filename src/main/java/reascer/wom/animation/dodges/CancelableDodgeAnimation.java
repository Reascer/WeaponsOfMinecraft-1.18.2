package reascer.wom.animation.dodges;

import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.EntityDimensions;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPRotateEntityModelYRot;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class CancelableDodgeAnimation extends ActionAnimation {
	public CancelableDodgeAnimation(float convertTime, String path, float width, float height, Armature armature) {
		this(convertTime, 0.0F, path, width, height, armature);
	}
	
	public CancelableDodgeAnimation(float convertTime, float delayTime, String path, float width, float height, Armature armature) {
		super(convertTime, delayTime, path, armature);
		
		this.stateSpectrumBlueprint.clear()
			.newTimePair(0.0F, 10.0F)
			.addState(EntityState.PHASE_LEVEL, 1)
			.addState(EntityState.TURNING_LOCKED, false)
			.addState(EntityState.MOVEMENT_LOCKED, true)
			.addState(EntityState.CAN_BASIC_ATTACK, true)
			.addState(EntityState.CAN_SKILL_EXECUTION, true)
			.addState(EntityState.INACTION, true)
			.newTimePair(delayTime, Float.MAX_VALUE)
			.addState(EntityState.ATTACK_RESULT, (damagesource) -> {
				if (damagesource instanceof EntityDamageSource && !damagesource.isExplosion() && !damagesource.isMagic() && !damagesource.isBypassArmor() && !damagesource.isBypassInvul()) {
					return AttackResult.ResultType.MISSED;
				}
				
				return AttackResult.ResultType.SUCCESS;
			});
		
		this.addProperty(ActionAnimationProperty.AFFECT_SPEED, true);
		this.addEvents(StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(Animations.ReusableSources.RESTORE_BOUNDING_BOX, AnimationEvent.Side.BOTH));
		this.addEvents(StaticAnimationProperty.EVENTS, AnimationEvent.create(Animations.ReusableSources.RESIZE_BOUNDING_BOX, AnimationEvent.Side.BOTH).params(EntityDimensions.scalable(width, height)));
	}
	
	public CancelableDodgeAnimation(float convertTime, float start, float end, String path, float width, float height, Armature armature) {
		super(convertTime, 0.0f, path, armature);
		
		this.stateSpectrumBlueprint.clear()
			.newTimePair(0.0F, Float.MAX_VALUE)
			.addState(EntityState.PHASE_LEVEL, 1)
			.addState(EntityState.TURNING_LOCKED, false)
			.addState(EntityState.MOVEMENT_LOCKED, true)
			.addState(EntityState.CAN_BASIC_ATTACK, true)
			.addState(EntityState.CAN_SKILL_EXECUTION, true)
			.addState(EntityState.INACTION, true)
			.newTimePair(0.0F, Float.MAX_VALUE)
			.addState(EntityState.ATTACK_RESULT, (damagesource) -> {
				if (damagesource instanceof EntityDamageSource && !damagesource.isExplosion() && !damagesource.isMagic() && !damagesource.isBypassArmor() && !damagesource.isBypassInvul()) {
					return AttackResult.ResultType.MISSED;
				}
				
				return AttackResult.ResultType.SUCCESS;
			})
			.newTimePair(0.0F, end)
			.addState(EntityState.PHASE_LEVEL, 2)
			.addState(EntityState.TURNING_LOCKED, false)
			.addState(EntityState.MOVEMENT_LOCKED, true)
			.addState(EntityState.CAN_BASIC_ATTACK, false);
		
		this.addProperty(ActionAnimationProperty.AFFECT_SPEED, true);
		this.addEvents(StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(Animations.ReusableSources.RESTORE_BOUNDING_BOX, AnimationEvent.Side.BOTH));
		this.addEvents(StaticAnimationProperty.EVENTS, AnimationEvent.create(Animations.ReusableSources.RESIZE_BOUNDING_BOX, AnimationEvent.Side.BOTH).params(EntityDimensions.scalable(width, height)));
	}
	
	@Override
	public void end(LivingEntityPatch<?> entitypatch, DynamicAnimation nextAnimation, boolean isEnd) {
		super.end(entitypatch, nextAnimation, isEnd);
		
		if (entitypatch.isLogicalClient() && entitypatch instanceof LocalPlayerPatch) {
			((LocalPlayerPatch)entitypatch).changeModelYRot(0);
			EpicFightNetworkManager.sendToServer(new CPRotateEntityModelYRot(0));
		}
	}
}