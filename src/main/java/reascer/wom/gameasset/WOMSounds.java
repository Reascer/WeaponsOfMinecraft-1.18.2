package reascer.wom.gameasset;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import reascer.wom.main.WeaponsOfMinecraft;

public class WOMSounds {
	public static final SoundEvent ENDERBLASTER_RELOAD = registerSound("sfx.enderblaster_reload");
	public static final SoundEvent ANTITHEUS_BLACKKHOLE = registerSound("sfx.antitheus_blackhole");
	public static final SoundEvent ANTITHEUS_BLACKKHOLE_CHARGEUP = registerSound("sfx.antitheus_blackhole_chargeup");
	public static final SoundEvent SOLAR_HIT = registerSound("sfx.solar_hit");
	
	private static SoundEvent registerSound(String name) {
		ResourceLocation res = new ResourceLocation(WeaponsOfMinecraft.MODID, name);
		SoundEvent soundEvent = new SoundEvent(res).setRegistryName(name);
		return soundEvent;
	}
	
	public static void registerSoundRegistry(final RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().registerAll(
				ENDERBLASTER_RELOAD,
				ANTITHEUS_BLACKKHOLE,
				ANTITHEUS_BLACKKHOLE_CHARGEUP,
				SOLAR_HIT
	    );
	}
}