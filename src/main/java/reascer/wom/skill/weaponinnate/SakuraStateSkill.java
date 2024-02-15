package reascer.wom.skill.weaponinnate;

import java.util.UUID;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.main.WeaponsOfMinecraft;
import reascer.wom.skill.WOMSkillDataKeys;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.weaponinnate.ConditionalWeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.SkillConsumeEvent;

public class SakuraStateSkill extends ConditionalWeaponInnateSkill {
	private static final UUID EVENT_UUID = UUID.fromString("1a56d169-416a-4206-ba3d-e7100d55d603");
	
	public SakuraStateSkill(ConditionalWeaponInnateSkill.Builder builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			container.getDataManager().setData(WOMSkillDataKeys.TIMEDSLASH.get(), false);
			ServerPlayer serverPlayer = event.getPlayerPatch().getOriginal();
			StaticAnimation[] resetAnimations = {
					WOMAnimations.KATANA_SHEATHED_AUTO_1,
					WOMAnimations.KATANA_SHEATHED_AUTO_2,
					WOMAnimations.KATANA_SHEATHED_AUTO_3,
					WOMAnimations.KATANA_FATAL_DRAW,
					WOMAnimations.KATANA_FATAL_DRAW_SECOND};
			
			if (event.getAnimation().equals(WOMAnimations.KATANA_SHEATHED_DASH)) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.TIMEDSLASH.get(), true,serverPlayer);
				container.getDataManager().setDataSync(WOMSkillDataKeys.FREQUENCY.get(), 1,serverPlayer);
				container.getDataManager().setDataSync(WOMSkillDataKeys.ATTACKS.get(), 3 + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal()),serverPlayer);
				container.getDataManager().setDataSync(WOMSkillDataKeys.SECOND_DRAW.get(), false,serverPlayer);
				container.getDataManager().setDataSync(WOMSkillDataKeys.TIMER.get(), 20,serverPlayer);
			}
			
			if (event.getAnimation().equals(WOMAnimations.KATANA_SHEATHED_COUNTER)) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.TIMEDSLASH.get(), true,serverPlayer);
				container.getDataManager().setDataSync(WOMSkillDataKeys.FREQUENCY.get(), 1,serverPlayer);
				container.getDataManager().setDataSync(WOMSkillDataKeys.ATTACKS.get(), 3 + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal()),serverPlayer);
				container.getDataManager().setDataSync(WOMSkillDataKeys.SECOND_DRAW.get(), false,serverPlayer);
				container.getDataManager().setDataSync(WOMSkillDataKeys.TIMER.get(), 20,serverPlayer);
			}
			
			if (event.getAnimation().equals(WOMAnimations.KATANA_FATAL_DRAW_DASH)) {
				container.getDataManager().setDataSync(WOMSkillDataKeys.TIMEDSLASH.get(), true,serverPlayer);
				container.getDataManager().setDataSync(WOMSkillDataKeys.FREQUENCY.get(), 1,serverPlayer);
				container.getDataManager().setDataSync(WOMSkillDataKeys.ATTACKS.get(), 3 + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal()),serverPlayer);
				container.getDataManager().setDataSync(WOMSkillDataKeys.SECOND_DRAW.get(), false,serverPlayer);
				container.getDataManager().setDataSync(WOMSkillDataKeys.TIMER.get(), 40,serverPlayer);
			}
			
			for (StaticAnimation staticAnimation : resetAnimations) {
				if (event.getAnimation().equals(staticAnimation)) {
					container.getDataManager().setDataSync(WOMSkillDataKeys.TIMEDSLASH.get(), true,serverPlayer);
					container.getDataManager().setDataSync(WOMSkillDataKeys.FREQUENCY.get(), 1,serverPlayer);
					container.getDataManager().setDataSync(WOMSkillDataKeys.ATTACKS.get(),0,serverPlayer);
					container.getDataManager().setDataSync(WOMSkillDataKeys.TIMER.get(), 20,serverPlayer);
					
					if (staticAnimation.equals(WOMAnimations.KATANA_FATAL_DRAW)) {
						container.getDataManager().setDataSync(WOMSkillDataKeys.SECOND_DRAW.get(), true,serverPlayer);
					}
					
					if (staticAnimation.equals(WOMAnimations.KATANA_FATAL_DRAW) ||
						staticAnimation.equals(WOMAnimations.KATANA_FATAL_DRAW_SECOND)	) {
						container.getDataManager().setDataSync(WOMSkillDataKeys.FREQUENCY.get(), 0,serverPlayer);
						container.getDataManager().setDataSync(WOMSkillDataKeys.TIMER.get(), 0,serverPlayer);
					}
				}
			}
			
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID, (event) -> {
			container.getDataManager().setData(WOMSkillDataKeys.DAMAGE.get(), event.getDamage());
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
			if (event.getDamageSource().getAnimation().equals(WOMAnimations.KATANA_SHEATHED_DASH) ||
				event.getDamageSource().getAnimation().equals(WOMAnimations.KATANA_SHEATHED_COUNTER) ||
				event.getDamageSource().getAnimation().equals(WOMAnimations.KATANA_FATAL_DRAW_DASH) ||
				event.getDamageSource().getAnimation().equals(WOMAnimations.KATANA_FATAL_DRAW) ||
				event.getDamageSource().getAnimation().equals(WOMAnimations.KATANA_FATAL_DRAW_SECOND)) {
				for (String tag : event.getTarget().getTags()) {
					if (tag.contains("anti_stunlock:")) {
						String replaceTag = tag.split(":")[0] +":"+ Float.valueOf(tag.split(":")[1])*1.25 ;
						for (int i = 2; i < tag.split(":").length; i++) {
							replaceTag = replaceTag.concat(":"+tag.split(":")[i]);
						}
						event.getTarget().removeTag(tag);
						event.getTarget().addTag(replaceTag);
						break;
					}
				}
			}
			if (!event.getDamageSource().getAnimation().equals(WOMAnimations.KATANA_SAKURA_TIMED_SLASH)) {
				if (container.getExecuter().getStamina() > 0) {
					if(container.getDataManager().getDataValue(WOMSkillDataKeys.TIMEDSLASH.get())){
						boolean tsa = false;
						for (String tag : event.getTarget().getTags()) {
							if (tag.contains("timed_katana_slashes:")) {
								int attacks = Integer.valueOf(tag.split(":")[3]);
								event.getTarget().removeTag(tag);
								event.getTarget().addTag("timed_katana_slashes:"+
										container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get())+":"+
										(container.getDataManager().getDataValue(WOMSkillDataKeys.FREQUENCY.get())-1)+":"+
										(attacks + container.getDataManager().getDataValue(WOMSkillDataKeys.ATTACKS.get()))+":"+
										0+":"+
										container.getDataManager().getDataValue(WOMSkillDataKeys.DAMAGE.get())+":"+
										event.getPlayerPatch().getOriginal().getId()+":"+
										(attacks + container.getDataManager().getDataValue(WOMSkillDataKeys.ATTACKS.get()))
										);
								tsa = true;
								break;
							}
						}
						if (!tsa) {
							event.getTarget().addTag("timed_katana_slashes:"+
									container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get())+":"+
									container.getDataManager().getDataValue(WOMSkillDataKeys.FREQUENCY.get())+":"+
									container.getDataManager().getDataValue(WOMSkillDataKeys.ATTACKS.get())+":"+
									1+":"+
									container.getDataManager().getDataValue(WOMSkillDataKeys.DAMAGE.get())+":"+
									event.getPlayerPatch().getOriginal().getId()+":"+
									container.getDataManager().getDataValue(WOMSkillDataKeys.ATTACKS.get())
							);
						}
					}
				}
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		if (executer.getSkill(this).getDataManager().getDataValue(WOMSkillDataKeys.COOLDOWN.get()) < 80) {
			executer.getSkill(this).getDataManager().setData(WOMSkillDataKeys.COOLDOWN.get(), 80);
			boolean isSheathed = executer.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().getDataValue(WOMSkillDataKeys.SHEATH.get());
			if (isSheathed || executer.getSkill(this).getDataManager().getDataValue(WOMSkillDataKeys.ACTIVE.get())) {
				float convertTime =  -0.45F;
				executer.playAnimationSynchronized(this.attackAnimations[this.getAnimationInCondition(executer)], convertTime);
			} else {
				executer.playAnimationSynchronized(this.attackAnimations[this.getAnimationInCondition(executer)], 0);
			}
			if (!executer.getOriginal().isCreative()) {
				//this.setConsumptionSynchronize(executer, 0);
				this.setDurationSynchronize(executer, 0);
				SkillConsumeEvent event = new SkillConsumeEvent(executer, this, this.resource, true);
				executer.getEventListener().triggerEvents(EventType.SKILL_CONSUME_EVENT, event);
				
				if (!event.isCanceled()) {
					event.getResourceType().consumer.consume(this, executer, event.getAmount());
				}
			}
			
			if (!executer.getSkill(this).getDataManager().getDataValue(WOMSkillDataKeys.ACTIVE.get())) {
				//executer.getOriginal().sendMessage(new TextComponent("katana stack:"+Math.round(Math.min(6,executer.getSkill(this).getStack()+1) * (1.0f + (EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal())/3.0f))-1)), UUID.randomUUID());
				this.setStackSynchronize(executer, (int) Math.min(12,Math.round(Math.min(6,executer.getSkill(this).getStack()+1)* (1.0f + (EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal())/3.0f)))-1));
				executer.getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.ACTIVE.get(), true, executer.getOriginal());
			}
			if (executer.getSkill(this).getStack() == 0) {
				executer.getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.ACTIVE.get(), false, executer.getOriginal());
			}
			executer.getSkill(this).activate();
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void onScreen(LocalPlayerPatch playerpatch, float resolutionX, float resolutionY) {
		if (playerpatch.getSkill(this).getDataManager().getDataValue(WOMSkillDataKeys.ACTIVE.get())) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, new ResourceLocation(WeaponsOfMinecraft.MODID, "textures/gui/overlay/katana_eternity.png"));
			GlStateManager._enableBlend();
			GlStateManager._disableDepthTest();
			GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Tesselator tessellator = Tesselator.getInstance();
		    BufferBuilder bufferbuilder = tessellator.getBuilder();
		    bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		    bufferbuilder.vertex(0, 0, 1).uv(0, 0).endVertex();
		    bufferbuilder.vertex(0, resolutionY, 1).uv(0, 1).endVertex();
		    bufferbuilder.vertex(resolutionX, resolutionY, 1).uv(1, 1).endVertex();
		    bufferbuilder.vertex(resolutionX, 0, 1).uv(1, 0).endVertex();
		    tessellator.end();
		}
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		super.updateContainer(container);
		if (container.getDataManager().getDataValue(WOMSkillDataKeys.COOLDOWN.get()) > 0) {
			container.getDataManager().setData(WOMSkillDataKeys.COOLDOWN.get(), container.getDataManager().getDataValue(WOMSkillDataKeys.COOLDOWN.get())-1);
		} else {
			if (container.getDataManager().getDataValue(WOMSkillDataKeys.ACTIVE.get())) {
				if(!container.getExecuter().isLogicalClient()) {
					this.setStackSynchronize((ServerPlayerPatch)container.getExecuter(),0);
					container.getExecuter().getSkill(this).getDataManager().setDataSync(WOMSkillDataKeys.ACTIVE.get(), false,((ServerPlayerPatch)container.getExecuter()).getOriginal());
				}
			}
		}
	}
}