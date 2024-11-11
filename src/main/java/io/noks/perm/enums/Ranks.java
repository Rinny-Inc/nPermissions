package io.noks.perm.enums;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;

public enum Ranks {
	DEFAULT("default", "&7", Collections.singletonList("kit.*")),
	MOD("mod", "&9&7[&9Mod&7] &9", Collections.singletonList("*")),
	ADMIN("admin", "&c&7[&cAdmin&7] &c", Collections.singletonList("*")),
	MANAGER("manager", "&4&7[&4Manager&7] &4", Collections.singletonList("*")),
	CREATOR("creator", "&d&7[&dCreator&7] &d", Collections.singletonList("*"));
	
	public String name;
	public String prefix;
	public List<String> permissions;
	
	Ranks(String name, String prefix, List<String> perm){
		this.name = name;
		this.prefix = prefix;
		this.permissions = perm;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getPrefix() {
		return this.prefix;
	}
	
	public List<String> getPermissions() {
		return this.permissions;
	}
	
	public static Ranks getRankFromName(String name) {
		for (Ranks rank : Ranks.values()) {
			if (rank.getName().toLowerCase().equals(name.toLowerCase())) {
				return rank;
			}
		}
		return null;
	}
	
	public String getColoredPrefix() {
		return ChatColor.translateAlternateColorCodes('&', getPrefix());
	}
	
	public String getPrefixColors() {
		if (getPrefix().isEmpty()) {
			return "";
		}
		char code = 'f';
		int count = 0;

		for (String string : getPrefix().split("&")) {
			if (string.isEmpty() || ChatColor.getByChar(string.toCharArray()[0]) == null) {
				continue;
			}
			switch (count) {
				case 0: {
					if (!isMagicColor(string.toCharArray()[0])) {
						code = string.toCharArray()[0];
						count++;
						continue;
					}
				}
				case 1: {
					if (isMagicColor(string.toCharArray()[0])) {
						return ChatColor.getByChar(code).toString() + ChatColor.getByChar(string.toCharArray()[0]).toString();
					}
				}
				default: {
					break;
				}
			}
		}
		return ChatColor.getByChar(code).toString();

		//                 |Tab||   Chat Prefix    |
		//                 |   ||                  |
		// PREFIX FORMAT -> &3&l&f[&3Developer&f] &3
	}
	
	private boolean isMagicColor(char letter) {
		return switch (letter) {
			case 'k' -> true;
			case 'l' -> true;
			case 'm' -> true;
			case 'n' -> true;
			case 'o' -> true;
			case 'r' -> true;
			default -> false;
		};
	}
}
