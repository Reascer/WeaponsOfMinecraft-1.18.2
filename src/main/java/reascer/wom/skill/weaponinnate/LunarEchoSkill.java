package reascer.wom.skill.weaponinnate;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import reascer.wom.gameasset.WOMAnimations;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.IndirectEpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.SkillConsumeEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;


public class LunarEchoSkill extends WeaponInnateSkill {
	private static final UUID EVENT_UUID = UUID.fromString("c7a0ee46-56b3-4008-9fba-d2594b1e2676");
	public static final SkillDataKey<Boolean> ECHO = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	public static final SkillDataKey<Boolean> CRESCENT = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	public static final SkillDataKey<Integer> TIMER = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	private StaticAnimation attackAnimations;
	
	public LunarEchoSkill(Builder builder) {
		super(builder);
		this.attackAnimations = WOMAnimations.MOONLESS_LUNAR_ECHO;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(ECHO);
		container.getDataManager().registerData(CRESCENT);
		container.getDataManager().registerData(TIMER);
		
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(ECHO)) {
				if (event.getTarget().hasEffect(MobEffects.GLOWING)) {
					int glowing_amp = event.getTarget().getEffect(MobEffects.GLOWING).getAmplifier();
					int blindnesslvl = 0;
					event.getTarget().removeEffect(MobEffects.GLOWING);
					if (event.getTarget().hasEffect(MobEffects.BLINDNESS)) {
						blindnesslvl = event.getTarget().getEffect(MobEffects.BLINDNESS).getAmplifier();
						int blindnessDuration = event.getTarget().getEffect(MobEffects.BLINDNESS).getDuration();
						event.getTarget().removeEffect(MobEffects.BLINDNESS);
						int newduration = blindnessDuration+(3-(Math.abs(new Random().nextInt())%5));
						if (newduration < 2) {
							newduration = 2;
						}
						event.getTarget().addEffect(new MobEffectInstance(MobEffects.BLINDNESS,newduration,0,true,false,false));
					} else {
						event.getTarget().addEffect(new MobEffectInstance(MobEffects.BLINDNESS,20,0,true,false,false));
					}
					event.getTarget().addEffect(new MobEffectInstance(MobEffects.GLOWING,20*7,glowing_amp+blindnesslvl+1,true,false,false));
					Boolean no_lunarEclipse_tag = true;
					for (String tag : event.getTarget().getTags()) {
						if (tag.contains("lunar_eclipse:")) {
							no_lunarEclipse_tag = false;
							break;
						}
					}
					if (no_lunarEclipse_tag) {
						event.getTarget().addTag("lunar_eclipse:"+event.getPlayerPatch().getOriginal().getId());
					}
				} else {
					if (event.getTarget().hasEffect(MobEffects.BLINDNESS)) {
						int blindnesslvl = event.getTarget().getEffect(MobEffects.BLINDNESS).getAmplifier();
						event.getTarget().addEffect(new MobEffectInstance(MobEffects.GLOWING,20*7,blindnesslvl+1,true,false,false));
						event.getTarget().removeEffect(MobEffects.BLINDNESS);
						event.getTarget().addEffect(new MobEffectInstance(MobEffects.BLINDNESS,20,0,true,false,false));
					} else {
						event.getTarget().addEffect(new MobEffectInstance(MobEffects.GLOWING,20*7,0,true,false,false));
					}
				}
			} else {
				if (event.getTarget().hasEffect(MobEffects.GLOWING)) {
					int glowing_amp = event.getTarget().getEffect(MobEffects.GLOWING).getAmplifier();
					event.getTarget().addEffect(new MobEffectInstance(MobEffects.BLINDNESS,20*7,(int) (glowing_amp+event.getAttackDamage()+EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, event.getPlayerPatch().getOriginal())),true,false,false));
					event.getTarget().removeEffect(MobEffects.GLOWING);
					Boolean no_lunarEclipse_tag = true;
					for (String tag : event.getTarget().getTags()) {
						if (tag.contains("lunar_eclipse:")) {
							no_lunarEclipse_tag = false;
							break;
						}
					}
					if (no_lunarEclipse_tag) {
						event.getTarget().addTag("lunar_eclipse:"+event.getPlayerPatch().getOriginal().getId());
					}
				}
				
