package reascer.wom.gameasset;

import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;

public class WOMColliders {
	public static final Collider NONE = new MultiOBBCollider(1, 0.0D, 0.0D, 0.0D, 0D, 0D, 0D);
	
	public static final Collider STAFF = new MultiOBBCollider(4, 0.5D, 0.5D, 1.9D, 0D, 0D, 0D);
	public static final Collider STAFF_EXTENTION = new MultiOBBCollider(4, 0.5D, 0.5D, 2.5D, 0D, 0D, 0D);
	public static final Collider STAFF_CHARYBDIS = new MultiOBBCollider(4, 0.6D, 0.6D, 2.3D, 0D, 0D, 0D);
	
	public static final Collider GREATSWORD = new MultiOBBCollider(3, 0.2D, 0.8D, 1.0D, 0D, 0D, -1.2D);
	
	public static final Collider AGONY = new MultiOBBCollider(5, 0.45D, 0.45D, 1.4D, 0D, 0D, -1.0D);
	public static final Collider AGONY_AIRSLASH = new MultiOBBCollider(4, 0.55D, 0.55D, 1.6D, 0D, 0D, -0.8D);
	public static final Collider AGONY_PLUNGE = new OBBCollider(5.0F, 2.0F, 5.0F, 0F, 0F, 0F);
	
	public static final Collider PLUNDER_PERDITION = new OBBCollider(8.0F, 4.0F, 8.0F, 0F, 0F, 0F);
	public static final Collider RUINE = new MultiOBBCollider(5, 0.4D, 0.4D, 1.45D, 0D, 0D, -1.05D);
	public static final Collider RUINE_COMET = new MultiOBBCollider(4, 0.6D, 0.6D, 1.35D, 0D, 0D, -0.95D);
	public static final Collider RUINE_REDEMPTION = new OBBCollider(1.5D, 1.5D, 1.5D, 0D, 0.0D, 0.0D);
	
	public static final Collider SHOULDER_BUMP = new OBBCollider( 0.8D, 0.8D, 1.6D, 0D, 1.2D, -0.6D);
	
	public static final Collider TORMENT = new MultiOBBCollider(3, 0.4F, 0.6F, 1.4F, 0F, -0.2F, -0.6F);
	public static final Collider TORMENT_AIRSLAM = new OBBCollider( 1.4F, 0.8F, 1.4F, 0F, 0.8F, -1.6F);
	public static final Collider TORMENT_BERSERK_AIRSLAM = new OBBCollider( 2.1F, 2.1F, 2.1F, 0F, 2.1F, -2.1F);
	public static final Collider TORMENT_BERSERK_DASHSLAM = new OBBCollider( 1.8F, 1.6F, 1.8F, 0F, 1.6F, -1.8F);
	
	public static final Collider KATANA_SHEATHED_AUTO = new OBBCollider(2.00F, 1.0F, 2.0F, 0F, 1.0F, -1.0F);
	public static final Collider KATANA_SHEATHED_DASH = new OBBCollider(1.60F, 0.7F, 2.5F, 0F, 1.0F, -0.5F);
	public static final Collider KATANA = new MultiOBBCollider(5, 0.2D, 0.3D, 1.0D, 0D, 0D, -1.0D);
	public static final Collider FATAL_DRAW = new OBBCollider(1.75D, 0.7D, 1.35D, 0D, 1.0D, -1.0D);
	public static final Collider FATAL_DRAW_DASH = new OBBCollider(1.0D, 1.0D, 2.75D, 0D, 1.2D, -0.2D);
	
