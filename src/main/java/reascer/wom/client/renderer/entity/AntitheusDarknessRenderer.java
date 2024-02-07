package reascer.wom.client.renderer.entity;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.client.model.AntitheusDarknessModel;
import reascer.wom.main.WeaponsOfMinecraft;
import reascer.wom.world.entity.projectile.AntitheusDarkness;
import reascer.wom.world.entity.projectile.EnderBullet;
import yesman.epicfight.api.utils.math.QuaternionUtils;

@OnlyIn(Dist.CLIENT)
public class AntitheusDarknessRenderer extends EntityRenderer<AntitheusDarkness> {
   private final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(WeaponsOfMinecraft.MODID,"textures/item/ruine_texture.png");
   private final Model model;

   public AntitheusDarknessRenderer(EntityRendererProvider.Context p_174449_) {
      super(p_174449_);
      this.model = new AntitheusDarknessModel(RenderType::entityTranslucent);
   }

   protected int getBlockLightLevel(EnderBullet p_116491_, BlockPos p_116492_) {
      return 15;
   }

   public void render(AntitheusDarkness antitheusDarkness, float p_116485_, float p_116486_, PoseStack poseStack, MultiBufferSource p_116488_, int p_116489_) {
      poseStack.pushPose();
      poseStack.scale(-1.0F, -1.0F, 1.0F);
      float f = Mth.lerp(1, antitheusDarkness.yRotO, antitheusDarkness.getYRot());
      float f1 = Mth.lerp(1, antitheusDarkness.xRotO, antitheusDarkness.getXRot());
      VertexConsumer vertexconsumer = p_116488_.getBuffer(this.model.renderType(this.getTextureLocation(antitheusDarkness)));
      poseStack.mulPose(QuaternionUtils.YP.rotationDegrees(f + (new Random().nextFloat() -0.5f)));
      poseStack.mulPose(QuaternionUtils.XP.rotationDegrees(f1 + (new Random().nextFloat() -0.5f)));
      this.model.renderToBuffer(poseStack, vertexconsumer, p_116489_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      poseStack.popPose();
      super.render(antitheusDarkness, p_116485_, p_116486_, poseStack, p_116488_, p_116489_);
   }
   
   public ResourceLocation getTextureLocation(AntitheusDarkness p_116482_) {
      return TEXTURE_LOCATION;
   }
}