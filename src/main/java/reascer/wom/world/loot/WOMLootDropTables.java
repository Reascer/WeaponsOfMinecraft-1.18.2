package reascer.wom.world.loot;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import reascer.wom.main.WeaponsOfMinecraft;
import reascer.wom.world.item.WOMItems;
import yesman.epicfight.api.forgeevent.SkillLootTableRegistryEvent;
import yesman.epicfight.config.ConfigManager;
import yesman.epicfight.data.loot.function.SetSkillFunction;
import yesman.epicfight.world.item.EpicFightItems;

@Mod.EventBusSubscriber(modid = WeaponsOfMinecraft.MODID , bus = EventBusSubscriber.Bus.MOD)
public class WOMLootDropTables {
	@SubscribeEvent
	public static void SkillLootDrop(SkillLootTableRegistryEvent event) {
		int modifier = ConfigManager.SKILL_BOOK_MOB_DROP_CHANCE_MODIFIER.get();
		int dropChance = 100 + modifier;
		int antiDropChance = 100 - modifier;
		float dropChanceModifier = (antiDropChance == 0) ? Float.MAX_VALUE : dropChance / (float)antiDropChance;
		event.add(EntityType.ZOMBIE,LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(
				LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
				.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
						1.0F, "wom:heart_shield",
						1.0F, "wom:pain_anticipation",
						1.0F, "wom:precise_roll",
						1.0F, "wom:bull_charge",
						1.0F, "wom:adrenaline"
				))
	    	)).add(EntityType.HUSK, 
				LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
				.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
						1.0F, "wom:heart_shield",
						1.0F, "wom:pain_anticipation",
						1.0F, "wom:precise_roll",
						1.0F, "wom:bull_charge",
						1.0F, "wom:adrenaline"
				))
	    	)).add(EntityType.DROWNED, 
				LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
				.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
						1.0F, "wom:heart_shield",
						1.0F, "wom:pain_anticipation",
						1.0F, "wom:precise_roll",
						1.0F, "wom:bull_charge",
						1.0F, "wom:adrenaline"
				))
	    	)).add(EntityType.SKELETON, 
				LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
				.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
						1.0F, "wom:mindset",
						1.0F, "wom:dodge_master",
						1.0F, "wom:arrow_tenacity",
						1.0F, "wom:counter_attack",
						0.5F, "wom:vampirize"
				))
	    	)).add(EntityType.STRAY, 
				LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
				.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
						1.0F, "wom:mindset",
						1.0F, "wom:dodge_master",
						1.0F, "wom:arrow_tenacity",
						1.0F, "wom:counter_attack",
						0.5F, "wom:vampirize"
				))
	    	)).add(EntityType.SPIDER, 
				LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
				.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
						"wom:perfect_bulwark",
						"wom:bull_charge",
						"wom:precise_roll"
				))
	    	)).add(EntityType.CAVE_SPIDER, 
				LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
				.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
						"wom:perfect_bulwark",
						"wom:bull_charge",
						"wom:precise_roll"
				))
	    	)).add(EntityType.CREEPER, 
				LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
				.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
						"wom:perfect_bulwark",
						"wom:critical_knowledge",
						"wom:dancing_blade"
				))
	    	)).add(EntityType.ENDERMAN, 
				LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
				.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
						1.0F, "wom:ender_step",
						1.0F, "wom:meditation",
						1.0F, "wom:mindset",
						1.0F, "wom:critical_knowledge",
						1.0F, "wom:counter_attack",
						0.2F, "wom:ender_obscuris"
				))
	    	)).add(EntityType.VINDICATOR, 
				LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
				.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
						"wom:critical_knowledge",
						"wom:adrenaline",
						"wom:latent_retribution",
						"wom:bull_charge",
						"wom:precise_roll"
				))
	    	)).add(EntityType.PILLAGER, 
				LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
				.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
						"wom:adrenaline",
						"wom:latent_retribution",
						"wom:arrow_tenacity",
						"wom:precise_roll"
				))
	    	)).add(EntityType.WITCH, 
				LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
				.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
						"wom:meditation",
						"wom:heart_shield",
						"wom:dancing_blade"
				))
	    	)).add(EntityType.PIGLIN, 
				LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
				.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
						"wom:vampirize",
						"wom:heart_shield",
						"wom:adrenaline",
						"wom:precise_roll"
				))
	    	)).add(EntityType.PIGLIN_BRUTE, 
				LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
				.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
						"wom:critical_knowledge",
						"wom:vampirize",
						"wom:latent_retribution",
						"wom:vengeful_parry"
				))
	    	)).add(EntityType.ZOMBIFIED_PIGLIN, 
				LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
				.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
						"wom:adrenaline",
						"wom:pain_anticipation",
						"wom:bull_charge",
						"wom:vampirize"
				))
	    	)).add(EntityType.WITHER_SKELETON, 
				LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
				.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
						1.0F, "wom:critical_knowledge",
						1.0F, "wom:latent_retribution",
						1.0F, "wom:vampirize",
						1.0F, "wom:dodge_master",
						1.0F, "wom:counter_attack",
						1.0F, "wom:vengeful_parry",
						0.5F, "wom:shadow_step"
				))
	    	)).add(EntityType.WITHER, 
				LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
				.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
						"wom:shadow_step"
				))
	    	));
	}
	
}

