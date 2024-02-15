package reascer.wom.skill.dodges;

import java.util.Random;
import java.util.UUID;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.skill.WOMSkillDataKeys;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.dodge.DodgeSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.DodgeSuccessEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class DodgeMasterSkill extends DodgeSkill {
	private static final UUID EVENT_UUID = UUID.fromString("691d9d1e-05ce-11ed-b939-0242ac120002");
	
	public DodgeMasterSkill(Builder builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_PRE, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) > 4) {
				if (!container.getDataManager().getDataValue(WOMSkillDataKeys.DODGE.get())) {
					event.getPlayerPatch().getEventListener().triggerEvents(EventType.DODGE_SUCCESS_EVENT, new DodgeSuccessEvent(event.getPlayerPatch(), event.getDamageSource()));
					container.getDataManager().setDataSync(WOMSkillDataKeys.DODGE.get(), true,event.getPlayerPatch().getOriginal());
				}
				event.setCanceled(true);
				event.setResult(ResultType.MISSED);
			}
        });
		
		container.getExecuter().getEventListener().addEventListener(EventType.BASIC_ATTACK_EVENT, EVENT_UUID, (event) -> {
					container.getDataManager().setDataSync(WOMSkillDataKeys.DODGE.get(), false,event.getPlayerPatch().getOriginal());
					container.getDataManager().setDataSync(WOMSkillDataKeys.TIMER.get(), 0,event.getPlayerPatch().getOriginal());
        });
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_PRE, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.BASIC_ATTACK_EVENT, EVENT_UUID);
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
		int degree = vertic == 0 ? 0 : -(90 * horizon * (1 - Math.abs(vertic)) + 45 * vertic * horizon);
		int animation;
		
		if (vertic == 0) {
			if (horizon == 0) {
				animation = 1;
			} else {
				animation = horizon >= 0 ? 2 : 3;
			}
		} else {
			animation = vertic >= 0 ? 0 : 1;
		}
		
		CPExecuteSkill packet = new CPExecuteSkill(executer.getSkill(this).getSlotId());
		packet.getBuffer().writeInt(animation);
		packet.getBuffer().writeFloat(degree);
		
		return packet;
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		int i = args.readInt();
		float yaw = args.readFloat();
		if (executer.getSkill(this).getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) > 0 && !executer.getSkill(this).getDataManager().getDataValue(WOMSkillDataKeys.DODGE.get())) {
			executer.playAnimationSynchronized(this.animations[i], 0);
			executer.changeModelYRot(yaw);
			if(!executer.consumeStamina(this.getConsumption()*3)){
				executer.setStamina(0);
			}
		}
		executer.getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.TIMER.get(), 14,executer.getOriginal());
		executer.getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.DODGE.get(), false, executer.getOriginal());
	}
	
	@Override
	public Skill getPriorSkill() {
		return EpicFightSkills.STEP;
	}
	
	@Override
	public boolean isExecutableState(PlayerPatch<?> executer) {
		EntityState playerState = executer.getEntityState();
		if (!(executer.isUnstable() || !playerState.canUseSkill()) && !executer.getOriginal().isInWater() && !executer.getOriginal().onClimbable() && !(executer.getStamina() < 8)) {
			executer.getOriginal().playSound(SoundEvents.BLAZE_SHOOT, 0.3f, 2);
			return true;
		} else {
			executer.getOriginal().playSound(SoundEvents.LAVA_EXTINGUISH, 1, 2f);
			return false;
		}
	}
	
	@Override
	public void updateContainer(SkillContainer container) {

	super.updateContainer(container);
		if (container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) > 0) {
			if(!container.getExecuter().isLogicalClient()) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.TIMER.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get())-1,((ServerPlayerPatch) container.getExecuter()).getOriginal());
				if (((ServerPlayerPatch) container.getExecuter()).getStamina() > 0) {
					if (container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) < 14 && container.getDataManager().getDataValue(WOMSkillDataKeys.DODGE.get())) {
						container.getExecuter().playAnimationSynchronized(this.animations[(Math.abs(new Random().nextInt()) % 2)+2], 0);
						container.getDataManager().setDataSync(WOMSkillDataKeys.TIMER.get(), 14,((ServerPlayerPatch) container.getExecuter()).getOriginal());
						if(!container.getExecuter().consumeStamina(this.getConsumption()+1)){
							container.getExecuter().setStamina(0);
						}
					}
					if (container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) > 5 && container.getDataManager().getDataValue(WOMSkillDataKeys.DODGE.get())) {
						container.getDataManager().setDataSync(WOMSkillDataKeys.DODGE.get(), false,((ServerPlayerPatch) container.getExecuter()).getOriginal());
					}
				}
			}
		}
	}
}
