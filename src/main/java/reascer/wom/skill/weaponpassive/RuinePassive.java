package reascer.wom.skill.weaponpassive;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.skill.weaponinnate.SoulSnatchSkill;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class RuinePassive extends PassiveSkill {
	public RuinePassive(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldDraw(SkillContainer container) {
		if (container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getSkill() instanceof SoulSnatchSkill) {
			return container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(SoulSnatchSkill.TIMER) > 0;
		}
		return false;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawOnGui(BattleModeGui gui, SkillContainer container, PoseStack poseStack, float x, float y) {
		poseStack.pushPose();
		poseStack.translate(0, (float)gui.getSlidingProgression(), 0);
		RenderSystem.setShaderTexture(0, WOMSkills.SOUL_SNATCH.getSkillTexture());
		GuiComponent.blit(poseStack, (int)x, (int)y, 24, 24, 0, 0, 1, 1, 1, 1);
		gui.font.drawShadow(poseStack, String.valueOf((container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(SoulSnatchSkill.TIMER)/20)+1), x+4, y+13, 16777215);
		float strenght = (container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(SoulSnatchSkill.STRENGHT)/40.0f)*100f;
		gui.font.drawShadow(poseStack, String.valueOf(String.format("%.0f", strenght) + "%"), x+4, y+4, 16777215);
		poseStack.popPose();
	}
}