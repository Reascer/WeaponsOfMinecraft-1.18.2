package reascer.wom.skill.passive;

import java.util.List;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.skill.WOMSkillDataKeys;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class HeartShieldSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("42580b91-53a6-4d7f-92b4-487aa585cd0b");
	
	private float recovery_delay;
	private float recovery_rate;
	
	public HeartShieldSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		recovery_delay = 5f;
		recovery_rate = 2f;
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			container.getDataManager().setDataSync(WOMSkillDataKeys.COOLDOWN.get(), 100, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
		container.getExecuter().getOriginal().setAbsorptionAmount(0);
		container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldDraw(SkillContainer container) {
		return container.getDataManager().getDataValue(WOMSkillDataKeys.COOLDOWN.get()) > 0 || container.getExecuter().getOriginal().getAbsorptionAmount() < container.getDataManager().getDataValue(WOMSkillDataKeys.MAX_SHIELD.get());
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y) {
		PoseStack poseStack = guiGraphics.pose();
		poseStack.pushPose();
		poseStack.translate(0, (float)gui.getSlidingProgression(), 0);
		if (container.getDataManager().getDataValue(WOMSkillDataKeys.COOLDOWN.get()) > 0) {
			RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 0.5F);
		} else {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}
		guiGraphics.blit(this.getSkillTexture(), (int)x, (int)y, 24, 24, 0, 0, 1, 1, 1, 1);
		if (container.getDataManager().getDataValue(WOMSkillDataKeys.COOLDOWN.get()) > 0) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			guiGraphics.drawString(gui.font, String.valueOf((container.getDataManager().getDataValue(WOMSkillDataKeys.COOLDOWN.get())/20)+1), x+9, y+10, 16777215,true);
		}
		poseStack.popPose();
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<Object> getTooltipArgsOfScreen(List<Object> list) {
		list.add(String.format("%.1f", this.recovery_delay));
		list.add(String.format("%.1f", this.recovery_rate));
		return list;
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		super.updateContainer(container);
		int protection = 0;
		for (ItemStack ArmorPiece : container.getExecuter().getOriginal().getArmorSlots()) {
			protection += ArmorPiece.getEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION);
		}
		recovery_rate = (40 / (1 + (protection/4)))/20f;
		if (!container.getExecuter().isLogicalClient()) {
			container.getDataManager().setDataSync(WOMSkillDataKeys.MAX_SHIELD.get(), 20, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.COOLDOWN.get()) > 0) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.COOLDOWN.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.COOLDOWN.get()) -1, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
			} else {
				if (container.getExecuter().getOriginal().getAbsorptionAmount() < container.getDataManager().getDataValue(WOMSkillDataKeys.MAX_SHIELD.get())) {
					if (container.getDataManager().getDataValue(WOMSkillDataKeys.RECOVERY_RATE.get()) > 0) {
						container.getDataManager().setDataSync(WOMSkillDataKeys.RECOVERY_RATE.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.RECOVERY_RATE.get()) -1, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
					} else {
						
						container.getDataManager().setDataSync(WOMSkillDataKeys.RECOVERY_RATE.get(), 40 / (1 + (protection/4)) , ((ServerPlayerPatch) container.getExecuter()).getOriginal());
						if (container.getExecuter().getOriginal().getAbsorptionAmount()+1 >= container.getDataManager().getDataValue(WOMSkillDataKeys.MAX_SHIELD.get())) {
							container.getExecuter().getOriginal().setAbsorptionAmount(container.getDataManager().getDataValue(WOMSkillDataKeys.MAX_SHIELD.get()));
						} else {
							container.getExecuter().getOriginal().setAbsorptionAmount(container.getExecuter().getOriginal().getAbsorptionAmount()+1);
						}
					}
				}
			}
		}
	}
}