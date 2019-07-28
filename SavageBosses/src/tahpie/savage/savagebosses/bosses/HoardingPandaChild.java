package tahpie.savage.savagebosses.bosses;

import org.bukkit.Location;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_14_R1.EntityInsentient;
import net.minecraft.server.v1_14_R1.EntityLiving;
import net.minecraft.server.v1_14_R1.EntityPanda;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.EnumMoveType;
import net.minecraft.server.v1_14_R1.GenericAttributes;
import net.minecraft.server.v1_14_R1.PathfinderGoal;
import net.minecraft.server.v1_14_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_14_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_14_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_14_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_14_R1.World;
import tahpie.savage.savagebosses.SavageBosses;

public class HoardingPandaChild extends EntityPanda{

	public Panda parent;
	private Player target;
	private double SPAWN_X;
	private double SPAWN_Y;
	private double SPAWN_Z;
	public HoardingPandaChild(EntityTypes<? extends EntityPanda> entitytypes, World world, Player target, SavageBosses SB) {
		super(entitytypes, world);
		getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(30.0D);
		getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(5.0D);
		
		SPAWN_X = target.getLocation().getX();
		SPAWN_Y = target.getLocation().getY();
		SPAWN_Z = target.getLocation().getZ();
		this.enderTeleportAndLoad(SPAWN_X, SPAWN_Y, SPAWN_Z);
		parent = (Panda)this.getBukkitEntity();
		world.addEntity(this);
		parent.setCustomName(ChatColor.LIGHT_PURPLE+"Hoarding Panda's Child");
		parent.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*20, 1));
		parent.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*20, 2));
		this.target = target;
		parent.setCustomNameVisible(true);
		parent.setBaby();
		if(!(target.isInsideVehicle())) {
			parent.addPassenger(target);	
		}
		parent.setMetadata("minion", new FixedMetadataValue(SB, true));

	}
	@Override
	protected void initPathfinder() {
		super.initPathfinder();
        this.goalSelector = new PathfinderGoalSelector(world != null && world.getMethodProfiler() != null ? world.getMethodProfiler() : null);
        this.targetSelector = new PathfinderGoalSelector(world != null && world.getMethodProfiler() != null ? world.getMethodProfiler() : null);
        this.setMainGene(Gene.WORRIED);
        this.goalSelector.a(0,new PathFinderGoalWalkToLoc(this));
	}

	public class PathFinderGoalWalkToLoc extends PathfinderGoal{
		private EntityPanda entity;
		private Location destination;
		public PathFinderGoalWalkToLoc(EntityPanda entity){
			this.entity = entity;
			this.destination = new Location(entity.getBukkitEntity().getWorld(),92, 78,-364); // must not be run during init 
		}
		@Override
		public boolean a(){
			Vector direction = destination.clone().subtract(entity.getBukkitEntity().getLocation()).toVector();
			direction = direction.setY(0);
			if(direction.length() <= 3) {
				return false;
			}
			direction = direction.normalize();
			direction = direction.multiply(0.2);
			direction = direction.setY(-0.05);
			entity.setMot(direction.getX(),entity.getMot().y,direction.getZ());
            entity.move(EnumMoveType.SELF, entity.getMot());
            
            entity.yaw = entity.getBukkitEntity().getLocation().setDirection(direction.toLocation(entity.getBukkitEntity().getWorld()).toVector()).getYaw();
            entity.pitch = entity.getBukkitEntity().getLocation().setDirection(direction.toLocation(entity.getBukkitEntity().getWorld()).toVector()).getPitch();
            return false;
		}
	}
}
