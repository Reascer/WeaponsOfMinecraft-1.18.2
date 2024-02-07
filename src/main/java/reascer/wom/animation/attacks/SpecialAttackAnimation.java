package reascer.wom.animation.attacks;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import reascer.wom.config.WOMConfigurationIngame;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.EntityState.StateFactor;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.animation.property.JointMask;
import yesman.epicfight.api.client.animation.property.JointMask.BindModifier;
import yesman.epicfight.api.client.animation.property.JointMaskEntry;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.api.utils.TypeFlexibleHashMap;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.HurtableEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.eventlistener.DealtDamageEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.gamerule.EpicFightGamerules;

public class SpecialAttackAnimation extends BasicMultipleAttackAnimation {
	public SpecialAttackAnimation(float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
		this(convertTime, antic, antic, contact, recovery, collider, colliderJoint, path, armature);
	}
	
	public SpecialAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
		this(convertTime, path, armature, new Phase(0.0F, antic, preDelay, contact, recovery, Float.MAX_VALUE, colliderJoint, collider));
	}
	
	public SpecialAttackAnimation(float convertTime, float antic, float contact, float recovery, InteractionHand hand, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
		this(convertTime, path, armature, new Phase(0.0F, antic, antic, contact, recovery, Float.MAX_VALUE, hand, colliderJoint, collider));
	}
	
	public SpecialAttackAnimation(float convertTime, String path, Armature armature, boolean Coordsetter, Phase... phases) {
		super(convertTime, path, armature, phases);
	}
	
	public SpecialAttackAnimation(float convertTime, String path, Armature armature, Phase... phases) {
		super(convertTime, path, armature, phases);
		this.newTimePair(0, Float.MAX_VALUE);
		this.addStateRemoveOld(EntityState.TURNING_LOCKED, false);
		
		this.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET);
		this.addProperty(ActionAnimationProperty.COORD_SET_TICK, (self, entitypatch, transformSheet) -> {
			LivingEntity attackTarget = entitypatch.getTarget();
			
			if (!self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
				TransformSheet transform = self.getTransfroms().get("Root").copyAll();
				Keyframe[] keyframes = transform.getKeyframes();
				int startFrame = 0;
				int endFrame = transform.getKeyframes().length - 1;
				Vec3f keyLast = keyframes[endFrame].transform().translation();
				Vec3 pos = entitypatch.getOriginal().getEyePosition();
				Vec3 targetpos = attackTarget.position();
				float horizontalDistance = Math.max((float)targetpos.subtract(pos).horizontalDistance()*1.75f - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
				Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
				float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
				
				for (int i = startFrame; i <= endFrame; i++) {
					Vec3f translation = keyframes[i].transform().translation();
					translation.z *= scale;
				}
				
				transformSheet.readFrom(transform);
			} else {
				transformSheet.readFrom(self.getTransfroms().get("Root"));
			}
		});
	}
	
	@Override
	protected void onLoaded() {
		super.onLoaded();
		
		if (!this.properties.containsKey(AttackAnimationProperty.BASIS_ATTACK_SPEED)) {
			float basisSpeed = Float.parseFloat(String.format(Locale.US, "%.2f", (1.0F / this.totalTime)));
			this.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, basisSpeed);
		}
	}
	
	@Override
	public void end(LivingEntityPatch<?> entitypatch, DynamicAnimation nextAnimation, boolean isEnd) {
		super.end(entitypatch, nextAnimation, isEnd);
		
		boolean stiffAttack = entitypatch.getOriginal().level().getGameRules().getRule(EpicFightGamerules.STIFF_COMBO_ATTACKS).get();
		
		if (!isEnd && !nextAnimation.isMainFrameAnimation() && entitypatch.isLogicalClient() && !stiffAttack) {
			float playbackSpeed = WOMConfigurationIngame.A_TICK * this.getPlaySpeed(entitypatch);
			entitypatch.getClientAnimator().baseLayer.copyLayerTo(entitypatch.getClientAnimator().baseLayer.getLayer(Layer.Priority.HIGHEST), playbackSpeed);
		}
	}
	
	@Override
	public TypeFlexibleHashMap<StateFactor<?>> getStatesMap(LivingEntityPatch<?> entitypatch, float time) {
		TypeFlexibleHashMap<StateFactor<?>> stateMap = super.getStatesMap(entitypatch, time);
		
		if (!entitypatch.getOriginal().level().getGameRules().getRule(EpicFightGamerules.STIFF_COMBO_ATTACKS).get()) {
			stateMap.put(EntityState.MOVEMENT_LOCKED, (Object)false);
		}
		
		return stateMap;
	}
	
	@Override
	public Vec3 getCoordVector(LivingEntityPatch<?> entitypatch, DynamicAnimation dynamicAnimation) {
		Vec3 vec3 = super.getCoordVector(entitypatch, dynamicAnimation);
		
		if (entitypatch.shouldBlockMoving() && this.getProperty(ActionAnimationProperty.CANCELABLE_MOVE).orElse(false)) {
			vec3 = vec3.scale(0.0F);
		}
		
		return vec3;
	}
	
	@Override
	public boolean isJointEnabled(LivingEntityPatch<?> entitypatch, Layer.Priority layer, String joint) {
		if (layer == Layer.Priority.HIGHEST) {
			return !JointMaskEntry.BASIC_ATTACK_MASK.isMasked(entitypatch.getCurrentLivingMotion(), joint);
		} else {
			return super.isJointEnabled(entitypatch, layer, joint);
		}
	}
	
	@Override
	public BindModifier getBindModifier(LivingEntityPatch<?> entitypatch, Layer.Priority layer, String joint) {
		if (layer == Layer.Priority.HIGHEST) {
			List<JointMask> list = JointMaskEntry.BIPED_UPPER_JOINTS_WITH_ROOT;
			int position = list.indexOf(JointMask.of(joint));
			
			if (position >= 0) {
				return list.get(position).getBindModifier();
			} else {
				return null;
			}
		} else {
			return super.getBindModifier(entitypatch, layer, joint);
		}
	}
	
	@Override
	public boolean isBasicAttackAnimation() {
		return false;
	}
	
	public float applyAntiStunLock(Entity hitten, float anti_stunlock, EpicFightDamageSource source, Phase phase, String tag, String replaceTag) {
		boolean isPhaseFromSameAnimnation = false;
		if (hitten.level().getBlockState(new BlockPos.MutableBlockPos( hitten.getX(), hitten.getY()-1, hitten.getZ())).isAir() && source.getStunType() != StunType.FALL ) {
			String phaseID = String.valueOf(this.getId())+"-"+String.valueOf(phase.contact);
			if (tag.split(":").length > 3) {
				if ((String.valueOf(this.getId()).equals(tag.split(":")[3].split("-")[0])) && (!String.valueOf(phase.contact).equals(tag.split(":")[3].split("-")[1]))) {
					anti_stunlock = Float.valueOf(tag.split(":")[1])* 0.98f;
					isPhaseFromSameAnimnation = true;
				} else {
					anti_stunlock = Float.valueOf(tag.split(":")[1]) * 0.95f;
					isPhaseFromSameAnimnation = false;
				}
			}
			for (int i = 3; i < tag.split(":").length && i < 5; i++) {
				if (tag.split(":")[i].equals(phaseID)) {
					anti_stunlock *= 0.60f;
				}
			}
		} else {
			String phaseID = String.valueOf(this.getId())+"-"+String.valueOf(phase.contact);
			if (tag.split(":").length > 3) {
				if ((String.valueOf(this.getId()).equals(tag.split(":")[3].split("-")[0])) && (!String.valueOf(phase.contact).equals(tag.split(":")[3].split("-")[1]))) {
					anti_stunlock = Float.valueOf(tag.split(":")[1]) * 0.98f;
					isPhaseFromSameAnimnation = true;
				} else {
					anti_stunlock = Float.valueOf(tag.split(":")[1]) * 0.80f;
					isPhaseFromSameAnimnation = false;
				}
			}
			for (int i = 3; i < tag.split(":").length && i < 7; i++) {
				if (tag.split(":")[i].equals(phaseID)) {
					anti_stunlock *= 0.60f;
				}
			}
		}
		hitten.removeTag(tag);
		int maxSavedAttack = 5;
		
		if (isPhaseFromSameAnimnation) {
			replaceTag = "anti_stunlock:"+ anti_stunlock+":"+hitten.tickCount;
			maxSavedAttack = 6;
		} else {
			replaceTag = "anti_stunlock:"+ anti_stunlock+":"+hitten.tickCount+":"+this.getId()+"-"+phase.contact;
			maxSavedAttack = 5;
		}
		
		for (int i = 3; i < tag.split(":").length && i < maxSavedAttack; i++) {
			replaceTag = replaceTag.concat(":"+tag.split(":")[i]);
		}
		hitten.addTag(replaceTag);
		return anti_stunlock;
	}
}
