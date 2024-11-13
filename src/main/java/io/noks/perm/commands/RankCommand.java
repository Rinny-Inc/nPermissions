package io.noks.perm.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import io.noks.perm.Main;
import io.noks.perm.enums.Ranks;
import io.noks.perm.managers.PlayerManager;

public class RankCommand implements CommandExecutor {
	
	private Main main;
	public RankCommand(Main main) {
		this.main = main;
		main.getCommand("rank").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("rank.set")) {
			sender.sendMessage(ChatColor.RED + "No permission!");
			return false;
		}
		if (args.length == 0 || args.length > 2) {
			sender.sendMessage(ChatColor.RED + "Usage: /rank <player> <rank name>");
			return false;
		}
		boolean console = false;
		
		if (sender instanceof ConsoleCommandSender) {
			console = true;
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("list")) {
				for (Ranks rank : Ranks.values()) {
					sender.sendMessage(rank.getPrefixColors() + rank.getName());
				}
				return true;
			}
			sender.sendMessage(ChatColor.RED + "Usage: /rank <player> <rank name>");
			return false;
		}
		final Player target = this.main.getServer().getPlayer(args[0]);
		
		if (target == null) { // TODO: make offlineplayer usable
			sender.sendMessage(ChatColor.RED + "Player's not connected! (Coming soon)");
			return false;
		}
		final Ranks rank = Ranks.getRankFromName(args[1]);
		
		if (rank == null) {
			sender.sendMessage(ChatColor.RED + "Invalid rank; do (/rank list) to know all the ranks names!");
			return false;
		}
		if (!console) {
			Player player = (Player) sender;
			PlayerManager pm = PlayerManager.get(player.getUniqueId());
			if (pm.getRank().getPower() < 60) {
				sender.sendMessage(ChatColor.RED + "No permission!");
				return false;
			}
			if (!pm.getRank().hasHigherPowerThan(rank)) {
				sender.sendMessage(ChatColor.RED + "No permission!");
				return false;
			}
		}
		final PlayerManager manager = PlayerManager.get(target.getUniqueId());
		manager.setRank(rank);
		
		if (sender.getName() != target.getName()) {
			sender.sendMessage(ChatColor.GREEN + "Rank " + rank.getName() + " set to " + target.getName());
		}
		target.sendMessage(ChatColor.GREEN + "Rank " + rank.getName() + " obtained!");
		return true;
	}
}
