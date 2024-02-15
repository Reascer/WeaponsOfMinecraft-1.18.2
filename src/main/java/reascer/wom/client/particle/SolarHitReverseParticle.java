package reascer.wom.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.particle.WOMParticles;
import yesman.epicfight.particle.EpicFightParticles;

@OnlyIn(Dist.CLIENT)
public class SolarHitReverseParticle extends NoRenderParticle {
	public SolarHitReverseParticle(ClientLevel world, double x, double y, double z, double width, double height, double _null) {
		super(world, x, y, z);
		this.x = x + (this.random.nextDouble() - 0.5D) * width;
		this.y = y + (this.random.nextDouble() + height) * 0.5;
		this.z = z + (this.random.nextDouble() - 0.5D) * width;
		this.level.addParticle(WOMParticles.SOLAR_CUT.get(), this.x, this.y, this.z, 180.0D, 0.0D, 0.0D);
		double d = 0.2F;
		
		for(int i = 0; i < 6; i++) {
			double particleMotionX = this.random.nextDouble() * d;
			d = d * (this.random.nextBoolean() ? 1.0D : -1.0D);
			double particleMotionZ = this.random.nextDouble() * d;
			d = d * (this.random.nextBoolean() ? 1.0D : -1.0D);
			this.level.addParticle(EpicFightParticles.BLOOD.get(), this.x, this.y, this.z, particleMotionX, 0.0D, particleMotionZ);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Provider implements ParticleProvider<SimpleParticleType> {
		@Override
		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			SolarHitReverseParticle particle = new SolarHitReverseParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
			return particle;
		}
	}
}