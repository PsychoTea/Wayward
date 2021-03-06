package net.wayward_realms.waywardcharacters;

import net.wayward_realms.waywardlib.character.Character;
import net.wayward_realms.waywardlib.character.Race;
import net.wayward_realms.waywardlib.classes.ClassesPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CharacterCommand implements CommandExecutor {

    private WaywardCharacters plugin;

    public CharacterCommand(WaywardCharacters plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("new")) {
                int livingCharacters = 0;
                for (net.wayward_realms.waywardlib.character.Character character : plugin.getCharacters((Player) sender)) {
                    if (!character.isDead()) {
                        livingCharacters++;
                    }
                }
                if (livingCharacters < plugin.getConfig().getInt("characters.limit") || plugin.getConfig().getInt("characters.limit") <= 0 || sender.hasPermission("wayward.characters.characters.unlimited")) {
                    plugin.setActiveCharacter((Player) sender, new CharacterImpl((Player) sender));
                    sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Created a new character. Make sure to set up your character information!");
                } else {
                    sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "You have reached your character limit.");
                }
            } else if (args[0].equalsIgnoreCase("card")) {
                Player player = (Player) sender;
                if (args.length >= 2) {
                    if (plugin.getServer().getPlayer(args[1]) != null) {
                        player = plugin.getServer().getPlayer(args[1]);
                    }
                }
                Character character = plugin.getActiveCharacter(player);
                ClassesPlugin classesPlugin = plugin.getServer().getServicesManager().getRegistration(ClassesPlugin.class).getProvider();
                sender.sendMessage(plugin.getPrefix() + ChatColor.BLUE + ChatColor.BOLD + character.getName() + "'s " + ChatColor.RESET + ChatColor.DARK_GRAY + "character card");
                sender.sendMessage(ChatColor.DARK_GRAY + "Age: " + ChatColor.BLUE +  character.getAge());
                sender.sendMessage(ChatColor.DARK_GRAY + "Gender: " + ChatColor.BLUE + character.getGender().getName());
                sender.sendMessage(ChatColor.DARK_GRAY + "Race: " + ChatColor.BLUE + character.getRace().getName());
                if (classesPlugin != null) {
                    if (classesPlugin.getClass(character) != null) {
                        sender.sendMessage(ChatColor.DARK_GRAY + "Class: " + ChatColor.BLUE + "Lv" + classesPlugin.getLevel(character) + " " + classesPlugin.getClass(character).getName());
                    }
                }
                sender.sendMessage(ChatColor.DARK_GRAY + "Description: " + ChatColor.BLUE + character.getDescription());
            } else if (args[0].equalsIgnoreCase("switch")) {
                if (args.length >= 2) {
                    boolean found = false;
                    for (Character character : plugin.getCharacters((Player) sender)) {
                        if (character.getName().toUpperCase().startsWith(args[1].toUpperCase())) {
                            if (!character.isDead()) {
                                plugin.setActiveCharacter((Player) sender, character);
                                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Switched character to " + character.getName());
                            } else {
                                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "You cannot switch to a dead character.");
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        try {
                            int id = Integer.parseInt(args[1]);
                            if (plugin.getCharacter(id) != null) {
                                Character character = plugin.getCharacter(id);
                                if (character.getPlayer().getName().equalsIgnoreCase(sender.getName())) {
                                    if (!character.isDead()) {
                                        plugin.setActiveCharacter((Player) sender, character);
                                        sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Switched character to " + character.getName());
                                    } else {
                                        sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "You cannot switch to a dead character.");
                                    }
                                } else {
                                    sender.sendMessage(plugin.getPrefix() + ChatColor.RED + character.getName() + " is not your character.");
                                }
                            } else {
                                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "No character with that ID could be found.");
                            }
                        } catch (NumberFormatException exception) {
                            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have a character by that name");
                        }
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /" + label + " switch [character name]");
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                if (args.length >= 2) {
                    Character character = plugin.getActiveCharacter((Player) sender);
                    if (args[1].equalsIgnoreCase("name")) {
                        if (args.length >= 3) {
                            StringBuilder nameBuilder = new StringBuilder();
                            for (int i = 2; i < args.length; i++) {
                                nameBuilder.append(args[i]);
                                if (i < args.length - 1) {
                                    nameBuilder.append(" ");
                                }
                            }
                            character.setName(nameBuilder.toString());
                            sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Set character's name to " + nameBuilder.toString());
                        } else {
                            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /" + label + " set name [name]");
                        }
                    } else if (args[1].equalsIgnoreCase("age")) {
                        if (args.length >= 3) {
                            try {
                                character.setAge(Integer.parseInt(args[2]));
                                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Set character's age to " + args[2]);
                            } catch (NumberFormatException exception) {
                                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Age must be an integer");
                            }
                        } else {
                            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /" + label + " set age [age]");
                        }
                    } else if (args[1].equalsIgnoreCase("gender")) {
                        if (args.length >= 3) {
                            try {
                                character.setGender(plugin.getGender(args[2].toUpperCase()));
                                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Set character's gender to " + args[2].toUpperCase());
                            } catch (IllegalArgumentException exception) {
                                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Gender must be either male or female");
                            }
                        } else {
                            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /" + label + " set gender [gender]");
                        }
                    } else if (args[1].equalsIgnoreCase("race")) {
                        if (args.length >= 3) {
                            if (plugin.getRace(args[2]) != null) {
                                character.setRace(plugin.getRace(args[2]));
                                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Set character's race to " + args[2]);
                            } else {
                                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Race must be one of the following: ");
                                for (Race race : plugin.getRaces()) {
                                    sender.sendMessage(ChatColor.RED + race.getName());
                                }
                            }
                        } else {
                            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /" + label + " set race [race]");
                        }
                    } else if (args[1].equalsIgnoreCase("description")) {
                        if (args.length >= 3) {
                            String description = "";
                            for (int i = 2; i <= args.length - 1; i++) {
                                description += args[i] + " ";
                            }
                            character.setDescription(description);
                            sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Set character's description to " + description);
                            sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Extend it by using /character extenddescription [info]");
                        } else {
                            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /" + label + " set description [description]");
                        }
                    } else if (args[1].equalsIgnoreCase("dead")) {
                        Player player = (Player) sender;
                        character.setDead(true);
                        plugin.setActiveCharacter(player, new CharacterImpl(player));
                        sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Your character has been set to daed, and a new character has been created.");
                    } else {
                        sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /" + label + " set [name|age|gender|race|description|dead]");
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /" + label + " set [name|age|gender|race|description|dead]");
                }
            } else if (args[0].equalsIgnoreCase("extenddescription")) {
                Character character = plugin.getActiveCharacter((Player) sender);
                if (args.length >= 2) {
                    StringBuilder description = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        description.append(args[i]).append(" ");
                    }
                    character.addDescription(description.toString());
                    sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Description extended");
                } else {
                    sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /" + label + " extenddescription [info]");
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Character list: ");
                for (Character character : plugin.getCharacters((Player) sender)) {
                    sender.sendMessage(ChatColor.GRAY + "[" + (character.isDead() ? ChatColor.RED : ChatColor.GREEN) + character.getId() + ChatColor.GRAY + "] " + character.getName() + " (" + (character.isDead() ? ChatColor.RED + "Dead" : ChatColor.GREEN + "Alive") + ChatColor.GRAY + ")");
                }
            } else if (args[0].equalsIgnoreCase("revive")) {
                if (sender.hasPermission("wayward.characters.command.character.revive")) {
                    if (args.length >= 2) {
                        try {
                            if (plugin.getCharacter(Integer.parseInt(args[1])) != null) {
                                plugin.getCharacter(Integer.parseInt(args[1])).setDead(false);
                                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Revived " + plugin.getCharacter(Integer.parseInt(args[1])).getName());
                            } else {
                                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "That character does not exist.");
                            }
                        } catch (NumberFormatException exception) {
                            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "You must specify the character ID.");
                        }
                    } else {
                        sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "You must specify the character ID.");
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission.");
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /" + label + " [new|switch|card|set|extenddescription|list|revive]");
            }
        } else {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /" + label + " [new|switch|card|set|extenddescription|list|revive]");
        }
        return true;
    }

}
