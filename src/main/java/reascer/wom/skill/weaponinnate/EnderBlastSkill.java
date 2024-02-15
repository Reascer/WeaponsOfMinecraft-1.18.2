package reascer.wom.skill.weaponinnate;

import java.util.List;
import java.util.Random;
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
import reascer.wom.gameasset.WOMSounds;
import reascer.wom.skill.WOMSkillDataKeys;
import reascer.wom.skill.WomMultipleAnimationSkill;
import reascer.wom.world.capabilities.item.WOMWeaponCategories;
import reascer.wom.world.item.WOMItems;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.network.server.SPSkillExecutionFeedback;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.SkillConsumeEvent;

public class EnderBlastSkill extends WomMultipleAnimationSkill {
	private static final UUID EVENT_UUID = UUID.fromString("b9023f5e-ee42-11ec-8ea0-0242ac120002");
	
	public EnderBlastSkill(Builder<? extends Skill> builder) {
		super(builder, (executer) -> {
			int combo = executer.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(WOMSkillDataKeys.COMBO.get());
			return combo;
			
		},  WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT_1,
			WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT_2, 
			WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT_3, 
			WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT_2_FORWARD, 
			WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT_2_LEFT, 
			WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT_2_RIGHT,
			WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT_DASH,
			WOMAnimations.ENDERBLASTER_ONEHAND_AIRSHOOT);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		
		if(!container.getExecuter().isLogicalClient()) {
			container.getDataManager().setDataSync(WOMSkillDataKeys.COMBO.get(), 0,((ServerPlayerPatch)container.getExecuter()).getOriginal());
			container.getDataManager().setDataSync(WOMSkillDataKeys.RELOAD_COOLDOWN.get(), 80,((ServerPlayerPatch)container.getExecuter()).getOriginal());
		}
		
		container.getExecuter().getEventListener().addEventListener(EventType.CLIENT_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
			if (event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WOMWeaponCategories.ENDERBLASTER &&
					container.getExecuter().getEntityState().canBasicAttack()) {
				
				event.getPlayerPatch().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
			if (event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WOMWeaponCategories.ENDERBLASTER &&
					container.getExecuter().getEntityState().canBasicAttack()) {
				
				event.getPlayerPatch().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
				if(!container.getExecuter().isLogicalClient()) {
					container.getDataManager().setDataSync(WOMSkillDataKeys.SHOOT.get(), true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				}
			}
			if(!container.getExecuter().isLogicalClient() && event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WOMWeaponCategories.ENDERBLASTER && container.getExecuter().getEntityState().canBasicAttack() ) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.COOLDOWN.get(), 40, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				container.getDataManager().setDataSync(WOMSkillDataKeys.ZOOM.get(), true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			container.getDataManager().setDataSync(WOMSkillDataKeys.RELOAD_COOLDOWN.get(), 80, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
			if (!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT_LAYED)) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.SHOOT.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
			
			if (!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT_1) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT_2) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT_3) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT_2_FORWARD) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT_2_LEFT) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT_2_RIGHT) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT_DASH) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT) &&
				!event.getAnimation().equals(WOMAnimations.ENDERBLASTER_ONEHAND_AIRSHOOT)
					) {
				if(!container.getExecuter().isLogicalClient()) {
					container.getDataManager().setDataSync(WOMSkillDataKeys.COOLDOWN.get(), 40, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(WOMSkillDataKeys.ZOOM.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				}
			}
			
			if (event.getAnimation().equals(WOMAnimations.ENDERBLASTER_ONEHAND_AIRSHOOT)) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.NOFALLDAMAGE.get(), true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				
			}
			
			if (event.getAnimation().equals(WOMAnimations.ENDERBLASTER_ONEHAND_JUMPKICK)) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.NOFALLDAMAGE.get(), true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				container.getDataManager().setDataSync(WOMSkillDataKeys.COOLDOWN.get(), 40, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				container.getDataManager().setDataSync(WOMSkillDataKeys.ZOOM.get(), true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
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
	
	@Override
	public boolean resourcePredicate(PlayerPatch<?> playerpatch) {
		float consumption = this.getDefaultConsumeptionAmount(playerpatch);
		
		SkillConsumeEvent event = new SkillConsumeEvent(playerpatch, this, this.resource, consumption*5, false);
		playerpatch.getEventListener().triggerEvents(EventType.SKILL_CONSUME_EVENT, event);
		
		if (event.isCanceled()) {
			return false;
		}
		
		if (event.getResourceType().predicate.canExecute(this, playerpatch, event.getAmount())) {
			int stack = playerpatch.getSkill(this).getStack();
			if (playerpatch.getSkill(EpicFightSkills.HYPERVITALITY) != null && stack <= 0) {
				if (playerpatch.getSkill(EpicFightSkills.HYPERVITALITY).getStack() > 0) {
					playerpatch.getSkill(EpicFightSkills.HYPERVITALITY).activate();
					playerpatch.getSkill(EpicFightSkills.HYPERVITALITY).setMaxResource(6);
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
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
				animation = horizon >= 0 ? 1 : 2;
			}
		} else {
			animation = vertic <= 0 ? -2 : 0;
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
		if ((!player.onGround() && !player.isInWater()) && (player.level().isEmptyBlock(player.blockPosition().below()) || (player.yo - player.blockPosition().getY()) > 0.2D)) {
			executer.playAnimationSynchronized(this.attackAnimations[this.attackAnimations.length - 1], 0);
		} else {
			if(executer.getOriginal().isSprinting()) {
				executer.playAnimationSynchronized(this.attackAnimations[this.attackAnimations.length - 2], 0);
			} else {
				if (i != -3 && executer.getSkill(this).getDataManager().getDataValue(WOMSkillDataKeys.COMBO.get()) >= 1) {
					executer.playAnimationSynchronized(this.attackAnimations[i+3], 0);
					executer.getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.COMBO.get(), 1, executer.getOriginal());
				} else {
					int animation = this.getAnimationInCondition(executer);
					executer.playAnimationSynchronized(this.attackAnimations[animation], 0);
					if (animation == 2) {
						double_cost = true;
					}
				}
				if (executer.getSkill(this).getDataManager().getDataValue(WOMSkillDataKeys.COMBO.get()) < 2) {
					executer.getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.COMBO.get(), executer.getSkill(this).getDataManager().getDataValue(WOMSkillDataKeys.COMBO.get())+1, executer.getOriginal());	
				}
				else {
					executer.getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.COMBO.get(), 0, executer.getOriginal());
				}
				
			}
		}
		executer.getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.COOLDOWN.get(), 40, executer.getOriginal());
		executer.getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.ZOOM.get(), true, executer.getOriginal());
		executer.getSkill(this).activate();
		if (!executer.getOriginal().isCreative()) {
			int stack = executer.getSkill(this).getStack();
			SkillConsumeEvent event = new SkillConsumeEvent(executer, this, this.resource, true);
			
			executer.getEventListener().triggerEvents(EventType.SKILL_CONSUME_EVENT, event);
			if (!event.isCanceled()) {
				event.getResourceType().consumer.consume(this, executer, event.getAmount());
				if (double_cost) {
					event.getResourceType().consumer.consume(this, executer, event.getAmount());
				}
			}
			
			int sweeping_edge = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal()) + EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getValidItemInHand(InteractionHand.OFF_HAND));
			if (Math.abs(new Random().nextInt()) % 100 < (100 * (-(1f/(Math.sqrt(sweeping_edge+1)))+1))) {
				if (double_cost && stack == 1) {
					
				} else {
					this.setStackSynchronize((ServerPlayerPatch) executer, executer.getSkill(this).getStack()+1);
				}
				executer.getOriginal().level().playSound(null, executer.getOriginal().getX(),executer.getOriginal().getY(), executer.getOriginal().getZ(),
		    			WOMSounds.ENDERBLASTER_RELOAD.get(), executer.getOriginal().getSoundSource(), 1.0F, 2.0F);
			}
		}
		executer.getSkill(this).activate();
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
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Close range shot:");
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(1), "Bullet shot:");
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
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.RELOAD_COOLDOWN.get()) == null) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.RELOAD_COOLDOWN.get(), 80,((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.RELOAD_COOLDOWN.get()) > 0) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.RELOAD_COOLDOWN.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.RELOAD_COOLDOWN.get())-1,((ServerPlayerPatch)container.getExecuter()).getOriginal());
			} else {
				container.getDataManager().setDataSync(WOMSkillDataKeys.RELOAD_COOLDOWN.get(), 80,((ServerPlayerPatch)container.getExecuter()).getOriginal());
				if (container.getExecuter().getSkill(this).getStack() < this.getMaxStack() && container.getExecuter().getOriginal().getItemInHand(InteractionHand.MAIN_HAND).getItem() == WOMItems.ENDER_BLASTER.get()) {
					if (container.getExecuter().getSkill(WOMSkills.MEDITATION) == null) {
						container.getExecuter().playAnimationSynchronized(WOMAnimations.ENDERBLASTER_ONEHAND_RELOAD, 0);
					} else {
						if (container.getExecuter().getSkill(WOMSkills.MEDITATION).getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) == 0 || container.getExecuter().getSkill(WOMSkills.MEDITATION).getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) == null) {
							container.getExecuter().playAnimationSynchronized(WOMAnimations.ENDERBLASTER_ONEHAND_RELOAD, 0);
						}
					}
				}
				
			}
		}
		if (container.getDataManager().getDataValue(WOMSkillDataKeys.COOLDOWN.get()) > 0) {
			if(container.getExecuter().isLogicalClient()) {
				if (container.getDataManager().getDataValue(WOMSkillDataKeys.ZOOM.get())) {
					ClientEngine.getInstance().renderEngine.zoomIn();
				}
			}
			if(!container.getExecuter().isLogicalClient()) {
				ServerPlayerPatch executer = (ServerPlayerPatch) container.getExecuter();
				container.getDataManager().setDataSync(WOMSkillDataKeys.COOLDOWN.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.COOLDOWN.get())-1,((ServerPlayerPatch)container.getExecuter()).getOriginal());
				if (container.getDataManager().getDataValue(WOMSkillDataKeys.NOFALLDAMAGE.get())) {
					//System.out.println(container.getDataManager().getDataValue(COOLDOWN));
					if (container.getDataManager().getDataValue(WOMSkillDataKeys.COOLDOWN.get()) > 10) {
						container.getExecuter().getOriginal().resetFallDistance();
					} else {
						container.getExecuter().getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.NOFALLDAMAGE.get(), false,((ServerPlayerPatch)container.getExecuter()).getOriginal());
					}
				}
				int sweeping_edge = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal());
				if (container.getDataManager().getDataValue(WOMSkillDataKeys.SHOOT.get()) && !container.getExecuter().getOriginal().isUsingItem() && container.getExecuter().getEntityState().canBasicAttack()) {
					container.getExecuter().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
					container.getDataManager().setDataSync(WOMSkillDataKeys.SHOOT.get(), false, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
					if (executer.getSkill(EpicFightSkills.HYPERVITALITY) != null || container.getStack() > 0 || container.getExecuter().getOriginal().isCreative() ) {
						boolean flag = true;
						if (!container.getExecuter().getOriginal().isCreative()) {
							int stack = executer.getSkill(this).getStack();
							if (executer.getSkill(EpicFightSkills.HYPERVITALITY) != null && stack <= 0) {
								if (executer.getSkill(EpicFightSkills.HYPERVITALITY).getStack() > 0) {
									executer.getSkill(EpicFightSkills.HYPERVITALITY).activate();
									executer.getSkill(EpicFightSkills.HYPERVITALITY).setMaxResource(6);
									executer.getSkill(EpicFightSkills.HYPERVITALITY).getSkill().setStackSynchronize(executer, -1);
									EpicFightNetworkManager.sendToPlayer(SPSkillExecutionFeedback.executed(executer.getSkill(EpicFightSkills.HYPERVITALITY).getSlotId()), executer.getOriginal());
								} else {
									flag = false;
								}
							}
							if (flag) {
								this.setStackSynchronize((ServerPlayerPatch) executer, executer.getSkill(this).getStack()-1);
								if (Math.abs(new Random().nextInt()) % 100 < (100 * (-(1f/(Math.sqrt(sweeping_edge+1)))+1))) {
									this.setStackSynchronize((ServerPlayerPatch) executer, executer.getSkill(this).getStack()+1);
									container.getExecuter().getOriginal().level().playSound(null, container.getExecuter().getOriginal().getX(), container.getExecuter().getOriginal().getY(), container.getExecuter().getOriginal().getZ(),
							    			WOMSounds.ENDERBLASTER_RELOAD.get(), container.getExecuter().getOriginal().getSoundSource(), 1.0F, 2.0F);
								}
							}
						}
						if (flag) {
							if (container.getExecuter().getOriginal().isVisuallySwimming()) {
							container.getExecuter().playAnimationSynchronized(WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT_LAYED, 0);
							} else {
								container.getExecuter().playAnimationSynchronized(WOMAnimations.ENDERBLASTER_ONEHAND_SHOOT, 0);
							}
						}
						
					}
					container.getDataManager().setDataSync(WOMSkillDataKeys.COOLDOWN.get(), 40, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
					container.getDataManager().setDataSync(WOMSkillDataKeys.ZOOM.get(), true, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
				} else {
					if (container.getExecuter().getOriginal().isUsingItem()) {
						container.getExecuter().getOriginal().setSprinting(false);
						container.getDataManager().setDataSync(WOMSkillDataKeys.COOLDOWN.get(), 40, ((ServerPlayerPatch)container.getExecuter()).getOriginal());
					}
				}
			}
		} else {
			if(!container.getExecuter().isLogicalClient()) {
				container.getExecuter().getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.COMBO.get(), 0,((ServerPlayerPatch)container.getExecuter()).getOriginal());
			}
			if(container.getExecuter().isLogicalClient()) {
				ClientEngine.getInstance().renderEngine.zoomOut(0);
			}
		}
	
	}
}