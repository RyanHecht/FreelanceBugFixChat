package me.matt.daxxieecommands;

import java.io.File;

import net.milkbowl.vault.economy.Economy;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Economy econ = null;
    public static PlayerPoints playerPoints;
	
    @Override
    public void onEnable() {
        getConfig().options().header("DCommands (DaxxieeCommands) version " + getDescription().getVersion() + " by Daxxiee and DevOG. Configuration page: bitly.com/dcommands #");
        if (!new File(getDataFolder() + "/config.yml").exists()) {
            getConfig().options().copyDefaults(true);
        }
        getConfig().options().copyHeader(true);
        saveConfig();
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        hookPlayerPoints();
        
        getServer().getPluginManager().registerEvents(new CommandListener(this), this);
    }
    
    private boolean hookPlayerPoints() {
        final Plugin plugin = this.getServer().getPluginManager().getPlugin("PlayerPoints");
        playerPoints = PlayerPoints.class.cast(plugin);
        return playerPoints != null; 
    }
    
    private boolean setupEconomy() {
    	if (getServer().getPluginManager().getPlugin("Vault") == null) {
    		return false;
    	}
    		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    		if (rsp == null) {
    			return false;
    		}
    	econ = rsp.getProvider();
    	return econ != null;
    }
    
    public PlayerPoints getPlayerPoints() {
        return playerPoints;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (cmnd.getName().equalsIgnoreCase("dcommands")) {
            if (strings.length == 0) {
                cs.sendMessage(ChatColor.RED + "[DCommands] " + ChatColor.GRAY + "Running Version " + ChatColor.RED + getDescription().getVersion() + ChatColor.GRAY + " by Daxxiee and DevOG.");
                return true;
            }

            if (strings[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                saveConfig();
                cs.sendMessage(ChatColor.RED + "[DCommands] " + ChatColor.GRAY + "Configuration has been reloaded!");
                return true;
            } else if (strings[0].equalsIgnoreCase("help")) {
                cs.sendMessage(ChatColor.RED + "[DCommands] " + ChatColor.GRAY + "Commands associated with DCommands:");
                cs.sendMessage(ChatColor.RED + "/dcommands" + ChatColor.GRAY + ": General plugin information.");
                cs.sendMessage(ChatColor.RED + "/dcommands reload" + ChatColor.GRAY + ": Reload the configuration.");
                return true;
            }
            cs.sendMessage(ChatColor.RED + "[DCommands] " + ChatColor.GRAY + "Unknown subcommand. " + ChatColor.RED + "/dcommands help " + ChatColor.GRAY + "for help.");
        }
        return true;
    }

}
