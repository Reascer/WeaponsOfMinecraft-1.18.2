package reascer.wom.world.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import reascer.wom.main.WeaponsOfMinecraft;

public class WOMItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WeaponsOfMinecraft.MODID);
	
	public static final RegistryObject<Item> AGONY = ITEMS.register("agony", () -> new AgonySpearItem(new Item.Properties().rarity(Rarity.RARE)));
	public static final RegistryObject<Item> TORMENTED_MIND = ITEMS.register("tormented_mind", () -> new TormentedMindItem(new Item.Properties().rarity(Rarity.RARE)));
	public static final RegistryObject<Item> RUINE = ITEMS.register("ruine", () -> new RuineItem(new Item.Properties().rarity(Rarity.RARE)));
	public static final RegistryObject<Item> ENDER_BLASTER = ITEMS.register("ender_blaster", () -> new EnderBlasterItem(new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<Item> ANTITHEUS = ITEMS.register("antitheus", () -> new AntitheusItem(new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<Item> SATSUJIN = ITEMS.register("satsujin", () -> new SatsujinItem(new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<Item> SATSUJIN_SHEATH = ITEMS.register("satsujin_sheath", () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<Item> HERRSCHER = ITEMS.register("herrscher", () -> new HerscherItem(new Item.Properties().rarity(Rarity.RARE).defaultDurability(1582).durability(1582)));
	public static final RegistryObject<Item> GESETZ = ITEMS.register("gesetz", () -> new MagneticShieldItem(new Item.Properties().rarity(Rarity.RARE).defaultDurability(4157).durability(4157)));
	public static final RegistryObject<Item> GESETZ_HANDLE = ITEMS.register("gesetz_handle", () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<Item> MOONLESS = ITEMS.register("moonless", () -> new MoonlessItem(new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<Item> MOONLESS_HANDLE = ITEMS.register("moonless_handle", () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<Item> SOLAR = ITEMS.register("solar", () -> new SolarItem(new Item.Properties().rarity(Rarity.EPIC)));
	
	public static final RegistryObject<Item> IRON_GREATAXE = ITEMS.register("iron_greataxe", () -> new GreataxeItem(new Item.Properties(), Tiers.IRON));
	public static final RegistryObject<Item> GOLDEN_GREATAXE = ITEMS.register("golden_greataxe", () -> new GreataxeItem(new Item.Properties(), Tiers.GOLD));
	public static final RegistryObject<Item> DIAMOND_GREATAXE = ITEMS.register("diamond_greataxe", () -> new GreataxeItem(new Item.Properties(), Tiers.DIAMOND));
	public static final RegistryObject<Item> NETHERITE_GREATAXE = ITEMS.register("netherite_greataxe", () -> new GreataxeItem(new Item.Properties(), Tiers.NETHERITE));
	
	public static final RegistryObject<Item> WOODEN_STAFF = ITEMS.register("wooden_staff", () -> new StaffItem(new Item.Properties(), Tiers.WOOD));
	public static final RegistryObject<Item> STONE_STAFF = ITEMS.register("stone_staff", () -> new StaffItem(new Item.Properties(), Tiers.STONE));
	public static final RegistryObject<Item> IRON_STAFF = ITEMS.register("iron_staff", () -> new StaffItem(new Item.Properties(), Tiers.IRON));
	public static final RegistryObject<Item> GOLDEN_STAFF = ITEMS.register("golden_staff", () -> new StaffItem(new Item.Properties(), Tiers.GOLD));
	public static final RegistryObject<Item> DIAMOND_STAFF = ITEMS.register("diamond_staff", () -> new StaffItem(new Item.Properties(), Tiers.DIAMOND));
	public static final RegistryObject<Item> NETHERITE_STAFF = ITEMS.register("netherite_staff", () -> new StaffItem(new Item.Properties(), Tiers.NETHERITE));
	
	public static final RegistryObject<Item> NETHERITE_MASK = ITEMS.register("netherite_mask", () -> new ArtefactsItem(WomArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties()));
	public static final RegistryObject<Item> NETHERITE_MANICLE = ITEMS.register("netherite_manicle", () -> new ArtefactsItem(WomArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<Item> NETHERITE_BELT = ITEMS.register("netherite_belt", () -> new ArtefactsItem(WomArmorMaterials.NETHERITE, ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final RegistryObject<Item> NETHERITE_CHAINS = ITEMS.register("netherite_chains", () -> new ArtefactsItem(WomArmorMaterials.NETHERITE, ArmorItem.Type.BOOTS, new Item.Properties()));
	
	public static final RegistryObject<Item> EMERALD_EARRINGS = ITEMS.register("emerald_earrings", () -> new ArtefactsItem(WomArmorMaterials.EMERALD, ArmorItem.Type.HELMET, new Item.Properties()));
	public static final RegistryObject<Item> EMERALD_CHAKRA = ITEMS.register("emerald_chakra", () -> new ArtefactsItem(WomArmorMaterials.EMERALD, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<Item> EMERALD_TASSET = ITEMS.register("emerald_tasset", () -> new ArtefactsItem(WomArmorMaterials.EMERALD, ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final RegistryObject<Item> EMERALD_ANKLEBRACELET = ITEMS.register("emerald_anklebracelet", () -> new ArtefactsItem(WomArmorMaterials.EMERALD, ArmorItem.Type.BOOTS, new Item.Properties()));
	
	public static final RegistryObject<Item> DIAMOND_CROWN = ITEMS.register("diamond_crown", () -> new ArtefactsItem(WomArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, new Item.Properties()));
	public static final RegistryObject<Item> DIAMOND_ARMBRACELET = ITEMS.register("diamond_armbracelet", () -> new ArtefactsItem(WomArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<Item> DIAMOND_LEGTOPSEAL = ITEMS.register("diamond_legtopseal", () -> new ArtefactsItem(WomArmorMaterials.DIAMOND, ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final RegistryObject<Item> DIAMOND_LEGBOTTOMSEAL = ITEMS.register("diamond_legbottomseal", () -> new ArtefactsItem(WomArmorMaterials.DIAMOND, ArmorItem.Type.BOOTS, new Item.Properties()));
	
	public static final RegistryObject<Item> GOLDEN_MONOCLE = ITEMS.register("golden_monocle", () -> new ArtefactsItem(WomArmorMaterials.GOLD, ArmorItem.Type.HELMET, new Item.Properties()));
	public static final RegistryObject<Item> GOLDEN_KIT = ITEMS.register("golden_kit", () -> new ArtefactsItem(WomArmorMaterials.GOLD, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<Item> GOLDEN_CHRONO = ITEMS.register("golden_chrono", () -> new ArtefactsItem(WomArmorMaterials.GOLD, ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final RegistryObject<Item> GOLDEN_MOKASSIN = ITEMS.register("golden_mokassin", () -> new ArtefactsItem(WomArmorMaterials.GOLD, ArmorItem.Type.BOOTS, new Item.Properties()));
	
	
	public static final RegistryObject<Item> DEMON_SEAL = ITEMS.register("demon_seal", () -> new DemonSealItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
}
