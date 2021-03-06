package net.wayward_realms.waywardprofessions;

import net.wayward_realms.waywardlib.professions.ToolType;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ProfessionInfo implements ConfigurationSerializable {

    private Map<ToolType, Integer> maxToolDurability = new EnumMap<>(ToolType.class);
    private Map<Material, Integer> craftEfficiency = new EnumMap<>(Material.class);
    private Map<Material, Integer> miningEfficiency = new EnumMap<>(Material.class);
    private int brewingEfficiency;

    public int getMaxToolDurability(ToolType type) {
        if (maxToolDurability.get(type) == null) return 0;
        return maxToolDurability.get(type);
    }

    public void setMaxToolDurability(ToolType type, int durability) {
        maxToolDurability.put(type, durability);
    }

    public int getCraftEfficiency(Material material) {
        if (craftEfficiency.get(material) == null) return 0;
        return craftEfficiency.get(material);
    }

    public void setCraftEfficiency(Material material, int efficiency) {
        craftEfficiency.put(material, Math.min(efficiency, 100));
    }

    public int getMiningEfficiency(Material material) {
        if (miningEfficiency.get(material) == null) return 0;
        return miningEfficiency.get(material);
    }

    public void setMiningEfficiency(Material material, int efficiency) {
        miningEfficiency.put(material, Math.min(efficiency, 100));
    }

    public int getBrewingEfficiency() {
        return brewingEfficiency;
    }

    public void setBrewingEfficiency(int efficiency) {
        this.brewingEfficiency = Math.min(efficiency, 100);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialised = new HashMap<>();
        serialised.put("max-tool-durability", maxToolDurability);
        serialised.put("craft-efficiency", craftEfficiency);
        serialised.put("mining-efficiency", miningEfficiency);
        serialised.put("brewing-efficiency", brewingEfficiency);
        return serialised;
    }

    public static ProfessionInfo deserialize(Map<String, Object> serialised) {
        ProfessionInfo deserialised = new ProfessionInfo();
        deserialised.maxToolDurability = (Map<ToolType, Integer>) serialised.get("max-tool-durability");
        deserialised.craftEfficiency = (Map<Material, Integer>) serialised.get("craft-efficiency");
        deserialised.miningEfficiency = (Map<Material, Integer>) serialised.get("mining-efficiency");
        deserialised.brewingEfficiency = (int) serialised.get("brewing-efficiency");
        return deserialised;
    }
}
