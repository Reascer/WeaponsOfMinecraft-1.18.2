package reascer.wom.world.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import reascer.wom.main.WeaponsOfMinecraft;
import yesman.epicfight.world.item.EpicFightItems;

public class WOMCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WeaponsOfMinecraft.MODID);

	public static final RegistryObject<CreativeModeTab> ITEMS = TABS.register("items",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.wom.items"))
					.icon(() -> new ItemStack(WOMItems.AGONY.get()))
					.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
					.displayItems((params, output) -> {
						WOMItems.ITEMS.getEntries().forEach(it -> {
							// FIXME: bad implement, maybe based protocol better yet.
							// ignore UCHIGATANA_SHEATH
							if (it == WOMItems.GESETZ_HANDLE) {
								return;
							}
							if (it == WOMItems.MOONLESS_HANDLE) {
								return;
							}
							if (it == WOMItems.SATSUJIN_SHEATH) {
								return;
							}
							output.accept(it.get());
						});
					})
					.build());
}