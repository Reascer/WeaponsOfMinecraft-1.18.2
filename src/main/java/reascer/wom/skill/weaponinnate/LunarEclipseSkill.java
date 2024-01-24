package reascer.wom.skill.weaponinnate;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.lwjgl.opengl.ARBTextureMirrorClampToEdge;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.skill.weaponpassive.LunarEchoPassiveSkill;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightEntityDamageSource;
import yesman.epicfight.world.damagesource.SourceTags;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.DealtDamageEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;


public class LunarEclipseSkill extends WeaponInnateSkill {
	private static final UUID EVENT_UUID = UUID.fromString("c7a0ee46-56b3-4008-9fba-d2594b1e2676");
	public static final SkillDataKey<Boolean> ECHO = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	public static final SkillDataKey<Boolean> CRESCENT = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	public static final SkillDataKey<Float> LUNAR_ECLIPSE_STACK = SkillDataKey.createDataKey(SkillDataManager.ValueType.FLOAT);
	public static final SkillDataKey<Integer> TIMER = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	
	
	public LunarEclipseSkill(Builder builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(ECHO);
		container.getDataManager().registerData(CRESCENT);
		container.getDataManager().registerData(LUNAR_ECLIPSE_STACK);
		container.getDataManager().registerData(TIMER);
		
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
			int sweeping_edge = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, event.getPlayerPatch().getOriginal());
			
