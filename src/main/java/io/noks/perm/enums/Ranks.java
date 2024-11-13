package io.noks.perm.enums;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;

public enum Ranks {
	DEFAULT("default", "&7", (byte)0, Collections.singletonList("kit.*")),
	HIGHROLLER("highroller","&6&7[&5HighRoller&7] &6", (byte)9, Collections.singletonList("kit.*")),
	MEDIA("media", "&d&o&7[&d&oMedia&7] &d&o", (byte)10, Collections.singletonList("kit.*")),
	MOD("mod", "&9&7[&9Mod&7] &9", (byte)50, Collections.singletonList("*")),
	ADMIN("admin", "&c&7[&cAdmin&7] &c", (byte)60, Collections.singletonList("*")),
	MANAGER("manager", "&4&7[&4Manager&7] &4", (byte)70, Collections.singletonList("*")),
	CREATOR("creator", "&d&l&7[&d&lCreator&7] &d&l", (byte)100, Collections.singletonList("*"));
	
	public String name;
	public String prefix;
	public byte power;
	public List<String> permissions;
	
	Ranks(String name, String prefix, byte power, List<String> perm){
		this.name = name;
		this.prefix = prefix;
		this.power = power;
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
	
	public byte getPower() {
		return this.power;
	}
	
	public boolean hasHigherPowerThan(Ranks rank) {
		return this.power > rank.power;
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
	}
	private boolean isMagicColor(char letter) {
		return switch (letter) {
			case 'k', 'l', 'm', 'n', 'o', 'r' -> true;
			default -> false;
		};
	}
}
