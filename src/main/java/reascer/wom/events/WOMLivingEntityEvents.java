package reascer.wom.events;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.WOMEnchantment;
import reascer.wom.main.WeaponsOfMinecraft;
import reascer.wom.particle.WOMParticles;
import reascer.wom.wold.gamerules.WOMGamerules;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.SourceTags;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.eventlistener.DealtDamageEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.item.EpicFightItems;

@Mod.EventBusSubscriber(modid = WeaponsOfMinecraft.MODID , bus = EventBusSubscriber.Bus.FORGE)
public class WOMLivingEntityEvents {
	private static final Map<EquipmentSlot,UUID> STAMINAR_ADD = makeUUIDMap(WeaponsOfMinecraft.MODID + "_staminar_add");
	
	/** Generates a UUID map for all slot types from a string key */
	private static Map<EquipmentSlot,UUID> makeUUIDMap(String key) {
		Map<EquipmentSlot,UUID> map = new EnumMap<>(EquipmentSlot.class);
		for (EquipmentSlot type : EquipmentSlot.values()) {
			map.put(type, UUID.nameUUIDFromBytes((key + type.getName()).getBytes()));
		}
		return map;
	}
	
	@SubscribeEvent
	public static void onSpawnEvent(MobSpawnEvent event) {
		EntityPatch<Entity> entitypatch = EpicFightCapabilities.getEntityPatch(event.getEntity(), EntityPatch.class);
		
		if (entitypatch != null && entitypatch.isInitialized() && !event.getEntity().getTags().contains("wom-bow-replaced")) {
			if ((event.getEntity() instanceof Skeleton) && (event.getEntity().getMainHandItem().getItem() == Items.BOW) && !event.getEntity().getTags().contains("wom-bow-replaced")) {
				ItemStack weapon = Items.BOW.getDefaultInstance();
				boolean no_change = true;
				if (new Random().nextInt() % 100 < event.getEntity().level().getGameRules().getInt(WOMGamerules.SKELETON_MELEE_PERCENTAGE)) {
					no_change = false;
					switch (Math.abs(new Random().nextInt()) % 4) {
						case 0:
							weapon = Items.STONE_SWORD.getDefaultInstance();
							break;
						case 1:
							weapon = Items.STONE_AXE.getDefaultInstance();
							break;
						case 2:
							weapon = EpicFightItems.STONE_SPEAR.get().getDefaultInstance();
							break;
						case 3:
							weapon = EpicFightItems.STONE_GREATSWORD.get().getDefaultInstance();
							break;
					}
				}
				
				if (!no_change) {
					event.getEntity().setItemInHand(InteractionHand.MAIN_HAND, weapon);
				}
				event.getEntity().addTag("wom-bow-replaced");
			}
			float distance_from_zero = (float) Math.sqrt(Math.pow(event.getX(), 2) + Math.pow(event.getZ(), 2));
			if (distance_from_zero / 1000 > 1 && !event.getEntity().getTags().contains("wom-stronger-mob") && event.getEntity().level().getGameRules().getBoolean(WOMGamerules.SPAWN_STONGER_MOB_OVER_DISTANCE)) {
				AttributeInstance entity_max_health = event.getEntity().getAttribute(Attributes.MAX_HEALTH);
				AttributeModifier boosted_health = new AttributeModifier(UUID.fromString("5a70f02c-7ca0-43c5-a766-2be3d68461a2"), "wom.wom_stronger_health", Math.round(Math.pow(1.5D, (distance_from_zero / 1000))-1) , Operation.MULTIPLY_TOTAL);
				if (entity_max_health != null) {
					entity_max_health.removeModifier(boosted_health);
					entity_max_health.addPermanentModifier(boosted_health);
				}
				AttributeInstance entity_attack_damage = event.getEntity().getAttribute(Attributes.ATTACK_DAMAGE);
				AttributeModifier boosted_damage = new AttributeModifier(UUID.fromString("5a70f02c-7ca0-43c5-a766-2be3d68461a2"), "wom.wom_stronger_damage", Math.round(Math.pow(1.2D, (distance_from_zero / 1000))-1), Operation.MULTIPLY_TOTAL);
				if (entity_attack_damage != null) {
					entity_attack_damage.removeModifier(boosted_damage);
					entity_attack_damage.addPermanentModifier(boosted_damage);
				}
				
				event.getEntity().heal(event.getEntity().getMaxHealth());
				event.getEntity().addTag("wom-stronger-mob");
			}
		}
	}
	
