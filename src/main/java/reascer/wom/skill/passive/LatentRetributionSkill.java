package reascer.wom.skill.passive;

import java.util.UUID;

import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class LatentRetributionSkill extends PainAnticipationSkill {
	private static final UUID EVENT_UUID = UUID.fromString("44fd425a-043d-11ed-b939-0242ac120002");
	
	public LatentRetributionSkill(Builder<? extends Skill> builder) {
		super(builder);
		maxtimer = 80;
		maxduree = 80;
		damage_reduction = 0;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		
		container.getExecuter().getEventListener().addEventListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID, (event) -> {
			if (container.getDataManager().getDataValue(ACTIVE)) {
				float attackDamage = event.getDamage();
				event.setDamage(attackDamage * 1.4f);
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
		container.getExecuter().getEventListener().removeListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID);
	}
}