package reascer.wom.world.capabilities.item;

import java.util.function.Function;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.WOMColliders;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.main.WeaponsOfMinecraft;
import reascer.wom.skill.weaponinnate.SoulSnatchSkill;
import reascer.wom.skill.weaponpassive.LunarEclipsePassiveSkill;
import reascer.wom.skill.weaponpassive.SatsujinPassive;
import reascer.wom.world.item.WOMItems;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.forgeevent.WeaponCapabilityPresetRegistryEvent;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

@Mod.EventBusSubscriber(modid = WeaponsOfMinecraft.MODID , bus = EventBusSubscriber.Bus.MOD)
public class WOMWeaponCapabilityPresets {
	public static final Function<Item, CapabilityItem.Builder> STAFF = (item) -> {
		CapabilityItem.Builder builder = WeaponCapability.builder()
			.category(WeaponCategories.SPEAR)
			.styleProvider((playerpatch) -> Styles.TWO_HAND)
			.collider(WOMColliders.STAFF)
			.hitSound(EpicFightSounds.BLUNT_HIT)
			.hitParticle(EpicFightParticles.HIT_BLUNT.get())
			.canBePlacedOffhand(false)
			.newStyleCombo(Styles.TWO_HAND, WOMAnimations.STAFF_AUTO_1, WOMAnimations.STAFF_AUTO_2, WOMAnimations.STAFF_AUTO_3, WOMAnimations.STAFF_DASH, WOMAnimations.STAFF_KINKONG)
			.newStyleCombo(Styles.MOUNT, Animations.SPEAR_MOUNT_ATTACK)
			.innateSkill(Styles.TWO_HAND,(itemstack) -> WOMSkills.CHARYBDIS)
			.comboCancel((style) -> {
				return false;
			})
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, WOMAnimations.STAFF_IDLE)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, Animations.BIPED_HOLD_SPEAR)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, WOMAnimations.STAFF_RUN)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, WOMAnimations.STAFF_RUN)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.SPEAR_GUARD);
		return builder;
	};
	
	public static final Function<Item, CapabilityItem.Builder> AGONY = (item) -> {
		CapabilityItem.Builder builder = WeaponCapability.builder()
				.category(WeaponCategories.SPEAR)
				.styleProvider((playerpatch) -> Styles.TWO_HAND)
				.collider(WOMColliders.AGONY)
				.hitSound(EpicFightSounds.BLADE_HIT)
				.canBePlacedOffhand(false)
				.newStyleCombo(Styles.TWO_HAND, WOMAnimations.AGONY_AUTO_1, WOMAnimations.AGONY_AUTO_2, WOMAnimations.AGONY_AUTO_3, WOMAnimations.AGONY_AUTO_4, WOMAnimations.AGONY_CLAWSTRIKE, WOMAnimations.AGONY_AIR_SLASH)
				.newStyleCombo(Styles.MOUNT, Animations.SPEAR_MOUNT_ATTACK)
				.innateSkill(Styles.TWO_HAND,(itemstack) -> WOMSkills.AGONY_PLUNGE)
				.comboCancel((style) -> {
					return false;
				})
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, WOMAnimations.AGONY_IDLE)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, WOMAnimations.AGONY_WALK)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, WOMAnimations.AGONY_RUN)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, WOMAnimations.AGONY_RUN)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, WOMAnimations.AGONY_GUARD);
		return builder;
	};
	
	public static final Function<Item, CapabilityItem.Builder> TORMENT = (item) -> {
		CapabilityItem.Builder builder = WeaponCapability.builder()
				.category(WOMWeaponCategories.TORMENT)
				.styleProvider((entitypatch) -> {
					if (entitypatch instanceof PlayerPatch<?>) {
						if (((PlayerPatch<?>)entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getRemainDuration() > 0) {
							return Styles.OCHS;
						}
					}
					return Styles.TWO_HAND;
				})
				.collider(WOMColliders.TORMENT)
				.hitSound(EpicFightSounds.BLADE_HIT)
				.swingSound(EpicFightSounds.WHOOSH_BIG)
				.canBePlacedOffhand(false)
				.newStyleCombo(Styles.TWO_HAND, WOMAnimations.TORMENT_AUTO_1, WOMAnimations.TORMENT_AUTO_2, WOMAnimations.TORMENT_AUTO_3, WOMAnimations.TORMENT_AUTO_4, WOMAnimations.TORMENT_DASH, WOMAnimations.TORMENT_AIRSLAM)
				.newStyleCombo(Styles.OCHS, WOMAnimations.TORMENT_BERSERK_AUTO_1, WOMAnimations.TORMENT_BERSERK_AUTO_2, WOMAnimations.TORMENT_BERSERK_DASH, WOMAnimations.TORMENT_BERSERK_AIRSLAM)
				.newStyleCombo(Styles.MOUNT, Animations.SWORD_MOUNT_ATTACK)
				.innateSkill(Styles.TWO_HAND,(itemstack) -> WOMSkills.TRUE_BERSERK)
				.innateSkill(Styles.OCHS,(itemstack) -> WOMSkills.TRUE_BERSERK)
				.passiveSkill(WOMSkills.TORMENT_PASSIVE)
				.comboCancel((style) -> {
					return false;
				})
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, WOMAnimations.TORMENT_IDLE)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, WOMAnimations.TORMENT_WALK)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, WOMAnimations.TORMENT_RUN)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, WOMAnimations.TORMENT_RUN)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, WOMAnimations.TORMENT_CHARGE)
				.livingMotionModifier(Styles.OCHS, LivingMotions.IDLE, WOMAnimations.TORMENT_BERSERK_IDLE)
				.livingMotionModifier(Styles.OCHS, LivingMotions.WALK, WOMAnimations.TORMENT_BERSERK_WALK)
				.livingMotionModifier(Styles.OCHS, LivingMotions.CHASE, WOMAnimations.TORMENT_BERSERK_RUN)
				.livingMotionModifier(Styles.OCHS, LivingMotions.RUN, WOMAnimations.TORMENT_BERSERK_RUN)
				.livingMotionModifier(Styles.OCHS, LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR);
		return builder;
	};
	
	public static final Function<Item, CapabilityItem.Builder> RUINE = (item) -> {
		CapabilityItem.Builder builder = WeaponCapability.builder()
			.category(WeaponCategories.LONGSWORD)
			.styleProvider((entitypatch) -> {
				if (entitypatch instanceof PlayerPatch<?>) {
					if (((PlayerPatch<?>)entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(SoulSnatchSkill.BUFFED) != null) {
						if (((PlayerPatch<?>)entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(SoulSnatchSkill.BUFFED)) {
							return Styles.OCHS;
						}
					}
				}
				return Styles.TWO_HAND;
			})
			.hitSound(EpicFightSounds.BLADE_HIT)
			.collider(WOMColliders.RUINE)
			.canBePlacedOffhand(false)
			.newStyleCombo(Styles.TWO_HAND, WOMAnimations.RUINE_AUTO_1, WOMAnimations.RUINE_AUTO_2, WOMAnimations.RUINE_AUTO_3, WOMAnimations.RUINE_AUTO_4, WOMAnimations.RUINE_DASH, WOMAnimations.RUINE_COMET)
			.newStyleCombo(Styles.OCHS, WOMAnimations.RUINE_AUTO_1, WOMAnimations.RUINE_AUTO_2, WOMAnimations.RUINE_AUTO_3, WOMAnimations.RUINE_AUTO_4, WOMAnimations.RUINE_DASH, WOMAnimations.RUINE_COMET)
			.newStyleCombo(Styles.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.innateSkill(Styles.TWO_HAND,(itemstack) -> WOMSkills.SOUL_SNATCH)
			.innateSkill(Styles.OCHS,(itemstack) -> WOMSkills.SOUL_SNATCH)
			.passiveSkill(WOMSkills.RUINE_PASSIVE)
			.comboCancel((style) -> {
				return false;
			})
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, WOMAnimations.RUINE_IDLE)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, WOMAnimations.RUINE_WALK)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, WOMAnimations.RUINE_RUN)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SNEAK, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.KNEEL, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.JUMP, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, WOMAnimations.RUINE_BLOCK)
			.livingMotionModifier(Styles.OCHS, LivingMotions.IDLE, WOMAnimations.RUINE_BOOSTED_IDLE)
			.livingMotionModifier(Styles.OCHS, LivingMotions.WALK, WOMAnimations.RUINE_BOOSTED_WALK)
			.livingMotionModifier(Styles.OCHS, LivingMotions.CHASE, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.OCHS, LivingMotions.RUN, WOMAnimations.RUINE_RUN)
			.livingMotionModifier(Styles.OCHS, LivingMotions.SNEAK, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.OCHS, LivingMotions.KNEEL, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.OCHS, LivingMotions.JUMP, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.OCHS, LivingMotions.SWIM, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.OCHS, LivingMotions.BLOCK, WOMAnimations.RUINE_BLOCK);
		return builder;
	};
	
	public static final Function<Item, CapabilityItem.Builder> SATSUJIN = (item) -> {
		CapabilityItem.Builder builder = WeaponCapability.builder()
			.category(WeaponCategories.UCHIGATANA)
			.styleProvider((entitypatch) -> {
				if (entitypatch instanceof PlayerPatch) {
					PlayerPatch<?> playerpatch = (PlayerPatch<?>)entitypatch;
					if (playerpatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().hasData(SatsujinPassive.SHEATH) && 
							playerpatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().getDataValue(SatsujinPassive.SHEATH)) {
						return Styles.SHEATH;
					}
				}
				return Styles.TWO_HAND;
			})
			.passiveSkill(WOMSkills.SATSUJIN_PASSIVE)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.collider(WOMColliders.KATANA)
			.canBePlacedOffhand(false)
			.newStyleCombo(Styles.SHEATH, WOMAnimations.KATANA_SHEATHED_AUTO_1, WOMAnimations.KATANA_SHEATHED_AUTO_2, WOMAnimations.KATANA_SHEATHED_AUTO_3, WOMAnimations.KATANA_SHEATHED_DASH, WOMAnimations.HERRSCHER_AUSROTTUNG)
			.newStyleCombo(Styles.TWO_HAND, WOMAnimations.KATANA_AUTO_1, WOMAnimations.KATANA_AUTO_2, WOMAnimations.KATANA_AUTO_3, WOMAnimations.KATANA_DASH, WOMAnimations.HERRSCHER_AUSROTTUNG)
			.newStyleCombo(Styles.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.innateSkill(Styles.SHEATH,(itemstack) -> WOMSkills.SAKURA_STATE)
			.innateSkill(Styles.TWO_HAND,(itemstack) -> WOMSkills.SAKURA_STATE)
			.comboCancel((style) -> {
				return false;
			})
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, WOMAnimations.KATANA_IDLE)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.KNEEL, WOMAnimations.KATANA_IDLE)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, WOMAnimations.KATANA_WALK)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, WOMAnimations.KATANA_RUN)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, WOMAnimations.KATANA_RUN)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SNEAK, WOMAnimations.KATANA_IDLE)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, WOMAnimations.KATANA_IDLE)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.FLOAT, WOMAnimations.KATANA_IDLE)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.FALL, WOMAnimations.KATANA_IDLE)
			.livingMotionModifier(Styles.SHEATH, LivingMotions.IDLE, WOMAnimations.KATANA_SHEATHED_IDLE)
			.livingMotionModifier(Styles.SHEATH, LivingMotions.KNEEL,  WOMAnimations.KATANA_SHEATHED_IDLE)
			.livingMotionModifier(Styles.SHEATH, LivingMotions.WALK,  WOMAnimations.KATANA_SHEATHED_IDLE)
			.livingMotionModifier(Styles.SHEATH, LivingMotions.CHASE,  WOMAnimations.KATANA_SHEATHED_RUN)
			.livingMotionModifier(Styles.SHEATH, LivingMotions.RUN,  WOMAnimations.KATANA_SHEATHED_RUN)
			.livingMotionModifier(Styles.SHEATH, LivingMotions.SNEAK,  WOMAnimations.KATANA_SHEATHED_IDLE)
			.livingMotionModifier(Styles.SHEATH, LivingMotions.SWIM,  WOMAnimations.KATANA_SHEATHED_IDLE)
			.livingMotionModifier(Styles.SHEATH, LivingMotions.FLOAT,  WOMAnimations.KATANA_SHEATHED_IDLE)
			.livingMotionModifier(Styles.SHEATH, LivingMotions.FALL, WOMAnimations.KATANA_SHEATHED_IDLE)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, WOMAnimations.KATANA_GUARD);
		return builder;
	};
	
	public static final Function<Item, CapabilityItem.Builder> ENDER_BLASTER = (item) -> {
		CapabilityItem.Builder builder = WeaponCapability.builder()
			.category(WOMWeaponCategories.ENDERBLASTER)
			.styleProvider((playerpatch) -> playerpatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() == WOMWeaponCategories.ENDERBLASTER ? Styles.TWO_HAND : Styles.ONE_HAND)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.collider(WOMColliders.PUNCH)
			.newStyleCombo(Styles.ONE_HAND, WOMAnimations.ENDERBLASTER_ONEHAND_AUTO_1, WOMAnimations.ENDERBLASTER_ONEHAND_AUTO_2, WOMAnimations.ENDERBLASTER_ONEHAND_AUTO_3, WOMAnimations.ENDERBLASTER_ONEHAND_AUTO_4, WOMAnimations.ENDERBLASTER_ONEHAND_DASH, WOMAnimations.ENDERBLASTER_ONEHAND_JUMPKICK)
			.newStyleCombo(Styles.TWO_HAND, WOMAnimations.ENDERBLASTER_TWOHAND_AUTO_1, WOMAnimations.ENDERBLASTER_TWOHAND_AUTO_2, WOMAnimations.ENDERBLASTER_TWOHAND_AUTO_3, WOMAnimations.ENDERBLASTER_TWOHAND_AUTO_4, WOMAnimations.ENDERBLASTER_ONEHAND_DASH, WOMAnimations.ENDERBLASTER_TWOHAND_TISHNAW)
			.newStyleCombo(Styles.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.innateSkill(Styles.ONE_HAND,(itemstack) -> WOMSkills.ENDER_BLAST)
			.innateSkill(Styles.TWO_HAND,(itemstack) -> WOMSkills.ENDER_FUSION)
			.comboCancel((style) -> {
				return false;
			})
			.livingMotionModifier(Styles.ONE_HAND, LivingMotions.IDLE, WOMAnimations.ENDERBLASTER_ONEHAND_IDLE)
			.livingMotionModifier(Styles.ONE_HAND, LivingMotions.WALK, WOMAnimations.ENDERBLASTER_ONEHAND_WALK)
			.livingMotionModifier(Styles.ONE_HAND, LivingMotions.CHASE, WOMAnimations.ENDERBLASTER_ONEHAND_RUN)
			.livingMotionModifier(Styles.ONE_HAND, LivingMotions.RUN, WOMAnimations.ENDERBLASTER_ONEHAND_RUN)
			.livingMotionModifier(Styles.ONE_HAND, LivingMotions.SWIM, Animations.BIPED_SWIM)
			.livingMotionModifier(Styles.ONE_HAND, LivingMotions.BLOCK, WOMAnimations.ENDERBLASTER_ONEHAND_AIMING)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, WOMAnimations.ENDERBLASTER_TWOHAND_IDLE)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, WOMAnimations.ENDERBLASTER_ONEHAND_WALK)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, WOMAnimations.ENDERBLASTER_ONEHAND_RUN)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, WOMAnimations.ENDERBLASTER_ONEHAND_RUN)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_SWIM)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, WOMAnimations.ENDERBLASTER_TWOHAND_AIMING)
			.weaponCombinationPredicator((entitypatch) -> EpicFightCapabilities.getItemStackCapability(entitypatch.getOriginal().getOffhandItem()).getWeaponCollider() == WOMColliders.PUNCH);
		return builder;
	};
	
	public static final Function<Item, CapabilityItem.Builder> ANTITHEUS = (item) -> {
		CapabilityItem.Builder builder = WeaponCapability.builder()
				.category(WOMWeaponCategories.ANTITHEUS)
				.styleProvider((entitypatch) -> {
					if (entitypatch instanceof PlayerPatch<?>) {
						if (((PlayerPatch<?>)entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getRemainDuration() > 0) {
							return Styles.OCHS;
						}
					}
					return Styles.TWO_HAND;
				})
				.collider(WOMColliders.ANTITHEUS)
				.hitSound(EpicFightSounds.BLADE_HIT)
				.swingSound(EpicFightSounds.WHOOSH)
				.passiveSkill(WOMSkills.DEMON_MARK_PASSIVE)
				.canBePlacedOffhand(false)
				.newStyleCombo(Styles.TWO_HAND, WOMAnimations.ANTITHEUS_AUTO_1, WOMAnimations.ANTITHEUS_AUTO_2, WOMAnimations.ANTITHEUS_AUTO_3, WOMAnimations.ANTITHEUS_AUTO_4, WOMAnimations.ANTITHEUS_AGRESSION, WOMAnimations.ANTITHEUS_GUILLOTINE)
				.newStyleCombo(Styles.OCHS, WOMAnimations.ANTITHEUS_ASCENDED_AUTO_1, WOMAnimations.ANTITHEUS_ASCENDED_AUTO_2, WOMAnimations.ANTITHEUS_ASCENDED_AUTO_3, WOMAnimations.ANTITHEUS_ASCENDED_BLINK, WOMAnimations.ANTITHEUS_ASCENDED_DEATHFALL)
				.newStyleCombo(Styles.MOUNT, Animations.SWORD_MOUNT_ATTACK)
				.innateSkill(Styles.TWO_HAND,(itemstack) -> WOMSkills.DEMONIC_ASCENSION)
				.innateSkill(Styles.OCHS,(itemstack) -> WOMSkills.DEMONIC_ASCENSION)
				.comboCancel((style) -> {
					return false;
				})
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, WOMAnimations.ANTITHEUS_IDLE)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, WOMAnimations.ANTITHEUS_WALK)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, WOMAnimations.ANTITHEUS_RUN)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, WOMAnimations.ANTITHEUS_RUN)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, WOMAnimations.ANTITHEUS_AIMING)
				.livingMotionModifier(Styles.OCHS, LivingMotions.IDLE, WOMAnimations.ANTITHEUS_ASCENDED_IDLE)
				.livingMotionModifier(Styles.OCHS, LivingMotions.WALK, WOMAnimations.ANTITHEUS_ASCENDED_WALK)
				.livingMotionModifier(Styles.OCHS, LivingMotions.CHASE, WOMAnimations.ANTITHEUS_ASCENDED_RUN)
				.livingMotionModifier(Styles.OCHS, LivingMotions.RUN, WOMAnimations.ANTITHEUS_ASCENDED_RUN)
				.livingMotionModifier(Styles.OCHS, LivingMotions.SWIM, Animations.BIPED_SWIM);
		return builder;
	};
		
	public static final Function<Item, CapabilityItem.Builder> HERRSCHER = (item) -> {
		CapabilityItem.Builder builder = WeaponCapability.builder()
			.category(WeaponCategories.SWORD)
			.styleProvider((playerpatch) -> {
				if (playerpatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() == WeaponCategories.SWORD && !playerpatch.getOriginal().getItemInHand(InteractionHand.OFF_HAND).is(WOMItems.GESETZ.get())) {
					return Styles.TWO_HAND;
				} else if(playerpatch.getOriginal().getItemInHand(InteractionHand.OFF_HAND).is(WOMItems.GESETZ.get())) {
					return Styles.OCHS;
				} else {
					return Styles.ONE_HAND;
				}
			})
			
			.collider(WOMColliders.HERSCHER)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.comboCancel((style) -> {
				return false;
			})
			.newStyleCombo(Styles.ONE_HAND, Animations.SWORD_AUTO1, Animations.SWORD_AUTO2, Animations.SWORD_AUTO3, Animations.SWORD_DASH, Animations.SWORD_AIR_SLASH)
			.newStyleCombo(Styles.TWO_HAND, Animations.SWORD_DUAL_AUTO1, Animations.SWORD_DUAL_AUTO2, Animations.SWORD_DUAL_AUTO3, Animations.SWORD_DUAL_DASH, Animations.SWORD_DUAL_AIR_SLASH)
			.newStyleCombo(Styles.OCHS, WOMAnimations.HERRSCHER_AUTO_1, WOMAnimations.HERRSCHER_AUTO_2, WOMAnimations.HERRSCHER_AUTO_3, WOMAnimations.HERRSCHER_BEFREIUNG, WOMAnimations.HERRSCHER_AUSROTTUNG)
			.newStyleCombo(Styles.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.innateSkill(Styles.ONE_HAND,(itemstack) -> EpicFightSkills.SWEEPING_EDGE)
			.innateSkill(Styles.TWO_HAND,(itemstack) -> EpicFightSkills.DANCING_EDGE)
			.innateSkill(Styles.OCHS,(itemstack) -> WOMSkills.REGIERUNG)
			.livingMotionModifier(Styles.ONE_HAND, LivingMotions.BLOCK, Animations.SWORD_GUARD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.SWORD_DUAL_GUARD)
			.livingMotionModifier(Styles.OCHS, LivingMotions.IDLE, WOMAnimations.HERRSCHER_IDLE)
			.livingMotionModifier(Styles.OCHS, LivingMotions.WALK, WOMAnimations.HERRSCHER_WALK)
			.livingMotionModifier(Styles.OCHS, LivingMotions.RUN, WOMAnimations.HERRSCHER_RUN)
			.livingMotionModifier(Styles.OCHS, LivingMotions.BLOCK, WOMAnimations.HERRSCHER_GUARD)
			.weaponCombinationPredicator((entitypatch) -> {
				return EpicFightCapabilities.getItemStackCapability(entitypatch.getOriginal().getOffhandItem()).getWeaponCategory() == WeaponCategories.SWORD || entitypatch.getOriginal().getItemInHand(InteractionHand.OFF_HAND).is(WOMItems.GESETZ.get());
			});
		
		if (item instanceof TieredItem) {
			int harvestLevel = ((TieredItem)item).getTier().getLevel();
			builder.addStyleAttibutes(CapabilityItem.Styles.COMMON, Pair.of(EpicFightAttributes.IMPACT.get(), EpicFightAttributes.getImpactModifier(0.5D + 0.2D * harvestLevel)));
			builder.addStyleAttibutes(CapabilityItem.Styles.COMMON, Pair.of(EpicFightAttributes.MAX_STRIKES.get(), EpicFightAttributes.getMaxStrikesModifier(1)));
		}
		
		return builder;
	};
	
	public static final Function<Item, CapabilityItem.Builder> MOONLESS = (item) -> {
		CapabilityItem.Builder builder = WeaponCapability.builder()
				.category(WeaponCategories.TACHI)
				.styleProvider((entitypatch) -> {
					if (entitypatch instanceof PlayerPatch<?> playerpatch && (playerpatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().hasData(LunarEclipsePassiveSkill.VERSO) &&
								playerpatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().getDataValue(LunarEclipsePassiveSkill.VERSO))) {
							return Styles.OCHS;
					}
					return Styles.TWO_HAND;
				})
				.collider(WOMColliders.MOONLESS)
				.hitSound(EpicFightSounds.BLADE_HIT)
				.canBePlacedOffhand(false)
				.newStyleCombo(Styles.TWO_HAND, WOMAnimations.MOONLESS_AUTO_1, WOMAnimations.MOONLESS_AUTO_2, WOMAnimations.MOONLESS_AUTO_3, WOMAnimations.MOONLESS_REVERSED_BYPASS, WOMAnimations.MOONLESS_CRESCENT)
				.innateSkill(Styles.TWO_HAND,(itemstack) -> WOMSkills.LUNAR_ECHO)
				.newStyleCombo(Styles.OCHS, WOMAnimations.MOONLESS_AUTO_1_VERSO, WOMAnimations.MOONLESS_AUTO_2_VERSO,WOMAnimations.MOONLESS_AUTO_3_VERSO, WOMAnimations.MOONLESS_BYPASS, WOMAnimations.MOONLESS_FULLMOON)
				.innateSkill(Styles.OCHS,(itemstack) -> WOMSkills.LUNAR_ECHO)
				.newStyleCombo(Styles.MOUNT, Animations.SWORD_MOUNT_ATTACK)
				.passiveSkill(WOMSkills.LUNAR_ECLIPSE_PASSIVE)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, WOMAnimations.MOONLESS_IDLE)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, WOMAnimations.MOONLESS_WALK)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, WOMAnimations.MOONLESS_RUN)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, WOMAnimations.MOONLESS_RUN)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, WOMAnimations.MOONLESS_GUARD)
				.livingMotionModifier(Styles.OCHS, LivingMotions.IDLE, WOMAnimations.MOONLESS_IDLE)
				.livingMotionModifier(Styles.OCHS, LivingMotions.WALK, WOMAnimations.MOONLESS_WALK)
				.livingMotionModifier(Styles.OCHS, LivingMotions.CHASE, WOMAnimations.MOONLESS_RUN)
				.livingMotionModifier(Styles.OCHS, LivingMotions.RUN, WOMAnimations.MOONLESS_RUN)
				.livingMotionModifier(Styles.OCHS, LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR)
				.livingMotionModifier(Styles.OCHS, LivingMotions.BLOCK, WOMAnimations.MOONLESS_GUARD);
		return builder;
	};
	
	public static final Function<Item, CapabilityItem.Builder> SOLAR = (item) -> {
		CapabilityItem.Builder builder = WeaponCapability.builder()
				.category(WeaponCategories.GREATSWORD)
				.styleProvider((playerpatch) -> Styles.TWO_HAND)
				.collider(WOMColliders.SOLAR)
				.hitSound(EpicFightSounds.BLADE_HIT)
				.swingSound(EpicFightSounds.WHOOSH_BIG)
				.canBePlacedOffhand(false)
				.newStyleCombo(Styles.TWO_HAND, WOMAnimations.TORMENT_AUTO_1, WOMAnimations.TORMENT_AUTO_2, WOMAnimations.RUINE_AUTO_2, WOMAnimations.RUINE_AUTO_3, WOMAnimations.TORMENT_DASH, WOMAnimations.TORMENT_AIRSLAM)
				.newStyleCombo(Styles.MOUNT, Animations.SWORD_MOUNT_ATTACK)
				.innateSkill(Styles.TWO_HAND,(itemstack) -> EpicFightSkills.STEEL_WHIRLWIND)
				.comboCancel((style) -> {
					return false;
				})
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, WOMAnimations.SOLAR_IDLE)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, WOMAnimations.SOLAR_WALK)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, WOMAnimations.SOLAR_RUN)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, WOMAnimations.SOLAR_RUN)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_HOLD_GREATSWORD)
				.livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.GREATSWORD_GUARD);
		return builder;
	};
	
	@SubscribeEvent
	public static void register(WeaponCapabilityPresetRegistryEvent event) {
		event.getTypeEntry().put("staff", STAFF);
		event.getTypeEntry().put("agony", AGONY);
		event.getTypeEntry().put("torment", TORMENT);
		event.getTypeEntry().put("ruine", RUINE);
		event.getTypeEntry().put("satsujin", SATSUJIN);
		event.getTypeEntry().put("ender_blaster", ENDER_BLASTER);
		event.getTypeEntry().put("antitheus", ANTITHEUS);
		event.getTypeEntry().put("herrscher", HERRSCHER);
		event.getTypeEntry().put("moonless", MOONLESS);
		event.getTypeEntry().put("solar", SOLAR);
	}
	
}
