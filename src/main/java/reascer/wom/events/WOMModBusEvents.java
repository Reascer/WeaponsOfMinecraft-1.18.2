package reascer.wom.events;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import reascer.wom.gameasset.WOMSounds;
import reascer.wom.main.WeaponsOfMinecraft;

@Mod.EventBusSubscriber(modid=WeaponsOfMinecraft.MODID, bus=EventBusSubscriber.Bus.MOD)
public class WOMModBusEvents {
    @SubscribeEvent
	public static void onSoundRegistry(final RegistryEvent.Register<SoundEvent> event) {
    	WOMSounds.registerSoundRegistry(event);
    }
}