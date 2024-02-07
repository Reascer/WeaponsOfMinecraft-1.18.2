package reascer.wom.config;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.api.utils.math.Vec2i;
import yesman.epicfight.client.gui.widget.ColorSlider;
import yesman.epicfight.config.Option.DoubleOption;
import yesman.epicfight.config.Option.IntegerOption;

public class WOMConfigurationIngame {
	public static final float A_TICK = 0.05F;
	public static final float GENERAL_ANIMATION_CONVERT_TIME = 0.15F;
	
	public final Option<Boolean> stongerMobs;
	public final Option<Boolean> mobsDropEmeralds;
	public final Option<Boolean> mobsMoreXP;
	public final Option<Boolean> skeletonMelee;
	public final IntegerOption skeletonMeleePercent;
	
	public WOMConfigurationIngame() {
		WOMClientConfig config = WOMConfigManager.INGAME_CONFIG;
		this.stongerMobs = new Option<Boolean>(config.stongerMobs.get());
		this.mobsDropEmeralds = new Option<Boolean>(config.mobsDropEmeralds.get());
		this.mobsMoreXP = new Option<Boolean>(config.mobsMoreXP.get());
		this.skeletonMelee = new Option<Boolean>(config.skeletonMelee.get());
		this.skeletonMeleePercent = new IntegerOption(config.skeletonMeleePercent.get(), 1, 100);
	}
	
	public void resetSettings() {
		this.stongerMobs.setDefaultValue();
		this.mobsDropEmeralds.setDefaultValue();
		this.mobsMoreXP.setDefaultValue();
		this.skeletonMelee.setDefaultValue();
		this.skeletonMeleePercent.setDefaultValue();
	}
	
	public void save() {
		WOMClientConfig config = WOMConfigManager.INGAME_CONFIG;
		config.stongerMobs.set(this.stongerMobs.getValue());
		config.mobsDropEmeralds.set(this.mobsDropEmeralds.getValue());
		config.mobsMoreXP.set(this.mobsMoreXP.getValue());
		config.skeletonMelee.set(this.skeletonMelee.getValue());
		config.skeletonMeleePercent.set(this.skeletonMeleePercent.getValue());
	}
}