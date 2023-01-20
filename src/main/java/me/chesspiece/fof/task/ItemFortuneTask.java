package me.chesspiece.fof.task;

import me.chesspiece.fof.FountainOfFortune;
import me.chesspiece.fof.fountain.FountainManager;
import me.chesspiece.fof.util.ui.ItemCreator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.UUID;

public class ItemFortuneTask extends BukkitRunnable {

    private final FountainManager fountainManager;
    private final ArmorStand entity;
    private final UUID uuid;
    private final Location startLocation;
    private final double targetYLevel;

    public ItemFortuneTask(FountainOfFortune plugin, UUID uuid, Location startLocation, double targetYLevel) {
        this.fountainManager = plugin.getFountainManager();

        this.uuid = uuid;
        this.startLocation = startLocation;

        this.entity = this.createEntity();
        this.targetYLevel = targetYLevel;
    }

    @Override
    public void run() {
        if (this.entity == null) {
            this.cancel();
            this.fountainManager.getItemTasks().remove(this);
            return;
        }

        Location location = this.entity.getLocation();
        float newYaw = location.getYaw() + this.getYawSpeed(location);
        if (newYaw > 360) newYaw = 0;

        location.setYaw(newYaw);
        location.setY(location.getY() + 0.03);

        this.entity.teleport(location);

        if (location.getY() >= this.targetYLevel) {
            this.entity.remove();

            World world = location.getWorld();
            world.playSound(location, Sound.ENTITY_ITEM_PICKUP, 1F, 1F);
            world.spawnParticle(Particle.FIREWORKS_SPARK, location.add(0, 1, 0), 6);

            this.fountainManager.fountainEvent(this.uuid);

            this.cancel();
            this.fountainManager.getItemTasks().remove(this);
        }
    }

    private ArmorStand createEntity() {
        ArmorStand armorStand = (ArmorStand) this.startLocation.getWorld().spawnEntity(this.startLocation, EntityType.ARMOR_STAND);
        armorStand.setGravity(false);
        armorStand.setArms(true);
        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(false);
        armorStand.setItem(EquipmentSlot.HEAD, new ItemCreator(this.fountainManager.getKnuckle()).flags().get());
        armorStand.setRightArmPose(new EulerAngle(- 1.58D, - 1.58D, 0.0D));
        return armorStand;
    }

    public ArmorStand getEntity() {
        return entity;
    }

    private float getYawSpeed(Location currentLocation) {
        double difference = currentLocation.getY() - this.startLocation.getY();

        if (difference > 1.3) return 30f;
        else if (difference > 1) return 25f;
        else if (difference > 0.5) return 20f;
        else return 15f;
    }
}
