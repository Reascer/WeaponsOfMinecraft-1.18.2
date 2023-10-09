package reascer.wom.events;

import java.util.UUID;

import net.minecraft.world.item.UseAnim;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import reascer.wom.main.WeaponsOfMinecraft;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.forgeevent.UpdatePlayerMotionEvent;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.skill.CapabilitySkill;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

//@Mod.EventBusSubscriber(modid = WeaponOfMinecraft.MODID , bus = EventBusSubscriber.Bus.MOD)
public class WOMEntityEvents{
	
	/*
	@SubscribeEvent
	public void updateMotion(UpdatePlayerMotionEvent event) {
		UseAnim useAnim = event.getPlayerPatch().getOriginal().getItemInHand(event.getPlayerPatch().getOriginal().getUsedItemHand()).getUseAnimation();
		if (useAnim == UseAnim.EAT) {
			event.getPlayerPatch().getOriginal().sendMessage(new TextComponent("eating"), UUID.randomUUID());
			event.getPlayerPatch().currentCompositeMotion = LivingMotions.DIGGING;
		}
	}
	*/
}
