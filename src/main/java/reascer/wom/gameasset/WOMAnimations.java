package reascer.wom.gameasset;

import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import reascer.wom.animation.attacks.AntitheusShootAttackAnimation;
import reascer.wom.animation.attacks.BasicMultipleAttackAnimation;
import reascer.wom.animation.attacks.EnderblasterShootAttackAnimation;
import reascer.wom.animation.attacks.SpecialAttackAnimation;
import reascer.wom.animation.dodges.CancelableDodgeAnimation;
import reascer.wom.main.WeaponsOfMinecraft;
import reascer.wom.particle.WOMParticles;
import reascer.wom.skill.dodges.BullChargeSkill;
import reascer.wom.skill.dodges.EnderObscurisSkill;
import reascer.wom.skill.weaponinnate.AgonyPlungeSkill;
import reascer.wom.skill.weaponinnate.DemonicAscensionSkill;
import reascer.wom.skill.weaponinnate.RegierungSkill;
import reascer.wom.skill.weaponpassive.DemonMarkPassiveSkill;
import reascer.wom.skill.weaponpassive.LunarEclipsePassiveSkill;
import reascer.wom.skill.weaponpassive.SatsujinPassive;
import reascer.wom.world.capabilities.item.WOMWeaponCategories;
import reascer.wom.world.damagesources.WOMExtraDamageInstance;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.TimePeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.TimeStampedEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.AimAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.GuardAnimation;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.SourceTags;
import yesman.epicfight.world.damagesource.StunType;

@Mod.EventBusSubscriber(modid = WeaponsOfMinecraft.MODID , bus = EventBusSubscriber.Bus.MOD)
public class WOMAnimations {
	public static StaticAnimation EATING;
	
	public static StaticAnimation ENDERSTEP_FORWARD;
	public static StaticAnimation ENDERSTEP_BACKWARD;
	public static StaticAnimation ENDERSTEP_LEFT;
	public static StaticAnimation ENDERSTEP_RIGHT;
	
	public static StaticAnimation ENDERSTEP_OBSCURIS;
	public static StaticAnimation MOB_ENDERSTEP_OBSCURIS;
	
	public static StaticAnimation DODGEMASTER_BACKWARD;
	public static StaticAnimation DODGEMASTER_LEFT;
	public static StaticAnimation DODGEMASTER_RIGHT;
	
	public static StaticAnimation KNIGHT_ROLL_FORWARD;
	public static StaticAnimation KNIGHT_ROLL_BACKWARD;
	public static StaticAnimation KNIGHT_ROLL_LEFT;
	public static StaticAnimation KNIGHT_ROLL_RIGHT;
	
	public static StaticAnimation SHADOWSTEP_FORWARD;
	public static StaticAnimation SHADOWSTEP_BACKWARD;
	public static StaticAnimation MOB_SHADOWSTEP_FORWARD;
	public static StaticAnimation MOB_SHADOWSTEP_BACKWARD;
	
	public static StaticAnimation BULL_CHARGE;
	
	public static StaticAnimation MEDITATION_SITING;
	public static StaticAnimation MEDITATION_BREATHING;
	
	public static StaticAnimation KICK_AUTO_1;
	public static StaticAnimation KICK_AUTO_2;
	public static StaticAnimation KICK_AUTO_3;
	
	public static StaticAnimation STAFF_AUTO_1;
	public static StaticAnimation STAFF_AUTO_2;
	public static StaticAnimation STAFF_AUTO_3;
	public static StaticAnimation STAFF_DASH;
	public static StaticAnimation STAFF_KINKONG;
	public static StaticAnimation STAFF_CHARYBDIS;
	public static StaticAnimation STAFF_IDLE;
	public static StaticAnimation STAFF_RUN;
	
	public static StaticAnimation AGONY_AUTO_1;
	public static StaticAnimation AGONY_AUTO_2;
	public static StaticAnimation AGONY_AUTO_3;
	public static StaticAnimation AGONY_AUTO_4;
	public static StaticAnimation AGONY_HEAVY_1;
	public static StaticAnimation AGONY_HEAVY_2;
	public static StaticAnimation AGONY_HEAVY_3;
	public static StaticAnimation AGONY_HEAVY_CHARGE_1;
	public static StaticAnimation AGONY_HEAVY_CHARGE_2;
	public static StaticAnimation AGONY_HEAVY_CHARGE_3;
	public static StaticAnimation AGONY_AIR_SLASH;
	public static StaticAnimation AGONY_CLAWSTRIKE;
	public static StaticAnimation AGONY_COUNTER;
	public static StaticAnimation AGONY_IDLE;
	public static StaticAnimation AGONY_RUN;
	public static StaticAnimation AGONY_WALK;
	public static StaticAnimation AGONY_GUARD;
	public static StaticAnimation AGONY_GUARD_HIT_1;
	public static StaticAnimation AGONY_GUARD_HIT_2;
	public static StaticAnimation AGONY_PLUNGE_MIDDLE;
	public static StaticAnimation AGONY_PLUNGE_FORWARD;
	public static StaticAnimation AGONY_PLUNGE_BACKWARD;
	public static StaticAnimation AGONY_PLUNGE_LEFT;
	public static StaticAnimation AGONY_PLUNGE_RIGHT;
	
	public static StaticAnimation RUINE_AUTO_1;
	public static StaticAnimation RUINE_AUTO_2;
	public static StaticAnimation RUINE_AUTO_3;
	public static StaticAnimation RUINE_AUTO_4;
	public static StaticAnimation RUINE_COMET;
	public static StaticAnimation RUINE_DASH;
	public static StaticAnimation RUINE_COUNTER;
	public static StaticAnimation RUINE_BLOCK;
	public static StaticAnimation RUINE_IDLE;
	public static StaticAnimation RUINE_BOOSTED_IDLE;
	public static StaticAnimation RUINE_RUN;
	public static StaticAnimation RUINE_WALK;
	public static StaticAnimation RUINE_BOOSTED_WALK;
	public static StaticAnimation RUINE_PLUNDER;
	public static StaticAnimation RUINE_EXPIATION;
	public static StaticAnimation RUINE_REDEMPTION;
	
	public static StaticAnimation TORMENT_AUTO_1;
	public static StaticAnimation TORMENT_AUTO_2;
	public static StaticAnimation TORMENT_AUTO_3;
	public static StaticAnimation TORMENT_AUTO_4;
	public static StaticAnimation TORMENT_DASH;
	public static StaticAnimation TORMENT_AIRSLAM;
	public static StaticAnimation TORMENT_IDLE;
	public static StaticAnimation TORMENT_RUN;
	public static StaticAnimation TORMENT_WALK;
	public static StaticAnimation TORMENT_CHARGE;
	public static StaticAnimation TORMENT_CHARGED_ATTACK_1;
	public static StaticAnimation TORMENT_CHARGED_ATTACK_2;
	public static StaticAnimation TORMENT_CHARGED_ATTACK_3;
	public static StaticAnimation TORMENT_BERSERK_AUTO_1;
	public static StaticAnimation TORMENT_BERSERK_AUTO_2;
	public static StaticAnimation TORMENT_BERSERK_DASH;
	public static StaticAnimation TORMENT_BERSERK_AIRSLAM;
	public static StaticAnimation TORMENT_BERSERK_CONVERT;
	public static StaticAnimation TORMENT_BERSERK_IDLE;
	public static StaticAnimation TORMENT_BERSERK_RUN;
	public static StaticAnimation TORMENT_BERSERK_WALK;
	
	public static StaticAnimation KATANA_AUTO_1;
	public static StaticAnimation KATANA_AUTO_2;
	public static StaticAnimation KATANA_AUTO_3;
	public static StaticAnimation KATANA_DASH;
	public static StaticAnimation KATANA_IDLE;
	public static StaticAnimation KATANA_WALK;
	public static StaticAnimation KATANA_RUN;
	public static StaticAnimation KATANA_GUARD;
	public static StaticAnimation KATANA_GUARD_HIT;
	public static StaticAnimation KATANA_SHEATHED_AUTO_1;
	public static StaticAnimation KATANA_SHEATHED_AUTO_2;
	public static StaticAnimation KATANA_SHEATHED_AUTO_3;
	public static StaticAnimation KATANA_SHEATHED_DASH;
	public static StaticAnimation KATANA_SHEATHED_COUNTER;
	public static StaticAnimation KATANA_SAKURA_TIMED_SLASH;
	public static StaticAnimation KATANA_SHEATHE;
	public static StaticAnimation KATANA_SHEATHED_IDLE;
	public static StaticAnimation KATANA_SHEATHED_RUN;
	public static StaticAnimation KATANA_FATAL_DRAW;
	public static StaticAnimation KATANA_FATAL_DRAW_SECOND;
	public static StaticAnimation KATANA_FATAL_DRAW_DASH;
	
	public static StaticAnimation ENDERBLASTER_ONEHAND_AUTO_1;//ONEHAND
	public static StaticAnimation ENDERBLASTER_ONEHAND_AUTO_2;
	public static StaticAnimation ENDERBLASTER_ONEHAND_AUTO_3;
	public static StaticAnimation ENDERBLASTER_ONEHAND_AUTO_4;
	public static StaticAnimation ENDERBLASTER_ONEHAND_DASH;
	public static StaticAnimation ENDERBLASTER_ONEHAND_JUMPKICK;
	public static StaticAnimation ENDERBLASTER_ONEHAND_IDLE;
	public static StaticAnimation ENDERBLASTER_ONEHAND_WALK;
	public static StaticAnimation ENDERBLASTER_ONEHAND_RUN;
	public static StaticAnimation ENDERBLASTER_ONEHAND_SHOOT_1;
	public static StaticAnimation ENDERBLASTER_ONEHAND_SHOOT_2;
	public static StaticAnimation ENDERBLASTER_ONEHAND_SHOOT_2_FORWARD;
	public static StaticAnimation ENDERBLASTER_ONEHAND_SHOOT_2_LEFT;
	public static StaticAnimation ENDERBLASTER_ONEHAND_SHOOT_2_RIGHT;
	public static StaticAnimation ENDERBLASTER_ONEHAND_SHOOT_3;
	public static StaticAnimation ENDERBLASTER_ONEHAND_SHOOT_DASH;
	public static StaticAnimation ENDERBLASTER_ONEHAND_AIRSHOOT;
	public static StaticAnimation ENDERBLASTER_ONEHAND_AIMING;
	public static StaticAnimation ENDERBLASTER_ONEHAND_SHOOT;
	public static StaticAnimation ENDERBLASTER_ONEHAND_SHOOT_LAYED;
	public static StaticAnimation ENDERBLASTER_ONEHAND_RELOAD;
	public static StaticAnimation ENDERBLASTER_TWOHAND_TISHNAW;// TWOHAND
	public static StaticAnimation ENDERBLASTER_TWOHAND_TOMAHAWK;
	public static StaticAnimation ENDERBLASTER_TWOHAND_AUTO_1;
	public static StaticAnimation ENDERBLASTER_TWOHAND_AUTO_2;
	public static StaticAnimation ENDERBLASTER_TWOHAND_AUTO_3;
	public static StaticAnimation ENDERBLASTER_TWOHAND_AUTO_4;
	public static StaticAnimation ENDERBLASTER_TWOHAND_SHOOT_1;
	public static StaticAnimation ENDERBLASTER_TWOHAND_SHOOT_2;
	public static StaticAnimation ENDERBLASTER_TWOHAND_SHOOT_3;
	public static StaticAnimation ENDERBLASTER_TWOHAND_SHOOT_4;
	public static StaticAnimation ENDERBLASTER_TWOHAND_EVADE_LEFT;
	public static StaticAnimation ENDERBLASTER_TWOHAND_EVADE_RIGHT;
	public static StaticAnimation ENDERBLASTER_TWOHAND_AIRSHOOT;
	public static StaticAnimation ENDERBLASTER_TWOHAND_PISTOLERO;
	public static StaticAnimation ENDERBLASTER_TWOHAND_IDLE;
	public static StaticAnimation ENDERBLASTER_TWOHAND_AIMING;
	public static StaticAnimation ENDERBLASTER_TWOHAND_SHOOT_RIGHT;
	public static StaticAnimation ENDERBLASTER_TWOHAND_SHOOT_LEFT;
	public static StaticAnimation ENDERBLASTER_TWOHAND_SHOOT_LAYED_RIGHT;
	public static StaticAnimation ENDERBLASTER_TWOHAND_SHOOT_LAYED_LEFT;
	public static StaticAnimation ENDERBLASTER_TWOHAND_RELOAD;
	
	public static StaticAnimation ANTITHEUS_AGRESSION;
	public static StaticAnimation ANTITHEUS_GUILLOTINE;
	public static StaticAnimation ANTITHEUS_AUTO_1;
	public static StaticAnimation ANTITHEUS_AUTO_2;
	public static StaticAnimation ANTITHEUS_AUTO_3;
	public static StaticAnimation ANTITHEUS_AUTO_4;
	public static StaticAnimation ANTITHEUS_IDLE;
	public static StaticAnimation ANTITHEUS_IDLE_TWIST;
	public static StaticAnimation ANTITHEUS_RUN;
	public static StaticAnimation ANTITHEUS_WALK;
	public static StaticAnimation ANTITHEUS_AIMING;
	public static StaticAnimation ANTITHEUS_SHOOT;
	public static StaticAnimation ANTITHEUS_PULL;
	public static StaticAnimation ANTITHEUS_ASCENSION;
	public static StaticAnimation ANTITHEUS_LAPSE;
	public static StaticAnimation ANTITHEUS_ASCENDED_AUTO_1;
	public static StaticAnimation ANTITHEUS_ASCENDED_AUTO_2;
	public static StaticAnimation ANTITHEUS_ASCENDED_AUTO_3;
	public static StaticAnimation ANTITHEUS_ASCENDED_DEATHFALL;
	public static StaticAnimation ANTITHEUS_ASCENDED_BLINK;
	public static StaticAnimation ANTITHEUS_ASCENDED_BLACKHOLE;
	public static StaticAnimation ANTITHEUS_ASCENDED_IDLE;
	public static StaticAnimation ANTITHEUS_ASCENDED_RUN;
	public static StaticAnimation ANTITHEUS_ASCENDED_WALK;
	
	public static StaticAnimation HERRSCHER_IDLE;
	public static StaticAnimation HERRSCHER_WALK;
	public static StaticAnimation HERRSCHER_RUN;
	public static StaticAnimation HERRSCHER_AUTO_1;
	public static StaticAnimation HERRSCHER_AUTO_2;
	public static StaticAnimation HERRSCHER_AUTO_3;
	public static StaticAnimation HERRSCHER_BEFREIUNG;
	public static StaticAnimation HERRSCHER_AUSROTTUNG;
	public static StaticAnimation HERRSCHER_GUARD;
	public static StaticAnimation HERRSCHER_GUARD_HIT;
	public static StaticAnimation HERRSCHER_GUARD_PARRY;
	public static StaticAnimation HERRSCHER_TRANE;
	
	public static StaticAnimation GESETZ_AUTO_1;
	public static StaticAnimation GESETZ_AUTO_2;
	public static StaticAnimation GESETZ_AUTO_3;
	public static StaticAnimation GESETZ_SPRENGKOPF;
	public static StaticAnimation GESETZ_KRUMMEN;
	
	public static StaticAnimation MOONLESS_IDLE;
	public static StaticAnimation MOONLESS_WALK;
	public static StaticAnimation MOONLESS_RUN;
	public static StaticAnimation MOONLESS_BYPASS;
	public static StaticAnimation MOONLESS_REVERSED_BYPASS;
	public static StaticAnimation MOONLESS_CRESCENT;
	public static StaticAnimation MOONLESS_FULLMOON;
	public static StaticAnimation MOONLESS_AUTO_1;
	public static StaticAnimation MOONLESS_AUTO_2;
	public static StaticAnimation MOONLESS_AUTO_1_VERSO;
	public static StaticAnimation MOONLESS_AUTO_2_VERSO;
	public static StaticAnimation MOONLESS_LUNAR_ECHO;
	public static StaticAnimation MOONLESS_GUARD;
	public static StaticAnimation MOONLESS_GUARD_HIT_1;
	public static StaticAnimation MOONLESS_GUARD_HIT_2;
	public static StaticAnimation MOONLESS_GUARD_HIT_3;
	public static StaticAnimation MOONLESS_REWINDER;
	
	public static StaticAnimation SOLAR_IDLE;
	public static StaticAnimation SOLAR_WALK;
	public static StaticAnimation SOLAR_RUN;
	
	@SubscribeEvent
	public static void registerAnimations(AnimationRegistryEvent event) {
		event.getRegistryMap().put(WeaponsOfMinecraft.MODID, WOMAnimations::build);
	}
	
