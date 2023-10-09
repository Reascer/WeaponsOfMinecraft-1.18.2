package reascer.wom.skill;

import java.util.List;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.main.WeaponsOfMinecraft;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.SkillConsumeEvent;

public class TrueBerserkSkill extends WeaponInnateSkill {
	private static final SkillDataKey<Integer> TIMER = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	public static final SkillDataKey<Boolean> ACTIVE = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	private static final SkillDataKey<Boolean> SUPER_ARMOR = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	

	private static final UUID EVENT_UUID = UUID.fromString("16c6748a-1c74-4681-9edb-e9ea4d69e54f");
	
	protected final StaticAnimation activateAnimation;
	
	public TrueBerserkSkill(Builder<?> builder) {
		super(builder.setActivateType(ActivateType.DURATION_INFINITE));

		this.activateAnimation = WOMAnimations.TORMENT_BERSERK_CONVERT;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(TIMER);
		container.getDataManager().registerData(ACTIVE);
		container.getDataManager().registerData(SUPER_ARMOR);
		
		if (!container.getExecuter().isLogicalClient()) {
			this.setMaxDurationSynchronize((ServerPlayerPatch)container.getExecuter(), this.maxDuration + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal()));
		}
		
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		if(!container.getExecuter().isLogicalClient()) {
			container.getSkill().cancelOnServer((ServerPlayerPatch)container.getExecuter(), null);
		}
		container.deactivate();
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		if (executer.getSkill(this).isActivated()) { 
			super.cancelOnServer(executer, args);
			executer.getSkill(this).getDataManager().setDataSync(ACTIVE, false,executer.getOriginal());
			this.setDurationSynchronize(executer, 0);
			this.setStackSynchronize(executer, executer.getSkill(this).getStack() - 1);
			executer.getSkill(this).deactivate();
			executer.modifyLivingMotionByCurrentItem();
		} else {
			executer.playAnimationSynchronized(this.activateAnimation, 0);
			executer.getOriginal().level().playSound(null, executer.getOriginal().xo, executer.getOriginal().yo, executer.getOriginal().zo,
	    			SoundEvents.DRAGON_FIREBALL_EXPLODE, executer.getOriginal().getSoundSource(), 1.0F, 0.5F);
			((ServerLevel) executer.getOriginal().level()).sendParticles( ParticleTypes.LARGE_SMOKE, 
					executer.getOriginal().getX() - 0.15D, 
					executer.getOriginal().getY() + 1.2D, 
					executer.getOriginal().getZ() - 0.15D, 
					150, 0.3D, 0.6D, 0.3D, 0.1D);
			executer.getSkill(this).getDataManager().setData(TIMER, 2);
			executer.getSkill(this).getDataManager().setDataSync(ACTIVE, true,executer.getOriginal());
			this.setDurationSynchronize(executer, this.maxDuration + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal()));
			executer.getSkill(this).activate();
			executer.modifyLivingMotionByCurrentItem();
			SkillConsumeEvent event = new SkillConsumeEvent(executer, this, this.resource, true);
			executer.getEventListener().triggerEvents(EventType.SKILL_CONSUME_EVENT, event);
			if (!event.isCanceled()) {
				event.getResourceType().consumer.consume(this, executer, event.getAmount());
			}
			this.setStackSynchronize(executer, 1);
		}
	}
	
	@Override
	public void cancelOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		super.cancelOnServer(executer, args);
		executer.getSkill(this).getDataManager().setDataSync(ACTIVE, false,executer.getOriginal());
		this.setStackSynchronize(executer, executer.getSkill(this).getStack() - 1);
		this.setDurationSynchronize(executer, 0);
		executer.modifyLivingMotionByCurrentItem();
	}
	
	@Override
	public boolean canExecute(PlayerPatch<?> executer) {
		if (executer.isLogicalClient()) {
			return super.canExecute(executer);
		} else {
			ItemStack itemstack = executer.getOriginal().getMainHandItem();
			
			return EpicFightCapabilities.getItemStackCapability(itemstack).getInnateSkill(executer, itemstack) == this && executer.getOriginal().getVehicle() == null;
		}
	}
	
	@Override
	public WeaponInnateSkill registerPropertiesToAnimation() {
		return this;
	}
	
	@Override
	public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
		List<Component> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Auto attack :");
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(1), "Dash attack :");
		
		return list;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void onScreen(LocalPlayerPatch playerpatch, float resolutionX, float resolutionY) {
		if (playerpatch.getSkill(this).getDataManager().getDataValue(ACTIVE)) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, new ResourceLocation(WeaponsOfMinecraft.MODID, "textures/gui/overlay/true_berserk.png"));
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
		if (container.isActivated()) {
			if (container.getDataManager().getDataValue(ACTIVE)) {
				container.getExecuter().getOriginal().addEffect(new MobEffectInstance(EpicFightMobEffects.STUN_IMMUNITY.get(),2, 0,true,false,false));
				if (container.getDataManager().getDataValue(TIMER) > 0) {
					container.getDataManager().setData(TIMER, container.getDataManager().getDataValue(TIMER)-1);
				} else {
					container.getDataManager().setData(TIMER,3);
					if (container.getRemainDuration() > 1) {
						if(!container.getExecuter().isLogicalClient()) {
							this.setDurationSynchronize((ServerPlayerPatch) container.getExecuter(), container.getRemainDuration()-1);
						}
					} else {
						if (container.getExecuter().getOriginal().getHealth() - (container.getExecuter().getOriginal().getMaxHealth() * 0.04f) > 0f) {
							EpicFightDamageSource selfdamage = container.getExecuter().getDamageSource(WOMAnimations.ANTITHEUS_PULL, InteractionHand.MAIN_HAND);
							selfdamage.setStunType(StunType.NONE);
							container.getExecuter().getOriginal().hurt(selfdamage,(container.getExecuter().getOriginal().getMaxHealth() * 0.04f));
							if(!container.getExecuter().isLogicalClient()) {
								if (!container.getExecuter().getOriginal().isCreative()) {
								((ServerLevel) container.getExecuter().getOriginal().level()).sendParticles( ParticleTypes.SMOKE, 
										container.getExecuter().getOriginal().getX() - 0.2D, 
										container.getExecuter().getOriginal().getY() + 1.3D, 
										container.getExecuter().getOriginal().getZ() - 0.2D, 
										40, 0.6D, 0.8D, 0.6D, 0.05);
								}
								this.setDurationSynchronize((ServerPlayerPatch) container.getExecuter(),(this.maxDuration + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal())));
							}
						} else {
							if(!container.getExecuter().isLogicalClient()) {
								container.getSkill().cancelOnServer((ServerPlayerPatch)container.getExecuter(), null);
								this.setDurationSynchronize((ServerPlayerPatch) container.getExecuter(), 0);
							}
							container.deactivate();
						}
					}
				}
			} else {
				if(!container.getExecuter().isLogicalClient()) {
					container.getSkill().cancelOnServer((ServerPlayerPatch)container.getExecuter(), null);
				}
			}
		}
	}
}