package reascer.wom.skill.weaponpassive;

import java.util.Random;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.skill.weaponinnate.LunarEclipseSkill;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class LunarEchoPassiveSkill extends PassiveSkill {
	public static final SkillDataKey<Boolean> IDLE = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	public static final SkillDataKey<Boolean> VERSO = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	private static final UUID EVENT_UUID = UUID.fromString("bc38699e-0de8-11ed-861d-0242ac120002");
	
	public LunarEchoPassiveSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		container.getDataManager().registerData(IDLE);
		container.getDataManager().registerData(VERSO);
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
	}
	
	@Override
	public boolean shouldDeactivateAutomatically(PlayerPatch<?> executer) {
		return true;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldDraw(SkillContainer container) {
		if (container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getSkill() instanceof LunarEclipseSkill) {
			return container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(LunarEclipseSkill.LUNAR_ECLIPSE_STACK) > 0;
		}
		return false;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawOnGui(BattleModeGui gui, SkillContainer container, PoseStack poseStack, float x, float y) {
		poseStack.pushPose();
		poseStack.translate(0, (float)gui.getSlidingProgression(), 0);
		RenderSystem.setShaderTexture(0, WOMSkills.lUNAR_ECLIPSE.getSkillTexture());
		GuiComponent.blit(poseStack, (int)x, (int)y, 24, 24, 0, 0, 1, 1, 1, 1);
		if (container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(LunarEclipseSkill.LUNAR_ECLIPSE_STACK) > 0) {
			float lunar_eclipse_stack = container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(LunarEclipseSkill.LUNAR_ECLIPSE_STACK);
			int lunar_eclipse_damage = (int) (4f * lunar_eclipse_stack*(1f/Math.sqrt((lunar_eclipse_stack/8f)+1f)));
			int nombre_de_chiffre = (int) Math.log(lunar_eclipse_damage);
			
			gui.font.drawShadow(poseStack, String.valueOf(lunar_eclipse_damage), x+8-(2*nombre_de_chiffre), y+8, 16777215);
		}
		poseStack.popPose();
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		super.updateContainer(container);
		PlayerPatch<?> entitypatch = container.getExecuter();
		int numberOf = 3;
		float partialScale = 1.0F / (numberOf - 1);
		float interpolation = 0.0F;
		OpenMatrix4f transformMatrix;
		if (entitypatch.currentLivingMotion == LivingMotions.IDLE) {
			container.getDataManager().setData(IDLE, false);
		}
		if (container.getDataManager().getDataValue(IDLE)) {
			int numberOf2 = 2;
			float partialScale2 = 1.0F / (numberOf2 - 1);
			float interpolation2 = 0.0F;
			for (int i = 0; i < numberOf2; i++) {
				transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(interpolation2), Armatures.BIPED.toolL);
				transformMatrix.translate(new Vec3f(0,0.0F,0.0F));
				OpenMatrix4f.mul(new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO + 180F), new Vec3f(0, 1, 0)),transformMatrix,transformMatrix);
				for (int j = 0; j < 1; j++) {
					entitypatch.getOriginal().level.addParticle(ParticleTypes.END_ROD,
						(transformMatrix.m30 + entitypatch.getOriginal().getX()+(new Random().nextFloat() - 0.5F)*0.35f),
						(transformMatrix.m31 + entitypatch.getOriginal().getY()+(new Random().nextFloat() - 0.5F)*0.35f),
						(transformMatrix.m32 + entitypatch.getOriginal().getZ()+(new Random().nextFloat() - 0.5F)*0.35f),
						(new Random().nextFloat() - 0.5F)*0.05f,
						(new Random().nextFloat() - 0.5F)*0.05f,
						(new Random().nextFloat() - 0.5F)*0.05f);
				}
				for (int j = 0; j < 1; j++) {
					entitypatch.getOriginal().level.addParticle(ParticleTypes.END_ROD,
						(transformMatrix.m30 + entitypatch.getOriginal().getX()),
						(transformMatrix.m31 + entitypatch.getOriginal().getY()),
						(transformMatrix.m32 + entitypatch.getOriginal().getZ()),
						0,
						0.05,
						0);
				}
				interpolation += partialScale2;
			}
		}
		
		if (container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(LunarEclipseSkill.LUNAR_ECLIPSE_STACK) > 0) {
			float lunar_eclipse_stack = container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(LunarEclipseSkill.LUNAR_ECLIPSE_STACK);
			int lunar_eclipse_damage = (int) (4f * lunar_eclipse_stack*(1f/Math.sqrt((lunar_eclipse_stack/8f)+1f)));
			int numberOf2 = lunar_eclipse_damage/30;
			float partialScale2 = 1.0F / (numberOf2 - 1);
			float interpolation2 = 0.0F;
			OpenMatrix4f transformMatrix2;
			for (int i = 0; i < numberOf2; i++) {
				transformMatrix2 = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(interpolation2), Armatures.BIPED.toolR);
				transformMatrix2.translate(new Vec3f(0,1.7F,0.2F));
				OpenMatrix4f.mul(new OpenMatrix4f().rotate(-(float) Math.toRadians(entitypatch.getOriginal().yBodyRotO + 180F), new Vec3f(0, 1, 0)),transformMatrix2,transformMatrix2);
				float blade = -(new Random().nextFloat() * 2.4f);
				transformMatrix2.translate(new Vec3f(-((new Random().nextFloat()-0.5f) * 0.4f),blade,-((new Random().nextFloat()-0.5f) * 0.4f)));
				entitypatch.getOriginal().level.addParticle(ParticleTypes.ELECTRIC_SPARK,
					(transformMatrix2.m30 + entitypatch.getOriginal().getX()),
					(transformMatrix2.m31 + entitypatch.getOriginal().getY()),
					(transformMatrix2.m32 + entitypatch.getOriginal().getZ()),
					0,
					0,
					0);
				interpolation2 += partialScale2;
			}
		}
	}
	
}