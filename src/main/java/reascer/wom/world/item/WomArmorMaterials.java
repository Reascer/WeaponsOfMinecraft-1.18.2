package reascer.wom.world.item;

import java.util.function.Supplier;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import yesman.epicfight.main.EpicFightMod;

public enum WomArmorMaterials implements ArmorMaterial {
	   GOLD("golden_artefacts", 25, new int[]{1, 1, 2, 1}, 25, SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> {
	      return Ingredient.of(Items.GOLD_INGOT);
	   }, new int[]{3, 4, 4, 3} , new int[]{0, 0, 0, 0}),
	   DIAMOND("diamond_artefacts", 35, new int[]{1, 3, 4, 2}, 15, SoundEvents.ARMOR_EQUIP_DIAMOND, 1.0F, 0.0F, () -> {
	      return Ingredient.of(Items.DIAMOND);
	   }, new int[]{5, 5, 5, 5}, new int[]{0, 0, 0, 0}),
	   EMERALD("emerald_artefacts", 40, new int[]{1, 3, 4, 2}, 20, SoundEvents.ARMOR_EQUIP_CHAIN, 1.25F, 0.0F, () -> {
	      return Ingredient.of(Items.EMERALD);
	   }, new int[]{7, 8, 8, 7}, new int[]{0, 0, 0, 0}),
	   NETHERITE("netherite_artefacts", 45, new int[]{1, 3, 4, 2}, 25, SoundEvents.ARMOR_EQUIP_NETHERITE, 1.5F, 0.0F, () -> {
	      return Ingredient.of(Items.NETHERITE_INGOT);
	   }, new int[]{10, 10, 10, 10}, new int[]{0, 0, 0, 0});
	
	private static final int[] HEALTH_PER_SLOT = new int[]{16, 16, 16, 16};
	private final String name;
	private final int enchantability;
	private final int durabilityMultiplier;
	private final int[] damageReductionAmountArray;
	private final int[] AddHealthAmountArray;
	private final int[] AddStaminaAmountArray;
	private final SoundEvent soundEvent;
	private final float toughness;
	private final float knockbackResistance;
	private final Ingredient repairMaterial;

	private WomArmorMaterials(String nameIn, int maxDamageFactorIn, int[] damageReductionAmountsIn, int enchantabilityIn,
			SoundEvent equipSoundIn, float toughness, float knockbackResistance, Supplier<Ingredient> repairMaterialSupplier,int[] addHealthAmountArray,int[] addStaminaAmountArray) {
		this.name = nameIn;
	    this.durabilityMultiplier = maxDamageFactorIn;
	    this.damageReductionAmountArray = damageReductionAmountsIn;
	    this.enchantability = enchantabilityIn;
		this.AddHealthAmountArray = addHealthAmountArray;
		this.AddStaminaAmountArray = addStaminaAmountArray;
	    this.soundEvent = equipSoundIn;
	    this.toughness = toughness;
	    this.knockbackResistance = knockbackResistance;
	    this.repairMaterial = repairMaterialSupplier.get();
	}
	
	@Override
	public String getName() {
		return EpicFightMod.MODID + ":" + this.name;
	}

	@Override
	public float getToughness() {
		return this.toughness;
	}

	@Override
	public float getKnockbackResistance() {
		return knockbackResistance;
	}

	@Override
	public int getDurabilityForType(ArmorItem.Type type) {
		return HEALTH_PER_SLOT[type.getSlot().getIndex()] * this.durabilityMultiplier;
	}
	
	public int getAddHealthAmountArray(EquipmentSlot slotIn) {
		return AddHealthAmountArray[slotIn.getIndex()];
	}

	public int getAddStaminaAmountArray(EquipmentSlot slotIn) {
		return AddStaminaAmountArray[slotIn.getIndex()];
	}
	
	@Override
	public int getDefenseForType(ArmorItem.Type type) {
		return this.damageReductionAmountArray[type.getSlot().getIndex()];
	}

	@Override
	public int getEnchantmentValue() {
		return this.enchantability;
	}

	@Override
	public SoundEvent getEquipSound() {
		return this.soundEvent;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairMaterial;
	}
	
}