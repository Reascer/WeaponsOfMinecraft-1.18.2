package reascer.wom.gameasset;

import java.util.Set;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import reascer.wom.main.WeaponsOfMinecraft;
import reascer.wom.skill.dodges.BullChargeSkill;
import reascer.wom.skill.dodges.DodgeMasterSkill;
import reascer.wom.skill.dodges.EnderObscurisSkill;
import reascer.wom.skill.dodges.EnderStepSkill;
import reascer.wom.skill.dodges.ShadowStepSkill;
import reascer.wom.skill.guard.CounterAttack;
import reascer.wom.skill.guard.PerfectBulwarkSkill;
import reascer.wom.skill.guard.VengefulParry;
import reascer.wom.skill.passive.AdrenalineSkill;
import reascer.wom.skill.passive.ArrowTenacitySkill;
import reascer.wom.skill.passive.CriticalKnowledgeSkill;
import reascer.wom.skill.passive.DancingBladeSkill;
import reascer.wom.skill.passive.HeartShieldSkill;
import reascer.wom.skill.passive.LatentRetributionSkill;
import reascer.wom.skill.passive.MeditationSkill;
import reascer.wom.skill.passive.MindSetSkill;
import reascer.wom.skill.passive.PainAnticipationSkill;
import reascer.wom.skill.passive.VampirizeSkill;
import reascer.wom.skill.weaponinnate.AgonyPlungeSkill;
import reascer.wom.skill.weaponinnate.CharybdisSkill;
import reascer.wom.skill.weaponinnate.DemonicAscensionSkill;
import reascer.wom.skill.weaponinnate.EnderBlastSkill;
import reascer.wom.skill.weaponinnate.EnderFusionSkill;
import reascer.wom.skill.weaponinnate.LunarEclipseSkill;
import reascer.wom.skill.weaponinnate.RegierungSkill;
import reascer.wom.skill.weaponinnate.SakuraStateSkill;
import reascer.wom.skill.weaponinnate.SoulSnatchSkill;
import reascer.wom.skill.weaponinnate.TrueBerserkSkill;
import reascer.wom.skill.weaponpassive.DemonMarkPassiveSkill;
import reascer.wom.skill.weaponpassive.LunarEchoPassiveSkill;
import reascer.wom.skill.weaponpassive.RuinePassive;
import reascer.wom.skill.weaponpassive.SatsujinPassive;
import reascer.wom.skill.weaponpassive.TormentPassiveSkill;
import reascer.wom.world.damagesources.WOMExtraDamageInstance;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.dodge.DodgeSkill;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.skill.weaponinnate.ConditionalWeaponInnateSkill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;

@Mod.EventBusSubscriber(modid = WeaponsOfMinecraft.MODID , bus = EventBusSubscriber.Bus.FORGE)
public class WOMSkills {
	public static Skill ENDERSTEP;
	public static Skill ENDEROBSCURIS;
	public static Skill SHADOWSTEP;
	public static Skill DODGEMASTER;
	public static Skill KNIGHT_ROLL;
	public static Skill BULL_CHARGE;
	
	public static Skill KICK;
	
	// Guard --------------------------------------------------------
	public static Skill COUNTERATTACK_PASSIVE;
	public static Skill VENGEFUL_PARRY;
	public static Skill PERFECT_BULWARK;
	
	// Weapon Skill --------------------------------------------------------
	//public static Skill BLOSSOM; 
	public static Skill CHARYBDIS;
	public static Skill AGONY_PLUNGE;
	public static Skill TRUE_BERSERK;
	public static Skill DEMONIC_ASCENSION;
	public static Skill SOUL_SNATCH;
	public static Skill REGIERUNG;
	public static Skill SAKURA_STATE;
	public static Skill ENDER_BLAST;
	public static Skill ENDER_FUSION;
	public static Skill lUNAR_ECLIPSE;
	
	// Weapon Passive --------------------------------------------------------
	public static Skill SATSUJIN_PASSIVE;
	public static Skill DEMON_MARK_PASSIVE;
	public static Skill RUINE_PASSIVE;
	public static Skill TORMENT_PASSIVE;
	public static Skill LUNAR_ECHO_PASSIVE;

	// Passive --------------------------------------------------------
	public static Skill ARROW_TENACITY;
	public static Skill COUNTER_ATTACK;
	public static Skill PAIN_ANTICIPATION;
	public static Skill LATENT_RETRIBUTION;
	public static Skill VAMPIRIZE;
	public static Skill CRITICAL_KNOWLEDGE;
	public static Skill HEART_SHIELD;
	public static Skill MINDSET;
	public static Skill ADRENALINE;
	public static Skill DANCING_BLADE;
	public static Skill MEDITATION;
	
	
	
