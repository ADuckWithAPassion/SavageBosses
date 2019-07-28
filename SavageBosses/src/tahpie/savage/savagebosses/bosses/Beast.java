package tahpie.savage.savagebosses.bosses;

import java.lang.reflect.Field;
import java.util.EnumSet;

import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.EntityLiving;
import net.minecraft.server.v1_14_R1.EntityPanda;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.EnumMoveType;
import net.minecraft.server.v1_14_R1.GenericAttributes;
import net.minecraft.server.v1_14_R1.IJumpable;
import net.minecraft.server.v1_14_R1.MathHelper;
import net.minecraft.server.v1_14_R1.MobEffects;
import net.minecraft.server.v1_14_R1.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_14_R1.PathfinderGoal;
import net.minecraft.server.v1_14_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_14_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_14_R1.PathfinderGoalTame;
import net.minecraft.server.v1_14_R1.RandomPositionGenerator;
import net.minecraft.server.v1_14_R1.Vec3D;
import net.minecraft.server.v1_14_R1.World;
import savageclasses.Assassin;
import tahpie.savage.savagebosses.SavageBosses;

public class Beast extends EntityPanda implements IJumpable{

	public Panda parent;
	private Player rider;
	private EntityPlayer riderHandle;
	private double SPAWN_X;
	private double SPAWN_Y;
	private double SPAWN_Z;
	private double jumpPower;
	public Beast(EntityTypes<? extends EntityPanda> entitytypes, World world, Player rider) {
		super(entitytypes, world);
		getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(30.0D);
		getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(20.0D);

		SPAWN_X = rider.getLocation().getX();
		SPAWN_Y = rider.getLocation().getY();
		SPAWN_Z = rider.getLocation().getZ();
		this.enderTeleportAndLoad(SPAWN_X, SPAWN_Y, SPAWN_Z);
		parent = (Panda)this.getBukkitEntity();
		world.addEntity(this);
		parent.setCustomName(ChatColor.LIGHT_PURPLE+rider.getName()+"'s Beast");
		parent.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*20, 2));
		this.rider = rider;
		this.riderHandle = ((CraftPlayer)rider).getHandle();
		parent.setCustomNameVisible(true);
		if(!(rider.isInsideVehicle())) {
			//parent.addPassenger(rider);	
			riderHandle.startRiding(this);
		}
		jumpPower = 5;
		this.onGround = true;
	}
	@Override
	protected void initPathfinder() {
		super.initPathfinder();
		this.goalSelector = new PathfinderGoalSelector(world != null && world.getMethodProfiler() != null ? world.getMethodProfiler() : null);
		this.targetSelector = new PathfinderGoalSelector(world != null && world.getMethodProfiler() != null ? world.getMethodProfiler() : null);
		this.setMainGene(Gene.AGGRESSIVE);
		this.goalSelector.a(0, new PathfinderGoalFloat(this));
		//this.goalSelector.a(1,new PathFinderGoalRide(this));

	}

	@EventHandler
	public void onSteer(PacketPlayInSteerVehicle event) {
		Log.info(event.a());
		Log.info(event.b());
		Log.info(event.c());
		Log.info(event.d());
		Log.info(event.e());
	}

	@Override
	public void e(Vec3D vec3d) {
		if(isAlive() && hasSinglePlayerPassenger()) {

			EntityLiving entityliving = (EntityLiving)this.getRidingPassenger();

			this.setYawPitch(entityliving.yaw, entityliving.pitch);
			float f2 = entityliving.bb * 0.5f; // sideways
			float f1 = entityliving.bd; // forward	
            Field jump = null; //Jumping
            try {
                jump = EntityLiving.class.getDeclaredField("jumping");
            } catch (NoSuchFieldException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (SecurityException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            jump.setAccessible(true);
			if(f1>0) {
				this.setMot(this.getLookDirection().getX()*1, this.getMot().y, this.getLookDirection().getZ()*1);				
			}
            if (jump != null && this.onGround) {    // Wouldn't want it jumping while on the ground would we?
                try {
                    if (jump.getBoolean(entityliving)) {
                        double jumpHeight = 0.5D;//Here you can set the jumpHeight
                        this.setMot(this.getMot().x, jumpHeight, this.getMot().z);    // Used all the time in NMS for entity jumping
                    }
                } catch (Exception e) {
                   // e.printStackTrace();
                }
            }
		}
        super.e(this.getMot());
	}
    
	public class PathFinderGoalRide extends PathfinderGoal{
		private EntityPanda entity;
		public PathFinderGoalRide(EntityPanda entity){
			this.entity = entity;
		}
		@Override
		public boolean a(){
			//			Vector direction = rider.getLocation().getDirection();
			//			direction = direction.setY(0);
			//			direction = direction.normalize();
			//			direction = direction.setY(-0.05);
			//			entity.setMot(direction.getX(),entity.getMot().y,direction.getZ());
			//            entity.move(EnumMoveType.SELF, entity.getMot());
			//            
			//            entity.yaw = rider.getLocation().getYaw();
			//            entity.pitch = rider.getLocation().getPitch();
			return true;
		}
	}
	@Override
	public Entity getRidingPassenger() {
		return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
	}
	@Override
	public boolean F_() {
		Log.info("F_");
		return false;
	}
	@Override
	public void b(int arg0) {
		Log.info("b");
		
	}
	@Override
	public void c() {
		Log.info("C");
	}
}


