package me.chesspiece.fof.fountain;

import me.chesspiece.fof.util.location.CustomLocation;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;

public class FountainArea {

    private final String id;
    private final CustomLocation min, max;

    public FountainArea(String id, CustomLocation cornerA, CustomLocation cornerB) {
        Validate.notNull(id, "ID cannot be null.");
        Validate.notNull(cornerA, "CustomLocation A cannot be null.");
        Validate.notNull(cornerB, "CustomLocation B cannot be null.");
        Validate.notNull(cornerA.getWorld(), "World cannot be null.");
        Validate.notNull(cornerB.getWorld(), "World cannot be null.");

        this.id = id;

        //Validation for min/max locations
        double minX = cornerA.getX(), minZ = cornerA.getZ();
        double maxX = cornerB.getX(), maxZ = cornerB.getZ();
        double maxY = cornerB.getY(), minY = cornerA.getY();
        if (minX > maxX) {
            double lastMinX = minX;
            minX = maxX;
            maxX = lastMinX;
        }
        if (minZ > maxZ) {
            double lastMinZ = minZ;
            minZ = maxZ;
            maxZ = lastMinZ;
        }
        if (minY > maxY) {
            double lastMinY = minY;
            minY = maxY;
            maxY = lastMinY;
        }

        this.min = new CustomLocation(cornerA.getWorld(), minX, minY, minZ);
        this.max = new CustomLocation(cornerA.getWorld(), maxX, maxY, maxZ);
    }

    /**
     * Checks if the location is inside the fountain area
     *
     * @param location the location to check against
     * @return true if they are within the fountain area, false otherwise
     */
    public boolean isInside(Location location) {
        Validate.notNull(location, "Location cannot be null.");

        return location.getX() >= min.getX() && location.getZ() >= min.getZ() &&
                location.getX() <= max.getX() && location.getZ() <= max.getZ() &&
                location.getY() >= min.getY() && location.getY() <= max.getY();
    }

    /* Getters */

    public String getId() {
        return id;
    }

    public CustomLocation getMax() {
        return max;
    }

    public CustomLocation getMin() {
        return min;
    }
}
