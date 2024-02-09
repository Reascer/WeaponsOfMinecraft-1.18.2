package reascer.wom.world.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import reascer.wom.main.WeaponsOfMinecraft;

public class WOMEntities {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, WeaponsOfMinecraft.MODID);
	
	public static final RegistryObject<EntityType<EnderBullet>> ENDERBLAST = ENTITIES.register("enderblast", () ->
		EntityType.Builder.<EnderBullet>of(EnderBullet::new, MobCategory.MISC)
		.sized(1f, 1f).clientTrackingRange(12).updateInterval(20).build("enderblast")
		);
	
	public static final RegistryObject<EntityType<AntitheusDarkness>> ANTITHEUS_DARKNESS = ENTITIES.register("antitheus_darkness", () ->
	EntityType.Builder.<AntitheusDarkness>of(AntitheusDarkness::new, MobCategory.MISC)
	.sized(1f, 1f).clientTrackingRange(12).updateInterval(20).build("antitheus_darkness")
	);
}