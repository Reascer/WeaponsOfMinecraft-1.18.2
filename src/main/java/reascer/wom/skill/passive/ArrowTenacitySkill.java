package reascer.wom.skill.passive;

import java.util.UUID;

import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class ArrowTenacitySkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("8f274f40-ea63-11ec-8fea-0242ac120002");
	
	public ArrowTenacitySkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_POST, EVENT_UUID, (event) -> {
            if (event.getDamageSource() instanceof IndirectEntityDamageSource) {
                event.getDamageSource().setStunType(StunType.NONE);
            }
        });
		/*
		container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_PRE, EVENT_UUID, (event) -> {
			//System.out.println(event.getDamageSource());
			//container.getExecuter().getOriginal().sendMessage(new TextComponent("Damage source: "+event.getDamageSource()), UUID.randomUUID());
			if (event.getDamageSource().getDirectEntity() != null) {
				if (event.getDamageSource().getDirectEntity().getType() == EntityType.ARROW) {
					if (event.getDamageSource() instanceof ExtendedDamageSource) {
						if (((ExtendedDamageSource)event.getDamageSource()).getStunType() != ExtendedDamageSource.StunType.NONE) {
							event.setResult(AttackResult.ResultType.BLOCKED);
							//container.getExecuter().getOriginal().sendMessage(new TextComponent("Stuntype pre: "+((ExtendedDamageSource)event.getDamageSource()).getStunType()), UUID.randomUUID());
							((ExtendedDamageSource)event.getDamageSource()).setStunType(ExtendedDamageSource.StunType.NONE);
							//container.getExecuter().getOriginal().sendMessage(new TextComponent("Stuntype after: "+((ExtendedDamageSource)event.getDamageSource()).getStunType()), UUID.randomUUID());
							event.getPlayerPatch().getOriginal().hurt(DamageSource.mobAttack((LivingEntity) ((AbstractArrow)event.getDamageSource().getDirectEntity()).getOwner()), event.getAmount());
							event.setCanceled(true);
						}
					}
				}
			}
		});
		*/
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
	}
}