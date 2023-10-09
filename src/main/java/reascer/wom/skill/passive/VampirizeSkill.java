package reascer.wom.skill.passive;

import java.util.UUID;

import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class VampirizeSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("417b1eb8-05e1-11ed-b939-0242ac120002");
	
	public VampirizeSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
			container.getExecuter().getOriginal().heal(event.getAttackDamage() * 0.10f);
        });
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
	}
}