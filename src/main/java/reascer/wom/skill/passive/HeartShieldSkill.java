package reascer.wom.skill.passive;

import java.util.List;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class HeartShieldSkill extends PassiveSkill {
	public static final SkillDataKey<Integer> MAX_SHIELD = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	public static final SkillDataKey<Integer> RECOVERY_COOLDOWN = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	public static final SkillDataKey<Integer> RECOVERY_RATE = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	
	private static final UUID EVENT_UUID = UUID.fromString("42580b91-53a6-4d7f-92b4-487aa585cd0b");
	
	private float recovery_delay;
	private float recovery_rate;
	
	public HeartShieldSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		container.getDataManager().registerData(MAX_SHIELD);
		container.getDataManager().registerData(RECOVERY_COOLDOWN);
		container.getDataManager().registerData(RECOVERY_RATE);
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			int protection = 0;
			for (ItemStack ArmorPiece : container.getExecuter().getOriginal().getArmorSlots()) {
				protection += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION, ArmorPiece);
			}
			container.getDataManager().setDataSync(RECOVERY_COOLDOWN, 100 / (1 + (protection/4)), ((ServerPlayerPatch) container.getExecuter()).getOriginal());
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
		return container.getDataManager().getDataValue(RECOVERY_COOLDOWN) > 0 || container.getExecuter().getOriginal().getAbsorptionAmount() < container.getDataManager().getDataValue(MAX_SHIELD);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y) {
		PoseStack poseStack = guiGraphics.pose();
		poseStack.pushPose();
		poseStack.translate(0, (float)gui.getSlidingProgression(), 0);
		RenderSystem.setShaderTexture(0, this.getSkillTexture());
		if (container.getDataManager().getDataValue(RECOVERY_COOLDOWN) > 0) {
			RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 0.5F);
		} else {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}
		guiGraphics.blit(this.getSkillTexture(), (int)x, (int)y, 24, 24, 0, 0, 1, 1, 1, 1);
		if (container.getDataManager().getDataValue(RECOVERY_COOLDOWN) > 0) {
			guiGraphics.drawString(gui.font, String.valueOf((container.getDataManager().getDataValue(RECOVERY_COOLDOWN)/20)+1), x+7, y+13, 16777215,true);
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
		if (!container.getExecuter().isLogicalClient()) {
			int protection = 0;
			for (ItemStack ArmorPiece : container.getExecuter().getOriginal().getArmorSlots()) {
				protection += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION, ArmorPiece);
			}
			recovery_delay = (100 / (1 + (protection/4)))/20f;
			recovery_rate = (40 / (1 + (protection/4)))/20f;
			container.getDataManager().setDataSync(MAX_SHIELD, 20, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
			if (container.getDataManager().getDataValue(RECOVERY_COOLDOWN) > 0) {
				container.getDataManager().setDataSync(RECOVERY_COOLDOWN, container.getDataManager().getDataValue(RECOVERY_COOLDOWN) -1, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
			} else {
				if (container.getExecuter().getOriginal().getAbsorptionAmount() < container.getDataManager().getDataValue(MAX_SHIELD)) {
					if (container.getDataManager().getDataValue(RECOVERY_RATE) > 0) {
						container.getDataManager().setDataSync(RECOVERY_RATE, container.getDataManager().getDataValue(RECOVERY_RATE) -1, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
					} else {
						
						container.getDataManager().setDataSync(RECOVERY_RATE, 40 / (1 + (protection/4)) , ((ServerPlayerPatch) container.getExecuter()).getOriginal());
						if (container.getExecuter().getOriginal().getAbsorptionAmount()+1 >= container.getDataManager().getDataValue(MAX_SHIELD)) {
							container.getExecuter().getOriginal().setAbsorptionAmount(container.getDataManager().getDataValue(MAX_SHIELD));
						} else {
							container.getExecuter().getOriginal().setAbsorptionAmount(container.getExecuter().getOriginal().getAbsorptionAmount()+1);
						}
					}
				}
			}
		}
	}
}