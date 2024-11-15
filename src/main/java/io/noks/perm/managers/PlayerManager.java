package io.noks.perm.managers;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

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
		this.rank = Ranks.DEFAULT;
		
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
		this.player.setDisplayName(this.rank.getColoredPrefix() + this.player.getName());
		this.player.setPlayerListName(this.rank.getPrefixColors() + this.player.getName(), true);
		if (this.player.isOp()) {
			return;
		}
		PermissionAttachment pa = this.player.addAttachment(Main.getInstance());
		for (String perm : rank.getPermissions()) {
			pa.setPermission(perm, true);
		}
		this.player.recalculatePermissions();
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
	
	public void setRank(Ranks rank) {
		if (!this.player.isOp()) {
			for (PermissionAttachmentInfo perm : this.player.getEffectivePermissions()) {
				if (perm.getAttachment() == null) {
					continue;
				}
				this.player.removeAttachment(perm.getAttachment());
			}
		}
		this.rank = rank;
		this.setupPermission();
	}
}