	private static void build() {
		HumanoidArmature biped = Armatures.BIPED;
		
		EATING = new StaticAnimation(0.11F, true, "biped/living/eating", biped);
		
		KICK_AUTO_1 = new BasicMultipleAttackAnimation(0.1F, 0.05F, 0.15F, 0.2F, null, biped.toolR, "biped/skill/kick", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(2))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(5))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F);
		
		DODGEMASTER_BACKWARD = new CancelableDodgeAnimation(0.05F, 0.00F, 0.2f, "biped/skill/dodgemaster_back", 0.6F, 1.65F, biped)
				.addEvents(TimeStampedEvent.create(0.00F, (entitypatch, self, params) -> {
					Entity entity = entitypatch.getOriginal();
					entitypatch.getOriginal().playSound(EpicFightSounds.WHOOSH_BIG.get(), 1.0F, 2.0F);
					entitypatch.getOriginal().level().addParticle(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
				}, Side.CLIENT));
		DODGEMASTER_LEFT = new CancelableDodgeAnimation(0.05F, 0.00F, 0.2f, "biped/skill/dodgemaster_left", 0.6F, 1.65F, biped)
				.addEvents(TimeStampedEvent.create(0.00F, (entitypatch, self, params) -> {
					Entity entity = entitypatch.getOriginal();
					entitypatch.getOriginal().playSound(EpicFightSounds.WHOOSH_BIG.get(), 1.0F, 2.0F);
					entitypatch.getOriginal().level().addParticle(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
				}, Side.CLIENT));
		DODGEMASTER_RIGHT = new CancelableDodgeAnimation(0.05F, 0.00F, 0.2f, "biped/skill/dodgemaster_right", 0.6F, 1.65F, biped)
				.addEvents(TimeStampedEvent.create(0.00F, (entitypatch, self, params) -> {
					Entity entity = entitypatch.getOriginal();
					entitypatch.getOriginal().playSound(EpicFightSounds.WHOOSH_BIG.get(), 1.0F, 2.0F);
					entitypatch.getOriginal().level().addParticle(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
				}, Side.CLIENT));
		
		
		ENDERSTEP_FORWARD = new DodgeAnimation(0.05F, "biped/skill/enderstep_forward", 0.6F, 1.65F, biped)
				.addEvents(TimeStampedEvent.create(0.05F, ReuseableEvents.ENDER_STEP, Side.BOTH));
		ENDERSTEP_BACKWARD = new DodgeAnimation(0.05F, "biped/skill/enderstep_backward", 0.6F, 1.65F, biped)
				.addEvents(TimeStampedEvent.create(0.05F, ReuseableEvents.ENDER_STEP, Side.BOTH));
		ENDERSTEP_LEFT = new DodgeAnimation(0.05F, "biped/skill/enderstep_left", 0.6F, 1.65F, biped)
				.addEvents(TimeStampedEvent.create(0.05F, ReuseableEvents.ENDER_STEP, Side.BOTH));
		ENDERSTEP_RIGHT = new DodgeAnimation(0.05F, "biped/skill/enderstep_right", 0.6F, 1.65F, biped)
				.addEvents(TimeStampedEvent.create(0.05F, ReuseableEvents.ENDER_STEP, Side.BOTH));
		
		ENDERSTEP_OBSCURIS = new CancelableDodgeAnimation(0.05F, "biped/skill/ender_obscuris", 0.6F, 1.65F, biped)
				.addEvents(TimeStampedEvent.create(0.10F, ReuseableEvents.ENDER_STEP, Side.BOTH),
						TimeStampedEvent.create(0.30F, ReuseableEvents.ENDER_OBSCURIS, Side.BOTH));
		MOB_ENDERSTEP_OBSCURIS = new DodgeAnimation(0.05F, "biped/skill/mob_ender_obscuris", 0.6F, 1.65F, biped)
				.addEvents(TimeStampedEvent.create(0.10F, ReuseableEvents.ENDER_STEP, Side.BOTH),
						TimeStampedEvent.create(0.30F, ReuseableEvents.MOB_ENDER_OBSCURIS, Side.BOTH));
		
		SHADOWSTEP_FORWARD = new CancelableDodgeAnimation(0.05F, "biped/skill/shadow_step_forward", 0.6F, 1.65F, biped)
				.addEvents(TimePeriodEvent.create(0.05F, 1f, ReuseableEvents.SHADOW_STEP, Side.BOTH))
				.addEvents(TimeStampedEvent.create(0.05F, ReuseableEvents.SHADOW_STEP_ENTER, Side.BOTH));
		SHADOWSTEP_BACKWARD = new CancelableDodgeAnimation(0.05F, "biped/skill/shadow_step_backward", 0.6F, 1.65F, biped)
				.addEvents(TimePeriodEvent.create(0.05F,1f, ReuseableEvents.SHADOW_STEP, Side.BOTH))
				.addEvents(TimeStampedEvent.create(0.05F, ReuseableEvents.SHADOW_STEP_ENTER, Side.BOTH));
		
		MOB_SHADOWSTEP_FORWARD = new DodgeAnimation(0.05F, "biped/skill/mob_shadow_step_forward", 0.6F, 1.65F, biped)
				.addEvents(TimePeriodEvent.create(0.05F, 1f, ReuseableEvents.SHADOW_STEP, Side.BOTH))
				.addEvents(TimeStampedEvent.create(0.05F, ReuseableEvents.SHADOW_STEP_ENTER, Side.BOTH));
		MOB_SHADOWSTEP_BACKWARD = new DodgeAnimation(0.05F, "biped/skill/mob_shadow_step_backward", 0.6F, 1.65F, biped)
				.addEvents(TimePeriodEvent.create(0.05F,1f, ReuseableEvents.SHADOW_STEP, Side.BOTH))
				.addEvents(TimeStampedEvent.create(0.05F, ReuseableEvents.SHADOW_STEP_ENTER, Side.BOTH));
		
		BULL_CHARGE = new BasicMultipleAttackAnimation(0.2F, "biped/skill/bull_charge", biped,
				new Phase(0.0F, 0.15F, 0.25F, 0.26F, 0.26F, biped.rootJoint, WOMColliders.SHOULDER_BUMP),
				new Phase(0.26F, 0.3F, 0.4F, 0.41F, 0.41F, biped.rootJoint, WOMColliders.SHOULDER_BUMP),
				new Phase(0.41F, 0.45F, 0.55F, 0.56F, 0.56F, biped.rootJoint, WOMColliders.SHOULDER_BUMP),
				new Phase(0.56F, 0.6F, 0.7F, 0.8F, Float.MAX_VALUE, biped.rootJoint, WOMColliders.SHOULDER_BUMP))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(3F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get())
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(2F),1)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get(),1)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1),2)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(1F),2)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10F),2)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,2)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get(),2)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,2)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(2),3)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(5F),3)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10F),3)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,3)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get(),3)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,3)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.7F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.00F, (entitypatch, self, params) -> {
						if (entitypatch instanceof ServerPlayerPatch) {
							ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch) entitypatch;
							if (serverPlayerPatch.getSkill(SkillSlots.DODGE) != null) {
								serverPlayerPatch.getSkill(SkillSlots.DODGE).getDataManager().setDataSync(BullChargeSkill.SUPER_ARMOR, true,(ServerPlayer)entitypatch.getOriginal());
							}
						}
					}, Side.SERVER),
					TimeStampedEvent.create(0.7F, (entitypatch, self, params) -> {
						if (entitypatch instanceof ServerPlayerPatch) {
							ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch) entitypatch;
							if (serverPlayerPatch.getSkill(SkillSlots.DODGE) != null) {
								serverPlayerPatch.getSkill(SkillSlots.DODGE).getDataManager().setDataSync(BullChargeSkill.SUPER_ARMOR, false,(ServerPlayer)entitypatch.getOriginal());
							}
						}
					}, Side.SERVER));
		
		KNIGHT_ROLL_FORWARD = new DodgeAnimation(0.1F, "biped/skill/roll_forward", 0.6F, 0.8F, biped);
		KNIGHT_ROLL_BACKWARD = new DodgeAnimation(0.1F, "biped/skill/roll_backward", 0.6F, 0.8F, biped);
		KNIGHT_ROLL_LEFT = new DodgeAnimation(0.1F, "biped/skill/roll_left", 0.6F, 0.8F, biped);
		KNIGHT_ROLL_RIGHT = new DodgeAnimation(0.1F, "biped/skill/roll_right", 0.6F, 0.8F, biped);
		
		MEDITATION_SITING = new StaticAnimation(0.2f,false, "biped/skill/meditation_siting", biped);
		MEDITATION_BREATHING = new StaticAnimation(0.1f,true, "biped/skill/meditation_breathing", biped);
		
		AGONY_AUTO_1 = new BasicMultipleAttackAnimation(0.05F, 0.05F, 0.15F, 0.2F, null, biped.toolR, "biped/combat/agony_auto_1", biped)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.7F);
		
		AGONY_AUTO_2 = new BasicMultipleAttackAnimation(0.05F, "biped/combat/agony_auto_2", biped,
				new Phase(0.00F, 0.3F, 0.45F, 0.5F, 0.5F, biped.toolR, null),
				new Phase(0.5F, 0.5F, 0.65F, 0.8F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.7F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.25F, 0.4F));
		
		AGONY_AUTO_3 = new BasicMultipleAttackAnimation(0.05F, "biped/combat/agony_auto_3", biped,
				new Phase(0.0F, 0.25F, 0.5F, 0.55F, 0.55F, biped.toolR, null),
				new Phase(0.55F, 0.75F, 1.0F, 1.20F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.8F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.5F))
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false);
		
		AGONY_AUTO_4 = new BasicMultipleAttackAnimation(0.05F, "biped/combat/agony_auto_4", biped,
				new Phase(0.0F, 0.10F, 0.2F, 0.25F, 0.25F, biped.toolR, null),
				new Phase(0.25F, 0.25F, 0.35F, 0.55F, 0.55F, biped.toolR, null),
				new Phase(0.55F, 0.55F, 0.7F, 0.95F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F),1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F),2)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,2)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.addEvents(TimeStampedEvent.create(1.3F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						   TimeStampedEvent.create(1.5F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						   TimeStampedEvent.create(1.7F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						   TimeStampedEvent.create(2.05F, ReuseableEvents.FAST_SPINING, Side.CLIENT));
		
		AGONY_CLAWSTRIKE = new BasicMultipleAttackAnimation(0.05F, "biped/combat/agony_clawstrike", biped,
				new Phase(0.0F, 0.35F, 0.6F, 0.95F, Float.MAX_VALUE, biped.toolR, WOMColliders.AGONY_AIRSLASH))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.4F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false);
		
		AGONY_COUNTER = new BasicMultipleAttackAnimation(0.20F, 0.20F, 0.5F,0.75f, WOMColliders.AGONY_AIRSLASH, biped.toolR, "biped/combat/agony_counter", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.8F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.KATANA_SHEATHED_HIT)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false);
		
		AGONY_AIR_SLASH = new BasicMultipleAttackAnimation(0.05F, "biped/combat/agony_airslash", biped,
				new Phase(0.0F, 0.10F, 0.25F, 0.3F, 0.3F, biped.toolR, WOMColliders.AGONY_AIRSLASH),
				new Phase(0.3F, 0.35F, 0.45F, 0.49F, 0.49F, biped.toolR, WOMColliders.AGONY_AIRSLASH),
				new Phase(0.49F, 0.50F, 0.75F, 0.85F, Float.MAX_VALUE, biped.toolR, WOMColliders.AGONY_AIRSLASH))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.8F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F),2)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.8F),2)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,2)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.3F)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.2F))
				.addEvents(TimePeriodEvent.create(0.20F, 0.4F, ReuseableEvents.LOOPED_FALLING_MOVE, Side.BOTH))
				.addEvents(TimeStampedEvent.create(0.4F, ReuseableEvents.LOOPED_FALLING, Side.BOTH),
						   TimeStampedEvent.create(0.65F, ReuseableEvents.AGONY_GROUNDSLAM, Side.CLIENT));
		
		AGONY_IDLE = new StaticAnimation(0.1f,true, "biped/living/agony_idle", biped);
		AGONY_RUN = new MovementAnimation(0.1f,true, "biped/living/agony_run", biped);
		AGONY_WALK = new MovementAnimation(0.1f,true, "biped/living/agony_walk", biped);
		
		AGONY_GUARD = new StaticAnimation(0.05F, true, "biped/skill/agony_guard", biped)
				.addEvents(TimeStampedEvent.create(0.0F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
					TimeStampedEvent.create(0.1F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
					TimeStampedEvent.create(0.2F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
					TimeStampedEvent.create(0.3F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
					TimeStampedEvent.create(0.4F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
					TimeStampedEvent.create(0.5F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
					TimeStampedEvent.create(0.6F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
					TimeStampedEvent.create(0.7F, ReuseableEvents.FAST_SPINING, Side.CLIENT));
		
		AGONY_GUARD_HIT_1 = new GuardAnimation(0.05F, 0.5F, "biped/skill/agony_guard_hit1", biped)
				.addEvents(
						TimeStampedEvent.create(0.1F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						TimeStampedEvent.create(0.2F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						TimeStampedEvent.create(0.3F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						TimeStampedEvent.create(0.4F, ReuseableEvents.FAST_SPINING, Side.CLIENT));
		
		AGONY_GUARD_HIT_2 = new GuardAnimation(0.05F, 0.5F, "biped/skill/agony_guard_hit2", biped)
				.addEvents(
						TimeStampedEvent.create(0.1F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						TimeStampedEvent.create(0.2F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						TimeStampedEvent.create(0.3F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						TimeStampedEvent.create(0.4F, ReuseableEvents.FAST_SPINING, Side.CLIENT));
		
		AGONY_PLUNGE_FORWARD = new SpecialAttackAnimation(0.05F, "biped/skill/agony_plunge_forward", biped,
				new Phase(0.0F, 0.10F, 0.20F, 0.2F, 0.2F, biped.rootJoint, WOMColliders.AGONY_PLUNGE), 
				new Phase(0.2F, 1.1F, 1.45F, 1.7F, Float.MAX_VALUE, biped.rootJoint, WOMColliders.AGONY_PLUNGE))
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.WHOOSH_BIG.get(),0)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,0)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10),0)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(9.0F),0)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F),0)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,0)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2f),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(1.5f),WOMExtraDamageInstance.TARGET_LOST_HEALTH.create(0.2f)),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F),1)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10),1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get(),1)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL,1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN,1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 1.3F))
				.addEvents(TimeStampedEvent.create(0.15F, ReuseableEvents.AGONY_AIRBURST_JUMP, Side.CLIENT),
						TimeStampedEvent.create(0.2F, ReuseableEvents.AGONY_ENCHANTED_JUMP, Side.CLIENT),
						TimeStampedEvent.create(0.25F, ReuseableEvents.AGONY_ENCHANTED_JUMP, Side.CLIENT),
						TimeStampedEvent.create(0.3F, ReuseableEvents.AGONY_ENCHANTED_JUMP, Side.CLIENT),
						TimeStampedEvent.create(0.35F, ReuseableEvents.AGONY_ENCHANTED_JUMP, Side.CLIENT),
						TimeStampedEvent.create(1.3F, ReuseableEvents.AGONY_PLUNGE_GROUNDTHRUST, Side.CLIENT),
						TimeStampedEvent.create(1.45F, (entitypatch, self, params) -> {
							if (entitypatch instanceof PlayerPatch) {
								((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(AgonyPlungeSkill.PLUNGING, true,(ServerPlayer)entitypatch.getOriginal());
								((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(AgonyPlungeSkill.STACK, 0,(ServerPlayer)entitypatch.getOriginal());
							}
						}, Side.SERVER),
						TimeStampedEvent.create(1.55F, ReuseableEvents.AGONY_ENCHANTED_JUMP, Side.CLIENT));
		
		RUINE_AUTO_1 = new BasicMultipleAttackAnimation(0.15F, 0.25F, 0.45F, 0.55F, null, biped.toolR, "biped/combat/ruine_auto_1", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.35F);
		
		RUINE_AUTO_2 = new BasicMultipleAttackAnimation(0.10F, "biped/combat/ruine_auto_2", biped,
				new Phase(0.0F, 0.05F, 0.25F, 0.3F, 0.3F, biped.toolR, null),
				new Phase(0.3F, 0.35F, 0.6F, 0.65F, 0.65F, biped.toolR, null),
				new Phase(0.65F, 0.65F, 0.75F, 0.90F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.2F),1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F),2)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F),2)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,2)				
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.55F);
		
		RUINE_AUTO_3 = new BasicMultipleAttackAnimation(0.05F, "biped/combat/ruine_auto_3", biped,
				new Phase(0.0F, 0.3F, 0.5F, 0.55F, 0.55F, biped.toolR, null),
				new Phase(0.55F, 0.65F, 0.85F, 1.0F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.50F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.90F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,1 )
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.35F);
		
		RUINE_AUTO_4 = new BasicMultipleAttackAnimation(0.05F, "biped/combat/ruine_auto_4", biped,
				new Phase(0.0F, 0.3F, 0.5F, 0.55F, 0.55F, biped.toolR, WOMColliders.RUINE_COMET),
				new Phase(0.55F, 0.8F, 1.05F, 1.45F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.20F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.40F),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.TARGET_LOST_HEALTH.create(0.10f)),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.4F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.4F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD )
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,1 )
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
				.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, (self, entitypatch, transformSheet) -> {
					LivingEntity attackTarget = entitypatch.getTarget();
					
					if (!self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
						TransformSheet transform = self.getTransfroms().get("Root").copyAll();
						Keyframe[] keyframes = transform.getKeyframes();
						int startFrame = 0;
						int endFrame = transform.getKeyframes().length - 1;
						Vec3f keyLast = keyframes[endFrame].transform().translation();
						Vec3 pos = entitypatch.getOriginal().getEyePosition();
						Vec3 targetpos = attackTarget.position();
						float horizontalDistance = Math.max((float)targetpos.subtract(pos).horizontalDistance() - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
						Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
						float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
						
						for (int i = startFrame; i <= endFrame; i++) {
							Vec3f translation = keyframes[i].transform().translation();
							translation.z *= scale;
						}
						
						transformSheet.readFrom(transform);
					} else {
						transformSheet.readFrom(self.getTransfroms().get("Root"));
					}
				}).addProperty(ActionAnimationProperty.COORD_SET_TICK, (self, entitypatch, transformSheet) -> {
					LivingEntity attackTarget = entitypatch.getTarget();
					if (entitypatch instanceof PlayerPatch<?>) {
						PlayerPatch<?> playerPatch;
						playerPatch = (PlayerPatch<?>) entitypatch;
						
						if (!self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
							TransformSheet transform = self.getTransfroms().get("Root").copyAll();
							Keyframe[] keyframes = transform.getKeyframes();
							int startFrame = 0;
							int endFrame = transform.getKeyframes().length - 1;
							Vec3f keyLast = keyframes[endFrame].transform().translation();
							Vec3 pos = entitypatch.getOriginal().getEyePosition();
							Vec3 targetpos = attackTarget.position();
							float horizontalDistance = (float) Math.max((float)targetpos.subtract(pos).horizontalDistance()*(1+(1.0f/playerPatch.getAttackSpeed(InteractionHand.MAIN_HAND))) - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
							Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
							float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
							
							for (int i = startFrame; i <= endFrame; i++) {
								Vec3f translation = keyframes[i].transform().translation();
								translation.z *= scale;
							}
							
							transformSheet.readFrom(transform);
						} else {
							transformSheet.readFrom(self.getTransfroms().get("Root"));
						}
					}
				})
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.4F, ReuseableEvents.RUINE_COMET_AIRBURST, Side.CLIENT));
				
		
		RUINE_COUNTER = new BasicMultipleAttackAnimation(0.05F, "biped/skill/ruine_counterattack", biped,
				new Phase(0.0F, 0.2F, 0.25F, 0.3F, 0.3F, biped.rootJoint, WOMColliders.SHOULDER_BUMP),
				new Phase(0.3F, 0.5F, 0.7F, 1.0F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(2.0F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.20F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.3F),1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get())			
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)			
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)			
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.25F);
		
		RUINE_DASH = new BasicMultipleAttackAnimation(0.1F, 0.15F, 0.65F, 0.75F, null, biped.toolR, "biped/combat/ruine_dash", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.4F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.6F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.35F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false);
		
		RUINE_COMET = new BasicMultipleAttackAnimation(0.05F, 0.25F, 0.55F, 0.75F, WOMColliders.RUINE_COMET, biped.toolR, "biped/combat/ruine_comet", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_TARGET_CURRENT_HEALTH.create(0.05f)))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.8F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 20)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.25F)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.3F))
				.addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, elapsedTime) -> {
					if (elapsedTime >= 0.35F && elapsedTime < 0.45F) {
						float dpx = (float) entitypatch.getOriginal().getX();
						float dpy = (float) entitypatch.getOriginal().getY();
						float dpz = (float) entitypatch.getOriginal().getZ();
						BlockState block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						
						while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
							dpy--;
							block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						}
						
						float distanceToGround = (float) Math.max(Math.abs(entitypatch.getOriginal().getY() - dpy)-1, 0.0F);
						
						LivingEntity livingentity = entitypatch.getOriginal();
						
						Vec3f direction = new Vec3f(2.5F,-0.25f, 0.0f);
					    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO+90), new Vec3f(0, 1, 0));
					    OpenMatrix4f.transform3v(rotation, direction, direction);
					    
					    AABB box = AABB.ofSize(entitypatch.getOriginal().getPosition(1.0f),3, 3, 3);
						
						List<Entity> list = entitypatch.getOriginal().level().getEntities(entitypatch.getOriginal(),box);
					    
					    if (distanceToGround > 0.5F && list.size() == 0) {
					    	livingentity.move(MoverType.SELF, direction.toDoubleVector());
					    	
					    	return 0.05f;
						} else {
							return 1;
						}
					}
					
					return 1.0F;
				})
				.addEvents(TimeStampedEvent.create(0.25F, ReuseableEvents.RUINE_COMET_AIRBURST, Side.CLIENT),
						TimeStampedEvent.create(0.50F, ReuseableEvents.RUINE_COMET_GROUNDTHRUST, Side.CLIENT));
		
		RUINE_BLOCK = new StaticAnimation(0.05F, true, "biped/skill/ruine_block", biped);
		RUINE_IDLE = new StaticAnimation(0.1f,true, "biped/living/ruine_idle", biped);
		RUINE_BOOSTED_IDLE = new StaticAnimation(0.2f,true, "biped/living/ruine_boosted_idle", biped);
		RUINE_RUN = new MovementAnimation(0.1f,true, "biped/living/ruine_run", biped);
		RUINE_WALK = new MovementAnimation(0.1f,true, "biped/living/ruine_walk", biped);
		RUINE_BOOSTED_WALK = new MovementAnimation(0.1f,true, "biped/living/ruine_boosted_walk", biped);
		
		RUINE_PLUNDER = new SpecialAttackAnimation(0.05F, "biped/skill/ruine_plunder", biped,
				new Phase(0.0F, 1.1F, 1.25F, 1.85F, 1.85F, biped.rootJoint, WOMColliders.PLUNDER_PERDITION), 
				new Phase(1.85F, 1.85F, 2.2F, 3.35F, Float.MAX_VALUE, biped.rootJoint, WOMColliders.PLUNDER_PERDITION))
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_HIT.get(),0)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.RUINE_PLUNDER_SWORD,0)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20),0)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F),0)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,0)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.3F),0)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()),1)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20),1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_HIT.get(),1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.00F);
		
		RUINE_EXPIATION = new SpecialAttackAnimation(0.20F, "biped/skill/ruine_expiation", biped,
				new Phase(0.0F, 0.25F, 0.35F, 0.4F, 0.4F, biped.rootJoint, WOMColliders.SHOULDER_BUMP), 
				new Phase(0.4F, 0.65F, 0.8F, 1.2F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.8F),0)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F),0)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_HIT.get(),0)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.RUINE_PLUNDER_SWORD,0)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.5f)),0)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),0)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,0)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F),1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_HIT.get(),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.5f)),1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.45F)
				.addProperty(ActionAnimationProperty.COORD_SET_TICK, (self, entitypatch, transformSheet) -> {
					LivingEntity attackTarget = entitypatch.getTarget();
					if (entitypatch instanceof PlayerPatch<?>) {
						
						if (!self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
							TransformSheet transform = self.getTransfroms().get("Root").copyAll();
							Keyframe[] keyframes = transform.getKeyframes();
							int startFrame = 0;
							int endFrame = transform.getKeyframes().length - 1;
							Vec3f keyLast = keyframes[endFrame].transform().translation();
							Vec3 pos = entitypatch.getOriginal().getEyePosition();
							Vec3 targetpos = attackTarget.position();
							float horizontalDistance = (float) Math.max(((float)targetpos.subtract(pos).horizontalDistance()*1.50f) - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
							Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
							float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
							
							for (int i = startFrame; i <= endFrame; i++) {
								Vec3f translation = keyframes[i].transform().translation();
								translation.z *= scale;
							}
							
							transformSheet.readFrom(transform);
						} else {
							transformSheet.readFrom(self.getTransfroms().get("Root"));
						}
					}
				})
				.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, (self, entitypatch, transformSheet) -> {
					LivingEntity attackTarget = entitypatch.getTarget();
					if (entitypatch instanceof PlayerPatch<?>) {
						if (!self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
							TransformSheet transform = self.getTransfroms().get("Root").copyAll();
							Keyframe[] keyframes = transform.getKeyframes();
							int startFrame = 0;
							int endFrame = transform.getKeyframes().length - 1;
							Vec3f keyLast = keyframes[endFrame].transform().translation();
							Vec3 pos = entitypatch.getOriginal().getEyePosition();
							Vec3 targetpos = attackTarget.position();
							float horizontalDistance = (float) Math.max(((float)targetpos.subtract(pos).horizontalDistance()*1.50f) - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
							Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
							float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
							
							for (int i = startFrame; i <= endFrame; i++) {
								Vec3f translation = keyframes[i].transform().translation();
								translation.z *= scale;
							}
							
							transformSheet.readFrom(transform);
						} else {
							transformSheet.readFrom(self.getTransfroms().get("Root"));
						}
					}
				})
				.addEvents(TimeStampedEvent.create(0.1F, (entitypatch, self, params) -> {
					if (entitypatch instanceof PlayerPatch) {
						LivingEntity entity = entitypatch.getOriginal();
						if ((entitypatch.getOriginal().getLastHurtMob() != null && ((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getStack() > 1) || ((ServerPlayer) entitypatch.getOriginal()).isCreative()) {
							LivingEntity target = entitypatch.getOriginal().getLastHurtMob();
							if (target != null) {
								double offset = 4.0; // Adjust this value as needed

								// Calculate the new position based on the reference entity's position and rotation
								double referenceX = target.getX();
								double referenceY = target.getY();
								double referenceZ = target.getZ();
								float referenceYaw = entity.yHeadRot;

								double newX = referenceX + (offset * Math.sin(Math.toRadians(referenceYaw)));
								double newZ = referenceZ - (offset * Math.cos(Math.toRadians(referenceYaw)));
								double newY = referenceY; // Keep the same Y position
								
								entity.teleportTo(
										newX,
										newY,
										newZ);
								entity.setDeltaMovement(target.getDeltaMovement());
							}
							((ServerLevel) entity.level()).sendParticles(ParticleTypes.REVERSE_PORTAL,
									entity.getX(), 
									entity.getY() + 1, 
									entity.getZ(),
									60,
									0.05,
								    0.05,
									0.05,
									0.5);
							entity.level().playSound(null, 
									entity.xo, 
									entity.yo + 1, 
									entity.zo,
					    			SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 2.0F, 1.0F - ((new Random().nextFloat()-0.5f) * 0.2F));
						}
					}
				}, Side.SERVER),
				TimeStampedEvent.create(0.05F, (entitypatch, self, params) -> {
					if (entitypatch.getOriginal().getLastHurtMob() != null && ((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getStack() > 1) {
						LivingEntity entity = entitypatch.getOriginal();
						entitypatch.getOriginal().level().addParticle(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
					}
				}, Side.CLIENT));
		
		RUINE_REDEMPTION = new SpecialAttackAnimation(0.05F, "biped/skill/ruine_redemption", biped,
				new Phase(0.0F, 0.60F, 0.75F, 1.0F, Float.MAX_VALUE, biped.rootJoint, WOMColliders.RUINE_REDEMPTION))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.8F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_HIT.get())
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.RUINE_PLUNDER_SWORD)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.5f)))
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.45F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, false)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.80F))
				.addEvents(TimeStampedEvent.create(0.15F, (entitypatch, self, params) -> {
					if (!entitypatch.isLogicalClient() && entitypatch instanceof PlayerPatch) {
						LivingEntity entity = entitypatch.getOriginal();
						if ((entitypatch.getOriginal().getLastHurtMob() != null && ((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getStack() > 1) || ((ServerPlayer) entitypatch.getOriginal()).isCreative()) {
							LivingEntity target = entitypatch.getOriginal().getLastHurtMob();
							if (target != null) {
								double offset = 4.0; // Adjust this value as needed

								// Calculate the new position based on the reference entity's position and rotation
								double referenceX = target.getX();
								double referenceY = target.getY();
								double referenceZ = target.getZ();
								float referenceYaw = entity.yHeadRot;

								double newX = referenceX;
								double newZ = referenceZ;
								double newY = referenceY + offset; // Keep the same Y position
								
								entity.teleportTo(
										newX,
										newY,
										newZ);
								entity.setDeltaMovement(target.getDeltaMovement());
							}
						}
					}
				}, Side.SERVER),
				TimeStampedEvent.create(0.00F, (entitypatch, self, params) -> {
					if (!entitypatch.isLogicalClient() && entitypatch instanceof PlayerPatch) {
						LivingEntity entity = entitypatch.getOriginal();
						((ServerLevel) entity.level()).sendParticles(ParticleTypes.REVERSE_PORTAL,
								entity.getX(), 
								entity.getY() + 1, 
								entity.getZ(),
								60,
								0.05,
							    0.05,
								0.05,
								0.5);
						entity.level().playSound(null, 
								entity.xo, 
								entity.yo + 1, 
								entity.zo,
				    			SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 2.0F, 1.0F - ((new Random().nextFloat()-0.5f) * 0.2F));
					} else {
						LivingEntity entity = entitypatch.getOriginal();
						entitypatch.getOriginal().level().addParticle(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
					}
				}, Side.BOTH),
				TimeStampedEvent.create(0.75F, (entitypatch, self, params) -> {
					Vec3 bodyFloorPos = getfloor(entitypatch, self,new Vec3f(0,0.0F, 0.0F),Armatures.BIPED.rootJoint);
					entitypatch.getOriginal().teleportTo(
							bodyFloorPos.x,
							(int)(bodyFloorPos.y)+1,
							bodyFloorPos.z);
				}, Side.SERVER));
		
		TORMENT_AUTO_1 = new BasicMultipleAttackAnimation(0.15F, 0.2F, 0.60F, 0.65F, null, biped.toolR, "biped/combat/torment_auto_1", biped)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.5F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 2)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.10F);
		
		TORMENT_AUTO_2 = new BasicMultipleAttackAnimation(0.20F, 0.2F, 0.5F, 0.6F, null, biped.toolR, "biped/combat/torment_auto_2", biped)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.10F);
		
		TORMENT_AUTO_3 = new BasicMultipleAttackAnimation(0.25F, 0.05F, 0.3F, 0.3F, null, biped.toolR, "biped/combat/torment_auto_3", biped)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.2F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get())
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.20F);
		
		TORMENT_AUTO_4 = new BasicMultipleAttackAnimation(0.25F, "biped/combat/torment_auto_4", biped,
				new Phase(0.0F, 0.15F, 0.3F, 0.4F, 0.4F, biped.toolR, null),
				new Phase(0.4F, 0.4F, 0.55F, 0.9F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.7F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.20F)
				.addEvents(TimeStampedEvent.create(0.55F, ReuseableEvents.TORMENT_GROUNDSLAM_SMALL, Side.CLIENT));
		
		TORMENT_DASH = new BasicMultipleAttackAnimation(0.1F, 0.15F, 0.35F, 0.6F, null, biped.toolR, "biped/combat/torment_dash", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.8F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.8F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.20F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
				.addEvents(TimeStampedEvent.create(0.45F, ReuseableEvents.TORMENT_GROUNDSLAM_SMALL, Side.CLIENT));
		
		TORMENT_AIRSLAM = new BasicMultipleAttackAnimation(0.1F, "biped/combat/torment_airslam", biped,
				new Phase(0.0F, 0.45F, 0.55F, 0.6F, 0.6F, biped.toolR, null),
				new Phase(0.6F, 0.50F, 0.65F, 0.8F, Float.MAX_VALUE, biped.rootJoint, WOMColliders.TORMENT_AIRSLAM))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.8F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F),1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.0F))
				.addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, elapsedTime) -> {
					if (elapsedTime >= 0.3F && elapsedTime < 0.55F) {
						float dpx = (float) entitypatch.getOriginal().getX();
						float dpy = (float) entitypatch.getOriginal().getY();
						float dpz = (float) entitypatch.getOriginal().getZ();
						BlockState block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						
						while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
							dpy--;
							block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						}
						
						float distanceToGround = (float) Math.max(Math.abs(entitypatch.getOriginal().getY() - dpy)-1, 0.0F);
						
						return 1 - (1 / (-distanceToGround - 1.F) + 1.0f);
					}
					
					return 1.0F;
				})
				.addEvents(TimeStampedEvent.create(0.55F, ReuseableEvents.TORMENT_GROUNDSLAM_SMALL, Side.CLIENT));
		
		TORMENT_IDLE = new StaticAnimation(0.1f,true, "biped/living/torment_idle", biped);
		TORMENT_RUN = new MovementAnimation(0.1f,true, "biped/living/torment_run", biped);
		TORMENT_WALK = new MovementAnimation(0.1f,true, "biped/living/torment_walk", biped);
		
		TORMENT_CHARGE = new StaticAnimation(0.05F, true, "biped/living/torment_charge", biped);
		
		TORMENT_CHARGED_ATTACK_1 = new BasicMultipleAttackAnimation(0.05F, "biped/combat/torment_charged_attack_1", biped,
				new Phase(0.0F, 0.2F, 0.5F, 0.55F, 0.55F, biped.toolR, null),
				new Phase(0.55F, 0.6F, 0.85F, 0.9F, 0.9F, biped.toolR, null),
				new Phase(0.9F, 1.20F, 1.35F, 1.55F, Float.MAX_VALUE, biped.rootJoint, WOMColliders.TORMENT_BERSERK_AIRSLAM))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F),1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F),2)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(2F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(2F),1)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(2F),2)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.7F),2)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,2)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.20F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.7F, 1.0F))
				.addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, elapsedTime) -> {
					if (elapsedTime >= 1.0F && elapsedTime < 1.3F) {
						float dpx = (float) entitypatch.getOriginal().getX();
						float dpy = (float) entitypatch.getOriginal().getY();
						float dpz = (float) entitypatch.getOriginal().getZ();
						BlockState block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						
						while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
							dpy--;
							block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						}
						
						float distanceToGround = (float) Math.max(Math.abs(entitypatch.getOriginal().getY() - dpy)-1, 0.0F);
						
						return 1 - (1 / (-distanceToGround - 1.F) + 1.0f);
					}
					
					return 1.0F;
				})
				.addEvents(TimeStampedEvent.create(1.3F, ReuseableEvents.TORMENT_GROUNDSLAM_SMALL, Side.CLIENT));
		
		TORMENT_CHARGED_ATTACK_2 = new BasicMultipleAttackAnimation(0.05F, 0.25F, 0.4F, 1.00F, null, biped.toolR, "biped/combat/torment_charged_attack_2", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.00F)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.15F, 0.65F))
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.COORD_SET_TICK, (self, entitypatch, transformSheet) -> {
					LivingEntity attackTarget = entitypatch.getTarget();
					if (entitypatch instanceof PlayerPatch<?>) {
						PlayerPatch<?> playerPatch;
						playerPatch = (PlayerPatch<?>) entitypatch;
						
						if (!self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
							TransformSheet transform = self.getTransfroms().get("Root").copyAll();
							Keyframe[] keyframes = transform.getKeyframes();
							int startFrame = 0;
							int endFrame = transform.getKeyframes().length - 1;
							Vec3f keyLast = keyframes[endFrame].transform().translation();
							Vec3 pos = entitypatch.getOriginal().getEyePosition();
							Vec3 targetpos = attackTarget.position();
							float horizontalDistance = (float) Math.max((float)targetpos.subtract(pos).horizontalDistance()*(1+(1.0f/playerPatch.getAttackSpeed(InteractionHand.MAIN_HAND))) - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
							Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
							float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
							
							for (int i = startFrame; i <= endFrame; i++) {
								Vec3f translation = keyframes[i].transform().translation();
								translation.z *= scale;
							}
							
							transformSheet.readFrom(transform);
						} else {
							transformSheet.readFrom(self.getTransfroms().get("Root"));
						}
					}
				});
		
		TORMENT_CHARGED_ATTACK_3 = new BasicMultipleAttackAnimation(0.05F, 1.00F, 1.2F, 1.50F, WOMColliders.TORMENT_BERSERK_AIRSLAM, biped.rootJoint, "biped/combat/torment_charged_attack_3", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.8F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(3F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.10F)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.35F, 0.9F))
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, elapsedTime) -> {
					if (elapsedTime >= 0.9F && elapsedTime < 1.15F) {
						float dpx = (float) entitypatch.getOriginal().getX();
						float dpy = (float) entitypatch.getOriginal().getY();
						float dpz = (float) entitypatch.getOriginal().getZ();
						BlockState block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						
						while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
							dpy--;
							block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						}
						
						float distanceToGround = (float) Math.max(Math.abs(entitypatch.getOriginal().getY() - dpy)-1, 0.0F);
						
						return 1 - (1 / (-distanceToGround - 1.F) + 1.0f);
					}
					
					return 1.0F;
				})
				.addEvents(TimeStampedEvent.create(0.35F, ReuseableEvents.AGONY_AIRBURST_JUMP, Side.CLIENT),
						TimeStampedEvent.create(1.15F, ReuseableEvents.TORMENT_GROUNDSLAM, Side.CLIENT));
		
		TORMENT_BERSERK_IDLE = new StaticAnimation(true, "biped/living/torment_berserk_idle", biped);
		
		TORMENT_BERSERK_AUTO_1 = new BasicMultipleAttackAnimation(0.2F, 0.1F, 0.35F, 0.65F, null, biped.toolR, "biped/skill/torment_berserk_auto_1", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.1F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(3F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.OVERBLOOD_HIT)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.75F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.50F);
		
		TORMENT_BERSERK_AUTO_2 = new BasicMultipleAttackAnimation(0.2F, 0.1F, 0.55F, 0.70F, null, biped.toolR, "biped/skill/torment_berserk_auto_2", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.1F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(3F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.OVERBLOOD_HIT)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.75F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.50F);
		
		TORMENT_BERSERK_DASH = new BasicMultipleAttackAnimation(0.15F, "biped/skill/torment_berserk_dash", biped,
				new Phase(0.0F, 0.2F, 0.3F, 0.35F, 0.35F, biped.rootJoint, WOMColliders.TORMENT_BERSERK_DASHSLAM),
				new Phase(0.35F, 0.5F, 0.6F, 0.65F, 0.65F, biped.rootJoint, WOMColliders.TORMENT_BERSERK_DASHSLAM),
				new Phase(0.65F, 0.9F, 1.0F, 1.2F, Float.MAX_VALUE, biped.rootJoint, WOMColliders.TORMENT_BERSERK_DASHSLAM))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()),1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F),2)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()),2)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(3F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(3F),1)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(3F),2)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,2)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, false)
				.addEvents(TimeStampedEvent.create(0.4F, ReuseableEvents.TORMENT_GROUNDSLAM_SMALL, Side.CLIENT),
					TimeStampedEvent.create(0.7F, ReuseableEvents.TORMENT_GROUNDSLAM_SMALL, Side.CLIENT),
					TimeStampedEvent.create(1.15F, ReuseableEvents.TORMENT_GROUNDSLAM_SMALL, Side.CLIENT));
		
		TORMENT_BERSERK_AIRSLAM = new BasicMultipleAttackAnimation(0.05F, 0.5F, 0.7F, 1.2F, WOMColliders.TORMENT_BERSERK_AIRSLAM, biped.rootJoint, "biped/skill/torment_berserk_airslam", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(4F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.20F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.50F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.30F))
				.addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, elapsedTime) -> {
					if (elapsedTime >= 0.3F && elapsedTime < 0.35f) {
						float dpx = (float) entitypatch.getOriginal().getX();
						float dpy = (float) entitypatch.getOriginal().getY();
						float dpz = (float) entitypatch.getOriginal().getZ();
						BlockState block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						
						while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
							dpy--;
							block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						}
						
						float distanceToGround = (float) Math.max(Math.abs(entitypatch.getOriginal().getY() - dpy)-1, 0.0F);
						
						return 1 - (1 / (-distanceToGround - 1.F) + 1.0f);
					}
					
					return 1.0F;
				})
				.addEvents(TimeStampedEvent.create(0.0F, (entitypatch, self, params) -> {
							if (entitypatch instanceof PlayerPatch) {
								((PlayerPatch<?>)entitypatch).setStamina(((PlayerPatch<?>)entitypatch).getStamina() - 2.0f);
							}
						}, Side.CLIENT),
				TimeStampedEvent.create(0.6F, ReuseableEvents.TORMENT_GROUNDSLAM, Side.CLIENT));
		
		TORMENT_BERSERK_CONVERT = new BasicMultipleAttackAnimation(0.05F, 0.6F, 1.35F, 1.7F, WOMColliders.PLUNDER_PERDITION, biped.rootJoint, "biped/skill/torment_berserk_convert", biped)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.ENDERMAN_SCREAM)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.BLASTFURNACE_FIRE_CRACKLE)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(8.0F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20F))
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.00F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.50F);
		
		TORMENT_BERSERK_RUN = new MovementAnimation(0.1f,true, "biped/living/torment_berserk_run", biped);
		TORMENT_BERSERK_WALK = new MovementAnimation(0.1f,true, "biped/living/torment_berserk_walk", biped);
		
		KATANA_SHEATHED_AUTO_1 = new BasicMultipleAttackAnimation(0.05F, "biped/combat/katana_sheathed_auto_1", biped,
				new Phase(0.0F, 0.15F, 0.24F, 0.25F, 0.25F, biped.rootJoint, WOMColliders.KATANA_SHEATHED_AUTO),
				new Phase(0.25F, 0.3F, 0.40F, 0.55F, Float.MAX_VALUE, biped.rootJoint, WOMColliders.KATANA_SHEATHED_AUTO))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F),1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.KATANA_SHEATHED_HIT)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.KATANA_SHEATHED_HIT,1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
				.addEvents(TimeStampedEvent.create(0.10F, (entitypatch, self, params) -> {
					Entity entity = entitypatch.getOriginal();
					entitypatch.getOriginal().level().addParticle(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
				}, Side.CLIENT),TimeStampedEvent.create(0.90F, (entitypatch, self, params) -> {
					if (entitypatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill() != null) {
						((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(SatsujinPassive.SHEATH, true,(ServerPlayer)entitypatch.getOriginal());
						entitypatch.getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill().setConsumption(((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE), 5);
					}
					entitypatch.updateMotion(true);
					entitypatch.playSound(EpicFightSounds.SWORD_IN.get(), 0, 0);
				}, Side.SERVER));
		
		KATANA_SHEATHED_AUTO_2 = new BasicMultipleAttackAnimation(0.05F, "biped/combat/katana_sheathed_auto_2", biped,
				new Phase(0.0F, 0.15F, 0.24F, 0.25F, 0.25F, biped.rootJoint, WOMColliders.KATANA_SHEATHED_AUTO),
				new Phase(0.25F, 0.3F, 0.39F, 0.40F, 0.40F, biped.rootJoint, WOMColliders.KATANA_SHEATHED_AUTO),
				new Phase(0.40F, 0.5F, 0.59F, 0.7F, Float.MAX_VALUE, biped.rootJoint, WOMColliders.KATANA_SHEATHED_AUTO))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F),1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F),2)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.KATANA_SHEATHED_HIT)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.KATANA_SHEATHED_HIT,1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.KATANA_SHEATHED_HIT,2)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,2)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
				.addEvents(TimeStampedEvent.create(0.10F, (entitypatch, self, params) -> {
						Entity entity = entitypatch.getOriginal();
						entitypatch.getOriginal().level().addParticle(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
					}, Side.CLIENT),TimeStampedEvent.create(1.1F, (entitypatch, self, params) -> {
						((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(SatsujinPassive.SHEATH, true,(ServerPlayer)entitypatch.getOriginal());
						if (entitypatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill() != null) {
							entitypatch.getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill().setConsumption(((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE), 5);
						}
						entitypatch.updateMotion(true);
						entitypatch.playSound(EpicFightSounds.SWORD_IN.get(), 0, 0);
					}, Side.SERVER));
				
		
		KATANA_SHEATHED_AUTO_3 = new BasicMultipleAttackAnimation(0.05F, 0.2F, 0.4F, 0.65F, WOMColliders.KATANA_SHEATHED_AUTO, biped.rootJoint, "biped/combat/katana_sheathed_auto_3", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.8F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.5f)))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.1F))
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.KATANA_SHEATHED_HIT)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(3F))
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
				.addEvents(TimeStampedEvent.create(0.25F, (entitypatch, self, params) -> {
					Entity entity = entitypatch.getOriginal();
					entitypatch.getOriginal().level().addParticle(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
				}, Side.CLIENT),TimeStampedEvent.create(1.0F, (entitypatch, self, params) -> {
					((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(SatsujinPassive.SHEATH, true,(ServerPlayer)entitypatch.getOriginal());
					if (entitypatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill() != null) {
						entitypatch.getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill().setConsumption(((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE), 5);
					}
					entitypatch.updateMotion(true);
					entitypatch.playSound(EpicFightSounds.SWORD_IN.get(), 0, 0);
				}, Side.SERVER));
		
		KATANA_SHEATHED_DASH = new BasicMultipleAttackAnimation(0.15F, 0.16F, 0.4F, 1.0F,WOMColliders.KATANA_SHEATHED_DASH, biped.rootJoint, "biped/combat/katana_sheathed_dash", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.75F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.75F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10F))
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.KATANA_SHEATHED_HIT)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.0F, (entitypatch, self, params) -> {
					if (entitypatch instanceof PlayerPatch) {
						ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch)entitypatch); 
						if(!serverPlayerPatch.consumeStamina(4)){
							serverPlayerPatch.setStamina(0f);
						}
					}
				}, Side.SERVER),TimeStampedEvent.create(0.12F, (entitypatch, self, params) -> {
					Entity entity = entitypatch.getOriginal();
					entitypatch.getOriginal().level().addParticle(WOMParticles.ENTITY_AFTER_IMAGE_WEAPON.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
				}, Side.CLIENT),TimeStampedEvent.create(0.8F, (entitypatch, self, params) -> {
					if (entitypatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill() != null) {
						((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(SatsujinPassive.SHEATH, true,(ServerPlayer)entitypatch.getOriginal());
						entitypatch.getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill().setConsumption(((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE), 5);
						entitypatch.updateMotion(true);
					}
					entitypatch.playSound(EpicFightSounds.SWORD_IN.get(), 0, 0);
				}, Side.SERVER));
		
		KATANA_SHEATHED_COUNTER = new BasicMultipleAttackAnimation(0.15F, 0.16F, 0.4F, 1.0F,WOMColliders.KATANA_SHEATHED_DASH, biped.rootJoint, "biped/combat/katana_sheathed_dash", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.75F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.75F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10F))
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.KATANA_SHEATHED_HIT)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.0F, (entitypatch, self, params) -> {
					if (entitypatch instanceof PlayerPatch) {
						ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch)entitypatch); 
						if(!serverPlayerPatch.consumeStamina(4)){
							serverPlayerPatch.setStamina(0f);
						}
					}
				}, Side.SERVER),TimeStampedEvent.create(0.12F, (entitypatch, self, params) -> {
					Entity entity = entitypatch.getOriginal();
					entitypatch.getOriginal().level().addParticle(WOMParticles.ENTITY_AFTER_IMAGE_WEAPON.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
				}, Side.CLIENT),TimeStampedEvent.create(0.8F, (entitypatch, self, params) -> {
					if (entitypatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill() != null) {
						((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(SatsujinPassive.SHEATH, true,(ServerPlayer)entitypatch.getOriginal());
						entitypatch.getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill().setConsumption(((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE), 5);
						entitypatch.updateMotion(true);
					}
					entitypatch.playSound(EpicFightSounds.SWORD_IN.get(), 0, 0);
				}, Side.SERVER));
		
		KATANA_SHEATHED_COUNTER = new BasicMultipleAttackAnimation(0.15F, 0.16F, 0.4F, 0.6F,WOMColliders.KATANA_SHEATHED_DASH, biped.rootJoint, "biped/combat/katana_sheathed_counter", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.95F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10F))
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.KATANA_SHEATHED_HIT)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.12F, (entitypatch, self, params) -> {
					Entity entity = entitypatch.getOriginal();
					entitypatch.getOriginal().level().addParticle(WOMParticles.ENTITY_AFTER_IMAGE_WEAPON.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
				}, Side.CLIENT),TimeStampedEvent.create(0.55F, (entitypatch, self, params) -> {
					if (entitypatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill() != null) {
						((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(SatsujinPassive.SHEATH, true,(ServerPlayer)entitypatch.getOriginal());
						entitypatch.getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill().setConsumption(((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE), 5);
						entitypatch.updateMotion(true);
					}
					entitypatch.playSound(EpicFightSounds.SWORD_IN.get(), 0, 0);
				}, Side.SERVER));
		
		KATANA_SHEATHE = new StaticAnimation(0.1f,false, "biped/living/katana_sheathe", biped)
				.addEvents(TimeStampedEvent.create(0.2F, ReuseableEvents.KATANA_IN, Side.CLIENT));
		
		KATANA_SHEATHED_IDLE = new StaticAnimation(0.1f,true, "biped/living/katana_sheathed_idle", biped);
		KATANA_SHEATHED_RUN = new MovementAnimation(0.1f,true, "biped/living/katana_sheathed_run", biped);

		KATANA_AUTO_1 = new BasicMultipleAttackAnimation(0.05F, 0.00F, 0.2F, 0.25F, null, biped.toolR, "biped/combat/katana_auto_1", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.8F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 2)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F);
		KATANA_AUTO_2 = new BasicMultipleAttackAnimation(0.1F, "biped/combat/katana_auto_2", biped,
				new Phase(0.0F, 0.15F, 0.3F, 0.4F, 0.4F, biped.toolR, null),
				new Phase(0.4F, 0.4F, 0.65F, 0.65F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F),1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F);
		KATANA_AUTO_3 = new BasicMultipleAttackAnimation(0.05F, 0.25F, 0.4F, 0.75F, null, biped.toolR, "biped/combat/katana_auto_3", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F);
		
		KATANA_DASH = new BasicMultipleAttackAnimation(0.05F, 0.4F, 0.7F, 0.95F, null, biped.toolR, "biped/combat/katana_dash", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.4F);
		
		KATANA_IDLE = new StaticAnimation(0.2f,true, "biped/living/katana_idle", biped);
		KATANA_WALK = new MovementAnimation(0.1f,true, "biped/living/katana_walk", biped);
		KATANA_RUN = new MovementAnimation(0.1f,true, "biped/living/katana_run", biped);
		
		KATANA_GUARD = new StaticAnimation(0.05F, true, "biped/skill/katana_guard", biped);
		KATANA_GUARD_HIT = new GuardAnimation(0.05F, 0.5F, "biped/skill/katana_guard_hit", biped)
			.addEvents(TimeStampedEvent.create(0.05F, (entitypatch, self, params) -> {
					Entity entity = entitypatch.getOriginal();
					entitypatch.getOriginal().level().addParticle(WOMParticles.ENTITY_AFTER_IMAGE_WEAPON.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
					entitypatch.playSound(EpicFightSounds.WHOOSH.get(), 0, 0);
			}, Side.CLIENT),
				TimeStampedEvent.create(0.15F, (entitypatch, self, params) -> {
					Entity entity = entitypatch.getOriginal();
					entitypatch.playSound(EpicFightSounds.WHOOSH.get(), 0, 0);
					entitypatch.playSound(EpicFightSounds.SWORD_IN.get(), 0, 0);
					entitypatch.getOriginal().level().addParticle(WOMParticles.KATANA_SHEATHED_CUT.get(), entity.getX(), entity.getY() + entity.getBbHeight()/2f, entity.getZ(), 0, 0, 0);
				}, Side.CLIENT));
		
		KATANA_FATAL_DRAW = new SpecialAttackAnimation(0.15F, 0.0F, 0.50F, 0.70F, 0.70F, WOMColliders.FATAL_DRAW, biped.rootJoint, "biped/skill/katana_fatal_draw", biped)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP.get())
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.KATANA_SHEATHED_HIT)
				.addEvents(TimeStampedEvent.create(0.05F, ReuseableEvents.KATANA_IN, Side.SERVER),
						TimeStampedEvent.create(0.5F, (entitypatch, self, params) -> {
							if (entitypatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill() != null) {
								((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(SatsujinPassive.SHEATH, true,(ServerPlayer)entitypatch.getOriginal());
								entitypatch.getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill().setConsumption(((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE), 3);
							}
							entitypatch.updateMotion(true);
						}, Side.SERVER),TimeStampedEvent.create(2.05F, (entitypatch, self, params) -> {
							entitypatch.playSound(EpicFightSounds.WHOOSH.get(), 0, 0);
						}, Side.SERVER),TimeStampedEvent.create(3.45F, (entitypatch, self, params) -> {
							entitypatch.playSound(EpicFightSounds.SWORD_IN.get(), 0, 0);
						}, Side.SERVER));
		
		KATANA_FATAL_DRAW_SECOND = new SpecialAttackAnimation(0.15F, 0.0F, 0.50F, 0.70F, 0.70F, WOMColliders.FATAL_DRAW, biped.rootJoint, "biped/skill/katana_fatal_draw_second", biped)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP.get())
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.KATANA_SHEATHED_HIT)
				.addEvents(TimeStampedEvent.create(0.05F, ReuseableEvents.KATANA_IN, Side.SERVER),
						TimeStampedEvent.create(0.5F, (entitypatch, self, params) -> {
							if (entitypatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill() != null) {
								((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(SatsujinPassive.SHEATH, true,(ServerPlayer)entitypatch.getOriginal());
								entitypatch.getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill().setConsumption(((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE), 3);
							}
							entitypatch.updateMotion(true);
						}, Side.SERVER),TimeStampedEvent.create(2.05F, (entitypatch, self, params) -> {
							entitypatch.playSound(EpicFightSounds.WHOOSH.get(), 0, 0);
						}, Side.SERVER),TimeStampedEvent.create(3.45F, (entitypatch, self, params) -> {
							entitypatch.playSound(EpicFightSounds.SWORD_IN.get(), 0, 0);
						}, Side.SERVER));
		
		KATANA_FATAL_DRAW_DASH = new SpecialAttackAnimation(0.15F, 0.35F, 0.50F, 0.70F, 1.05F, WOMColliders.FATAL_DRAW_DASH, biped.rootJoint, "biped/skill/katana_fatal_draw_dash", biped)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP.get())
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.KATANA_SHEATHED_HIT)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addEvents(TimeStampedEvent.create(0.05F, ReuseableEvents.KATANA_IN, Side.SERVER),
				TimeStampedEvent.create(0.5F, (entitypatch, self, params) -> {
					Entity entity = entitypatch.getOriginal();
					entitypatch.getOriginal().level().addParticle(WOMParticles.ENTITY_AFTER_IMAGE_WEAPON.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
				}, Side.CLIENT),TimeStampedEvent.create(0.8F, (entitypatch, self, params) -> {
					if (entitypatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill() != null) {
						((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(SatsujinPassive.SHEATH, true,(ServerPlayer)entitypatch.getOriginal());
						entitypatch.getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill().setConsumption(((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE), 3);
					}
					entitypatch.updateMotion(true);
				}, Side.SERVER),TimeStampedEvent.create(1.90F, (entitypatch, self, params) -> {
					entitypatch.playSound(EpicFightSounds.WHOOSH.get(), 0, 0);
				}, Side.SERVER),TimeStampedEvent.create(3.35F, (entitypatch, self, params) -> {
					entitypatch.playSound(EpicFightSounds.SWORD_IN.get(), 0, 0);
				}, Side.SERVER));
		
		KATANA_SAKURA_TIMED_SLASH = new SpecialAttackAnimation(0.05F, 0.0F, 0.0F, 0.0F, 0.00F, null, biped.rootJoint, "biped/skill/katana_sakura_timed_slash", biped);
		
		ENDERBLASTER_ONEHAND_AUTO_1 = new BasicMultipleAttackAnimation(0.05F, "biped/combat/enderblaster_onehand_auto_1", biped,
				new Phase(0.0F, 0.15F, 0.24F, 0.25F, 0.25F, biped.legL, WOMColliders.KICK),
				new Phase(0.25F, 0.3F, 0.34F, 0.35F, Float.MAX_VALUE, biped.legL, WOMColliders.KICK))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get())
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get(),1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F);
		
		ENDERBLASTER_ONEHAND_AUTO_2 = new BasicMultipleAttackAnimation(0.15F, "biped/combat/enderblaster_onehand_auto_2", biped,
				new Phase(0.0F, 0.15F, 0.24F, 0.25F, 0.25F, biped.legR, WOMColliders.KICK),
				new Phase(0.25F, 0.3F, 0.44F, 0.45F, 0.45F, biped.toolR, null),
				new Phase(0.45F, 0.5F, 0.6F, 0.75F, Float.MAX_VALUE, biped.elbowL, WOMColliders.KNEE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.30F))
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get())
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.40F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.50F),2)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,2)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get(),2)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,2)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.30F),2)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F),2)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F);
		
		ENDERBLASTER_ONEHAND_AUTO_3 = new BasicMultipleAttackAnimation(0.15F, "biped/combat/enderblaster_onehand_auto_3", biped,
				new Phase(0.0F, 0.05F, 0.19F, 0.2F, 0.2F, biped.toolR, null),
				new Phase(0.2F, 0.3F, 0.4F, 0.45F, Float.MAX_VALUE, biped.toolL, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.50F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.50F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.8F),1)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get(),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F);
		
		ENDERBLASTER_ONEHAND_AUTO_4 = new BasicMultipleAttackAnimation(0.15F, "biped/combat/enderblaster_onehand_auto_4", biped,
				new Phase(0.0F, 0.3F, 0.4F, 0.95F, Float.MAX_VALUE, biped.legL, WOMColliders.KICK_HUGE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.50F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(2.00F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get())
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false).addProperty(ActionAnimationProperty.COORD_SET_BEGIN, (self, entitypatch, transformSheet) -> {
					LivingEntity attackTarget = entitypatch.getTarget();
					
					if (!self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
						TransformSheet transform = self.getTransfroms().get("Root").copyAll();
						Keyframe[] keyframes = transform.getKeyframes();
						int startFrame = 0;
						int endFrame = transform.getKeyframes().length - 1;
						Vec3f keyLast = keyframes[endFrame].transform().translation();
						Vec3 pos = entitypatch.getOriginal().getEyePosition();
						Vec3 targetpos = attackTarget.position();
						float horizontalDistance = Math.max((float)targetpos.subtract(pos).horizontalDistance() - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
						Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
						float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
						
						for (int i = startFrame; i <= endFrame; i++) {
							Vec3f translation = keyframes[i].transform().translation();
							translation.z *= scale;
						}
						
						transformSheet.readFrom(transform);
					} else {
						transformSheet.readFrom(self.getTransfroms().get("Root"));
					}
				}).addProperty(ActionAnimationProperty.COORD_SET_TICK, (self, entitypatch, transformSheet) -> {
					LivingEntity attackTarget = entitypatch.getTarget();
					
					if (!self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
						TransformSheet transform = self.getTransfroms().get("Root").copyAll();
						Keyframe[] keyframes = transform.getKeyframes();
						int startFrame = 0;
						int endFrame = transform.getKeyframes().length - 1;
						Vec3f keyLast = keyframes[endFrame].transform().translation();
						Vec3 pos = entitypatch.getOriginal().getEyePosition();
						Vec3 targetpos = attackTarget.position();
						float horizontalDistance = Math.max((float)targetpos.subtract(pos).horizontalDistance()*2.0f - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
						Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
						float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
						
						for (int i = startFrame; i <= endFrame; i++) {
							Vec3f translation = keyframes[i].transform().translation();
							translation.z *= scale;
						}
						
						transformSheet.readFrom(transform);
					} else {
						transformSheet.readFrom(self.getTransfroms().get("Root"));
					}
				});
				
		
		ENDERBLASTER_ONEHAND_DASH = new BasicMultipleAttackAnimation(0.05F, "biped/combat/enderblaster_onehand_dash", biped, false,
				new Phase(0.0F, 0.15F, 0.20F, 0.45F, 0.45F, biped.legL, WOMColliders.KICK_HUGE),
				new Phase(0.45F, 0.45F, 0.75F, 1.0F, Float.MAX_VALUE, biped.legL, WOMColliders.KICK_HUGE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get())
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,1)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get(),1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.05F, 0.75F))
				.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, (self, entitypatch, transformSheet) -> {
					LivingEntity attackTarget = entitypatch.getTarget();
					
					if (!self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
						TransformSheet transform = self.getTransfroms().get("Root").copyAll();
						Keyframe[] keyframes = transform.getKeyframes();
						int startFrame = 0;
						int endFrame = transform.getKeyframes().length - 1;
						Vec3f keyLast = keyframes[endFrame].transform().translation();
						Vec3 pos = entitypatch.getOriginal().getEyePosition();
						Vec3 targetpos = attackTarget.position();
						float horizontalDistance = Math.max((float)targetpos.subtract(pos).horizontalDistance() - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
						Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
						float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
						
						for (int i = startFrame; i <= endFrame; i++) {
							Vec3f translation = keyframes[i].transform().translation();
							translation.z *= scale;
						}
						
						transformSheet.readFrom(transform);
					} else {
						transformSheet.readFrom(self.getTransfroms().get("Root"));
					}
				}).addProperty(ActionAnimationProperty.COORD_SET_TICK, (self, entitypatch, transformSheet) -> {
					LivingEntity attackTarget = entitypatch.getTarget();
					
					if (!self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
						TransformSheet transform = self.getTransfroms().get("Root").copyAll();
						Keyframe[] keyframes = transform.getKeyframes();
						int startFrame = 0;
						int endFrame = transform.getKeyframes().length - 1;
						Vec3f keyLast = keyframes[endFrame].transform().translation();
						Vec3 pos = entitypatch.getOriginal().getEyePosition();
						Vec3 targetpos = attackTarget.position();
						float horizontalDistance = Math.max((float)targetpos.subtract(pos).horizontalDistance()*1.0f - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
						Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
						float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
						
						for (int i = startFrame; i <= endFrame; i++) {
							Vec3f translation = keyframes[i].transform().translation();
							translation.z *= scale;
						}
						
						transformSheet.readFrom(transform);
					} else {
						transformSheet.readFrom(self.getTransfroms().get("Root"));
					}
				});
		
		ENDERBLASTER_ONEHAND_JUMPKICK = new BasicMultipleAttackAnimation(0.05F, "biped/combat/enderblaster_onehand_jumpkick", biped,
				new Phase(0.0F, 0.15F, 0.19F, 0.2F, 0.2F, biped.legL, WOMColliders.KICK_HUGE),
				new Phase(0.2F, 0.25F, 0.29F, 0.30F, 0.30F, biped.legR, WOMColliders.KICK_HUGE),
				new Phase(0.30F, 0.35F, 0.39F, 0.6F, 0.6F, biped.legL, WOMColliders.KICK_HUGE),
				new Phase(0.6F, 0.6F, 0.7F, 0.80F, Float.MAX_VALUE, biped.legR, WOMColliders.KICK_HUGE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3F))
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get())
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3F),1)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get(),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F),2)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3F),2)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,2)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get(),2)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,2)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F),3)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,3)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,3)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get(),3)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.STOP_MOVEMENT, false);
		
		ENDERBLASTER_ONEHAND_SHOOT_1 = new EnderblasterShootAttackAnimation(0.05F, "biped/skill/enderblaster_onehand_shoot_1", biped,
				new Phase(0.0F, 0.1F, 0.2F, 0.25F, 0.25F, biped.toolR, WOMColliders.ENDER_SHOOT),
				new Phase(0.25F, 0.30F, 0.45F, 0.55F, 0.55F, biped.toolR, WOMColliders.ENDER_SHOOT),
				new Phase(0.55F, 0.6F, 0.7F, 0.75F, Float.MAX_VALUE, biped.toolR, WOMColliders.ENDER_SHOOT))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),1)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),2)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),2)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,2)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,2)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,2)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),2)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.1F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.35F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.6F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT));
		
		ENDERBLASTER_ONEHAND_SHOOT_2 = new EnderblasterShootAttackAnimation(0.05F, "biped/skill/enderblaster_onehand_shoot_2", biped,
				new Phase(0.0F, 0.25F, 0.3F, 0.35F, 0.35F, biped.toolR, WOMColliders.ENDER_SHOOT),
				new Phase(0.35F, 0.5F, 0.55F, 0.65F, Float.MAX_VALUE, biped.toolR, WOMColliders.ENDER_SHOOT))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),1)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.25F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.5F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT));
		
		ENDERBLASTER_ONEHAND_SHOOT_2_FORWARD = new EnderblasterShootAttackAnimation(0.05F, "biped/skill/enderblaster_onehand_shoot_2_forward", biped,
				new Phase(0.0F, 0.25F, 0.3F, 0.35F, 0.35F, biped.toolR, WOMColliders.ENDER_SHOOT),
				new Phase(0.35F, 0.5F, 0.55F, 0.65F, Float.MAX_VALUE, biped.toolR, WOMColliders.ENDER_SHOOT))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE,WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),1)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.25F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.5F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT));
		
		ENDERBLASTER_ONEHAND_SHOOT_2_LEFT = new EnderblasterShootAttackAnimation(0.05F, "biped/skill/enderblaster_onehand_shoot_2_left", biped,
				new Phase(0.0F, 0.25F, 0.3F, 0.35F, 0.35F, biped.toolR, WOMColliders.ENDER_SHOOT),
				new Phase(0.35F, 0.5F, 0.55F, 0.65F, Float.MAX_VALUE, biped.toolR, WOMColliders.ENDER_SHOOT))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),1)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.25F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT),
						TimeStampedEvent.create(0.5F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT));
		
		ENDERBLASTER_ONEHAND_SHOOT_2_RIGHT = new EnderblasterShootAttackAnimation(0.05F, "biped/skill/enderblaster_onehand_shoot_2_right", biped,
				new Phase(0.0F, 0.25F, 0.3F, 0.35F, 0.35F, biped.toolR, WOMColliders.ENDER_SHOOT),
				new Phase(0.35F, 0.5F, 0.55F, 0.65F, Float.MAX_VALUE, biped.toolR, WOMColliders.ENDER_SHOOT))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),1)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.25F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.5F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT));
		
		ENDERBLASTER_ONEHAND_SHOOT_3 = new SpecialAttackAnimation(0.05F, 0.7F, 0.7F, 0.75F, 1.2F, WOMColliders.ENDER_LASER, biped.toolR, "biped/skill/enderblaster_onehand_shoot_3", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(1.00f)))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(4.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.00F, (entitypatch, self, params) -> {
					if (entitypatch instanceof PlayerPatch) {
						entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.BUZZ.get(), SoundSource.PLAYERS, 0.6F, 1.5F);
					}
				}, Side.CLIENT),TimeStampedEvent.create(0.75F, (entitypatch, self, params) -> {
					if (entitypatch instanceof PlayerPatch) {
						entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.LASER_BLAST.get(), SoundSource.PLAYERS, 0.7F, 1F);
					}
					OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.toolR);
					OpenMatrix4f transformMatrix2 = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.toolR);
					transformMatrix.translate(new Vec3f(0,-0.6F,-0.3F));
					transformMatrix2.translate(new Vec3f(0.0f,-0.8F,-0.3F));
					OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0));
					OpenMatrix4f.mul(CORRECTION,transformMatrix,transformMatrix);
					OpenMatrix4f.mul(CORRECTION,transformMatrix2,transformMatrix2);
					
					int n = 40; // set the number of particles to emit
					double r = 0.2; // set the radius of the disk to 1
					double t = 0.01; // set the thickness of the disk to 0.1
					
					for (int i = 0; i < n; i++) {
					    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
					    double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness

					    // calculate the emission direction in Cartesian coordinates using the polar coordinates
					    double x = r * Math.cos(phi) * Math.cos(theta);
					    double y = r * Math.cos(phi) * Math.sin(theta);
					    double z = r * Math.sin(phi);
					    
					 // create a Vector3f object to represent the emission direction
					    Vec3f direction = new Vec3f((float)x, (float)y, (float)z);

					    // rotate the direction vector to align with the forward vector
					    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO), new Vec3f(0, 1, 0));
					    rotation.rotate((float) ((transformMatrix.m11+0.07f)*1.5f), new Vec3f(1, 0, 0));
					    OpenMatrix4f.transform3v(rotation, direction, direction);
					    
					    // emit the particle in the calculated direction, with some random velocity added
					    entitypatch.getOriginal().level().addParticle(ParticleTypes.DRAGON_BREATH,
					        (transformMatrix.m30 + entitypatch.getOriginal().getX()),
					        (transformMatrix.m31 + entitypatch.getOriginal().getY()),
					        (transformMatrix.m32 + entitypatch.getOriginal().getZ()),
					        (float)((transformMatrix2.m30 - transformMatrix.m30) + direction.x),
					        (float)((transformMatrix2.m31 - transformMatrix.m31) + direction.y),
					        (float)((transformMatrix2.m32 - transformMatrix.m32) + direction.z));
					}
					
					HitResult ray = entitypatch.getOriginal().pick(10.D, 0.7F, false);

					entitypatch.getOriginal().level().addParticle(EpicFightParticles.LASER.get(),
							(transformMatrix.m30 + entitypatch.getOriginal().getX()),
							(transformMatrix.m31 + entitypatch.getOriginal().getY()),
							(transformMatrix.m32 + entitypatch.getOriginal().getZ()),
							(ray.getLocation().x),
							(ray.getLocation().y),
							(ray.getLocation().z));
				}, Side.CLIENT));
		
		ENDERBLASTER_ONEHAND_AIRSHOOT = new EnderblasterShootAttackAnimation(0.05F, "biped/skill/enderblaster_onehand_airshoot", biped,
				new Phase(0.0F, 0.15F, 0.2F, 0.25F, 0.25F, biped.toolR, WOMColliders.ENDER_SHOOT),
				new Phase(0.25F, 0.3F, 0.35F, 0.4F, Float.MAX_VALUE, biped.toolR, WOMColliders.ENDER_SHOOT))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),1)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.15F, ReuseableEvents.SHOTGUN_SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.3F, ReuseableEvents.SHOTGUN_SHOOT_RIGHT, Side.CLIENT));
		
		ENDERBLASTER_ONEHAND_SHOOT_DASH = new EnderblasterShootAttackAnimation(0.05F, "biped/skill/enderblaster_onehand_shoot_dash", biped,
				new Phase(0.0F, 0.0F, 0.25F, 0.55F, 0.55F, biped.rootJoint, WOMColliders.ENDER_DASH),
				new Phase(0.55F, 0.6F, 0.65F, 0.70F, 0.70F, biped.toolR, WOMColliders.ENDER_SHOOT),
				new Phase(0.70F, 0.85F, 0.9F, 1F, Float.MAX_VALUE, biped.toolR, WOMColliders.ENDER_SHOOT))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(6.0F))
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get())
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),1)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F),2)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F),2)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),2)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,2)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,2)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,2)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),2)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,2)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.6F, ReuseableEvents.SHOTGUN_SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.85F, ReuseableEvents.SHOTGUN_SHOOT_RIGHT, Side.CLIENT));
		
		ENDERBLASTER_ONEHAND_IDLE = new StaticAnimation(0.1f,true, "biped/living/enderblaster_onehand_idle", biped);
		ENDERBLASTER_ONEHAND_WALK = new MovementAnimation(0.1f,true, "biped/living/enderblaster_onehand_walk", biped);
		ENDERBLASTER_ONEHAND_RUN = new MovementAnimation(0.1f,true, "biped/living/enderblaster_onehand_run", biped);
		
		ENDERBLASTER_ONEHAND_AIMING = new AimAnimation(true, "biped/skill/enderblaster_onehand_aiming","biped/skill/enderblaster_onehand_aiming_up","biped/skill/enderblaster_onehand_aiming_down","biped/skill/enderblaster_onehand_aiming_layed", biped);
		ENDERBLASTER_ONEHAND_SHOOT = new EnderblasterShootAttackAnimation(0.05F, 0.00F, 0.05F, 0.5F, WOMColliders.ENDER_SHOOT, biped.toolR, "biped/skill/enderblaster_onehand_shoot", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.00F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT));
		
		ENDERBLASTER_ONEHAND_SHOOT_LAYED = new EnderblasterShootAttackAnimation(0.05F, 0.00F, 0.05F, 0.5F, WOMColliders.ENDER_SHOOT, biped.toolR, "biped/skill/enderblaster_onehand_shoot_layed", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.00F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT));
		
		ENDERBLASTER_ONEHAND_RELOAD = new StaticAnimation(0.1f,false, "biped/skill/enderblaster_onehand_reload", biped)
				.addEvents(TimeStampedEvent.create(0.2F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						   TimeStampedEvent.create(0.3F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						   TimeStampedEvent.create(0.4F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						   TimeStampedEvent.create(0.5F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						   TimeStampedEvent.create(0.7F, ReuseableEvents.ENDERBLASTER_RELOAD, Side.SERVER));
		
		ENDERBLASTER_TWOHAND_AUTO_1 = new BasicMultipleAttackAnimation(0.05F, "biped/combat/enderblaster_onehand_auto_1", biped,
				new Phase(0.0F, 0.15F, 0.24F, 0.25F, 0.25F,InteractionHand.OFF_HAND, biped.legL, WOMColliders.KICK),
				new Phase(0.25F, 0.3F, 0.34F, 0.35F, Float.MAX_VALUE,InteractionHand.OFF_HAND, biped.legL, WOMColliders.KICK))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get())
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F),1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get(),1);
		
		ENDERBLASTER_TWOHAND_AUTO_2 = new BasicMultipleAttackAnimation(0.15F, "biped/combat/enderblaster_onehand_auto_2", biped,
				new Phase(0.0F, 0.15F, 0.24F, 0.25F, 0.25F, biped.legR, WOMColliders.KICK),
				new Phase(0.25F, 0.3F, 0.44F, 0.45F, 0.45F, biped.toolR, null),
				new Phase(0.45F, 0.5F, 0.6F, 0.75F, Float.MAX_VALUE,InteractionHand.OFF_HAND, biped.elbowL, WOMColliders.KNEE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.30F))
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get())
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.40F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.50F),2)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,2)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get(),2)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,2)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.30F),2)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F),2);
		
		ENDERBLASTER_TWOHAND_AUTO_3 = new BasicMultipleAttackAnimation(0.15F, "biped/combat/enderblaster_onehand_auto_3", biped,
				new Phase(0.0F, 0.05F, 0.19F, 0.2F, 0.2F, biped.toolR, null),
				new Phase(0.2F, 0.3F, 0.40F, 0.45F, Float.MAX_VALUE,InteractionHand.OFF_HAND, biped.toolL, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.50F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.50F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.8F),1)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get(),1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,1);
		
		ENDERBLASTER_TWOHAND_AUTO_4 = new BasicMultipleAttackAnimation(0.15F, "biped/combat/enderblaster_onehand_auto_4", biped,
				new Phase(0.0F, 0.3F, 0.4F, 0.95F, Float.MAX_VALUE, biped.legL, WOMColliders.KICK_HUGE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.50F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(2.00F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get())
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, (self, entitypatch, transformSheet) -> {
					LivingEntity attackTarget = entitypatch.getTarget();
					
					if (!self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
						TransformSheet transform = self.getTransfroms().get("Root").copyAll();
						Keyframe[] keyframes = transform.getKeyframes();
						int startFrame = 0;
						int endFrame = transform.getKeyframes().length - 1;
						Vec3f keyLast = keyframes[endFrame].transform().translation();
						Vec3 pos = entitypatch.getOriginal().getEyePosition();
						Vec3 targetpos = attackTarget.position();
						float horizontalDistance = Math.max((float)targetpos.subtract(pos).horizontalDistance() - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
						Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
						float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
						
						for (int i = startFrame; i <= endFrame; i++) {
							Vec3f translation = keyframes[i].transform().translation();
							translation.z *= scale;
						}
						
						transformSheet.readFrom(transform);
					} else {
						transformSheet.readFrom(self.getTransfroms().get("Root"));
					}
				}).addProperty(ActionAnimationProperty.COORD_SET_TICK, (self, entitypatch, transformSheet) -> {
					LivingEntity attackTarget = entitypatch.getTarget();
					
					if (!self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
						TransformSheet transform = self.getTransfroms().get("Root").copyAll();
						Keyframe[] keyframes = transform.getKeyframes();
						int startFrame = 0;
						int endFrame = transform.getKeyframes().length - 1;
						Vec3f keyLast = keyframes[endFrame].transform().translation();
						Vec3 pos = entitypatch.getOriginal().getEyePosition();
						Vec3 targetpos = attackTarget.position();
						float horizontalDistance = Math.max((float)targetpos.subtract(pos).horizontalDistance()*2.0f - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
						Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
						float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
						
						for (int i = startFrame; i <= endFrame; i++) {
							Vec3f translation = keyframes[i].transform().translation();
							translation.z *= scale;
						}
						
						transformSheet.readFrom(transform);
					} else {
						transformSheet.readFrom(self.getTransfroms().get("Root"));
					}
				});
		
		ENDERBLASTER_TWOHAND_TISHNAW = new BasicMultipleAttackAnimation(0.05F, 0.3F, 0.5F, 0.65F, WOMColliders.KICK_HUGE, biped.legR, "biped/combat/enderblaster_twohand_tishnaw", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.65F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3.2F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(4.0F))
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get())
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 20)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.30F))
				.addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, elapsedTime) -> {
					if (elapsedTime >= 0.35F && elapsedTime < 0.45F) {
						float dpx = (float) entitypatch.getOriginal().getX();
						float dpy = (float) entitypatch.getOriginal().getY();
						float dpz = (float) entitypatch.getOriginal().getZ();
						BlockState block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						
						while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
							dpy--;
							block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						}
						
						float distanceToGround = (float) Math.max(Math.abs(entitypatch.getOriginal().getY() - dpy)-1, 0.0F);
						
						LivingEntity livingentity = entitypatch.getOriginal();
						
						Vec3f direction = new Vec3f(2.5F,-0.25f, 0.0f);
					    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO+90), new Vec3f(0, 1, 0));
					    OpenMatrix4f.transform3v(rotation, direction, direction);
					    
					    AABB box = AABB.ofSize(entitypatch.getOriginal().getPosition(1.0f),3, 3, 3);
						
						List<Entity> list = entitypatch.getOriginal().level().getEntities(entitypatch.getOriginal(),box);
					    
					    if (distanceToGround > 0.5F && list.size() == 0) {
					    	livingentity.move(MoverType.SELF, direction.toDoubleVector());
					    	
					    	return 0.05f;
						} else {
							return 1;
						}
					}
					
					return 1.0F;
				})
				.addEvents(TimeStampedEvent.create(0.3F, ReuseableEvents.RUINE_COMET_AIRBURST, Side.CLIENT),
					TimeStampedEvent.create(0.50F, ReuseableEvents.GROUND_BODYSCRAPE_LAND, Side.CLIENT));
		
		ENDERBLASTER_TWOHAND_TOMAHAWK = new BasicMultipleAttackAnimation(0.05F, "biped/combat/enderblaster_twohand_dash", biped,
				new Phase(0.0F, 0.3F, 0.44F, 0.45F, 0.45F, biped.legL, WOMColliders.KICK_HUGE),
				new Phase(0.45F, 0.5F, 0.6F, 0.65F, Float.MAX_VALUE, biped.rootJoint, WOMColliders.TORMENT_AIRSLAM ))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.3F),1)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(5.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(5.0F),1)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get())
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get(),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.45F, ReuseableEvents.GROUND_BODYSCRAPE_LAND, Side.CLIENT));
		
		ENDERBLASTER_TWOHAND_SHOOT_1 = new EnderblasterShootAttackAnimation(0.05F, "biped/skill/enderblaster_twohand_shoot_1", biped,
				new Phase(0.0F, 0.05F, 0.15F, 0.30F, 0.30F, biped.toolR, WOMColliders.ENDER_SHOOT),
				new Phase(0.30F, 0.35F, 0.4F, 0.55F, 0.55F, InteractionHand.OFF_HAND, biped.toolL, WOMColliders.ENDER_SHOOT),
				new Phase(0.55F, 0.6F, 0.65F, 0.70F, Float.MAX_VALUE, biped.toolR, WOMColliders.ENDER_SHOOT))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),1)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),2)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),2)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,2)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,2)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,2)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),2)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(
					TimeStampedEvent.create(0.1F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.4F, ReuseableEvents.SHOOT_LEFT, Side.CLIENT),
					TimeStampedEvent.create(0.65F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT));

		ENDERBLASTER_TWOHAND_SHOOT_2 = new EnderblasterShootAttackAnimation(0.05F, "biped/skill/enderblaster_twohand_shoot_2", biped,
				new Phase(0.0F, 0.4F, 0.44F, 0.45F, 0.45F, InteractionHand.OFF_HAND, biped.toolL, WOMColliders.ENDER_SHOOT),
				new Phase(0.45F, 0.5F, 0.54F, 0.55F, 0.55F, biped.toolR, WOMColliders.ENDER_SHOOT),
				new Phase(0.55F, 0.6F, 0.64F, 0.65F, 0.65F, InteractionHand.OFF_HAND, biped.toolL, WOMColliders.ENDER_SHOOT),
				new Phase(0.65F, 0.7F, 0.74F, 0.75F, 0.75F, biped.toolR, WOMColliders.ENDER_SHOOT),
				new Phase(0.75F, 0.8F, 0.84F, 0.85F, 0.85F, InteractionHand.OFF_HAND, biped.toolL, WOMColliders.ENDER_SHOOT),
				new Phase(0.85F, 0.9F, 0.94F, 0.95F, 0.95F, biped.toolR, WOMColliders.ENDER_SHOOT),
				new Phase(0.95F, 1.0F, 1.04F, 1.05F, 1.05F, InteractionHand.OFF_HAND, biped.toolL, WOMColliders.ENDER_SHOOT),
				new Phase(1.05F, 1.1F, 1.14F, 1.2F, Float.MAX_VALUE, biped.toolR, WOMColliders.ENDER_SHOOT))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),1)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),2)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),2)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,2)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,2)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,2)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),2)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),3)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),3)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,3)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,3)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,3)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),3)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),4)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),4)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,4)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,4)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,4)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),4)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),5)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),5)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,5)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,5)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,5)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),5)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),6)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),6)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,6)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,6)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,6)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),6)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),7)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),7)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,7)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,7)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,7)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),7)
				
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.45F, ReuseableEvents.SHOOT_LEFT, Side.CLIENT),
					TimeStampedEvent.create(0.55F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.65F, ReuseableEvents.SHOOT_LEFT, Side.CLIENT),
					TimeStampedEvent.create(0.75F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.85F, ReuseableEvents.SHOOT_LEFT, Side.CLIENT),
					TimeStampedEvent.create(0.95F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(1.05F, ReuseableEvents.SHOOT_LEFT, Side.CLIENT),
					TimeStampedEvent.create(1.15F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT));
		
		ENDERBLASTER_TWOHAND_SHOOT_3 = new EnderblasterShootAttackAnimation(0.05F, "biped/skill/enderblaster_twohand_shoot_3", biped,
				new Phase(0.0F, 0.05F, 0.25F, 0.30F, 0.30F, biped.rootJoint, WOMColliders.ENDER_DASH),
				new Phase(0.30F, 0.35F, 0.39F, 0.4F, 0.4F, InteractionHand.OFF_HAND, biped.rootJoint, WOMColliders.ENDER_BULLET_WIDE),
				new Phase(0.4F, 0.45F, 0.49F, 0.5F, 0.5F, biped.rootJoint, WOMColliders.ENDER_BULLET_WIDE),
				new Phase(0.5F, 0.55F, 0.59F, 0.6F, 0.6F, InteractionHand.OFF_HAND, biped.rootJoint, WOMColliders.ENDER_BULLET_WIDE),
				new Phase(0.6F, 0.65F, 0.69F, 0.70F, 0.70F, biped.rootJoint, WOMColliders.ENDER_BULLET_WIDE),
				new Phase(0.70F, 0.75F, 0.85F, 0.9F, Float.MAX_VALUE, InteractionHand.OFF_HAND, biped.rootJoint, WOMColliders.ENDER_BULLET_WIDE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(6.0F))
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT.get())
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),1)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(4.0F),1)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG,1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),2)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(4.0F),2)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),2)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,2)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,2)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,2)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG,2)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),2)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),3)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(4.0F),3)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),3)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,3)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,3)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,3)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG,3)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),3)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),4)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(4.0F),4)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),4)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,4)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,4)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,4)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG,4)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),4)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),5)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(4.0F),5)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),5)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,5)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,5)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,5)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG,5)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),5)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.4F, ReuseableEvents.SHOTGUN_SHOOT_BOTH, Side.CLIENT),
					TimeStampedEvent.create(0.5F, ReuseableEvents.SHOTGUN_SHOOT_BOTH, Side.CLIENT),
					TimeStampedEvent.create(0.6F, ReuseableEvents.SHOTGUN_SHOOT_BOTH, Side.CLIENT),
					TimeStampedEvent.create(0.7F, ReuseableEvents.SHOTGUN_SHOOT_BOTH, Side.CLIENT),
					TimeStampedEvent.create(0.8F, ReuseableEvents.SHOTGUN_SHOOT_BOTH, Side.CLIENT));
		
		ENDERBLASTER_TWOHAND_SHOOT_4 = new SpecialAttackAnimation(0.05F, 0.30F, 0.30F, 0.45F, 0.8F, WOMColliders.ENDER_LASER, biped.toolR, "biped/skill/enderblaster_twohand_shoot_4", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.8F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(1.4f)))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(4.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.80F))
				.addEvents(TimeStampedEvent.create(0.35F, (entitypatch, self, params) -> {
					if (entitypatch instanceof PlayerPatch) {
						entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.LASER_BLAST.get(), SoundSource.PLAYERS, 0.7F, 1F);
					}
					OpenMatrix4f transformMatrixR = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.toolR);
					OpenMatrix4f transformMatrixR2 = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.toolR);
					transformMatrixR.translate(new Vec3f(0,-0.6F,-0.3F));
					transformMatrixR2.translate(new Vec3f(0.0f,-0.8F,-0.3F));
					OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0));
					OpenMatrix4f.mul(CORRECTION,transformMatrixR,transformMatrixR);
					OpenMatrix4f.mul(CORRECTION,transformMatrixR2,transformMatrixR2);
					
					int n = 40; // set the number of particles to emit
					double r = 0.2; // set the radius of the disk to 1
					double t = 0.01; // set the thickness of the disk to 0.1
					
					for (int i = 0; i < n; i++) {
					    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
					    double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness

					    // calculate the emission direction in Cartesian coordinates using the polar coordinates
					    double x = r * Math.cos(phi) * Math.cos(theta);
					    double y = r * Math.cos(phi) * Math.sin(theta);
					    double z = r * Math.sin(phi);
					    
					 // create a Vector3f object to represent the emission direction
					    Vec3f direction = new Vec3f((float)x, (float)y, (float)z);

					    // rotate the direction vector to align with the forward vector
					    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO), new Vec3f(0, 1, 0));
					    rotation.rotate((float) ((transformMatrixR.m11+0.07f)*1.5f), new Vec3f(1, 0, 0));
					    OpenMatrix4f.transform3v(rotation, direction, direction);
					    
					    // emit the particle in the calculated direction, with some random velocity added
					    entitypatch.getOriginal().level().addParticle(ParticleTypes.DRAGON_BREATH,
					        (transformMatrixR.m30 + entitypatch.getOriginal().getX()),
					        (transformMatrixR.m31 + entitypatch.getOriginal().getY()),
					        (transformMatrixR.m32 + entitypatch.getOriginal().getZ()),
					        (float)((transformMatrixR2.m30 - transformMatrixR.m30) + direction.x),
					        (float)((transformMatrixR2.m31 - transformMatrixR.m31) + direction.y),
					        (float)((transformMatrixR2.m32 - transformMatrixR.m32) + direction.z));
					}
					
					HitResult ray = entitypatch.getOriginal().pick(20.D, 0.7F, false);

					entitypatch.getOriginal().level().addParticle(EpicFightParticles.LASER.get(),
							(transformMatrixR.m30 + entitypatch.getOriginal().getX()),
							(transformMatrixR.m31 + entitypatch.getOriginal().getY()),
							(transformMatrixR.m32 + entitypatch.getOriginal().getZ()),
							(ray.getLocation().x),
							(ray.getLocation().y),
							(ray.getLocation().z));
					
					
					
					OpenMatrix4f transformMatrixL = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.toolL);
					OpenMatrix4f transformMatrixL2 = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.toolL);
					transformMatrixL.translate(new Vec3f(0,-0.6F,-0.3F));
					transformMatrixL2.translate(new Vec3f(0.0f,-0.8F,-0.3F));
					OpenMatrix4f CORRECTION2 = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0));
					OpenMatrix4f.mul(CORRECTION2,transformMatrixL,transformMatrixL);
					OpenMatrix4f.mul(CORRECTION2,transformMatrixL2,transformMatrixL2);
					
					for (int i = 0; i < n; i++) {
					    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
					    double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness

					    // calculate the emission direction in Cartesian coordinates using the polar coordinates
					    double x = r * Math.cos(phi) * Math.cos(theta);
					    double y = r * Math.cos(phi) * Math.sin(theta);
					    double z = r * Math.sin(phi);
					    
					 // create a Vector3f object to represent the emission direction
					    Vec3f direction = new Vec3f((float)x, (float)y, (float)z);

					    // rotate the direction vector to align with the forward vector
					    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO), new Vec3f(0, 1, 0));
					    rotation.rotate((float) ((transformMatrixL.m11+0.07f)*1.5f), new Vec3f(1, 0, 0));
					    OpenMatrix4f.transform3v(rotation, direction, direction);
					    
					    // emit the particle in the calculated direction, with some random velocity added
					    entitypatch.getOriginal().level().addParticle(ParticleTypes.DRAGON_BREATH,
					        (transformMatrixL.m30 + entitypatch.getOriginal().getX()),
					        (transformMatrixL.m31 + entitypatch.getOriginal().getY()),
					        (transformMatrixL.m32 + entitypatch.getOriginal().getZ()),
					        (float)((transformMatrixL2.m30 - transformMatrixL.m30) + direction.x),
					        (float)((transformMatrixL2.m31 - transformMatrixL.m31) + direction.y),
					        (float)((transformMatrixL2.m32 - transformMatrixL.m32) + direction.z));
					}

					entitypatch.getOriginal().level().addParticle(EpicFightParticles.LASER.get(),
							(transformMatrixL.m30 + entitypatch.getOriginal().getX()),
							(transformMatrixL.m31 + entitypatch.getOriginal().getY()),
							(transformMatrixL.m32 + entitypatch.getOriginal().getZ()),
							(ray.getLocation().x),
							(ray.getLocation().y),
							(ray.getLocation().z));
				}, Side.CLIENT));
		
		ENDERBLASTER_TWOHAND_EVADE_LEFT = new EnderblasterShootAttackAnimation(0.01F, "biped/skill/enderblaster_twohand_evade_left", biped,
				new Phase(0.0F, 0.25F, 0.30F, 0.35F, 0.35F, biped.toolR, WOMColliders.ENDER_BULLET_DASH),
				new Phase(0.35F, 0.40F, 0.44F, 0.45F, 0.45F, InteractionHand.OFF_HAND, biped.toolL, WOMColliders.ENDER_BULLET_DASH),
				new Phase(0.45F, 0.50F, 0.55F, 0.55F, Float.MAX_VALUE, biped.toolR, WOMColliders.ENDER_BULLET_DASH),
				new Phase(0.55F, 0.70F, 0.75F, 0.75F, 1.30F, InteractionHand.OFF_HAND, biped.toolL, WOMColliders.ENDER_BULLET_DASH))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER,ValueModifier.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),1)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER,ValueModifier.multiplier(2.0F),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),1)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),2)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER,ValueModifier.multiplier(2.0F),2)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),2)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,2)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,2)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,2)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),2)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),3)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER,ValueModifier.multiplier(2.0F),3)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),3)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,3)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,3)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,3)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),3)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.8F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(
					TimeStampedEvent.create(0.30F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.45F, ReuseableEvents.SHOOT_LEFT, Side.CLIENT),
					TimeStampedEvent.create(0.55F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.75F, ReuseableEvents.SHOOT_LEFT, Side.CLIENT));
		
		ENDERBLASTER_TWOHAND_EVADE_RIGHT = new EnderblasterShootAttackAnimation(0.01F, "biped/skill/enderblaster_twohand_evade_right", biped,
				new Phase(0.0F, 0.25F, 0.30F, 0.35F, 0.35F, biped.toolL, WOMColliders.ENDER_BULLET_DASH),
				new Phase(0.35F, 0.40F, 0.44F, 0.45F, 0.45F, InteractionHand.OFF_HAND, biped.toolR, WOMColliders.ENDER_BULLET_DASH),
				new Phase(0.45F, 0.50F, 0.55F, 0.55F, Float.MAX_VALUE, biped.toolL, WOMColliders.ENDER_BULLET_DASH),
				new Phase(0.55F, 0.70F, 0.75F, 0.75F, 1.30F, InteractionHand.OFF_HAND, biped.toolR, WOMColliders.ENDER_BULLET_DASH))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER,ValueModifier.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),1)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER,ValueModifier.multiplier(2.0F),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),1)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),2)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER,ValueModifier.multiplier(2.0F),2)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),2)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,2)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,2)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,2)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),2)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),3)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER,ValueModifier.multiplier(2.0F),3)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),3)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,3)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,3)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,3)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),3)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.8F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(
					TimeStampedEvent.create(0.30F, ReuseableEvents.SHOOT_LEFT, Side.CLIENT),
					TimeStampedEvent.create(0.45F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.55F, ReuseableEvents.SHOOT_LEFT, Side.CLIENT),
					TimeStampedEvent.create(0.75F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT));
		
		ENDERBLASTER_TWOHAND_AIRSHOOT = new EnderblasterShootAttackAnimation(0.01F, "biped/skill/enderblaster_twohand_airshoot", biped,
				new Phase(0.0F, 0.4F, 0.45F, 0.49F, 0.49F, biped.toolR, WOMColliders.ENDER_SHOOT),
				new Phase(0.49F, 0.5F, 0.55F, 0.59F, 0.59F, InteractionHand.OFF_HAND, biped.toolL, WOMColliders.ENDER_SHOOT),
				new Phase(0.59F, 0.6F, 0.65F, 0.69F, 0.69F, biped.toolR, WOMColliders.ENDER_SHOOT),
				new Phase(0.69F, 0.7F, 0.75F, 0.79F, 0.79F, InteractionHand.OFF_HAND, biped.toolL, WOMColliders.ENDER_SHOOT),
				new Phase(0.79F, 0.8F, 0.85F, 0.89F, 0.89F, biped.toolR, WOMColliders.ENDER_SHOOT),
				new Phase(0.89F, 0.9F, 0.95F, 1.3F, Float.MAX_VALUE, InteractionHand.OFF_HAND, biped.toolL, WOMColliders.ENDER_SHOOT))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),1)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),2)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),2)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,2)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,2)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,2)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),2)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),3)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),3)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,3)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,3)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,3)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),3)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),4)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),4)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,4)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,4)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,4)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),4)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),5)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),5)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,5)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,5)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,5)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),5)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.95F))
				.addEvents(TimeStampedEvent.create(0.45F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.55F, ReuseableEvents.SHOOT_LEFT, Side.CLIENT),
					TimeStampedEvent.create(0.65F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.75F, ReuseableEvents.SHOOT_LEFT, Side.CLIENT),
					TimeStampedEvent.create(0.85F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.95F, ReuseableEvents.SHOOT_LEFT, Side.CLIENT));
		
		ENDERBLASTER_TWOHAND_PISTOLERO = new SpecialAttackAnimation(0.05F, "biped/skill/enderblaster_twohand_shoot_dash", biped,
				new Phase(0.0F, 0.35F, 0.39F, 0.4F, 0.4F, biped.rootJoint, WOMColliders.ENDER_PISTOLERO),
				new Phase(0.4F, 0.45F, 0.49F, 0.5F, 0.5F, InteractionHand.OFF_HAND, biped.rootJoint, WOMColliders.ENDER_PISTOLERO),
				new Phase(0.5F, 0.55F, 0.59F, 0.6F, 0.6F, biped.rootJoint, WOMColliders.ENDER_PISTOLERO),
				new Phase(0.6F, 0.65F, 0.69F, 0.7F, 0.7F, InteractionHand.OFF_HAND, biped.rootJoint, WOMColliders.ENDER_PISTOLERO),
				new Phase(0.7F, 0.75F, 0.79F, 0.8F, 0.8F, biped.rootJoint, WOMColliders.ENDER_PISTOLERO),
				new Phase(0.8F, 0.85F, 0.9F, 1.5F, Float.MAX_VALUE, InteractionHand.OFF_HAND, biped.rootJoint, WOMColliders.ENDER_PISTOLERO))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER,ValueModifier.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER,ValueModifier.multiplier(2.0F),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),1)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),2)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,2)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER,ValueModifier.multiplier(2.0F),2)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),2)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,2)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,2)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,2)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),2)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),3)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,3)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER,ValueModifier.multiplier(2.0F),3)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),3)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,3)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,3)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,3)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),3)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),4)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,4)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER,ValueModifier.multiplier(2.0F),4)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),4)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,4)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,4)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,4)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),4)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER,ValueModifier.multiplier(0.4F),5)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,5)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER,ValueModifier.multiplier(2.0F),5)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)),5)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT,5)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST,5)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT,5)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),5)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 1.5F))
				.addEvents(TimeStampedEvent.create(0.4F, ReuseableEvents.SHOTGUN_SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.5F, ReuseableEvents.SHOTGUN_SHOOT_LEFT, Side.CLIENT),
					TimeStampedEvent.create(0.6F, ReuseableEvents.SHOTGUN_SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.7F, ReuseableEvents.SHOTGUN_SHOOT_LEFT, Side.CLIENT),
					TimeStampedEvent.create(0.8F, ReuseableEvents.SHOTGUN_SHOOT_RIGHT, Side.CLIENT),
					TimeStampedEvent.create(0.9F, ReuseableEvents.SHOTGUN_SHOOT_LEFT, Side.CLIENT));
		
		ENDERBLASTER_TWOHAND_IDLE = new StaticAnimation(0.15f,true, "biped/living/enderblaster_twohand_idle", biped);
		
		ENDERBLASTER_TWOHAND_AIMING = new AimAnimation(true, "biped/skill/enderblaster_twohand_aiming","biped/skill/enderblaster_twohand_aiming_up","biped/skill/enderblaster_twohand_aiming_down","biped/skill/enderblaster_twohand_aiming_layed", biped);
		ENDERBLASTER_TWOHAND_SHOOT_RIGHT = new EnderblasterShootAttackAnimation(0.05F, 0.00F, 0.05F, 0.25F, WOMColliders.ENDER_SHOOT, biped.toolR, "biped/skill/enderblaster_twohand_shoot_right", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.00F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT));
		ENDERBLASTER_TWOHAND_SHOOT_LEFT = new EnderblasterShootAttackAnimation(0.05F, 0.00F, 0.05F, 0.25F,InteractionHand.OFF_HAND, WOMColliders.ENDER_SHOOT, biped.toolL, "biped/skill/enderblaster_twohand_shoot_left", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.00F, ReuseableEvents.SHOOT_LEFT, Side.CLIENT));
		
		
		ENDERBLASTER_TWOHAND_SHOOT_LAYED_RIGHT = new EnderblasterShootAttackAnimation(0.05F, 0.00F, 0.05F, 0.25F, WOMColliders.ENDER_SHOOT, biped.toolR, "biped/skill/enderblaster_twohand_shoot_layed_right", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.00F, ReuseableEvents.SHOOT_RIGHT, Side.CLIENT));
		ENDERBLASTER_TWOHAND_SHOOT_LAYED_LEFT = new EnderblasterShootAttackAnimation(0.05F, 0.00F, 0.05F, 0.25F, InteractionHand.OFF_HAND, WOMColliders.ENDER_SHOOT, biped.toolL, "biped/skill/enderblaster_twohand_shoot_layed_left", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(0.4f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.BLAZE_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.FIREWORK_ROCKET_BLAST)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ENDERBLASTER_BULLET_HIT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.00F, ReuseableEvents.SHOOT_LEFT, Side.CLIENT));
		
		ENDERBLASTER_TWOHAND_RELOAD = new StaticAnimation(0.1f, false, "biped/skill/enderblaster_twohand_reload", biped)
				.addEvents(TimeStampedEvent.create(0.2F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						   TimeStampedEvent.create(0.25F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						   TimeStampedEvent.create(0.3F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						   TimeStampedEvent.create(0.35F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						   TimeStampedEvent.create(0.4F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						   TimeStampedEvent.create(0.5F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
						   TimeStampedEvent.create(0.7F, ReuseableEvents.ENDERBLASTER_RELOAD_BOTH, Side.SERVER));
		
		STAFF_AUTO_1 = new BasicMultipleAttackAnimation(0.15F, "biped/combat/staff_auto_1", biped,
				new Phase(0.0F, 0.1F, 0.19F, 0.2F, 0.2F, biped.toolR, null),
				new Phase(0.2F, 0.25F, 0.34F, 0.35F, 0.35F, biped.toolR, null),
				new Phase(0.35F, 0.4F, 0.49F, 0.50F, 0.50F, biped.toolR, null),
				new Phase(0.5F, 0.55F, 0.7F, 0.7F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.40F))
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.40F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.20F),1)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.40F),2)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,2)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.40F),3)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.20F),3)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,3)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,3)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F);
		
		STAFF_AUTO_2 = new BasicMultipleAttackAnimation(0.15F, "biped/combat/staff_auto_2", biped,
				new Phase(0.0F, 0.15F, 0.25F, 0.35F, 0.35F, biped.toolR, WOMColliders.STAFF_EXTENTION),
				new Phase(0.35F, 0.35F, 0.45F, 0.55F, 0.55F, biped.toolR, WOMColliders.STAFF_EXTENTION),
				new Phase(0.55F, 0.55F, 0.65F, 0.65F, Float.MAX_VALUE, biped.toolR, WOMColliders.STAFF_EXTENTION))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.60F))
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.60F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.20F),1)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.60F),2)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,2)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,2)
				.addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 2)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F);
		
		STAFF_AUTO_3 = new BasicMultipleAttackAnimation(0.15F, "biped/combat/staff_auto_3", biped,
				new Phase(0.0F, 0.15F, 0.45F, 0.50F, 0.50F, biped.toolR, null),
				new Phase(0.50F, 0.55F, 0.85F, 1.5F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.95F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.40F))
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.95F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.80F),1)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, (self, entitypatch, transformSheet) -> {
					LivingEntity attackTarget = entitypatch.getTarget();
					
					if (!self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
						TransformSheet transform = self.getTransfroms().get("Root").copyAll();
						Keyframe[] keyframes = transform.getKeyframes();
						int startFrame = 0;
						int endFrame = transform.getKeyframes().length - 1;
						Vec3f keyLast = keyframes[endFrame].transform().translation();
						Vec3 pos = entitypatch.getOriginal().getEyePosition();
						Vec3 targetpos = attackTarget.position();
						float horizontalDistance = Math.max((float)targetpos.subtract(pos).horizontalDistance() * 1.75f - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
						Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
						float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
						
						for (int i = startFrame; i <= endFrame; i++) {
							Vec3f translation = keyframes[i].transform().translation();
							translation.z *= scale;
						}
						
						transformSheet.readFrom(transform);
					} else {
						transformSheet.readFrom(self.getTransfroms().get("Root"));
					}
				}).addProperty(ActionAnimationProperty.COORD_SET_TICK, (self, entitypatch, transformSheet) -> {
					LivingEntity attackTarget = entitypatch.getTarget();
					
					if (!self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
						TransformSheet transform = self.getTransfroms().get("Root").copyAll();
						Keyframe[] keyframes = transform.getKeyframes();
						int startFrame = 0;
						int endFrame = transform.getKeyframes().length - 1;
						Vec3f keyLast = keyframes[endFrame].transform().translation();
						Vec3 pos = entitypatch.getOriginal().getEyePosition();
						Vec3 targetpos = attackTarget.position();
						float horizontalDistance = Math.max((float)targetpos.subtract(pos).horizontalDistance()*2f - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
						Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
						float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
						
						for (int i = startFrame; i <= endFrame; i++) {
							Vec3f translation = keyframes[i].transform().translation();
							translation.z *= scale;
						}
						
						transformSheet.readFrom(transform);
					} else {
						transformSheet.readFrom(self.getTransfroms().get("Root"));
					}
				});
		
		STAFF_DASH = new BasicMultipleAttackAnimation(0.05F, "biped/combat/staff_dash", biped,
				new Phase(0.0F, 0.15F, 0.19F, 0.2F, 0.2F, biped.toolR, WOMColliders.STAFF_EXTENTION),
				new Phase(0.2F, 0.25F, 0.40F, 0.50F, Float.MAX_VALUE, biped.toolR, WOMColliders.STAFF_EXTENTION))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.50F))
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.50F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,1)
				.addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.3F);
		
		STAFF_KINKONG = new BasicMultipleAttackAnimation(0.05F, "biped/combat/staff_kingkong", biped,
				new Phase(0.0F, 0.35F, 0.45F, 0.95F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.40F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.6F))
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.AIR_BURST)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.NEUTRALIZE_MOBS.get())
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 4.0F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.3F, 0.6F))
				.addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, elapsedTime) -> {
					if (elapsedTime >= 0.6F && elapsedTime < 0.85F) {
						float dpx = (float) entitypatch.getOriginal().getX();
						float dpy = (float) entitypatch.getOriginal().getY();
						float dpz = (float) entitypatch.getOriginal().getZ();
						BlockState block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						
						while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
							dpy--;
							block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						}
						
						float distanceToGround = (float) Math.max(Math.abs(entitypatch.getOriginal().getY() - dpy)-1, 0.0F);
						
						return 1 - (1 / (-distanceToGround - 1.F) + 1.0f);
					}
					
					return 1.0F;
				});
		
		STAFF_CHARYBDIS = new SpecialAttackAnimation(0.1F, "biped/skill/staff_charybdisandscylla", biped,
				new Phase(0.0F, 0.1F, 0.14F, 0.15F, 0.15F, biped.toolR, WOMColliders.STAFF_CHARYBDIS),
				new Phase(0.15F, 0.2F, 0.24F, 0.25F, 0.25F, biped.toolR, WOMColliders.STAFF_CHARYBDIS),
				new Phase(0.25F, 0.3F, 0.34F, 0.35F, 0.35F, biped.toolR, WOMColliders.STAFF_CHARYBDIS),
				new Phase(0.35F, 0.4F, 0.44F, 0.45F, 0.45F, biped.toolR, WOMColliders.STAFF_CHARYBDIS),
				new Phase(0.45F, 0.55F, 0.59F, 0.6F, 0.6F, biped.toolR, WOMColliders.STAFF_CHARYBDIS),
				new Phase(0.6F, 0.65F, 0.69F, 0.7F, 0.7F, biped.toolR, WOMColliders.STAFF_CHARYBDIS),
				new Phase(0.7F, 0.75F, 0.79F, 0.8F, 0.8F, biped.toolR, WOMColliders.STAFF_CHARYBDIS),
				new Phase(0.8F, 0.85F, 0.89F, 0.9F, Float.MAX_VALUE, biped.toolR, WOMColliders.STAFF_CHARYBDIS))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35F),1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35F),2)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35F),3)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35F),4)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35F),5)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35F),6)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35F),7)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.8F),7)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,2)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,3)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,4)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,5)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,6)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,7)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,1)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,2)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,3)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,4)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,5)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,6)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT,7)
				.addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 2)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.65F);
		
		STAFF_IDLE = new StaticAnimation(0.1f,true, "biped/living/staff_idle", biped);
		STAFF_RUN = new MovementAnimation(0.1f,true, "biped/living/staff_run", biped);
		
		ANTITHEUS_AGRESSION = new BasicMultipleAttackAnimation(0.05F, "biped/combat/antitheus_agression", biped,
				new Phase(0.0F, 0.20F, 0.35F, 0.59F, 0.59F, biped.toolR, WOMColliders.ANTITHEUS_AGRESSION),
				new Phase(0.59F, 0.60F, 0.65F, 0.85F, Float.MAX_VALUE, biped.rootJoint, WOMColliders.ANTITHEUS_AGRESSION_REAP))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0f))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT_DOWN)		
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F),1)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.TARGET_LOST_HEALTH.create(0.2f)),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5f),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL, 1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.EVISCERATE.get(),1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT_UP,1)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE),1)
				.addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.9F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.05F, ReuseableEvents.ANTITHEUS_AIRBURST, Side.CLIENT),
					TimeStampedEvent.create(0.15F, ReuseableEvents.ANTITHEUS_WEAPON_TRAIL_ON, Side.SERVER),
					TimeStampedEvent.create(0.45F, ReuseableEvents.ANTITHEUS_GROUND_SLAM, Side.CLIENT),
					TimeStampedEvent.create(0.8F, ReuseableEvents.ANTITHEUS_WEAPON_TRAIL_OFF, Side.SERVER));
		
		ANTITHEUS_GUILLOTINE = new BasicMultipleAttackAnimation(0.05F, "biped/combat/antitheus_guillotine", biped,
				new Phase(0.0F, 0.6F, 0.75F, 0.8F, 0.8F, biped.rootJoint, WOMColliders.ANTITHEUS_GUILLOTINE),
				new Phase(0.8F, 0.9F, 1.0F, 1.1F, Float.MAX_VALUE, biped.rootJoint, WOMColliders.ANTITHEUS_GUILLOTINE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.40F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.60F),1)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.6F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT_REVERSE)				
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT_REVERSE,1)				
				.addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 2)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.9F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.3F))
				.addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, elapsedTime) -> {
					if (elapsedTime >= 0.4F && elapsedTime < 0.55F) {
						float dpx = (float) entitypatch.getOriginal().getX();
						float dpy = (float) entitypatch.getOriginal().getY();
						float dpz = (float) entitypatch.getOriginal().getZ();
						BlockState block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						
						while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
							dpy--;
							block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						}
						
						float distanceToGround = (float) Math.max(Math.abs(entitypatch.getOriginal().getY() - dpy)-1, 0.0F);
						
						return 1 - (1 / (-distanceToGround - 1.F) + 1.0f);
					}
					
					return 1.0F;
				})
				.addEvents(TimeStampedEvent.create(0.05F, (entitypatch, self, params) -> {
					entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.WHOOSH_BIG.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
				}, Side.CLIENT),
					TimeStampedEvent.create(0.5F, ReuseableEvents.ANTITHEUS_WEAPON_TRAIL_ON, Side.SERVER),
					TimeStampedEvent.create(1.0F, ReuseableEvents.ANTITHEUS_WEAPON_TRAIL_OFF, Side.SERVER));

		ANTITHEUS_AUTO_1 = new BasicMultipleAttackAnimation(0.15F, "biped/combat/antitheus_auto_1", biped,
				new Phase(0.0F, 0.35F, 0.55F, 0.6F, 0.6F, biped.toolR, null),
				new Phase(0.6F, 0.70F, 0.9F, 0.9F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.55F))
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT)				
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.75F),1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT,1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD )
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.9F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addEvents(TimeStampedEvent.create(0.05F, (entitypatch, self, params) -> {
					entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.WHOOSH_BIG.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
				}, Side.CLIENT),
					TimeStampedEvent.create(0.25F, ReuseableEvents.ANTITHEUS_WEAPON_TRAIL_ON, Side.SERVER),
					TimeStampedEvent.create(0.9F, ReuseableEvents.ANTITHEUS_WEAPON_TRAIL_OFF, Side.SERVER));
		
		ANTITHEUS_AUTO_2 = new BasicMultipleAttackAnimation(0.15F, 0.15F, 0.45F, 0.45F,null, biped.toolR, "biped/combat/antitheus_auto_2", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.5F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT_REVERSE)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.7F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addEvents(TimeStampedEvent.create(0.1F, ReuseableEvents.ANTITHEUS_WEAPON_TRAIL_ON, Side.SERVER),
						TimeStampedEvent.create(0.45F, ReuseableEvents.ANTITHEUS_WEAPON_TRAIL_OFF, Side.SERVER));
		
		ANTITHEUS_AUTO_3 = new BasicMultipleAttackAnimation(0.15F, "biped/combat/antitheus_auto_3", biped,
				new Phase(0.0F, 0.15F, 0.35F, 0.5F, 0.5F, biped.toolR, null),
				new Phase(0.5F, 0.55F, 0.70F, 0.75F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT_REVERSE)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.5F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD )
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.8F),1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT_REVERSE,1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.5F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.9F)
				.addEvents(TimeStampedEvent.create(0.10F, ReuseableEvents.ANTITHEUS_WEAPON_TRAIL_ON, Side.SERVER),
					TimeStampedEvent.create(0.75F, ReuseableEvents.ANTITHEUS_WEAPON_TRAIL_OFF, Side.SERVER));
		
		ANTITHEUS_AUTO_4 = new BasicMultipleAttackAnimation(0.15F, 0.50F, 0.75F, 0.9F, null, biped.toolR, "biped/combat/antitheus_auto_4", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.6F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT_REVERSE)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.7F)
				.addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 2)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addEvents(TimeStampedEvent.create(0.55F, ReuseableEvents.ANTITHEUS_WEAPON_TRAIL_ON, Side.SERVER),
					TimeStampedEvent.create(0.75F, ReuseableEvents.ANTITHEUS_WEAPON_TRAIL_OFF, Side.SERVER));
		
		ANTITHEUS_IDLE = new StaticAnimation(0.2f, true, "biped/living/antitheus_idle", biped)
				.addEvents(TimeStampedEvent.create(6.00F, (entitypatch, self, params) -> {
						((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setData(DemonMarkPassiveSkill.IDLE, true);
						entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 0.2F, 0.5F);
				}, Side.CLIENT),
					TimeStampedEvent.create(8.60F, (entitypatch, self, params) -> {
						entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.7F, 0.5F);
						((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setData(DemonMarkPassiveSkill.IDLE, false);
						OpenMatrix4f transformMatrix;
						transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(0), Armatures.BIPED.toolL);
						transformMatrix.translate(new Vec3f(0,0.0F,0));
						OpenMatrix4f.mul(new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO + 180F), new Vec3f(0, 1, 0)),transformMatrix,transformMatrix);
						int n = 70; // set the number of particles to emit
						double r = 0.1; // set the radius of the hemisphere to 1

						for (int i = 0; i < n; i++) {
						    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
						    double phi = Math.acos(2 * new Random().nextDouble() - 1); // generate a random polar angle in the upper hemisphere

						    // calculate the emission direction in Cartesian coordinates using the polar coordinates
						    double x = r * Math.sin(phi) * Math.cos(theta);
						    double y = r * Math.sin(phi) * Math.sin(theta);
						    double z = r * Math.cos(phi);

						    // emit the particle in the calculated direction, with some random velocity added
						    entitypatch.getOriginal().level().addParticle(ParticleTypes.SMOKE,
						        (transformMatrix.m30 + entitypatch.getOriginal().getX()),
						        (transformMatrix.m31 + entitypatch.getOriginal().getY()),
						        (transformMatrix.m32 + entitypatch.getOriginal().getZ()),
						        (float)(x),
						        (float)(y),
						        (float)(z));
						}
				}, Side.CLIENT));
		
		ANTITHEUS_RUN = new MovementAnimation(0.2f,true, "biped/living/antitheus_run", biped);
		ANTITHEUS_WALK = new MovementAnimation(0.2f,true, "biped/living/antitheus_walk", biped);
		ANTITHEUS_AIMING = new AimAnimation(true, "biped/skill/antitheus_aiming","biped/skill/antitheus_aiming_up","biped/skill/antitheus_aiming_down","biped/skill/antitheus_aiming", biped);
		ANTITHEUS_SHOOT = new AntitheusShootAttackAnimation(0.00F, 0.00F, 0.05F, 0.5F, WOMColliders.ANTITHEUS_SHOOT, biped.toolL, "biped/skill/antitheus_shoot", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.2F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(1.0f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.WITHER_SHOOT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.WITHER_BREAK_BLOCK)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.7F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false);
		
		ANTITHEUS_PULL = new SpecialAttackAnimation(0.00F, 0.00F, 0.05F, 0.5F, WOMColliders.NONE, biped.toolL, "biped/skill/antitheus_pull", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.0F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(1.0f)))
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.WITHER_SKELETON_DEATH)
				.addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.WITHER_BREAK_BLOCK)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.7F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false);
		
		ANTITHEUS_ASCENSION = new SpecialAttackAnimation(0.1f, "biped/skill/antitheus_ascension", biped,
				new Phase(0.0f, 0.5f, 0.6f, 0.65f, 0.65f, biped.rootJoint, WOMColliders.PLUNDER_PERDITION),
				new Phase(0.65f, 1.75f, 2.05f, 2.85f, Float.MAX_VALUE, biped.rootJoint, WOMColliders.PLUNDER_PERDITION))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(4.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20))
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)	
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F),1)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20),1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT,1)	
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.7F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addEvents(TimeStampedEvent.create(0.5F, (entitypatch, self, params) -> {
					
					entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 1.0F, 0.5F);
				}, Side.CLIENT),TimeStampedEvent.create(1.75F, (entitypatch, self, params) -> {
					((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(DemonMarkPassiveSkill.PARTICLE, true, (ServerPlayer)entitypatch.getOriginal());
					((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(DemonicAscensionSkill.ACTIVE, true, (ServerPlayer)entitypatch.getOriginal());
					((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(DemonicAscensionSkill.ASCENDING, true, (ServerPlayer)entitypatch.getOriginal());
					((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(DemonicAscensionSkill.SUPERARMOR, false, (ServerPlayer)entitypatch.getOriginal());
					
					entitypatch.getOriginal().level().playSound(null, entitypatch.getOriginal(), SoundEvents.WITHER_BREAK_BLOCK, SoundSource.PLAYERS, 1.0F, 0.5F);
					entitypatch.getOriginal().level().playSound(null, entitypatch.getOriginal(), SoundEvents.WITHER_AMBIENT, SoundSource.PLAYERS, 1.0F, 0.5F);
				}, Side.SERVER),TimeStampedEvent.create(1.75F, (entitypatch, self, params) -> {
					
					float target_x = (float) entitypatch.getOriginal().getX();
					float target_y = (float) entitypatch.getOriginal().getY()+0.2f;
					float target_z = (float) entitypatch.getOriginal().getZ();
					
					int n = 80; // set the number of particles to emit
					double r = 0.6; // set the radius of the disk to 1
					double t = 0.05; // set the thickness of the disk to 0.1
					
					
					for (int i = 0; i < n*2; i++) {
						double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
						double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness
						
						// calculate the emission direction in Cartesian coordinates using the polar coordinates
						double x = r * Math.cos(phi) * Math.cos(theta);
						double y = r * Math.cos(phi) * Math.sin(theta);
						double z = r * Math.sin(phi);
						
						// create a Vector3f object to represent the emission direction
						Vec3f direction = new Vec3f((float)x, (float)y, (float)z);
						
						// rotate the direction vector to align with the forward vector
						OpenMatrix4f rotation = new OpenMatrix4f().rotate((float) Math.toRadians(90), new Vec3f(1, 0, 0));
						OpenMatrix4f.transform3v(rotation, direction, direction);
						
						// emit the particle in the calculated direction, with some random velocity added
						entitypatch.getOriginal().level().addParticle(ParticleTypes.LARGE_SMOKE,
								(target_x),
								(target_y),
								(target_z),
								(float)(direction.x),
								(float)(direction.y),
								(float)(direction.z));
					}
					
					for (int i = 0; i < n; i++) {
						double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
						double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness
						
						// calculate the emission direction in Cartesian coordinates using the polar coordinates
						double x = r * Math.cos(phi) * Math.cos(theta);
						double y = r * Math.cos(phi) * Math.sin(theta);
						double z = r * Math.sin(phi);
						
						// create a Vector3f object to represent the emission direction
						Vec3f direction = new Vec3f(
								(float)x *(new Random().nextFloat()+0.5f)*0.8f,
								(float)y *(new Random().nextFloat()+0.5f)*0.8f,
								(float)z *(new Random().nextFloat()+0.5f)*0.8f);
						
						// rotate the direction vector to align with the forward vector
						OpenMatrix4f rotation = new OpenMatrix4f().rotate((float) Math.toRadians(90), new Vec3f(1, 0, 0));
						OpenMatrix4f.transform3v(rotation, direction, direction);
						
						// emit the particle in the calculated direction, with some random velocity added
						entitypatch.getOriginal().level().addParticle(ParticleTypes.LARGE_SMOKE,
								(target_x),
								(target_y),
								(target_z),
								(float)(direction.x),
								(float)(direction.y),
								(float)(direction.z));
					}
					
					for (int i = 0; i < 60; i++) {
						entitypatch.getOriginal().level().addParticle(ParticleTypes.LARGE_SMOKE,
								target_x + ((new Random().nextFloat() - 0.5F)*1.2f),
								target_y + 0.2F,
								target_z + ((new Random().nextFloat() - 0.5F)*1.2f),
								0,
								((new Random().nextFloat()) * 1.5F),
								0);
					}
					Level level = entitypatch.getOriginal().level();
					
					Vec3 floorPos = ReuseableEvents.getfloor(entitypatch, self,new Vec3f(0,0.0F,0.0F),Armatures.BIPED.rootJoint);
					Vec3 weaponEdge = new Vec3(floorPos.x ,floorPos.y, floorPos.z);
					
					LevelUtil.circleSlamFracture(entitypatch.getOriginal(), level, weaponEdge, 4.00f,true,true);
				}, Side.CLIENT));
		
		ANTITHEUS_LAPSE = new SpecialAttackAnimation(0.1f, "biped/skill/antitheus_lapse", biped,
				new Phase(0.0f, 0.65f, 0.75f, 0.80f, 0.80f, biped.rootJoint, WOMColliders.PLUNDER_PERDITION),
				new Phase(0.80f, 1.30f, 1.4f, 1.45f, 1.45f, biped.rootJoint, WOMColliders.PLUNDER_PERDITION),
				new Phase(1.45f, 1.75f, 1.85f, 2.30f, Float.MAX_VALUE, biped.rootJoint, WOMColliders.PLUNDER_PERDITION))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(4.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20))
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.WITHER_HURT)	
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(4.0F),1)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20),1)
				.addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.WITHER_HURT,1)	
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)	
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F),2)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20),2)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT,2)	
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,2)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.7F)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
				.addEvents(TimeStampedEvent.create(0.2F, (entitypatch, self, params) -> {
					entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 1.0F, 0.5F);
				}, Side.CLIENT),TimeStampedEvent.create(1.75F, (entitypatch, self, params) -> {
					((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(DemonMarkPassiveSkill.LAPSE, false, (ServerPlayer)entitypatch.getOriginal());
					((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(DemonMarkPassiveSkill.PARTICLE, false, (ServerPlayer)entitypatch.getOriginal());
					((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(DemonicAscensionSkill.ACTIVE, false, (ServerPlayer)entitypatch.getOriginal());
					entitypatch.getOriginal().level().playSound(null, entitypatch.getOriginal(), SoundEvents.WITHER_BREAK_BLOCK, SoundSource.PLAYERS, 1.0F, 0.5F);
					entitypatch.getOriginal().level().playSound(null, entitypatch.getOriginal(), SoundEvents.WITHER_DEATH, SoundSource.PLAYERS, 1.0F, 2.0F);
				}, Side.SERVER),TimeStampedEvent.create(2.25F, (entitypatch, self, params) -> {
					((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(DemonicAscensionSkill.SUPERARMOR, false, (ServerPlayer)entitypatch.getOriginal());
				}, Side.SERVER),TimeStampedEvent.create(1.75F, (entitypatch, self, params) -> {
					
					float target_x = (float) entitypatch.getOriginal().getX();
					float target_y = (float) entitypatch.getOriginal().getY()+0.2f;
					float target_z = (float) entitypatch.getOriginal().getZ();
					
					int n = 80; // set the number of particles to emit
					double r = 0.6; // set the radius of the disk to 1
					double t = 0.05; // set the thickness of the disk to 0.1
					
					
					for (int i = 0; i < n*2; i++) {
						double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
						double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness
						
						// calculate the emission direction in Cartesian coordinates using the polar coordinates
						double x = r * Math.cos(phi) * Math.cos(theta);
						double y = r * Math.cos(phi) * Math.sin(theta);
						double z = r * Math.sin(phi);
						
						// create a Vector3f object to represent the emission direction
						Vec3f direction = new Vec3f((float)x, (float)y, (float)z);
						
						// rotate the direction vector to align with the forward vector
						OpenMatrix4f rotation = new OpenMatrix4f().rotate((float) Math.toRadians(90), new Vec3f(1, 0, 0));
						OpenMatrix4f.transform3v(rotation, direction, direction);
						
						// emit the particle in the calculated direction, with some random velocity added
						entitypatch.getOriginal().level().addParticle(ParticleTypes.LARGE_SMOKE,
								(target_x),
								(target_y),
								(target_z),
								(float)(direction.x),
								(float)(direction.y),
								(float)(direction.z));
					}
					
					for (int i = 0; i < n; i++) {
						double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
						double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness
						
						// calculate the emission direction in Cartesian coordinates using the polar coordinates
						double x = r * Math.cos(phi) * Math.cos(theta);
						double y = r * Math.cos(phi) * Math.sin(theta);
						double z = r * Math.sin(phi);
						
						// create a Vector3f object to represent the emission direction
						Vec3f direction = new Vec3f(
								(float)x *(new Random().nextFloat()+0.5f)*0.8f,
								(float)y *(new Random().nextFloat()+0.5f)*0.8f,
								(float)z *(new Random().nextFloat()+0.5f)*0.8f);
						
						// rotate the direction vector to align with the forward vector
						OpenMatrix4f rotation = new OpenMatrix4f().rotate((float) Math.toRadians(90), new Vec3f(1, 0, 0));
						OpenMatrix4f.transform3v(rotation, direction, direction);
						
						// emit the particle in the calculated direction, with some random velocity added
						entitypatch.getOriginal().level().addParticle(ParticleTypes.LARGE_SMOKE,
								(target_x),
								(target_y),
								(target_z),
								(float)(direction.x),
								(float)(direction.y),
								(float)(direction.z));
					}
					Level level = entitypatch.getOriginal().level();
					
					Vec3 floorPos = ReuseableEvents.getfloor(entitypatch, self,new Vec3f(0,0.0F,0.0F),Armatures.BIPED.rootJoint);
					Vec3 weaponEdge = new Vec3(floorPos.x ,floorPos.y, floorPos.z);
					
					LevelUtil.circleSlamFracture(entitypatch.getOriginal(), level, weaponEdge, 4.00f,true,true);
				}, Side.CLIENT));
		
		ANTITHEUS_ASCENDED_AUTO_1 = new BasicMultipleAttackAnimation(0.05F, 0.3F, 0.4F, 0.4F, WOMColliders.ANTITHEUS_ASCENDED_PUNCHES, biped.rootJoint, "biped/skill/antitheus_ascended_auto_1", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get())	
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.9F)
				.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, (self, entitypatch, transformSheet) -> {
					LivingEntity attackTarget = entitypatch.getTarget();
					
					if (!self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
						TransformSheet transform = self.getTransfroms().get("Root").copyAll();
						Keyframe[] keyframes = transform.getKeyframes();
						int startFrame = 0;
						int endFrame = transform.getKeyframes().length - 1;
						Vec3f keyLast = keyframes[endFrame].transform().translation();
						Vec3 pos = entitypatch.getOriginal().getEyePosition();
						Vec3 targetpos = attackTarget.position();
						float horizontalDistance = Math.max((float)targetpos.subtract(pos).horizontalDistance() - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
						Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
						float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
						
						for (int i = startFrame; i <= endFrame; i++) {
							Vec3f translation = keyframes[i].transform().translation();
							translation.z *= scale;
						}
						
						transformSheet.readFrom(transform);
					} else {
						transformSheet.readFrom(self.getTransfroms().get("Root"));
					}
				}).addProperty(ActionAnimationProperty.COORD_SET_TICK, (self, entitypatch, transformSheet) -> {
					LivingEntity attackTarget = entitypatch.getTarget();
					if (entitypatch instanceof PlayerPatch<?>) {
						if (!self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
							TransformSheet transform = self.getTransfroms().get("Root").copyAll();
							Keyframe[] keyframes = transform.getKeyframes();
							int startFrame = 0;
							int endFrame = transform.getKeyframes().length - 1;
							Vec3f keyLast = keyframes[endFrame].transform().translation();
							Vec3 pos = entitypatch.getOriginal().getEyePosition();
							Vec3 targetpos = attackTarget.position();
							float horizontalDistance = (float) Math.max((float)targetpos.subtract(pos).horizontalDistance()*1.5f - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
							Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
							float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
							
							for (int i = startFrame; i <= endFrame; i++) {
								Vec3f translation = keyframes[i].transform().translation();
								translation.z *= scale;
							}
							
							transformSheet.readFrom(transform);
						} else {
							transformSheet.readFrom(self.getTransfroms().get("Root"));
						}
					}
				})
				.addEvents(TimeStampedEvent.create(0.05F, (entitypatch, self, params) -> {
					entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.WHOOSH_BIG.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
				}, Side.CLIENT));
		
		ANTITHEUS_ASCENDED_AUTO_2 = new BasicMultipleAttackAnimation(0.05F, "biped/skill/antitheus_ascended_auto_2", biped,
				new Phase(0.0F, 0.3F, 0.4F, 0.5F, 0.5F, biped.rootJoint, WOMColliders.ANTITHEUS_ASCENDED_PUNCHES),
				new Phase(0.5F, 0.6F, 0.7F, 0.7F, Float.MAX_VALUE, biped.rootJoint, WOMColliders.ANTITHEUS_ASCENDED_PUNCHES))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F),1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)		
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT,1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get())	
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get(),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.9F)
				.addEvents(TimeStampedEvent.create(0.05F, (entitypatch, self, params) -> {
					entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.WHOOSH_BIG.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
				}, Side.CLIENT));
		
		ANTITHEUS_ASCENDED_AUTO_3 = new BasicMultipleAttackAnimation(0.05F, "biped/skill/antitheus_ascended_auto_3", biped,
				new Phase(0.0F, 0.2F, 0.3F, 0.35F, 0.35F, biped.rootJoint, WOMColliders.ANTITHEUS_ASCENDED_PUNCHES),
				new Phase(0.35F, 0.4F, 0.5F, 0.55F, 0.55F, biped.rootJoint, WOMColliders.ANTITHEUS_ASCENDED_PUNCHES),
				new Phase(0.55F, 0.7F, 0.80F, 0.85F, Float.MAX_VALUE, biped.rootJoint, WOMColliders.ANTITHEUS_ASCENDED_PUNCHES))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F),1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F),2)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)		
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT,1)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT,2)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get())	
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get(),1)	
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get(),2)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,2)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.9F)
				.addEvents(TimeStampedEvent.create(0.05F, (entitypatch, self, params) -> {
					entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.WHOOSH_BIG.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
				}, Side.CLIENT));
		
		ANTITHEUS_ASCENDED_DEATHFALL = new BasicMultipleAttackAnimation(0.05F, 0.5F, 0.55F, 0.75F, WOMColliders.ANTITHEUS_ASCENDED_DEATHFALL, biped.rootJoint, "biped/skill/antitheus_ascended_deathfall", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)		
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get())		
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.9F)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.75F))
				.addEvents(TimeStampedEvent.create(0.05F, (entitypatch, self, params) -> {
					entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.7F, 0.7F);
					entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.WHOOSH_BIG.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
				}, Side.CLIENT),
				TimeStampedEvent.create(0.35F, (entitypatch, self, params) -> {
					((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(DemonicAscensionSkill.SUPERARMOR, true, (ServerPlayer)entitypatch.getOriginal());
					entitypatch.getOriginal().resetFallDistance();
				}, Side.SERVER),
				TimeStampedEvent.create(0.45F, (entitypatch, self, params) -> {
					entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.7F, 0.7F);
					entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.WHOOSH_BIG.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
					
					float floor_x = (float) entitypatch.getOriginal().getX();
					float floor_y = (float) entitypatch.getOriginal().getY();
					float floor_z = (float) entitypatch.getOriginal().getZ();
					
					while (entitypatch.getOriginal().level().isEmptyBlock(new BlockPos.MutableBlockPos(floor_x,floor_y,floor_z))) {
						floor_y--;
					}
					for (int i = 0; i < 24; i++) {
						entitypatch.getOriginal().level().addParticle(ParticleTypes.LARGE_SMOKE,
							entitypatch.getOriginal().getX() + ((new Random().nextFloat() - 0.5F)),
							entitypatch.getOriginal().getY() + 2.2F,
							entitypatch.getOriginal().getZ() + ((new Random().nextFloat() - 0.5F)),
							((new Random().nextFloat() - 0.5F) * 0.05F),
							(-(new Random().nextFloat() * ((entitypatch.getOriginal().getY() - floor_y) * 0.4f))),
							((new Random().nextFloat() - 0.5F) * 0.05F));
					}
				}, Side.CLIENT),
				TimeStampedEvent.create(0.5F, (entitypatch, self, params) -> {
					entitypatch.getOriginal().resetFallDistance();
				}, Side.SERVER),
				TimeStampedEvent.create(0.55F, (entitypatch, self, params) -> {
					if (entitypatch instanceof PlayerPatch) {
						entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 0.7F, 0.5F);
						entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.BLUNT_HIT_HARD.get(), SoundSource.PLAYERS, 0.7F, 0.7F);
					}//System.out.println((transformMatrix.m11+0.07f)*1.2f);
					
					float floor_x = (float) entitypatch.getOriginal().getX();
					float floor_y = (float) entitypatch.getOriginal().getY();
					float floor_z = (float) entitypatch.getOriginal().getZ();
					
					while (entitypatch.getOriginal().level().isEmptyBlock(new BlockPos.MutableBlockPos(floor_x,floor_y,floor_z))) {
						floor_y--;
					}
					
					Vec3 position = new Vec3(0,(floor_y-2) - entitypatch.getOriginal().getY(),0);
					entitypatch.getOriginal().move(MoverType.SELF,position);
					
					int n = 80; // set the number of particles to emit
					double r = 0.6; // set the radius of the disk to 1
					double t = 0.01; // set the thickness of the disk to 0.1
					
					for (int i = 0; i < n; i++) {
					    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
					    double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness

					    // calculate the emission direction in Cartesian coordinates using the polar coordinates
					    double x = r * Math.cos(phi) * Math.cos(theta);
					    double y = r * Math.cos(phi) * Math.sin(theta);
					    double z = r * Math.sin(phi);
					    
					 // create a Vector3f object to represent the emission direction
					    float randomVelocity =  new Random().nextFloat() + 0.4f;
					    Vec3f direction = new Vec3f((float)x * randomVelocity, (float)y * randomVelocity, (float)z * randomVelocity);

					 // rotate the direction vector to align with the forward vector
					    OpenMatrix4f rotation = new OpenMatrix4f().rotate((float) Math.toRadians(90), new Vec3f(1, 0, 0));
					    OpenMatrix4f.transform3v(rotation, direction, direction);
					    
					    // emit the particle in the calculated direction, with some random velocity added
					    entitypatch.getOriginal().level().addParticle(ParticleTypes.LARGE_SMOKE,
					        (entitypatch.getOriginal().getX()) + direction.x,
					        ((int) entitypatch.getOriginal().getY())+ direction.y + 0.02f,
					        (entitypatch.getOriginal().getZ()) + direction.z,
					        (float)(direction.x),
					        (float)(direction.y),
					        (float)(direction.z));
					}
				}, Side.CLIENT),
				TimeStampedEvent.create(0.55F, (entitypatch, self, params) -> {
					((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(DemonicAscensionSkill.SUPERARMOR, false, (ServerPlayer)entitypatch.getOriginal());
					entitypatch.getOriginal().resetFallDistance();
				}, Side.SERVER));
		
		ANTITHEUS_ASCENDED_BLINK = new BasicMultipleAttackAnimation(0.05F, 0.3F, 0.4F, 0.4F, WOMColliders.ANTITHEUS_ASCENDED_BLINK, biped.rootJoint, "biped/skill/antitheus_ascended_blink", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.4F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT_REVERSE)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP.get())	
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_HIT.get())
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.9F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addEvents(TimeStampedEvent.create(0.05F, (entitypatch, self, params) -> {
							entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.7F, 0.7F);
							entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.WHOOSH_BIG.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
						}, Side.CLIENT),
					TimeStampedEvent.create(0.3F, ReuseableEvents.ANTITHEUS_WEAPON_TRAIL_ON, Side.SERVER),
					TimeStampedEvent.create(0.65F, ReuseableEvents.ANTITHEUS_WEAPON_TRAIL_OFF, Side.SERVER));
		
		ANTITHEUS_ASCENDED_BLACKHOLE = new BasicMultipleAttackAnimation(0.05F, 1.45F, 1.5F, 1.7F, WOMColliders.PLUNDER_PERDITION, biped.rootJoint, "biped/skill/antitheus_ascended_blackhole", biped)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(30.0F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.5F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH.get())	
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_HIT.get())
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 1.70F))
				.addEvents(TimeStampedEvent.create(0.00F, (entitypatch, self, params) -> {
							entitypatch.getOriginal().level().playSound(null, entitypatch.getOriginal(), WOMSounds.ANTITHEUS_BLACKKHOLE_CHARGEUP.get(), SoundSource.PLAYERS, 2.0F, 1.0F);
							if (entitypatch instanceof ServerPlayerPatch) {
								((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(DemonicAscensionSkill.SUPERARMOR, true, (ServerPlayer)entitypatch.getOriginal());
							}
							
						}, Side.SERVER),
						TimeStampedEvent.create(0.05F, (entitypatch, self, params) -> {
							OpenMatrix4f transformMatrix;
							transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1), Armatures.BIPED.toolL);
							transformMatrix.translate(new Vec3f(0,0.0F,0));
							OpenMatrix4f.mul(new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO + 180F), new Vec3f(0, 1, 0)),transformMatrix,transformMatrix);
							int n = 70; // set the number of particles to emit
							double r = 5.0; // set the radius of the hemisphere to 1

							for (int i = 0; i < n; i++) {
							    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
							    double phi = Math.acos(2 * new Random().nextDouble() - 1); // generate a random polar angle in the upper hemisphere

							    // calculate the emission direction in Cartesian coordinates using the polar coordinates
							    double x = r * Math.sin(phi) * Math.cos(theta);
							    double y = r * Math.sin(phi) * Math.sin(theta);
							    double z = r * Math.cos(phi);

							    // emit the particle in the calculated direction, with some random velocity added
							    entitypatch.getOriginal().level().addParticle(ParticleTypes.SMOKE,
							        (transformMatrix.m30 + entitypatch.getOriginal().getX() + x),
							        (transformMatrix.m31 + entitypatch.getOriginal().getY() + y),
							        (transformMatrix.m32 + entitypatch.getOriginal().getZ() + z),
							        (float)(-x * 0.15f),
							        (float)(-y * 0.15f),
							        (float)(-z * 0.15f));
							}
						}, Side.CLIENT),
						TimeStampedEvent.create(1.05F, (entitypatch, self, params) -> {
							entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.7F, 0.7F);
							entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.WHOOSH_BIG.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
						}, Side.CLIENT),
						TimeStampedEvent.create(1.45F, (entitypatch, self, params) -> {
							entitypatch.getOriginal().level().playSound(null, entitypatch.getOriginal(), SoundEvents.WITHER_BREAK_BLOCK, SoundSource.PLAYERS, 1.0F, 0.5F);
							OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.handR);
							OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0));
							CORRECTION.translate(new Vec3f(0.0f, 0.0F, -1.5F));
							OpenMatrix4f.mul(CORRECTION,transformMatrix,transformMatrix);
							if (entitypatch instanceof ServerPlayerPatch) {
								((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(DemonicAscensionSkill.SUPERARMOR, false, (ServerPlayer)entitypatch.getOriginal());

								SkillContainer skill = ((ServerPlayerPatch) entitypatch).getSkill(SkillSlots.WEAPON_INNATE);
								skill.getDataManager().setDataSync(DemonicAscensionSkill.BLACKHOLE_X,(float) (transformMatrix.m30 + entitypatch.getOriginal().getX()), (ServerPlayer)entitypatch.getOriginal());
								skill.getDataManager().setDataSync(DemonicAscensionSkill.BLACKHOLE_Y,(float) (transformMatrix.m31 + entitypatch.getOriginal().getY()), (ServerPlayer)entitypatch.getOriginal());
								skill.getDataManager().setDataSync(DemonicAscensionSkill.BLACKHOLE_Z,(float) (transformMatrix.m32 + entitypatch.getOriginal().getZ()), (ServerPlayer)entitypatch.getOriginal());
								
							}
						
							((ServerLevel) entitypatch.getOriginal().level()).sendParticles( WOMParticles.ANTITHEUS_BLACKHOLE_START.get(),
									transformMatrix.m30 + entitypatch.getOriginal().getX(), 
									transformMatrix.m31 + entitypatch.getOriginal().getY(), 
									transformMatrix.m32 + entitypatch.getOriginal().getZ(), 
									1, 0.0D, 0.0D, 0.0D, 0.0D);
							
							((ServerLevel) entitypatch.getOriginal().level()).sendParticles( ParticleTypes.LARGE_SMOKE,
									transformMatrix.m30 + entitypatch.getOriginal().getX(), 
									transformMatrix.m31 + entitypatch.getOriginal().getY(), 
									transformMatrix.m32 + entitypatch.getOriginal().getZ(), 
									48, 0.0D, 0.0D, 0.0D, 0.5D);
							
						}, Side.SERVER),TimeStampedEvent.create(1.45F, (entitypatch, self, params) -> {
							OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.handR);
							OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0));
							CORRECTION.translate(new Vec3f(0.5f, 0.0F, -1.5F));
							OpenMatrix4f.mul(CORRECTION,transformMatrix,transformMatrix);
							
							Level level = entitypatch.getOriginal().level();
							
							Vec3 FractureCenter = new Vec3(
									transformMatrix.m30 + entitypatch.getOriginal().getX(), 
									transformMatrix.m31 + entitypatch.getOriginal().getY()-2, 
									transformMatrix.m32 + entitypatch.getOriginal().getZ());
							
							LevelUtil.circleSlamFracture(entitypatch.getOriginal(), level, FractureCenter, 4.00f,true,true);
						}, Side.CLIENT));
		
		ANTITHEUS_ASCENDED_IDLE = new StaticAnimation(0.1f, true, "biped/living/antitheus_ascended_idle", biped);
		ANTITHEUS_ASCENDED_RUN = new MovementAnimation(0.1f, true, "biped/living/antitheus_ascended_run", biped);
		ANTITHEUS_ASCENDED_WALK = new MovementAnimation(0.1f, true, "biped/living/antitheus_ascended_walk", biped);
		
		HERRSCHER_IDLE = new StaticAnimation(0.1f,true, "biped/living/herrscher_idle", biped);
		HERRSCHER_WALK = new MovementAnimation(0.1f, true, "biped/living/herrscher_walk", biped);
		HERRSCHER_RUN = new MovementAnimation(0.1f, true, "biped/living/herrscher_run", biped);
		
		HERRSCHER_GUARD = new StaticAnimation(0.05F, true, "biped/skill/herrscher_guard", biped);
		HERRSCHER_GUARD_HIT  = new GuardAnimation(0.05F, 0.2F, "biped/skill/herrscher_guard_hit", biped);
		HERRSCHER_GUARD_PARRY  = new GuardAnimation(0.05F, 0.5F, "biped/skill/herrscher_guard_parry", biped);
		
		HERRSCHER_TRANE = new BasicMultipleAttackAnimation(0.05F, "biped/skill/herrscher_trane", biped,
				new Phase(0.0F, 0.25F, 0.40F, 0.45F, 0.45F, biped.toolR, null),
				new Phase(0.45F, 0.50F, 0.65F, 0.70F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.80F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.5F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.80F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1 )
				.addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 4)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.8F)
				.addEvents(TimeStampedEvent.create(0.65F, (entitypatch, self, params) -> {
						if (entitypatch instanceof PlayerPatch<?>) {
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT, true,(ServerPlayer)entitypatch.getOriginal());
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT_RESULT, 6,(ServerPlayer)entitypatch.getOriginal());
						}
					}, Side.SERVER),
					TimeStampedEvent.create(1.35F, (entitypatch, self, params) -> {
						if (entitypatch instanceof PlayerPatch<?>) {
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT, false,(ServerPlayer)entitypatch.getOriginal());
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT_RESULT, 0,(ServerPlayer)entitypatch.getOriginal());
						}
					}, Side.SERVER));
		
		HERRSCHER_AUTO_1 = new BasicMultipleAttackAnimation(0.05F, "biped/combat/herrscher_auto_1", biped,
				new Phase(0.0F, 0.10F, 0.25F, 0.3F, 0.3F, biped.toolR, null),
				new Phase(0.3F, 0.35F, 0.5F, 0.5F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.80F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.5F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.80F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.8F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL,1 )
				.addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 4)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.8F)
				.addEvents(TimeStampedEvent.create(0.40F, (entitypatch, self, params) -> {
						if (entitypatch instanceof PlayerPatch<?>) {
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT, true,(ServerPlayer)entitypatch.getOriginal());
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT_RESULT, 1,(ServerPlayer)entitypatch.getOriginal());
						}
					}, Side.SERVER),
					TimeStampedEvent.create(0.60F, (entitypatch, self, params) -> {
						if (entitypatch instanceof PlayerPatch<?>) {
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT, false,(ServerPlayer)entitypatch.getOriginal());
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT_RESULT, 0,(ServerPlayer)entitypatch.getOriginal());
						}
					}, Side.SERVER));
		
		HERRSCHER_AUTO_2 = new BasicMultipleAttackAnimation(0.2F, "biped/combat/herrscher_auto_2", biped,
				new Phase(0.0F, 0.25F, 0.45F, 0.55F, Float.MAX_VALUE, biped.rootJoint, WOMColliders.HERSCHER_THRUST))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.40F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
				.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, (self, entitypatch, transformSheet) -> {
					LivingEntity attackTarget = entitypatch.getTarget();
					
					if (!self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
						TransformSheet transform = self.getTransfroms().get("Root").copyAll();
						Keyframe[] keyframes = transform.getKeyframes();
						int startFrame = 0;
						int endFrame = transform.getKeyframes().length - 1;
						Vec3f keyLast = keyframes[endFrame].transform().translation();
						Vec3 pos = entitypatch.getOriginal().getEyePosition();
						Vec3 targetpos = attackTarget.position();
						float horizontalDistance = Math.max((float)targetpos.subtract(pos).horizontalDistance() - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
						Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
						float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
						
						for (int i = startFrame; i <= endFrame; i++) {
							Vec3f translation = keyframes[i].transform().translation();
							translation.z *= scale;
						}
						
						transformSheet.readFrom(transform);
					} else {
						transformSheet.readFrom(self.getTransfroms().get("Root"));
					}
				}).addProperty(ActionAnimationProperty.COORD_SET_TICK, (self, entitypatch, transformSheet) -> {
					LivingEntity attackTarget = entitypatch.getTarget();
					if (entitypatch instanceof PlayerPatch<?>) {
						if (!self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
							TransformSheet transform = self.getTransfroms().get("Root").copyAll();
							Keyframe[] keyframes = transform.getKeyframes();
							int startFrame = 0;
							int endFrame = transform.getKeyframes().length - 1;
							Vec3f keyLast = keyframes[endFrame].transform().translation();
							Vec3 pos = entitypatch.getOriginal().getEyePosition();
							Vec3 targetpos = attackTarget.position();
							float horizontalDistance = (float) Math.max(((float)targetpos.subtract(pos).horizontalDistance() * 2.0f) - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
							Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
							float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
							
							for (int i = startFrame; i <= endFrame; i++) {
								Vec3f translation = keyframes[i].transform().translation();
								translation.z *= scale;
							}
							
							transformSheet.readFrom(transform);
						} else {
							transformSheet.readFrom(self.getTransfroms().get("Root"));
						}
					}
				})
				.addEvents(TimeStampedEvent.create(0.00F, (entitypatch, self, params) -> {
						if (entitypatch instanceof PlayerPatch<?>) {
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT, true,(ServerPlayer)entitypatch.getOriginal());
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT_RESULT, 2,(ServerPlayer)entitypatch.getOriginal());
						}
					}, Side.SERVER),
					TimeStampedEvent.create(0.20F, (entitypatch, self, params) -> {
						if (entitypatch instanceof PlayerPatch<?>) {
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT, false,(ServerPlayer)entitypatch.getOriginal());
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT_RESULT, 0,(ServerPlayer)entitypatch.getOriginal());
						}
					}, Side.SERVER));
		
		HERRSCHER_AUTO_3 = new BasicMultipleAttackAnimation(0.05F, "biped/combat/herrscher_auto_3", biped,
				new Phase(0.0F, 0.45F, 0.55F, 0.75F, Float.MAX_VALUE, biped.toolR, WOMColliders.HERSCHER_CHARGE_1))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.60F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(5.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.addEvents(TimeStampedEvent.create(0.10F, (entitypatch, self, params) -> {
						if (entitypatch instanceof PlayerPatch<?>) {
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT, true,(ServerPlayer)entitypatch.getOriginal());
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT_RESULT, 3,(ServerPlayer)entitypatch.getOriginal());
						}
					}, Side.SERVER),
					TimeStampedEvent.create(0.30F, (entitypatch, self, params) -> {
						if (entitypatch instanceof PlayerPatch<?>) {
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT, false,(ServerPlayer)entitypatch.getOriginal());
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT_RESULT, 0,(ServerPlayer)entitypatch.getOriginal());
						}
					}, Side.SERVER));
		
		HERRSCHER_BEFREIUNG = new BasicMultipleAttackAnimation(0.05F, "biped/combat/herrscher_befreiung", biped,
				new Phase(0.0F, 0.35F, 0.50F, 0.7F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.40F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.addEvents(TimeStampedEvent.create(0.10F, (entitypatch, self, params) -> {
						if (entitypatch instanceof PlayerPatch<?>) {
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT, true,(ServerPlayer)entitypatch.getOriginal());
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT_RESULT, 4,(ServerPlayer)entitypatch.getOriginal());
						}
					}, Side.SERVER),
					TimeStampedEvent.create(0.30F, (entitypatch, self, params) -> {
						if (entitypatch instanceof PlayerPatch<?>) {
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT, false,(ServerPlayer)entitypatch.getOriginal());
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT_RESULT, 0,(ServerPlayer)entitypatch.getOriginal());
						}
					}, Side.SERVER));
		
		HERRSCHER_AUSROTTUNG = new BasicMultipleAttackAnimation(0.05F, "biped/combat/herrscher_ausrottung", biped,
				new Phase(0.0F, 0.15F, 0.30F, 0.35F, 0.35F, biped.toolR, null),
				new Phase(0.35F, 0.45F, 0.60F, 0.65F, 0.65F, biped.toolR, null),
				new Phase(0.65F, 0.85F, 1.00F, 1.00F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.80F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.80F),1)
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.80F),2)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4.0F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F),2)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,2)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.4F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 1.00F))
				.addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, elapsedTime) -> {
					if (elapsedTime >= 1.15F && elapsedTime < 1.50F) {
						float dpx = (float) entitypatch.getOriginal().getX();
						float dpy = (float) entitypatch.getOriginal().getY();
						float dpz = (float) entitypatch.getOriginal().getZ();
						BlockState block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						
						while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
							dpy--;
							block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						}
						
						float distanceToGround = (float) Math.max(Math.abs(entitypatch.getOriginal().getY() - dpy)-1, 0.0F);
						
						return 1 - (1 / (-distanceToGround - 1.F) + 1.0f);
					}
					
					return 1.0F;
				});
		
		GESETZ_AUTO_1 = new BasicMultipleAttackAnimation(0.05F, "biped/skill/gezets_auto_1", biped,
				new Phase(0.0F, 0.10F, 0.20F, 0.3F, Float.MAX_VALUE, InteractionHand.OFF_HAND, biped.toolL, WOMColliders.GESETZ))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.40F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(1.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get())
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
				.addEvents(TimeStampedEvent.create(0.00F, (entitypatch, self, params) -> {
						if (entitypatch instanceof PlayerPatch<?>) {
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT, true,(ServerPlayer)entitypatch.getOriginal());
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT_RESULT, 5,(ServerPlayer)entitypatch.getOriginal());
						}
					}, Side.SERVER),
					TimeStampedEvent.create(0.20F, (entitypatch, self, params) -> {
						if (entitypatch instanceof PlayerPatch<?>) {
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT, false,(ServerPlayer)entitypatch.getOriginal());
							((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GUARD_POINT_RESULT, 0,(ServerPlayer)entitypatch.getOriginal());
						}
					}, Side.SERVER));
		
		GESETZ_AUTO_2 = new BasicMultipleAttackAnimation(0.2F, "biped/skill/gezets_auto_2", biped,
				new Phase(0.0F, 0.10F, 0.20F, 0.3F, Float.MAX_VALUE, InteractionHand.OFF_HAND, biped.toolL, WOMColliders.GESETZ_INSET_LARGE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.20F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.5F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_HIT.get())
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
				.addEvents(TimeStampedEvent.create(0.3F, (entitypatch, self, params) -> {
							entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.ANVIL_LAND, SoundSource.MASTER, 0.3F, 1.2F - ((new Random().nextFloat()-0.5f) * 0.2F));
						}, Side.CLIENT));
		
		GESETZ_AUTO_3 = new BasicMultipleAttackAnimation(0.15F, "biped/skill/gezets_auto_3", biped,
				new Phase(0.0F, 0.30F, 0.50F, 0.55F, 0.55F, InteractionHand.OFF_HAND, biped.handL, WOMColliders.PUNCH),
				new Phase(0.55F, 0.70F, 0.85F, 1.00F, Float.MAX_VALUE, InteractionHand.OFF_HAND, biped.toolL, WOMColliders.GESETZ))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.60F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.33F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(1.0F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.00F),1)
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.84F),1)
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(1.0F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,1 )
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get())
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get(),1)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_HIT.get(),1)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE,1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
				.addEvents(TimeStampedEvent.create(0.8F, (entitypatch, self, params) -> {
					entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.ANVIL_LAND, SoundSource.MASTER, 0.3F, 1.2F - ((new Random().nextFloat()-0.5f) * 0.2F));
				}, Side.CLIENT));
		
		GESETZ_SPRENGKOPF = new BasicMultipleAttackAnimation(0.05F, "biped/skill/gezets_sprengkopf", biped,
				new Phase(0.00F, 0.65F, 0.75F, 1.65F, Float.MAX_VALUE, InteractionHand.OFF_HAND, biped.rootJoint, WOMColliders.ANTITHEUS_GUILLOTINE))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.20F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.16F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(4.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get())
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
				.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.35F))
				.addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, elapsedTime) -> {
					if (elapsedTime >= 0.55F && elapsedTime < 0.65F) {
						float dpx = (float) entitypatch.getOriginal().getX();
						float dpy = (float) entitypatch.getOriginal().getY();
						float dpz = (float) entitypatch.getOriginal().getZ();
						BlockState block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						
						while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
							dpy--;
							block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
						}
						
						float distanceToGround = (float) Math.max(Math.abs(entitypatch.getOriginal().getY() - dpy)-1, 0.0F);
						
						return 1 - (1 / (-distanceToGround - 1.F) + 1.0f);
					}
					
					return 1.0F;
				})
				.addEvents(TimeStampedEvent.create(0.65F, ReuseableEvents.BODY_BIG_GROUNDSLAM, Side.CLIENT),
						TimeStampedEvent.create(0.65F, (entitypatch, self, params) -> {
							if (entitypatch instanceof PlayerPatch<?>) {
								((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GESETZ_SPRENGKOPF, true,(ServerPlayer)entitypatch.getOriginal());
							}
						}, Side.SERVER),
						TimeStampedEvent.create(0.75F, (entitypatch, self, params) -> {
							if (entitypatch instanceof ServerPlayerPatch) {
								((ServerPlayerPatch) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.GESETZ_SPRENGKOPF, false,(ServerPlayer)entitypatch.getOriginal());
								((ServerPlayerPatch) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(RegierungSkill.SUPER_ARMOR, false,(ServerPlayer)entitypatch.getOriginal());
								((ServerPlayerPatch) entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getSkill().setDurationSynchronize((ServerPlayerPatch) entitypatch, 0);
							}
						}, Side.SERVER),
						TimeStampedEvent.create(1.35F, (entitypatch, self, params) -> {
							entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.CLASH.get(), SoundSource.MASTER, 0.3F, 1.2F - ((new Random().nextFloat()-0.5f) * 0.2F));
						}, Side.CLIENT),
						TimeStampedEvent.create(2.65F, (entitypatch, self, params) -> {
							entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.WHOOSH.get(), SoundSource.MASTER, 1.0F, 0.6F - ((new Random().nextFloat()-0.5f) * 0.1F));
						}, Side.CLIENT),
						TimeStampedEvent.create(3.25F, (entitypatch, self, params) -> {
							entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.WHOOSH.get(), SoundSource.MASTER, 1.0F, 0.9F - ((new Random().nextFloat()-0.5f) * 0.1F));
						}, Side.CLIENT),
						TimeStampedEvent.create(3.45F, (entitypatch, self, params) -> {
							entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.ANVIL_LAND, SoundSource.MASTER, 0.3F, 1.2F - ((new Random().nextFloat()-0.5f) * 0.2F));
						}, Side.CLIENT));
		
		GESETZ_KRUMMEN = new BasicMultipleAttackAnimation(0.05F, "biped/skill/gezets_krummen", biped,
				new Phase(0.00F, 0.2F, 0.45F, 0.6F, Float.MAX_VALUE, InteractionHand.OFF_HAND, biped.toolL, WOMColliders.GESETZ_KRUMMEN))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.80F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.33F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_HIT.get())
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.4F);
		
		MOONLESS_IDLE = new StaticAnimation(0.1f,true, "biped/living/moonless_idle", biped);
		MOONLESS_WALK = new MovementAnimation(0.1f, true, "biped/living/moonless_walk", biped);
		MOONLESS_RUN = new MovementAnimation(0.1f, true, "biped/living/moonless_run", biped);
		
		MOONLESS_REVERSED_BYPASS = new BasicMultipleAttackAnimation(0.05F, "biped/combat/moonless_reversed_bypass", biped,
				new Phase(0.0F, 0.3F, 0.65F, 0.75F, Float.MAX_VALUE, biped.toolR, WOMColliders.MOONLESS_BYPASS))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.00F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_HIT.get())
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.addEvents(TimeStampedEvent.create(0.10F, (entitypatch, self, params) -> {
					Entity entity = entitypatch.getOriginal();
					entitypatch.getOriginal().level().addParticle(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
				},Side.CLIENT),
						TimeStampedEvent.create(0.50F, (entitypatch, self, params) -> {
							if (entitypatch instanceof ServerPlayerPatch) {
								((ServerPlayerPatch) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(LunarEclipsePassiveSkill.VERSO, true,(ServerPlayer)entitypatch.getOriginal());
								((ServerPlayerPatch) entitypatch).modifyLivingMotionByCurrentItem();
							}
						},Side.SERVER));
		
		MOONLESS_BYPASS = new BasicMultipleAttackAnimation(0.05F, "biped/combat/moonless_bypass", biped,
				new Phase(0.0F, 0.3F, 0.65F, 0.75F, Float.MAX_VALUE, biped.toolR, WOMColliders.MOONLESS_BYPASS))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.00F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_HIT.get())
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.addEvents(TimeStampedEvent.create(0.10F, (entitypatch, self, params) -> {
						Entity entity = entitypatch.getOriginal();
						entitypatch.getOriginal().level().addParticle(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
					},Side.CLIENT),
					TimeStampedEvent.create(0.50F, (entitypatch, self, params) -> {
						if (entitypatch instanceof ServerPlayerPatch) {
							((ServerPlayerPatch) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(LunarEclipsePassiveSkill.VERSO, false,(ServerPlayer)entitypatch.getOriginal());
							((ServerPlayerPatch) entitypatch).modifyLivingMotionByCurrentItem();
						}
					},Side.SERVER));
		
		MOONLESS_AUTO_1 = new BasicMultipleAttackAnimation(0.05F, "biped/combat/moonless_auto_1", biped,
				new Phase(0.0F, 0.25F, 0.4F, 0.45F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.20F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.50F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_HIT.get())
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F);
		
		MOONLESS_AUTO_2 = new BasicMultipleAttackAnimation(0.05F, "biped/combat/moonless_auto_2", biped,
				new Phase(0.0F, 0.3F, 0.4F, 0.45F, 0.45F, biped.toolR, null),
				new Phase(0.45F, 0.5F, 0.6F, 0.65F, 0.65F, biped.toolR, null),
				new Phase(0.65F, 0.7F, 1.0F, 1.15F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.00F),2)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,2)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F);
		
		MOONLESS_AUTO_1_VERSO = new BasicMultipleAttackAnimation(0.05F, "biped/combat/moonless_auto_1_verso", biped,
				new Phase(0.00F, 0.2F, 0.25F, 0.29F, 0.29F, biped.toolR, null),
				new Phase(0.29F, 0.3F, 0.4F, 0.45F, Float.MAX_VALUE, biped.toolR, null))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.50F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.00F),1)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,1)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.4F)
				.addEvents(TimeStampedEvent.create(0.00F, (entitypatch, self, params) -> {
					Entity entity = entitypatch.getOriginal();
					entitypatch.getOriginal().level().addParticle(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
				},Side.CLIENT));
		
		MOONLESS_AUTO_2_VERSO = new BasicMultipleAttackAnimation(0.25F, "biped/combat/moonless_auto_2_verso", biped,
				new Phase(0.0F, 0.35F, 0.45F, 0.85F, Float.MAX_VALUE, biped.toolR, WOMColliders.MOONLESS_BYPASS))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.20F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, false);
		
		MOONLESS_LUNAR_ECHO = new SpecialAttackAnimation(0.05F, "biped/skill/moonless_lunar_echo", biped,
				new Phase(0.0F, 0.6F, 0.7F, 0.85F, Float.MAX_VALUE, biped.rootJoint, WOMColliders.LUNAR_ECHO))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.50F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.addEvents(TimeStampedEvent.create(0.10F, (entitypatch, self, params) -> {
					((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setData(LunarEclipsePassiveSkill.IDLE, true);
					entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 0.2F, 0.5F);
				}, Side.CLIENT),
					TimeStampedEvent.create(0.75F, (entitypatch, self, params) -> {
						entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.7F, 0.5F);
						((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setData(LunarEclipsePassiveSkill.IDLE, false);
						OpenMatrix4f transformMatrix;
						transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(0), Armatures.BIPED.toolL);
						transformMatrix.translate(new Vec3f(0,0.0F,0));
						OpenMatrix4f.mul(new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO + 180F), new Vec3f(0, 1, 0)),transformMatrix,transformMatrix);
						int n = 70; // set the number of particles to emit
						double r = 0.3; // set the radius of the hemisphere to 1
						double t = 0.01; // set the radius of the hemisphere to 1

						 entitypatch.getOriginal().level().addParticle(ParticleTypes.FLASH,
							        (transformMatrix.m30 + entitypatch.getOriginal().getX()),
							        (transformMatrix.m31 + entitypatch.getOriginal().getY()),
							        (transformMatrix.m32 + entitypatch.getOriginal().getZ()),
							        (float)(0),
							        (float)(0),
							        (float)(0));
						for (int i = 0; i < n; i++) {
						   entitypatch.getOriginal().level().addParticle(ParticleTypes.END_ROD,
						        (transformMatrix.m30 + entitypatch.getOriginal().getX()),
						        (transformMatrix.m31 + entitypatch.getOriginal().getY()),
						        (transformMatrix.m32 + entitypatch.getOriginal().getZ()),
						        (float)(0),
						        (float)((new Random().nextFloat() * 10)),
						        (float)(0));
						}
						
						for (int i = 0; i < n; i++) {
							double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
							double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness
							
							// calculate the emission direction in Cartesian coordinates using the polar coordinates
							double x = r * Math.cos(phi) * Math.cos(theta);
							double y = r * Math.cos(phi) * Math.sin(theta);
							double z = r * Math.sin(phi);
							
							// create a Vector3f object to represent the emission direction
							Vec3f direction = new Vec3f(
									(float)x * 1.8f,
									(float)y * 1.8f,
									(float)z * 1.8f);
							
							// rotate the direction vector to align with the forward vector
							OpenMatrix4f rotation = new OpenMatrix4f().rotate((float) Math.toRadians(90), new Vec3f(1, 0, 0));
							OpenMatrix4f.transform3v(rotation, direction, direction);
							
							// emit the particle in the calculated direction, with some random velocity added
							entitypatch.getOriginal().level().addParticle(ParticleTypes.END_ROD,
									(transformMatrix.m30 + entitypatch.getOriginal().getX() + direction.x*2),
							        (transformMatrix.m31 + entitypatch.getOriginal().getY() + direction.y*2) + 0.8f,
							        (transformMatrix.m32 + entitypatch.getOriginal().getZ() + direction.z*2),
									(float)(direction.x/2),
									(float)(direction.y/2),
									(float)(direction.z/2));
						}
				}, Side.CLIENT));;

				MOONLESS_CRESCENT = new BasicMultipleAttackAnimation(0.05F, "biped/combat/moonless_crescent", biped,
						new Phase(0.0F, 0.40F, 0.50F, 0.70F, Float.MAX_VALUE, biped.toolR, null))
						.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
						.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.TARGET_LOST_HEALTH.create(0.20f)))
						.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
						.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
						.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
						.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
						.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
						.addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
						.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.25F))
						.addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, elapsedTime) -> {
							if (elapsedTime >= 0.25F && elapsedTime < 0.40F) {
								float dpx = (float) entitypatch.getOriginal().getX();
								float dpy = (float) entitypatch.getOriginal().getY();
								float dpz = (float) entitypatch.getOriginal().getZ();
								BlockState block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
								
								while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
									dpy--;
									block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
								}
								
								float distanceToGround = (float) Math.max(Math.abs(entitypatch.getOriginal().getY() - dpy)-1, 0.0F);
								
								return 1 - (1 / (-distanceToGround - 1.F) + 1.0f);
							}
							
							return 1.0F;
						});
				
				MOONLESS_FULLMOON = new BasicMultipleAttackAnimation(0.05F, "biped/combat/moonless_fullmoon", biped,
						new Phase(0.00F, 0.00F, 0.10F, 0.15F, 0.15F, biped.toolR, WOMColliders.MOONLESS_BYPASS),
						new Phase(0.15F, 0.20F, 0.30F, 0.35F, 0.35F, biped.toolR, WOMColliders.MOONLESS_BYPASS),
						new Phase(0.35F, 0.40F, 0.50F, 0.55F, 0.55F, biped.toolR, WOMColliders.MOONLESS_BYPASS),
						new Phase(0.55F, 0.60F, 0.70F, 0.75F, 0.75F, biped.toolR, WOMColliders.MOONLESS_BYPASS),
						new Phase(0.75F, 0.80F, 1.00F, 2.25F, Float.MAX_VALUE, biped.toolR, WOMColliders.MOONLESS_BYPASS))
						.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
						.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F))
						.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL)
						.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F),1)
						.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F),1)
						.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,1)
						.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F),2)
						.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F),2)
						.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,2)
						.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F),3)
						.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F),3)
						.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD,3)
						.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F),4)
						.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F),4)
						.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE,4)
						.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.4F)
						.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
						.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
						.addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
						.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 1.00F))
						.addEvents(TimePeriodEvent.create(1.50F, 1.7F, ReuseableEvents.LOOPED_FALLING_MOVE, Side.BOTH))
						.addEvents(TimeStampedEvent.create(1.7F, ReuseableEvents.LOOPED_FALLING, Side.BOTH),
								TimeStampedEvent.create(1.5F, (entitypatch, self, params) -> {
									entitypatch.getOriginal().resetFallDistance();
								}, Side.BOTH));
				
				MOONLESS_GUARD = new StaticAnimation(0.20F, true, "biped/skill/moonless_guard", biped);
				MOONLESS_GUARD_HIT_1  = new GuardAnimation(0.05F, 0.2F, "biped/skill/moonless_guard_hit_1", biped);
				MOONLESS_GUARD_HIT_2  = new GuardAnimation(0.05F, 0.2F, "biped/skill/moonless_guard_hit_2", biped);
				MOONLESS_GUARD_HIT_3  = new GuardAnimation(0.05F, 0.2F, "biped/skill/moonless_guard_hit_3", biped);
				
				MOONLESS_REWINDER = new BasicMultipleAttackAnimation(0.05F, "biped/combat/moonless_rewinder", biped,
						new Phase(0.0F, 0.20F, 0.55F, 0.65F, Float.MAX_VALUE, biped.toolR, WOMColliders.MOONLESS_BYPASS))
						.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.40F))
						.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
						.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
						.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_HIT.get())
						.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
						.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.4F)
						.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
						.addEvents(TimeStampedEvent.create(0.20F, (entitypatch, self, params) -> {
							Entity entity = entitypatch.getOriginal();
							entitypatch.getOriginal().level().addParticle(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
						},Side.CLIENT));
				
				SOLAR_IDLE = new StaticAnimation(0.1f,true, "biped/living/solar_idle", biped);
				SOLAR_WALK = new MovementAnimation(0.1f, true, "biped/living/solar_walk", biped);
				SOLAR_RUN = new MovementAnimation(0.1f, true, "biped/living/solar_run", biped);
	}
	
	private static class ReuseableEvents {
		
		public static final AnimationEvent.AnimationEventConsumer KATANA_IN = (entitypatch, self, params) -> entitypatch.playSound(EpicFightSounds.SWORD_IN.get(), 0, 0);
		public static final AnimationEvent.AnimationEventConsumer FAST_SPINING = (entitypatch, self, params) -> entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.WHOOSH.get(), SoundSource.MASTER, 0.5F, 1.1F - ((new Random().nextFloat()-0.5f) * 0.2F));
		
		public static final AnimationEvent.AnimationEventConsumer LOOPED_FALLING_MOVE = (entitypatch, self, params) -> {
			float dpx = (float) entitypatch.getOriginal().getX();
			float dpy = (float) entitypatch.getOriginal().getY();
			float dpz = (float) entitypatch.getOriginal().getZ();
			BlockState block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
			while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
				dpy--;
				block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
			}
			dpy = (int)dpy;
			float A = 4;
			float B = 0.01f;
			float vertical_distance = (float) (A * (1- Math.exp(-B * (entitypatch.getOriginal().getY() - dpy))));
			if (entitypatch.getOriginal().getY() - dpy > 4 && entitypatch.getOriginal().getDeltaMovement().y < -0.08f) {
				LivingEntity livingentity = entitypatch.getOriginal();
				
				Vec3f direction = new Vec3f((float)0.5*vertical_distance,0.0f, 0.0f);
			    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO+90), new Vec3f(0, 1, 0));
			    OpenMatrix4f.transform3v(rotation, direction, direction);
			    
				livingentity.move(MoverType.SELF, direction.toDoubleVector());
			}
		};
		
		public static final AnimationEvent.AnimationEventConsumer LOOPED_FALLING = (entitypatch, self, params) -> {
			float dpx = (float) entitypatch.getOriginal().getX();
			float dpy = (float) entitypatch.getOriginal().getY();
			float dpz = (float) entitypatch.getOriginal().getZ();
			BlockState block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
			while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
				dpy--;
				block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
			}
			dpy = (int)dpy;
			if (entitypatch.getOriginal().getY() - dpy > 4 && entitypatch.getOriginal().getDeltaMovement().y < -0.08f) {
				entitypatch.getAnimator().getPlayerFor(self).setElapsedTimeCurrent((float) (entitypatch.getAnimator().getPlayerFor(self).getElapsedTime() - 0.20f));
			} else {
				if (entitypatch.getOriginal().getY() - dpy > 1) {
					entitypatch.getOriginal().move(MoverType.SELF,new Vec3(0,-1,0));
				}
			}
		};
		
		public static final AnimationEvent.AnimationEventConsumer ANGLED_FALLING = (entitypatch, self, params) -> {
			float dpx = (float) entitypatch.getOriginal().getX();
			float dpy = (float) entitypatch.getOriginal().getY();
			float dpz = (float) entitypatch.getOriginal().getZ();
			BlockState block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
			while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
				dpy--;
				block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
			}
			dpy = (int)dpy;
			float A = 0.051f * self.getPlaySpeed(entitypatch);
			float B = 0.18f;
			float time_to_rewind = (float) Math.min((A * (1- Math.exp(-B * (entitypatch.getOriginal().getY() - dpy)))),0.050f * self.getPlaySpeed(entitypatch) * 0.995f);
			A = 4;
			B = 0.02f;
			float vertical_distance = (float) (A * (1- Math.exp(-B * (entitypatch.getOriginal().getY() - dpy))));
			if (time_to_rewind > 0.01f && entitypatch.getOriginal().getDeltaMovement().y < -0.08f) {
				EntityState state = self.getState(entitypatch, entitypatch.getAnimator().getPlayerFor(self).getElapsedTime());
				
				if (state.inaction()) {
					LivingEntity livingentity = entitypatch.getOriginal();
					Vec3 coordvector = ((BasicMultipleAttackAnimation) self).getCoordVector(entitypatch, self);
					
					Vec3f direction = new Vec3f((float)coordvector.horizontalDistance()*vertical_distance,-0.2f, 0.0f);
				    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO+90), new Vec3f(0, 1, 0));
				    OpenMatrix4f.transform3v(rotation, direction, direction);
				    
					livingentity.move(MoverType.SELF, direction.toDoubleVector());
				}
				entitypatch.getAnimator().getPlayerFor(self).setElapsedTimeCurrent((float) (entitypatch.getAnimator().getPlayerFor(self).getElapsedTime() - time_to_rewind));
			}
		};
		
		public static final AnimationEvent.AnimationEventConsumer ENDERBLASTER_RELOAD = (entitypatch, self, params) -> {
			if (entitypatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WOMWeaponCategories.ENDERBLASTER) {
				if (entitypatch instanceof PlayerPatch) {
					entitypatch.getOriginal().level().playSound(null, entitypatch.getOriginal(), WOMSounds.ENDERBLASTER_RELOAD.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
				}
				SkillContainer skill = ((ServerPlayerPatch) entitypatch).getSkill(SkillSlots.WEAPON_INNATE);
				skill.getSkill().setStackSynchronize(((ServerPlayerPatch) entitypatch), skill.getStack()+1);
				if (skill.getStack() == skill.getSkill().getMaxStack()) {
					skill.getSkill().setConsumptionSynchronize(((ServerPlayerPatch) entitypatch), 6);
				}
			}
		};
		
		public static final AnimationEvent.AnimationEventConsumer ENDERBLASTER_RELOAD_BOTH = (entitypatch, self, params) -> {
			if (entitypatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WOMWeaponCategories.ENDERBLASTER) {
				if (entitypatch instanceof PlayerPatch) {
					entitypatch.getOriginal().level().playSound(null, entitypatch.getOriginal(), WOMSounds.ENDERBLASTER_RELOAD.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
				}
				SkillContainer skill = ((ServerPlayerPatch) entitypatch).getSkill(SkillSlots.WEAPON_INNATE);
				skill.getSkill().setStackSynchronize(((ServerPlayerPatch) entitypatch), skill.getStack()+2);
				if (skill.getStack() == skill.getSkill().getMaxStack()) {
					skill.getSkill().setConsumptionSynchronize(((ServerPlayerPatch) entitypatch), 12);
				}
			}
		};
		
		private static final AnimationEvent.AnimationEventConsumer SHOOT_RIGHT = (entitypatch, self, params) -> {
			if (entitypatch instanceof PlayerPatch) {
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.LASER_BLAST.get(), SoundSource.PLAYERS, 0.6F,1.5F - ((new Random().nextFloat()-0.5f) * 0.2F));
			}
			OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.toolR);
			transformMatrix.translate(new Vec3f(0,-0.8F,-0.3F));
			OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0));
			OpenMatrix4f.mul(CORRECTION,transformMatrix,transformMatrix);
			
			//System.out.println((transformMatrix.m11+0.07f)*1.2f);
			
			int n = 20; // set the number of particles to emit
			double r = 0.1; // set the radius of the disk to 1
			double t = 0.01; // set the thickness of the disk to 0.1
			
			for (int i = 0; i < n; i++) {
			    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
			    double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness

			    // calculate the emission direction in Cartesian coordinates using the polar coordinates
			    double x = r * Math.cos(phi) * Math.cos(theta);
			    double y = r * Math.cos(phi) * Math.sin(theta);
			    double z = r * Math.sin(phi);
			    
			 // create a Vector3f object to represent the emission direction
			    Vec3f direction = new Vec3f((float)x, (float)y, (float)z);

			 // rotate the direction vector to align with the forward vector
			    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO), new Vec3f(0, 1, 0));
			    rotation.rotate((float) ((transformMatrix.m11+0.07f)*1.5f), new Vec3f(1, 0, 0));
			    OpenMatrix4f.transform3v(rotation, direction, direction);
			    
			    // emit the particle in the calculated direction, with some random velocity added
			    entitypatch.getOriginal().level().addParticle(ParticleTypes.DRAGON_BREATH,
				        (transformMatrix.m30 + entitypatch.getOriginal().getX()),
				        (transformMatrix.m31 + entitypatch.getOriginal().getY()),
				        (transformMatrix.m32 + entitypatch.getOriginal().getZ()),
				        (float)(direction.x),
				        (float)(direction.y),
				        (float)(direction.z));
			}
		};
		private static final AnimationEvent.AnimationEventConsumer SHOTGUN_SHOOT_RIGHT = (entitypatch, self, params) -> {
			if (entitypatch instanceof PlayerPatch) {
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.LASER_BLAST.get(), SoundSource.PLAYERS, 0.6F, 1.5f - ((new Random().nextFloat()-0.5f) * 0.2F));
			}
			OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.toolR);
			transformMatrix.translate(new Vec3f(0,-0.8F,-0.3F));
			OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0));
			OpenMatrix4f.mul(CORRECTION,transformMatrix,transformMatrix);
			
			int n = 30; // set the number of particles to emit
			double r = 0.15; // set the radius of the disk to 1
			double t = 0.01; // set the thickness of the disk to 0.1
			
			for (int i = 0; i < n; i++) {
			    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
			    double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness

			    // calculate the emission direction in Cartesian coordinates using the polar coordinates
			    double x = r * Math.cos(phi) * Math.cos(theta);
			    double y = r * Math.cos(phi) * Math.sin(theta);
			    double z = r * Math.sin(phi);
			    
			 // create a Vector3f object to represent the emission direction
			    Vec3f direction = new Vec3f((float)x, (float)y, (float)z);

			    // rotate the direction vector to align with the forward vector
			    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO), new Vec3f(0, 1, 0));
			    rotation.rotate((float) ((transformMatrix.m11+0.07f)*1.5f), new Vec3f(1, 0, 0));
			    rotation.translate(new Vec3f(0, 0, -0.1f));
			    OpenMatrix4f.transform3v(rotation, direction, direction);
			    
			    // emit the particle in the calculated direction, with some random velocity added
			    entitypatch.getOriginal().level().addParticle(ParticleTypes.DRAGON_BREATH,
				        (transformMatrix.m30 + entitypatch.getOriginal().getX()),
				        (transformMatrix.m31 + entitypatch.getOriginal().getY()),
				        (transformMatrix.m32 + entitypatch.getOriginal().getZ()),
				        (float)(direction.x),
				        (float)(direction.y),
				        (float)(direction.z));
			}
		};
		private static final AnimationEvent.AnimationEventConsumer SHOOT_LEFT = (entitypatch, self, params) -> {
			if (entitypatch instanceof PlayerPatch) {
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.LASER_BLAST.get(), SoundSource.PLAYERS, 0.6F, 1.5F - ((new Random().nextFloat()-0.5f) * 0.2F));
			}
			OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.toolL);
			transformMatrix.translate(new Vec3f(0,-0.8F,-0.3F));
			OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0));
			OpenMatrix4f.mul(CORRECTION,transformMatrix,transformMatrix);
			
			int n = 20; // set the number of particles to emit
			double r = 0.1; // set the radius of the disk to 1
			double t = 0.01; // set the thickness of the disk to 0.1
			
			for (int i = 0; i < n; i++) {
			    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
			    double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness

			    // calculate the emission direction in Cartesian coordinates using the polar coordinates
			    double x = r * Math.cos(phi) * Math.cos(theta);
			    double y = r * Math.cos(phi) * Math.sin(theta);
			    double z = r * Math.sin(phi);
			    
			 // create a Vector3f object to represent the emission direction
			    Vec3f direction = new Vec3f((float)x, (float)y, (float)z);

			    // rotate the direction vector to align with the forward vector
			    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO), new Vec3f(0, 1, 0));
			    rotation.rotate((float) ((transformMatrix.m11+0.07f)*1.5f), new Vec3f(1, 0, 0));
			    OpenMatrix4f.transform3v(rotation, direction, direction);
			    
			    // emit the particle in the calculated direction, with some random velocity added
			    entitypatch.getOriginal().level().addParticle(ParticleTypes.DRAGON_BREATH,
				        (transformMatrix.m30 + entitypatch.getOriginal().getX()),
				        (transformMatrix.m31 + entitypatch.getOriginal().getY()),
				        (transformMatrix.m32 + entitypatch.getOriginal().getZ()),
				        (float)(direction.x),
				        (float)(direction.y),
				        (float)(direction.z));
			}
		};
		private static final AnimationEvent.AnimationEventConsumer SHOTGUN_SHOOT_LEFT = (entitypatch, self, params) -> {
			if (entitypatch instanceof PlayerPatch) {
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.LASER_BLAST.get(), SoundSource.PLAYERS, 0.6F, 1.5F- ((new Random().nextFloat()-0.5f) * 0.2F));
			}
			OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.toolL);
			transformMatrix.translate(new Vec3f(0,-0.8F,-0.3F));
			OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0));
			OpenMatrix4f.mul(CORRECTION,transformMatrix,transformMatrix);
			
			int n = 30; // set the number of particles to emit
			double r = 0.15; // set the radius of the disk to 1
			double t = 0.01; // set the thickness of the disk to 0.1
			
			for (int i = 0; i < n; i++) {
			    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
			    double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness

			    // calculate the emission direction in Cartesian coordinates using the polar coordinates
			    double x = r * Math.cos(phi) * Math.cos(theta);
			    double y = r * Math.cos(phi) * Math.sin(theta);
			    double z = r * Math.sin(phi);
			    
			 // create a Vector3f object to represent the emission direction
			    Vec3f direction = new Vec3f((float)x, (float)y, (float)z);

			    // rotate the direction vector to align with the forward vector
			    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO), new Vec3f(0, 1, 0));
			    rotation.rotate((float) ((transformMatrix.m11+0.07f)*1.5f), new Vec3f(1, 0, 0));
			    rotation.translate(new Vec3f(0, 0, -0.1f));
			    OpenMatrix4f.transform3v(rotation, direction, direction);
			    
			    // emit the particle in the calculated direction, with some random velocity added
			    entitypatch.getOriginal().level().addParticle(ParticleTypes.DRAGON_BREATH,
				        (transformMatrix.m30 + entitypatch.getOriginal().getX()),
				        (transformMatrix.m31 + entitypatch.getOriginal().getY()),
				        (transformMatrix.m32 + entitypatch.getOriginal().getZ()),
				        (float)(direction.x),
				        (float)(direction.y),
				        (float)(direction.z));
			}
		};
		/*
		private static final AnimationEvent.AnimationEventConsumer SHOOT_BOTH = (entitypatch, self, params) -> {
			if (entitypatch instanceof PlayerPatch) {
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.LASER_BLAST.get(), SoundSource.PLAYERS, 0.6F, 1.5F);
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.LASER_BLAST.get(), SoundSource.PLAYERS, 0.6F, 1.5F);
			}
			OpenMatrix4f transformMatrixl = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.toolL);
			OpenMatrix4f transformMatrixl2 = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.toolL);
			OpenMatrix4f transformMatrixr = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.toolL);
			OpenMatrix4f transformMatrixr2 = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.toolL);
			transformMatrixl.translate(new Vec3f(0,-0.6F,-0.3F));
			transformMatrixl2.translate(new Vec3f(0.0f,-2F,-0.3F));
			transformMatrixr.translate(new Vec3f(0,-0.6F,-0.3F));
			transformMatrixr2.translate(new Vec3f(0.0f,-2F,-0.3F));
			OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0));
			OpenMatrix4f.mul(CORRECTION,transformMatrixl,transformMatrixl);
			OpenMatrix4f.mul(CORRECTION,transformMatrixl2,transformMatrixl2);
			OpenMatrix4f.mul(CORRECTION,transformMatrixr,transformMatrixr);
			OpenMatrix4f.mul(CORRECTION,transformMatrixr2,transformMatrixr2);
			for (int i = 0; i < 20; i++) {
				entitypatch.getOriginal().level().addParticle(ParticleTypes.REVERSE_PORTAL,
					(transformMatrixl.m30 + entitypatch.getOriginal().getX()),
					(transformMatrixl.m31 + entitypatch.getOriginal().getY()),
					(transformMatrixl.m32 + entitypatch.getOriginal().getZ()),
					((transformMatrixl2.m30 - transformMatrixl.m30) + (new Random().nextFloat() - 0.5F)*2),
					((transformMatrixl2.m31 - transformMatrixl.m31) + (new Random().nextFloat() - 0.5F)*2),
					((transformMatrixl2.m32 - transformMatrixl.m32) + (new Random().nextFloat() - 0.5F)*2));
			}
			for (int i = 0; i < 20; i++) {
				entitypatch.getOriginal().level().addParticle(ParticleTypes.REVERSE_PORTAL,
						(transformMatrixr.m30 + entitypatch.getOriginal().getX()),
						(transformMatrixr.m31 + entitypatch.getOriginal().getY()),
						(transformMatrixr.m32 + entitypatch.getOriginal().getZ()),
						((transformMatrixr2.m30 - transformMatrixr.m30) + (new Random().nextFloat() - 0.5F)*2),
						((transformMatrixr2.m31 - transformMatrixr.m31) + (new Random().nextFloat() - 0.5F)*2),
						((transformMatrixr2.m32 - transformMatrixr.m32) + (new Random().nextFloat() - 0.5F)*2));
			}
		};
		*/
		private static final AnimationEvent.AnimationEventConsumer SHOTGUN_SHOOT_BOTH = (entitypatch, self, params) -> {
			if (entitypatch instanceof PlayerPatch) {
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.LASER_BLAST.get(), SoundSource.PLAYERS, 0.6F, 0.7F- ((new Random().nextFloat()-0.5f) * 0.2F));
			}
			OpenMatrix4f transformMatrixl = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.toolL);
			transformMatrixl.translate(new Vec3f(0,-0.8F,-0.3F));
			OpenMatrix4f.mul(new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0)),transformMatrixl,transformMatrixl);
			
			int n = 20; // set the number of particles to emit
			double r = 0.1; // set the radius of the disk to 1
			double t = 0.01; // set the thickness of the disk to 0.1
			
			for (int i = 0; i < n; i++) {
			    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
			    double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness

			    // calculate the emission direction in Cartesian coordinates using the polar coordinates
			    double x = r * Math.cos(phi) * Math.cos(theta);
			    double y = r * Math.cos(phi) * Math.sin(theta);
			    double z = r * Math.sin(phi);
			    
			 // create a Vector3f object to represent the emission direction
			    Vec3f direction = new Vec3f((float)x, (float)y, (float)z);

			    // rotate the direction vector to align with the forward vector
			    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO), new Vec3f(0, 1, 0));
			    rotation.rotate((float) ((transformMatrixl.m11+0.07f)*1.5f), new Vec3f(1, 0, 0));
			    OpenMatrix4f.transform3v(rotation, direction, direction);
			    
			    // emit the particle in the calculated direction, with some random velocity added
			    entitypatch.getOriginal().level().addParticle(ParticleTypes.DRAGON_BREATH,
				        (transformMatrixl.m30 + entitypatch.getOriginal().getX()),
				        (transformMatrixl.m31 + entitypatch.getOriginal().getY()),
				        (transformMatrixl.m32 + entitypatch.getOriginal().getZ()),
				        (float)(direction.x),
				        (float)(direction.y),
				        (float)(direction.z));
			}
			
			OpenMatrix4f transformMatrixr = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.toolR);
			transformMatrixr.translate(new Vec3f(0,-0.8F,-0.3F));
			OpenMatrix4f.mul(new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0)),transformMatrixr,transformMatrixr);
			
			for (int i = 0; i < n; i++) {
			    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
			    double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness

			    // calculate the emission direction in Cartesian coordinates using the polar coordinates
			    double x = r * Math.cos(phi) * Math.cos(theta);
			    double y = r * Math.cos(phi) * Math.sin(theta);
			    double z = r * Math.sin(phi);
			    
			 // create a Vector3f object to represent the emission direction
			    Vec3f direction = new Vec3f((float)x, (float)y, (float)z);

			    // rotate the direction vector to align with the forward vector
			    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO), new Vec3f(0, 1, 0));
			    rotation.rotate((float) ((transformMatrixr.m11+0.07f)*1.5f), new Vec3f(1, 0, 0));
			    OpenMatrix4f.transform3v(rotation, direction, direction);
			    
			    // emit the particle in the calculated direction, with some random velocity added
			    entitypatch.getOriginal().level().addParticle(ParticleTypes.DRAGON_BREATH,
			        (transformMatrixr.m30 + entitypatch.getOriginal().getX()),
			        (transformMatrixr.m31 + entitypatch.getOriginal().getY()),
			        (transformMatrixr.m32 + entitypatch.getOriginal().getZ()),
			        (float)(direction.x),
			        (float)(direction.y),
			        (float)(direction.z));
			}
		};
		
		private static final AnimationEvent.AnimationEventConsumer BODY_BIG_GROUNDSLAM = (entitypatch, self, params) -> {
			Vec3 position = entitypatch.getOriginal().position();
			OpenMatrix4f modelTransform = entitypatch.getArmature().getBindedTransformFor(
					entitypatch.getArmature().getPose(1.0f),
					Armatures.BIPED.rootJoint).mulFront(
					OpenMatrix4f.createTranslation((float)position.x, (float)position.y, (float)position.z)
					.mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
					.mulBack(entitypatch.getModelMatrix(1.0F)))
				);
			
			Vec3 weaponEdge = OpenMatrix4f.transform(modelTransform, (new Vec3f(0,0.0F,-0.0F)).toDoubleVector());
			Level level = entitypatch.getOriginal().level();
			
			Vec3 floorPos = getfloor(entitypatch, self,new Vec3f(0,0.0F,0.0F),Armatures.BIPED.rootJoint);
			weaponEdge = new Vec3(weaponEdge.x ,floorPos.y, weaponEdge.z);
			BlockState blockState = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(floorPos.x,floorPos.y,floorPos.z));
			if (entitypatch instanceof PlayerPatch) {
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), blockState.is(Blocks.WATER) ? SoundEvents.GENERIC_SPLASH : EpicFightSounds.GROUND_SLAM.get() , SoundSource.PLAYERS, 1.5F, 1.3F- (new Random().nextFloat()-0.5f) * 0.2F);
			}
			entitypatch.getOriginal().level().addParticle(WOMParticles.WOM_GROUND_SLAM.get(),
					floorPos.x,
					(int) floorPos.y + 1,
					floorPos.z,
					0.7D, 40.0D, 0.6D);
			LevelUtil.circleSlamFracture(entitypatch.getOriginal(), level, weaponEdge, 2.0f,true,false);
		};
		
		private static final AnimationEvent.AnimationEventConsumer AGONY_GROUNDSLAM = (entitypatch, self, params) -> {
			Vec3 floorPos = getfloor(entitypatch, self,new Vec3f(0,0.0F,-1.5F),Armatures.BIPED.toolR);
			BlockState blockState = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(floorPos.x,floorPos.y,floorPos.z));
			if (entitypatch instanceof PlayerPatch) {
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), blockState.is(Blocks.WATER) ? SoundEvents.GENERIC_SPLASH : EpicFightSounds.GROUND_SLAM.get() , SoundSource.PLAYERS, 1.5F, 1.8F- (new Random().nextFloat()-0.5f) * 0.2F);
			}
			entitypatch.getOriginal().level().addParticle(WOMParticles.WOM_GROUND_SLAM.get(),
					floorPos.x,
					(int) floorPos.y + 1,
					floorPos.z,
					0.6D, 25.0D, 0.6D);
		};
		private static final AnimationEvent.AnimationEventConsumer AGONY_AIRBURST_JUMP = (entitypatch, self, params) -> {
			if (entitypatch instanceof PlayerPatch) {
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.PLAYERS, 0.7F, 0.7F);
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.7F, 0.7F);
			}
			OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.rootJoint);
			transformMatrix.translate(new Vec3f(0.0f, 0.0F, 0.0F));
			OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0));
			OpenMatrix4f.mul(CORRECTION,transformMatrix,transformMatrix);
			
			//System.out.println((transformMatrix.m11+0.07f)*1.2f);
			
			int n = 80; // set the number of particles to emit
			double r = 0.8; // set the radius of the disk to 1
			double t = 0.01; // set the thickness of the disk to 0.1
			
			for (int i = 0; i < n; i++) {
			    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
			    double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness

			    // calculate the emission direction in Cartesian coordinates using the polar coordinates
			    double x = r * Math.cos(phi) * Math.cos(theta);
			    double y = r * Math.cos(phi) * Math.sin(theta);
			    double z = r * Math.sin(phi);
			    
			 // create a Vector3f object to represent the emission direction
			    Vec3f direction = new Vec3f((float)x, (float)y, (float)z);

			 // rotate the direction vector to align with the forward vector
			    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO), new Vec3f(0, 1, 0));
			    rotation.rotate((float) Math.toRadians(90), new Vec3f(1, 0, 0));
			    OpenMatrix4f.transform3v(rotation, direction, direction);
			    
			    // emit the particle in the calculated direction, with some random velocity added
			    entitypatch.getOriginal().level().addParticle(ParticleTypes.POOF,
			        (transformMatrix.m30 + entitypatch.getOriginal().getX()),
			        (transformMatrix.m31 + entitypatch.getOriginal().getY()),
			        (transformMatrix.m32 + entitypatch.getOriginal().getZ()),
			        (float)(direction.x),
			        (float)(direction.y),
			        (float)(direction.z));
			}
			
			for (int i = 0; i < 24; i++) {
				entitypatch.getOriginal().level().addParticle(ParticleTypes.CLOUD,
					entitypatch.getOriginal().getX() + ((new Random().nextFloat() - 0.5F)),
					entitypatch.getOriginal().getY() + 0.2F,
					entitypatch.getOriginal().getZ() + ((new Random().nextFloat() - 0.5F)),
					((new Random().nextFloat() - 0.5F) * 0.05F),
					((new Random().nextFloat()) * 0.5F) +0.05f,
					((new Random().nextFloat() - 0.5F) * 0.05F));
			}
		};
		private static final AnimationEvent.AnimationEventConsumer AGONY_ENCHANTED_JUMP = (entitypatch, self, params) -> {
			float lineXpos = 0;
			float lineYpos = 0;
			float lineZpos = 0;
			for (int i = 0; i < 10; i++) {
				lineXpos = (new Random().nextFloat() - 0.5F)*16;
				lineYpos = (new Random().nextFloat())*2.5f;
				lineZpos = (new Random().nextFloat() - 0.5F)*16;
				for (int j = 0; j < 10; j++) {
					entitypatch.getOriginal().level().addAlwaysVisibleParticle(ParticleTypes.ENCHANT,
						entitypatch.getOriginal().getX() + lineXpos,
						entitypatch.getOriginal().getY() + (0.3f*j) + lineYpos + 5F,
						entitypatch.getOriginal().getZ() + lineZpos,
						0,
						-7.5F,
						0);
				}
			}
		};
		private static final AnimationEvent.AnimationEventConsumer AGONY_PLUNGE_GROUNDTHRUST = (entitypatch, self, params) -> {
			Vec3 position = entitypatch.getOriginal().position();
			OpenMatrix4f modelTransform = entitypatch.getArmature().getBindedTransformFor(
					entitypatch.getArmature().getPose(1.0f),
					Armatures.BIPED.toolR).mulFront(
					OpenMatrix4f.createTranslation((float)position.x, (float)position.y, (float)position.z)
					.mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
					.mulBack(entitypatch.getModelMatrix(1.0F)))
				);
			
			Vec3 weaponEdge = OpenMatrix4f.transform(modelTransform, (new Vec3f(0,0.0F,-1.0F)).toDoubleVector());
			Level level = entitypatch.getOriginal().level();
			
			Vec3 bodyFloorPos = getfloor(entitypatch, self,new Vec3f(0,0.0F, 0.0F),Armatures.BIPED.rootJoint);
			Vec3 position2 = new Vec3(0,(bodyFloorPos.y - 2) - entitypatch.getOriginal().getY(),0);
			entitypatch.getOriginal().move(MoverType.SELF,position2);
			
			Vec3 floorPos = getfloor(entitypatch, self,new Vec3f(0,0.0F,-0.5F),Armatures.BIPED.toolR);
			BlockState blockState = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(floorPos.x,floorPos.y,floorPos.z));
			if (entitypatch instanceof PlayerPatch) {
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), blockState.is(Blocks.WATER) ? SoundEvents.GENERIC_SPLASH : EpicFightSounds.GROUND_SLAM.get() , SoundSource.PLAYERS, 1.5F, 1.2F - (new Random().nextFloat()-0.5f) * 0.2F);
			}
			weaponEdge = new Vec3(weaponEdge.x ,floorPos.y, weaponEdge.z);
			entitypatch.getOriginal().level().addParticle(WOMParticles.WOM_GROUND_SLAM.get(),
					floorPos.x,
					(int) floorPos.y + 1,
					floorPos.z,
					1.0D, 50.0D, 1.0D);
			LevelUtil.circleSlamFracture(entitypatch.getOriginal(), level, weaponEdge, 3.0f,true,false);
		};
		
		private static final AnimationEvent.AnimationEventConsumer TORMENT_GROUNDSLAM = (entitypatch, self, params) -> {
			Vec3 position = entitypatch.getOriginal().position();
			OpenMatrix4f modelTransform = entitypatch.getArmature().getBindedTransformFor(
					entitypatch.getArmature().getPose(1.0f),
					Armatures.BIPED.toolR).mulFront(
					OpenMatrix4f.createTranslation((float)position.x, (float)position.y, (float)position.z)
					.mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
					.mulBack(entitypatch.getModelMatrix(1.0F)))
				);
			
			Vec3 weaponEdge = OpenMatrix4f.transform(modelTransform, (new Vec3f(0,-0.0F,-1.4F)).toDoubleVector());
			Level level = entitypatch.getOriginal().level();
			
			Vec3 floorPos = getfloor(entitypatch, self,new Vec3f(0,0.0F,-1.4F),Armatures.BIPED.toolR);
			BlockState blockState = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(floorPos.x,floorPos.y,floorPos.z));
			if (entitypatch instanceof PlayerPatch) {
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), blockState.is(Blocks.WATER) ? SoundEvents.GENERIC_SPLASH : EpicFightSounds.GROUND_SLAM.get() , SoundSource.PLAYERS, 1.5F, 1.1F- (new Random().nextFloat()-0.5f) * 0.2F);
			}
			weaponEdge = new Vec3(weaponEdge.x ,floorPos.y, weaponEdge.z);
			entitypatch.getOriginal().level().addParticle(WOMParticles.WOM_GROUND_SLAM.get(),
					floorPos.x,
					(int) floorPos.y + 1,
					floorPos.z,
					1.0D, 50.0D, 1.0D);
			LevelUtil.circleSlamFracture(entitypatch.getOriginal(), level, weaponEdge, 3.50f,true,false);
			
		};
		private static final AnimationEvent.AnimationEventConsumer TORMENT_GROUNDSLAM_SMALL = (entitypatch, self, params) -> {
			Vec3 position = entitypatch.getOriginal().position();
			OpenMatrix4f modelTransform = entitypatch.getArmature().getBindedTransformFor(
					entitypatch.getArmature().getPose(1.0f),
					Armatures.BIPED.toolR).mulFront(
					OpenMatrix4f.createTranslation((float)position.x, (float)position.y, (float)position.z)
					.mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
					.mulBack(entitypatch.getModelMatrix(1.0F)))
				);
			
			Vec3 weaponEdge = OpenMatrix4f.transform(modelTransform, (new Vec3f(0,0.0F,-1.4F)).toDoubleVector());
			Level level = entitypatch.getOriginal().level();
			
			Vec3 floorPos = getfloor(entitypatch, self,new Vec3f(0,0.0F,-1.4F),Armatures.BIPED.toolR);
			BlockState blockState = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(floorPos.x,floorPos.y,floorPos.z));
			if (entitypatch instanceof PlayerPatch) {
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), blockState.is(Blocks.WATER) ? SoundEvents.GENERIC_SPLASH : EpicFightSounds.GROUND_SLAM.get() , SoundSource.PLAYERS, 1.5F, 1.5F- (new Random().nextFloat()-0.5f) * 0.2F);
			}
			weaponEdge = new Vec3(weaponEdge.x ,floorPos.y, weaponEdge.z);
			
			entitypatch.getOriginal().level().addParticle(WOMParticles.WOM_GROUND_SLAM.get(),
					floorPos.x,
					(int) floorPos.y + 1,
					floorPos.z,
					0.7D, 35.0D, 0.7D);
			LevelUtil.circleSlamFracture(entitypatch.getOriginal(), level, weaponEdge, 2.0f,true,false);
			
			
		};
		
		private static final AnimationEvent.AnimationEventConsumer RUINE_COMET_GROUNDTHRUST = (entitypatch, self, params) -> {
			Vec3 floorPos = getfloor(entitypatch, self,new Vec3f(0,0.0F,-1.4F),Armatures.BIPED.toolR);
			BlockState blockState = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(floorPos.x,floorPos.y,floorPos.z));
			if (entitypatch instanceof PlayerPatch) {
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), blockState.is(Blocks.WATER) ? SoundEvents.GENERIC_SPLASH : EpicFightSounds.GROUND_SLAM.get() , SoundSource.PLAYERS, 1.5F, 1.8F- (new Random().nextFloat()-0.5f) * 0.2F);
			}
			entitypatch.getOriginal().level().addParticle(WOMParticles.WOM_GROUND_SLAM.get(),
					floorPos.x,
					(int) floorPos.y + 1,
					floorPos.z,
					0.4D, 25.0D, 0.4D);
		};
		private static final AnimationEvent.AnimationEventConsumer RUINE_COMET_AIRBURST = (entitypatch, self, params) -> {
			if (entitypatch instanceof PlayerPatch) {
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.PLAYERS, 0.7F, 0.7F);
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.7F, 0.7F);
			}
			OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.rootJoint);
			transformMatrix.translate(new Vec3f(0.0f,1.0F,0.0F));
			OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0));
			OpenMatrix4f.mul(CORRECTION,transformMatrix,transformMatrix);
			
			//System.out.println((transformMatrix.m11+0.07f)*1.2f);
			
			int n = 20; // set the number of particles to emit
			double r = 0.2; // set the radius of the disk to 1
			double t = 0.01; // set the thickness of the disk to 0.1
			
			for (int i = 0; i < n; i++) {
			    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
			    double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness

			    // calculate the emission direction in Cartesian coordinates using the polar coordinates
			    double x = r * Math.cos(phi) * Math.cos(theta);
			    double y = r * Math.cos(phi) * Math.sin(theta);
			    double z = r * Math.sin(phi);
			    
			 // create a Vector3f object to represent the emission direction
			    Vec3f direction = new Vec3f((float)x, (float)y, (float)z);

			 // rotate the direction vector to align with the forward vector
			    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO), new Vec3f(0, 1, 0));
			    //rotation.rotate((float) ((transformMatrix.m11-1.07f)*1.5f), new Vec3f(1, 0, 0));
			    OpenMatrix4f.transform3v(rotation, direction, direction);
			    
			    // emit the particle in the calculated direction, with some random velocity added
			    entitypatch.getOriginal().level().addParticle(ParticleTypes.POOF,
			        (transformMatrix.m30 + entitypatch.getOriginal().getX()),
			        (transformMatrix.m31 + entitypatch.getOriginal().getY()),
			        (transformMatrix.m32 + entitypatch.getOriginal().getZ()),
			        (float)(direction.x),
			        (float)(direction.y),
			        (float)(direction.z));
			}
			
			n = 40;
			r = 0.5;
			for (int i = 0; i < n; i++) {
			    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
			    double phi = Math.acos(new Random().nextDouble()); // generate a random angle within the disk thickness

			    // calculate the emission direction in Cartesian coordinates using the polar coordinates
			    double x = r * Math.sin(phi) * Math.cos(theta);
			    double y = r * Math.sin(phi) * Math.sin(theta);
			    double z = r * Math.cos(phi);
			    
			 // create a Vector3f object to represent the emission direction
			    float randomVelocity =  new Random().nextFloat();
			    Vec3f direction = new Vec3f((float)x * randomVelocity, (float)y * randomVelocity, (float)z * randomVelocity *1.5f);

			 // rotate the direction vector to align with the forward vector
			    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO + 180), new Vec3f(0, 1, 0));
			    //rotation.rotate((float) Math.toRadians(-90), new Vec3f(1, 0, 0));
			    rotation.translate(new Vec3f(0, 0, 0.2f));
			    OpenMatrix4f.transform3v(rotation, direction, direction);
			    
			    // emit the particle in the calculated direction, with some random velocity added
			    entitypatch.getOriginal().level().addParticle(ParticleTypes.POOF,
			        (transformMatrix.m30 + entitypatch.getOriginal().getX()),
			        (transformMatrix.m31 + entitypatch.getOriginal().getY())-0.8f,
			        (transformMatrix.m32 + entitypatch.getOriginal().getZ()),
			        (float)(direction.x),
			        (float)(direction.y),
			        (float)(direction.z)
			        );
			}
		};
		
		private static final AnimationEvent.AnimationEventConsumer GROUND_BODYSCRAPE_LAND = (entitypatch, self, params) -> {
			if (entitypatch instanceof PlayerPatch) {
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.GROUND_SLAM.get(), SoundSource.PLAYERS, 1.0F, 1.8F- ((new Random().nextFloat()-0.5f) * 0.2F));
			}
			Vec3 floorPos = getfloor(entitypatch, self,new Vec3f(0,0.0F,0.4F),Armatures.BIPED.rootJoint);
			entitypatch.getOriginal().level().addParticle(WOMParticles.WOM_GROUND_SLAM.get(),
					floorPos.x,
					(int) floorPos.y + 1,
					floorPos.z,
					0.5D, 35.0D, 0.5D);
		};
		/*
		private static final AnimationEvent.AnimationEventConsumer GROUND_BODYSCRAPE_TRAIL = (entitypatch, self, params) -> {
			for (int i = 0; i < 10; i++) {
				entitypatch.getOriginal().level().addParticle(ParticleTypes.CRIT,
					entitypatch.getOriginal().getX(),
					entitypatch.getOriginal().getY(),
					entitypatch.getOriginal().getZ(),
					(new Random().nextFloat() - 0.5F) * 1.5,
					(new Random().nextFloat() * 0.5) + 0.2f,
					(new Random().nextFloat() - 0.5F) * 1.5);
			}
		};
		*/
		
		private static final AnimationEvent.AnimationEventConsumer ENDER_STEP = (entitypatch, self, params) -> {
			if (!entitypatch.isLogicalClient()) {
				Entity entity = entitypatch.getOriginal();
				((ServerLevel) entity.level).sendParticles(ParticleTypes.REVERSE_PORTAL,
						entity.xo, 
						entity.yo + 1, 
						entity.zo,
						60,
						0.65,
					    0.65,
						0.65,
						0.25);
				entity.level().playSound(null, 
						entity.xo, 
						entity.yo + 1, 
						entity.zo,
		    			SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 2.0F, 1.0F - ((new Random().nextFloat()-0.5f) * 0.2F));
			}
			LivingEntity entity = entitypatch.getOriginal();
			entitypatch.getOriginal().level().addParticle(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
		};
		
		private static final AnimationEvent.AnimationEventConsumer MOB_ENDER_OBSCURIS = (entitypatch, self, params) -> {
			if (!entitypatch.isLogicalClient()) {
				Entity entity = entitypatch.getOriginal();
				if (entitypatch.getOriginal().getLastHurtMob() != null) {
					LivingEntity target = entitypatch.getOriginal().getLastHurtMob();
					if (target != null) {
						double offset = 2.0; // Adjust this value as needed

						// Calculate the new position based on the reference entity's position and rotation
						double referenceX = target.getX();
						double referenceY = target.getY();
						double referenceZ = target.getZ();
						float referenceYaw = target.yHeadRot;

						double newX = referenceX + (offset * Math.sin(Math.toRadians(referenceYaw)));
						double newZ = referenceZ - (offset * Math.cos(Math.toRadians(referenceYaw)));
						double newY = referenceY; // Keep the same Y position
						
						entity.teleportTo(
								newX,
								newY,
								newZ);
						entity.setDeltaMovement(target.getDeltaMovement());
						entity.rotate(Rotation.CLOCKWISE_180);
					}
				}
				((ServerLevel) entity.level()).sendParticles(ParticleTypes.REVERSE_PORTAL,
						entity.getX(), 
						entity.getY() + 1, 
						entity.getZ(),
						60,
						0.05,
					    0.05,
						0.05,
						0.5);
				entity.level().playSound(null, 
						entity.xo, 
						entity.yo + 1, 
						entity.zo,
		    			SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 2.0F, 1.0F - ((new Random().nextFloat()-0.5f) * 0.2F));
			}
		};
		
		private static final AnimationEvent.AnimationEventConsumer ENDER_OBSCURIS = (entitypatch, self, params) -> {
			if (!entitypatch.isLogicalClient()) {
				ServerPlayer entity = (ServerPlayer) entitypatch.getOriginal();
				ServerPlayerPatch playerPatch = (ServerPlayerPatch) entitypatch;
				if (playerPatch.getSkill(WOMSkills.ENDEROBSCURIS) != null) {
					LivingEntity target = (LivingEntity) entity.level().getEntity(playerPatch.getSkill(WOMSkills.ENDEROBSCURIS).getDataManager().getDataValue(EnderObscurisSkill.TARGET_ID));
					if (target != null) {
						double offset = 2.0; // Adjust this value as needed

						// Calculate the new position based on the reference entity's position and rotation
						double referenceX = target.getX();
						double referenceY = target.getY();
						double referenceZ = target.getZ();
						float referenceYaw = target.yHeadRot;

						double newX = referenceX + (offset * Math.sin(Math.toRadians(referenceYaw)));
						double newZ = referenceZ - (offset * Math.cos(Math.toRadians(referenceYaw)));
						double newY = referenceY; // Keep the same Y position
						entity.teleportTo(((ServerLevel) entity.level()),
								newX,
								newY,
								newZ,
								target.yHeadRot,
								entity.getViewXRot(1));
						entity.setDeltaMovement(target.getDeltaMovement());
					}
				}
				((ServerLevel) entity.level()).sendParticles(ParticleTypes.REVERSE_PORTAL,
						entity.getX(), 
						entity.getY() + 1, 
						entity.getZ(),
						60,
						0.05,
					    0.05,
						0.05,
						0.5);
				entity.level().playSound(null, 
						entity.xo, 
						entity.yo + 1, 
						entity.zo,
		    			SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 2.0F, 1.0F - ((new Random().nextFloat()-0.5f) * 0.2F));
			}
		};
		
		private static final AnimationEvent.AnimationEventConsumer SHADOW_STEP_ENTER = (entitypatch, self, params) -> {
			if (!entitypatch.isLogicalClient()) {
				Entity entity = (Entity) entitypatch.getOriginal();
				((ServerLevel) entity.level).sendParticles(ParticleTypes.LARGE_SMOKE,
						entity.xo, 
						entity.yo + 1, 
						entity.zo,
						40,
						0.0,
					    0.0,
						0.0,
						0.1);
				entity.level().playSound(null, 
						entity.xo, 
						entity.yo + 1, 
						entity.zo,
		    			SoundEvents.WITHER_SHOOT, entity.getSoundSource(), 1.0F, 1.0F - ((new Random().nextFloat()-0.5f) * 0.2F));
			}
		};
		
		private static final AnimationEvent.AnimationEventConsumer SHADOW_STEP = (entitypatch, self, params) -> {
			if (!entitypatch.isLogicalClient()) {
				Entity entity = (Entity) entitypatch.getOriginal();
				((ServerLevel) entity.level).sendParticles(ParticleTypes.SMOKE,
					entity.xo, 
					entity.yo + 1, 
					entity.zo,
					10,
					0.45,
				    0.45,
					0.45,
					0.05);
			}
		};
		
		private static final AnimationEvent.AnimationEventConsumer ANTITHEUS_WEAPON_TRAIL_ON = (entitypatch, self, params) -> {
			((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(DemonMarkPassiveSkill.BASIC_ATTACK, true, (ServerPlayer)entitypatch.getOriginal());
		};
		private static final AnimationEvent.AnimationEventConsumer ANTITHEUS_WEAPON_TRAIL_OFF = (entitypatch, self, params) -> {
			((PlayerPatch<?>) entitypatch).getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().setDataSync(DemonMarkPassiveSkill.BASIC_ATTACK, false, (ServerPlayer)entitypatch.getOriginal());
		};
		
		private static final AnimationEvent.AnimationEventConsumer ANTITHEUS_AIRBURST = (entitypatch, self, params) -> {
			if (entitypatch instanceof PlayerPatch) {
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.PLAYERS, 0.7F, 0.7F);
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.7F, 0.7F);
			}
			OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.rootJoint);
			transformMatrix.translate(new Vec3f(0.0f,1.0F,0.0F));
			OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0));
			OpenMatrix4f.mul(CORRECTION,transformMatrix,transformMatrix);
			
			//System.out.println((transformMatrix.m11+0.07f)*1.2f);
			
			int n = 20; // set the number of particles to emit
			double r = 0.2; // set the radius of the disk to 1
			double t = 0.01; // set the thickness of the disk to 0.1
			
			for (int i = 0; i < n; i++) {
			    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
			    double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness

			    // calculate the emission direction in Cartesian coordinates using the polar coordinates
			    double x = r * Math.cos(phi) * Math.cos(theta);
			    double y = r * Math.cos(phi) * Math.sin(theta);
			    double z = r * Math.sin(phi);
			    
			 // create a Vector3f object to represent the emission direction
			    Vec3f direction = new Vec3f((float)x, (float)y, (float)z);

			 // rotate the direction vector to align with the forward vector
			    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO), new Vec3f(0, 1, 0));
			    //rotation.rotate((float) ((transformMatrix.m11-1.07f)*1.5f), new Vec3f(1, 0, 0));
			    OpenMatrix4f.transform3v(rotation, direction, direction);
			    
			    // emit the particle in the calculated direction, with some random velocity added
			    entitypatch.getOriginal().level().addParticle(ParticleTypes.LARGE_SMOKE,
			        (transformMatrix.m30 + entitypatch.getOriginal().getX()),
			        (transformMatrix.m31 + entitypatch.getOriginal().getY()),
			        (transformMatrix.m32 + entitypatch.getOriginal().getZ()),
			        (float)(direction.x),
			        (float)(direction.y),
			        (float)(direction.z));
			}
			
			n = 40;
			r = 0.5;
			for (int i = 0; i < n; i++) {
			    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
			    double phi = Math.acos(new Random().nextDouble()); // generate a random angle within the disk thickness

			    // calculate the emission direction in Cartesian coordinates using the polar coordinates
			    double x = r * Math.sin(phi) * Math.cos(theta);
			    double y = r * Math.sin(phi) * Math.sin(theta);
			    double z = r * Math.cos(phi);
			    
			    // create a Vector3f object to represent the emission direction
			    float randomVelocity =  new Random().nextFloat();
			    Vec3f direction = new Vec3f((float)x * randomVelocity, (float)y * randomVelocity, (float)z * randomVelocity *1.5f);

			    // rotate the direction vector to align with the forward vector
			    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO + 180), new Vec3f(0, 1, 0));
			    //rotation.rotate((float) Math.toRadians(-90), new Vec3f(1, 0, 0));
			    rotation.translate(new Vec3f(0, 0, 0.2f));
			    OpenMatrix4f.transform3v(rotation, direction, direction);
			    
			    // emit the particle in the calculated direction, with some random velocity added
			    entitypatch.getOriginal().level().addParticle(ParticleTypes.LARGE_SMOKE,
			        (transformMatrix.m30 + entitypatch.getOriginal().getX()),
			        (transformMatrix.m31 + entitypatch.getOriginal().getY())-0.8f,
			        (transformMatrix.m32 + entitypatch.getOriginal().getZ()),
			        (float)(direction.x),
			        (float)(direction.y),
			        (float)(direction.z)
			        );
			}
			
		};
		
		private static final AnimationEvent.AnimationEventConsumer ANTITHEUS_GROUND_SLAM = (entitypatch, self, params) -> {
			if (entitypatch instanceof PlayerPatch) {
				entitypatch.getOriginal().level().playSound((Player)entitypatch.getOriginal(), entitypatch.getOriginal(), EpicFightSounds.BLUNT_HIT_HARD.get(), SoundSource.PLAYERS, 0.7F, 0.7F);
			}
			OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f), Armatures.BIPED.toolR);
			transformMatrix.translate(new Vec3f(0.0f,0.2F,-1.6F));
			OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0));
			OpenMatrix4f.mul(CORRECTION,transformMatrix,transformMatrix);
			
			//System.out.println((transformMatrix.m11+0.07f)*1.2f);
			
			int n = 40; // set the number of particles to emit
			double r = 0.4; // set the radius of the disk to 1
			double t = 0.01; // set the thickness of the disk to 0.1
			
			for (int i = 0; i < n; i++) {
			    double theta = 2 * Math.PI * new Random().nextDouble(); // generate a random azimuthal angle
			    double phi = (new Random().nextDouble() - 0.5) * Math.PI * t / r; // generate a random angle within the disk thickness

			    // calculate the emission direction in Cartesian coordinates using the polar coordinates
			    double x = r * Math.cos(phi) * Math.cos(theta);
			    double y = r * Math.cos(phi) * Math.sin(theta);
			    double z = r * Math.sin(phi);
			    
			 // create a Vector3f object to represent the emission direction
			    Vec3f direction = new Vec3f((float)x, (float)y, (float)z);

			 // rotate the direction vector to align with the forward vector
			    OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO), new Vec3f(0, 1, 0));
			    rotation.rotate((float) Math.toRadians(90), new Vec3f(1, 0, 0));
			    OpenMatrix4f.transform3v(rotation, direction, direction);
			    
			    // emit the particle in the calculated direction, with some random velocity added
			    entitypatch.getOriginal().level().addParticle(ParticleTypes.LARGE_SMOKE,
			        (transformMatrix.m30 + entitypatch.getOriginal().getX()),
			        (transformMatrix.m31 + entitypatch.getOriginal().getY()),
			        (transformMatrix.m32 + entitypatch.getOriginal().getZ()),
			        (float)(direction.x),
			        (float)(direction.y),
			        (float)(direction.z));
			}
		};
		
		public static Vec3 getfloor(LivingEntityPatch<?> entitypatch,StaticAnimation self, Vec3f WeaponOffset,Joint joint) {
			OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f),joint);
			transformMatrix.translate(WeaponOffset);
			OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0));
			OpenMatrix4f.mul(CORRECTION,transformMatrix,transformMatrix);
			float dpx = transformMatrix.m30 + (float) entitypatch.getOriginal().getX();
			float dpy = transformMatrix.m31 + (float) entitypatch.getOriginal().getY();
			float dpz = transformMatrix.m32 + (float) entitypatch.getOriginal().getZ();
			BlockState block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
			while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
				dpy--;
				block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
			}
			return new Vec3(dpx,dpy,dpz);
		}
	}
	
	public static Vec3 getfloor(LivingEntityPatch<?> entitypatch,StaticAnimation self, Vec3f WeaponOffset,Joint joint) {
		OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(1.0f),joint);
		transformMatrix.translate(WeaponOffset);
		OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yRotO + 180F), new Vec3f(0, 1, 0));
		OpenMatrix4f.mul(CORRECTION,transformMatrix,transformMatrix);
		float dpx = transformMatrix.m30 + (float) entitypatch.getOriginal().getX();
		float dpy = transformMatrix.m31 + (float) entitypatch.getOriginal().getY();
		float dpz = transformMatrix.m32 + (float) entitypatch.getOriginal().getZ();
		BlockState block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
		while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
			dpy--;
			block = entitypatch.getOriginal().level().getBlockState(new BlockPos.MutableBlockPos(dpx,dpy,dpz));
		}
		return new Vec3(dpx,dpy,dpz);
	}
}