	@SubscribeEvent
	public static void onkillEvent(LivingDeathEvent event) {
		if (!(event.getEntity() instanceof Player) && !(event.getEntity() instanceof Animal) && !(event.getEntity() instanceof Npc) && event.getEntity().level().getGameRules().getBoolean(WOMGamerules.STONGER_MOB_DROP_EMERALDS)) {
			for (int i = 1; i < event.getEntity().getMaxHealth()/20; i++) {
				if (new Random().nextInt() % 4 == 0) {
					double d0 = (double)EntityType.ITEM.getWidth();
					double d1 = 1.0D - d0;
  					double d2 = d0 / 2.0D;
  					double d3 = Math.floor(event.getEntity().getX()) + new Random().nextDouble() * d1 + d2;
  					double d4 = Math.floor(event.getEntity().getY()) + new Random().nextDouble() * d1;
  					double d5 = Math.floor(event.getEntity().getZ()) + new Random().nextDouble() * d1 + d2;
  					
  					ItemStack itemStack = Items.EMERALD.getDefaultInstance();
			    	ItemEntity itementity = new ItemEntity(event.getEntity().level(), d3, d4, d5, itemStack);
			       	itementity.setDefaultPickUpDelay();
			       	itementity.setDeltaMovement( new Random().nextGaussian() * (double)0.05F,  new Random().nextGaussian() * (double)0.05F + (double)0.2F,  new Random().nextGaussian() * (double)0.05F);
			       	event.getEntity().level().addFreshEntity(itementity);
			    }
			}
		}
		for (String tag : event.getEntity().getTags()) {
			event.getEntity().removeTag(tag);
			break;
		}
	}
	
	@SubscribeEvent
	public static void onDropedExpPoint(LivingExperienceDropEvent event) {
		if (!(event.getEntity() instanceof Player) && event.getEntity().level().getGameRules().getBoolean(WOMGamerules.STONGER_MOB_GIVE_MORE_EXP)) {
			event.setDroppedExperience((int) (event.getDroppedExperience() * (event.getEntity().getMaxHealth()/20)));
		}
	}
	
	@SubscribeEvent
	public static void itemAttributeModifiers(ItemAttributeModifierEvent event) {
		// must be in the right slot
		float invigoration = 0;
		ItemStack stack = event.getItemStack();
		EquipmentSlot slot = event.getSlotType();
		if (slot == LivingEntity.getEquipmentSlotForItem(stack)) {
			// boost from enchant
			invigoration += stack.getEnchantmentLevel(WOMEnchantment.INVIGORATION.get());
			//System.out.println("in. \n invig lvl = "+ invigoration);
		}
		//System.out.println("out. \n Stack:"+LivingEntity.getEquipmentSlotForItem(stack)+" Slot:"+slot.getName()+"\n");
		// add the attributes if we have any changes
		if (invigoration != 0) event.addModifier(EpicFightAttributes.MAX_STAMINA.get(), new AttributeModifier(STAMINAR_ADD.get(slot), "invigoration_stamina_add", invigoration*2, Operation.ADDITION));
	}
	