			if (!event.getDamageSource().getAnimation().equals(WOMAnimations.MOONLESS_LUNAR_ECLIPSE)) {
				if (container.getDataManager().getDataValue(ECHO)) {
					if (event.getTarget().hasEffect(MobEffects.GLOWING)) {
						int glowing_amp = event.getTarget().getEffect(MobEffects.GLOWING).getAmplifier();
						event.getTarget().removeEffect(MobEffects.GLOWING);
						event.getTarget().addEffect(new MobEffectInstance(MobEffects.GLOWING,20*7,glowing_amp+((2+EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, event.getPlayerPatch().getOriginal()))*2),true,false,false));
						
						AABB box = AABB.ofSize(event.getPlayerPatch().getOriginal().position(),10 + (Math.min(40, 1 * glowing_amp)), 10, 10 + (Math.min(40, 1 * glowing_amp)));
						
						List<Entity> list = event.getPlayerPatch().getOriginal().level.getEntities(event.getPlayerPatch().getOriginal(),box);
						
						for (Entity entity : list) {
							if (entity instanceof LivingEntity) {
								LivingEntity livingEntity = (LivingEntity) entity;
								
								if (livingEntity.isAlive()) {
									if (!livingEntity.hasEffect(MobEffects.GLOWING)) {
										livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING,20*7,glowing_amp+((2+EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, event.getPlayerPatch().getOriginal()))*2),true,false,false));
									}
								}
							}
						}
					} else {
						event.getTarget().addEffect(new MobEffectInstance(MobEffects.GLOWING,20*7,0,true,false,false));
					}
				} else {
					if (event.getPlayerPatch().getSkill(SkillSlots.WEAPON_PASSIVE) != null ) {
						if (event.getPlayerPatch().getSkill(SkillSlots.WEAPON_PASSIVE).getSkill() == WOMSkills.LUNAR_ECHO_PASSIVE) {
							if (!event.getPlayerPatch().getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().getDataValue(LunarEchoPassiveSkill.VERSO)) {
								// RECTO
								if (event.getTarget().hasEffect(MobEffects.GLOWING)) {
									if (event.getPlayerPatch().getOriginal().hasEffect(MobEffects.INVISIBILITY)) {
										event.getPlayerPatch().getOriginal().removeEffect(MobEffects.INVISIBILITY);
									}
									event.getPlayerPatch().getOriginal().addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,20*7,0,true,false,false));
									
									container.getDataManager().setDataSync(TIMER, 20*7,event.getPlayerPatch().getOriginal());
									container.getDataManager().setDataSync(LUNAR_ECLIPSE_STACK, container.getDataManager().getDataValue(LUNAR_ECLIPSE_STACK) + (event.getAttackDamage() * 2),event.getPlayerPatch().getOriginal());
									((ServerLevel) event.getTarget().level).playSound(null,
											event.getTarget().getX(),
											event.getTarget().getY()+0.75f,
											event.getTarget().getZ(),
											SoundEvents.BEACON_ACTIVATE, event.getPlayerPatch().getOriginal().getSoundSource(), 4.0F, 2.0F);
									((ServerLevel) event.getPlayerPatch().getOriginal().level).sendParticles(ParticleTypes.FLASH,
											event.getTarget().getX(),
											event.getTarget().getY(),
											event.getTarget().getZ(),
											1,
											0.0,
											0.0,
											0.0,
											0);
									if (!event.getTarget().hasEffect(MobEffects.BLINDNESS)) {
										event.getTarget().addEffect(new MobEffectInstance(MobEffects.BLINDNESS,20*7,event.getTarget().getEffect(MobEffects.GLOWING).getAmplifier()*5,true,false,false));
									}
									event.getTarget().removeEffect(MobEffects.GLOWING);
								}
								
							} else {
								// VERSO
								if (event.getTarget().hasEffect(MobEffects.BLINDNESS)) {
									int blindess_amp = event.getTarget().getEffect(MobEffects.BLINDNESS).getAmplifier();
									int blindess_dur = event.getTarget().getEffect(MobEffects.BLINDNESS).getDuration();
									event.getTarget().removeEffect(MobEffects.BLINDNESS);
									event.getTarget().addEffect(new MobEffectInstance(MobEffects.BLINDNESS,blindess_dur,blindess_amp + (5*(1+(sweeping_edge/3))),true,false,false));
								}
								
								if (event.getTarget().hasEffect(MobEffects.GLOWING)) {
									if (event.getPlayerPatch().getOriginal().hasEffect(MobEffects.INVISIBILITY)) {
										event.getPlayerPatch().getOriginal().removeEffect(MobEffects.INVISIBILITY);
									}
									event.getPlayerPatch().getOriginal().addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,20*7,0,true,false,false));
									container.getDataManager().setDataSync(TIMER, 20*7,event.getPlayerPatch().getOriginal());
									container.getDataManager().setDataSync(LUNAR_ECLIPSE_STACK, container.getDataManager().getDataValue(LUNAR_ECLIPSE_STACK) + event.getAttackDamage(),event.getPlayerPatch().getOriginal());
									((ServerLevel) event.getTarget().level).playSound(null,
											event.getTarget().getX(),
											event.getTarget().getY()+0.75f,
											event.getTarget().getZ(),
											SoundEvents.BEACON_ACTIVATE, event.getPlayerPatch().getOriginal().getSoundSource(), 4.0F, 2.0F);
									((ServerLevel) event.getPlayerPatch().getOriginal().level).sendParticles(ParticleTypes.FLASH,
											event.getTarget().getX(),
											event.getTarget().getY(),
											event.getTarget().getZ(),
											1,
											0.0,
											0.0,
											0.0,
											0);
									if (!event.getTarget().hasEffect(MobEffects.BLINDNESS)) {
										event.getTarget().addEffect(new MobEffectInstance(MobEffects.BLINDNESS,20*7,event.getTarget().getEffect(MobEffects.GLOWING).getAmplifier()*5,true,false,false));
									}
									event.getTarget().removeEffect(MobEffects.GLOWING);
								}
							}
						}
					}
				}
				if (container.getDataManager().getDataValue(CRESCENT) && event.getDamageSource().getAnimation().equals(WOMAnimations.MOONLESS_CRESCENT) && container.getDataManager().getDataValue(LUNAR_ECLIPSE_STACK) > 0	) {
					Entity player = event.getPlayerPatch().getOriginal();
					if (event.getPlayerPatch().getOriginal().hasEffect(MobEffects.INVISIBILITY)) {
						event.getPlayerPatch().getOriginal().removeEffect(MobEffects.INVISIBILITY);
					}
					int blindness_amp = 0;
					if (event.getTarget().hasEffect(MobEffects.BLINDNESS)) {
						blindness_amp = event.getTarget().getEffect(MobEffects.BLINDNESS).getAmplifier();
						event.getTarget().removeEffect(MobEffects.BLINDNESS);
					}
					int glowing_amp = 0;
					if (event.getTarget().hasEffect(MobEffects.GLOWING)) {
						glowing_amp = event.getTarget().getEffect(MobEffects.GLOWING).getAmplifier();
						event.getTarget().removeEffect(MobEffects.GLOWING);
					}
					EpicFightEntityDamageSource epicFightDamageSource = new EpicFightEntityDamageSource("lunar_eclipse", player,WOMAnimations.MOONLESS_LUNAR_ECLIPSE);
					epicFightDamageSource.setImpact(4.0f);
					epicFightDamageSource.setStunType(StunType.HOLD);
					epicFightDamageSource.addTag(SourceTags.WEAPON_INNATE);
					DamageSource damage = epicFightDamageSource;
					float lunar_eclipse_stack = container.getDataManager().getDataValue(LUNAR_ECLIPSE_STACK);
					float lunar_eclipse_damage = (float) (4f * lunar_eclipse_stack*(1f/Math.sqrt((lunar_eclipse_stack/8f)+1f)));
					float lunar_power = lunar_eclipse_damage + (lunar_eclipse_damage * ((blindness_amp)/100F));
					
					((ServerLevel) player.level).sendParticles(ParticleTypes.END_ROD,
							event.getTarget().getX(),
							event.getTarget().getY()+ 0.25 * (int) (lunar_power*(1f/Math.sqrt((lunar_power/8f)+1f))),
							event.getTarget().getZ(),
							5 * (int) (lunar_power*(1f/Math.sqrt((lunar_power/8f)+1f))),
							0.1,
							0.5 * (int) (lunar_power*(1f/Math.sqrt((lunar_power/8f)+1f))),
							0.1,
							0);
					
					AABB box = AABB.ofSize(event.getTarget().position(),10 + (Math.min(40, glowing_amp)), 10, 10 + (Math.min(40, glowing_amp)));
					List<Entity> list = event.getTarget().level.getEntities(player,box);
					
					LivingEntity livingEntityLowestHP = null;
					float distance_to_stored_target = -1;
					
					for (Entity entity : list) {
						if (entity instanceof LivingEntity) {
							LivingEntity livingEntity = (LivingEntity) entity;
							if (livingEntity.hasEffect(MobEffects.BLINDNESS)) {
								int Aoe_blindness_amp = livingEntity.getEffect(MobEffects.BLINDNESS).getAmplifier();
								int Aoe_blindness_dur = livingEntity.getEffect(MobEffects.BLINDNESS).getDuration();
								livingEntity.removeEffect(MobEffects.BLINDNESS);
								if (Aoe_blindness_dur < 20*4) {
									livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,20*4,Aoe_blindness_amp,true,false,false));
								}
							}
							
							if (livingEntity.equals(event.getTarget())){
								if (livingEntity.isAlive()) {
									livingEntity.hurt(damage,lunar_power);
									if (!livingEntity.isInvulnerable()) {
										((ServerLevel) livingEntity.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR,
												livingEntity.getX(),
												livingEntity.getY()+1,
												livingEntity.getZ(),
												(1 * (int) lunar_power),
												0.2,
												0.2,
												0.2,
												0.2);
									}
									((ServerLevel) event.getTarget().level).sendParticles(ParticleTypes.FLASH,
											livingEntity.getX(),
											livingEntity.getY()+1,
											livingEntity.getZ(),
											1,
											0.0,
											0.0,
											0.0,
											0);
									((ServerLevel) event.getTarget().level).sendParticles(ParticleTypes.END_ROD,
											livingEntity.getX(),
											livingEntity.getY()+1,
											livingEntity.getZ(),
											5 * (1 + (int) lunar_power / 10),
											0.5 * (1 + (int) lunar_power / 20),
											0.5 * (1 + (int) lunar_power / 20),
											0.5 * (1 + (int) lunar_power / 20),
											0);
								}
							} else {
								if (!(livingEntity instanceof Animal) && livingEntity.isAlive() && !(livingEntity instanceof Npc)) {
									if (livingEntityLowestHP == null) {
										livingEntityLowestHP = livingEntity;
										distance_to_stored_target = (float) Math.sqrt(
												Math.pow(livingEntity.getX() - event.getTarget().getX(), 2) + 
												Math.pow(livingEntity.getZ() - event.getTarget().getZ(), 2) + 
												Math.pow(livingEntity.getY() - event.getTarget().getY(), 2));
									} else {
										float distance_to_current_target = (float) Math.sqrt(
												Math.pow(livingEntity.getX() - event.getTarget().getX(), 2) + 
												Math.pow(livingEntity.getZ() - event.getTarget().getZ(), 2) + 
												Math.pow(livingEntity.getY() - event.getTarget().getY(), 2));
										
										if (distance_to_current_target < distance_to_stored_target) {
											livingEntityLowestHP = livingEntity;
											distance_to_stored_target = distance_to_current_target;
										}
									}
								}
							}
							if (event.getPlayerPatch() != null) {
								event.getPlayerPatch().getEventListener().triggerEvents(EventType.DEALT_DAMAGE_EVENT_POST, new DealtDamageEvent(event.getPlayerPatch(), livingEntity, epicFightDamageSource, lunar_power));
							}
						}
					}
					
					if (event.getTarget().isDeadOrDying() && livingEntityLowestHP != null) {
						Boolean no_lunarEclipse_tag = true;
						for (String tag : livingEntityLowestHP.getTags()) {
							if (tag.contains("lunar_eclipse:")) {
								no_lunarEclipse_tag = false;
								break;
							}
						}
						if (no_lunarEclipse_tag) {
							livingEntityLowestHP.addTag("lunar_eclipse:"+event.getPlayerPatch().getOriginal().getId()+":"+lunar_power*0.95f);
						}
						int lowestHP_blindness_amp = 0; 
						int lowestHP_glowing_amp = 0; 
						if (livingEntityLowestHP.hasEffect(MobEffects.BLINDNESS)) {
							lowestHP_blindness_amp = livingEntityLowestHP.getEffect(MobEffects.BLINDNESS).getAmplifier();
							livingEntityLowestHP.removeEffect(MobEffects.BLINDNESS);
						}
						if (livingEntityLowestHP.hasEffect(MobEffects.GLOWING)) {
							lowestHP_glowing_amp = livingEntityLowestHP.getEffect(MobEffects.GLOWING).getAmplifier();
							if (lowestHP_glowing_amp < glowing_amp) {
								livingEntityLowestHP.removeEffect(MobEffects.GLOWING);
								livingEntityLowestHP.addEffect(new MobEffectInstance(MobEffects.GLOWING,5,glowing_amp,true,false,false));
							} else {
								livingEntityLowestHP.removeEffect(MobEffects.GLOWING);
								livingEntityLowestHP.addEffect(new MobEffectInstance(MobEffects.GLOWING,5,lowestHP_glowing_amp,true,false,false));
							}
						} else if (glowing_amp > 0) {
							livingEntityLowestHP.addEffect(new MobEffectInstance(MobEffects.GLOWING,5,glowing_amp,true,false,false));
						}
						
						livingEntityLowestHP.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,3,lowestHP_blindness_amp,true,false,false));
					}
					container.getDataManager().setDataSync(LUNAR_ECLIPSE_STACK, 0f,event.getPlayerPatch().getOriginal());
				}
				
				if (event.getDamageSource().getAnimation().equals(WOMAnimations.MOONLESS_FULLMOON) && container.getDataManager().getDataValue(LUNAR_ECLIPSE_STACK) > 0	) {
					ServerPlayerPatch entitypatch = event.getPlayerPatch();
					AttackAnimation anim = ((AttackAnimation) event.getDamageSource().getAnimation());
					AnimationPlayer animplayer = entitypatch.getAnimator().getPlayerFor(event.getDamageSource().getAnimation());
					float elapsedTime = animplayer.getElapsedTime();
					Phase phase = anim.getPhaseByTime(elapsedTime);
					
					if (phase == anim.phases[anim.phases.length-1]) {
						Entity player = event.getPlayerPatch().getOriginal();
						if (event.getPlayerPatch().getOriginal().hasEffect(MobEffects.INVISIBILITY)) {
							event.getPlayerPatch().getOriginal().removeEffect(MobEffects.INVISIBILITY);
						}
						int blindness_amp = 0;
						if (event.getTarget().hasEffect(MobEffects.BLINDNESS)) {
							blindness_amp = event.getTarget().getEffect(MobEffects.BLINDNESS).getAmplifier();
							event.getTarget().removeEffect(MobEffects.BLINDNESS);
						}
						
						EpicFightEntityDamageSource epicFightDamageSource = new EpicFightEntityDamageSource("lunar_eclipse", player,WOMAnimations.MOONLESS_LUNAR_ECLIPSE);
						epicFightDamageSource.setImpact(4.0f);
						epicFightDamageSource.setStunType(StunType.LONG);
						epicFightDamageSource.addTag(SourceTags.WEAPON_INNATE);
						DamageSource damage = epicFightDamageSource;
						float lunar_eclipse_stack = container.getDataManager().getDataValue(LUNAR_ECLIPSE_STACK);
						float lunar_eclipse_damage = (float) (4f * lunar_eclipse_stack*(1f/Math.sqrt((lunar_eclipse_stack/8f)+1f)));
						float lunar_power = lunar_eclipse_damage + (lunar_eclipse_damage * ((blindness_amp)/100F));
						lunar_power = lunar_power * 0.7f;
						
						((ServerLevel) player.level).sendParticles(ParticleTypes.END_ROD,
								event.getTarget().getX(),
								event.getTarget().getY()+ 0.25 * (int) (lunar_power*(1f/Math.sqrt((lunar_power/8f)+1f))),
								event.getTarget().getZ(),
								5 * (int) (lunar_power*(1f/Math.sqrt((lunar_power/8f)+1f))),
								0.1,
								0.5 * (int) (lunar_power*(1f/Math.sqrt((lunar_power/8f)+1f))),
								0.1,
								0);
						
						((ServerLevel) event.getTarget().level).playSound(null,
								event.getTarget().getX(),
								event.getTarget().getY()+0.75f,
								event.getTarget().getZ(),
								SoundEvents.LIGHTNING_BOLT_IMPACT, event.getPlayerPatch().getOriginal().getSoundSource(), 2.0F, 2.0F);
						
						((ServerLevel) player.level).sendParticles(ParticleTypes.FIREWORK,
								event.getTarget().getX(),
								event.getTarget().getY()+ 0.25f,
								event.getTarget().getZ(),
								5 * (int) (lunar_power*(1f/Math.sqrt((lunar_power/8f)+1f))),
								0.0,
								0.0,
								0.0,
								0.5);
						
						AABB box = AABB.ofSize(event.getTarget().position(),15, 15, 15);
						List<Entity> list = event.getTarget().level.getEntities(player,box);
						
						for (Entity entity : list) {
							if (entity instanceof LivingEntity) {
								LivingEntity livingEntity = (LivingEntity) entity;
								if (livingEntity.hasEffect(MobEffects.BLINDNESS)) {
									int Aoe_blindness_amp = livingEntity.getEffect(MobEffects.BLINDNESS).getAmplifier();
									int Aoe_blindness_dur = livingEntity.getEffect(MobEffects.BLINDNESS).getDuration();
									livingEntity.removeEffect(MobEffects.BLINDNESS);
									if (Aoe_blindness_dur < 20*4) {
										livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,20*4,Aoe_blindness_amp,true,false,false));
									}
								}
								
								if (livingEntity.isAlive()) {
									int aoe_blindness_amp = 0;
									if (livingEntity.hasEffect(MobEffects.BLINDNESS)) {
										aoe_blindness_amp = livingEntity.getEffect(MobEffects.BLINDNESS).getAmplifier();
										livingEntity.removeEffect(MobEffects.BLINDNESS);
									}
									
									livingEntity.hurt(damage,lunar_power + (lunar_power * aoe_blindness_amp));
									((ServerLevel) event.getTarget().level).sendParticles(ParticleTypes.FLASH,
											livingEntity.getX(),
											livingEntity.getY()+1,
											livingEntity.getZ(),
											1,
											0.0,
											0.0,
											0.0,
											0);
									((ServerLevel) event.getTarget().level).sendParticles(ParticleTypes.END_ROD,
											livingEntity.getX(),
											livingEntity.getY()+1,
											livingEntity.getZ(),
											5 * (1 + (int) lunar_power / 10),
											0.5 * (1 + (int) lunar_power / 20),
											0.5 * (1 + (int) lunar_power / 20),
											0.5 * (1 + (int) lunar_power / 20),
											0);
								}
								if (event.getPlayerPatch() != null) {
									event.getPlayerPatch().getEventListener().triggerEvents(EventType.DEALT_DAMAGE_EVENT_POST, new DealtDamageEvent(event.getPlayerPatch(), livingEntity, epicFightDamageSource, lunar_power));
								}
								
								double power = 1.00;
								double d1 = event.getTarget().getX() - entity.getX();
								double d2 = event.getTarget().getY()-1 - entity.getY();
								double d0;
								
								
								if (entity instanceof ItemEntity || entity instanceof ExperienceOrb) {
									power = 0.0;
								}
								
								for (d0 = event.getTarget().getZ() - entity.getZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
									d1 = (Math.random() - Math.random()) * 0.01D;
								}
								
								if (entity instanceof LivingEntity) {
									power *= 1.0D - ((LivingEntity) entity).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
								}
								
								entity.hasImpulse = true;
								Vec3 vec3 = entity.getDeltaMovement();
								Vec3 vec31 = (new Vec3(d1, d2, d0)).normalize().scale(power);
								entity.setDeltaMovement(vec3.x / 2.0D - vec31.x, vec3.y / 2.0D - vec31.y, vec3.z / 2.0D - vec31.z);
							}
						}
						container.getDataManager().setDataSync(LUNAR_ECLIPSE_STACK, 0f,event.getPlayerPatch().getOriginal());
					}
				}
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			if (!event.getAnimation().equals(WOMAnimations.MOONLESS_LUNAR_ECHO)) {
				container.getDataManager().setDataSync(ECHO, false, event.getPlayerPatch().getOriginal());
			}
			if (!event.getAnimation().equals(WOMAnimations.MOONLESS_CRESCENT)) {
				container.getDataManager().setDataSync(CRESCENT, false, event.getPlayerPatch().getOriginal());
			} else {
				container.getDataManager().setDataSync(CRESCENT, true, event.getPlayerPatch().getOriginal());
			}
		});
		
		
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		super.executeOnServer(executer, args);
		executer.playAnimationSynchronized(WOMAnimations.MOONLESS_LUNAR_ECHO, 0);
		executer.getSkill(this).getDataManager().setDataSync(ECHO, true, executer.getOriginal());
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
			if (container.getDataManager().getDataValue(LUNAR_ECLIPSE_STACK) > 0) {
				if (container.getDataManager().getDataValue(TIMER) > 0) {
					container.getDataManager().setDataSync(TIMER, container.getDataManager().getDataValue(TIMER)-1,((ServerPlayerPatch)container.getExecuter()).getOriginal());
				} else {
					container.getDataManager().setDataSync(LUNAR_ECLIPSE_STACK,0f,((ServerPlayerPatch)container.getExecuter()).getOriginal());
				}
			} else {
				container.getDataManager().setDataSync(TIMER,0,((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
		}
	}
}