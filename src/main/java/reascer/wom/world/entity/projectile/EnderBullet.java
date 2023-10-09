package reascer.wom.world.entity.projectile;

import java.util.Random;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.particle.WOMParticles;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.SourceTags;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.DealtDamageEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class EnderBullet extends AbstractHurtingProjectile {

   public EnderBullet(EntityType<? extends EnderBullet> p_37598_, Level p_37599_) {
	   super(p_37598_, p_37599_);
   }
   
   public EnderBullet(Level p_37609_, LivingEntity p_37610_, double p_37611_, double p_37612_, double p_37613_) {
      super(WOMEntities.ENDERBLAST.get(), p_37610_, p_37611_, p_37612_, p_37613_, p_37609_);
   }

   public boolean isOnFire() {
      return false;
   }
   
   @Override
   public void tick() {
      Entity entity = this.getOwner();
      if (this.level().isClientSide || (entity == null || !entity.isRemoved())) {
         if (this.shouldBurn()) {
            this.setSecondsOnFire(1);
         }

         HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
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
               this.level().addParticle(ParticleTypes.BUBBLE, d0 - vec3.x * 0.25D, d1 - vec3.y * 0.25D, d2 - vec3.z * 0.25D, vec3.x, vec3.y, vec3.z);
            }

            f = 0.8F;
         }

         this.setDeltaMovement(vec3.add(this.xPower, this.yPower, this.zPower).scale((double)f));
         this.level().addParticle(this.getTrailParticle(), d0 + ((new Random().nextFloat() - 0.5f)*0.1f), d1 + ((new Random().nextFloat() - 0.5f)*0.17f), d2 + ((new Random().nextFloat() - 0.5f)*0.1f), 0.0D, 0.0D, 0.0D);
         this.setPos(d0, d1, d2);
         if ( Math.abs(getDeltaMovement().x) + Math.abs(getDeltaMovement().y) + Math.abs(getDeltaMovement().z) > 100.0 || Math.abs(getDeltaMovement().x) + Math.abs(getDeltaMovement().y) + Math.abs(getDeltaMovement().z) < 1.0) {
        	 this.discard();
         }
      } else {
         this.discard();
      }
   }
   
   @Override
   protected float getInertia() {
		return 1.5f;
   }
   
   
   
   protected void onHitEntity(EntityHitResult p_37626_) {
      super.onHitEntity(p_37626_);
      if (!this.level().isClientSide) {
         Entity entity = p_37626_.getEntity();
         Entity entity1 = this.getOwner();
         boolean flag;
         if (entity1 instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entity1;
            EpicFightDamageSource damage = new EpicFightDamageSource(this.damageSources().magic());
            damage.setAnimation(WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT);
            damage.setStunType(StunType.SHORT);
            damage.addTag(SourceTags.WEAPON_INNATE);
            int prevInvulTime = entity.invulnerableTime;
            entity.invulnerableTime = 0;
            float entity1damage = 7f;
            float enchantmentDamage = 0;
            if (entity instanceof LivingEntity) {
            	enchantmentDamage = EnchantmentHelper.getDamageBonus(livingentity.getItemInHand(InteractionHand.MAIN_HAND), ((LivingEntity) entity).getMobType());
            	entity1damage += enchantmentDamage;
            }
            flag = entity.hurt(damage, (entity1damage) * (1 +(EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, livingentity) / 4)));
            if (enchantmentDamage != 0) {
            	((ServerLevel) this.level()).sendParticles(ParticleTypes.ENCHANTED_HIT,
            			(p_37626_.getLocation().x),
            			(p_37626_.getLocation().y),
            			(p_37626_.getLocation().z),
            			20,
            			0,
            			0,
            			0,
            			0.5f);
            }
            
            ((ServerLevel) this.level()).playSound(null,
            		entity1.getX(),
            		entity1.getY(),
            		entity1.getZ(),
        			SoundEvents.FIREWORK_ROCKET_BLAST, this.getSoundSource(), 0.7F, 0.5F);
            
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
         } else {
            flag = entity.hurt(this.damageSources().magic(), 6.0F);
         }
      }
   }
   
   @Override
	protected ParticleOptions getTrailParticle() {
		return ParticleTypes.DRAGON_BREATH;
	}
   
   protected void onHit(HitResult hitResult) {
      super.onHit(hitResult);
      if (!this.level().isClientSide) {
    	  ((ServerLevel) this.level()).sendParticles(WOMParticles.ENDERBLASTER_BULLET.get(),
    			(this.getX()),
      			(this.getY()),
      			(this.getZ()),
  		        1,
  		        0,
  		        0,
  		        0,
  		        0);
        ((ServerLevel) this.level()).playSound(null,
        		(hitResult.getLocation().x),
      			(this.getY()),
      			(hitResult.getLocation().z),
    			SoundEvents.FIREWORK_ROCKET_BLAST, this.getSoundSource(), 0.8F, 0.5F);
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
