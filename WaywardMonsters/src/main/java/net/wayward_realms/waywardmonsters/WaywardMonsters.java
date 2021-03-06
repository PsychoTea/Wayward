package net.wayward_realms.waywardmonsters;

import net.wayward_realms.waywardlib.classes.Stat;
import net.wayward_realms.waywardlib.monsters.MonstersPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class WaywardMonsters extends JavaPlugin implements MonstersPlugin {

    private EntityLevelManager entityLevelManager;

    @Override
    public void onEnable() {
        entityLevelManager = new EntityLevelManager(this);
        getCommand("zeropoint").setExecutor(new ZeroPointCommand(this));
        getCommand("viewzeropoints").setExecutor(new ViewZeroPointsCommand(this));
    }

    @Override
    public Collection<Location> getZeroPoints() {
        return entityLevelManager.getZeroPoints();
    }

    @Override
    public void addZeroPoint(Location zeroPoint) {
        entityLevelManager.addZeroPoint(zeroPoint);
    }

    @Override
    public void removeZeroPoint(Location zeroPoint) {
        entityLevelManager.removeZeroPoint(zeroPoint);
    }

    @Override
    public int getEntityLevel(Entity entity) {
        return entityLevelManager.getEntityLevel(entity);
    }

    @Override
    public int getEntityStatValue(Entity entity, Stat stat) {
        return entityLevelManager.getEntityStatValue(entity);
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public void loadState() {

    }

    @Override
    public void saveState() {

    }

    public EntityLevelManager getEntityLevelManager() {
        return entityLevelManager;
    }
}
