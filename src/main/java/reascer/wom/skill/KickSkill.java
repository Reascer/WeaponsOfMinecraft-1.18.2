package reascer.wom.skill;

import net.minecraft.network.FriendlyByteBuf;
import reascer.wom.gameasset.WOMAnimations;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class KickSkill extends Skill {
	private StaticAnimation animations;
	
	public KickSkill(Builder builder) {
		super(builder);
		this.animations = WOMAnimations.KICK_AUTO_1;
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		super.executeOnServer(executer, args);
		executer.playAnimationSynchronized(WOMAnimations.KICK_AUTO_1, 0);
		
	}
}
