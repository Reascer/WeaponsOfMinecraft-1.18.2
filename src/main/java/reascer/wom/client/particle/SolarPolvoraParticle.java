package reascer.wom.client.particle;

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
public class SolarPolvoraParticle extends HitParticle {
	public SolarPolvoraParticle(ClientLevel world, double x, double y, double z, SpriteSet animatedSprite) {
		super(world, x, y, z, animatedSprite);
	    this.rCol = 1.0F;
	    this.gCol = 1.0F;
	    this.bCol = 1.0F;
	    this.quadSize = 2F;
		this.lifetime = 7;
		this.setSpriteFromAge(animatedSprite);
		
		float angle = 0.0f;
		this.oRoll = angle;
		this.roll = angle;
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

			SolarPolvoraParticle particle = new SolarPolvoraParticle(worldIn, x, y, z, spriteSet);
			return particle;
		}
	}
}