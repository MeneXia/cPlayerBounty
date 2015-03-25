package bounty.george.me;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class BountyAPI {

    File file = new File("plugins/PlayerBounty/bounties.dat");
    YamlConfiguration inv = YamlConfiguration.loadConfiguration(file);
	
	/*
	 * @param Add a bounty to a specified player with the arguments of player, bounty (integer), with no broadcast allowing a custom broadcast or none at all.
	 */
	public static void addBounty(Player player, Integer bounty) {
		
	}
	/*
	 * @param Add a bounty to a specified player with the arguments of player, bounty (integer), message to broadcast (string).
	 */
	public static void addBounty(Player player, Integer bounty, String bc) {
		
	}
	
}
