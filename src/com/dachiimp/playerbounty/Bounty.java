package com.dachiimp.playerbounty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;



public class Bounty extends JavaPlugin implements Listener {
	
	public final Logger logger = Logger.getLogger("Minecraft");
	public static Economy economy = null;
	
	public String prefix = "[" + ChatColor.DARK_GRAY + "Bounty" + ChatColor.WHITE + "] ";
	
	private Scoreboard board;
	
	ArrayList<Player> enabled = new ArrayList<Player>();
	
	@SuppressWarnings("deprecation")
	public void onEnable() {
	    	PluginManager pm = this.getServer().getPluginManager();
	    	pm.registerEvents(this, this);
	    	PluginDescriptionFile pdfFile = this.getDescription();
	    	this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " Enabled.");
	    	setupEconomy();
	    	setupDataFolder();
	    	setupScoreboard();
	    	checkConfig();
	    	updateScoreboard(Bukkit.getOfflinePlayer("No Bounties"), 0);
	    }
	  
	  static String currency;
	  int maxBounty;
	  int minBounty;


	  
	  
	  private void checkConfig() {
		  Configuration cfg = this.getConfig();
		  if(cfg.getString("Currency") == null) {
			  System.out.println("[" + this.getDescription().getName() + "] Config doesn't contain a currency string. Setting to default $");
			  cfg.set("Currency", "$");
		  }
		  if(cfg.getString("maxBounty") != null) {
			  try{
				  Integer.parseInt(cfg.getString("maxBounty"));
			  } catch(NumberFormatException e) {
				  System.out.println("[" + this.getDescription().getName() + "] maxBounty string in config not an integer. Setting to default 100000");
				  cfg.set("maxBounty", 100000);
				  saveConfig();
			  }
		  } else {
			  System.out.println("[" + this.getDescription().getName() + "] Config doesn't contain a maxBounty string. Setting to default 100000");
			  cfg.set("maxBounty", 100000);
			  saveConfig();
		  }
		  if(cfg.getString("minBounty") != null) {
			  try{
				  Integer.parseInt(cfg.getString("minBounty"));
			  } catch(NumberFormatException e) {
				  System.out.println("[" + this.getDescription().getName() + "] minBounty string in config not an integer. Setting to default 1000");
				  cfg.set("minBounty", 1000);
				  saveConfig();
			  }
		  } else {
			  System.out.println("[" + this.getDescription().getName() + "] Config doesn't contain a minBounty string. Setting to default 1000");
			  cfg.set("minBounty", 1000);
			  saveConfig();
		  }
		  currency = cfg.getString("Currency");
		  maxBounty = cfg.getInt("maxBounty");
		  minBounty = cfg.getInt("minBounty");
		  saveConfig();

	  }
	  
	  private void setupDataFolder() {
		  File file = new File("plugins/PlayerBounty");
		  if(file.exists()) {
			  System.out.println("[" + this.getDescription().getName() + "] Datafolder exists");
		  } else {
			  System.out.println("[" + this.getDescription().getName() + "] Datafolder doesn't exist, creating one now");
			  file.mkdirs();
		  }
		  
		  File file2 = new File("plugins/PlayerBounty/bounties.dat");
		  if(file2.exists()) {
			  System.out.println("[" + this.getDescription().getName() + "] Bounties file exists");
		  } else {
			  System.out.println("[" + this.getDescription().getName() + "] Bounties file doesn't exist, creating one now");
			  try {
				file2.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("[" + this.getDescription().getName() + "] Bounties file failed to create");				
			}
		  }
		  
		  File file3 = new File("plugins/PlayerBounty/placedBounties.dat");
		  if(file3.exists()) {
			  System.out.println("[" + this.getDescription().getName() + "] Placed bounties file exists");
		  } else {
			  System.out.println("[" + this.getDescription().getName() + "] Placed bounties file doesn't exist, creating one now");
			  try {
				file3.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("[" + this.getDescription().getName() + "] Placed bounties file failed to create");				
			}
		  }
		  saveConfig();
	  }
	  
	  private void setupScoreboard() {
		  ScoreboardManager manager = Bukkit.getScoreboardManager();
          board = manager.getNewScoreboard();
          Objective objective = board.registerNewObjective("Bounty", "dummy");

          objective.setDisplayName(ChatColor.GOLD + "Player Bounties:");
          objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	  }
	  
	  
	  
	  // Events 
	  
	  
	  
	  
	  @EventHandler
	  public void onJoin(PlayerJoinEvent e) {
		  Player player = e.getPlayer();
		  File file = new File("plugins/PlayerBounty/bounties.dat");
          YamlConfiguration inv = YamlConfiguration.loadConfiguration(file);
          if(inv.getString(player.getName()) != null) {
        	  int score = inv.getInt(player.getName());
        	  updateScoreboard(player, score);
          }
	  }
	  
	  @SuppressWarnings("deprecation")
	  @EventHandler
	  public void onQuit(PlayerQuitEvent e) {
		  Player player = e.getPlayer();
		  board.resetScores(player);
		  if(board.getEntries().size() == 0) {
			  updateScoreboard(Bukkit.getOfflinePlayer("No Bounties"), 0);
		  }
	  }
	  
	  @SuppressWarnings("deprecation")
	  @EventHandler
	  public void onKick(PlayerKickEvent e) {
		  Player player = e.getPlayer();
		  board.resetScores(player);
		  if(board.getEntries().size() == 0) {
			  updateScoreboard(Bukkit.getOfflinePlayer("No Bounties"), 0);
		  }
		  
	  }
	  

	  private Scoreboard getScoreboard() {
		  return this.board;
	  }


	private boolean setupEconomy() {
	        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	        if (economyProvider != null) {
	            economy = economyProvider.getProvider();
	        }

	        return (economy != null);
	    }

	  
	  public void onDisable() {
	    	PluginDescriptionFile pdfFile = this.getDescription();
	    	this.logger.info(pdfFile.getName() + " Disabled.");
	    	
	    }
	  
	  @SuppressWarnings("deprecation")
	public void updateScoreboard(OfflinePlayer offlinePlayer, Integer amount) {
		  if(offlinePlayer.getName().equalsIgnoreCase("No Bounties")) {
			  Scoreboard board = getScoreboard();
			  Objective obj = board.getObjective("Bounty");
			  Score score = obj.getScore((offlinePlayer));
			  score.setScore(amount);
		  } else {
			  Scoreboard board = getScoreboard();
			  Objective obj = board.getObjective("Bounty");
			  board.resetScores(Bukkit.getOfflinePlayer("No Bounties"));
			  Score score = obj.getScore((offlinePlayer));
			  score.setScore(amount);
		  }
	  }
	  
	public boolean onCommand(CommandSender sender, Command cmd,String commandlabel, String[] args) {
		File file = new File("plugins/PlayerBounty/bounties.dat");
		YamlConfiguration inv = YamlConfiguration.loadConfiguration(file);
		  if(sender instanceof Player) {
			  Player player = (Player) sender;
			  	if(cmd.getName().equalsIgnoreCase("bounties")) {
			  		if(enabled.contains(player)) {
			  			enabled.remove(player);
			  			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			  			send(player, "Toggled off the bounty scoreboard");
			  		} else {
			  			enabled.add(player);
			  			player.setScoreboard(board);
			  			send(player, "Toggled on the bounty scoreboard");
			  		}
			  	} else if(cmd.getName().equalsIgnoreCase("bounty")) {
			  		if(player.hasPermission("bounty.create")) {
			  			if(args.length == 2) {
			  				double econ = economy.getBalance(player);
			  				Player target = Bukkit.getServer().getPlayer(args[0]);
			  				if(target != null) {
			  					if(!(target.hasPermission("bounty.ignore"))) {
			  						if(!(target == player)) {
						  				try {
						  					int amount = Integer.parseInt(args[1]);
						  					
						  					if(econ >= amount) {
						  						if(!(amount > maxBounty)) {
						  							if(!(amount < minBounty)) {
						  								File file2 = new File("plugins/PlayerBounty/placedBounties.dat");
						  								ArrayList<String> list = new ArrayList<String>();
						  								YamlConfiguration inv2 = YamlConfiguration.loadConfiguration(file2);
						  								if(inv2.getStringList(target.getName()) != null) {
						  									list.addAll(inv2.getStringList(target.getName()));
						  									if(list.contains(player.getName())) {
						  										send(player, "You have already placed a bounty on " + target.getName() + ". You cannot place another one");
						  										return false;
						  									} else {
						  										int returnA = saveBounty(target, amount);
						  										if(returnA == -69) {
						  											send(player, "There was an error setting the bounty of " + target.getName());
						  										} else {
						  											if(inv.getString(target.getName()) != null) {
						  												sendBounty(player, target, returnA, true);
						  											} else {
						  												sendBounty(player, target, returnA, false);
						  											}
						  											updateScoreboard(target, returnA);
						  										}
									  								
						  										economy.withdrawPlayer(player, amount);
						  										updatePlacedBount(target, player);
						  									}
							  								
						  								}
						  							} else {
						  								send(player, "The minimum bounty you may place is " + minBounty);
						  							}
						  						} else {
						  							send(player, "The maximum bounty you may place is " + maxBounty);
						  						}
						  					} else {
						  						send(player, "You do not have " + currency + amount);
						  					}
						  					
						  				} catch(NumberFormatException e) {
						  					incorrectUsage(player, "/bounty <player> <amount>");
						  					return false;
						  				}
			  						} else {
			  							send(player, "You cannot place a bounty on yourself");
			  						}
			  					} else {
			  						send(player, "You cannot place a bounty on " + target.getName());
			  					}
			  				} else {
			  					send(player, "Cannot find player " + args[0] + ". Are they online?");
			  				}
			  						
			  			} else {
			  				incorrectUsage(player, "/bounty <player> <amount>");
			  			}
			  		} else {
			  			noPerms(player);
			  		}
			  	}
		  }
			  return true;
		}




	private void sendBounty(Player player, Player target, Integer bounty, boolean b) {
		if(b) {
			Bukkit.broadcastMessage(prefix + "The bounty of " + ChatColor.GOLD + target.getName() + ChatColor.WHITE + " has been raised to " + ChatColor.GOLD + currency + bounty + ChatColor.WHITE + " By " + ChatColor.GOLD + player.getName());
		} else {
			Bukkit.broadcastMessage(prefix + "A bounty of " + ChatColor.GOLD + currency + bounty + ChatColor.WHITE + " has been placed on " + ChatColor.GOLD + target.getName() +ChatColor.WHITE + " By " + ChatColor.GOLD + player.getName());
		}
	}

	private void noPerms(Player player) {
		player.sendMessage(ChatColor.DARK_RED + "You don't have access to that command");
	}




	private void incorrectUsage(Player player, String usage) {
		player.sendMessage(prefix + ChatColor.DARK_RED + "Incorrect usage. " + usage);
	}




	private void send(Player player, String message) {
		player.sendMessage(prefix + message);
	}
	
	
	
	
	public int saveBounty(Player player, Integer bounty) {
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
						return bount;
					} catch (IOException e) {
						e.printStackTrace();
						return -69;
					}
				} catch(NumberFormatException e) {
					e.printStackTrace();
					return -69;
				}
			} else {
				inv.set(player.getName(), bounty);
				try {
					inv.save(file);
					return bounty;
				} catch (IOException e) {
					e.printStackTrace();
					return -69;
				}
			}
		} else {
			setupDataFolder();
			return -69;
		}
	}
	
	int temp = 0;
	
	public void updatePlacedBount(Player player, Player newplayer) {
		File file = new File("plugins/PlayerBounty/placedBounties.dat");
		if(file.exists()) {
			ArrayList<String> list = new ArrayList<String>();
			YamlConfiguration inv = YamlConfiguration.loadConfiguration(file);
			if(inv.getStringList(player.getName()) == null) {
				list.add(newplayer.getName());
				inv.set(player.getName(), list);
			} else {
				list.addAll(inv.getStringList(player.getName()));
				list.add(newplayer.getName());
				inv.set(player.getName(), list);
				try {
					inv.save(file);
				} catch (IOException e) {
					player.sendMessage("Failed to save placed bounties. Please contact an administrator.");
					e.printStackTrace();
				}
			}
		} else {
			if(temp == 4) {
				player.sendMessage("Failed to update placed bounties. Please contact an administrator");
				temp = 0;
			} else {
				temp++;
				player.sendMessage("Failed to update placed bounties. Trying again. Attempt " + temp + "/3");
				setupDataFolder();
				updatePlacedBount(player, newplayer);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		File file = new File("plugins/PlayerBounty/bounties.dat");
		YamlConfiguration inv = YamlConfiguration.loadConfiguration(file);
		File file2 = new File("plugins/PlayerBounty/placedBounties.dat");
		YamlConfiguration inv2 = YamlConfiguration.loadConfiguration(file);
		if(e.getEntity() instanceof Player) {
			if(e.getEntity().getKiller() instanceof Player) {
				Player player = (Player) e.getEntity();
				Player killer = (Player) e.getEntity().getKiller();
				if(file.exists() && file2.exists()) {
					if(inv.getString(player.getName()) != null) {
						if(player != killer) {
							Bukkit.broadcastMessage(prefix + "The bounty of " + ChatColor.GOLD + currency + inv.getString(player.getName()) + ChatColor.WHITE + " on " + ChatColor.GOLD + player.getName() + ChatColor.WHITE + " has been claimed by " + ChatColor.GOLD + killer.getName());
							economy.depositPlayer(killer, inv.getInt(player.getName()));
							inv.set(player.getName(), null);
							try {
								inv.save(file);
							} catch (IOException e1) {
								killer.sendMessage("Failed to save bounties. Please contact an administrator.");
								e1.printStackTrace();
							}
							inv2.set(player.getName(), null);
							try {
								inv2.save(file2);
							} catch (IOException e1) {
								killer.sendMessage("Failed to save placed bounties. Please contact an administrator.");
								e1.printStackTrace();
							}
							board.resetScores(player.getName());
							
							if(file.length() == 0) {
								updateScoreboard(Bukkit.getOfflinePlayer("No Bounties"), 0);
							}
							
						}
					}
				} else {
					setupDataFolder();
				}
			}
		}
	}

	public static String getCurrency() {
		return currency;
	}
	

}
