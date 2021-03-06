package net.wayward_realms.waywardcombat;

import net.wayward_realms.waywardlib.character.Character;
import net.wayward_realms.waywardlib.character.CharacterPlugin;
import net.wayward_realms.waywardlib.combat.Turn;
import net.wayward_realms.waywardlib.skills.Skill;
import net.wayward_realms.waywardlib.skills.SkillType;
import net.wayward_realms.waywardlib.skills.SkillsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashSet;
import java.util.Set;

public class InventoryClickListener implements Listener {
	
	private WaywardCombat plugin;
	
	public InventoryClickListener(WaywardCombat plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		CharacterPlugin characterPlugin = plugin.getServer().getServicesManager().getRegistration(CharacterPlugin.class).getProvider();
		if (event.getInventory().getTitle().equals("Skill type")) {
			event.setCancelled(true);
			if (event.getSlot() == 2 || event.getSlot() == 3 || event.getSlot() == 4 || event.getSlot() == 5 || event.getSlot() == 6) {
				Set<Skill> skills = new HashSet<>();
                RegisteredServiceProvider<SkillsPlugin> skillsPluginProvider = Bukkit.getServer().getServicesManager().getRegistration(SkillsPlugin.class);
                if (skillsPluginProvider != null) {
                    SkillsPlugin skillsPlugin = skillsPluginProvider.getProvider();
                    skills.addAll(skillsPlugin.getSkills());
                }
				Set<Skill> skillsToRemove = new HashSet<>();
				SkillType skillType = null;
				switch (event.getSlot()) {
                    case 1: skillType = SkillType.MELEE_OFFENCE; break;
                    case 2: skillType = SkillType.MELEE_DEFENCE; break;
                    case 3: skillType = SkillType.RANGED_OFFENCE; break;
                    case 4: skillType = SkillType.RANGED_DEFENCE; break;
                    case 5: skillType = SkillType.MAGIC_OFFENCE; break;
                    case 6: skillType = SkillType.MAGIC_DEFENCE; break;
                    case 7: skillType = SkillType.MAGIC_HEALING; break;
                    case 8: skillType = SkillType.MAGIC_HEALING; break;
                    case 9: skillType = SkillType.MAGIC_NATURE; break;
                    case 10: skillType = SkillType.MAGIC_SUMMONING; break;
                    case 11: skillType = SkillType.MAGIC_SWORD; break;
                    case 12: skillType = SkillType.SPEED_NIMBLE; break;
                    case 13: skillType = SkillType.SUPPORT_PERFORM; break;
					default: break;
				}
				for (Skill skill : skills) {
					if (skill.getType() != skillType || !skill.canUse((Player) event.getWhoClicked())) {
						skillsToRemove.add(skill);
					}
				}
				skills.removeAll(skillsToRemove);
				event.getWhoClicked().closeInventory();
				FightImpl fight = (FightImpl) plugin.getActiveFight(characterPlugin.getActiveCharacter((Player) event.getWhoClicked()));
				fight.showSkillOptions((Player) event.getWhoClicked(), skills);
			}
		} else if (event.getInventory().getTitle().equals("Skill")) {
			event.setCancelled(true);
            RegisteredServiceProvider<SkillsPlugin> skillsPluginProvider = Bukkit.getServer().getServicesManager().getRegistration(SkillsPlugin.class);
            if (skillsPluginProvider != null) {
                SkillsPlugin skillsPlugin = skillsPluginProvider.getProvider();
                for (Skill skill : skillsPlugin.getSkills()) {
                    if (skill.getIcon().equals(event.getCurrentItem())) {
                        Character skilling = characterPlugin.getActiveCharacter((Player) event.getWhoClicked());
                        FightImpl fight = (FightImpl) plugin.getActiveFight(skilling);
                        Turn turn = fight.getActiveTurn();
                        turn.setSkill(skill);
                        event.getWhoClicked().closeInventory();
                        ((Player) event.getWhoClicked()).sendMessage(new String[] {plugin.getPrefix() + ChatColor.GREEN + "Current turn:",
                                (turn.getSkill() != null ? ChatColor.GREEN + "\u2611" : ChatColor.RED + "\u2612") + ChatColor.GRAY + "Skill type: " + (turn.getSkill() != null ? ChatColor.GREEN + turn.getSkill().getType().toString() : ChatColor.RED + "NOT CHOSEN - use /turn skill to choose"),
                                (turn.getSkill() != null ? ChatColor.GREEN + "\u2611" : ChatColor.RED + "\u2612") + ChatColor.GRAY + "Skill: " + (turn.getSkill() != null ? ChatColor.GREEN + turn.getSkill().getName() : ChatColor.RED + "NOT CHOSEN - use /turn skill to choose"),
                                (turn.getDefender() != null ? ChatColor.GREEN + "\u2611" : ChatColor.RED + "\u2612") + ChatColor.GRAY + "Target: " + (turn.getDefender() != null ? ChatColor.GREEN + turn.getDefender().getName() + " (" + ((Character) turn.getDefender()).getPlayer().getName() + "'s character)" : ChatColor.RED + "NOT CHOSEN - use /turn target to choose"),
                                (turn.getWeapon() != null ? ChatColor.GREEN + "\u2611" : ChatColor.RED + "\u2612") + ChatColor.GRAY + "Weapon: " + (turn.getWeapon() != null ? ChatColor.GREEN + turn.getWeapon().getType().toString() : ChatColor.RED + "NOT CHOSEN - use /turn weapon to choose"),
                                (turn.getSkill() != null && turn.getDefender() != null && turn.getWeapon() != null ? ChatColor.GREEN + "Ready to make a move! Use /turn complete to complete your turn." : ChatColor.RED + "There are still some options you must set before completing your turn.")});
                        break;
                    }
                }
            }

		} else if (event.getInventory().getTitle().equals("Target")) {
			event.setCancelled(true);
			Character character = characterPlugin.getActiveCharacter((Player) event.getWhoClicked());
			FightImpl fight = (FightImpl) plugin.getActiveFight(character);
			Turn turn = fight.getActiveTurn();
			turn.setDefender(characterPlugin.getCharacter(Integer.parseInt(event.getCurrentItem().getItemMeta().getLore().get(0))));
			event.getWhoClicked().closeInventory();
			((Player) event.getWhoClicked()).sendMessage(new String[] {plugin.getPrefix() + ChatColor.GREEN + "Current turn:",
					(turn.getSkill() != null ? ChatColor.GREEN + "\u2611" : ChatColor.RED + "\u2612") + ChatColor.GRAY + "Skill type: " + (turn.getSkill() != null ? ChatColor.GREEN + turn.getSkill().getType().toString() : ChatColor.RED + "NOT CHOSEN - use /turn skill to choose"),
					(turn.getSkill() != null ? ChatColor.GREEN + "\u2611" : ChatColor.RED + "\u2612") + ChatColor.GRAY + "Skill: " + (turn.getSkill() != null ? ChatColor.GREEN + turn.getSkill().getName() : ChatColor.RED + "NOT CHOSEN - use /turn skill to choose"),
					(turn.getDefender() != null ? ChatColor.GREEN + "\u2611" : ChatColor.RED + "\u2612") + ChatColor.GRAY + "Target: " + (turn.getDefender() != null ? ChatColor.GREEN + turn.getDefender().getName() + " (" + ((Character) turn.getDefender()).getPlayer().getName() + "'s character)" : ChatColor.RED + "NOT CHOSEN - use /turn target to choose"),
					(turn.getWeapon() != null ? ChatColor.GREEN + "\u2611" : ChatColor.RED + "\u2612") + ChatColor.GRAY + "Weapon: " + (turn.getWeapon() != null ? ChatColor.GREEN + turn.getWeapon().getType().toString() : ChatColor.RED + "NOT CHOSEN - use /turn weapon to choose"),
					(turn.getSkill() != null && turn.getDefender() != null && turn.getWeapon() != null ? ChatColor.GREEN + "Ready to make a move! Use /turn complete to complete your turn." : ChatColor.RED + "There are still some options you must set before completing your turn.")});
		} else if (event.getInventory().getTitle().equals("Weapon")) {
			event.setCancelled(true);
			Character character = characterPlugin.getActiveCharacter((Player) event.getWhoClicked());
			FightImpl fight = (FightImpl) plugin.getActiveFight(character);
			Turn turn = fight.getActiveTurn();
			turn.setWeapon(event.getCurrentItem());
			event.getWhoClicked().closeInventory();
			((Player) event.getWhoClicked()).sendMessage(new String[] {plugin.getPrefix() + ChatColor.GREEN + "Current turn:",
					(turn.getSkill() != null ? ChatColor.GREEN + "\u2611" : ChatColor.RED + "\u2612") + ChatColor.GRAY + "Skill type: " + (turn.getSkill() != null ? ChatColor.GREEN + turn.getSkill().getType().toString() : ChatColor.RED + "NOT CHOSEN - use /turn skill to choose"),
					(turn.getSkill() != null ? ChatColor.GREEN + "\u2611" : ChatColor.RED + "\u2612") + ChatColor.GRAY + "Skill: " + (turn.getSkill() != null ? ChatColor.GREEN + turn.getSkill().getName() : ChatColor.RED + "NOT CHOSEN - use /turn skill to choose"),
					(turn.getDefender() != null ? ChatColor.GREEN + "\u2611" : ChatColor.RED + "\u2612") + ChatColor.GRAY + "Target: " + (turn.getDefender() != null ? ChatColor.GREEN + turn.getDefender().getName() + " (" + ((Character) turn.getDefender()).getPlayer().getName() + "'s character)" : ChatColor.RED + "NOT CHOSEN - use /turn target to choose"),
					(turn.getWeapon() != null ? ChatColor.GREEN + "\u2611" : ChatColor.RED + "\u2612") + ChatColor.GRAY + "Weapon: " + (turn.getWeapon() != null ? ChatColor.GREEN + turn.getWeapon().getType().toString() : ChatColor.RED + "NOT CHOSEN - use /turn weapon to choose"),
					(turn.getSkill() != null && turn.getDefender() != null && turn.getWeapon() != null ? ChatColor.GREEN + "Ready to make a move! Use /turn complete to complete your turn." : ChatColor.RED + "There are still some options you must set before completing your turn.")});
		}
	}

}