	@SubscribeEvent
	public static void onUpdateEvent(LivingEvent.LivingTickEvent event) {
		Entity e = event.getEntity();
		
		if (e instanceof Player) {
			for (String tag : event.getEntity().getTags()) {
				if (tag.contains("wom_health_fix:")) {
					if (Float.valueOf(tag.substring(15)) <= event.getEntity().getMaxHealth()) {
						event.getEntity().setHealth(Float.valueOf(tag.substring(15)));
						event.getEntity().getTags().remove(tag);
					}
					break;
				}
			}
			if (event.getEntity().getHealth() > event.getEntity().getMaxHealth()) {
				event.getEntity().setHealth(event.getEntity().getMaxHealth());
			}
		}
		for (String tag : event.getEntity().getTags()) {
			if (tag.contains("anti_stunlock:")) {
				if (e.tickCount - Float.valueOf(tag.split(":")[2]) > 40) {
					e.removeTag(tag);
					break;
				}
			}
		}
		for (String tag : event.getEntity().getTags()) {
			if (tag.contains("timed_katana_slashes:")) {
				if (e != null) {
					if(e.isAlive()) {
						if (Integer.valueOf(tag.split(":")[1]) > 0) {
							String replacetag = new String(tag); 
							e.removeTag(tag);
							e.addTag("timed_katana_slashes:"+
									(Integer.valueOf(replacetag.split(":")[1])-1)+":"+
									Integer.valueOf(replacetag.split(":")[2])+":"+
									Integer.valueOf(replacetag.split(":")[3])+":"+
									Integer.valueOf(replacetag.split(":")[4])+":"+
									Float.valueOf(replacetag.split(":")[5])+":"+
									Integer.valueOf(replacetag.split(":")[6])+":"+
									Integer.valueOf(replacetag.split(":")[7]) // max attack
									);
							break;
						} else {
							if (Integer.valueOf(tag.split(":")[2]) > 0) {
								String replacetag = new String(tag); 
								e.removeTag(tag);
								e.addTag("timed_katana_slashes:"+
										Integer.valueOf(replacetag.split(":")[1])+":"+
										(Integer.valueOf(replacetag.split(":")[2])-1)+":"+
										Integer.valueOf(replacetag.split(":")[3])+":"+
										Integer.valueOf(replacetag.split(":")[4])+":"+
										Float.valueOf(replacetag.split(":")[5])+":"+
										Integer.valueOf(replacetag.split(":")[6])+":"+
										Integer.valueOf(replacetag.split(":")[7]) // max attack
										);
								break;
							} else {
								if (Integer.valueOf(tag.split(":")[3]) > 0) {
									String replacetag = new String(tag); 
									e.removeTag(tag);
									ServerPlayerPatch player = EpicFightCapabilities.getEntityPatch(e.level().getEntity(Integer.valueOf(replacetag.split(":")[6])), ServerPlayerPatch.class);
									EpicFightDamageSource epicFightDamageSource = player.getDamageSource(WOMAnimations.KATANA_SAKURA_TIMED_SLASH, InteractionHand.MAIN_HAND);
									
									epicFightDamageSource.setImpact(2.0f);
									epicFightDamageSource.setStunType(StunType.HOLD);
									epicFightDamageSource.addTag(SourceTags.WEAPON_INNATE);
									DamageSource damage = epicFightDamageSource;
									e.invulnerableTime = 0;
									if (e.hurt(damage,(float) Math.max(1.0f, Float.valueOf(replacetag.split(":")[5]) * 0.25f))) {
										player.getEventListener().triggerEvents(EventType.DEALT_DAMAGE_EVENT_POST, new DealtDamageEvent(player, (LivingEntity) e, epicFightDamageSource, (float) Math.max(1.0f, Float.valueOf(replacetag.split(":")[5]) * 0.25f)));
										((ServerLevel) event.getEntity().level()).playSound(null,
												event.getEntity().getX(),
												event.getEntity().getY()+0.75f,
												event.getEntity().getZ(),
												EpicFightSounds.BLADE_HIT.get(), event.getEntity().getSoundSource(), 1.0F, 1.0F);
										WOMParticles.KATANA_SHEATHED_HIT.get().spawnParticleWithArgument(((ServerLevel) e.level()), null, null, e, player.getOriginal());
									}
									e.addTag("timed_katana_slashes:"+
											Integer.valueOf(replacetag.split(":")[1])+":"+ // Timer
											Integer.valueOf(replacetag.split(":")[4])+":"+ // frequency
											(Integer.valueOf(replacetag.split(":")[3])-1)+":"+ // attack
											Integer.valueOf(replacetag.split(":")[4])+":"+ // frequency max
											Float.valueOf(replacetag.split(":")[5])+":"+ // damage
											Integer.valueOf(replacetag.split(":")[6])+":"+ // player
											Integer.valueOf(replacetag.split(":")[7]) // max attack
									);
									break;
								} else {
									e.removeTag(tag);
									break;
								}
							}
						}
					}
				}
			}
		}
		
		for (String tag : event.getEntity().getTags()) {
			if (tag.contains("lunar_eclipse:")) {
				if (event.getEntity().hasEffect(MobEffects.BLINDNESS)) {
					int blindness_amp = event.getEntity().getEffect(MobEffects.BLINDNESS).getAmplifier();
					if (event.getEntity().getEffect(MobEffects.BLINDNESS).getDuration() == 1 || event.getEntity().isDeadOrDying()) {
						Entity player = event.getEntity().level().getEntity(Integer.valueOf(tag.substring(14)));
						PlayerPatch<?> playerpatch = EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
						ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch) playerpatch;
						EpicFightDamageSource epicFightDamageSource = serverPlayerPatch.getDamageSource(WOMAnimations.MOONLESS_LUNAR_ECHO, InteractionHand.MAIN_HAND);
						epicFightDamageSource.setImpact(4.0f);
						epicFightDamageSource.setStunType(StunType.HOLD);
						epicFightDamageSource.addTag(SourceTags.WEAPON_INNATE);
						DamageSource damage = epicFightDamageSource;
						((ServerLevel) event.getEntity().level()).sendParticles(ParticleTypes.END_ROD,
								event.getEntity().getX(),
								event.getEntity().getY()+ 0.25 * blindness_amp,
								event.getEntity().getZ(),
								5 * blindness_amp,
								0.1,
								0.5 * blindness_amp,
								0.1,
								0);
						((ServerLevel) event.getEntity().level()).sendParticles(ParticleTypes.FLASH,
								event.getEntity().getX(),
								event.getEntity().getY(),
								event.getEntity().getZ(),
								1,
								0.0,
								0.0,
								0.0,
								0);
						((ServerLevel) event.getEntity().level()).playSound(null,
								event.getEntity().getX(),
								event.getEntity().getY()+0.75f,
								event.getEntity().getZ(),
								blindness_amp == 0 ? SoundEvents.BEACON_ACTIVATE : SoundEvents.BEACON_DEACTIVATE, event.getEntity().getSoundSource(), 4.0F, 2.0F);
						int glowing_amp = 0;
						if (event.getEntity().hasEffect(MobEffects.GLOWING)) {
							glowing_amp = event.getEntity().getEffect(MobEffects.GLOWING).getAmplifier();
						}
						AABB box = AABB.ofSize(event.getEntity().position(),10 + (Math.min(90, 1 * glowing_amp)), 10, 10 + (Math.min(90, 1 * glowing_amp)));
						
						List<Entity> list = event.getEntity().level().getEntities(event.getEntity().level().getEntity(Integer.valueOf(tag.substring(14))),box);
						for (Entity entity : list) {
							if (entity instanceof LivingEntity) {
								LivingEntity livingEntity = (LivingEntity) entity;
								
								if (livingEntity.isAlive()) {
									if (blindness_amp > 0) {
										boolean particle = true;
										if (livingEntity.equals(event.getEntity())){
											particle = livingEntity.hurt(damage,2 * blindness_amp);
										} else {
											particle = livingEntity.hurt(damage,1 * blindness_amp);
										}
										if (particle) {
											((ServerLevel) livingEntity.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR,
													livingEntity.getX(),
													livingEntity.getY()+1,
													livingEntity.getZ(),
													(1 * blindness_amp),
													0.2,
													0.2,
													0.2,
													0.2);
										}
										
										((ServerLevel) event.getEntity().level()).sendParticles(ParticleTypes.END_ROD,
												livingEntity.getX(),
												livingEntity.getY()+1,
												livingEntity.getZ(),
												5 * (1 + blindness_amp / 10),
												0.5 * (1 + blindness_amp / 20),
												0.5 * (1 + blindness_amp / 20),
												0.5 * (1 + blindness_amp / 20),
												0);
									}
									if (serverPlayerPatch != null) {
										serverPlayerPatch.getEventListener().triggerEvents(EventType.DEALT_DAMAGE_EVENT_POST, new DealtDamageEvent(serverPlayerPatch, livingEntity, epicFightDamageSource, 1 * blindness_amp));
									} else {
										event.getEntity().getTags().remove(tag);
										break;
									}
								}
							}
						}
						event.getEntity().removeEffect(MobEffects.BLINDNESS);
						event.getEntity().getTags().remove(tag);
					}
				}
				break;
			}
		}
	}
	
	@SubscribeEvent
	public static void onleaveEvent(EntityLeaveLevelEvent event) {
		Entity e = event.getEntity();
		if (e instanceof Player) {
			Player p = (Player) e;
			for (String tag : p.getTags()) {
				if (tag.contains("wom_health_fix:")) {
					p.getTags().remove(tag);
					break;
				}
			}
			if (p.isAlive()) {
				p.addTag("wom_health_fix:"+p.getHealth());
			}
		}
	}
	
	@SubscribeEvent
	public static void onDisconectEvent(PlayerLoggedOutEvent event) {
		Entity e = event.getEntity();
		if (e instanceof Player) {
			Player p = (Player) e;
			for (String tag : p.getTags()) {
				if (tag.contains("wom_health_fix:")) {
					p.getTags().remove(tag);
					break;
				}
			}
			if (p.isAlive()) {
				p.addTag("wom_health_fix:"+p.getHealth());
			}
		}
	}
}
