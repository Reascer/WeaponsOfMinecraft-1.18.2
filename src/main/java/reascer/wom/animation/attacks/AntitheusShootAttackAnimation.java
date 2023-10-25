package reascer.wom.animation.attacks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import reascer.wom.world.entity.projectile.AntitheusDarkness;
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
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.LinkAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.HurtableEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.StunType;

public class AntitheusShootAttackAnimation extends BasicMultipleAttackAnimation {
	public AntitheusShootAttackAnimation(float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
		this(convertTime, antic, antic, contact, recovery, collider, colliderJoint, path, armature);
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
		
		this.addProperty(StaticAnimationProperty.POSE_MODIFIER, (self, pose, entitypatch, time, partialticks ) -> {
			if (self instanceof AttackAnimation) {
				float pitch = (float) Math.toDegrees(entitypatch.getOriginal().getViewVector(1.0f).y);
				
				JointTransform armR = pose.getOrDefaultTransform("Arm_R");
				armR.frontResult(JointTransform.getRotation(Vector3f.XP.rotationDegrees(-pitch)), OpenMatrix4f::mulAsOriginFront);
				
				if (((AttackAnimation) self).getPhaseByTime(1).getColliders().get(0).getFirst() != Armatures.BIPED.armR) {
					JointTransform armL = pose.getOrDefaultTransform("Arm_L");
					armL.frontResult(JointTransform.getRotation(Vector3f.XP.rotationDegrees(-pitch)), OpenMatrix4f::mulAsOriginFront);
				}
				
				JointTransform chest = pose.getOrDefaultTransform("Chest");
				chest.frontResult(JointTransform.getRotation(Vector3f.XP.rotationDegrees((float) (pitch > 35f ? (-pitch + 35f):0f))), OpenMatrix4f::mulAsOriginFront);	
			}
		});
	}
	
	public AntitheusShootAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
		super(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, path, armature);
	}
	
	public AntitheusShootAttackAnimation(float convertTime, String path, Armature armature, Phase... phases) {
		super(convertTime, path, armature, phases);
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
					entitypatch.getArmature().initializeTransform();
					float prevPoseTime = prevElapsedTime;
					float poseTime = elapsedTime;
					List<Entity> list = this.getPhaseByTime(elapsedTime).getCollidingEntities(entitypatch, this, prevPoseTime, poseTime, this.getPlaySpeed(entitypatch));
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
						Joint joint = phase.getColliders().get(0).getFirst();
							
						OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(0.0f), joint);
						transformMatrix.translate(new Vec3f(-0.2f,0.4F,-0.4F));
						OpenMatrix4f.mul(new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0)),transformMatrix,transformMatrix);
						
						Vec3 direction = entitypatch.getOriginal().getViewVector(1.0F);
						Projectile projectile = new AntitheusDarkness(WOMEntities.ANTITHEUS_DARKNESS.get(),worldIn);
						projectile.setOwner(entitypatch.getOriginal());
		                projectile.setPosRaw((
		                		transformMatrix.m30 + entitypatch.getOriginal().getX()),
								(transformMatrix.m31 + entitypatch.getOriginal().getY()),
								(transformMatrix.m32 + entitypatch.getOriginal().getZ()));
		                
		                projectile.shoot(
		                		direction.x,
		                		direction.y,
		                		direction.z,
		                		2.0f, 0.0f);
		                
		                worldIn.addFreshEntity(projectile);
					} else {
						if (list2.get(list2.size()-1) != null) {
							int entityId = list2.get(list2.size()-1).getId();
							for (Entity entity : list2) {
								if (entity instanceof LivingEntity) {
									LivingEntity livingEntity = (LivingEntity) entity;
									if (livingEntity.getEffect(MobEffects.WITHER) != null) {
										entityId = livingEntity.getId();
									}
								}
							}
							entitypatch.getOriginal().addTag("antitheus_pull:"+entityId);
							((ServerLevel) entitypatch.getOriginal().level).playSound(null, entitypatch.getOriginal().getX(), entitypatch.getOriginal().getY(), entitypatch.getOriginal().getZ(),
				        			SoundEvents.WITHER_AMBIENT, (list2.get(list2.size()-1)).getSoundSource(), 0.4F, 2.0F);
							((ServerLevel) entitypatch.getOriginal().level).sendParticles(ParticleTypes.LARGE_SMOKE,
					      			(list2.get(list2.size()-1).getX()),
					      			(list2.get(list2.size()-1).getY() + list2.get(list2.size()-1).getBbHeight()/2),
					      			(list2.get(list2.size()-1).getZ()),
					      			20,
					      			0,
					      			0,
					      			0,
					      			0.1f);
							for (Entity entity : list2) {
								HurtableEntityPatch<?> hitHurtableEntityPatch = EpicFightCapabilities.getEntityPatch(entity, HurtableEntityPatch.class);
								if (phase.getProperty(AttackPhaseProperty.STUN_TYPE).isPresent()) {
									if (phase.getProperty(AttackPhaseProperty.STUN_TYPE).get() == StunType.NONE) {
										float stunTime = (float) (0.83f * (1.0F - ((LivingEntity) entity).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
										if (hitHurtableEntityPatch.getOriginal().isAlive()) {
											hitHurtableEntityPatch.setStunReductionOnHit(StunType.LONG);
											hitHurtableEntityPatch.applyStun(StunType.LONG, stunTime);
											hitHurtableEntityPatch.knockBackEntity(entitypatch.getOriginal().getPosition(1),3 * 0.25f);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public Pose getPoseByTime(LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
			Pose pose = super.getPoseByTime(entitypatch, time, partialTicks);
			
			float pitch = (float) Math.toDegrees(entitypatch.getOriginal().getViewVector(1.0f).y);
			JointTransform armR = pose.getOrDefaultTransform("Arm_R");
			armR.frontResult(JointTransform.getRotation(Vector3f.XP.rotationDegrees(-pitch)), OpenMatrix4f::mulAsOriginFront);
			
			if (this.getPhaseByTime(partialTicks).getColliders().get(0).getFirst() != Armatures.BIPED.armR) {
				JointTransform armL = pose.getOrDefaultTransform("Arm_L");
				armL.frontResult(JointTransform.getRotation(Vector3f.XP.rotationDegrees(-pitch)), OpenMatrix4f::mulAsOriginFront);
			}
			
			JointTransform chest = pose.getOrDefaultTransform("Chest");
			chest.frontResult(JointTransform.getRotation(Vector3f.XP.rotationDegrees((float) (pitch > 35f ? (-pitch + 35f):0f))), OpenMatrix4f::mulAsOriginFront);
			
			if (entitypatch instanceof PlayerPatch) {
				JointTransform head = pose.getOrDefaultTransform("Head");
				MathUtils.mulQuaternion(Vector3f.XP.rotationDegrees(-entitypatch.getAttackDirectionPitch()), head.rotation(), head.rotation());
			}
			
			return pose;
	}
	
	@Override
	public boolean isBasicAttackAnimation() {
		return false;
	}
}
