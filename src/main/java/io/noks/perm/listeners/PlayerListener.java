package io.noks.perm.listeners;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.noks.perm.Main;
import io.noks.perm.managers.PlayerManager;

public class PlayerListener implements Listener {
	
	private Main main;
	public PlayerListener(Main main) {
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		this.main.getDataBase().loadPlayer(event.getPlayer().getUniqueId());
	}
	
	private void leaveAction(UUID uuid) {
		final PlayerManager pm = PlayerManager.get(uuid);
		this.main.getDataBase().savePlayer(pm);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		this.leaveAction(event.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent event) {
		this.leaveAction(event.getPlayer().getUniqueId());
	}
}
