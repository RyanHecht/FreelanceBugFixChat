package me.matt.daxxieecommands;

import java.util.List;

import net.minecraft.server.v1_8_R3.EntityPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.google.common.base.Joiner;

public class CommandListener implements Listener {

    Main plugin;
    String permissionsGroup;
//    String tpsFinal;
    
    public CommandListener(Main p) {
        plugin = p;
    }

    @SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String args[] = event.getMessage().split(" ");

        String userPrefix = ChatColor.translateAlternateColorCodes('&', PermissionsEx.getUser(player).getPrefix());
        String userSuffix = ChatColor.translateAlternateColorCodes('&', PermissionsEx.getUser(player).getSuffix());
        
//        DecimalFormat decimalFormat = new DecimalFormat("##.##");
//        for(double tps : MinecraftServer.getServer().recentTps) {
//            tpsFinal = decimalFormat.format(tps);	
//        }
//        
        //Checking if they have PermissionsEx, if not, %group will display: No Valid Permissions System.
        if (!(this.plugin.getConfig().getString("Permissions-Plugin").equalsIgnoreCase("NONE"))) {
        	
        
        if (this.plugin.getConfig().getString("Permissions-Plugin").equalsIgnoreCase("PermissionsEx")
        		|| this.plugin.getConfig().getString("Permissions-Plugin").equalsIgnoreCase("PEX")) {
        	permissionsGroup = ChatColor.translateAlternateColorCodes('&', PermissionsEx.getUser(player).getPrefix());
        } else {
        	permissionsGroup = "No Valid Permissions System";
        }
        }
        //Getting the system's RAM
	    Runtime runtime = Runtime.getRuntime();
	    System.gc();
//    	used = runtime.totalMemory() - runtime.freeMemory();
    	double max = runtime.totalMemory();
    	double free = runtime.freeMemory();
//    	fin = used/max * 100;
//		fin = Double.parseDouble(Double.toString(fin).substring(0, Double.toString(fin).indexOf('.') + 2));
        
        // Loop through all defined commands in the configuration.
        for (String commandToCheck : this.plugin.getConfig().getConfigurationSection("commands").getKeys(false)) {
            // Check if the command we're currently looping through matches the
            // one the player typed.
            if (commandToCheck.equalsIgnoreCase(args[0])) {
            	System.out.print("command working... running.");
                String permission = this.plugin.getConfig().getString("commands." + args[0] + ".permission");
                boolean canDo = false;
                //Parse permissions
                if (permission != null) {
                    if (player.hasPermission(permission)) {
                        canDo = true;
                    } else {
                        canDo = false;
                    }
                } else {
                    canDo = true;
                }
                
                if (canDo) {
                	System.out.print("permission working... running.");
                    // Loop through all commands in the string list.
                    List<String> commands = this.plugin.getConfig().getStringList("commands." + args[0] + ".commands");
                    for (String com : commands) {
                        // Replace variables...
                        for (int i = 1; i < args.length; i++) {
                            com = com.replace("%" + i, args[i]);
                        }
                        //PlaceHolders replacing:
//                        com = com.replace("%0", Joiner.on(" ").join(args).replace(args[0] + " ", "")).replace("%name", player.getName()).replace("%displayname", player.getDisplayName());
                        com = com.replace("%0", Joiner.on(" ").join(args).replace(args[0] + " ", "")).replace("%name", player.getName()).replace("%displayname", player.getDisplayName()
                        		.replace("%servername", ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ServerName"))
                        				).replace("playercount", Integer.toString(Bukkit.getServer().getOnlinePlayers().size())
                        						).replace("%serverram", Double.toString(max)
                        								).replace("%uuid", player.getUniqueId().toString()
                        										).replace("%health", Double.toString(player.getHealth())
                        												).replace("%hunger", Float.toString(player.getSaturation())
                        														).replace("%gamemode", player.getGameMode().toString()
                        																).replace("%balance", Double.toString(Main.econ.getBalance(player))
                        																		).replace("%group", permissionsGroup)
                        			     .replace("%location-x", Double.toString(player.getLocation().getX())
                        			    		 ).replace("%location-y", Double.toString(player.getLocation().getY())
                        			    				 ).replace("%location-z", Double.toString(player.getLocation().getZ())
                        			    						).replace("%worldname", player.getWorld().getName()
                        			    								).replace("%points", Integer.toString(Main.playerPoints.getAPI().look("Player"))
                        			    										).replace("%xp", Float.toString(player.getExp())
                        			    												).replace("", Integer.toString(getPing(player))
                        			    														).replace("%userprefix", userPrefix)
                        			     .replace("%usersuffix", userSuffix)
                        			     		 .replace("%prefix", ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Prefix"))
                        			     				 ).replace("%ip", plugin.getConfig().getString("ServerIP")
                        			     						 ).replace("%motd", ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("MOTD"))
                        			     								 ).replace("%website", ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("Website"))
                        			     										 ).replace("%version", Bukkit.getBukkitVersion()
                        			     												 ).replace("%freeram", Double.toString(free)));
//                        			     														 ).replace("%tps", tpsFinal)
                        			     														 
                        String command = ChatColor.translateAlternateColorCodes('&', com);

                        // Execute commands.
                        doCommand(player, command);
                    	System.out.print("running doCommand working... running.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have permission.");
                }
                event.setCancelled(true);
            }
        }
    }


    public void doCommand(final Player player, String com) {
        if (com.startsWith("%delay-")) {
            String del = com.split("\\ ")[0];
            int delay = Integer.parseInt(del.replace("%delay-", ""));
            final String command = com.replace("%delay-" + delay, "").substring(1);
            System.out.println(command);
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if (command.startsWith("/")) {
                        player.performCommand(command.substring(1));
                    } else if (command.contains("%console ")) {
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace("%console ", ""));
                    } else if (command.contains("%message ")) {
                        player.sendMessage(command.replace("%message ", ""));
                    } else if (command.contains("%broadcast ")) {
                        plugin.getServer().broadcastMessage(command.replace("%broadcast ", ""));
                    }
                }
            }, delay * 20L);
        } else {
            String command = com;
            if (command.startsWith("/")) {
                player.performCommand(command.substring(1));
            } else if (command.contains("%console ")) {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace("%console ", ""));
            } else if (command.contains("%message ")) {
                player.sendMessage(command.replace("%message ", ""));
            } else if (command.contains("%broadcast ")) {
                plugin.getServer().broadcastMessage(command.replace("%broadcast ", ""));
            }
        }
    }
    
	public int getPing(Player p) {
		CraftPlayer cp = (CraftPlayer) p;
		EntityPlayer ep = cp.getHandle();
		p.sendMessage("");
		return ep.ping; 
		}
	public int getPing2(Player player) {
	    return ((CraftPlayer) player).getHandle().ping;
	}

}