	public static final Collider PUNCH = new MultiOBBCollider(4, 0.4D, 0.6D, 0.4D, 0D, 0D, 0D);
	public static final Collider ENDER_BLASTER_CROSS = new MultiOBBCollider(4, 0.4D, 0.4D, 0.4D, 0D, 0D, 0.6D);
	public static final Collider KICK = new MultiOBBCollider(4, 0.4D, 0.4D, 0.4D, 0D, 0.6D, 0D);
	public static final Collider KICK_HUGE = new MultiOBBCollider(4, 0.8D, 0.8D, 0.8D, 0D, 0.9D, 0D);
	public static final Collider ENDER_DASH = new OBBCollider(1.5D, 1.5D, 1.5D, 0D, 1.0D, -1.0D);
	public static final Collider ENDER_TISHNAW = new MultiOBBCollider(4, 1.5D, 1.5D, 1.5D, 0D, 1.0D, 0.0D);
	public static final Collider KNEE = new MultiOBBCollider(4, 0.8D, 0.8D, 0.8D, 0D, 0.6D, 0D);
	public static final Collider ENDER_LASER = new MultiOBBCollider(4, 0.3D, 20.0D, 0.3D, 0.0D, -20.2D, 0.0D);
	public static final Collider ENDER_SHOOT = new MultiOBBCollider(4, 0.3D, 3.0D, 0.3D, 0.0D, -3.1D, 0.0D);
	public static final Collider ENDER_BULLET_DASH = new MultiOBBCollider(4, 1.2D, 5.0D, 1.2D, 0.0D, -5.1D, 0.0D);
	public static final Collider ENDER_BULLET_BACKANDFORTH = new MultiOBBCollider(4, 2.5D, 10.0D, 2.5D, 0.0D, 1.0D, 0.0D);
	public static final Collider ENDER_BULLET_ALl_DIRECTION = new MultiOBBCollider(4, 2.5D, 10.0D, 10.0D, 0.0D, 1.0D, 0.0D);
	public static final Collider ENDER_BULLET_WIDE = new OBBCollider( 7.5D, 1.5D, 5D, 0.0D, 0.75D, -5D);
	public static final Collider ENDER_PISTOLERO = new OBBCollider( 2.5D, 2D, 2.5D, 0.0D, -1.5D, 0.0D);
	
	public static final Collider ANTITHEUS = new MultiOBBCollider(3, 0.4F, 1.2F, 1.0F, 0F, -0.2F, -1.7F);
	public static final Collider ANTITHEUS_AGRESSION = new MultiOBBCollider(3, 1.2F, 1.6F, 1.4F, 0F, -0.6F, -2.0F);
	public static final Collider ANTITHEUS_AGRESSION_REAP = new OBBCollider(1.2F, 1.6F, 1.6F, 0F, 0.8F, -3.40F);
	public static final Collider ANTITHEUS_GUILLOTINE = new OBBCollider(3.5F, 1.5F, 3.5F, 0F, 0F, 0F);
	public static final Collider ANTITHEUS_ASCENDED_PUNCHES = new OBBCollider(1.0F, 1.0F, 1.8F, 0F, 1.0F, -1.15F);
	public static final Collider ANTITHEUS_ASCENDED_BLINK = new OBBCollider(3.5F, 1.5F, 3.5F, 0F, 0.75F, 0.0F);
	public static final Collider ANTITHEUS_ASCENDED_DEATHFALL = new OBBCollider(5F, 3F, 5F, 0F, 0F, 0F);
	public static final Collider ANTITHEUS_SHOOT = new MultiOBBCollider(4, 0.5D, 1.5D, 0.5D, 0.0D, -1.5D, 0.0D);
	
	public static final Collider HERSCHER = new MultiOBBCollider(5, 0.2D, 0.3D, 0.8D, 0D, 0D, -0.8D);
	public static final Collider HERSCHER_THRUST = new OBBCollider(0.6F, 0.6F, 1.3F, 0F, 0.8F, -1.1F);
	public static final Collider HERSCHER_CHARGE_1 = new MultiOBBCollider(6, 0.2D, 0.4D, 1.2D, 0D, 0D, -1.2D);
	public static final Collider HERSCHER_CHARGE_2 = new MultiOBBCollider(8, 0.2D, 0.6D, 1.6D, 0D, 0D, -1.6D);
	public static final Collider HERSCHER_CHARGE_3 = new MultiOBBCollider(10, 0.2D, 0.8D, 2.0D, 0D, 0D, -2.0D);
	public static final Collider GESETZ = new MultiOBBCollider(5, 0.4D, 0.6D, 1.0D, 0D, 0D, 0.4D);
	public static final Collider GESETZ_KRUMMEN = new MultiOBBCollider(5, 0.4D, 0.6D, 1.6D, 0D, 0D, 0.0D);
	public static final Collider GESETZ_INSET_LARGE = new MultiOBBCollider(5, 0.9D, 0.6D, 1.0D, 0.3D, 0D, 0.4D);
	
	public static final Collider MOONLESS = new MultiOBBCollider(4, 0.2D, 1.5D, 0.4D, 0.0D, 1.0D, 0.2D);
	public static final Collider MOONLESS_BYPASS = new MultiOBBCollider(4, 0.2D, 1.8D, 0.7D, 0.0D, 1.0D, 0.3D);
	public static final Collider LUNAR_ECHO = new OBBCollider( 6.0D, 6.0D, 6.0D, 0.0D, 0.0D, -6.0D);
	
	public static final Collider SOLAR = new MultiOBBCollider(4, 0.4F, 0.6F, 1.8F, 0F, 0.0F, -1.0F);
}
