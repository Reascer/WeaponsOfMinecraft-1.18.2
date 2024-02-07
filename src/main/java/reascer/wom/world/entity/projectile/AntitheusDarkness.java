package reascer.wom.world.entity.projectile;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfig.Server;
import reascer.wom.particle.WOMParticles;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.HurtableEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.IndirectEpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.DealtDamageEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class AntitheusDarkness extends AbstractHurtingProjectile {

   public AntitheusDarkness(EntityType<? extends AntitheusDarkness> p_37598_, Level p_37599_) {
	   super(p_37598_, p_37599_);
   }
   
   public AntitheusDarkness(Level p_37609_, LivingEntity p_37610_, double p_37611_, double p_37612_, double p_37613_) {
      super(WOMEntities.ANTITHEUS_DARKNESS.get(), p_37610_, p_37611_, p_37612_, p_37613_, p_37609_);
   }

   public boolean isOnFire() {
      return false;
   }
   
   @Override
   public void tick() {
      Entity entity = this.getOwner();
      if (this.level.isClientSide || (entity == null || !entity.isRemoved()) && this.level.hasChunkAt(this.blockPosition())) {
         if (this.shouldBurn()) {
            this.setSecondsOnFire(1);
         }

         HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
         if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
         }

         this.checkInsideBlocks();
         Vec3 vec3 = this.getDeltaMovement();
         double d0 = this.getX() + vec3.x;
         double d1 = this.getY() + vec3.y;
         double d2 = this.getZ() + vec3.z;
         ProjectileUtil.rotateTowardsMovement(this, 1.0F);
         float f = this.getInertia();
         if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
               this.level.addParticle(ParticleTypes.BUBBLE, d0 - vec3.x * 0.25D, d1 - vec3.y * 0.25D, d2 - vec3.z * 0.25D, vec3.x, vec3.y, vec3.z);
            }

            f = 0.8F;
         }

         this.setDeltaMovement(vec3.add(this.xPower, this.yPower, this.zPower).scale((double)f));
         int numberOf = 5;
         float partialScale = 1.0F / (numberOf - 1);
         float interpolation = 0;
         for (int i = 0; i < numberOf; i++) {
        	 
        	 float x = (float) this.xOld;
        	 float y = (float) this.yOld;
        	 float z = (float) this.zOld;
        	 MathUtils.lerpBetween( x, (float) d0, interpolation);
        	 MathUtils.lerpBetween( y, (float) d1, interpolation);
        	 MathUtils.lerpBetween( z, (float) d2, interpolation);
        	 this.level.addParticle(this.getTrailParticle(),
        			 d0 + ((new Random().nextFloat() - 0.5f)*0.1f),
        			 d1 + ((new Random().nextFloat() - 0.5f)*0.1f),
        			 d2 + ((new Random().nextFloat() - 0.5f)*0.1f),
        			 ((new Random().nextFloat() - 0.5f)*0.17f),
        			 ((new Random().nextFloat() - 0.5f)*0.17f) -0.05f,
        			 ((new Random().nextFloat() - 0.5f)*0.17f));
        	 
        	 this.level.addParticle(this.getTrailParticle(),
        			 d0,
        			 d1,
        			 d2,
        			 0,
        			 -0.05f,
        			 0);
        	 interpolation += partialScale;
         }
         d0 = this.getX() + vec3.x;
         d1 = this.getY() + vec3.y;
         d2 = this.getZ() + vec3.z;
         this.setPos(d0, d1, d2);
         if (this.tickCount > 60 || this.touchingUnloadedChunk()) {
        	 this.discard();
         }
      } else {
         this.discard();
      }
   }
   
   @Override
   protected float getInertia() {
		return 0.98f;
   }
   
   
   
   protected void onHitEntity(EntityHitResult p_37626_) {
      super.onHitEntity(p_37626_);
      if (!this.level.isClientSide) {
         Entity entity = p_37626_.getEntity();
         Entity entity1 = this.getOwner();
         boolean flag;
         if (entity1 instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entity1;
            IndirectEpicFightDamageSource damage = (IndirectEpicFightDamageSource) new IndirectEpicFightDamageSource("demon_fee", entity1, this, StunType.LONG).setImpact(2f);
            int prevInvulTime = entity.invulnerableTime;
            entity.invulnerableTime = 0;
            float entity1damage = 4f;
            float enchantmentDamage = 0;
            
            if (entity instanceof LivingEntity) {
            	((ServerLevel) this.level).playSound(null, livingentity.getX(), livingentity.getY(), livingentity.getZ(),
            			SoundEvents.WITHER_AMBIENT, this.getSoundSource(), 0.4F, 2.0F);
            	enchantmentDamage = EnchantmentHelper.getDamageBonus(livingentity.getItemInHand(InteractionHand.MAIN_HAND), ((LivingEntity) entity).getMobType());
            	entity1damage += enchantmentDamage;
            }
            
            flag = entity.hurt(damage, (entity1damage) * (1 + (EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, livingentity) / 3)));
            HurtableEntityPatch<?> hitHurtableEntityPatch = EpicFightCapabilities.getEntityPatch(entity, HurtableEntityPatch.class);
            if (hitHurtableEntityPatch != null) {
            	hitHurtableEntityPatch.knockBackEntity(livingentity.getPosition(1), 2f * 0.25F);
			}
            if (enchantmentDamage != 0) {
            	((ServerLevel) this.level).sendParticles(ParticleTypes.ENCHANTED_HIT,
            			(this.getX()),
            			(this.getY()),
            			(this.getZ()),
            			20,
            			0,
            			0,
            			0,
            			0.5f);
            }
            
            entity.invulnerableTime = prevInvulTime;
            if (flag) {
            	if (entity1 instanceof Player) {
	        		 ServerPlayerPatch PlayerPatch = EpicFightCapabilities.getEntityPatch(entity1, ServerPlayerPatch.class);
	               	 if (entity instanceof LivingEntity) {
	               		 PlayerPatch.getEventListener().triggerEvents(EventType.DEALT_DAMAGE_EVENT_POST, new DealtDamageEvent(PlayerPatch,(LivingEntity) entity, damage, (entity1damage) * (1 +(EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, livingentity) / 4))));
	               	 }
	               	 if (entity.isAlive()) {
	                     this.doEnchantDamageEffects(livingentity, entity);
	                 }
           	}
            }
            if (entity instanceof LivingEntity) {
            	if (!((LivingEntity) entity).isDeadOrDying()) {
            		if (!(entity instanceof ArmorStand)) {
						livingentity.setLastHurtMob(entity);
						livingentity.addTag("antitheus_pull:"+ entity.getId());
					}
				}
            }
         } else {
            flag = entity.hurt(DamageSource.MAGIC, 4.0F);
         }
      }
   }
   
   @Override
	protected ParticleOptions getTrailParticle() {
		return ParticleTypes.LARGE_SMOKE;
	}
   
   protected void onHit(HitResult hitResult) {
      super.onHit(hitResult);
      if (!this.level.isClientSide) {
    	  ((ServerLevel) this.level).sendParticles(WOMParticles.ANTITHEUS_PUNCH.get(),
  		        (this.getX()),
  		        (this.getY()),
  		        (this.getZ()),
  		        1,
  		        0,
  		        0,
  		        0,
  		        0);
    	  ((ServerLevel) this.level).sendParticles(ParticleTypes.LARGE_SMOKE,
      			(this.getX()),
      			(this.getY()),
      			(this.getZ()),
      			20,
      			0,
      			0,
      			0,
      			0.1f);
        ((ServerLevel) this.level).playSound(null, this.getX(), this.getY(), this.getZ(),
    			SoundEvents.WITHER_BREAK_BLOCK, this.getSoundSource(), 0.8F, 1.0F);
         this.discard();
      }

   }

   public boolean isPickable() {
      return false;
   }

   public boolean hurt(DamageSource p_37616_, float p_37617_) {
      return false;
   }

   protected boolean shouldBurn() {
      return false;
   }
}
