package net.wayward_realms.waywardlocks;

import net.wayward_realms.waywardlib.lock.LockPlugin;
import net.wayward_realms.waywardlib.util.serialisation.SerialisableLocation;
import net.wayward_realms.waywardlocks.keyring.KeyringManager;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WaywardLocks extends JavaPlugin implements LockPlugin {

    private KeyringManager keyringManager;

    private Set<Block> locked = new HashSet<>();
    private Set<String> unclaiming = new HashSet<>();
    private Set<String> getkey = new HashSet<>();

    @Override
    public void onEnable() {
        keyringManager = new KeyringManager(this);
        getCommand("keyring").setExecutor(new KeyringCommand(this));
        getCommand("lock").setExecutor(new LockCommand(this));
        getCommand("unlock").setExecutor(new UnlockCommand(this));
        getCommand("getkey").setExecutor(new GetKeyCommand(this));
        getServer().getPluginManager().registerEvents(new CraftItemListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        ItemStack lockpickItem = new ItemStack(Material.IRON_INGOT);
        ItemMeta lockpickMeta = lockpickItem.getItemMeta();
        lockpickMeta.setDisplayName("Lockpick");
        lockpickItem.setItemMeta(lockpickMeta);
        ShapedRecipe lockpickRecipe = new ShapedRecipe(lockpickItem);
        lockpickRecipe.shape("L", "I", "I").setIngredient('L', Material.LEVER).setIngredient('I', Material.IRON_INGOT);
        getServer().addRecipe(lockpickRecipe);
        ItemStack lockItem = new ItemStack(Material.IRON_INGOT);
        ItemMeta lockMeta = lockItem.getItemMeta();
        lockMeta.setDisplayName("Lock");
        List<String> lore = new ArrayList<>();
        lore.add("Used for locking chests");
        lockMeta.setLore(lore);
        lockItem.setItemMeta(lockMeta);
        ShapedRecipe lockRecipe = new ShapedRecipe(lockItem);
        lockRecipe.shape("I", "B").setIngredient('I', Material.IRON_INGOT).setIngredient('B', Material.IRON_BLOCK);
        getServer().addRecipe(lockRecipe);
    }

    @Override
    public void onDisable() {
        saveState();
    }

    @Override
    public String getPrefix() {
        //return "" + ChatColor.DARK_GRAY + ChatColor.MAGIC + "|" + ChatColor.RESET + ChatColor.BLUE + "WaywardLocks" + ChatColor.DARK_GRAY + ChatColor.MAGIC + "| " + ChatColor.RESET;
        return "";
    }

    @Override
    public void loadState() {
        File lockDirectory = new File(this.getDataFolder().getPath() + File.separator + "locks");
        if (lockDirectory.exists()) {
            for (File worldDirectory : lockDirectory.listFiles()) {
                for (File xDirectory : worldDirectory.listFiles()) {
                    for (File yDirectory : xDirectory.listFiles()) {
                        for (File zDirectory : yDirectory.listFiles()) {
                            File lockFile = new File(zDirectory.getPath() + File.separator + "lock.yml");
                            if (lockFile.exists()) {
                                try {
                                    YamlConfiguration lockConfig = new YamlConfiguration();
                                    lockConfig.load(lockFile);
                                    if (getServer().getWorld(worldDirectory.getName()) != null) {
                                        locked.add(getServer().getWorld(worldDirectory.getName()).getBlockAt(Integer.parseInt(xDirectory.getName()), Integer.parseInt(yDirectory.getName()), Integer.parseInt(zDirectory.getName())));
                                    }
                                } catch (FileNotFoundException exception) {
                                    exception.printStackTrace();
                                } catch (IOException exception) {
                                    exception.printStackTrace();
                                } catch (InvalidConfigurationException exception) {
                                    exception.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
            deleteDirectory(lockDirectory);
        }
        File locksFile = new File(getDataFolder(), "locks.yml");
        YamlConfiguration lockConfig = new YamlConfiguration();
        if (locksFile.exists()) {
            try {
                lockConfig.load(locksFile);
            } catch (IOException exception) {
                exception.printStackTrace();
            } catch (InvalidConfigurationException exception) {
                exception.printStackTrace();
            }
            List<SerialisableLocation> lockedLocations = (List<SerialisableLocation>) lockConfig.getList("locks");
            for (SerialisableLocation lockedLocation : lockedLocations) {
                locked.add(lockedLocation.toLocation().getWorld().getBlockAt(lockedLocation.toLocation()));
            }
        }
        keyringManager.loadKeyrings();
    }

    private void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                    file.delete();
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    @Override
    public void saveState() {
        File locksFile = new File(getDataFolder(), "locks.yml");
        YamlConfiguration lockConfig = new YamlConfiguration();
        List<SerialisableLocation> lockedLocations = new ArrayList<>();
        for (Block block : locked) {
            lockedLocations.add(new SerialisableLocation(block.getLocation()));
        }
        lockConfig.set("locks", lockedLocations);
        try {
            lockConfig.save(locksFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        keyringManager.saveKeyrings();
    }

    @Override
    public int getLockId(Block block) {
        return 0;
    }

    @Override
    public boolean isLocked(Block block) {
        return locked.contains(block);
    }

    @Override
    public ItemStack lock(Block block) {
        ItemStack key = new ItemStack(Material.IRON_INGOT);
        locked.add(block);
        ItemMeta keyMeta = key.getItemMeta();
        keyMeta.setDisplayName("Key");
        List<String> keyLore = new ArrayList<>();
        keyLore.add(block.getWorld().getName() + "," + block.getLocation().getBlockX() + "," + block.getLocation().getBlockY() + "," + block.getLocation().getBlockZ());
        keyMeta.setLore(keyLore);
        key.setItemMeta(keyMeta);
        return key;
    }

    @Override
    public void unlock(Block block) {
        locked.remove(block);
    }

    public boolean isClaiming(OfflinePlayer offlinePlayer) {
        if (offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();
            if (player.getItemInHand() != null) {
                ItemStack item = player.getItemInHand();
                if (item.hasItemMeta()) {
                    if (item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore()) {
                        return item.getItemMeta().getDisplayName().equalsIgnoreCase("Lock") && item.getItemMeta().getLore().contains("Used for locking chests");
                    }
                }
            }
        }
        return false;
    }

    public boolean isUnclaiming(OfflinePlayer player) {
        return unclaiming.contains(player.getName());
    }

    public void setUnclaiming(OfflinePlayer player, boolean unclaim) {
        if (unclaim) {
            unclaiming.add(player.getName());
        } else {
            unclaiming.remove(player.getName());
        }
    }

    public boolean isGettingKey(OfflinePlayer player) {
        return getkey.contains(player.getName());
    }

    public void setGettingKey(OfflinePlayer player, boolean gettingKey) {
        if (gettingKey) {
            getkey.add(player.getName());
        } else {
            getkey.remove(player.getName());
        }
    }

    public KeyringManager getKeyringManager() {
        return keyringManager;
    }
}
