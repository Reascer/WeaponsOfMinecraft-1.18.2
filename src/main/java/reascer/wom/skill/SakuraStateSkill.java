package reascer.wom.skill;

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
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.weaponinnate.ConditionalWeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.SkillConsumeEvent;

public class SakuraStateSkill extends ConditionalWeaponInnateSkill {
	private static final UUID EVENT_UUID = UUID.fromString("1a56d169-416a-4206-ba3d-e7100d55d603");
	public static final SkillDataKey<Boolean> ACTIVE = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	public static final SkillDataKey<Integer> COOLDOWN = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	public static final SkillDataKey<Boolean> SECOND_DRAW = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	
	public static final SkillDataKey<Boolean> TIMEDSLASH = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	public static final SkillDataKey<Integer> TIMER = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	public static final SkillDataKey<Integer> FREQUENCY = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	public static final SkillDataKey<Integer> ATTACKS = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	public static final SkillDataKey<Float> DAMAGE = SkillDataKey.createDataKey(SkillDataManager.ValueType.FLOAT);
	
	public SakuraStateSkill(ConditionalWeaponInnateSkill.Builder builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		container.getDataManager().registerData(ACTIVE);
		container.getDataManager().registerData(SECOND_DRAW);
		container.getDataManager().registerData(COOLDOWN);
		
		container.getDataManager().registerData(TIMEDSLASH);
		container.getDataManager().registerData(TIMER);
		container.getDataManager().registerData(FREQUENCY);
		container.getDataManager().registerData(ATTACKS);
		container.getDataManager().registerData(DAMAGE);
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			container.getDataManager().setData(TIMEDSLASH, false);
			ServerPlayer serverPlayer = event.getPlayerPatch().getOriginal();
			StaticAnimation[] resetAnimations = {
					WOMAnimations.KATANA_SHEATHED_AUTO_1,
					WOMAnimations.KATANA_SHEATHED_AUTO_2,
					WOMAnimations.KATANA_SHEATHED_AUTO_3,
					WOMAnimations.KATANA_FATAL_DRAW,
					WOMAnimations.KATANA_FATAL_DRAW_SECOND};
			
			if (event.getAnimation().equals(WOMAnimations.KATANA_SHEATHED_DASH)) {
				container.getDataManager().setDataSync(TIMEDSLASH, true,serverPlayer);
				container.getDataManager().setDataSync(FREQUENCY, 1,serverPlayer);
				container.getDataManager().setDataSync(ATTACKS, 3 + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal()),serverPlayer);
				container.getDataManager().setDataSync(SECOND_DRAW, false,serverPlayer);
				container.getDataManager().setDataSync(TIMER, 20,serverPlayer);
			}
			
