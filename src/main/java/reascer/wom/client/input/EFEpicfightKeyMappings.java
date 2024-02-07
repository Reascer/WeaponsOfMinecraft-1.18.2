package reascer.wom.client.input;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import yesman.epicfight.main.EpicFightMod;

public class EFEpicfightKeyMappings {
	public static final KeyMapping HEAVY_ATTACK = new KeyMapping("key." + EpicFightMod.MODID + ".heavy_attack", 4, "key." + EpicFightMod.MODID + ".combat");
	
	public static void registerKeys() {
		ClientRegistry.registerKeyBinding(HEAVY_ATTACK);
	}
}