				if (event.getTarget().hasEffect(MobEffects.BLINDNESS)) {
					int blindnesslvl = event.getTarget().getEffect(MobEffects.BLINDNESS).getAmplifier();
					int blindnessDuration = event.getTarget().getEffect(MobEffects.BLINDNESS).getDuration();
					event.getTarget().removeEffect(MobEffects.BLINDNESS);
					int newduration = blindnessDuration+(3-(Math.abs(new Random().nextInt())%5));
					if (newduration < 2) {
						newduration = 2;
					}
					event.getTarget().addEffect(new MobEffectInstance(MobEffects.BLINDNESS,newduration,(int) (blindnesslvl+event.getAttackDamage()+EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, event.getPlayerPatch().getOriginal())),true,false,false));
					Boolean no_lunarEclipse_tag = true;
					for (String tag : event.getTarget().getTags()) {
						if (tag.contains("lunar_eclipse:")) {
							no_lunarEclipse_tag = false;
							break;
						}
					}
					if (no_lunarEclipse_tag) {
						event.getTarget().addTag("lunar_eclipse:"+event.getPlayerPatch().getOriginal().getId());
					}
					
				}
			}
			if (container.getDataManager().getDataValue(CRESCENT) && event.getDamageSource().getAnimation().equals(WOMAnimations.MOONLESS_CRESCENT)) {
				if (event.getTarget().hasEffect(MobEffects.BLINDNESS)) {
					int blindnesslvl = event.getTarget().getEffect(MobEffects.BLINDNESS).getAmplifier();
					event.getTarget().removeEffect(MobEffects.BLINDNESS);
					event.getTarget().addEffect(new MobEffectInstance(MobEffects.BLINDNESS,2,blindnesslvl+((1+EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, event.getPlayerPatch().getOriginal()))*2),true,false,false));
					Boolean no_lunarEclipse_tag = true;
					for (String tag : event.getTarget().getTags()) {
						if (tag.contains("lunar_eclipse:")) {
							no_lunarEclipse_tag = false;
							break;
						}
					}
					if (no_lunarEclipse_tag) {
						event.getTarget().addTag("lunar_eclipse:"+event.getPlayerPatch().getOriginal().getId());
					}
					
				}
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			if (!event.getAnimation().equals(attackAnimations)) {
				container.getDataManager().setDataSync(ECHO, false, event.getPlayerPatch().getOriginal());
			}
			if (!event.getAnimation().equals(WOMAnimations.MOONLESS_CRESCENT)) {
				container.getDataManager().setDataSync(CRESCENT, false, event.getPlayerPatch().getOriginal());
			} else {
				container.getDataManager().setDataSync(CRESCENT, true, event.getPlayerPatch().getOriginal());
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event) -> {
		
		});
		
		
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID);
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		super.executeOnServer(executer, args);
		executer.playAnimationSynchronized(this.attackAnimations, 0);
		executer.getSkill(this).getDataManager().setDataSync(ECHO, true, executer.getOriginal());
		executer.getSkill(this).getDataManager().setDataSync(TIMER,20 * 4, executer.getOriginal());
		this.setDurationSynchronize(executer, 0);
	}
	
	@Override
	public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
		List<Component> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		
		return list;
	}
	
	@Override
	public boolean isExecutableState(PlayerPatch<?> executer) {
		executer.updateEntityState();
		EntityState playerState = executer.getEntityState();
		return !(executer.getOriginal().isFallFlying() || executer.currentLivingMotion == LivingMotions.FALL || !playerState.canUseSkill());
	}
	
	@Override
	public WeaponInnateSkill registerPropertiesToAnimation() {
		return this;
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		super.updateContainer(container);
		if(!container.getExecuter().isLogicalClient()) {
			if (container.getDataManager().getDataValue(ECHO)) {
				if (container.getDataManager().getDataValue(TIMER) > 0) {
					container.getDataManager().setDataSync(TIMER, container.getDataManager().getDataValue(TIMER)-1,((ServerPlayerPatch)container.getExecuter()).getOriginal());
				} else {
					container.getDataManager().setDataSync(ECHO, false,((ServerPlayerPatch)container.getExecuter()).getOriginal());
				}
			} else {
				container.getDataManager().setDataSync(TIMER,0,((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
		}
	}
}