			if (event.getAnimation().equals(WOMAnimations.KATANA_FATAL_DRAW_DASH)) {
				container.getDataManager().setDataSync(TIMEDSLASH, true,serverPlayer);
				container.getDataManager().setDataSync(FREQUENCY, 1,serverPlayer);
				container.getDataManager().setDataSync(ATTACKS, 3 + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal()),serverPlayer);
				container.getDataManager().setDataSync(SECOND_DRAW, false,serverPlayer);
				container.getDataManager().setDataSync(TIMER, 40,serverPlayer);
			}
			
			for (StaticAnimation staticAnimation : resetAnimations) {
				if (event.getAnimation().equals(staticAnimation)) {
					container.getDataManager().setDataSync(TIMEDSLASH, true,serverPlayer);
					container.getDataManager().setDataSync(FREQUENCY, 1,serverPlayer);
					container.getDataManager().setDataSync(ATTACKS,0,serverPlayer);
					container.getDataManager().setDataSync(TIMER, 20,serverPlayer);
					
					if (staticAnimation.equals(WOMAnimations.KATANA_FATAL_DRAW)) {
						container.getDataManager().setDataSync(SECOND_DRAW, true,serverPlayer);
					}
					
					if (staticAnimation.equals(WOMAnimations.KATANA_FATAL_DRAW) ||
						staticAnimation.equals(WOMAnimations.KATANA_FATAL_DRAW_SECOND)	) {
						container.getDataManager().setDataSync(FREQUENCY, 0,serverPlayer);
						container.getDataManager().setDataSync(TIMER, 0,serverPlayer);
					}
				}
			}
			
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID, (event) -> {
			container.getDataManager().setData(DAMAGE, event.getDamage());
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
			if (event.getDamageSource().getAnimation().equals(WOMAnimations.KATANA_SHEATHED_DASH) ||
				event.getDamageSource().getAnimation().equals(WOMAnimations.KATANA_SHEATHED_COUNTER) ||
				event.getDamageSource().getAnimation().equals(WOMAnimations.KATANA_FATAL_DRAW_DASH) ||
				event.getDamageSource().getAnimation().equals(WOMAnimations.KATANA_FATAL_DRAW) ||
				event.getDamageSource().getAnimation().equals(WOMAnimations.KATANA_FATAL_DRAW_SECOND)) {
				for (String tag : event.getTarget().getTags()) {
					if (tag.contains("anti_stunlock:")) {
						String replaceTag = tag.split(":")[0] +":"+ Float.valueOf(tag.split(":")[1])*1.3 ;
						for (int i = 2; i < tag.split(":").length; i++) {
							replaceTag = replaceTag.concat(":"+tag.split(":")[i]);
						}
						event.getTarget().removeTag(tag);
						event.getTarget().addTag(replaceTag);
						break;
					}
				}
			}
			if (!event.getDamageSource().getAnimation().equals(WOMAnimations.KATANA_SHEATHED_COUNTER)) {
				if (container.getExecuter().getStamina() > 0) {
					if(container.getDataManager().getDataValue(TIMEDSLASH)){
						boolean tsa = false;
						for (String tag : event.getTarget().getTags()) {
							if (tag.contains("timed_katana_slashes:")) {
								int attacks = Integer.valueOf(tag.split(":")[3]);
								event.getTarget().removeTag(tag);
								event.getTarget().addTag("timed_katana_slashes:"+
										container.getDataManager().getDataValue(TIMER)+":"+
										(container.getDataManager().getDataValue(FREQUENCY)-1)+":"+
										(attacks + container.getDataManager().getDataValue(ATTACKS))+":"+
										0+":"+
										container.getDataManager().getDataValue(DAMAGE)+":"+
										event.getPlayerPatch().getOriginal().getId()+":"+
										(attacks + container.getDataManager().getDataValue(ATTACKS))
										);
								tsa = true;
								break;
							}
						}
						if (!tsa) {
							event.getTarget().addTag("timed_katana_slashes:"+
									container.getDataManager().getDataValue(TIMER)+":"+
									container.getDataManager().getDataValue(FREQUENCY)+":"+
									container.getDataManager().getDataValue(ATTACKS)+":"+
									1+":"+
									container.getDataManager().getDataValue(DAMAGE)+":"+
									event.getPlayerPatch().getOriginal().getId()+":"+
									container.getDataManager().getDataValue(ATTACKS)
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
		if (executer.getSkill(this).getDataManager().getDataValue(COOLDOWN) < 80) {
			executer.getSkill(this).getDataManager().setData(COOLDOWN, 80);
			boolean isSheathed = executer.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().getDataValue(SatsujinPassive.SHEATH);
			if (isSheathed || executer.getSkill(this).getDataManager().getDataValue(ACTIVE)) {
				executer.playAnimationSynchronized(this.attackAnimations[this.getAnimationInCondition(executer)], -0.45F);
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
			
			if (!executer.getSkill(this).getDataManager().getDataValue(ACTIVE)) {
				//executer.getOriginal().sendMessage(new TextComponent("katana stack:"+Math.round(Math.min(6,executer.getSkill(this).getStack()+1) * (1.0f + (EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal())/3.0f))-1)), UUID.randomUUID());
				this.setStackSynchronize(executer, (int) Math.min(12,Math.round(Math.min(6,executer.getSkill(this).getStack()+1)* (1.0f + (EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal())/3.0f)))-1));
				executer.getSkill(this).getDataManager().setDataSync(ACTIVE, true, executer.getOriginal());
			}
			if (executer.getSkill(this).getStack() == 0) {
				executer.getSkill(this).getDataManager().setDataSync(ACTIVE, false, executer.getOriginal());
			}
			executer.getSkill(this).activate();
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void onScreen(LocalPlayerPatch playerpatch, float resolutionX, float resolutionY) {
		if (playerpatch.getSkill(this).getDataManager().getDataValue(ACTIVE)) {
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
		if (container.getDataManager().getDataValue(COOLDOWN) > 0) {
			container.getDataManager().setData(COOLDOWN, container.getDataManager().getDataValue(COOLDOWN)-1);
		} else {
			if (container.getDataManager().getDataValue(ACTIVE)) {
				if(!container.getExecuter().isLogicalClient()) {
					this.setStackSynchronize((ServerPlayerPatch)container.getExecuter(),0);
					container.getExecuter().getSkill(this).getDataManager().setDataSync(ACTIVE, false,((ServerPlayerPatch)container.getExecuter()).getOriginal());
				}
			}
		}
	}
}