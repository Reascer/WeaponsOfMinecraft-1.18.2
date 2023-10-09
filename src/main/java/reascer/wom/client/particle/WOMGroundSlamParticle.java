package reascer.wom.client.particle;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.level.block.FractureBlockState;

@OnlyIn(Dist.CLIENT)
public class WOMGroundSlamParticle extends NoRenderParticle {
	protected WOMGroundSlamParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
		super(level, x, y, z, dx, dy, dz);
		
		BlockPos blockpos = new BlockPos.MutableBlockPos(x, y, z);
		if (y < 0) {
			y = y-1;
			blockpos = new BlockPos.MutableBlockPos(x, y, z);
		}
		BlockState blockstate = level.getBlockState(blockpos.below());
		Minecraft mc = Minecraft.getInstance();
		
		float floorY = (float) y; 
		
		while (blockstate instanceof FractureBlockState) {
			blockpos = new BlockPos.MutableBlockPos(x, floorY--, z);
			blockstate = level.getBlockState(blockpos.below());
		}
		
		for (int i = 0; i < dy; i ++) {
			OpenMatrix4f mat = OpenMatrix4f.createRotatorDeg((float)Math.random() * 360.0F, Vec3f.Y_AXIS);
			Vec3f positionVec = OpenMatrix4f.transform3v(mat, Vec3f.Z_AXIS, null).scale((float)dx);
			Vec3f moveVec = OpenMatrix4f.transform3v(mat, Vec3f.Z_AXIS, null).scale((float)dz);
			
			// Particle block side
			Particle blockParticle = new TerrainParticle(level, x + positionVec.x, y, z + positionVec.z, 0, 0, 0, blockstate, blockpos);
			blockParticle.setParticleSpeed((
					moveVec.x + (Math.random()-0.5)) * 0.3D,
					(Math.random()) * 0.5D,
					(moveVec.z + (Math.random()-0.5)) * 0.3D);
			blockParticle.setLifetime(60 + (new Random().nextInt(20)));
			
			// Particle block up
			Particle blockParticle2 = new TerrainParticle(level, x + positionVec.x, y, z + positionVec.z, 0, 0, 0, blockstate, blockpos);
			blockParticle2.setParticleSpeed((
					moveVec.x + (Math.random()-0.5)) * 0.1D,
					(Math.random()) * dz * 1.5,
					(moveVec.z + (Math.random()-0.5)) * 0.1D);
			blockParticle2.setLifetime(60 + (new Random().nextInt(20)));
			
			Particle smokeParticle = mc.particleEngine.createParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x + positionVec.x * 0.5D, y + dz * 1.5, z + positionVec.z * 0.5D, 0, 0, 0); 
			smokeParticle.setParticleSpeed(moveVec.x * 0.08D, moveVec.y * Math.random() * 0.08D, moveVec.z * 0.08D);
			smokeParticle.scale((float) (dz*2f));
			if (!blockstate.is(Blocks.WATER)) {
				mc.particleEngine.add(blockParticle);
				mc.particleEngine.add(blockParticle2);
				mc.particleEngine.add(smokeParticle);
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Provider implements ParticleProvider<SimpleParticleType> {
		@Override
		public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new WOMGroundSlamParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
		}
	}
}