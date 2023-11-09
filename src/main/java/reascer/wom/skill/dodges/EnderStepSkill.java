package reascer.wom.skill.dodges;

import java.util.UUID;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.gameasset.WOMSkills;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.dodge.DodgeSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.SkillConsumeEvent;

public class EnderStepSkill extends DodgeSkill {
	private static final UUID EVENT_UUID = UUID.fromString("38cb04e1-9751-445f-82bd-fb61426a58c7");
	
	public EnderStepSkill(DodgeSkill.Builder builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
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
				animation = 4;
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
		SkillConsumeEvent event = new SkillConsumeEvent(executer, this, this.resource, true);
		executer.getEventListener().triggerEvents(EventType.SKILL_CONSUME_EVENT, event);
		
		if (!event.isCanceled()) {
			event.getResourceType().consumer.consume(this, executer, event.getAmount());
		}
		
		executer.getSkill(this).activate();
		int i = args.readInt();
		float yaw = args.readFloat();
		
		executer.playAnimationSynchronized(this.animations[i], 0);
		executer.changeModelYRot(yaw);
	}
	
	@Override
	public Skill getPriorSkill() {
		return WOMSkills.ENDERSTEP;
	}
}
