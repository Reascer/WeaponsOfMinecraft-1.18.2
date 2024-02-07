package reascer.wom.events.engine;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.world.item.WOMItems;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.item.EpicFightItems;

@OnlyIn(Dist.CLIENT)
public class RenderGesetz extends RenderItemBase {
	private final ItemStack Secondmodel = new ItemStack(WOMItems.GESETZ_HANDLE.get());
	
	@Override
	public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand, HumanoidArmature armature, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight) {
		OpenMatrix4f modelMatrix = this.getCorrectionMatrix(stack, entitypatch, hand);
		boolean isInMainhand = (hand == InteractionHand.MAIN_HAND);
		Joint holdingHand = isInMainhand ? armature.toolR : armature.toolL;
		OpenMatrix4f jointTransform = poses[holdingHand.getId()];
		Joint holdingHand2 = isInMainhand ? armature.handR : armature.handL;
		OpenMatrix4f jointTransform2 = poses[holdingHand2.getId()];
		modelMatrix.mulFront(jointTransform);
		
		poseStack.pushPose();
		this.mulPoseStack(poseStack, modelMatrix);
		TransformType transformType = isInMainhand ? TransformType.THIRD_PERSON_RIGHT_HAND : TransformType.THIRD_PERSON_LEFT_HAND;
		Minecraft.getInstance().getItemInHandRenderer().renderItem(entitypatch.getOriginal(), stack, transformType, !isInMainhand, poseStack, buffer, packedLight);
		
		poseStack.popPose();
		
		modelMatrix = this.getCorrectionMatrix(Secondmodel, entitypatch, hand);
		modelMatrix.mulFront(jointTransform2);
		modelMatrix.translate(-0.001f,-0.02f, -0.08F);
		
		poseStack.pushPose();
		this.mulPoseStack(poseStack, modelMatrix);
		Minecraft.getInstance().getItemInHandRenderer().renderItem(entitypatch.getOriginal(), Secondmodel, transformType, !isInMainhand, poseStack, buffer, packedLight);
		
		poseStack.popPose();
    }
}