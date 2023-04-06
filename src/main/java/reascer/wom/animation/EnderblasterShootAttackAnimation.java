package reascer.wom.animation;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import reascer.wom.gameasset.WOMColliders;
import reascer.wom.world.entity.projectile.EnderBullet;
import reascer.wom.world.entity.projectile.WOMEntities;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.LinkAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class EnderblasterShootAttackAnimation extends AttackAnimation {
	public EnderblasterShootAttackAnimation(float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
		this(convertTime, antic, antic, contact, recovery, collider, colliderJoint, path, armature);
	}
	
	public EnderblasterShootAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
		this(convertTime, path, armature, new Phase(0.0F, antic, preDelay, contact, recovery, Float.MAX_VALUE, colliderJoint, collider));
	}
	
	public EnderblasterShootAttackAnimation(float convertTime, float antic, float contact, float recovery, InteractionHand hand, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
		this(convertTime, path, armature, new Phase(0.0F, antic, antic, contact, recovery, Float.MAX_VALUE, hand, colliderJoint, collider));
	}
	
	// something is going to be wrong with this one 
	public EnderblasterShootAttackAnimation(float convertTime, String path, Armature armature, boolean Coordsetter, Phase... phases) {
		super(convertTime, path, armature, phases);
	}
	
	public EnderblasterShootAttackAnimation(float convertTime, String path, Armature armature, Phase... phases) {
		super(convertTime, path, armature, phases);
		
		this.addProperty(StaticAnimationProperty.POSE_MODIFIER, (self, pose, entitypatch) -> {
			float pitch = (float) Math.toDegrees(entitypatch.getOriginal().getViewVector(1.0f).y);
			
			JointTransform armR = pose.getOrDefaultTransform("Arm_R");
			armR.frontResult(JointTransform.getRotation(Vector3f.XP.rotationDegrees(-pitch)), OpenMatrix4f::mulAsOriginFront);
			
			if (((AttackAnimation) self).getPhaseByTime(1).getColliderJoint() != Armatures.BIPED.armR) {
				JointTransform armL = pose.getOrDefaultTransform("Arm_L");
				armL.frontResult(JointTransform.getRotation(Vector3f.XP.rotationDegrees(-pitch)), OpenMatrix4f::mulAsOriginFront);
			}
			
			JointTransform chest = pose.getOrDefaultTransform("Chest");
			chest.frontResult(JointTransform.getRotation(Vector3f.XP.rotationDegrees((float) (pitch > 35f ? (-pitch + 35f):0f))), OpenMatrix4f::mulAsOriginFront);
			
			if (entitypatch instanceof PlayerPatch) {
				JointTransform head = pose.getOrDefaultTransform("Head");
				MathUtils.mulQuaternion(Vector3f.XP.rotationDegrees(-entitypatch.getAttackDirectionPitch()), head.rotation(), head.rotation());
			}
		});
	}
	@Override
	public void setLinkAnimation(Pose pose1, float timeModifier, LivingEntityPatch<?> entitypatch, LinkAnimation dest) {
		float extTime = Math.max(this.convertTime + timeModifier, 0);
		
		if (entitypatch instanceof PlayerPatch<?>) {
			PlayerPatch<?> playerpatch = (PlayerPatch<?>)entitypatch;
			Phase phase = this.getPhaseByTime(playerpatch.getAnimator().getPlayerFor(this).getElapsedTime());
			extTime *= (float)(this.totalTime * playerpatch.getAttackSpeed(phase.getHand()));
		}
		
		extTime = Math.max(extTime - this.convertTime, 0);
		super.setLinkAnimation(pose1, extTime, entitypatch, dest);
	}
	
	@Override
	public Vec3 getCoordVector(LivingEntityPatch<?> entitypatch, DynamicAnimation dynamicAnimation) {
		Vec3 vec3 = super.getCoordVector(entitypatch, dynamicAnimation);
		
		if (entitypatch.shouldBlockMoving() && this.getProperty(ActionAnimationProperty.CANCELABLE_MOVE).orElse(true)) {
			vec3.scale(0.0F);
		}
		
		return vec3;
	}
	
	@Override
	public void tick(LivingEntityPatch<?> entitypatch) {
		super.tick(entitypatch);
		if (!entitypatch.isLogicalClient()) {
			AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(this);
			float elapsedTime = player.getElapsedTime();
			float prevElapsedTime = player.getPrevElapsedTime();
			EntityState state = this.getState(entitypatch, elapsedTime);
			EntityState prevState = this.getState(entitypatch, prevElapsedTime);
			Phase phase = this.getPhaseByTime(elapsedTime);
			
			if (state.getLevel() == 1 && !state.turningLocked()) {
				if (entitypatch instanceof MobPatch) {
					((Mob)entitypatch.getOriginal()).getNavigation().stop();
					entitypatch.getOriginal().attackAnim = 2;
					LivingEntity target = entitypatch.getTarget();
					
					if (target != null) {
						entitypatch.rotateTo(target, entitypatch.getYRotLimit(), false);
					}
				}
			}
			
			if (prevState.attacking() || state.attacking() || (prevState.getLevel() < 2 && state.getLevel() > 2)) {
				if (!prevState.attacking() || (phase != this.getPhaseByTime(prevElapsedTime) && (state.attacking() || (prevState.getLevel() < 2 && state.getLevel() > 2)))) {
					
					Level worldIn = entitypatch.getOriginal().getLevel();
					Collider collider = this.getCollider(entitypatch, elapsedTime);
					entitypatch.getArmature().initializeTransform();
					float prevPoseTime = prevElapsedTime;
					float poseTime = elapsedTime;
					List<Entity> list = collider.updateAndSelectCollideEntity(entitypatch, this, prevPoseTime, poseTime, phase.getColliderJoint(), this.getPlaySpeed(entitypatch));
					List<Entity> list2 = new ArrayList<Entity>(list);
					for (Entity entity : list) {
						if (entity instanceof Projectile) {
							list2.remove(entity);
							continue;
						}
						if (!(entity instanceof LivingEntity)) {
							list2.remove(entity);
							continue;
						}
					}
					/*
					System.out.println("list 1");
					System.out.println(list);
					System.out.println("list 2");
					System.out.println(list2);
					System.out.println();
					*/
					if (list2.size() == 0) {
						if (phase.getColliderJoint() != Armatures.BIPED.head && phase.getColliderJoint() != Armatures.BIPED.rootJoint && this.getCollider(entitypatch, elapsedTime) != WOMColliders.ENDER_LASER && this.getCollider(entitypatch, elapsedTime) != WOMColliders.ENDER_PISTOLERO && this.getCollider(entitypatch, elapsedTime) != WOMColliders.ENDER_DASH && this.getCollider(entitypatch, elapsedTime) != WOMColliders.ENDER_BULLET_WIDE) {
							Joint joint = phase.getColliderJoint();
							
							OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(0.0f), joint);
							transformMatrix.translate(new Vec3f(0,-0.6F,-0.3F));
							OpenMatrix4f.mul(new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0)),transformMatrix,transformMatrix);
							
							Vec3 direction = entitypatch.getOriginal().getViewVector(1.0F);
							Projectile projectile = new EnderBullet(WOMEntities.ENDERBLAST.get(),worldIn);
							projectile.setOwner(entitypatch.getOriginal());
			                projectile.setPosRaw((
			                		transformMatrix.m30 + entitypatch.getOriginal().getX()),
									(transformMatrix.m31 + entitypatch.getOriginal().getY()),
									(transformMatrix.m32 + entitypatch.getOriginal().getZ()));
			                
			                projectile.shoot(
			                		direction.x,
			                		direction.y,
			                		direction.z,
			                		4.0f, 0.0f);
			                
			                worldIn.addFreshEntity(projectile);
						}
					}
				}
			}
		}
	}
	
	@Override
	public Pose getPoseByTime(LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
			Pose pose = super.getPoseByTime(entitypatch, time, partialTicks);
			
			this.getProperty(AttackAnimationProperty.ROTATE_X).ifPresent((flag) -> {
				if (flag) {
					float pitch = (float) Math.toDegrees(entitypatch.getOriginal().getViewVector(1.0f).y);
					JointTransform armR = pose.getOrDefaultTransform("Arm_R");
					armR.frontResult(JointTransform.getRotation(Vector3f.XP.rotationDegrees(-pitch)), OpenMatrix4f::mulAsOriginFront);
					
					if (this.getPhaseByTime(partialTicks).getColliderJoint() != Armatures.BIPED.armR) {
						JointTransform armL = pose.getOrDefaultTransform("Arm_L");
						armL.frontResult(JointTransform.getRotation(Vector3f.XP.rotationDegrees(-pitch)), OpenMatrix4f::mulAsOriginFront);
					}
					
					JointTransform chest = pose.getOrDefaultTransform("Chest");
					chest.frontResult(JointTransform.getRotation(Vector3f.XP.rotationDegrees((float) (pitch > 35f ? (-pitch + 35f):0f))), OpenMatrix4f::mulAsOriginFront);
					
					if (entitypatch instanceof PlayerPatch) {
						JointTransform head = pose.getOrDefaultTransform("Head");
						MathUtils.mulQuaternion(Vector3f.XP.rotationDegrees(-entitypatch.getAttackDirectionPitch()), head.rotation(), head.rotation());
					}
				}
			});
			
			return pose;
	}
	
	@Override
	public boolean isBasicAttackAnimation() {
		return false;
	}
}
