package net.wayward_realms.waywardlib.util.location;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Operations to simplify common location tasksWaywardPlugin
 *
 */
public class LocationUtils {

    /**
     * Parses a location from a string argument
     *
     * @param locationString the location as a string, with each part separated by commas. May either be in the format world,x,y,z or world,x,y,z,yaw,pitch
     * @return the location
     */
    public static Location parseLocation(String locationString) {
        if (StringUtils.countMatches(locationString, ",") == 3) {
            World world = Bukkit.getWorld(locationString.split(",")[0]);
            try {
                double x = Double.parseDouble(locationString.split(",")[1]);
                double y = Double.parseDouble(locationString.split(",")[2]);
                double z = Double.parseDouble(locationString.split(",")[3]);
                if (world != null) {
                    return new Location(world, x, y, z);
                } else {
                    throw new IllegalArgumentException("Unparsable location!");
                }
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Unparsable location!", exception);
            }
        } else if (StringUtils.countMatches(locationString, ",") == 5) {
            World world = Bukkit.getWorld(locationString.split(",")[0]);
            try {
                double x = Double.parseDouble(locationString.split(",")[1]);
                double y = Double.parseDouble(locationString.split(",")[2]);
                double z = Double.parseDouble(locationString.split(",")[3]);
                float yaw = Float.parseFloat(locationString.split(",")[4]);
                float pitch = Float.parseFloat(locationString.split(",")[5]);
                if (world != null) {
                    return new Location(world, x, y, z, yaw, pitch);
                } else {
                    throw new IllegalArgumentException("Unparsable location!");
                }
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Unparsable location!", exception);
            }
        }
        throw new IllegalArgumentException("Unparsable location!");
    }

}
