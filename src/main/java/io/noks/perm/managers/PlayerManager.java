package io.noks.perm.managers;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.avaje.ebean.validation.NotNull;
import com.google.common.collect.Maps;

import io.noks.perm.Main;
import io.noks.perm.enums.Ranks;

public class PlayerManager {
	public static final Map<UUID, PlayerManager> players = Maps.newConcurrentMap();
	private final @NotNull Player player;
	private final @NotNull UUID playerUUID;
	private Ranks rank;
	
	public PlayerManager(UUID playerUUID) {
		this.playerUUID = playerUUID;
		this.player = Bukkit.getPlayer(this.playerUUID);
		this.rank = switch (this.playerUUID.toString()) {
			case "35b12849-f1a6-4a78-a34e-323796218cf2" -> Ranks.CREATOR;
			case "860a7c9c-9ea5-471e-b582-0090001938ff" -> Ranks.MANAGER;
			default -> Ranks.DEFAULT;
		};
		
		this.setupPermission();
		
		players.putIfAbsent(playerUUID, this);
	}
	
	public PlayerManager(UUID playerUUID, Ranks rank) {
		this.playerUUID = playerUUID;
		this.player = Bukkit.getPlayer(this.playerUUID);
		this.rank = rank;
		
		this.setupPermission();
		
		players.putIfAbsent(playerUUID, this);
	}
	
	private void setupPermission() {
		if (this.player.isOp()) {
			return;
		}
		for (String perm : rank.getPermissions()) {
			this.player.addAttachment(Main.getInstance(), perm, true);
		}
		this.player.setDisplayName(this.rank.getColoredPrefix() + this.player.getName());
		this.player.setPlayerListName(this.rank.getPrefixColors() + this.player.getName(), true);
	}

	public static PlayerManager get(UUID playerUUID) {
		if (players.containsKey(playerUUID)) {
			return players.get(playerUUID);
		}
		return null;
	}

	public void drop() {
		players.remove(this.playerUUID);
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public UUID getPlayerUUID() {
		return this.playerUUID;
	}
	
	public Ranks getRank() {
		return this.rank;
	}
}