	public static void registerSkills() {
		// DODGES --------------------------------------------------------
		SkillManager.register(EnderStepSkill::new, DodgeSkill.createDodgeBuilder().setAnimations(
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/enderstep_forward"),
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/enderstep_backward"),
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/enderstep_left"),
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/enderstep_right")), 
				WeaponsOfMinecraft.MODID,"ender_step");
		
		SkillManager.register(EnderObscurisSkill::new, DodgeSkill.createDodgeBuilder().setAnimations(
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/enderstep_forward"),
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/enderstep_backward"),
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/enderstep_left"),
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/enderstep_right"), 
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/ender_obscuris")),
				WeaponsOfMinecraft.MODID,"ender_obscuris");
		
		SkillManager.register(DodgeMasterSkill::new, DodgeSkill.createDodgeBuilder().setAnimations(
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/dodgemaster_back"),
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/dodgemaster_back"),
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/dodgemaster_right"),
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/dodgemaster_left")),
				WeaponsOfMinecraft.MODID,"dodge_master");
		
		SkillManager.register(DodgeSkill::new, DodgeSkill.createDodgeBuilder().setAnimations(
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/roll_forward"),
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/roll_backward"),
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/roll_left"),
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/roll_right")),
				WeaponsOfMinecraft.MODID,"precise_roll");
		
		SkillManager.register(ShadowStepSkill::new, DodgeSkill.createDodgeBuilder().setAnimations(
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/shadow_step_forward"),
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/shadow_step_backward")),
				WeaponsOfMinecraft.MODID,"shadow_step");
		
		SkillManager.register(BullChargeSkill::new, BullChargeSkill.createChargeBuilder().setAnimations(
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/bull_charge")),
				WeaponsOfMinecraft.MODID,"bull_charge");		
		
		// WEAPON PASSIVE --------------------------------------------------------

		SkillManager.register(DemonMarkPassiveSkill::new, Skill.createBuilder().setCategory(SkillCategories.WEAPON_PASSIVE).setActivateType(ActivateType.DURATION_INFINITE).setResource(Resource.COOLDOWN),
				WeaponsOfMinecraft.MODID,"demon_mark_passive");

		SkillManager.register(TormentPassiveSkill::new, Skill.createBuilder().setCategory(SkillCategories.WEAPON_PASSIVE),
				WeaponsOfMinecraft.MODID,"torment_passive");
	
		SkillManager.register(RuinePassive::new, Skill.createBuilder().setCategory(SkillCategories.WEAPON_PASSIVE),
				WeaponsOfMinecraft.MODID,"ruine_passive");
		
		SkillManager.register(LunarEchoPassiveSkill::new, Skill.createBuilder().setCategory(SkillCategories.WEAPON_PASSIVE),
				WeaponsOfMinecraft.MODID,"lunar_echo_passive");
		
		SkillManager.register(SatsujinPassive::new, Skill.createBuilder().setCategory(SkillCategories.WEAPON_PASSIVE).setActivateType(ActivateType.ONE_SHOT).setResource(Resource.COOLDOWN),
				WeaponsOfMinecraft.MODID,"satsujin_passive");
		
		
		// WEAPON SKILL --------------------------------------------------------
		
		//BLOSSOM = event.registerSkill(new BlossomSkill(BlossomSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "blossom")).setConsumption(60.0F)),false);
		SkillManager.register(CharybdisSkill::new, WeaponInnateSkill.createWeaponInnateBuilder(),
				WeaponsOfMinecraft.MODID,"charybdis");

		SkillManager.register(AgonyPlungeSkill::new, WeaponInnateSkill.createWeaponInnateBuilder(),
				WeaponsOfMinecraft.MODID,"agony_plunge");
				
		
		SkillManager.register(TrueBerserkSkill::new, WeaponInnateSkill.createWeaponInnateBuilder(),
				WeaponsOfMinecraft.MODID,"true_berserk");
		
		SkillManager.register(SoulSnatchSkill::new, WeaponInnateSkill.createWeaponInnateBuilder(),
				WeaponsOfMinecraft.MODID,"plunder_perdition");
		
		SkillManager.register(SakuraStateSkill::new, ConditionalWeaponInnateSkill.createConditionalWeaponInnateBuilder().setSelector((executer) -> {
			if (executer.getOriginal().isSprinting()) {
				executer.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SakuraStateSkill.SECOND_DRAW, false, executer.getOriginal());
				return 2;
			} else if (executer.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(SakuraStateSkill.ACTIVE)) {
				if (executer.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(SakuraStateSkill.SECOND_DRAW)) {
					executer.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SakuraStateSkill.SECOND_DRAW, false, executer.getOriginal());
					return 1;
				} else {
					executer.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SakuraStateSkill.SECOND_DRAW, true, executer.getOriginal());
					return 0;
				}
			} else {
				executer.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SakuraStateSkill.SECOND_DRAW, true, executer.getOriginal());
				return 0;
			}
		}).setAnimations(
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/katana_fatal_draw"),
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/katana_fatal_draw_second"),
				new ResourceLocation(WeaponsOfMinecraft.MODID, "biped/skill/katana_fatal_draw_dash")),
				WeaponsOfMinecraft.MODID,"sakura_state");

		SkillManager.register(EnderBlastSkill::new, WeaponInnateSkill.createWeaponInnateBuilder(),
				WeaponsOfMinecraft.MODID,"ender_blast");

		SkillManager.register(EnderFusionSkill::new, WeaponInnateSkill.createWeaponInnateBuilder(),
				WeaponsOfMinecraft.MODID,"ender_fusion");
		
		SkillManager.register(DemonicAscensionSkill::new, WeaponInnateSkill.createWeaponInnateBuilder().setActivateType(ActivateType.DURATION_INFINITE),
				WeaponsOfMinecraft.MODID,"demonic_ascension");
		
		SkillManager.register(RegierungSkill::new, WeaponInnateSkill.createWeaponInnateBuilder().setActivateType(ActivateType.DURATION_INFINITE),
				WeaponsOfMinecraft.MODID,"regierung");
		
		SkillManager.register(LunarEclipseSkill::new, WeaponInnateSkill.createWeaponInnateBuilder(),
				WeaponsOfMinecraft.MODID,"lunar_eclipse");
		
		//  GUARD --------------------------------------------------------
		
		SkillManager.register(CounterAttack::new, CounterAttack.createCounterAttackBuilder(),
				WeaponsOfMinecraft.MODID,"counter_attack");
		
		SkillManager.register(PerfectBulwarkSkill::new, PerfectBulwarkSkill.createCounterAttackBuilder(),
				WeaponsOfMinecraft.MODID,"perfect_bulwark");
		
		SkillManager.register(VengefulParry::new, VengefulParry.createCounterAttackBuilder(),
				WeaponsOfMinecraft.MODID,"vengeful_parry");
		
		//  PASSIVE --------------------------------------------------------
		
		SkillManager.register(ArrowTenacitySkill::new, PassiveSkill.createPassiveBuilder(),
				WeaponsOfMinecraft.MODID,"arrow_tenacity");

		SkillManager.register(PainAnticipationSkill::new, PassiveSkill.createPassiveBuilder(),
				WeaponsOfMinecraft.MODID,"pain_anticipation");

		SkillManager.register(LatentRetributionSkill::new, PassiveSkill.createPassiveBuilder(),
				WeaponsOfMinecraft.MODID,"latent_retribution");

		SkillManager.register(VampirizeSkill::new, PassiveSkill.createPassiveBuilder(),
				WeaponsOfMinecraft.MODID,"vampirize");

		SkillManager.register(CriticalKnowledgeSkill::new, PassiveSkill.createPassiveBuilder(),
				WeaponsOfMinecraft.MODID,"critical_knowledge");
		
		SkillManager.register(HeartShieldSkill::new, PassiveSkill.createPassiveBuilder(),
				WeaponsOfMinecraft.MODID,"heart_shield");
		
		SkillManager.register(MindSetSkill::new, PassiveSkill.createPassiveBuilder(),
				WeaponsOfMinecraft.MODID,"mindset");
		
		SkillManager.register(AdrenalineSkill::new, PassiveSkill.createPassiveBuilder(),
				WeaponsOfMinecraft.MODID,"adrenaline");
		
		SkillManager.register(DancingBladeSkill::new, PassiveSkill.createPassiveBuilder(),
				WeaponsOfMinecraft.MODID,"dancing_blade");
		
		SkillManager.register(MeditationSkill::new, PassiveSkill.createPassiveBuilder(),
				WeaponsOfMinecraft.MODID,"meditation");
	}
	
	@SubscribeEvent
	public static void buildSkillEvent(SkillBuildEvent onBuild) {
		ENDERSTEP = onBuild.build(WeaponsOfMinecraft.MODID, "ender_step");
		ENDEROBSCURIS = onBuild.build(WeaponsOfMinecraft.MODID, "ender_obscuris");
		SHADOWSTEP = onBuild.build(WeaponsOfMinecraft.MODID, "shadow_step");
		KNIGHT_ROLL = onBuild.build(WeaponsOfMinecraft.MODID, "precise_roll");		
		DODGEMASTER = onBuild.build(WeaponsOfMinecraft.MODID, "dodge_master");
		BULL_CHARGE = onBuild.build(WeaponsOfMinecraft.MODID, "bull_charge");
		
		COUNTER_ATTACK = onBuild.build(WeaponsOfMinecraft.MODID, "counter_attack");
		VENGEFUL_PARRY = onBuild.build(WeaponsOfMinecraft.MODID, "vengeful_parry");
		PERFECT_BULWARK = onBuild.build(WeaponsOfMinecraft.MODID, "perfect_bulwark");
		
		ARROW_TENACITY = onBuild.build(WeaponsOfMinecraft.MODID, "arrow_tenacity");
		PAIN_ANTICIPATION = onBuild.build(WeaponsOfMinecraft.MODID, "pain_anticipation");
		LATENT_RETRIBUTION = onBuild.build(WeaponsOfMinecraft.MODID, "latent_retribution");
		VAMPIRIZE = onBuild.build(WeaponsOfMinecraft.MODID, "vampirize");
		CRITICAL_KNOWLEDGE = onBuild.build(WeaponsOfMinecraft.MODID, "critical_knowledge");
		HEART_SHIELD = onBuild.build(WeaponsOfMinecraft.MODID, "heart_shield");
		MINDSET = onBuild.build(WeaponsOfMinecraft.MODID, "mindset");
		ADRENALINE = onBuild.build(WeaponsOfMinecraft.MODID, "adrenaline");
		DANCING_BLADE = onBuild.build(WeaponsOfMinecraft.MODID, "dancing_blade");
		MEDITATION = onBuild.build(WeaponsOfMinecraft.MODID, "meditation");
		
		WeaponInnateSkill charybdisSkill = onBuild.build(WeaponsOfMinecraft.MODID, "charybdis");
		charybdisSkill.newProperty()
			.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35f));
		CHARYBDIS = charybdisSkill;
		
		WeaponInnateSkill AgonyPlungeSkill = onBuild.build(WeaponsOfMinecraft.MODID, "agony_plunge");
		AgonyPlungeSkill.newProperty()
			.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10))
			.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1))
			.newProperty()
			.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(10))
			.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2f))
			.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(1.2f)));
		AGONY_PLUNGE = AgonyPlungeSkill;
		
		TORMENT_PASSIVE = onBuild.build(WeaponsOfMinecraft.MODID, "torment_passive");
		
		WeaponInnateSkill trueBerserkSkill = onBuild.build(WeaponsOfMinecraft.MODID, "true_berserk");
		trueBerserkSkill.newProperty()
			.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2f))
			.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8))
			.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
			.newProperty()
			.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2f))
			.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(10))
			.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()));
		TRUE_BERSERK = trueBerserkSkill;
		
		RUINE_PASSIVE = onBuild.build(WeaponsOfMinecraft.MODID, "ruine_passive");
		
		WeaponInnateSkill soulSnatchSkill = onBuild.build(WeaponsOfMinecraft.MODID, "plunder_perdition");
		soulSnatchSkill.newProperty()
			.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7f))
			.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20))
			.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
			.newProperty()
			.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.3f))
			.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20))
			.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()));
		SOUL_SNATCH = soulSnatchSkill;
		
		SATSUJIN_PASSIVE = onBuild.build(WeaponsOfMinecraft.MODID, "satsujin_passive");
		
		WeaponInnateSkill sakuraStateskill = onBuild.build(WeaponsOfMinecraft.MODID, "sakura_state");
		sakuraStateskill.newProperty()
			.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2f))
			.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20))
			.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(30))
			.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
			.registerPropertiesToAnimation();
			
		SAKURA_STATE = sakuraStateskill;
		
		WeaponInnateSkill enderblastSkill = onBuild.build(WeaponsOfMinecraft.MODID, "ender_blast");
		enderblastSkill.newProperty()
			.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7f))
			.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.7f)))
			.newProperty()
			.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6f))
			.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.6f)))
			.newProperty()
			.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.1f))
			.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(4.0F))
			.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(1.05f)));
		ENDER_BLAST = enderblastSkill;
		
		WeaponInnateSkill enderfusionSkill = onBuild.build(WeaponsOfMinecraft.MODID, "ender_fusion");
		enderfusionSkill.newProperty()
			.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7f))
			.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.7f)))
			.newProperty()
			.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6f))
			.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.6f)))
			.newProperty()
			.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(4.2f))
			.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(4.0F))
			.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(2.1f)));
		ENDER_FUSION = enderfusionSkill;
		
		REGIERUNG = onBuild.build(WeaponsOfMinecraft.MODID, "regierung");
		
		lUNAR_ECLIPSE = onBuild.build(WeaponsOfMinecraft.MODID, "lunar_eclipse");
		LUNAR_ECHO_PASSIVE = onBuild.build(WeaponsOfMinecraft.MODID, "lunar_echo_passive");
		
		DEMONIC_ASCENSION = onBuild.build(WeaponsOfMinecraft.MODID, "demonic_ascension");
		DEMON_MARK_PASSIVE = onBuild.build(WeaponsOfMinecraft.MODID, "demon_mark_passive");
		
	}
}
