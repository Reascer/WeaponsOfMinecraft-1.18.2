package reascer.wom.client.particle;

import java.util.Random;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.particle.HitParticle;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class AntitheusCutParticle extends HitParticle {
	public AntitheusCutParticle(ClientLevel world, double x, double y, double z, SpriteSet animatedSprite) {
		super(world, x, y, z, animatedSprite);
	    this.rCol = 1.0F;
	    this.gCol = 1.0F;
	    this.bCol = 1.0F;
	    this.quadSize = 1.5F;
		this.lifetime = 9;
		this.setSpriteFromAge(animatedSprite);
		
		this.oRoll = 0;
		this.roll = 0;
	}
	
	@Override
	public void tick() {
	      this.xo = this.x;
	      this.yo = this.y;
	      this.zo = this.z;
	      if (this.age++ >= this.lifetime) {
	         this.remove();
	      } else {
	         this.setSpriteFromAge(this.animatedSprite);
	      }
	   }
	
	@OnlyIn(Dist.CLIENT)
	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public Provider(SpriteSet spriteSet) {
	         this.spriteSet = spriteSet;
	    }
	    
		@Override
		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			AntitheusCutParticle particle = new AntitheusCutParticle(worldIn, x, y, z, spriteSet);
			Float rand = new Random().nextFloat();
			particle.roll = (float)Math.toRadians(45.0F + ((rand - 0.5F) * 45F) + (xSpeed == 0 ? 0f : 180f));
			particle.oRoll = (float)Math.toRadians(45.0F + ((rand - 0.5F) * 45F) + (xSpeed == 0 ? 0f : 180f));
			if (xSpeed == -90) {
				particle.oRoll = (float)Math.toRadians(45.0F - 90F);
				particle.roll = (float)Math.toRadians(45.0F - 90F);
			}
			if (xSpeed == 90) {
				particle.oRoll = (float)Math.toRadians(45.0F + 90F);
				particle.roll = (float)Math.toRadians(45.0F + 90F);
			}
			return particle;
		}
	}
}