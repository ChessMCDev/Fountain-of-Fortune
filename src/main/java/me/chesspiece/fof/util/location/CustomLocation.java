package me.chesspiece.fof.util.location;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.StringJoiner;

public class CustomLocation {

    private String world;

    private double x;
    private double y;
    private double z;

    private float yaw;
    private float pitch;

    public CustomLocation(double x, double y, double z) {
        this(x, y, z, 0.0F, 0.0F);
    }

    public CustomLocation(String world, double x, double y, double z) {
        this(world, x, y, z, 0.0F, 0.0F);
    }

    public CustomLocation(double x, double y, double z, float yaw, float pitch) {
        this("world", x, y, z, yaw, pitch);
    }

    public CustomLocation(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;

        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Deserializes a string to create a custom location. You serialize a location using {@link CustomLocation#locationToString()}
     *
     * @param string the serialized string
     * @return the deserialized form of the string
     */
    public static CustomLocation stringToLocation(String string) {
        String[] split = string.split(", ");

        double x = Double.parseDouble(split[0]);
        double y = Double.parseDouble(split[1]);
        double z = Double.parseDouble(split[2]);

        CustomLocation customLocation = new CustomLocation(x, y, z);

        if (split.length == 4) {
            customLocation.setWorld(split[3]);
        } else if (split.length >= 5) {
            customLocation.setYaw(Float.parseFloat(split[3]));
            customLocation.setPitch(Float.parseFloat(split[4]));

            if (split.length >= 6) {
                customLocation.setWorld(split[5]);
            }
        }
        return customLocation;
    }

    /**
     * Translates the location into a string ready for the config
     *
     * @return the string for the config
     */
    public String locationToString() {
        StringJoiner joiner = new StringJoiner(", ");
        joiner.add(Double.toString(Math.round(this.x)));
        joiner.add(Double.toString(Math.round(this.y)));
        joiner.add(Double.toString(Math.round(this.z)));
        if (!this.world.equals("world")) {
            joiner.add(this.world);
        }
        return joiner.toString();
    }

    public Location toLocation() {
        return new Location(this.toBukkitWorld(), this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public World toBukkitWorld() {
        if (this.world == null) return Bukkit.getWorlds().get(0);
        else return Bukkit.getWorld(this.world);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof CustomLocation location)) return false;

        return location.x == this.x && location.y == this.y && location.z == this.z && location.pitch == this.pitch && location.yaw == this.yaw;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("x", this.x)
                .append("y", this.y)
                .append("z", this.z)
                .append("yaw", this.yaw)
                .append("pitch", this.pitch)
                .append("world", this.world)
                .toString();
    }

    /* Getters & Setters */
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public String getWorld() {
        return world;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setZ(double z) {
        this.z = z;
    }
}