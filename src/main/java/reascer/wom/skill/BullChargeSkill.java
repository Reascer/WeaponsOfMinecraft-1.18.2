package reascer.wom.skill;

import java.util.List;
import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class BullChargeSkill extends Skill {
	public static final SkillDataKey<Boolean> SUPER_ARMOR = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	private static final UUID EVENT_UUID = UUID.fromString("0c413ee2-663b-4d30-8e27-e3217fb45aa1");
	
	protected final StaticAnimation animations;
	//0c413ee2-663b-4d30-8e27-e3217fb45aa1
	public static class Builder extends Skill.Builder<BullChargeSkill> {
		protected ResourceLocation animations;
		
		public Builder setCategory(SkillCategory category) {
			this.category = category;
			return this;
		}
		
		public Builder setActivateType(ActivateType activateType) {
			this.activateType = activateType;
			return this;
		}
		
		public Builder setResource(Resource resource) {
			this.resource = resource;
			return this;
		}
		
		public Builder setCreativeTab(CreativeModeTab tab) {
			this.tab = tab;
			return this;
		}
		
		public Builder setAnimations(ResourceLocation animations) {
			this.animations = animations;
			return this;
		}
	}
	
	public static Builder createChargeBuilder() {
		return (new Builder()).setCategory(SkillCategories.DODGE).setActivateType(ActivateType.ONE_SHOT).setResource(Resource.STAMINA);
	}
	
	public BullChargeSkill(Builder builder) {
		super(builder);
		this.animations = EpicFightMod.getInstance().animationManager.findAnimationByPath(builder.animations.toString());
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(SUPER_ARMOR);
		
		container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_POST, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(SUPER_ARMOR)) {
				event.setAmount(event.getAmount()*0.6f);
				event.getDamageSource().setStunType(StunType.NONE);
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_POST, EVENT_UUID);
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		super.executeOnServer(executer, args);
		executer.getSkill(this).getDataManager().setDataSync(SUPER_ARMOR, true, executer.getOriginal());
		executer.playAnimationSynchronized(animations, 0);
		
	}

	@Override
	public Skill getPriorSkill() {
		return EpicFightSkills.ROLL;
	}
	
	@OnlyIn(Dist.CLIENT)
	public List<Object> getTooltipArgs(List<Object> list) {
		list.add(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.consumption));
		return list;
	}
}

