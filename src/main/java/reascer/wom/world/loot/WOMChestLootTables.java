package reascer.wom.world.loot;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import reascer.wom.main.WeaponsOfMinecraft;
import reascer.wom.world.item.WOMItems;

@Mod.EventBusSubscriber(modid = WeaponsOfMinecraft.MODID)
public class WOMChestLootTables {
	@SubscribeEvent
	public static void modifyVanillaLootPools(final LootTableLoadEvent event) {
    	if (event.getName().equals(BuiltInLootTables.STRONGHOLD_CORRIDOR)) {
    		event.getTable().addPool(LootPool.lootPool()
    			.add(LootItem.lootTableItem(WOMItems.RUINE.get()).setWeight(1))
    			.add(LootItem.lootTableItem(Items.AIR).setWeight(10))
    		.build());
    		
    		event.getTable().addPool(LootPool.lootPool()
        		.add(LootItem.lootTableItem(WOMItems.EMERALD_ANKLEBRACELET.get()).setWeight(1))
        		.add(LootItem.lootTableItem(WOMItems.EMERALD_CHAKRA.get()).setWeight(1))
        		.add(LootItem.lootTableItem(WOMItems.EMERALD_EARRINGS.get()).setWeight(1))
        		.add(LootItem.lootTableItem(WOMItems.EMERALD_TASSET.get()).setWeight(1))
        		.add(LootItem.lootTableItem(Items.AIR).setWeight(20))
        	.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.STRONGHOLD_CROSSING)) {
    		event.getTable().addPool(LootPool.lootPool()
    			.add(LootItem.lootTableItem(WOMItems.RUINE.get()).setWeight(1))
    			.add(LootItem.lootTableItem(Items.AIR).setWeight(10))
    		.build());
    		
    		event.getTable().addPool(LootPool.lootPool()
            		.add(LootItem.lootTableItem(WOMItems.EMERALD_ANKLEBRACELET.get()).setWeight(1))
            		.add(LootItem.lootTableItem(WOMItems.EMERALD_CHAKRA.get()).setWeight(1))
            		.add(LootItem.lootTableItem(WOMItems.EMERALD_EARRINGS.get()).setWeight(1))
            		.add(LootItem.lootTableItem(WOMItems.EMERALD_TASSET.get()).setWeight(1))
            		.add(LootItem.lootTableItem(Items.AIR).setWeight(20))
            	.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.SIMPLE_DUNGEON)) {
    		event.getTable().addPool(LootPool.lootPool()
    			.add(LootItem.lootTableItem(WOMItems.AGONY.get()).setWeight(1))
    			.add(LootItem.lootTableItem(Items.AIR).setWeight(10))
    		.build());
    		
    		event.getTable().addPool(LootPool.lootPool()
            		.add(LootItem.lootTableItem(WOMItems.DIAMOND_ARMBRACELET.get()).setWeight(1))
            		.add(LootItem.lootTableItem(WOMItems.DIAMOND_CROWN.get()).setWeight(1))
            		.add(LootItem.lootTableItem(WOMItems.DIAMOND_LEGBOTTOMSEAL.get()).setWeight(1))
            		.add(LootItem.lootTableItem(WOMItems.DIAMOND_LEGTOPSEAL.get()).setWeight(1))
            		.add(LootItem.lootTableItem(Items.AIR).setWeight(20))
            	.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.WOODLAND_MANSION)) {
    		event.getTable().addPool(LootPool.lootPool()
    			.add(LootItem.lootTableItem(WOMItems.TORMENTED_MIND.get()).setWeight(1))
    			.add(LootItem.lootTableItem(Items.AIR).setWeight(10))
    		.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.PILLAGER_OUTPOST)) {
    		event.getTable().addPool(LootPool.lootPool()
    			.add(LootItem.lootTableItem(WOMItems.TORMENTED_MIND.get()).setWeight(1))
    			.add(LootItem.lootTableItem(Items.AIR).setWeight(20))
    		.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.END_CITY_TREASURE)) {
    		event.getTable().addPool(LootPool.lootPool()
    			.add(LootItem.lootTableItem(WOMItems.ENDER_BLASTER.get()).setWeight(1))
    			.add(LootItem.lootTableItem(Items.AIR).setWeight(20))
    		.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.NETHER_BRIDGE)) {
    		event.getTable().addPool(LootPool.lootPool()
    			.add(LootItem.lootTableItem(WOMItems.DEMON_SEAL.get()).setWeight(1))
    			.add(LootItem.lootTableItem(Items.AIR).setWeight(20))
    		.build());
    		
    		event.getTable().addPool(LootPool.lootPool()
        		.add(LootItem.lootTableItem(WOMItems.NETHERITE_BELT.get()).setWeight(1))
        		.add(LootItem.lootTableItem(WOMItems.NETHERITE_CHAINS.get()).setWeight(1))
        		.add(LootItem.lootTableItem(WOMItems.NETHERITE_MANICLE.get()).setWeight(1))
        		.add(LootItem.lootTableItem(WOMItems.NETHERITE_MASK.get()).setWeight(1))
        		.add(LootItem.lootTableItem(Items.AIR).setWeight(20))
        	.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.BASTION_TREASURE)) {
    		event.getTable().addPool(LootPool.lootPool()
    			.add(LootItem.lootTableItem(WOMItems.DEMON_SEAL.get()).setWeight(1))
    			.add(LootItem.lootTableItem(Items.AIR).setWeight(10))
    		.build());
    		
    		event.getTable().addPool(LootPool.lootPool()
            		.add(LootItem.lootTableItem(WOMItems.NETHERITE_BELT.get()).setWeight(1))
            		.add(LootItem.lootTableItem(WOMItems.NETHERITE_CHAINS.get()).setWeight(1))
            		.add(LootItem.lootTableItem(WOMItems.NETHERITE_MANICLE.get()).setWeight(1))
            		.add(LootItem.lootTableItem(WOMItems.NETHERITE_MASK.get()).setWeight(1))
            		.add(LootItem.lootTableItem(Items.AIR).setWeight(20))
            	.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.UNDERWATER_RUIN_BIG)) {
    		event.getTable().addPool(LootPool.lootPool()
    			.add(LootItem.lootTableItem(WOMItems.HERRSCHER.get()).setWeight(1))
    			.add(LootItem.lootTableItem(Items.AIR).setWeight(20))
    		.build());
    		
    		event.getTable().addPool(LootPool.lootPool()
        		.add(LootItem.lootTableItem(WOMItems.GESETZ.get()).setWeight(1))
        		.add(LootItem.lootTableItem(Items.AIR).setWeight(20))
        	.build());
    		
    		event.getTable().addPool(LootPool.lootPool()
        		.add(LootItem.lootTableItem(WOMItems.GOLDEN_CHRONO.get()).setWeight(1))
        		.add(LootItem.lootTableItem(WOMItems.GOLDEN_KIT.get()).setWeight(1))
        		.add(LootItem.lootTableItem(WOMItems.GOLDEN_MOKASSIN.get()).setWeight(1))
        		.add(LootItem.lootTableItem(WOMItems.GOLDEN_MONOCLE.get()).setWeight(1))
        		.add(LootItem.lootTableItem(Items.AIR).setWeight(20))
        	.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.UNDERWATER_RUIN_SMALL)) {
    		event.getTable().addPool(LootPool.lootPool()
    			.add(LootItem.lootTableItem(WOMItems.HERRSCHER.get()).setWeight(1))
    			.add(LootItem.lootTableItem(Items.AIR).setWeight(50))
    		.build());
    		
    		event.getTable().addPool(LootPool.lootPool()
    			.add(LootItem.lootTableItem(WOMItems.GESETZ.get()).setWeight(1))
    			.add(LootItem.lootTableItem(Items.AIR).setWeight(50))
    		.build());
    		
    		event.getTable().addPool(LootPool.lootPool()
            		.add(LootItem.lootTableItem(WOMItems.GOLDEN_CHRONO.get()).setWeight(1))
            		.add(LootItem.lootTableItem(WOMItems.GOLDEN_KIT.get()).setWeight(1))
            		.add(LootItem.lootTableItem(WOMItems.GOLDEN_MOKASSIN.get()).setWeight(1))
            		.add(LootItem.lootTableItem(WOMItems.GOLDEN_MONOCLE.get()).setWeight(1))
            		.add(LootItem.lootTableItem(Items.AIR).setWeight(20))
            	.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.DESERT_PYRAMID)) {
    		event.getTable().addPool(LootPool.lootPool()
    			.add(LootItem.lootTableItem(WOMItems.MOONLESS.get()).setWeight(1))
    			.add(LootItem.lootTableItem(Items.AIR).setWeight(20))
    		.build());
    		
    		event.getTable().addPool(LootPool.lootPool()
            		.add(LootItem.lootTableItem(WOMItems.GOLDEN_CHRONO.get()).setWeight(1))
            		.add(LootItem.lootTableItem(WOMItems.GOLDEN_KIT.get()).setWeight(1))
            		.add(LootItem.lootTableItem(WOMItems.GOLDEN_MOKASSIN.get()).setWeight(1))
            		.add(LootItem.lootTableItem(WOMItems.GOLDEN_MONOCLE.get()).setWeight(1))
            		.add(LootItem.lootTableItem(Items.AIR).setWeight(20))
            	.build());
    	}
    }
}

