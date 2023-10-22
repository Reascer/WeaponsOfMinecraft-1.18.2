package reascer.wom.animation;

import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.MainFrameAnimation;
import yesman.epicfight.api.model.Armature;

public class GuardHitAnimation extends MainFrameAnimation {
	public GuardHitAnimation(float convertTime, String path, Armature armature) {
		this(convertTime, Float.MAX_VALUE, path, armature);
	}
	
	public GuardHitAnimation(float convertTime, float lockTime, String path, Armature armature) {
		super(convertTime, path, armature);
		
		this.stateSpectrumBlueprint.clear()
			.newTimePair(0.0F, lockTime)
			.addState(EntityState.TURNING_LOCKED, false)
			.addState(EntityState.MOVEMENT_LOCKED, true)
			.addState(EntityState.CAN_BASIC_ATTACK, false)
			.newTimePair(0.0F, Float.MAX_VALUE)
			.addState(EntityState.INACTION, true);
	}
}