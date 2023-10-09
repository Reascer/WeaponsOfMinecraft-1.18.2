package reascer.wom.skill;

import java.util.UUID;

import net.minecraft.server.level.ServerPlayer;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.skill.passive.MeditationSkill;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPPlayAnimation;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class SatsujinPassive extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("010e5bfa-e6a2-11ec-8fea-0242ac120002");
	public static final SkillDataKey<Boolean> SHEATH = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	
	
	public SatsujinPassive(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		container.getDataManager().registerData(SHEATH);
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			StaticAnimation[] resetAnimations = {
					WOMAnimations.KATANA_AUTO_1,
					WOMAnimations.KATANA_AUTO_2,
					WOMAnimations.KATANA_AUTO_3,
					WOMAnimations.KATANA_DASH,
					WOMAnimations.HERRSCHER_AUSROTTUNG,
					WOMAnimations.KATANA_SHEATHED_AUTO_1,
					WOMAnimations.KATANA_SHEATHED_AUTO_2,
					WOMAnimations.KATANA_SHEATHED_AUTO_3,
					WOMAnimations.KATANA_GUARD,
					WOMAnimations.KATANA_GUARD_HIT,
					Animations.BIPED_HIT_SHORT,
					Animations.BIPED_HIT_LONG};
			
			for (StaticAnimation staticAnimation : resetAnimations) {
				if (event.getAnimation().equals(staticAnimation)) {
					container.getSkill().setConsumptionSynchronize(event.getPlayerPatch(), 0.0F);
					container.getSkill().setStackSynchronize(event.getPlayerPatch(), 0);
				}
			}
			
			
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
			this.onReset(container);
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID);
	}
	
	@Override
	public void onReset(SkillContainer container) {
		PlayerPatch<?> executer = container.getExecuter();
		
		if (!executer.isLogicalClient()) {
			if (container.getDataManager().getDataValue(SHEATH)) {
				ServerPlayerPatch playerpatch = (ServerPlayerPatch)executer;
				container.getDataManager().setDataSync(SHEATH, false, playerpatch.getOriginal());
				playerpatch.modifyLivingMotionByCurrentItem();
				container.getSkill().setConsumptionSynchronize(playerpatch, 0);
			}
		}
	}
	
	@Override
	public void setConsumption(SkillContainer container, float value) {
		PlayerPatch<?> executer = container.getExecuter();
		
		if (!executer.isLogicalClient()) {
			if (this.consumption < value) {
				ServerPlayer serverPlayer = (ServerPlayer) executer.getOriginal();
				if (!container.getDataManager().getDataValue(SHEATH)) {
					container.getDataManager().setDataSync(SHEATH, true, serverPlayer);
					boolean flag = false;
					if (container.getExecuter().getSkill(WOMSkills.MEDITATION) == null) {
						flag = true;
					} else {
						if (container.getExecuter().getSkill(WOMSkills.MEDITATION).getDataManager().getDataValue(MeditationSkill.TIMER) == 0) {
							flag = true;
						}
					}
					if (flag) {
						((ServerPlayerPatch) executer).modifyLivingMotionByCurrentItem();
						SPPlayAnimation msg3 = new SPPlayAnimation(WOMAnimations.KATANA_SHEATHE, serverPlayer.getId(), 0.0F);
						EpicFightNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(msg3, serverPlayer);
					}
				}else {
					((ServerPlayerPatch) executer).modifyLivingMotionByCurrentItem();
				}
				container.getDataManager().setDataSync(SHEATH, true, serverPlayer);
			}
		}
		
		super.setConsumption(container, value);
	}
	
	@Override
	public boolean shouldDeactivateAutomatically(PlayerPatch<?> executer) {
		return true;
	}
	
	@Override
	public float getCooldownRegenPerSecond(PlayerPatch<?> player) {
		return player.getOriginal().isUsingItem() ? 0.0F : 1.0F;
	}
}