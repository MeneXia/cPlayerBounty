package com.dachiimp.playerbounty;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class BountyAPI {
	
	

    File file = new File("plugins/PlayerBounty/bounties.dat");
    YamlConfiguration inv = YamlConfiguration.loadConfiguration(file);
	
    // Bounty API
    
    
    
	/*
	 * @param Add a bounty to a specified player with the arguments of player, bounty (integer), and whether to broadcast the bounty update (boolean - true/false) (Returns new bounty).
	 */
	public static int addBounty(Player player, Integer bounty, boolean broadcastBounty) {
		File file = new File("plugins/PlayerBounty/bounties.dat");
		YamlConfiguration inv = YamlConfiguration.loadConfiguration(file);
		if(file.exists()) {
			if(inv.getString(player.getName()) != null) {
				String name = inv.getString(player.getName());
				try{
					int bount = Integer.parseInt(name);
					bount = bount+bounty;
					inv.set(player.getName(), bount);
					try {
						inv.save(file);
						if(broadcastBounty) {
							sendBounty(player, bount, true);
						}
						return bount;
					} catch (IOException e) {
						e.printStackTrace();
						return 0;
					}
				} catch(NumberFormatException e) {
					e.printStackTrace();
					return 0;
				}
			} else {
				inv.set(player.getName(), bounty);
				try {
					inv.save(file);
					if(broadcastBounty) {
						sendBounty(player, bounty, false);
					}
					return bounty;
				} catch (IOException e) {
					e.printStackTrace();
					return 0;
				}
			}
		} else {
			return 0;
		}
	}
	
	
	
	/*
	 * @param Get the bounty of the specified player with the arguments of player.
	 */
	public static int getBounty(Player player) {
		File file = new File("plugins/PlayerBounty/bounties.dat");
		YamlConfiguration inv = YamlConfiguration.loadConfiguration(file);
		if(file.exists()) {
			if(inv.getString(player.getName()) != null) {
				String name = inv.getString(player.getName());
				try{
					int bount = Integer.parseInt(name);
						return bount;
				} catch(NumberFormatException e) {
					e.printStackTrace();
					return 0;
				}
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	
	
	
	
	// Methods
	
	private static void sendBounty(Player target, int bounty, boolean b) {
		String prefix = "[" + ChatColor.DARK_GRAY + "Bounty" + ChatColor.WHITE + "] ";
		String currency = Bounty.getCurrency();
		
		if(b) {
			Bukkit.broadcastMessage(prefix + "The bounty of " + ChatColor.GOLD + target.getName() + ChatColor.WHITE + " has been raised to " + ChatColor.GOLD + currency + bounty);
		} else {
			Bukkit.broadcastMessage(prefix + "A bounty of " + ChatColor.GOLD + currency + bounty + ChatColor.WHITE + " has been placed on " + ChatColor.GOLD + target.getName());
		}
	}
	
}
