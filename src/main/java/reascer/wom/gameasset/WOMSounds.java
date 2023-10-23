package reascer.wom.gameasset;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WOMSounds {
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "wom");
	public static final RegistryObject<SoundEvent> ENDERBLASTER_RELOAD = registerSound("sfx.enderblaster_reload");
	public static final RegistryObject<SoundEvent> ANTITHEUS_BLACKKHOLE = registerSound("sfx.antitheus_blackhole");
	public static final RegistryObject<SoundEvent> ANTITHEUS_BLACKKHOLE_CHARGEUP = registerSound("sfx.antitheus_blackhole_chargeup");
	
	private static RegistryObject<SoundEvent> registerSound(String name) {
		ResourceLocation res = new ResourceLocation("wom", name);
		return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(res));

	}
}