package reascer.wom.skill;

import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.gameasset.WOMSkills;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.skill.dodge.DodgeSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.SkillConsumeEvent;

public class EnderObscurisSkill extends DodgeSkill {
	public static final SkillDataKey<Integer> TARGET_ID = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	private static final UUID EVENT_UUID = UUID.fromString("38cb04e1-9751-445f-82bd-fb61426a58c7");
	
	public EnderObscurisSkill(DodgeSkill.Builder builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		container.getDataManager().registerData(TARGET_ID);
		
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
			container.getDataManager().setDataSync(TARGET_ID, event.getTarget().getId(), event.getPlayerPatch().getOriginal());
			
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
		container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
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
		boolean tag = false;
		LivingEntity target = (LivingEntity) executer.getOriginal().level().getEntity(executer.getSkill(this).getDataManager().getDataValue(EnderObscurisSkill.TARGET_ID));
		if (target != null) {
			if (target.distanceTo(executer.getOriginal()) < 30) {
				tag = true;
			}
			if (!tag) {
				i = 1;
			}
		}
		
		executer.playAnimationSynchronized(this.animations[i], 0);
		executer.changeModelYRot(yaw);
	}
	
	@Override
	public Skill getPriorSkill() {
		return WOMSkills.ENDERSTEP;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldDraw(SkillContainer container) {
		LivingEntity target = (LivingEntity) container.getExecuter().getOriginal().level().getEntity(container.getDataManager().getDataValue(EnderObscurisSkill.TARGET_ID));
		boolean tag = false;
		if (target != null) {
			if (target.distanceTo(container.getExecuter().getOriginal()) < 30) {
				tag = true;
			}
		}
		
		return tag;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y) {
		PoseStack poseStack = guiGraphics.pose();
		poseStack.pushPose();
		poseStack.translate(0, (float)gui.getSlidingProgression(), 0);
		RenderSystem.setShaderTexture(0, this.getSkillTexture());
		guiGraphics.blit(this.getSkillTexture(), (int)x, (int)y, 24, 24, 0, 0, 1, 1, 1, 1);
		poseStack.popPose();
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		super.updateContainer(container);
		if(!container.getExecuter().isLogicalClient()) {
			if (container.getDataManager().getDataValue(TARGET_ID) != null) {
				Entity target = container.getExecuter().getOriginal().level().getEntity(container.getDataManager().getDataValue(TARGET_ID));
				if (target != null) {
					if (target instanceof LivingEntity) {
						if (((LivingEntity)target).isDeadOrDying()) {
							container.getDataManager().setDataSync(TARGET_ID, -1, (ServerPlayer)container.getExecuter().getOriginal());
						}
					}
				}
			}
		}
	}
}
