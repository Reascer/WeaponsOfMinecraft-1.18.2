package reascer.wom.skill;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import io.netty.buffer.Unpooled;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.skill.passive.MeditationSkill;
import reascer.wom.world.capabilities.item.WOMWeaponCategories;
import reascer.wom.world.item.WOMItems;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.SkillConsumeEvent;

public class EnderFusionSkill extends WomMultipleAnimationSkill {
	private static final SkillDataKey<Integer> COMBO = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	private static final SkillDataKey<Integer> COOLDOWN = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	private static final SkillDataKey<Integer> RELOAD_COOLDOWN = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	private static final SkillDataKey<Boolean> ZOOM = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	private static final SkillDataKey<Boolean> NOFALLDAMAGE = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	private static final SkillDataKey<Boolean> SHOOT = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	private static final SkillDataKey<Boolean> SIDE = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	private static final UUID EVENT_UUID = UUID.fromString("b9023f5e-ee42-11ec-8ea0-0242ac120002");
    private static final int cooldown = 80;
    
	public EnderFusionSkill(Builder<? extends Skill> builder) {
		super(builder, (executer) -> {
			int combo = executer.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(COMBO);
			return combo;
			
		},  WOMAnimations.ENDERBLASTER_TWOHAND_SHOOT_1, 
			WOMAnimations.ENDERBLASTER_TWOHAND_SHOOT_2, 
			WOMAnimations.ENDERBLASTER_TWOHAND_SHOOT_3,
			WOMAnimations.ENDERBLASTER_TWOHAND_SHOOT_4,
			WOMAnimations.ENDERBLASTER_TWOHAND_EVADE_LEFT,
			WOMAnimations.ENDERBLASTER_TWOHAND_EVADE_RIGHT,
			WOMAnimations.ENDERBLASTER_TWOHAND_PISTOLERO,
			WOMAnimations.ENDERBLASTER_TWOHAND_AIRSHOOT);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		container.getDataManager().registerData(SIDE);
		container.getDataManager().registerData(SHOOT);
		container.getDataManager().registerData(COMBO);
		container.getDataManager().registerData(COOLDOWN);
		container.getDataManager().registerData(RELOAD_COOLDOWN);
		container.getDataManager().registerData(ZOOM);
		container.getDataManager().registerData(NOFALLDAMAGE);
		
		container.getExecuter().getEventListener().addEventListener(EventType.CLIENT_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
			if (event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WOMWeaponCategories.ENDERBLASTER && container.getExecuter().getEntityState().canBasicAttack()) {
				event.getPlayerPatch().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
			if (event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WOMWeaponCategories.ENDERBLASTER && container.getExecuter().getEntityState().canBasicAttack()) {
				event.getPlayerPatch().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
				if(!container.getExecuter().isLogicalClient()) {
					container.getDataManager().setDataSync(SHOOT, true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				}
			}
			if(!container.getExecuter().isLogicalClient() && event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WOMWeaponCategories.ENDERBLASTER && container.getExecuter().getEntityState().canBasicAttack()) {
				container.getDataManager().setDataSync(COOLDOWN, cooldown, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				container.getDataManager().setDataSync(ZOOM, true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			container.getDataManager().setDataSync(RELOAD_COOLDOWN, 80, event.getPlayerPatch().getOriginal());
			if (!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_TWOHAND_SHOOT_RIGHT) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_TWOHAND_SHOOT_LEFT) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_TWOHAND_SHOOT_LAYED_RIGHT) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_TWOHAND_SHOOT_LAYED_LEFT)) {
				container.getDataManager().setDataSync(SHOOT, false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
			if (!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_TWOHAND_SHOOT_1) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_TWOHAND_SHOOT_2) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_TWOHAND_SHOOT_3) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_TWOHAND_SHOOT_4) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_TWOHAND_PISTOLERO) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_TWOHAND_SHOOT_LEFT) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_TWOHAND_SHOOT_RIGHT) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_TWOHAND_AIRSHOOT)
					) {
				container.getDataManager().setDataSync(COOLDOWN, cooldown, event.getPlayerPatch().getOriginal());
				container.getDataManager().setDataSync(ZOOM, false, event.getPlayerPatch().getOriginal());
			}
			
		});
		
	}

	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
		container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.CLIENT_ITEM_USE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public FriendlyByteBuf gatherArguments(LocalPlayerPatch executer, ControllEngine controllEngine) {
        int forward = controllEngine.isKeyDown(Minecraft.getInstance().options.keyUp) ? 1 : 0;
        int backward = controllEngine.isKeyDown(Minecraft.getInstance().options.keyDown) ? -1 : 0;
        int left = controllEngine.isKeyDown(Minecraft.getInstance().options.keyLeft) ? 1 : 0;
        int right = controllEngine.isKeyDown(Minecraft.getInstance().options.keyRight) ? -1 : 0;
		
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeInt(forward);
		buf.writeInt(backward);
		buf.writeInt(left);
		buf.writeInt(right);
		
		return buf;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public Object getExecutionPacket(LocalPlayerPatch executer, FriendlyByteBuf args) {
		int forward = args.readInt();
		int backward = args.readInt();
		int left = args.readInt();
		int right = args.readInt();
		int vertic = forward + backward;
		int horizon = left + right;
		int animation;
		
		if (vertic == 0) {
			if (horizon == 0) {
				animation = -3;
			} else {
				animation = horizon >= 0 ? 0 : 1;
			}
		} else {
			animation = vertic <= 0 ? -3 : -3;
		}
		
		CPExecuteSkill packet = new CPExecuteSkill(executer.getSkill(this).getSlotId());
		packet.getBuffer().writeInt(animation);
		
		return packet;
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		int i = args.readInt();
		boolean double_cost = false;
		ServerPlayer player = executer.getOriginal();
		if ((!player.onGround() && !player.isInWater()) && player.fallDistance < 0.1f && (player.level().isEmptyBlock(player.blockPosition().below()) || (player.yo - player.blockPosition().getY()) > 0.2D)) {
			executer.playAnimationSynchronized(this.attackAnimations[this.attackAnimations.length - 1], 0);
			executer.getSkill(this).getDataManager().setDataSync(NOFALLDAMAGE, true, executer.getOriginal());
			executer.getSkill(this).getDataManager().setDataSync(COOLDOWN, cooldown+40, executer.getOriginal());
			double_cost = true;
		} else {
			if(executer.getOriginal().isSprinting()) {
				executer.playAnimationSynchronized(this.attackAnimations[this.attackAnimations.length - 2], 0);
				executer.getSkill(this).getDataManager().setDataSync(NOFALLDAMAGE, true, executer.getOriginal());
				executer.getSkill(this).getDataManager().setDataSync(COOLDOWN, cooldown+40, executer.getOriginal());
				double_cost = true;
			} else {
				if (i != -3) {
					executer.playAnimationSynchronized(this.attackAnimations[i+4], 0);
					executer.getSkill(this).getDataManager().setDataSync(COMBO, executer.getSkill(this).getDataManager().getDataValue(COMBO)-1, executer.getOriginal());
				} else {
					int animation = this.getAnimationInCondition(executer);
					executer.playAnimationSynchronized(this.attackAnimations[animation], 0);
					if (animation == 1 || animation == 3) {
						double_cost = true;
					}
				}
				if (executer.getSkill(this).getDataManager().getDataValue(COMBO) < 3) {
					if (executer.getSkill(this).getDataManager().getDataValue(COMBO) == 1) {
						executer.getSkill(this).getDataManager().setDataSync(COOLDOWN, cooldown+40, executer.getOriginal());
					}
					executer.getSkill(this).getDataManager().setDataSync(COMBO, executer.getSkill(this).getDataManager().getDataValue(COMBO)+1, executer.getOriginal());	
				}
				else {
					executer.getSkill(this).getDataManager().setDataSync(COMBO, 0, executer.getOriginal());
				}
				
			}
		}
		if (executer.getSkill(this).getDataManager().getDataValue(COOLDOWN) < cooldown) {
			executer.getSkill(this).getDataManager().setDataSync(COOLDOWN, cooldown, executer.getOriginal());
		}
		executer.getSkill(this).getDataManager().setDataSync(ZOOM, true, executer.getOriginal());
		if (!executer.getOriginal().isCreative()) {
			float ressource = executer.getSkill(this).getResource();
			int sweeping_edge = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal()) + EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getValidItemInHand(InteractionHand.OFF_HAND));
			sweeping_edge = 1/((sweeping_edge/6)+1);
			float consumption = ((24f * (double_cost?2f:1f)) * sweeping_edge);
			if (ressource - consumption < 0.0f) {
				consumption = -consumption;
				consumption += ressource;
				//System.out.println("c :"+ consumption + " | r :"+ ressource );
				while (consumption < 0.0f) {
					SkillConsumeEvent event = new SkillConsumeEvent(executer, this, this.resource, true);
					executer.getEventListener().triggerEvents(EventType.SKILL_CONSUME_EVENT, event);
					if (!event.isCanceled()) {
						event.getResourceType().consumer.consume(this, executer, event.getAmount());
					}
					consumption += 12.0f;
				}
				if (consumption == 0.0f) {
					this.setStackSynchronize((ServerPlayerPatch) executer, executer.getSkill(this).getStack() + 1);
				}
				this.setConsumptionSynchronize((ServerPlayerPatch) executer,consumption);
			} else {
				this.setConsumptionSynchronize((ServerPlayerPatch) executer,ressource - consumption);				
			}
		}
		executer.getSkill(this).activate();
	}
	
	@Override
	public boolean canExecute(PlayerPatch<?> executer) {
		if (executer.isLogicalClient()) {
			return (executer.getSkill(this) != null ? executer.getSkill(this).getStack() >= 0 : true) || executer.getOriginal().isCreative();
		} else {
			ItemStack itemstack = executer.getOriginal().getMainHandItem();
			boolean double_cost = false;
			if (executer.getOriginal().isSprinting()) {
				double_cost = true;
			}
			ServerPlayer player = (ServerPlayer) executer.getOriginal();
			if ((!player.onGround() && !player.isInWater()) && player.fallDistance < 0.1f && (player.level().isEmptyBlock(player.blockPosition().below()) || (player.yo - player.blockPosition().getY()) > 0.2D)) {
				double_cost = true;
			}
			int combo = executer.getSkill(this).getDataManager().getDataValue(COMBO);
			if ((combo == 1 || combo == 3)) {
				double_cost = true;
			}
			float ressource = executer.getSkill(this).getResource();
			int sweeping_edge = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal()) + EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getValidItemInHand(InteractionHand.OFF_HAND));
			float skill_consumption = ((24f * (double_cost?2f:1f)) * (1f - sweeping_edge/12f));
			float total_amount_of_ressources = ressource + 12f * executer.getSkill(this).getStack();
			//System.out.println(total_amount_of_ressources + " | " + consumption + " | r :"+ ressource);
			boolean Hypervitality = false;
			if (executer.getSkill(EpicFightSkills.HYPERVITALITY) != null) {
				float stamina_ressouce = executer.getStamina();
				if (total_amount_of_ressources > skill_consumption-12f) {
					if (executer.getSkill(EpicFightSkills.HYPERVITALITY).getResource() == 0) {
						if (total_amount_of_ressources - skill_consumption < 0) {
							if (stamina_ressouce > 0) {
								if (stamina_ressouce + total_amount_of_ressources > skill_consumption) {
									Hypervitality = true;
								}
							} else {
								if (executer.getSkill(EpicFightSkills.FORBIDDEN_STRENGTH) != null) {
									Hypervitality = true;
								}
								
							}
						}
					}
				}
			}
			return (total_amount_of_ressources >= skill_consumption || executer.getOriginal().isCreative() || Hypervitality) && EpicFightCapabilities.getItemStackCapability(itemstack).getInnateSkill(executer, itemstack) == this && executer.getOriginal().getVehicle() == null && (!executer.getSkill(this).isActivated() || this.activateType == ActivateType.TOGGLE);
		}
	}
	
	@Override
	public boolean isExecutableState(PlayerPatch<?> executer) {
		executer.updateEntityState();
		EntityState playerState = executer.getEntityState();
		return !(executer.getOriginal().isFallFlying() || executer.currentLivingMotion == LivingMotions.FALL || !playerState.canUseSkill() || !executer.getEntityState().canBasicAttack());
	}
	
	@Override
	public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
		List<Component> list = Lists.<Component>newArrayList();
		String traslatableText = this.getTranslationKey();
		
		list.add(Component.translatable(traslatableText).withStyle(ChatFormatting.WHITE).append(Component.literal(String.format("[%.0f]", this.consumption)).withStyle(ChatFormatting.AQUA)));
		list.add(Component.translatable(traslatableText + ".tooltip").withStyle(ChatFormatting.DARK_GRAY));
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Close range shots:");
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(1), "Ender Bullets:");
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(2), "Laser beam:");
		
		return list;
	}
	
	@Override
	public WeaponInnateSkill registerPropertiesToAnimation() {
		return this;
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		super.updateContainer(container);
		if(!container.getExecuter().isLogicalClient()) {
			if (container.getDataManager().getDataValue(RELOAD_COOLDOWN) == null) {
				container.getDataManager().setDataSync(RELOAD_COOLDOWN, 80,((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
			if (container.getDataManager().getDataValue(RELOAD_COOLDOWN) > 0) {
				container.getDataManager().setDataSync(RELOAD_COOLDOWN, container.getDataManager().getDataValue(RELOAD_COOLDOWN)-1,((ServerPlayerPatch)container.getExecuter()).getOriginal());
			} else {
				container.getDataManager().setDataSync(RELOAD_COOLDOWN, 80,((ServerPlayerPatch)container.getExecuter()).getOriginal());
				if (container.getExecuter().getSkill(this).getStack() < this.getMaxStack() && container.getExecuter().getOriginal().getItemInHand(InteractionHand.MAIN_HAND).getItem() == WOMItems.ENDER_BLASTER.get()) {
					if (container.getExecuter().getSkill(WOMSkills.MEDITATION) == null) {
						container.getExecuter().playAnimationSynchronized(WOMAnimations.ENDERBLASTER_TWOHAND_RELOAD, 0);
					} else {
						if (container.getExecuter().getSkill(WOMSkills.MEDITATION).getDataManager().getDataValue(MeditationSkill.TIMER) == 0 || container.getExecuter().getSkill(WOMSkills.MEDITATION).getDataManager().getDataValue(MeditationSkill.TIMER) == null) {
							container.getExecuter().playAnimationSynchronized(WOMAnimations.ENDERBLASTER_TWOHAND_RELOAD, 0);
						}
					}
				}
			}
		}
		if (container.getDataManager().getDataValue(COOLDOWN) > 0) {
			if(container.getExecuter().isLogicalClient()) {
				if (container.getDataManager().getDataValue(ZOOM)) {
					ClientEngine.getInstance().renderEngine.zoomIn();
				}
			}
			if(!container.getExecuter().isLogicalClient()) {
				ServerPlayerPatch executer = (ServerPlayerPatch) container.getExecuter();
				container.getDataManager().setDataSync(COOLDOWN, container.getDataManager().getDataValue(COOLDOWN)-1,((ServerPlayerPatch)container.getExecuter()).getOriginal());
				if (container.getDataManager().getDataValue(NOFALLDAMAGE)) {
					//System.out.println(container.getDataManager().getDataValue(COOLDOWN));
					if (container.getDataManager().getDataValue(COOLDOWN) > 10) {
						container.getExecuter().getOriginal().resetFallDistance();
					} else {
						container.getExecuter().getSkill(this).getDataManager().setDataSync(NOFALLDAMAGE, false,((ServerPlayerPatch)container.getExecuter()).getOriginal());
					}
				}
				int sweeping_edge = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal()) + EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getValidItemInHand(InteractionHand.OFF_HAND));
				if (container.getDataManager().getDataValue(SHOOT) && !container.getExecuter().getOriginal().isUsingItem() && container.getExecuter().getEntityState().canBasicAttack()) {
					container.getExecuter().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
					container.getDataManager().setDataSync(SHOOT, false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
					if (((container.getResource() >= (12f * (1f - sweeping_edge/12f)) && container.getStack() == 0) || container.getStack() > 0) || container.getExecuter().getOriginal().isCreative() ) {
						if (!container.getExecuter().getOriginal().isCreative()) {
							float ressource = container.getResource();
							float consumption = ressource - (12f * (1f - sweeping_edge/12f));
							if (consumption < 0.0f && consumption > -12.0f) {
								this.setStackSynchronize((ServerPlayerPatch) executer, executer.getSkill(this).getStack()-1);
								this.setConsumptionSynchronize((ServerPlayerPatch) executer,12f + consumption);
							} else if (consumption == 0.0f){
								this.setStackSynchronize((ServerPlayerPatch) executer, executer.getSkill(this).getStack()+1);
								this.setConsumptionSynchronize((ServerPlayerPatch) executer,consumption);
							} else if (consumption == -12.0f){
								this.setConsumptionSynchronize((ServerPlayerPatch) executer,0.0f);
								
							} else {
								this.setConsumptionSynchronize((ServerPlayerPatch) executer,consumption);				
							}
						}
						if (container.getExecuter().getOriginal().isVisuallySwimming()) {
							if (container.getDataManager().getDataValue(SIDE)) {
								container.getExecuter().playAnimationSynchronized(WOMAnimations.ENDERBLASTER_TWOHAND_SHOOT_LAYED_RIGHT, 0);
								container.getDataManager().setDataSync(SIDE, false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
							} else {
								container.getExecuter().playAnimationSynchronized(WOMAnimations.ENDERBLASTER_TWOHAND_SHOOT_LAYED_LEFT, 0);
								container.getDataManager().setDataSync(SIDE, true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
							}
						} else {
							if (container.getDataManager().getDataValue(SIDE)) {
								container.getExecuter().playAnimationSynchronized(WOMAnimations.ENDERBLASTER_TWOHAND_SHOOT_RIGHT, 0);
								container.getDataManager().setDataSync(SIDE, false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
							} else {
								container.getExecuter().playAnimationSynchronized(WOMAnimations.ENDERBLASTER_TWOHAND_SHOOT_LEFT, 0);
								container.getDataManager().setDataSync(SIDE, true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
							}
							
						}
					}
					container.getDataManager().setDataSync(COOLDOWN, cooldown, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(ZOOM, true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				} else {
					if (container.getExecuter().getOriginal().isUsingItem()) {
						container.getExecuter().getOriginal().setSprinting(false);
						container.getDataManager().setDataSync(COOLDOWN, cooldown, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
					}
				}
				
			}
		} else {
			if(!container.getExecuter().isLogicalClient()) {
				container.getExecuter().getSkill(this).getDataManager().setDataSync(COMBO, 0,((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
			if(container.getExecuter().isLogicalClient()) {
				ClientEngine.getInstance().renderEngine.zoomOut(0);
			}
		}
	}
}